package com.minecraft.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minecraft.dto.request.SystemLogQueryDTO;
import com.minecraft.dto.response.PageResponse;
import com.minecraft.entity.SystemLog;
import com.minecraft.enums.LogLevel;
import com.minecraft.enums.LogType;
import com.minecraft.mapper.SystemLogMapper;
import com.minecraft.service.SystemLogService;
import com.minecraft.vo.SystemLogVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 系统日志服务实现：支持系统报错、系统信息、安全事件的记录与查询，
 * 并在应用启动完成后自动记录一条系统启动信息日志。
 */
@Slf4j
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog>
        implements SystemLogService, ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port:8080}")
    private Integer serverPort;

    @Override
    public PageResponse<SystemLogVO> getSystemLogList(SystemLogQueryDTO queryDTO) {
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();

        // 关键字搜索（用户名、内容、模块、动作）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            String keyword = queryDTO.getKeyword().trim();
            wrapper.and(w -> w.like(SystemLog::getUsername, keyword)
                    .or()
                    .like(SystemLog::getContent, keyword)
                    .or()
                    .like(SystemLog::getModule, keyword)
                    .or()
                    .like(SystemLog::getAction, keyword));
        }

        if (queryDTO.getUserId() != null) {
            wrapper.eq(SystemLog::getUserId, queryDTO.getUserId());
        }
        if (queryDTO.getLogLevel() != null && !queryDTO.getLogLevel().trim().isEmpty()) {
            wrapper.eq(SystemLog::getLogLevel, queryDTO.getLogLevel());
        }
        if (queryDTO.getLogType() != null && !queryDTO.getLogType().trim().isEmpty()) {
            wrapper.eq(SystemLog::getLogType, queryDTO.getLogType());
        }
        if (queryDTO.getModule() != null && !queryDTO.getModule().trim().isEmpty()) {
            wrapper.eq(SystemLog::getModule, queryDTO.getModule());
        }
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().trim().isEmpty()) {
            wrapper.eq(SystemLog::getStatus, queryDTO.getStatus());
        }
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(SystemLog::getCreatedAt, queryDTO.getStartTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(SystemLog::getCreatedAt, queryDTO.getEndTime());
        }

        wrapper.orderByDesc(SystemLog::getCreatedAt);

        Page<SystemLog> resultPage = page(new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize()), wrapper);
        List<SystemLogVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return new PageResponse<>(voList, resultPage.getTotal(), queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public SystemLogVO getSystemLogDetail(Long id) {
        SystemLog systemLog = getById(id);
        return systemLog == null ? null : convertToVO(systemLog);
    }

    @Override
    @Transactional
    public void saveSystemLog(SystemLog systemLog) {
        if (systemLog.getCreatedAt() == null) {
            systemLog.setCreatedAt(LocalDateTime.now());
        }
        if (systemLog.getTraceId() == null) {
            systemLog.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (systemLog.getRequestId() == null) {
            systemLog.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        }
        fillServerInfo(systemLog);
        save(systemLog);
    }

    @Override
    @Async("taskExecutor")
    public void saveSystemLogAsync(SystemLog systemLog) {
        try {
            saveSystemLog(systemLog);
        } catch (Exception e) {
            log.error("异步保存系统日志失败", e);
        }
    }

    @Override
    public void recordError(String module, String action, String content, Throwable e) {
        try {
            SystemLog systemLog = buildRequestLog(module, action, content);
            systemLog.setLogLevel(LogLevel.ERROR.name());
            systemLog.setLogType(LogType.SYSTEM.name());
            systemLog.setStatus("ERROR");
            if (e != null) {
                systemLog.setErrorStack(getStackTrace(e));
            }
            saveSystemLogAsync(systemLog);
        } catch (Exception ex) {
            log.error("记录系统错误日志失败", ex);
        }
    }

    @Override
    public void recordInfo(String module, String action, String content) {
        try {
            SystemLog systemLog = buildRequestLog(module, action, content);
            systemLog.setLogLevel(LogLevel.INFO.name());
            systemLog.setLogType(LogType.SYSTEM.name());
            systemLog.setStatus("SUCCESS");
            saveSystemLogAsync(systemLog);
        } catch (Exception ex) {
            log.error("记录系统信息日志失败", ex);
        }
    }

    @Override
    @Transactional
    public void deleteSystemLog(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public boolean deleteLogsBeforeTime(LocalDateTime time) {
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SystemLog::getCreatedAt, time);
        return remove(wrapper);
    }

    /**
     * 应用启动完成后记录系统信息日志（服务器主机、端口等）
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            SystemLog systemLog = new SystemLog();
            systemLog.setLogLevel(LogLevel.INFO.name());
            systemLog.setLogType(LogType.SYSTEM.name());
            systemLog.setModule("系统");
            systemLog.setAction("应用启动");
            String host = InetAddress.getLocalHost().getHostName();
            systemLog.setContent("系统启动成功，服务器主机: " + host + "，端口: " + serverPort);
            systemLog.setStatus("SUCCESS");
            systemLog.setServerHost(host);
            systemLog.setServerPort(serverPort);
            saveSystemLog(systemLog);
        } catch (Exception e) {
            log.error("记录系统启动日志失败", e);
        }
    }

    /**
     * 构建带请求上下文的系统日志（非 Web 线程调用时请求相关字段为空）
     */
    private SystemLog buildRequestLog(String module, String action, String content) {
        SystemLog systemLog = new SystemLog();
        systemLog.setModule(module);
        systemLog.setAction(action);
        systemLog.setContent(content);

        Long userId = com.minecraft.utils.SecurityUtils.getCurrentUserId();
        String username = com.minecraft.utils.SecurityUtils.getCurrentUsername();
        systemLog.setUserId(userId);
        systemLog.setUsername(username);

        HttpServletRequest request = getRequest();
        if (request != null) {
            systemLog.setUserIp(request.getRemoteAddr());
            systemLog.setUserAgent(request.getHeader("User-Agent"));
            systemLog.setRequestUrl(request.getRequestURI());
            systemLog.setRequestMethod(request.getMethod());
            systemLog.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        }
        return systemLog;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    private void fillServerInfo(SystemLog systemLog) {
        if (systemLog.getServerPort() == null) {
            systemLog.setServerPort(serverPort);
        }
        if (systemLog.getServerHost() == null) {
            try {
                systemLog.setServerHost(InetAddress.getLocalHost().getHostName());
            } catch (Exception ignored) {
                // 主机名获取失败时不阻断日志保存
            }
        }
    }

    private String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        // 限制堆栈长度，避免超出 TEXT 字段合理范围
        String stack = sw.toString();
        return stack.length() > 4000 ? stack.substring(0, 4000) : stack;
    }

    /**
     * Entity转VO
     */
    private SystemLogVO convertToVO(SystemLog entity) {
        SystemLogVO vo = new SystemLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
