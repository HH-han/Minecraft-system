<template>
    <div class="mouse-style-page" ref="pageRef">
        <div id="cursor-svg-source">
            <!-- Minecraft 像素风光标：16x16 像素网格，每格 2 个单位，深色描边 + 浅色填充 -->
            <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32"
                shape-rendering="crispEdges">
                <!-- 外层描边 -->
                <rect x="0" y="0" width="2" height="2" fill="#1b1b1b" />
                <rect x="0" y="2" width="4" height="2" fill="#1b1b1b" />
                <rect x="0" y="4" width="8" height="2" fill="#1b1b1b" />
                <rect x="0" y="6" width="10" height="2" fill="#1b1b1b" />
                <rect x="0" y="8" width="14" height="2" fill="#1b1b1b" />
                <rect x="0" y="10" width="18" height="2" fill="#1b1b1b" />
                <rect x="0" y="12" width="20" height="2" fill="#1b1b1b" />
                <rect x="0" y="14" width="14" height="2" fill="#1b1b1b" />
                <rect x="0" y="16" width="14" height="2" fill="#1b1b1b" />
                <rect x="0" y="18" width="16" height="2" fill="#1b1b1b" />
                <rect x="0" y="20" width="4" height="2" fill="#1b1b1b" />
                <rect x="8" y="20" width="8" height="2" fill="#1b1b1b" />
                <rect x="0" y="22" width="2" height="2" fill="#1b1b1b" />
                <rect x="10" y="22" width="8" height="2" fill="#1b1b1b" />
                <rect x="10" y="24" width="8" height="2" fill="#1b1b1b" />
                <rect x="12" y="26" width="6" height="2" fill="#1b1b1b" />
                <rect x="12" y="28" width="2" height="2" fill="#1b1b1b" />
                <!-- 内层填充 -->
                <rect x="2" y="4" width="2" height="2" fill="#f5f5f5" />
                <rect x="2" y="6" width="6" height="2" fill="#f5f5f5" />
                <rect x="2" y="8" width="8" height="2" fill="#f5f5f5" />
                <rect x="2" y="10" width="12" height="2" fill="#f5f5f5" />
                <rect x="2" y="12" width="12" height="2" fill="#f5f5f5" />
                <rect x="2" y="14" width="10" height="2" fill="#f5f5f5" />
                <rect x="2" y="16" width="10" height="2" fill="#f5f5f5" />
                <rect x="2" y="18" width="2" height="2" fill="#f5f5f5" />
                <rect x="8" y="18" width="6" height="2" fill="#f5f5f5" />
                <rect x="10" y="20" width="4" height="2" fill="#f5f5f5" />
                <rect x="12" y="22" width="4" height="2" fill="#f5f5f5" />
                <rect x="12" y="24" width="4" height="2" fill="#f5f5f5" />
            </svg>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const pageRef = ref(null);
let originalBodyCursor = '';
let originalHtmlCursor = '';

/**
 * 将字符串转 Base64（兼容 Unicode 字符）
 */
function toBase64(str) {
    // 先将 UTF-16 字符串转成 UTF-8 字节，再 btoa
    const utf8Bytes = unescape(encodeURIComponent(str));
    return btoa(utf8Bytes);
}

/**
 * 清理 SVG 字符串：移除对 cursor 渲染无意义的属性，减小体积并避免解析差异
 */
function sanitizeSvgString(svg) {
    return svg
        // 移除 XML 声明
        .replace(/<\?xml[\s\S]*?\?>/gi, '')
        // 移除 xmlns:xlink / version 等非必要属性（保留核心 xmlns）
        .replace(/\s+(xmlns:[a-z]+|version|t|p-id|class|id)="[^"]*"/gi, '')
        // 合并多余空白
        .replace(/\s+/g, ' ')
        .trim();
}

onMounted(() => {
    const svgElement = document.querySelector('#cursor-svg-source svg');
    if (!svgElement) {
        console.warn('[MouseStyle] 未找到 SVG 光标源 #cursor-svg-source svg');
        return;
    }

    // 克隆节点，避免改动原 DOM
    const clonedSvg = svgElement.cloneNode(true);

    // 序列化 SVG
    const serializer = new XMLSerializer();
    const rawSvgString = serializer.serializeToString(clonedSvg);
    const cleanSvgString = sanitizeSvgString(rawSvgString);

    if (!cleanSvgString || cleanSvgString.indexOf('<svg') === -1) {
        console.error('[MouseStyle] SVG 序列化失败:', rawSvgString);
        return;
    }

    // --- 方案 A：Base64 编码（兼容性最好，Chrome/Firefox/Edge/Safari 都稳定）---
    const base64DataUri = `data:image/svg+xml;base64,${toBase64(cleanSvgString)}`;

    // --- 方案 B：URL 编码（作为备用日志输出，方便排查）---
    const urlEncoded = encodeURIComponent(cleanSvgString)
        .replace(/'/g, '%27')
        .replace(/"/g, '%22');
    const pctDataUri = `data:image/svg+xml,${urlEncoded}`;

    // 光标热点计算：
    // 当前 SVG viewBox = 0 0 32 32，光标实际渲染尺寸 = width/height = 32x32
    // 像素箭头的"尖端"位于网格 (0,0)，每个像素 2 个单位，映射到 viewBox 即 (0, 0)
    const HOTSPOT_X = 0;
    const HOTSPOT_Y = 0;

    // 使用 url() 不加分号外的额外包裹——Chrome 对无引号版本最友好
    const cursorStyle = `url(${base64DataUri}) ${HOTSPOT_X} ${HOTSPOT_Y}, pointer`;

    // 保存原始值，卸载时恢复
    originalBodyCursor = document.body.style.cursor;
    originalHtmlCursor = document.documentElement.style.cursor;

    // 1) 在 <html> 上设置——覆盖整个视口（包括 body 外部的空白区域）
    document.documentElement.style.cursor = cursorStyle;
    // 2) 在 <body> 上设置——兼容某些布局
    document.body.style.cursor = cursorStyle;
    // 3) 在组件根容器上设置——确保 scoped 容器内一定生效
    if (pageRef.value) {
        pageRef.value.style.setProperty('cursor', cursorStyle);
    }

    // 4) 兜底：为所有已知会覆盖 cursor 的 Element Plus 组件/通用交互元素提供继承
    //    通过插入一段 <style> 实现（组件卸载时会同步移除）
    const styleEl = document.createElement('style');
    styleEl.setAttribute('data-mouse-style-inject', 'true');
    styleEl.textContent = [
        `html, body { cursor: ${cursorStyle} !important; }`,
        `*, *::before, *::after { cursor: inherit !important; }`,
    ].join('\n');
    document.head.appendChild(styleEl);
    // 挂载到 window 上，onBeforeUnmount 能精准清理
    window.__mouseStyleInjectedStyleEl = styleEl;

    console.groupCollapsed('%c[MouseStyle] 自定义光标已应用', 'color:#1296DB;font-weight:bold');
    console.log('SVG 源码(清理后):', cleanSvgString);
    console.log('热点偏移 (x, y):', HOTSPOT_X, HOTSPOT_Y);
    console.log('Base64 Data URI (首 120 字):', base64DataUri.substring(0, 120) + '...');
    console.log('URL-enc Data URI (首 120 字):', pctDataUri.substring(0, 120) + '...');
    console.log('最终 CSS 样式:', cursorStyle.substring(0, 140) + '...');
    console.log('请在本页任意位置移动鼠标验证。如仍为默认箭头，F12 检查 Elements → html/body → style.cursor 是否存在如上值。');
    console.groupEnd();
});

onBeforeUnmount(() => {
    // 移除注入的全局 <style>
    const injected = document.querySelector('style[data-mouse-style-inject="true"]')
        || window.__mouseStyleInjectedStyleEl;
    if (injected && injected.parentNode) {
        injected.parentNode.removeChild(injected);
    }
    if (window.__mouseStyleInjectedStyleEl) {
        delete window.__mouseStyleInjectedStyleEl;
    }

    // 恢复 <html> / <body> 原始 cursor
    if (typeof originalHtmlCursor === 'string') {
        document.documentElement.style.cursor = originalHtmlCursor;
    } else {
        document.documentElement.style.removeProperty
            ? document.documentElement.style.removeProperty('cursor')
            : (document.documentElement.style.cursor = '');
    }
    if (typeof originalBodyCursor === 'string') {
        document.body.style.cursor = originalBodyCursor;
    } else {
        document.body.style.removeProperty
            ? document.body.style.removeProperty('cursor')
            : (document.body.style.cursor = '');
    }

    console.log('[MouseStyle] 已卸载并恢复原始光标');
});
</script>

<style scoped>
.mouse-style-page {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', sans-serif;
    width: 100%;
}

.mouse-style-page * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* 隐藏实际的 SVG 元素（仅用于定义光标） */
#cursor-svg-source {
    position: absolute;
    width: 0;
    height: 0;
    overflow: hidden;
    pointer-events: none;
    opacity: 0;
    visibility: hidden;
}
</style>
