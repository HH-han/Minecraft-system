package com.minecraft.config;

import com.minecraft.handler.DataChangeBroadcastInterceptor;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final DataChangeBroadcastInterceptor dataChangeBroadcastInterceptor;

    /** 图片上传目录（与 application.yml 的 image.upload 配置保持一致） */
    @Value("${image.upload.windows-dir}")
    private String windowsUploadDir;

    @Value("${image.upload.unix-dir}")
    private String unixUploadDir;

    /** 图片访问URL前缀（与 application.yml 的 image.upload 配置保持一致） */
    @Value("${image.upload.windows-url}")
    private String windowsBaseUrl;

    @Value("${image.upload.unix-url}")
    private String unixBaseUrl;

    /** 历史图片URL前缀（逗号分隔，兼容旧数据，来自 application.yml） */
    @Value("#{'${image.upload.legacy-url-prefixes:}'.split(',')}")
    private List<String> legacyUrlPrefixes;

    /** 日志目录（与 logback-spring.xml 共用的 logging.file.path） */
    @Value("${logging.file.path:logs}")
    private String logDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 写操作成功后广播数据变更事件，驱动客户端自动刷新
        registry.addInterceptor(dataChangeBroadcastInterceptor).addPathPatterns("/api/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        // 图片存储目录由 application.yml 的 image.upload.{windows,unix}-dir 统一配置
        String os = System.getProperty("os.name").toLowerCase();
        Path imagePath = Paths.get(os.contains("win") ? windowsUploadDir : unixUploadDir);
        createDirectoryIfNotExists(imagePath);
        log.info("使用目录: {}", imagePath);

        // 确保路径使用正斜杠，并且以斜杠结尾
        String resourceLocation = "file:" + imagePath.toString().replace("\\", "/");
        if (!resourceLocation.endsWith("/")) {
            resourceLocation += "/";
        }

        // 图片访问映射：URL前缀由 image.upload.{windows,unix}-url 配置决定
        String baseUrl = normalizeUrlPrefix(os.contains("win") ? windowsBaseUrl : unixBaseUrl);
        registry.addResourceHandler(baseUrl + "**")
                .addResourceLocations(resourceLocation);

        // 历史图片URL前缀兼容映射（同样指向当前上传目录）
        for (String legacy : legacyUrlPrefixes) {
            String prefix = normalizeUrlPrefix(legacy);
            if (!prefix.isEmpty() && !prefix.equals(baseUrl)) {
                registry.addResourceHandler(prefix + "**")
                        .addResourceLocations(resourceLocation);
            }
        }

        // 添加类路径下的静态资源映射（可选）
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 允许访问 .well-known 目录下的文件
        registry.addResourceHandler("/.well-known/**")
                .addResourceLocations("classpath:static/.well-known/");

        // 允许访问日志目录下的文件（目录由 logging.file.path 配置，与 logback 一致）
        Path logsPath = Paths.get(logDir);
        if (!logsPath.isAbsolute()) {
            logsPath = Paths.get(System.getProperty("user.dir")).resolve(logsPath);
        }
        createDirectoryIfNotExists(logsPath);
        String logsLocation = "file:" + logsPath.toString().replace("\\", "/") + "/";
        registry.addResourceHandler("/logs/**")
                .addResourceLocations(logsLocation);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 视图控制器配置
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    private void createDirectoryIfNotExists(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log.error("创建目录失败: {}", path, e);
                throw new RuntimeException("创建目录失败: " + path, e);
            }
        }
    }

    /** 规范化URL前缀：去空格，保证以 / 开头并以 / 结尾；空白输入返回空串 */
    private String normalizeUrlPrefix(String prefix) {
        if (prefix == null) {
            return "";
        }
        String p = prefix.trim();
        if (p.isEmpty()) {
            return "";
        }
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        if (!p.endsWith("/")) {
            p = p + "/";
        }
        return p;
    }
}
