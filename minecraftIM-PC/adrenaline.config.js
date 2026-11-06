'use strict';

/**
 * Adrenaline.js 运行时裁剪配置。
 * 默认使用系统原生 WebView 引擎，未使用的浏览器能力在编译期被剔除。
 * 切换 CEF：设置 engine: 'cef' 并先执行 `npm run setup:cef`。
 */
module.exports = {
  engine: 'webview',
  modules: {
    devtools: true,
    webrtc: true,
    pdf: false,
    cleanPrivacy: false,
    localFiles: true
  }
};
