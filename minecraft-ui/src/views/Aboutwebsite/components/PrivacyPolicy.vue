<template>
  <div class="policy-container">
    <header class="policy-header">
      <h1 class="policy-title">隐私政策</h1>
      <p class="policy-date">更新日期：2025年1月1日</p>
    </header>

    <div class="policy-content">
      <section class="policy-section">
        <h2>信息的收集与使用</h2>
        <p>我们致力于保护您的个人隐私。本隐私政策说明了我们如何收集、使用、披露、转移和存储您的信息。</p>
        <p>当您访问我们的网站时，我们会自动收集某些信息，包括您的IP地址、浏览器类型、操作系统、访问时间以及您查看的页面。</p>
      </section>

      <section class="policy-section">
        <h2>我们收集的信息</h2>
        <ul>
          <li><strong>个人信息</strong>：您在注册账户、订阅 newsletter 或参与活动时提供的姓名、电子邮件地址、电话号码等信息。</li>
          <li><strong>使用数据</strong>：包括您的设备信息、IP 地址、访问时间、浏览的页面以及在网站上的操作。</li>
          <li><strong>Cookies</strong>：我们使用 cookies 和类似技术来提升您的用户体验。</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>信息的使用方式</h2>
        <p>我们收集的信息用于：</p>
        <ul>
          <li>提供、维护和改进我们的服务</li>
          <li>处理您的请求和交易</li>
          <li>向您发送技术通知、更新、安全警报</li>
          <li>响应您的评论、问题或客户服务请求</li>
          <li>监控和分析服务使用情况</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>信息的共享与披露</h2>
        <p>我们不会出售您的个人信息。我们可能在以下情况下共享您的信息：</p>
        <ul>
          <li>经您明确同意</li>
          <li>用于外部处理（我们的服务提供商）</li>
          <li>遵守法律义务</li>
          <li>保护我们的权利和财产安全</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>数据安全</h2>
        <p>我们采用行业标准的安全措施来保护您的个人信息。然而，没有任何通过互联网传输的方法或电子存储方法是100%安全的。</p>
      </section>

      <section class="policy-section">
        <h2>您的权利</h2>
        <p>根据适用法律，您可能有权：</p>
        <ul>
          <li>访问我们持有的关于您的个人信息</li>
          <li>更正不准确的信息</li>
          <li>删除您的个人信息</li>
          <li>限制或反对处理</li>
          <li>数据可携带性</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>联系我们</h2>
        <p>如果您对本隐私政策有任何疑问，请通过以下方式联系我们：</p>
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
  margin: 0;
  padding-left: 24px;
}

.policy-section li {
  font-size: 16px;
  color: #1d1d1f;
  line-height: 1.6;
  margin-bottom: 8px;
}

.policy-section li strong {
  color: #1d1d1f;
  font-weight: 600;
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
}
</style>