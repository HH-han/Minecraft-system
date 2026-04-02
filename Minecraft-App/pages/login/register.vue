<template>
  <view class="register-container">
    <view class="login-background">
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
      <view class="block"></view>
    </view>
    <view class="register-form">
      <view class="logo">
        <text class="logo-text">Minecraft</text>
        <text class="logo-subtext">创建新账户</text>
      </view>
      <!-- 头像上传区域 -->
      <view class="avatar-upload-container">
        <view class="avatar-preview"
          :style="{ backgroundImage: formData.avatar ? `url(${formData.avatar})` : `url(${defaultAvatar})` }">
          <label for="avatar-upload" class="avatar-upload-label">
            <text v-if="!formData.avatar">点击上传头像</text>
            <text v-else>更换头像</text>
            <input id="avatar-upload" type="file" accept="image/*" @change="handleAvatarUpload" class="avatar-upload" />
          </label>
        </view>
        <text v-if="errors.avatar" class="error-message">{{ errors.avatar }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1772207429069" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="7501" width="256" height="256">
              <path d="M502.28224 338.432m-234.496 0a234.496 234.496 0 1 0 468.992 0 234.496 234.496 0 1 0-468.992 0Z"
                fill="#CAE4FF" p-id="7502"></path>
              <path
                d="M492.288 609.792c-160.256-0.512-289.792-130.048-289.792-289.792 0-160.768 129.024-291.84 289.792-294.4 160.256 0.512 289.792 130.048 289.792 289.792 0.512 161.28-129.024 291.84-289.792 294.4z m0-500.224c-113.664 0-205.824 92.16-205.824 205.824 0 113.664 92.16 205.824 205.824 205.824s205.824-92.16 205.824-205.824c0-113.664-92.16-205.824-205.824-205.824z"
                fill="#0972E7" p-id="7503"></path>
              <path
                d="M109.824 1013.248c-23.04 0-41.984-18.944-41.984-41.984v-16.896c1.024-236.544 192.512-427.52 428.544-428.544 23.04 0 41.984 18.944 41.984 41.984 0 23.04-18.944 41.984-41.984 41.984-189.952 1.024-343.552 154.624-344.576 344.576v12.8c0.512 24.064-17.92 44.032-41.984 46.08z"
                fill="#0972E7" p-id="7504"></path>
              <path
                d="M874.752 1009.152c-22.016 1.536-40.448-15.36-41.984-37.376v-17.408c-1.024-189.952-154.624-343.552-344.576-344.576-23.04 0-41.984-18.944-41.984-41.984 0-23.04 18.944-41.984 41.984-41.984 236.544 1.024 427.52 192.512 428.544 428.544v12.8c1.536 22.016-15.36 40.448-37.376 41.984h-4.608z"
                fill="#0972E7" p-id="7505"></path>
            </svg>
          </text>
          <input v-model="formData.username" placeholder="请输入用户名" class="form-input"
            :class="{ 'error': errors.username }" />
        </view>
        <text v-if="errors.username" class="error-message">{{ errors.username }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1775143734296" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="12595" width="256" height="256">
              <path
                d="M511.2832 509.696m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z"
                fill="#FF579C" p-id="12596"></path>
              <path
                d="M286.3104 415.5904c-85.0432 0-160.4096 41.3696-207.104 105.0624 4.5568 182.7328 122.368 337.3056 285.952 396.032 103.2192-33.28 177.92-130.048 177.92-244.3776-0.0512-141.7728-114.9952-256.7168-256.768-256.7168z"
                fill="#FF80BA" p-id="12597"></path>
              <path
                d="M408.7808 422.8096V358.7584c0-27.9552 22.7328-50.6368 50.6368-50.6368h103.68c27.9552 0 50.6368 22.7328 50.6368 50.6368v64.0512h61.44V358.7584c0-61.7984-50.2784-112.0768-112.0768-112.0768H459.4176c-61.7984 0-112.0768 50.2784-112.0768 112.0768v64.0512h61.44z"
                fill="#FFC7E3" p-id="12598"></path>
              <path
                d="M668.8768 395.2128H353.6384c-48.9472 0-88.6272 39.68-88.6272 88.6272v186.0608c0 48.9472 39.68 88.6272 88.6272 88.6272h315.2384c48.9472 0 88.6272-39.68 88.6272-88.6272V483.84c0-48.9472-39.68-88.6272-88.6272-88.6272z m-130.1504 188.2112v64.512a27.4432 27.4432 0 0 1-54.8864 0v-64.512a55.31648 55.31648 0 0 1-27.9552-48.0768c0-30.6176 24.7808-55.3984 55.3984-55.3984s55.3984 24.7808 55.3984 55.3984c0 20.6336-11.264 38.5536-27.9552 48.0768z"
                fill="#FFFFFF" p-id="12599"></path>
            </svg>
          </text>
          <input v-model="formData.password" type="password" placeholder="请输入密码" class="form-input"
            :class="{ 'error': errors.password }" />
        </view>
        <text v-if="errors.password" class="error-message">{{ errors.password }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1775143734296" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="12595" width="256" height="256">
              <path
                d="M511.2832 509.696m-432.3328 0a432.3328 432.3328 0 1 0 864.6656 0 432.3328 432.3328 0 1 0-864.6656 0Z"
                fill="#FF579C" p-id="12596"></path>
              <path
                d="M286.3104 415.5904c-85.0432 0-160.4096 41.3696-207.104 105.0624 4.5568 182.7328 122.368 337.3056 285.952 396.032 103.2192-33.28 177.92-130.048 177.92-244.3776-0.0512-141.7728-114.9952-256.7168-256.768-256.7168z"
                fill="#FF80BA" p-id="12597"></path>
              <path
                d="M408.7808 422.8096V358.7584c0-27.9552 22.7328-50.6368 50.6368-50.6368h103.68c27.9552 0 50.6368 22.7328 50.6368 50.6368v64.0512h61.44V358.7584c0-61.7984-50.2784-112.0768-112.0768-112.0768H459.4176c-61.7984 0-112.0768 50.2784-112.0768 112.0768v64.0512h61.44z"
                fill="#FFC7E3" p-id="12598"></path>
              <path
                d="M668.8768 395.2128H353.6384c-48.9472 0-88.6272 39.68-88.6272 88.6272v186.0608c0 48.9472 39.68 88.6272 88.6272 88.6272h315.2384c48.9472 0 88.6272-39.68 88.6272-88.6272V483.84c0-48.9472-39.68-88.6272-88.6272-88.6272z m-130.1504 188.2112v64.512a27.4432 27.4432 0 0 1-54.8864 0v-64.512a55.31648 55.31648 0 0 1-27.9552-48.0768c0-30.6176 24.7808-55.3984 55.3984-55.3984s55.3984 24.7808 55.3984 55.3984c0 20.6336-11.264 38.5536-27.9552 48.0768z"
                fill="#FFFFFF" p-id="12599"></path>
            </svg>
          </text>
          <input v-model="formData.confirmPassword" type="password" placeholder="请确认密码" class="form-input"
            :class="{ 'error': errors.confirmPassword }" />
        </view>
        <text v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1775143701530" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="10618" width="256" height="256">
              <path
                d="M407 170h358.6c64.6 0 117.4 52.8 117.4 117.4v362.7c0 64.6-52.8 117.4-117.4 117.4H407c-64.6 0-117.4-52.8-117.4-117.4V287.4c0-64.6 52.9-117.4 117.4-117.4z"
                fill="#22B229" p-id="10619"></path>
              <path
                d="M363.9 170h288.9c64.6 0 117.4 52.8 117.4 117.4v362.7c0 64.6-52.8 117.4-117.4 117.4H363.9c-64.6 0-117.4-52.8-117.4-117.4V287.4c0-64.6 52.9-117.4 117.4-117.4z"
                fill="#42DA56" p-id="10620"></path>
              <path
                d="M304.3 311h-0.1c-8.3 0-15-6.7-15-15s6.7-15 15-15h0.1c8.3 0 15 6.7 15 15s-6.7 15-15 15zM327 270c-2.8 0-5.6-0.8-8.1-2.4-7-4.5-9-13.8-4.5-20.7 12.8-19.9 33.8-31.7 34.7-32.2 7.2-4 16.4-1.4 20.4 5.9 4 7.2 1.4 16.3-5.8 20.4-0.3 0.2-15.4 8.8-24 22.1-2.9 4.5-7.7 6.9-12.7 6.9z"
                fill="#FFFFFF" p-id="10621"></path>
              <path
                d="M708.7 782.5h-418c-73 0-132.4-59.4-132.4-132.4V557c0-8.3 6.7-15 15-15s15 6.7 15 15v93.1c0 56.5 45.9 102.4 102.4 102.4h417.9c56.5 0 102.4-45.9 102.4-102.4 0-8.3 6.7-15 15-15s15 6.7 15 15c0.1 73-59.3 132.4-132.3 132.4zM826.1 595c-8.3 0-15-6.7-15-15V397c0-8.3 6.7-15 15-15s15 6.7 15 15v183c0 8.3-6.8 15-15 15zM826.1 372c-8.3 0-15-6.7-15-15v-18c0-8.3 6.7-15 15-15s15 6.7 15 15v18c0 8.3-6.8 15-15 15zM173.3 512c-8.3 0-15-6.7-15-15V287.4c0-73 59.4-132.4 132.4-132.4h417.9c73 0 132.4 59.4 132.4 132.4V301c0 8.3-6.7 15-15 15s-15-6.7-15-15v-13.6c0-56.5-45.9-102.4-102.4-102.4H290.7c-56.5 0-102.4 45.9-102.4 102.4V497c0 8.3-6.7 15-15 15z"
                fill="#1A1A1A" p-id="10622"></path>
              <path
                d="M562.3 595c-8.3 0-15-6.7-15-15v-70c0-8.3 6.7-15 15-15s15 6.7 15 15v70c0 8.3-6.7 15-15 15zM377.3 557c-8.3 0-15-6.7-15-15v-16c0-8.3 6.7-15 15-15s15 6.7 15 15v16c0 8.3-6.7 15-15 15zM377.3 502c-8.3 0-15-6.7-15-15v-88c0-14.3 5.6-27.6 15.7-37.7 10-9.9 23.2-15.3 37.3-15.3h133c8.3 0 15 6.7 15 15s-6.7 15-15 15h-133c-6.1 0-11.8 2.4-16.2 6.7-4.4 4.4-6.8 10.2-6.8 16.3v88c0 8.3-6.7 15-15 15z"
                fill="#FFFFFF" p-id="10623"></path>
              <path
                d="M593.3 596H563c-8.3 0-15-6.7-15-15s6.7-15 15-15h30.4c12.7 0 23-10.3 23-23V399c0-12.7-10.3-23-23-23-8.3 0-15-6.7-15-15s6.7-15 15-15c29.2 0 53 23.8 53 53v144c-0.1 29.2-23.8 53-53.1 53z"
                fill="#FFFFFF" p-id="10624"></path>
              <path
                d="M538.8 553.9H472c-21.2 0-38.5-17.3-38.5-38.5v-66.9c0-21.2 17.3-38.5 38.5-38.5h66.9c21.2 0 38.5 17.3 38.5 38.5v66.9c-0.1 21.2-17.3 38.5-38.6 38.5zM472 440c-4.6 0-8.5 3.9-8.5 8.5v66.9c0 4.6 3.9 8.5 8.5 8.5h66.9c4.6 0 8.5-3.9 8.5-8.5v-66.9c0-4.6-3.9-8.5-8.5-8.5H472z"
                fill="#FFFFFF" p-id="10625"></path>
              <path
                d="M995 869h-20.4c-8.3 0-15-6.7-15-15s6.7-15 15-15H995c8.3 0 15 6.7 15 15s-6.7 15-15 15zM925 869H491.8c-8.3 0-15-6.7-15-15s6.7-15 15-15H925c8.3 0 15 6.7 15 15s-6.7 15-15 15z"
                fill="#1A1A1A" p-id="10626"></path>
              <path
                d="M523.4 869H261c-8.3 0-15-6.7-15-15s6.7-15 15-15h262.4c8.3 0 15 6.7 15 15s-6.7 15-15 15zM162 869H44.3c-8.3 0-15-6.7-15-15s6.7-15 15-15H162c8.3 0 15 6.7 15 15s-6.8 15-15 15z"
                fill="#1A1A1A" p-id="10627"></path>
              <path
                d="M40.8 298.7c-0.8 0-1.5-0.7-1.5-1.5v-38.7c0-0.8 0.7-1.5 1.5-1.5s1.5 0.7 1.5 1.5v38.7c0 0.8-0.7 1.5-1.5 1.5z"
                fill="#F74E00" p-id="10628"></path>
              <path
                d="M28.7 293.3c-0.4 0-0.7-0.1-1-0.4-0.6-0.6-0.7-1.5-0.1-2.1l25.1-27.4c0.6-0.6 1.5-0.7 2.1-0.1 0.6 0.6 0.7 1.5 0.1 2.1l-25.1 27.4c-0.3 0.3-0.7 0.5-1.1 0.5z"
                fill="#F74E00" p-id="10629"></path>
              <path
                d="M53.9 293c-0.4 0-0.8-0.2-1.1-0.5l-25.1-27.4c-0.6-0.6-0.5-1.6 0.1-2.1 0.6-0.6 1.6-0.5 2.1 0.1L55 290.5c0.6 0.6 0.5 1.6-0.1 2.1-0.2 0.3-0.6 0.4-1 0.4z"
                fill="#F74E00" p-id="10630"></path>
              <path
                d="M58.6 279.3H23c-0.8 0-1.5-0.7-1.5-1.5s0.7-1.5 1.5-1.5h35.6c0.8 0 1.5 0.7 1.5 1.5s-0.7 1.5-1.5 1.5z"
                fill="#F74E00" p-id="10631"></path>
              <path
                d="M107.1 460.5h-7.4c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.2 2.5-2.5 2.5zM83.3 460.5h-7.4c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.1 2.5-2.5 2.5zM85.6 454.4c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.3-1.1 0.6-1.7 0.6zM103.6 472.7c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.4-1.1 0.6-1.7 0.6zM98.3 453.8c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2 1 0.9 1.1 2.5 0.2 3.5l-5.3 5.7c-0.4 0.6-1.1 0.9-1.8 0.9zM91.7 453.6c-1.4 0-2.5-1.1-2.5-2.5V443c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.4-1.1 2.5-2.5 2.5zM91.7 476.7c-1.4 0-2.5-1.1-2.5-2.5v-8.1c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.4-1.1 2.5-2.5 2.5zM80.5 472.6c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2 1 0.9 1.1 2.5 0.2 3.5l-5.3 5.7c-0.5 0.6-1.1 0.9-1.8 0.9z"
                fill="#42DA56" p-id="10632"></path>
              <path
                d="M118.7 259h-4.2c-3.1 0-5.7-2.5-5.7-5.7v-1c0-3.1 2.5-5.7 5.7-5.7h4.2c3.1 0 5.7 2.5 5.7 5.7v1c-0.1 3.2-2.6 5.7-5.7 5.7z"
                fill="#3268CF" p-id="10633"></path>
              <path
                d="M77.1 581c-2.5 0-4.5-2-4.5-4.5v-0.8c0-2.5 2-4.5 4.5-4.5s4.5 2 4.5 4.5v0.8c-0.1 2.5-2.1 4.5-4.5 4.5z"
                fill="#F74E00" p-id="10634"></path>
              <path
                d="M43.6 389c-7.3 0-13.2-6-13.2-13.2v-2.4c0-7.3 6-13.2 13.2-13.2 7.3 0 13.2 6 13.2 13.2v2.4c0 7.2-5.9 13.2-13.2 13.2z"
                fill="#42DA56" p-id="10635"></path>
              <path
                d="M993.7 434.8h-6.2c-5.4 0-9.8-4.4-9.8-9.8v-1.3c0-5.4 4.4-9.8 9.8-9.8h6.2c5.4 0 9.8 4.4 9.8 9.8v1.3c0 5.4-4.4 9.8-9.8 9.8z m-6.2-15.9c-2.6 0-4.8 2.2-4.8 4.8v1.3c0 2.6 2.2 4.8 4.8 4.8h6.2c2.6 0 4.8-2.2 4.8-4.8v-1.3c0-2.6-2.2-4.8-4.8-4.8h-6.2z"
                fill="#42DA56" p-id="10636"></path>
              <path
                d="M944 364.9c-0.8 0-1.5-0.7-1.5-1.5V340c0-0.8 0.7-1.5 1.5-1.5s1.5 0.7 1.5 1.5v23.4c0 0.8-0.6 1.5-1.5 1.5z"
                fill="#0000FF" p-id="10637"></path>
              <path
                d="M936.8 361.7c-0.4 0-0.7-0.1-1-0.4-0.6-0.6-0.7-1.5-0.1-2.1l15.2-16.6c0.6-0.6 1.5-0.7 2.1-0.1 0.6 0.6 0.7 1.5 0.1 2.1l-15.2 16.6c-0.3 0.3-0.7 0.5-1.1 0.5z"
                fill="#0000FF" p-id="10638"></path>
              <path
                d="M952 361.5c-0.4 0-0.8-0.2-1.1-0.5l-15.2-16.6c-0.6-0.6-0.5-1.6 0.1-2.1 0.6-0.6 1.6-0.5 2.1 0.1l15.2 16.6c0.6 0.6 0.5 1.6-0.1 2.1-0.3 0.3-0.6 0.4-1 0.4z"
                fill="#0000FF" p-id="10639"></path>
              <path
                d="M954.8 353.2h-21.5c-0.8 0-1.5-0.7-1.5-1.5s0.7-1.5 1.5-1.5h21.5c0.8 0 1.5 0.7 1.5 1.5s-0.7 1.5-1.5 1.5z"
                fill="#0000FF" p-id="10640"></path>
              <path
                d="M996.2 521.2h-7.4c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.1 2.5-2.5 2.5zM972.4 521.2H965c-1.4 0-2.5-1.1-2.5-2.5s1.1-2.5 2.5-2.5h7.4c1.4 0 2.5 1.1 2.5 2.5s-1.1 2.5-2.5 2.5zM974.7 515c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.4-1.1 0.6-1.7 0.6zM992.8 533.4c-0.7 0-1.3-0.3-1.8-0.8l-5.3-5.7c-0.9-1-0.9-2.6 0.2-3.5 1-0.9 2.6-0.9 3.5 0.2l5.3 5.7c0.9 1 0.9 2.6-0.2 3.5-0.5 0.4-1.1 0.6-1.7 0.6zM987.5 514.5c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2 1 0.9 1.1 2.5 0.2 3.5l-5.3 5.7c-0.4 0.6-1.1 0.9-1.8 0.9zM980.9 514.2c-1.4 0-2.5-1.1-2.5-2.5v-8.1c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.4-1.2 2.5-2.5 2.5zM980.9 537.4c-1.4 0-2.5-1.1-2.5-2.5v-8.1c0-1.4 1.1-2.5 2.5-2.5s2.5 1.1 2.5 2.5v8.1c0 1.3-1.2 2.5-2.5 2.5zM969.7 533.3c-0.6 0-1.2-0.2-1.7-0.7-1-0.9-1.1-2.5-0.2-3.5l5.3-5.7c0.9-1 2.5-1.1 3.5-0.2s1.1 2.5 0.2 3.5l-5.3 5.7c-0.5 0.6-1.2 0.9-1.8 0.9z"
                fill="#F74E00" p-id="10641"></path>
              <path
                d="M932.7 603h-4.3c-1.2 0-2.1-1-2.1-2.1v-0.4c0-1.2 1-2.1 2.1-2.1h4.3c1.2 0 2.1 1 2.1 2.1v0.4c0 1.1-1 2.1-2.1 2.1z"
                fill="#3268CF" p-id="10642"></path>
              <path
                d="M933.9 487.7h-9.3c-4 0-7.2-3.2-7.2-7.2v-0.8c0-4 3.2-7.2 7.2-7.2h9.3c4 0 7.2 3.2 7.2 7.2v0.8c0 4-3.3 7.2-7.2 7.2z m-9.4-10.2c-1.2 0-2.2 1-2.2 2.2v0.8c0 1.2 1 2.2 2.2 2.2h9.3c1.2 0 2.2-1 2.2-2.2v-0.8c0-1.2-1-2.2-2.2-2.2h-9.3z"
                fill="#F74E00" p-id="10643"></path>
              <path d="M922.3 275.3a5.7 6.3 0 1 0 11.4 0 5.7 6.3 0 1 0-11.4 0Z" fill="#3268CF" p-id="10644"></path>
            </svg>
          </text>
          <input v-model="formData.email" placeholder="请输入邮箱" class="form-input" :class="{ 'error': errors.email }" />
        </view>
        <text v-if="errors.email" class="error-message">{{ errors.email }}</text>
      </view>
      <view class="form-item">
        <view class="input-wrapper">
          <text class="input-icon">
            <svg t="1775143669773" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg"
              p-id="9464" width="256" height="256">
              <path d="M191.6 83.4c-47.4 22.4-85.8 60.8-108.2 108.2L191.6 83.4z" fill="#4ADF9E" p-id="9465"></path>
              <path d="M228.8 69.7C151.6 90.5 90.5 151.6 69.7 228.8L228.8 69.7z" fill="#49DF9E" p-id="9466"></path>
              <path d="M258.2 63.9c-100.8 13-181.3 93.5-194.4 194.4L258.2 63.9z" fill="#48DF9F" p-id="9467"></path>
              <path d="M283.6 62C162.5 63.9 63.9 162.5 62 283.6L283.6 62z" fill="#47E09F" p-id="9468"></path>
              <path d="M307.2 62H287C163.3 62 62 163.3 62 287v20.2L307.2 62z" fill="#46E09F" p-id="9469"></path>
              <path d="M330.7 62H287C163.3 62 62 163.3 62 287v43.7L330.7 62z" fill="#45E09F" p-id="9470"></path>
              <path d="M354.2 62H287C163.3 62 62 163.3 62 287v67.2L354.2 62z" fill="#44E0A0" p-id="9471"></path>
              <path d="M377.8 62H287C163.3 62 62 163.3 62 287v90.8L377.8 62z" fill="#43E0A0" p-id="9472"></path>
              <path d="M401.3 62H287C163.3 62 62 163.3 62 287v114.3L401.3 62z" fill="#42E0A0" p-id="9473"></path>
              <path d="M424.9 62H287C163.3 62 62 163.3 62 287v137.9L424.9 62z" fill="#41E0A0" p-id="9474"></path>
              <path d="M448.4 62H287c-39.8 0-77.3 10.5-109.8 28.8l-86.4 86.4C72.5 209.7 62 247.2 62 287v161.4L448.4 62z"
                fill="#40E1A1" p-id="9475"></path>
              <path d="M472 62H287c-23.7 0-46.5 3.7-68 10.6L72.6 219C65.7 240.5 62 263.3 62 287v185L472 62z"
                fill="#3FE1A1" p-id="9476"></path>
              <path d="M495.5 62H287c-12.6 0-24.9 1.1-37 3.1l-185 185c-2 12-3.1 24.4-3.1 37v208.5L495.5 62z"
                fill="#3EE1A1" p-id="9477"></path>
              <path d="M519.1 62H287c-3.6 0-7.1 0.1-10.6 0.3L62.3 276.4c-0.2 3.5-0.3 7-0.3 10.6v232.1L519.1 62z"
                fill="#3DE1A1" p-id="9478"></path>
              <path d="M62 542.6L542.6 62H300.2L62 300.2z" fill="#3CE1A2" p-id="9479"></path>
              <path d="M62 566.2L566.2 62H323.8L62 323.8z" fill="#3BE1A2" p-id="9480"></path>
              <path d="M62 589.7L589.7 62H347.3L62 347.3z" fill="#3AE1A2" p-id="9481"></path>
              <path d="M62 613.3L613.3 62H370.8L62 370.8z" fill="#39E2A2" p-id="9482"></path>
              <path d="M62 636.8L636.8 62H394.4L62 394.4z" fill="#38E2A3" p-id="9483"></path>
              <path d="M62 417.9v242.5L660.4 62H417.9z" fill="#37E2A3" p-id="9484"></path>
              <path d="M62 441.5v242.4L683.9 62H441.5z" fill="#36E2A3" p-id="9485"></path>
              <path d="M62 465v242.5L707.5 62H465z" fill="#35E2A3" p-id="9486"></path>
              <path d="M62 488.6V731L731 62H488.6z" fill="#34E2A3" p-id="9487"></path>
              <path d="M737 62H512.1L62 512.1V737c0 5.7 0.2 11.3 0.6 16.9L753.9 62.6c-5.6-0.4-11.2-0.6-16.9-0.6z"
                fill="#33E2A4" p-id="9488"></path>
              <path d="M737 62H535.7L62 535.7V737c0 12.9 1.1 25.5 3.2 37.9L774.9 65.2c-12.4-2.1-25-3.2-37.9-3.2z"
                fill="#32E3A4" p-id="9489"></path>
              <path d="M737 62H559.2L62 559.2V737c0 19.8 2.6 38.9 7.4 57.2L794.2 69.4C775.9 64.6 756.8 62 737 62z"
                fill="#31E3A4" p-id="9490"></path>
              <path d="M737 62H582.8L62 582.8V737c0 26.3 4.6 51.6 13 75.2L812.2 75c-23.6-8.4-48.9-13-75.2-13z"
                fill="#30E3A4" p-id="9491"></path>
              <path d="M737 62H606.3L62 606.3V737c0 32.7 7.1 63.8 19.8 92L829 81.8C800.8 69.1 769.7 62 737 62z"
                fill="#2FE3A5" p-id="9492"></path>
              <path d="M844.7 89.6C812.6 72 775.9 62 737 62H629.9L62 629.9V737c0 38.9 10 75.6 27.6 107.7L844.7 89.6z"
                fill="#2EE3A5" p-id="9493"></path>
              <path d="M859.4 98.5C824.1 75.4 782 62 737 62h-83.6L62 653.4V737c0 45 13.4 87.1 36.5 122.4L859.4 98.5z"
                fill="#2DE3A5" p-id="9494"></path>
              <path d="M873.2 108.2C835.3 79.3 788.1 62 737 62h-60L62 677v60c0 51.1 17.3 98.3 46.2 136.2l765-765z"
                fill="#2CE3A5" p-id="9495"></path>
              <path d="M886.1 118.8C846.3 83.5 794 62 737 62h-36.5L62 700.5V737c0 57 21.5 109.3 56.8 149.1l767.3-767.3z"
                fill="#2BE4A6" p-id="9496"></path>
              <path d="M737 62h-12.9L62 724.1V737c0 63 26.3 120.2 68.4 161.1l767.8-767.8C857.2 88.3 800 62 737 62z"
                fill="#2AE4A6" p-id="9497"></path>
              <path d="M62.2 747.4c3 64.8 33.7 122.7 80.5 161.9l766.6-766.6C870 95.9 812.2 65.2 747.4 62.2L62.2 747.4z"
                fill="#29E4A6" p-id="9498"></path>
              <path d="M64.3 768.9c8.9 61.8 43.1 115.7 91.7 150.7L919.6 156c-35-48.6-88.9-82.8-150.7-91.7L64.3 768.9z"
                fill="#28E4A6" p-id="9499"></path>
              <path d="M68 788.7c14 59.1 51.5 109.4 102.1 140.4l758.9-759c-30.9-50.5-81.2-88-140.4-102.1L68 788.7z"
                fill="#27E4A7" p-id="9500"></path>
              <path d="M73.2 807c18.7 56.7 59.4 103.6 111.9 130.5l752.3-752.3c-26.8-52.5-73.7-93.3-130.4-112L73.2 807z"
                fill="#26E4A7" p-id="9501"></path>
              <path
                d="M79.7 824.1c23.1 54.4 67 98.1 121.6 120.8l743.6-743.6c-22.7-54.6-66.3-98.6-120.8-121.6L79.7 824.1z"
                fill="#25E5A7" p-id="9502"></path>
              <path
                d="M87.2 840.1c27.2 52.3 74.4 92.8 131.3 111.1l732.8-732.8C933 161.5 892.5 114.3 840.2 87.1l-753 753z"
                fill="#25E5A7" p-id="9503"></path>
              <path
                d="M95.8 855.1c31.3 50.3 81.9 87.6 141.2 101.2L956.3 237c-13.6-59.4-50.8-110-101.2-141.2L95.8 855.1z"
                fill="#24E5A7" p-id="9504"></path>
              <path d="M256.9 960L960 256.9c-8.4-62-42.3-116.3-90.8-151.6l-764 764c35.4 48.3 89.6 82.3 151.7 90.7z"
                fill="#23E5A8" p-id="9505"></path>
              <path d="M278.6 961.8l683.3-683.3c-2.4-65.1-32.8-123.3-79.5-162.9L115.6 882.4c39.7 46.6 97.9 77 163 79.4z"
                fill="#22E5A8" p-id="9506"></path>
              <path
                d="M962 287c0-62.5-25.8-119.3-67.3-160.1L126.9 894.7C167.7 936.2 224.5 962 287 962h14.9L962 301.9V287z"
                fill="#21E5A8" p-id="9507"></path>
              <path d="M962 287c0-56.5-21.1-108.4-55.9-148L139 906.1c39.6 34.8 91.5 55.9 148 55.9h38.5L962 325.5V287z"
                fill="#20E5A8" p-id="9508"></path>
              <path d="M962 287c0-50.6-16.9-97.4-45.3-135L152 916.7c37.7 28.4 84.5 45.3 135 45.3h62l613-613v-62z"
                fill="#1FE6A9" p-id="9509"></path>
              <path
                d="M962 287c0-44.5-13.1-86.1-35.7-121.2L165.8 926.3c35 22.5 76.6 35.7 121.2 35.7h85.6L962 372.6V287z"
                fill="#1EE6A9" p-id="9510"></path>
              <path
                d="M962 287c0-38.4-9.8-74.6-26.9-106.4L180.6 935.1c31.7 17.2 68 26.9 106.4 26.9h109.1L962 396.1V287z"
                fill="#1DE6A9" p-id="9511"></path>
              <path
                d="M287 962h132.7L962 419.7V287c0-32.2-6.9-62.8-19.2-90.6L196.4 942.8c27.8 12.3 58.4 19.2 90.6 19.2z"
                fill="#1CE6A9" p-id="9512"></path>
              <path d="M287 962h156.2L962 443.2V287c0-25.8-4.4-50.6-12.5-73.7L213.3 949.5c23.1 8.1 47.9 12.5 73.7 12.5z"
                fill="#1BE6AA" p-id="9513"></path>
              <path d="M287 962h179.8L962 466.8V287c0-19.2-2.4-37.8-7-55.6L231.4 955c17.8 4.6 36.4 7 55.6 7z"
                fill="#1AE6AA" p-id="9514"></path>
              <path d="M287 962h203.3L962 490.3V287c0-12.3-1-24.4-2.9-36.2L250.8 959.1c11.8 1.9 23.9 2.9 36.2 2.9z"
                fill="#19E6AA" p-id="9515"></path>
              <path d="M287 962h226.9L962 513.9V287c0-5.1-0.2-10.1-0.5-15L272 961.5c4.9 0.3 9.9 0.5 15 0.5z"
                fill="#18E7AA" p-id="9516"></path>
              <path d="M537.4 962L962 537.4V295L295 962z" fill="#17E7AB" p-id="9517"></path>
              <path d="M561 962l401-401V318.5L318.5 962z" fill="#16E7AB" p-id="9518"></path>
              <path d="M584.5 962L962 584.5V342.1L342.1 962z" fill="#15E7AB" p-id="9519"></path>
              <path d="M608.1 962L962 608.1V365.6L365.6 962z" fill="#14E7AB" p-id="9520"></path>
              <path d="M631.6 962L962 631.6V389.2L389.2 962z" fill="#13E7AB" p-id="9521"></path>
              <path d="M655.2 962L962 655.2V412.7L412.7 962z" fill="#12E7AC" p-id="9522"></path>
              <path d="M678.7 962L962 678.7V436.3L436.3 962z" fill="#11E8AC" p-id="9523"></path>
              <path d="M702.2 962L962 702.2V459.8L459.8 962z" fill="#10E8AC" p-id="9524"></path>
              <path d="M725.8 962L962 725.8V483.4L483.4 962z" fill="#0FE8AC" p-id="9525"></path>
              <path d="M962 737V506.9L506.9 962H737c4.3 0 8.5-0.1 12.7-0.4l211.9-211.9c0.3-4.2 0.4-8.4 0.4-12.7z"
                fill="#0EE8AD" p-id="9526"></path>
              <path d="M962 737V530.5L530.5 962H737c13.4 0 26.6-1.2 39.4-3.5l182.2-182.2c2.2-12.7 3.4-25.9 3.4-39.3z"
                fill="#0DE8AD" p-id="9527"></path>
              <path d="M962 737V554L554 962h183c24.8 0 48.6-4.1 71-11.6L950.4 808c7.5-22.4 11.6-46.2 11.6-71z"
                fill="#0CE8AD" p-id="9528"></path>
              <path d="M962 737V577.6L577.6 962H737c41.8 0 80.9-11.5 114.6-31.6l78.9-78.9c20-33.6 31.5-72.7 31.5-114.5z"
                fill="#0BE8AD" p-id="9529"></path>
              <path d="M962 601.1L601.1 962H737c123.8 0 225-101.3 225-225V601.1z" fill="#0AE9AE" p-id="9530"></path>
              <path d="M962 624.7L624.7 962H737c123.8 0 225-101.3 225-225V624.7z" fill="#09E9AE" p-id="9531"></path>
              <path d="M962 648.2L648.2 962H737c123.8 0 225-101.3 225-225v-88.8z" fill="#08E9AE" p-id="9532"></path>
              <path d="M962 671.8L671.8 962H737c123.8 0 225-101.3 225-225v-65.2z" fill="#07E9AE" p-id="9533"></path>
              <path d="M962 695.3L695.3 962H737c123.8 0 225-101.3 225-225v-41.7z" fill="#06E9AF" p-id="9534"></path>
              <path d="M962 718.8L718.8 962H737c123.8 0 225-101.3 225-225v-18.2z" fill="#05E9AF" p-id="9535"></path>
              <path d="M961.9 742.5L742.5 961.9c119.4-2.9 216.5-100 219.4-219.4z" fill="#04E9AF" p-id="9536"></path>
              <path d="M959.8 768.1L768.1 959.8c99-13.8 177.9-92.7 191.7-191.7z" fill="#03EAAF" p-id="9537"></path>
              <path d="M953.6 797.9L797.9 953.6c75-21.3 134.4-80.7 155.7-155.7z" fill="#02EAB0" p-id="9538"></path>
              <path d="M938.7 836.3L836.3 938.7c44.3-21.9 80.5-58.1 102.4-102.4z" fill="#01EAB0" p-id="9539"></path>
              <path
                d="M386 269h252c19.9 0 36 16.1 36 36v414c0 19.9-16.1 36-36 36H386c-19.9 0-36-16.1-36-36V305c0-19.9 16.1-36 36-36z"
                fill="#F6FDFA" p-id="9540"></path>
              <path
                d="M602 683H422c-19.9 0-36-16.1-36-36V350c0-19.9 16.1-36 36-36h180c19.9 0 36 16.1 36 36v297c0 19.9-16.1 36-36 36z"
                fill="#CAF6E1" p-id="9541"></path>
              <path d="M512 719m-18 0a18 18 0 1 0 36 0 18 18 0 1 0-36 0Z" fill="#CAF6E1" p-id="9542"></path>
            </svg>
          </text>
          <input v-model="formData.phone" placeholder="请输入手机号" class="form-input" :class="{ 'error': errors.phone }" />
        </view>
        <text v-if="errors.phone" class="error-message">{{ errors.phone }}</text>
      </view>
      <view class="form-item">
        <button :loading="loading" @click="submitForm" class="register-button">
          <text class="button-text">注册</text>
        </button>
      </view>
      <view class="login-link">
        <text>已有账号？</text>
        <text class="link" @click="goLogin">立即登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { register } from '../../api/auth';

const loading = ref(false);

// 头像相关
const defaultAvatar = 'https://img.icons8.com/ios-filled/50/000000/user.png';

const formData = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  avatar: ''
});

const errors = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  avatar: ''
});

// 处理头像上传
const handleAvatarUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      formData.avatar = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const validateForm = () => {
  let isValid = true;

  // 重置错误信息
  errors.username = '';
  errors.password = '';
  errors.confirmPassword = '';
  errors.email = '';
  errors.phone = '';
  errors.avatar = '';

  // 验证用户名
  if (!formData.username.trim()) {
    errors.username = '请输入用户名';
    isValid = false;
  } else if (formData.username.length < 3 || formData.username.length > 20) {
    errors.username = '用户名长度必须在3-20位之间';
    isValid = false;
  }

  // 验证密码
  if (!formData.password.trim()) {
    errors.password = '请输入密码';
    isValid = false;
  } else if (formData.password.length < 6 || formData.password.length > 20) {
    errors.password = '密码长度必须在6-20位之间';
    isValid = false;
  }

  // 验证确认密码
  if (!formData.confirmPassword.trim()) {
    errors.confirmPassword = '请确认密码';
    isValid = false;
  } else if (formData.confirmPassword !== formData.password) {
    errors.confirmPassword = '两次输入密码不一致';
    isValid = false;
  }

  // 验证邮箱
  if (!formData.email.trim()) {
    errors.email = '请输入邮箱';
    isValid = false;
  } else {
    // 简单的邮箱格式验证
    const emailRegex = /^[^@]+@[^@]+\.[^@]+$/;
    if (!emailRegex.test(formData.email)) {
      errors.email = '请输入正确的邮箱格式';
      isValid = false;
    }
  }

  // 验证手机号
  if (!formData.phone.trim()) {
    errors.phone = '请输入手机号';
    isValid = false;
  }

  // 验证头像
  if (!formData.avatar) {
    errors.avatar = '请上传头像';
    isValid = false;
  }

  return isValid;
};

const submitForm = async () => {
  if (validateForm()) {
    loading.value = true;
    try {
      // 准备注册数据，移除confirmPassword字段
      const registerData = {
        username: formData.username,
        password: formData.password,
        email: formData.email,
        phone: formData.phone,
        avatar: formData.avatar
      };

      const response = await register(registerData);
      if (response.code === 200) {
        uni.showToast({
          title: '注册成功',
          icon: 'success'
        });
        uni.navigateTo({
          url: '/pages/login/login'
        });
      } else {
        uni.showToast({
          title: response.message || '注册失败',
          icon: 'none'
        });
      }
    } catch (error) {
      uni.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      });
    } finally {
      loading.value = false;
    }
  }
};

const goLogin = () => {
  uni.navigateTo({
    url: '/pages/login/login'
  });
};
</script>

<style scoped>
/* CSS 变量定义 */
:root {
  --bg-color: #f5f5f5;
  --card-color: #ffffff;
  --text-color: #333333;
  --border-color: #e0e0e0;
  --primary-color: #007aff;
}

.register-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20rpx;
  background-color: var(--bg-color);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.block {
  position: absolute;
  background-color: rgba(0, 122, 255, 0.05);
  border-radius: 50%;
  animation: float 8s infinite ease-in-out;
}

.block:nth-child(1) {
  width: 200rpx;
  height: 200rpx;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.block:nth-child(2) {
  width: 150rpx;
  height: 150rpx;
  top: 60%;
  right: 15%;
  animation-delay: 2s;
}

.block:nth-child(3) {
  width: 100rpx;
  height: 100rpx;
  top: 30%;
  right: 20%;
  animation-delay: 4s;
}

.block:nth-child(4) {
  width: 120rpx;
  height: 120rpx;
  bottom: 20%;
  left: 25%;
  animation-delay: 6s;
}

.block:nth-child(5) {
  width: 80rpx;
  height: 80rpx;
  top: 40%;
  left: 30%;
  animation-delay: 1s;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }

  50% {
    transform: translateY(-20rpx) rotate(180deg);
  }
}

.register-form {
  width: 100%;
  max-width: 300px;
  padding: 60rpx 40rpx;
  background-color: var(--card-color);
  border-radius: 20rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
  animation: slideIn 0.5s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(50rpx);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 头像上传样式 */
.avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
  animation: slideIn 0.5s ease-out 0.2s both;
}

.avatar-preview {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background-color: var(--bg-color);
  background-size: cover;
  background-position: center;
  position: relative;
  cursor: pointer;
  border: 3rpx solid var(--primary-color);
  box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.3);
  transition: all 0.3s ease;
  animation: scaleIn 0.5s ease-out 0.4s both;
}

.avatar-preview:hover {
  transform: scale(1.05);
  box-shadow: 0 6rpx 16rpx rgba(0, 122, 255, 0.4);
}

.avatar-upload-label {
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
  font-size: 24rpx;
  text-align: center;
  padding: 10rpx;
}

.avatar-preview:hover .avatar-upload-label {
  opacity: 1;
}

.avatar-upload {
  display: none;
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

.logo {
  text-align: center;
  margin-bottom: 50rpx;
}

.logo-text {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: var(--primary-color);
  margin-bottom: 10rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.logo-subtext {
  display: block;
  font-size: 24rpx;
  color: var(--text-color);
  opacity: 0.7;
}

.form-item {
  margin-bottom: 35rpx;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  flex-direction: row;
  border: 1px solid var(--border-color);
  border-radius: 12rpx;
  padding: 8rpx 20rpx;
  transition: all 0.3s ease;
  background-color: var(--bg-color);
}

.input-wrapper:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3rpx rgba(0, 122, 255, 0.1);
}

.input-wrapper.error {
  border-color: #ff4d4f;
}

.input-icon {
  padding: 0 20rpx;
  font-size: 28rpx;
}

.input-icon svg {
  width: 32rpx;
  height: 32rpx;
}

.form-input {
  flex: 1;
  font-size: 28rpx;
  background-color: transparent;
  color: var(--text-color);
  border: none;
  outline: none;
}

.form-input::placeholder {
  color: var(--text-color);
  opacity: 0.5;
}

.error-message {
  display: block;
  font-size: 20rpx;
  color: #ff4d4f;
  margin-top: 10rpx;
  animation: shake 0.3s ease;
}

@keyframes shake {

  0%,
  100% {
    transform: translateX(0);
  }

  25% {
    transform: translateX(-5rpx);
  }

  75% {
    transform: translateX(5rpx);
  }
}

.register-button {
  width: 100%;
  padding: 2rpx;
  font-size: 30rpx;
  background: linear-gradient(135deg, var(--primary-color), #0056b3);
  color: white;
  border: none;
  border-radius: 12rpx;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 12rpx rgba(0, 122, 255, 0.3);
  overflow: hidden;
  position: relative;
}

.register-button::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(to right,
      rgba(255, 255, 255, 0) 0%,
      rgba(255, 255, 255, 0.3) 50%,
      rgba(255, 255, 255, 0) 100%);
  transform: rotate(30deg);
  animation: shine 2s infinite;
}

@keyframes shine {
  0% {
    transform: translateX(-100%) rotate(30deg);
  }

  100% {
    transform: translateX(100%) rotate(30deg);
  }
}

.register-button:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 6rpx 16rpx rgba(0, 122, 255, 0.4);
}

.register-button:active {
  transform: translateY(0);
  box-shadow: 0 2rpx 8rpx rgba(0, 122, 255, 0.3);
}

.button-text {
  position: relative;
  z-index: 1;
  font-weight: bold;
}

.login-link {
  text-align: center;
  margin-top: 40rpx;
  font-size: 24rpx;
  color: var(--text-color);
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.link {
  color: var(--primary-color);
  margin-left: 10rpx;
  font-weight: 500;
  transition: color 0.3s ease;
}

.link:hover {
  color: #0056b3;
  text-decoration: underline;
}
</style>