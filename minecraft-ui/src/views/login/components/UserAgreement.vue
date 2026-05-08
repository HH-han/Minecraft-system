<template>
  <div class="policy-container">
    <div class="policy-header">
      <h1>用户协议</h1>
      <p class="last-updated">最后更新：2026年1月1日</p>
    </div>

    <div class="policy-content">
      <section class="policy-section">
        <h2>1. 服务说明</h2>
        <p>欢迎使用博览旅行平台（以下简称"我们"或"平台"）。本用户协议（以下简称"协议"）规定了您在使用我们提供的旅行服务时与本公司之间形成的权利义务关系。请在注册或使用平台服务前仔细阅读本协议的全部内容。</p>
        <p>通过访问或使用我们的服务，您确认已阅读、理解并同意接受本协议的全部条款和条件。如果您不同意本协议的任何内容，请停止使用我们的服务。</p>
      </section>

      <section class="policy-section">
        <h2>2. 账户注册与安全</h2>
        <p>您在使用平台服务前需要注册账户。在注册过程中，您承诺：</p>
        <ul>
          <li>提供真实、准确、完整和最新的个人信息</li>
          <li>维护并及时更新您的个人信息的真实性和完整性</li>
          <li>妥善保管您的账户信息和密码</li>
          <li>对您账户下发生的所有活动承担责任</li>
          <li>在发现任何未经授权使用您账户的行为时立即通知我们</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>3. 服务内容</h2>
        <p>我们的平台提供以下服务：</p>
        <ul>
          <li>旅行目的地信息浏览与搜索</li>
          <li>出行计划制定与管理</li>
          <li>票务预订服务（火车票、汽车票、机票、轮船票）</li>
          <li>用户评价与分享</li>
          <li>个性化旅行推荐</li>
        </ul>
        <p>我们保留随时调整、修改或终止部分或全部服务的权利，并会通过合理方式通知用户。</p>
      </section>

      <section class="policy-section">
        <h2>4. 用户行为规范</h2>
        <p>您在使用我们的服务时不得：</p>
        <ul>
          <li>违反任何适用的法律法规</li>
          <li>侵犯他人知识产权或其他合法权益</li>
          <li>发布、传送含有违法、淫秽、色情、赌博、暴力、恐怖或教唆犯罪的内容</li>
          <li>进行任何可能危害网络安全的行为</li>
          <li>干扰或破坏平台服务的正常运行</li>
          <li>利用平台进行任何商业宣传活动，未经许可的营销推广</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>5. 票务预订规则</h2>
        <p>通过平台预订票务时，请注意：</p>
        <ul>
          <li>票务价格以实际预订时显示的价格为准</li>
          <li>预订成功后请按指定时间和地点取票</li>
          <li>退改签政策因供应商不同而有所差异，请以具体票务页面说明为准</li>
          <li>因用户个人原因导致的行程变更，需自行承担相应费用</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>6. 知识产权</h2>
        <p>平台及其关联公司拥有的所有知识产权，包括但不限于商标、标识、文本、图片、音视频、软件和界面设计，均受法律保护。未经授权，您不得擅自复制、修改、传播或使用上述内容。</p>
      </section>

      <section class="policy-section">
        <h2>7. 免责声明</h2>
        <p>在法律允许的最大范围内，我们不对以下情况承担责任：</p>
        <ul>
          <li>因不可抗力导致的损失</li>
          <li>因第三方原因导致的服务中断或数据丢失</li>
          <li>用户因自身操作失误导致的损失</li>
          <li>第三方服务商提供的产品或服务质量问题</li>
        </ul>
      </section>

      <section class="policy-section">
        <h2>8. 协议修改</h2>
        <p>我们保留随时修改本协议的权利。修改后的协议一经在平台上公布即生效。如果您在协议修改后继续使用我们的服务，视为您已接受修改后的协议。</p>
      </section>

      <section class="policy-section">
        <h2>9. 联系我们</h2>
        <p>如果您对本协议有任何疑问或意见，请通过以下方式联系我们：</p>
        <ul class="contact-list">
          <li>客服邮箱：support@bolan travel.com</li>
          <li>客服电话：400-123-4567</li>
          <li>工作时间：周一至周日 09:00-21:00</li>
        </ul>
      </section>
    </div>

    <div class="policy-footer">
      <button class="close-btn" @click="handleClose">关闭</button>
    </div>
  </div>
</template>

<script setup>
const emit = defineEmits(['close']);

const handleClose = () => {
  emit('close');
};
</script>

<style scoped>
.policy-container {
  background: #ffffff;
  border-radius: 20px;
  max-width: 800px;
  max-height: 85vh;
  overflow-y: auto;
  padding: 48px 40px;
  font-family: 'Inter', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}

.policy-header {
  text-align: center;
  margin-bottom: 40px;
  padding-bottom: 24px;
  border-bottom: 1px solid #d2d2d6;
}

.policy-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 12px 0;
  letter-spacing: -0.02em;
}

.last-updated {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
}

.policy-content {
  color: #1d1d1f;
  line-height: 1.6;
}

.policy-section {
  margin-bottom: 32px;
}

.policy-section:last-child {
  margin-bottom: 0;
}

.policy-section h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 16px 0;
  letter-spacing: -0.01em;
}

.policy-section p {
  font-size: 15px;
  color: #1d1d1f;
  margin: 0 0 12px 0;
  line-height: 1.7;
}

.policy-section ul {
  margin: 0 0 16px 0;
  padding-left: 24px;
}

.policy-section li {
  font-size: 15px;
  color: #1d1d1f;
  margin-bottom: 8px;
  line-height: 1.6;
}

.policy-section li:last-child {
  margin-bottom: 0;
}

.contact-list {
  list-style: none;
  padding-left: 0;
}

.contact-list li {
  margin-bottom: 8px;
}

.policy-footer {
  margin-top: 40px;
  padding-top: 24px;
  border-top: 1px solid #d2d2d6;
  display: flex;
  justify-content: center;
}

.close-btn {
  background: #2997ff;
  color: #ffffff;
  border: none;
  padding: 12px 32px;
  border-radius: 24px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #0066cc;
}

.policy-container::-webkit-scrollbar {
  width: 8px;
}

.policy-container::-webkit-scrollbar-track {
  background: #f5f5f7;
  border-radius: 4px;
}

.policy-container::-webkit-scrollbar-thumb {
  background: #d2d2d6;
  border-radius: 4px;
}

.policy-container::-webkit-scrollbar-thumb:hover {
  background: #a1a1a6;
}
</style>
