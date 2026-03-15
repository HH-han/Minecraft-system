package com.minecraft.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 设备信息工具类
 * 用于解析用户代理字符串，获取浏览器和操作系统信息
 */
@Component
public class DeviceInfoUtils {

    /**
     * 从HttpServletRequest中获取用户IP地址
     * @param request HttpServletRequest
     * @return IP地址
     */
    public String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 解析用户代理字符串，获取浏览器信息
     * @param userAgent 用户代理字符串
     * @return 浏览器信息
     */
    public String getBrowser(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "未知浏览器";
        }

        if (userAgent.contains("MSIE")) {
            return "Internet Explorer";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Safari")) {
            return "Safari";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Postman")) {
            return "Postman";
        } else if (userAgent.contains("curl")) {
            return "cURL";
        } else {
            return "其他浏览器";
        }
    }

    /**
     * 解析用户代理字符串，获取操作系统信息
     * @param userAgent 用户代理字符串
     * @return 操作系统信息
     */
    public String getOS(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "未知操作系统";
        }

        if (userAgent.contains("Windows")) {
            if (userAgent.contains("Windows 10")) {
                return "Windows 10";
            } else if (userAgent.contains("Windows 8")) {
                return "Windows 8";
            } else if (userAgent.contains("Windows 7")) {
                return "Windows 7";
            } else if (userAgent.contains("Windows Vista")) {
                return "Windows Vista";
            } else if (userAgent.contains("Windows XP")) {
                return "Windows XP";
            } else {
                return "Windows";
            }
        } else if (userAgent.contains("Macintosh")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            if (userAgent.contains("Android")) {
                return "Android";
            } else {
                return "Linux";
            }
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
            return "iOS";
        } else {
            return "其他操作系统";
        }
    }

    /**
     * 从HttpServletRequest中获取用户代理字符串
     * @param request HttpServletRequest
     * @return 用户代理字符串
     */
    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}