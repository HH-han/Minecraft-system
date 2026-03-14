<template>
  <div class="login-container">
    <!-- 左侧介绍区域 -->
    <div class="left-section" :class="{ 'register-mode': isRegister }">
      <div class="logo">博览旅行</div>

      <!-- 登录模式内容 -->
      <transition v-if="!isRegister" name="fade-left">
        <div class="content-wrapper">
          <h1>博览旅行管理平台</h1>
          <p class="description">
            专注旅行管理的一体化系统，涵盖用户管理、旅游推荐与数据分析。
          </p>
          <div class="stats">
            <div class="stat-item">
              <div class="stat-number">1000+</div>
              <div class="stat-label">注册用户</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">50+</div>
              <div class="stat-label">旅游目的地</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">99%</div>
              <div class="stat-label">用户好评</div>
            </div>
          </div>
        </div>
      </transition>

      <!-- 注册模式内容 -->
      <transition v-else name="fade-right">
        <div class="content-wrapper">
          <h2>加入博览旅行</h2>
          <p class="description">
            录入您的信息，成为博览旅行的一员。
            与我们一起探索世界各地的精彩。
          </p>
          <div class="steps">
            <div class="step-item">
              <div class="step-number">01</div>
              <div class="step-text">填写基本信息</div>
            </div>
            <div class="step-item">
              <div class="step-number">02</div>
              <div class="step-text">选择兴趣偏好</div>
            </div>
            <div class="step-item">
              <div class="step-number">03</div>
              <div class="step-text">提交注册信息</div>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- 右侧表单区域 -->
    <div class="right-section" :class="{ 'register-mode': isRegister }">
      <!-- 登录表单 -->
      <transition v-if="!isRegister" name="slide-up">
        <div class="form-wrapper">
          <div class="login-form">
            <h3>USER PORTAL</h3>
            <div class="welcome-message">
              <h2>欢迎回来 </h2>
              <span class="user-icon">
                <svg t="1773503868766" class="icon" viewBox="0 0 1024 1024" version="1.1"
                  xmlns="http://www.w3.org/2000/svg" p-id="28684" width="256" height="256">
                  <path
                    d="M510.45 70.554c-243.163 0-442.115 198.951-442.115 442.115 0 243.16 198.952 442.11 442.116 442.11 243.159 0 442.11-198.95 442.11-442.11 0-243.165-198.951-442.115-442.11-442.115z m11.051 132.633c168.556 0 168.556 127.11 168.556 198.95 0 71.847-66.318 204.48-168.556 207.24-99.475 0-168.556-132.632-168.556-207.24 0.001-71.841 0.001-198.95 168.556-198.95z m-11.05 726.723c-127.11 0-243.164-58.03-320.534-149.211 11.051-27.635 24.868-58.03 44.212-74.607 41.446-33.162 165.79-88.425 165.79-88.425l77.37 149.215 13.816-35.923L469 686.747l44.212-44.208 44.212 44.208-19.345 46.974 11.055 35.924 80.132-146.45s124.343 55.262 165.793 88.424c19.34 13.813 33.157 38.685 41.447 60.787C761.9 869.12 643.082 929.91 510.45 929.91z m0 0"
                    fill="" p-id="28685"></path>
                </svg></span>
            </div>

            <p class="form-subtitle">请登录您的账户</p>
            <!-- 二维码登录快捷入口 -->
            <div class="loginpage-qr-link" @click="showQrCode">
              <div class="loginpage-qr-icon">
                <svg t="1773502026367" class="icon" viewBox="0 0 1024 1024" version="1.1"
                  xmlns="http://www.w3.org/2000/svg" p-id="25552" width="256" height="256">
                  <path
                    d="M117.368 487.445h371.92v-371.92h-371.92v371.92z m92.981-278.94h185.96v185.96h-185.96v-185.96z m325.43-92.98v371.92h371.92v-371.92H535.78z m278.94 278.94h-185.96v-185.96h185.96v185.96z m-557.88-46.49h92.98v-92.98h-92.98v92.98z m-139.47 557.88h371.92v-371.92h-371.92v371.92z m92.98-278.94h185.96v185.96h-185.96v-185.96z m557.88-371.92h-92.98v92.98h92.98v-92.98z m-232.45 650.86h92.98v-92.98h-92.98v92.98z m92.98-278.94v185.96h92.98v-185.96h-92.98z m185.96 185.96h-92.98v92.98h185.96v-185.96h-92.98v92.98z m0-185.96h92.98v-92.98h-92.98v92.98z m-278.94-92.98v92.98h92.98v-92.98h-92.98z m-278.94 232.45h92.98v-92.98h-92.98v92.98z"
                    fill="#929292" p-id="25553"></path>
                </svg>
              </div>
            </div>
            <!-- 用户头像显示 -->
            <div class="loginpage-avatar-container">
              <div class="loginpage-avatar">
                <transition name="fade" mode="out-in">
                  <img :key="loginForm.hasInput ? 'user' : 'default'"
                    :src="loginForm.hasInput ? loginForm.image : defaultAvatar" alt="用户头像"
                    class="loginpage-avatar-img" />
                </transition>
              </div>
            </div>
            <div class="userlogin-input-centainer">
              <div class="userlogin-inputContainer">
                <input required="required" id="userlogin-usernameField" placeholder="请输入账号" @input="handleUsernameInput"
                  v-model="loginForm.username" type="text">
                <label class="userlogin-usernameLabel" for="userlogin-usernameField">请输入账号</label>
                <svg t="1746095535223" class="userlogin-userIcon" viewBox="0 0 1024 1024" version="1.1" p-id="26266">
                  <path
                    d="M512 273.066667m-273.066667 0a273.066667 273.066667 0 1 0 546.133334 0 273.066667 273.066667 0 1 0-546.133334 0Z"
                    p-id="26267"></path>
                  <path
                    d="M375.466667 614.4h273.066666a341.333333 341.333333 0 0 1 341.333334 341.333333v68.266667H34.133333v-68.266667a341.333333 341.333333 0 0 1 341.333334-341.333333z"
                    p-id="26268"></path>
                </svg>
              </div>
              <!-- 密码输入框 -->
              <div class="userlogin-inputContainer">
                <input required="required" id="userlogin-passwordField" placeholder="请输入密码" v-model="loginForm.password"
                  type="password">
                <label class="userlogin-usernameLabel" for="userlogin-passwordField">请输入密码</label>
                <svg t="1746095118852" class="userlogin-userIcon" viewBox="0 0 1024 1024" version="1.1" p-id="21472">
                  <path
                    d="M808.435 370.238h-592.87A127.218 127.218 0 0 0 88.89 496.913v400.14a126.947 126.947 0 0 0 126.675 126.403h592.87A126.947 126.947 0 0 0 935.11 897.053v-400.14a127.218 127.218 0 0 0-126.675-126.675z m-244.65 347.948v131.296H459.4V717.915a105.744 105.744 0 1 1 105.2 0z"
                    p-id="21473"></path>
                  <path
                    d="M790.766 373.772h-81.55V278.63a197.08 197.08 0 0 0-394.16 0v95.142h-81.55V278.63a278.63 278.63 0 0 1 557.26 0z"
                    p-id="21474"></path>
                </svg>
              </div>
            </div>
            <!-- 其他方式登录 -->
            <div class="other-login-methods">
              <div class="other-login-title">其他方式登录</div>
              <div class="other-login-buttons">
                <!-- 微信登录 -->
                <button class="other-login-btn wechat-login" @click="wechatLogin">
                  <svg t="1773499701636" class="login-icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="7112" width="256" height="256">
                    <path
                      d="M347.729118 353.0242c-16.487119 0-29.776737 13.389539-29.776737 29.776737S331.241998 412.677596 347.729118 412.677596s29.776737-13.389539 29.776737-29.776737-13.289617-29.876659-29.776737-29.876659zM577.749415 511.800156c-13.689305 0-24.880562 11.091335-24.880563 24.880562 0 13.689305 11.091335 24.880562 24.880563 24.880562 13.689305 0 24.880562-11.191257 24.880562-24.880562s-11.191257-24.880562-24.880562-24.880562zM500.909446 412.677596c16.487119 0 29.776737-13.389539 29.776737-29.776737s-13.389539-29.776737-29.776737-29.776737c-16.487119 0-29.776737 13.389539-29.776737 29.776737s13.289617 29.776737 29.776737 29.776737zM698.455113 511.600312c-13.689305 0-24.880562 11.091335-24.880562 24.880562 0 13.689305 11.091335 24.880562 24.880562 24.880562 13.689305 0 24.880562-11.091335 24.880562-24.880562-0.099922-13.689305-11.191257-24.880562-24.880562-24.880562z"
                      fill="#00C800" p-id="7113"></path>
                    <path
                      d="M511.601093 0.799375C229.12178 0.799375 0.000781 229.820453 0.000781 512.399688s229.021077 511.600312 511.600312 511.600312 511.600312-229.021077 511.600312-511.600312S794.180328 0.799375 511.601093 0.799375z m-90.229508 634.504294c-27.37861 0-49.361436-5.595628-76.839969-10.991413l-76.640125 38.469945 21.882904-65.948477c-54.957065-38.370023-87.73146-87.831382-87.73146-148.084309 0-104.318501 98.722873-186.554254 219.32865-186.554255 107.815769 0 202.34192 65.648712 221.327088 153.979703-6.994536-0.799375-13.989071-1.298985-21.083529-1.298985-104.118657 0-186.454333 77.739266-186.454332 173.564403 0 15.98751 2.498048 31.275566 6.794692 45.964091-6.794692 0.599532-13.689305 0.899297-20.583919 0.899297z m323.547228 76.839969l16.48712 54.757221-60.153006-32.874317c-21.882904 5.495706-43.965652 10.991413-65.848555 10.991413-104.318501 0-186.554254-71.344262-186.554255-159.175644 0-87.631538 82.135831-159.175644 186.554255-159.175644 98.523029 0 186.254489 71.444184 186.254488 159.175644 0.099922 49.461358-32.774395 93.227166-76.740047 126.301327z"
                      fill="#00C800" p-id="7114"></path>
                  </svg>
                  <span>微信</span>
                </button>
                <!-- QQ登录 -->
                <button class="other-login-btn qq-login" @click="qqLogin">
                  <svg t="1773499770807" class="login-icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="13016" width="256" height="256">
                    <path
                      d="M511.09761 957.257c-80.159 0-153.737-25.019-201.11-62.386-24.057 6.702-54.831 17.489-74.252 30.864-16.617 11.439-14.546 23.106-11.55 27.816 13.15 20.689 225.583 13.211 286.912 6.767v-3.061z"
                      fill="#FAAD08" p-id="13017"></path>
                    <path
                      d="M496.65061 957.257c80.157 0 153.737-25.019 201.11-62.386 24.057 6.702 54.83 17.489 74.253 30.864 16.616 11.439 14.543 23.106 11.55 27.816-13.15 20.689-225.584 13.211-286.914 6.767v-3.061z"
                      fill="#FAAD08" p-id="13018"></path>
                    <path
                      d="M497.12861 474.524c131.934-0.876 237.669-25.783 273.497-35.34 8.541-2.28 13.11-6.364 13.11-6.364 0.03-1.172 0.542-20.952 0.542-31.155C784.27761 229.833 701.12561 57.173 496.64061 57.162 292.15661 57.173 209.00061 229.832 209.00061 401.665c0 10.203 0.516 29.983 0.547 31.155 0 0 3.717 3.821 10.529 5.67 33.078 8.98 140.803 35.139 276.08 36.034h0.972z"
                      fill="#000000" p-id="13019"></path>
                    <path
                      d="M860.28261 619.782c-8.12-26.086-19.204-56.506-30.427-85.72 0 0-6.456-0.795-9.718 0.148-100.71 29.205-222.773 47.818-315.792 46.695h-0.962C410.88561 582.017 289.65061 563.617 189.27961 534.698 185.44461 533.595 177.87261 534.063 177.87261 534.063 166.64961 563.276 155.56661 593.696 147.44761 619.782 108.72961 744.168 121.27261 795.644 130.82461 796.798c20.496 2.474 79.78-93.637 79.78-93.637 0 97.66 88.324 247.617 290.576 248.996a718.01 718.01 0 0 1 5.367 0C708.80161 950.778 797.12261 800.822 797.12261 703.162c0 0 59.284 96.111 79.783 93.637 9.55-1.154 22.093-52.63-16.623-177.017"
                      fill="#000000" p-id="13020"></path>
                    <path
                      d="M434.38261 316.917c-27.9 1.24-51.745-30.106-53.24-69.956-1.518-39.877 19.858-73.207 47.764-74.454 27.875-1.224 51.703 30.109 53.218 69.974 1.527 39.877-19.853 73.2-47.742 74.436m206.67-69.956c-1.494 39.85-25.34 71.194-53.24 69.956-27.888-1.238-49.269-34.559-47.742-74.435 1.513-39.868 25.341-71.201 53.216-69.974 27.909 1.247 49.285 34.576 47.767 74.453"
                      fill="#FFFFFF" p-id="13021"></path>
                    <path
                      d="M683.94261 368.627c-7.323-17.609-81.062-37.227-172.353-37.227h-0.98c-91.29 0-165.031 19.618-172.352 37.227a6.244 6.244 0 0 0-0.535 2.505c0 1.269 0.393 2.414 1.006 3.386 6.168 9.765 88.054 58.018 171.882 58.018h0.98c83.827 0 165.71-48.25 171.881-58.016a6.352 6.352 0 0 0 1.002-3.395c0-0.897-0.2-1.736-0.531-2.498"
                      fill="#FAAD08" p-id="13022"></path>
                    <path
                      d="M467.63161 256.377c1.26 15.886-7.377 30-19.266 31.542-11.907 1.544-22.569-10.083-23.836-25.978-1.243-15.895 7.381-30.008 19.25-31.538 11.927-1.549 22.607 10.088 23.852 25.974m73.097 7.935c2.533-4.118 19.827-25.77 55.62-17.886 9.401 2.07 13.75 5.116 14.668 6.316 1.355 1.77 1.726 4.29 0.352 7.684-2.722 6.725-8.338 6.542-11.454 5.226-2.01-0.85-26.94-15.889-49.905 6.553-1.579 1.545-4.405 2.074-7.085 0.242-2.678-1.834-3.786-5.553-2.196-8.135"
                      fill="#000000" p-id="13023"></path>
                    <path
                      d="M504.33261 584.495h-0.967c-63.568 0.752-140.646-7.504-215.286-21.92-6.391 36.262-10.25 81.838-6.936 136.196 8.37 137.384 91.62 223.736 220.118 224.996H506.48461c128.498-1.26 211.748-87.612 220.12-224.996 3.314-54.362-0.547-99.938-6.94-136.203-74.654 14.423-151.745 22.684-215.332 21.927"
                      fill="#FFFFFF" p-id="13024"></path>
                    <path
                      d="M323.27461 577.016v137.468s64.957 12.705 130.031 3.91V591.59c-41.225-2.262-85.688-7.304-130.031-14.574"
                      fill="#EB1C26" p-id="13025"></path>
                    <path
                      d="M788.09761 432.536s-121.98 40.387-283.743 41.539h-0.962c-161.497-1.147-283.328-41.401-283.744-41.539l-40.854 106.952c102.186 32.31 228.837 53.135 324.598 51.926l0.96-0.002c95.768 1.216 222.4-19.61 324.6-51.924l-40.855-106.952z"
                      fill="#EB1C26" p-id="13026"></path>
                  </svg>
                  <span>QQ</span>
                </button>
                <!-- 手机号登录 -->
                <button class="other-login-btn phone-login" @click="phoneLogin">
                  <svg t="1773499954254" class="login-icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="22125" width="256" height="256">
                    <path
                      d="M66.7 512.7c0 246.5 199.8 446.4 446.3 446.4s446.4-199.8 446.4-446.3v-0.1c0-246.5-199.8-446.4-446.3-446.4S66.7 266.1 66.7 512.7c0-0.1 0 0 0 0z"
                      fill="#BABABA" p-id="22126"></path>
                    <path
                      d="M643.5 249.4c30.7 0 55.6 25.8 55.6 57.6v413c0 31.8-24.9 57.6-55.6 57.6H377.8c-30.7 0-55.6-25.8-55.6-57.6V307c0-31.8 24.9-57.6 55.6-57.6h265.7zM510.7 611.1c-18.8 0-34 15.8-34 35.2s15.2 35.2 34 35.2 34-15.8 34-35.2-15.3-35.2-34-35.2z m0 0"
                      fill="#FFFFFF" p-id="22127"></path>
                  </svg>
                  <span>手机号</span>
                </button>
                <!-- 邮箱登录 -->
                <button class="other-login-btn email-login" @click="emailLogin">
                  <svg t="1773499832698" class="login-icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="18471" width="256" height="256">
                    <path
                      d="M873.472 0H150.528C67.584 0 0 67.584 0 150.528v722.944C0 956.416 67.584 1024 150.528 1024h722.944c82.944 0 150.528-67.584 150.528-150.528V150.528C1024 67.584 956.416 0 873.472 0z m-61.952 656.896c0 41.472-33.792 74.752-74.752 74.752H287.232c-41.472 0-74.752-33.28-74.752-74.752V367.104c0-41.472 33.28-74.752 74.752-74.752h449.024c41.472 0 74.752 33.28 74.752 74.752v289.792z"
                      fill="#4873ED" p-id="18472"></path>
                    <path
                      d="M712.192 427.008L512 556.032 311.808 427.008c-9.216-6.144-21.504-3.072-27.648 6.144-6.144 9.216-3.072 21.504 6.144 27.648l210.944 135.68c3.072 2.048 7.168 3.072 10.752 3.072 3.584 0 7.68-1.024 10.752-3.072L733.696 460.8a20.48 20.48 0 0 0 6.144-27.648 20.48 20.48 0 0 0-27.648-6.144z"
                      fill="#4873ED" p-id="18473"></path>
                  </svg>
                  <span>邮箱</span>
                </button>
              </div>
            </div>
            <button class="submit-btn" @click="handleLogin">立即登录</button>
            <div class="form-footer">
              <button @click="showForgotPassword" class="footer-btn">忘记密码？</button>
              <button @click="isRegister = true" class="footer-btn">立即注册</button>
            </div>
          </div>
        </div>
      </transition>

      <!-- 注册表单 -->
      <transition v-else name="slide-down">
        <div class="form-wrapper">
          <div class="register-form">
            <h3>NEW USER</h3>
            <h2>新用户注册</h2>
            <p class="form-subtitle">录入信息，加入博览旅行大家庭</p>
            <!-- 注册头像上传 -->
            <div class="registered-avatar-container">
              <div class="registered-avatar-preview"
                :style="{ backgroundImage: registerForm.avatar ? `url(${registerForm.avatar})` : `url(${defaultAvatar})` }">
                <label for="avatar-upload" class="registered-avatar-upload-label">
                  <span v-if="!registerForm.avatar">点击上传头像</span>
                  <input id="avatar-upload" type="file" accept="image/*" @change="handleAvatarUpload"
                    class="registered-avatar-upload" />
                </label>
              </div>
            </div>
            <div class="registered-css-input-container-all">
              <div>

                <div class="registered-css-input-container">
                  <input placeholder="" class="registered-css-input-field" type="text" v-model="registerForm.username">
                  <label for="enrolfirst-input-field" class="registered-css-input-label">请输入真实姓名...</label>
                  <span class="registered-css-input-highlight"></span>
                </div>

                <div class="registered-css-input-container">
                  <input placeholder="" class="registered-css-input-field" type="text" v-model="registerForm.email">
                  <label for="enrolfirst-input-field" class="registered-css-input-label">请输入邮箱...</label>
                  <span class="registered-css-input-highlight"></span>
                </div>
              </div>
              <div>
                <div class="registered-css-input-container">
                  <input placeholder="" class="registered-css-input-field" type="text" v-model="registerForm.phone">
                  <label for="enrolfirst-input-field" class="registered-css-input-label">请输入联系电话...</label>
                  <span class="registered-css-input-highlight"></span>
                </div>

                <div class="registered-css-input-container">
                  <input placeholder="" class="registered-css-input-field" type="password"
                    v-model="registerForm.password">
                  <label for="enrolfirst-input-field" class="registered-css-input-label">请设置密码...</label>
                  <span class="registered-css-input-highlight"></span>
                </div>

                <div class="registered-css-input-container">
                  <input placeholder="" class="registered-css-input-field" type="password"
                    v-model="registerForm.confirmPassword">
                  <label for="enrolfirst-input-field" class="registered-css-input-label">请确认密码...</label>
                  <span class="registered-css-input-highlight"></span>
                </div>
              </div>
            </div>
            <div class="registered-css-a-container">
              <input type="checkbox" id="agreeTerms" v-model="registerForm.agreeTerms" required />
              <a href="baidu.com" class="agreement-a4">我同意相关注册协议</a>
            </div>
            <div class="registered-center">
              <button class="registered-css-input-registered" @click="handleRegister">注册</button>
              <div class="form-footer">
                <button @click="isRegister = false" class="footer-btn">已有账号？返回登录</button>
              </div>
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
  <!-- 成功提示框 -->
  <div>
    <LoginSucceeded v-if="showSucceeded" :username="loginForm.username" :message="successMessage"
      @close="showSucceeded = false" />
  </div>
  <!-- 账号信息模态框 -->
  <div v-if="showAccountModal" class="loginpage-modal" @click="closeAccountModal">
    <div class="account-modal-content" @click.stop>
      <Account :account="generatedAccount" @confirm="confirmAccount" />
    </div>
  </div>
  <!-- 错误提示框 -->
  <div>
    <ErrorMessage v-if="showError" :message="errorMessage" @close="showError = false" />
  </div>
  <!-- 二维码登录模态框 -->
  <div v-if="isQrCodeVisible" class="loginpage-modal" @click="closeQrCode">
    <QRcodeLogin @click.stop />
  </div>
  <!-- 模糊背景模态框 -->
  <div v-if="isModalVisible" class="blur-modal" @click="closeModal">
    <div class="blur-modal-content" @click.stop>
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Login_background from '@/components/LoginComponent/Login_background.vue';
import LoginSucceeded from '@/components/PromptComponent/LoginSucceeded.vue';
import ErrorMessage from '@/components/PromptComponent/ErrorMessage.vue';
import { login, register } from '@/api/auth.js';
import { getUserByAccount } from '@/api/user.js';
import QRcodeLogin from '@/views/login/components/QRcodeLogin.vue'
import Account from '@/views/login/components/Account.vue'
import { useAuthStore } from '@/stores/auth.js'

// 登录-注册切换
const isRegister = ref(false);

// 模糊背景模态框状态
const isModalVisible = ref(false);

// 二维码登录模态框状态
const isQrCodeVisible = ref(false);

// 关闭模糊背景模态框
const closeModal = () => {
  isModalVisible.value = false;
};

// 显示二维码登录
const showQrCode = () => {
  isQrCodeVisible.value = true;
};

// 关闭二维码登录模态框
const closeQrCode = () => {
  isQrCodeVisible.value = false;
};

// 显示账号信息模态框
const showAccountInfo = (account) => {
  generatedAccount.value = account;
  showAccountModal.value = true;
};

// 关闭账号信息模态框
const closeAccountModal = () => {
  // 不允许点击背景关闭，只能通过确认按钮关闭
};

// 确认账号信息
const confirmAccount = () => {
  showAccountModal.value = false;
  isRegister.value = false;
};

// 用户信息
const router = useRouter();

// 状态管理
const showError = ref(false);
const showSucceeded = ref(false);
const showAccountModal = ref(false);
const successMessage = ref('');
const errorMessage = ref('');
const generatedAccount = ref('');

// 图片引入
const defaultAvatar = new URL('@/assets/defaultimage/mrtx.png', import.meta.url).href

// 登录表单数据
const loginForm = ref({
  username: '',
  password: '',
  image: defaultAvatar,
  hasInput: false
});

// 注册表单数据
const registerForm = ref({
  account: '',
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  avatar: '',
  agreeTerms: false
});

// auth store
const authStore = useAuthStore();

// 忘记密码处理
const showForgotPassword = () => {
  isModalVisible.value = true;
  router.push('/login/ForgotPassword');
};

// 用户名输入处理
const handleUsernameInput = async () => {
  const account = loginForm.value.username.trim();
  loginForm.value.hasInput = account !== '';

  if (account) {
    try {
      // 调用后端API获取用户信息
      const response = await getUserByAccount(account);
      if (response.code === 200 && response.data) {
        loginForm.value.image = response.data.avatar || defaultAvatar;
      } else {
        // 用户不存在，使用默认头像
        loginForm.value.image = defaultAvatar;
      }
    } catch (error) {
      // 网络错误或其他问题，使用默认头像
      loginForm.value.image = defaultAvatar;
      console.error('获取用户信息失败:', error);
    }
  } else {
    // 输入为空，使用默认头像
    loginForm.value.image = defaultAvatar;
  }
};

/**
 * 处理登录逻辑
 */
const handleLogin = async () => {
  errorMessage.value = '';
  showError.value = false;

  if (!loginForm.value.username || !loginForm.value.password) {
    errorMessage.value = '请输入账号和密码';
    showError.value = true;
    return;
  }

  try {
    // 使用login函数调用后端登录接口
    const response = await login({
      account: loginForm.value.username,
      password: loginForm.value.password
    });

    console.log('登录响应:', response);

    if (response.code === 200) {
      // 严格验证响应数据结构
      if (!response.data?.token || !response.data?.username) {
        throw new Error('响应数据不完整');
      }

      const { token, username } = response.data;

      // 验证必要字段
      if (!token || !username) {
        throw new Error('无效的用户数据');
      }

      // 使用auth store设置用户信息
      authStore.setUserInfo({
        token,
        username
      });

      // 记住用户名处理
      localStorage.setItem('rememberedUsername', username);

      successMessage.value = '';
      showSucceeded.value = true;

      setTimeout(() => {
        router.push('/');
      }, 2000);
    } else {
      errorMessage.value = response.message || '登录失败';
      showError.value = true;
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '登录失败';
    showError.value = true;
    console.error('登录失败:', error);
  }
};
// 头像上传处理
const handleAvatarUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      registerForm.value.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

// 处理注册逻辑
const handleRegister = async () => {
  errorMessage.value = '';
  showError.value = false;

  // 用户名验证
  if (!registerForm.value.username || registerForm.value.username.trim() === '') {
    errorMessage.value = '请输入真实姓名';
    showError.value = true;
    return;
  }

  // 邮箱验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(registerForm.value.email)) {
    errorMessage.value = '请输入有效的邮箱地址';
    showError.value = true;
    return;
  }

  // 手机号验证 (11位数字，1开头)
  const phoneRegex = /^1\d{10}$/;
  if (!phoneRegex.test(registerForm.value.phone)) {
    errorMessage.value = '请输入有效的手机号';
    showError.value = true;
    return;
  }

  // 密码校验
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致';
    showError.value = true;
    return;
  }

  // 密码长度验证
  if (registerForm.value.password.length < 6 || registerForm.value.password.length > 20) {
    errorMessage.value = '密码长度必须在6-20位之间';
    showError.value = true;
    return;
  }

  // 协议同意校验
  if (!registerForm.value.agreeTerms) {
    errorMessage.value = '请同意注册协议';
    showError.value = true;
    return;
  }

  try {
    // 调用后端注册接口
    const response = await register({
      username: registerForm.value.username,
      password: registerForm.value.password,
      email: registerForm.value.email,
      phone: registerForm.value.phone,
      avatar: registerForm.value.avatar
    });

    console.log('注册响应:', response);

    if (response.code === 200) {
      // 注册成功，获取生成的账号
      const account = response.data;
      showAccountInfo(account);
    } else {
      errorMessage.value = response.message || '注册失败';
      showError.value = true;
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '注册失败';
    showError.value = true;
    console.error('注册失败:', error);
  }
};

// 其他登录方式处理函数
const wechatLogin = () => {
  // 微信登录逻辑
  console.log('微信登录');
  // 这里可以添加微信登录的具体实现
};

const qqLogin = () => {
  // QQ登录逻辑
  console.log('QQ登录');
  // 这里可以添加QQ登录的具体实现
};

const phoneLogin = () => {
  // 手机号登录逻辑
  console.log('手机号登录');
  // 这里可以添加手机号登录的具体实现
  isModalVisible.value = true;
  router.push('/login/Fanginter');
};

const emailLogin = () => {
  // 邮箱登录逻辑
  console.log('邮箱登录');
  // 这里可以添加邮箱登录的具体实现
  isModalVisible.value = true;
  router.push('/login/emaillogin');
};

// 挂载逻辑
onMounted(() => {
  const rememberedUsername = localStorage.getItem('rememberedUsername');
  if (rememberedUsername) {
    loginForm.value.username = rememberedUsername;
  }
});
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
  content: '✈';
  margin-right: 10px;
  font-size: 28px;
  animation: bounce 2s ease-in-out infinite;
}

.content-wrapper {
  animation: fadeIn 0.8s ease-out;
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
  padding: 24px;
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
  display: flex;
  justify-content: center;
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
  max-width: 350px;
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

.welcome-message {
  font-size: 16px;
  color: #666;
  display: flex;
  animation: slideInUp 0.8s ease-out 0.6s both;
}

.form-subtitle {
  color: #666;
  margin-bottom: 40px;
  font-size: 14px;
  animation: slideInUp 0.8s ease-out 0.6s both;
}

.user-icon {
  font-size: 20px;
  margin: 8px 16px;
  display: flex;
  align-items: center;
  animation: bounce 2s ease-in-out infinite;
}

.user-icon svg {
  width: 32px;
  height: 32px;
  fill: #8B4513;
}

/* 头像显示样式 */
.loginpage-avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  animation: slideInUp 0.8s ease-out 0.8s both;
}

.loginpage-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #8B4513;
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.3);
  animation: scaleIn 0.8s ease-out 1s both;
}

.loginpage-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.loginpage-avatar-img:hover {
  transform: scale(1.1);
}

/* 注册头像上传区域 */
.registered-avatar-container {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  animation: slideInUp 0.8s ease-out 0.8s both;
}

.registered-avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: #f0f2f5;
  background-size: cover;
  background-position: center;
  position: relative;
  cursor: pointer;
  border: 4px solid #fff;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.registered-avatar-preview:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.registered-avatar-upload-label {
  position: absolute;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease;
  font-size: 14px;
  text-align: center;
  padding: 10px;
}

.registered-avatar-preview:hover .registered-avatar-upload-label {
  opacity: 1;
}

.registered-avatar-upload {
  display: none;
}

/* 输入框样式 */
.userlogin-input-centainer {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 30px;
  margin-bottom: 30px;
  animation: slideInUp 0.8s ease-out 1s both;
}

.userlogin-inputContainer {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.userlogin-userIcon {
  position: absolute;
  fill: #8B4513;
  width: 16px;
  height: 16px;
  top: -23px;
  left: -15px;
  opacity: 0;
  transition: .2s linear;
}

.userlogin-usernameLabel {
  position: absolute;
  top: -25px;
  left: 5px;
  color: rgb(83, 83, 83);
  font-size: 14px;
  font-weight: 500;
  font-family: Whitney, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
  overflow: hidden;
  transition: .2s linear;
  opacity: 0;
}

/* 账号密码输入框样式 */
#userlogin-passwordField,
#userlogin-usernameField {
  width: 100%;
  border: 2px solid rgb(186, 186, 186);
  background-color: transparent;
  border-radius: 15px;
  padding: 10px 15px;
  color: rgb(96, 96, 96);
  font-weight: 500;
  outline: none;
  caret-color: rgb(78, 164, 255);
  transition-duration: .3s;
  font-family: Whitney, -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica, Arial, sans-serif;
}

/* 密码 */
#userlogin-passwordField:focus~.userlogin-usernameLabel,
#userlogin-passwordField:valid~.userlogin-usernameLabel {
  transform: translateX(20px);
  opacity: 1;
}

#userlogin-passwordField:focus~.userlogin-userIcon,
#userlogin-passwordField:valid~.userlogin-userIcon {
  transform: translateX(20px);
  opacity: 1;
}

#userlogin-passwordField:focus,
#userlogin-passwordField:valid {
  background-color: #f7f7f7;
  transition-duration: .3s;
}

/* 账号 */
#userlogin-usernameField:focus~.userlogin-usernameLabel,
#userlogin-usernameField:valid~.userlogin-usernameLabel {
  transform: translateX(20px);
  opacity: 1;
}

#userlogin-usernameField:focus~.userlogin-userIcon,
#userlogin-usernameField:valid~.userlogin-userIcon {
  transform: translateX(20px);
  opacity: 1;
}

#userlogin-usernameField:focus,
#userlogin-usernameField:valid {
  background-color: #f7f7f7;
  transition-duration: .3s;
}

/* 其他登录方式样式 */
.other-login-methods {
  margin: 30px 0;
  animation: slideInUp 0.8s ease-out 1.2s both;
}

.other-login-title {
  text-align: center;
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  position: relative;
}

.other-login-title::before,
.other-login-title::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 30%;
  height: 1px;
  background-color: #ddd;
}

.other-login-title::before {
  left: 0;
}

.other-login-title::after {
  right: 0;
}

.other-login-buttons {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.other-login-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border: none;
  border-radius: 50%;
  background: none;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 10px;
}

.other-login-btn:hover {
  transform: translateY(-3px);
}

.other-login-btn span {
  font-size: 12px;
  margin-top: 5px;
}

.login-icon {
  width: 24px;
  height: 24px;
}

/* 各平台登录按钮颜色 */
.wechat-login {
  color: #07C160;
}

.qq-login {
  color: #000000;
}

.phone-login {
  color: #969797;
}

.email-login {
  color: #4880e8;
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
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

.form-footer button {
  background: none;
  border: none;
  color: #8B4513;
  text-decoration: none;
  transition: all 0.3s ease;
  position: relative;
  margin: 0 10px;
  cursor: pointer;
  font-family: inherit;
  font-size: 14px;
}

/* 注册表单样式 */
.registered-css-input-container-all {
  margin: 25px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex-wrap: wrap;
  animation: slideInUp 0.8s ease-out 1s both;
}

.registered-css-input-container {
  position: relative;
  margin-bottom: 25px;
}

.registered-css-input-field {
  font-size: 16px;
  width: 100%;
  border: none;
  border-bottom: 2px solid #e0e5ec;
  padding: 10px 0;
  background-color: transparent;
  color: #3a4a6d;
  transition: all 0.3s ease;
}

.registered-css-input-field:focus {
  outline: none;
  border-bottom-color: #8B4513;
}

.registered-css-input-field:focus+.registered-css-input-label,
.registered-css-input-field:not(:placeholder-shown)+.registered-css-input-label {
  transform: translateY(-25px);
  font-size: 14px;
  color: #8B4513;
}

.registered-css-input-label {
  position: absolute;
  top: 10px;
  left: 0;
  color: #a3a9c3;
  transition: all 0.3s ease;
  pointer-events: none;
}

.registered-css-input-highlight {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  width: 0;
  background-color: #8B4513;
  transition: all 0.4s ease;
}

.registered-css-input-field:focus~.registered-css-input-highlight {
  width: 100%;
}

.registered-css-a-container {
  display: flex;
  align-items: center;
  margin: 25px 0;
  animation: slideInUp 0.8s ease-out 1.2s both;
}

.registered-css-a-container input[type="checkbox"] {
  appearance: none;
  width: 18px;
  height: 18px;
  border: 2px solid #e0e5ec;
  border-radius: 4px;
  margin-right: 10px;
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
}

.registered-css-a-container input[type="checkbox"]:checked {
  background-color: #8B4513;
  border-color: #8B4513;
}

.registered-css-a-container input[type="checkbox"]:checked::after {
  content: '✓';
  position: absolute;
  color: white;
  font-size: 12px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.agreement-a4 {
  color: #8B4513;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s ease;
}

.agreement-a4:hover {
  color: #A0522D;
  text-decoration: underline;
}

.registered-center {
  display: flex;
  flex-direction: column;
  animation: slideInUp 0.8s ease-out 1.4s both;
}

.registered-css-input-registered {
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 100%);
  color: white;
  border: none;
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 30px;
  cursor: pointer;
  width: 100%;
  font-weight: 600;
  letter-spacing: 1px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.4);
  transition: all 0.3s ease;
  margin-bottom: 15px;
}

.registered-css-input-registered:hover {
  transform: translateY(-3px);
  box-shadow: 0 7px 20px rgba(139, 69, 19, 0.6);
}

.registered-css-input-registered:active {
  transform: translateY(1px);
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

/* 二维码登录链接样式 */
.loginpage-qr-link {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8B4513;
  background: rgba(139, 69, 19, 0.1);
  border-radius: 50%;
  transition: all 0.3s ease;
  cursor: pointer;
}

.loginpage-qr-link:hover {
  background: rgba(139, 69, 19, 0.2);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.2);
}

.loginpage-qr-icon svg {
  width: 20px;
  height: 20px;
}

/* 二维码登录模态框样式 */
.loginpage-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  color: #ffffff;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  -webkit-backdrop-filter: blur(5px);
  backdrop-filter: blur(5px);
  animation: fadeIn 0.3s ease-in-out;
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

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
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

/* 模糊背景模态框样式 */
.blur-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-in-out;
}

.blur-modal-content {
  background: white;
  border-radius: 15px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideIn 0.3s ease-in-out;
}
</style>
