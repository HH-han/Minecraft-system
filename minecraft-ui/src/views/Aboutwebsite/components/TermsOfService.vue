<template>
  <div class="policy-container">
    <header class="policy-header">
      <h1 class="policy-title">服务条款</h1>
      <p class="policy-date">更新日期：2025年1月1日</p>
    </header>

    <div class="policy-content">
      <section class="policy-section">
        <h2>概述</h2>
        <p>欢迎使用 Minecraft 官方网站及相关服务。您访问或使用我们的服务，即表示您同意遵守本服务条款。我们保留随时修改这些条款的权利。</p>
      </section>

      <section class="policy-section">
        <h2>服务说明</h2>
        <p>Minecraft 官方网站提供游戏信息、新闻、更新下载、社区交流等服务。我们致力于为用户提供最好的体验，但不对服务的持续可用性做出保证。</p>
      </section>

      <section class="policy-section">
        <h2>用户账户</h2>
        <p>您可能需要创建账户才能使用某些功能。您负责：</p>
        <ul>
          <li>保持账户凭证的安全</li>
          <li>对账户下发生的所有活动承担责任</li>
          <li>在发现未经授权的使用时立即通知我们</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>知识产权</h2>
        <p>我们的服务及其中包含的所有内容（包括但不限于文本、图形、徽标、图像、软件）均受版权、商标和其他知识产权法律保护。</p>
        <p>Minecraft®、Mojang® 及其相关徽标是 Mojang Studios 的商标。未经授权，不得修改、复制、分发或使用这些材料。</p>
      </section>

      <section class="policy-section">
        <h2>用户行为</h2>
        <p>您同意不会：</p>
        <ul>
          <li>违反任何适用的法律或法规</li>
          <li>侵犯他人权利或进行骚扰、辱骂行为</li>
          <li>上传或传播恶意软件、病毒或破坏性内容</li>
          <li>尝试未经授权访问我们的系统</li>
          <li>进行任何可能损害我们服务的行为</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>免责声明</h2>
        <p>我们的服务按"原样"提供，不提供任何明示或暗示的保证。我们不对服务的准确性、可靠性或完整性做出任何陈述。</p>
      </section>

      <section class="policy-section">
        <h2>责任限制</h2>
        <p>在适用法律允许的最大范围内，我们不对任何间接、偶然、特殊或后果性损害承担责任，包括但不限于利润损失、数据丢失或业务中断。</p>
      </section>

      <section class="policy-section">
        <h2>服务变更</h2>
        <p>我们保留随时修改、暂停或终止服务（或任何部分）的权利，恕不另行通知。我们不对服务的任何修改、暂停或终止承担责任。</p>
      </section>

      <section class="policy-section">
        <h2>适用法律</h2>
        <p>本服务条款受相关法律管辖。如发生争议，双方应首先通过友好协商解决。</p>
      </section>

      <section class="policy-section">
        <h2>联系我们</h2>
        <p>如果您对本服务条款有任何疑问，请通过以下方式联系我们：</p>
        <p class="contact-info">电子邮件：legal@minecraft-official.com</p>
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