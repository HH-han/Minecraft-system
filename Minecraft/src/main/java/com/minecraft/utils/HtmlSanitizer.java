package com.minecraft.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 富文本 HTML 白名单过滤（防 XSS）
 * 等效 Jsoup.Safelist 的最小实现：仅保留基础排版标签与安全属性，
 * 移除 script/iframe/事件属性/javascript: 协议等风险内容。
 */
public class HtmlSanitizer {

    /** 允许保留的标签 */
    private static final Pattern ALLOWED_TAGS = Pattern.compile(
            "p|br|hr|h1|h2|h3|h4|h5|h6|strong|b|em|i|u|s|del|blockquote|pre|code|ul|ol|li|"
                    + "table|thead|tbody|tr|td|th|span|div|a|img|font|center",
            Pattern.CASE_INSENSITIVE);

    /** 允许保留的属性 */
    private static final Pattern ALLOWED_ATTRS = Pattern.compile(
            "href|src|alt|title|width|height|color|style|target",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern TAG_PATTERN = Pattern.compile(
            "<(/?)([a-zA-Z][a-zA-Z0-9]*)((?:\\s+[^<>]*?)?)(/?)>",
            Pattern.DOTALL);

    private static final Pattern ATTR_PATTERN = Pattern.compile(
            "([a-zA-Z-]+)\\s*=\\s*(\"[^\"]*\"|'[^']*'|[^\\s\"'>]+)");

    private static final Pattern DANGEROUS_URL = Pattern.compile(
            "\\s*(javascript|vbscript|data\\s*:\\s*text/html)",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern EVENT_ATTR = Pattern.compile("^on[a-z]+$", Pattern.CASE_INSENSITIVE);

    public static String sanitize(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        Matcher matcher = TAG_PATTERN.matcher(html);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String replacement = sanitizeTag(matcher);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String sanitizeTag(Matcher matcher) {
        String closing = matcher.group(1);
        String tagName = matcher.group(2).toLowerCase();
        String attrs = matcher.group(3);
        String selfClose = matcher.group(4);

        if (!ALLOWED_TAGS.matcher(tagName).matches()) {
            return ""; // 非白名单标签整体移除（含 script/iframe/style 内容由调用方处理）
        }
        if (!closing.isEmpty()) {
            return "</" + tagName + ">";
        }
        StringBuilder cleaned = new StringBuilder("<" + tagName);
        Matcher attrMatcher = ATTR_PATTERN.matcher(attrs);
        while (attrMatcher.find()) {
            String name = attrMatcher.group(1).toLowerCase();
            String value = attrMatcher.group(2);
            if (EVENT_ATTR.matcher(name).matches() || !ALLOWED_ATTRS.matcher(name).matches()) {
                continue;
            }
            // javascript:/vbscript: 协议过滤
            String rawValue = value.replaceAll("^[\"']|[\"']$", "");
            if (DANGEROUS_URL.matcher(rawValue).find()) {
                continue;
            }
            cleaned.append(" ").append(name).append("=\"")
                    .append(rawValue.replace("\"", "&quot;")).append("\"");
        }
        // a 标签默认安全跳转
        if ("a".equals(tagName)) {
            cleaned.append(" rel=\"noopener noreferrer\"");
        }
        cleaned.append(selfClose.isEmpty() ? ">" : "/>");
        return cleaned.toString();
    }
}
