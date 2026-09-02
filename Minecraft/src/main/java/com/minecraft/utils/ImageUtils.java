package com.minecraft.utils;

import com.minecraft.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@Component
public class ImageUtils {
    //记录器
    static Logger logger = LoggerFactory.getLogger(UserController.class);

    /** 支持的图片类型：Content-Type -> 标准格式名 */
    private static final Map<String, String> ALLOWED_CONTENT_TYPES = Map.of(
            "image/jpeg", "jpeg",
            "image/png", "png",
            "image/gif", "gif",
            "image/webp", "webp"
    );

    /** 图片大小上限：2MB */
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    /** 超过该最大边长的图片会等比缩小 */
    private static final int MAX_DIMENSION = 1920;

    /** JPEG 压缩质量（0~1） */
    private static final float JPEG_QUALITY = 0.8f;

    private final String windowsUploadDir;
    private final String unixUploadDir;
    private final String windowsBaseUrl;
    private final String unixBaseUrl;
    /** 水印文字，留空表示不加水印 */
    private final String watermarkText;

    public ImageUtils(
            @Value("${image.upload.windows-dir}") String windowsUploadDir,
            @Value("${image.upload.unix-dir}") String unixUploadDir,
            @Value("${image.upload.windows-url}") String windowsBaseUrl,
            @Value("${image.upload.unix-url}") String unixBaseUrl,
            @Value("${image.upload.watermark-text:}") String watermarkText) {
        this.windowsUploadDir = windowsUploadDir;
        this.unixUploadDir = unixUploadDir;
        this.windowsBaseUrl = windowsBaseUrl;
        this.unixBaseUrl = unixBaseUrl;
        this.watermarkText = watermarkText;
    }

    private String getUploadDir() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? windowsUploadDir : unixUploadDir;
    }

    private String getBaseUrl() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? windowsBaseUrl : unixBaseUrl;
    }

    public void deleteImage(String imageUrl) throws Exception {
        String baseUrl = getBaseUrl();
        if (imageUrl != null && imageUrl.startsWith(baseUrl)) {
            String fileName = imageUrl.substring(baseUrl.length());
            Path baseDir = Paths.get(getUploadDir()).toAbsolutePath().normalize();
            Path imagePath = baseDir.resolve(fileName).normalize();

            // 防止路径穿越（如 fileName 包含 ../）删除上传目录之外的文件
            if (!imagePath.startsWith(baseDir)) {
                logger.warn("已阻止删除上传目录之外的文件: {}", fileName);
                return;
            }

            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
            }
        }
    }

    /**
     * 处理 Base64 图片并返回 URL。遇到错误时抛出异常。
     * 数据会经过格式校验（魔数）、大小限制和统一处理管线（压缩/水印）。
     */
    public String processBase64Image(String base64Image) throws Exception {
        if (base64Image == null || !base64Image.startsWith("data:image")) {
            return null;
        }

        // 只分割第一个逗号，避免base64内容中包含逗号的问题
        int commaIndex = base64Image.indexOf(",");
        if (commaIndex < 0 || commaIndex >= base64Image.length() - 1) {
            logger.error("Base64图片格式不正确，缺少数据部分");
            throw new Exception("Base64图片格式不正确");
        }
        String dataPart = base64Image.substring(commaIndex + 1);
        byte[] imageBytes = Base64.getMimeDecoder().decode(dataPart);
        if (imageBytes.length == 0) {
            throw new Exception("Base64图片数据为空");
        }
        if (imageBytes.length > MAX_IMAGE_SIZE) {
            throw new Exception("图片大小不能超过2MB");
        }

        // 通过文件魔数识别真实格式，防止伪造数据
        String format = detectImageFormat(imageBytes);
        if (format == null) {
            logger.error("Base64图片内容不是有效的JPEG/PNG/GIF/WEBP格式");
            throw new Exception("仅支持JPEG、PNG、GIF或WEBP格式的图片");
        }

        return processAndSave(imageBytes, format);
    }

    /**
     * 安全版本的 Base64 图片处理。遇到任何错误时返回 null 而不是抛出异常。
     */
    public String processBase64ImageSafe(String base64Image) {
        try {
            return processBase64Image(base64Image);
        } catch (Exception e) {
            logger.error("处理Base64图片失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 处理前端上传的二进制图片文件并返回 URL。遇到错误时抛出异常。
     * 流程：非空校验 -> Content-Type 白名单 -> 大小限制 -> 文件魔数校验 -> 压缩/水印处理 -> 保存 -> 返回访问地址。
     */
    public String processMultipartFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception("上传文件不能为空");
        }

        // 验证Content-Type是否在白名单内
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.containsKey(contentType.toLowerCase())) {
            logger.error("仅支持JPEG、PNG、GIF或WEBP格式的图片");
            throw new Exception("仅支持JPEG、PNG、GIF或WEBP格式的图片");
        }

        // 验证文件大小 (限制2MB)
        if (file.getSize() > MAX_IMAGE_SIZE) {
            logger.error("图片大小不能超过2MB");
            throw new Exception("图片大小不能超过2MB");
        }

        byte[] imageBytes = file.getBytes();

        // 通过文件魔数识别真实格式，防止伪造Content-Type上传恶意文件
        String format = detectImageFormat(imageBytes);
        if (format == null) {
            logger.error("图片内容不是有效的JPEG/PNG/GIF/WEBP格式");
            throw new Exception("仅支持JPEG、PNG、GIF或WEBP格式的图片");
        }

        return processAndSave(imageBytes, format);
    }

    /**
     * 统一处理管线：按真实格式分发处理，保存后返回访问地址。
     * JPEG/PNG：解码 -> 超大图等比缩放 -> 水印 -> 重新编码压缩；
     * GIF/WEBP：JDK 不支持安全解码（动图会丢帧/格式无法解码），保留原始数据直接存储。
     */
    private String processAndSave(byte[] data, String format) throws Exception {
        byte[] output = data;
        String extension = format;

        if ("jpeg".equals(format) || "png".equals(format)) {
            try (ByteArrayInputStream in = new ByteArrayInputStream(data)) {
                BufferedImage image = ImageIO.read(in);
                if (image != null) {
                    BufferedImage processed = applyWatermark(resizeIfNeeded(image));
                    extension = "jpeg".equals(format) ? "jpg" : "png";
                    output = encode(processed, extension);
                }
            } catch (Exception e) {
                // 解码/处理失败时保留原始数据直接存储，保证不因处理环节阻断上传
                logger.warn("图片处理失败，将保存原始数据: {}", e.getMessage());
                extension = format;
                output = data;
            }
        }

        return saveToDisk(output, extension);
    }

    /**
     * 通过文件头魔数识别图片真实格式。
     *
     * @return jpeg / png / gif / webp，无法识别时返回 null
     */
    private String detectImageFormat(byte[] data) {
        if (data == null || data.length < 12) {
            return null;
        }
        // JPEG: FF D8 FF
        if ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8 && (data[2] & 0xFF) == 0xFF) {
            return "jpeg";
        }
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if ((data[0] & 0xFF) == 0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47) {
            return "png";
        }
        // GIF: GIF87a / GIF89a
        if (data[0] == 0x47 && data[1] == 0x49 && data[2] == 0x46 && data[3] == 0x38) {
            return "gif";
        }
        // WEBP: RIFF .... WEBP
        if (data[0] == 0x52 && data[1] == 0x49 && data[2] == 0x46 && data[3] == 0x46
                && data[8] == 0x57 && data[9] == 0x45 && data[10] == 0x42 && data[11] == 0x50) {
            return "webp";
        }
        return null;
    }

    /**
     * 超过最大边长限制时等比缩小图片。
     */
    private BufferedImage resizeIfNeeded(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxSide = Math.max(width, height);
        if (maxSide <= MAX_DIMENSION) {
            return image;
        }

        double scale = (double) MAX_DIMENSION / maxSide;
        int newWidth = Math.max(1, (int) Math.round(width * scale));
        int newHeight = Math.max(1, (int) Math.round(height * scale));

        BufferedImage resized = new BufferedImage(newWidth, newHeight,
                image.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resized;
    }

    /**
     * 绘制文字水印（右下角、半透明），未配置水印文字时跳过。
     */
    private BufferedImage applyWatermark(BufferedImage image) {
        if (watermarkText == null || watermarkText.isBlank()) {
            return image;
        }

        Graphics2D g = image.createGraphics();
        int fontSize = Math.max(12, image.getWidth() / 30);
        g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        g.setComposite(AlphaComposite.SrcOver.derive(0.4f));
        g.setColor(Color.WHITE);
        int x = Math.max(4, image.getWidth() - g.getFontMetrics().stringWidth(watermarkText) - 8);
        int y = Math.max(fontSize, image.getHeight() - fontSize);
        g.drawString(watermarkText, x, y);
        g.dispose();
        return image;
    }

    /**
     * 将图片编码为指定格式字节流。JPEG 使用指定压缩质量，PNG 无损。
     */
    private byte[] encode(BufferedImage image, String extension) throws Exception {
        if ("jpg".equals(extension)) {
            // JPEG 不支持透明通道，统一转为 RGB，避免透明区域变红
            BufferedImage rgb = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = rgb.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = rgb;

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(JPEG_QUALITY);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (MemoryCacheImageOutputStream os = new MemoryCacheImageOutputStream(out)) {
                writer.setOutput(os);
                writer.write(null, new IIOImage(image, null, null), param);
            } finally {
                writer.dispose();
            }
            return out.toByteArray();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, extension, out);
        return out.toByteArray();
    }

    /**
     * 保存图片字节到上传目录并返回访问URL。
     */
    private String saveToDisk(byte[] data, String extension) throws Exception {
        String fileName = UUID.randomUUID() + "." + extension;
        Path uploadPath = Paths.get(getUploadDir());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, data);
        String baseUrl = getBaseUrl();
        logger.info("文件保存成功: {}{}", fileName, baseUrl);

        // 返回图片URL（由调用方存入数据库）
        return baseUrl + fileName;
    }
}
