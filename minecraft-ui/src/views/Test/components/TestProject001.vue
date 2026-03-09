<template>
  <div class="login-container">
    <!-- 左侧介绍区域 -->
    <div class="left-section" :class="{ 'register-mode': isRegister }">
      <div class="logo">爵来星云</div>

      <!-- 登录模式内容 -->
      <transition v-if="!isRegister" name="fade-left">
        <div class="content-wrapper">
          <h1>咖啡连锁门店管理平台</h1>
          <p class="description">
            专注咖啡连锁门店的一体化管理系统，涵盖员工管理、每日上班签到、薪资核算与物料管理。
          </p>
          <div class="stats">
            <div class="stat-item">
              <div class="stat-number">5</div>
              <div class="stat-label">连锁门店</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">200+</div>
              <div class="stat-label">签约伙伴</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">99%</div>
              <div class="stat-label">员工好评</div>
            </div>
          </div>
          <button class="register-btn" @click="isRegister = true">新伙伴注册 →</button>
        </div>
      </transition>

      <!-- 注册模式内容 -->
      <transition v-else name="fade-right">
        <div class="content-wrapper">
          <h2>加入爵来星云</h2>
          <p class="description">
            录入您的入职信息，成为爵来星云咖啡大家庭的一员。
            与全国200+的伙伴们一起用热咖啡温暖每一位顾客。
          </p>
          <div class="steps">
            <div class="step-item">
              <div class="step-number">01</div>
              <div class="step-text">填写基本信息</div>
            </div>
            <div class="step-item">
              <div class="step-number">02</div>
              <div class="step-text">选择门店与岗位</div>
            </div>
            <div class="step-item">
              <div class="step-number">03</div>
              <div class="step-text">提交注册信息</div>
            </div>
          </div>
          <button class="login-btn" @click="isRegister = false">← 返回登录</button>
        </div>
      </transition>
    </div>

    <!-- 右侧表单区域 -->
    <div class="right-section" :class="{ 'register-mode': isRegister }">
      <!-- 登录表单 -->
      <transition v-if="!isRegister" name="slide-up">
        <div class="form-wrapper">
          <div class="login-form">
            <h3>CREW PORTAL</h3>
            <h2>欢迎回来 <span class="user-icon">👤</span></h2>
            <p class="form-subtitle">选择角色，登录您的工作台</p>
            <div class="role-selector">
              <button class="role-btn active">门店员工</button>
              <button class="role-btn">管理后台</button>
            </div>
            <div class="form-group">
              <label>工号 / 账号</label>
              <input type="text" placeholder="admin" value="admin" class="input-animation">
            </div>
            <div class="form-group">
              <label>登录密码</label>
              <input type="password" placeholder="请输入密码" class="input-animation">
            </div>
            <button class="submit-btn">登录工作台</button>
            <div class="form-footer">
              <a href="#">忘记密码？</a>
              <a href="#" @click.prevent="isRegister = true">立即注册</a>
            </div>
          </div>
        </div>
      </transition>

      <!-- 注册表单 -->
      <transition v-else name="slide-down">
        <div class="form-wrapper">
          <div class="register-form">
            <h3>NEW PARTNER</h3>
            <h2>新伙伴注册</h2>
            <p class="form-subtitle">录入信息，加入爵来星云大家庭</p>
            <div class="form-group">
              <label>请设置工号</label>
              <input type="text" placeholder="请设置工号" class="input-animation">
            </div>
            <div class="form-group">
              <label>真实姓名</label>
              <input type="text" placeholder="请输入真实姓名" value="admin" class="input-animation">
            </div>
            <div class="form-group">
              <label>登录密码</label>
              <input type="password" placeholder="请设置密码" class="input-animation">
            </div>
            <div class="form-row">
              <div class="form-group half">
                <label>所属门店</label>
                <select class="input-animation">
                  <option>北京旗舰点</option>
                  <option>上海中心店</option>
                  <option>广州天河店</option>
                  <option>深圳南山店</option>
                  <option>杭州西湖店</option>
                </select>
              </div>
              <div class="form-group half">
                <label>职位</label>
                <select class="input-animation">
                  <option>成长</option>
                  <option>资深</option>
                  <option>店长</option>
                  <option>区域经理</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label>联系电话</label>
              <input type="text" placeholder="请输入联系电话" value="12345678901" class="input-animation">
            </div>
            <button class="submit-btn">提交注册</button>
            <div class="form-footer">
              <a href="#" @click.prevent="isRegister = false">已有账号？返回登录</a>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
  <!-- 背景 -->
  <div>
    <Login_background />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Login_background from '@/components/LoginComponent/Login_background.vue'
const isRegister = ref(false)

</script>

<style scoped>
/* 基础样式 */
.login-container {
  display: flex;
  width: 100%;
  max-width: 1000px;
  max-height: 600px;
  font-family: 'Arial', sans-serif;
  border-radius: 16px;
  overflow: hidden;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 100;
}

.left-section {
  width: 50%;
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 100%);
  color: white;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.5s ease;
}

.left-section::before {
  content: '';
  position: absolute;
  bottom: -100px;
  right: -100px;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  animation: floating 6s ease-in-out infinite;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 40px;
  display: flex;
  align-items: center;
  animation: slideInLeft 0.8s ease-out;
}

.logo::before {
  content: '☕';
  margin-right: 10px;
  font-size: 28px;
  animation: bounce 2s ease-in-out infinite;
}

.content-wrapper {
  animation: fadeIn 0.8s ease-out;
}

.register-btn,
.login-btn {
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: white;
  padding: 12px 26px;
  border-radius: 40px;
  font-weight: 500;
  font-size: 15px;
  width: fit-content;
  cursor: default;
  /* 仅作展示，无实际功能 */
  transition: 0.2s;
}

.register-btn,
.login-btn:hover {
  background: rgba(255, 255, 255, 0.26);
}

.left-section h1 {
  font-size: 32px;
  margin-bottom: 20px;
  line-height: 1.2;
  animation: slideInUp 0.8s ease-out 0.2s both;
}

.left-section h2 {
  font-size: 28px;
  margin-bottom: 20px;
  line-height: 1.2;
  animation: slideInUp 0.8s ease-out 0.2s both;
}

.description {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 40px;
  opacity: 0.9;
  animation: slideInUp 0.8s ease-out 0.4s both;
}

.stats {
  display: flex;
  gap: 40px;
  margin-bottom: 60px;
  animation: slideInUp 0.8s ease-out 0.6s both;
}

.stat-item {
  text-align: center;
  animation: scaleIn 0.8s ease-out 0.8s both;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  animation: countUp 1.5s ease-out 1s both;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.steps {
  margin-bottom: 60px;
  animation: slideInUp 0.8s ease-out 0.6s both;
}

.step-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  animation: slideInLeft 0.8s ease-out 0.8s both;
}

.step-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 14px;
  font-weight: bold;
  animation: pulse 2s ease-in-out infinite;
}

.right-section {
  width: 50%;
  background: white;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  transition: all 0.5s ease;
}

.right-section::before {
  content: '';
  position: absolute;
  bottom: -100px;
  left: -100px;
  width: 400px;
  height: 400px;
  background: rgba(139, 69, 19, 0.05);
  border-radius: 50%;
  animation: floating 8s ease-in-out infinite reverse;
}

.form-wrapper {
  animation: fadeIn 0.8s ease-out;
  max-height: 80vh;
  overflow-y: auto;
  padding-right: 10px;
}

.form-wrapper::-webkit-scrollbar {
  width: 0;
  display: none;
}

.form-wrapper {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.login-form,
.register-form {
  max-width: 400px;
  width: 100%;
}

.login-form h3,
.register-form h3 {
  color: #8B4513;
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 10px;
  text-transform: uppercase;
  letter-spacing: 1px;
  animation: slideInUp 0.8s ease-out 0.2s both;
}

.login-form h2,
.register-form h2 {
  font-size: 28px;
  margin-bottom: 10px;
  color: #333;
  animation: slideInUp 0.8s ease-out 0.4s both;
}

.form-subtitle {
  color: #666;
  margin-bottom: 40px;
  font-size: 14px;
  animation: slideInUp 0.8s ease-out 0.6s both;
}

.user-icon {
  font-size: 20px;
  margin-left: 5px;
  animation: bounce 2s ease-in-out infinite;
}

.role-selector {
  display: flex;
  margin-bottom: 30px;
  gap: 10px;
  animation: slideInUp 0.8s ease-out 0.8s both;
}

.role-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  position: relative;
  overflow: hidden;
}

.role-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(139, 69, 19, 0.1), transparent);
  transition: left 0.5s ease;
}

.role-btn:hover::before {
  left: 100%;
}

.role-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.role-btn.active {
  background: #8B4513;
  color: white;
  border-color: #8B4513;
}

.form-group {
  margin-bottom: 20px;
  animation: slideInUp 0.8s ease-out 1s both;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  animation: slideInUp 0.8s ease-out 1.2s both;
}

.form-group.half {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #333;
  font-weight: 500;
  transition: all 0.3s ease;
}

.input-animation {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.input-animation::placeholder {
  transition: all 0.3s ease;
  color: #999;
}

.input-animation:focus {
  outline: none;
  border-color: #8B4513;
  box-shadow: 0 0 0 3px rgba(139, 69, 19, 0.1);
  transform: translateY(-2px);
}

.input-animation:focus::placeholder {
  color: #8B4513;
  transform: translateY(-25px);
  font-size: 12px;
  opacity: 0.8;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  animation: slideInUp 0.8s ease-out 1.4s both;
  position: relative;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.3);
}

.form-footer {
  display: flex;
  justify-content: center;
  font-size: 14px;
  animation: slideInUp 0.8s ease-out 1.6s both;
}

.form-footer a {
  color: #8B4513;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
}

.form-footer a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #8B4513;
  transition: width 0.3s ease;
}

.form-footer a:hover::after {
  width: 100%;
}

.form-footer a:hover {
  color: #A0522D;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.8);
  }

  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes pulse {

  0%,
  100% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.1);
  }
}

@keyframes bounce {

  0%,
  20%,
  50%,
  80%,
  100% {
    transform: translateY(0);
  }

  40% {
    transform: translateY(-10px);
  }

  60% {
    transform: translateY(-5px);
  }
}

@keyframes floating {

  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 过渡动画 */
.fade-left-enter-active,
.fade-left-leave-active,
.fade-right-enter-active,
.fade-right-leave-active {
  transition: all 0.5s ease;
}

.fade-left-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-left-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.fade-right-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.fade-right-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

.slide-up-enter-active,
.slide-up-leave-active,
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.5s ease;
}

.slide-up-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.slide-up-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}

.slide-down-enter-from {
  opacity: 0;
  transform: translateY(-30px);
}

.slide-down-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .left-section,
  .right-section {
    width: 100%;
    height: 50%;
  }

  .left-section {
    padding: 40px;
  }

  .right-section {
    padding: 40px;
  }

  .stats {
    gap: 20px;
  }

  .form-row {
    flex-direction: column;
    gap: 20px;
  }

  .form-group.half {
    width: 100%;
  }
}
</style>