<template>
    <div class="mouse-style-page" ref="pageRef">
        <div id="cursor-svg-source">
            <svg xmlns="http://www.w3.org/2000/svg" x="0px" y="0px" width="32" height="32" viewBox="0 0 48 48">
                <linearGradient id="SVGID_1__s3JOUU9Yp36E_gr1" x1="34.618" x2="17.284" y1="11.573" y2="28.908"
                    gradientUnits="userSpaceOnUse">
                    <stop offset="0" stop-color="#60affe"></stop>
                    <stop offset="1" stop-color="#fff"></stop>
                </linearGradient>
                <path fill="url(#SVGID_1__s3JOUU9Yp36E_gr1)"
                    d="M34.625,18.575L12.163,7.711c-0.734-0.355-1.562,0.271-1.42,1.074l4.338,24.572	c0.152,0.86,1.256,1.126,1.783,0.429l4.444-5.879l9.384,12.086c0.333,0.44,0.96,0.528,1.401,0.194l4.785-3.619	c0.44-0.333,0.528-0.96,0.194-1.401l-9.384-12.086l6.866-2.675C35.367,20.09,35.412,18.955,34.625,18.575z">
                </path>
                <path fill="none" stroke="#2e9bfe" stroke-linecap="round" stroke-linejoin="round" stroke-width="3"
                    d="M30.696,27.048l-2.556-3.291l6.866-2.675c0.814-0.317,0.859-1.452,0.072-1.832L12.616,8.386	c-0.734-0.355-1.562,0.271-1.42,1.074l1.464,8.562">
                </path>
                <path fill="none" stroke="#2e9bfe" stroke-linecap="round" stroke-linejoin="round" stroke-width="3"
                    d="M13.572,22.919l1.962,11.112c0.152,0.86,1.256,1.126,1.783,0.429l4.444-5.879l9.384,12.086	c0.333,0.44,0.96,0.528,1.401,0.194l4.785-3.619c0.44-0.333,0.528-0.96,0.194-1.401l-3.258-4.196">
                </path>
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
    // 当前 SVG viewBox = 0 0 1024 1024，光标实际渲染尺寸 = width/height = 32x32
    // 蓝色箭头的"尖端"在 viewBox 中大约位于 (390, 210)
    // 映射到 32px： x = 390/1024 * 32 ≈ 12,  y = 210/1024 * 32 ≈ 7
    const HOTSPOT_X = 12;
    const HOTSPOT_Y = 7;

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
