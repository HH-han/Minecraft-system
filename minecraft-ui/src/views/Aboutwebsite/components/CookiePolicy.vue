<template>
  <div class="policy-container">
    <header class="policy-header">
      <h1 class="policy-title">Cookie 政策</h1>
      <p class="policy-date">更新日期：2025年1月1日</p>
    </header>

    <div class="policy-content">
      <section class="policy-section">
        <h2>什么是 Cookie</h2>
        <p>Cookie 是您访问网站时浏览器存储在您设备上的小型文本文件。它们被广泛用于确保网站正常运作、提升用户体验，以及帮助网站所有者获取有用的信息。</p>
      </section>

      <section class="policy-section">
        <h2>我们使用的 Cookie 类型</h2>
        
        <div class="cookie-category">
          <h3>必要 Cookie</h3>
          <p>这些 Cookie 是网站运行所必需的，无法关闭。它们通常用于响应您所做的操作，例如登录、填写表单或设置隐私偏好。</p>
          <ul>
            <li>会话管理 Cookie</li>
            <li>安全 Cookie</li>
            <li>负载均衡 Cookie</li>
          </ul>
        </div>

        <div class="cookie-category">
          <h3>功能 Cookie</h3>
          <p>这些 Cookie 使网站能够记住您的选择（例如您的语言或地区），并提供更个性化、更便捷的功能。</p>
          <ul>
            <li>语言偏好 Cookie</li>
            <li>主题设置 Cookie</li>
            <li>记住登录状态</li>
          </ul>
        </div>

        <div class="cookie-category">
          <h3>性能 Cookie</h3>
          <p>这些 Cookie 收集关于访问者如何使用网站的信息，例如哪些页面最常访问，以及是否收到错误消息。</p>
          <ul>
            <li>页面访问分析</li>
            <li>错误日志记录</li>
            <li>用户流量分析</li>
          </ul>
        </div>

        <div class="cookie-category">
          <h3>营销 Cookie</h3>
          <p>这些 Cookie 用于跟踪您在不同网站上的浏览活动，以显示与您相关的广告。</p>
          <ul>
            <li>广告个性化</li>
            <li>跨网站追踪</li>
            <li>广告效果分析</li>
          </ul>
        </div>
      </section>

      <section class="policy-section">
        <h2>第三方 Cookie</h2>
        <p>某些 Cookie 由我们信任的第三方服务提供商设置，包括：</p>
        <ul>
          <li><strong>Google Analytics</strong>：用于分析网站流量和使用情况</li>
          <li><strong>社交媒体平台</strong>：用于分享功能和社交媒体集成</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>如何管理 Cookie</h2>
        <p>您可以通过浏览器设置管理 Cookie 选项：</p>
        <ul>
          <li><strong>Chrome</strong>：设置 → 隐私和安全 → Cookie</li>
          <li><strong>Safari</strong>：偏好设置 → 隐私</li>
          <li><strong>Firefox</strong>：选项 → 隐私与安全</li>
          <li><strong>Edge</strong>：设置 → Cookie 和网站权限</li>
        </ul>
        <p>请注意，禁用某些 Cookie 可能会影响网站功能。</p>
      </section>

      <section class="policy-section">
        <h2>隐私选择</h2>
        <p>您有权选择是否接受非必要 Cookie。当您首次访问我们的网站时，我们会询问您的偏好设置。您可以随时通过浏览器设置更改您的选择。</p>
      </section>

      <section class="policy-section">
        <h2>联系我们</h2>
        <p>如果您对本 Cookie 政策有任何疑问，请通过以下方式联系我们：</p>
        <p class="contact-info">电子邮件：privacy@minecraft-official.com</p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.policy-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 60px 40px 100px;
}

.policy-header {
  margin-bottom: 48px;
  padding-bottom: 32px;
  border-bottom: 1px solid #d2d2d6;
}

.policy-title {
  font-size: 48px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 12px 0;
  line-height: 1.05;
}

.policy-date {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
}

.policy-content {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.policy-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 16px 0;
  line-height: 1.2;
}

.policy-section p {
  font-size: 16px;
  color: #1d1d1f;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.policy-section ul {
  margin: 0 0 12px 0;
  padding-left: 24px;
}

.policy-section li {
  font-size: 16px;
  color: #1d1d1f;
  line-height: 1.6;
  margin-bottom: 8px;
}

.policy-section li strong {
  font-weight: 600;
}

.cookie-category {
  background: #f5f5f7;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
}

.cookie-category:last-of-type {
  margin-bottom: 0;
}

.cookie-category h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 12px 0;
}

.cookie-category p {
  margin: 0 0 12px 0;
}

.contact-info {
  color: #2997ff !important;
}

@media (max-width: 768px) {
  .policy-container {
    padding: 40px 20px 80px;
  }

  .policy-title {
    font-size: 32px;
  }

  .policy-section h2 {
    font-size: 20px;
  }

  .cookie-category {
    padding: 20px;
  }
}
</style>