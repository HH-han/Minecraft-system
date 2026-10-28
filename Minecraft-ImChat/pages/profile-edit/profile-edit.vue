<template>
  <view class="edit-page" :class="themeClass">
    <CustomNavBar
      :show-back="true"
      :title="$t('profileEdit.title')"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="edit-scroll" scroll-y :show-scrollbar="false">
      <text class="section-label">{{ $t('profileEdit.basics') }}</text>

      <view class="group-block">
        <view class="edit-avatar" hover-class="edit-avatar--hover" hover-stay-time="60" @click="chooseAvatar">
          <view class="edit-avatar__frame">
            <UiAvatar shape="square" :size="112" :src="form.avatar" :name="form.nickname" />
          </view>
          <view class="edit-avatar__body">
            <text class="edit-avatar__title">{{ $t('profileEdit.avatar') }}</text>
            <text class="edit-avatar__desc">{{ $t('profileEdit.avatarHint') }}</text>
          </view>
          <view class="edit-avatar__action">
            <UiIcon name="camera" :size="28" />
          </view>
        </view>

        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.nickname') }}</text>
          <input
            class="edit-input"
            v-model="form.nickname"
            :placeholder="$t('profileEdit.nicknamePh')"
            placeholder-class="edit-ph"
          />
        </view>

        <view class="edit-row" hover-class="edit-row--hover" hover-stay-time="60" @click="chooseGender">
          <text class="edit-label">{{ $t('profileEdit.gender') }}</text>
          <view class="edit-right">
            <text class="edit-value">{{ genderText }}</text>
            <UiIcon name="chevron-right" :size="26" color="var(--text-disabled)" />
          </view>
        </view>

        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.age') }}</text>
          <input
            class="edit-input"
            type="number"
            v-model="form.age"
            :placeholder="$t('profileEdit.agePh')"
            placeholder-class="edit-ph"
          />
        </view>
      </view>

      <text class="section-label">{{ $t('profileEdit.contact') }}</text>

      <view class="group-block">
        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.phone') }}</text>
          <input
            class="edit-input"
            type="number"
            v-model="form.phone"
            :placeholder="$t('profileEdit.phonePh')"
            placeholder-class="edit-ph"
          />
        </view>

        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.email') }}</text>
          <input
            class="edit-input"
            v-model="form.email"
            :placeholder="$t('profileEdit.emailPh')"
            placeholder-class="edit-ph"
          />
        </view>

        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.occupation') }}</text>
          <input
            class="edit-input"
            v-model="form.occupation"
            :placeholder="$t('profileEdit.occupationPh')"
            placeholder-class="edit-ph"
          />
        </view>
      </view>

      <text class="section-label">{{ $t('profileEdit.about') }}</text>

      <view class="group-block">
        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.signature') }}</text>
          <input
            class="edit-input"
            v-model="form.signature"
            :placeholder="$t('profileEdit.signaturePh')"
            placeholder-class="edit-ph"
          />
        </view>

        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.hobby') }}</text>
          <input
            class="edit-input"
            v-model="form.hobby"
            :placeholder="$t('profileEdit.hobbyPh')"
            placeholder-class="edit-ph"
          />
        </view>

        <view class="edit-row edit-row--stack">
          <text class="edit-label">{{ $t('profileEdit.bio') }}</text>
          <textarea
            class="edit-textarea"
            v-model="form.bio"
            :placeholder="$t('profileEdit.bioPh')"
            placeholder-class="edit-ph"
            :maxlength="200"
            :auto-height="false"
          />
          <text class="edit-counter">{{ form.bio.length }}/200</text>
        </view>

        <view class="edit-row">
          <text class="edit-label">{{ $t('profileEdit.experience') }}</text>
          <input
            class="edit-input"
            type="number"
            v-model="form.experience"
            :placeholder="$t('profileEdit.experiencePh')"
            placeholder-class="edit-ph"
          />
        </view>
      </view>

      <view class="edit-scroll__safe"></view>
    </scroll-view>

    <view class="edit-footer">
      <view
        class="save-btn"
        :class="{ 'is-saving': saving }"
        hover-class="save-btn--hover"
        hover-stay-time="60"
        @click="onSave"
      >
        <UiIcon name="check" :size="30" color="var(--accent-contrast)" />
        <text>{{ saving ? $t('profileEdit.saving') : $t('profileEdit.save') }}</text>
      </view>
    </view>
  </view>
</template>

<script>
import CustomNavBar from '../../components/CustomNavBar.vue'
import UiAvatar from '../../components/Avatar.vue'
import UiIcon from '../../components/Icon.vue'
import { getUserInfo as getStoredUserInfo, setUserInfo } from '../../utils/storage.js'
import { uploadFile, updateUserInfo, getUserInfo as fetchUserInfo } from '../../utils/chat-api.js'
import { setUser } from '../../store/index.js'

export default {
  components: { CustomNavBar, UiAvatar, UiIcon },
  data() {
    return {
      saving: false,
      form: {
        id: '',
        avatar: '',
        nickname: '',
        gender: '',
        age: '',
        phone: '',
        email: '',
        occupation: '',
        signature: '',
        hobby: '',
        bio: '',
        experience: ''
      }
    }
  },
  computed: {
    // 选项值保持原有中文存储约定，仅展示文案跟随语言切换
    genderOptions() {
      return [
        { value: '男', label: this.$t('profileEdit.genderMale') },
        { value: '女', label: this.$t('profileEdit.genderFemale') },
        { value: '不显示', label: this.$t('profileEdit.genderHidden') }
      ]
    },
    genderText() {
      const g = this.form.gender
      if (g === 'male' || g === '男') return this.$t('profileEdit.genderMale')
      if (g === 'female' || g === '女') return this.$t('profileEdit.genderFemale')
      return this.$t('profileEdit.genderHidden')
    }
  },
  onLoad() {
    this.initForm()
  },
  methods: {
    initForm() {
      const stored = getStoredUserInfo() || {}
      this.assignFrom(stored)
      this.loadRemote()
    },
    assignFrom(u) {
      this.form.id = this.form.id || u.id || ''
      this.form.avatar = this.form.avatar || u.avatar || ''
      this.form.nickname = this.form.nickname || u.nickname || u.username || ''
      this.form.gender = this.form.gender || u.gender || ''
      this.form.age = this.form.age || (u.age != null ? u.age : '')
      this.form.phone = this.form.phone || u.phone || ''
      this.form.email = this.form.email || u.email || ''
      this.form.occupation = this.form.occupation || u.occupation || u.job || ''
      this.form.signature = this.form.signature || u.signature || ''
      this.form.hobby = this.form.hobby || u.hobby || ''
      this.form.bio = this.form.bio || u.bio || u.description || u.intro || ''
      this.form.experience = this.form.experience || (u.experience != null ? u.experience : '')
    },
    async loadRemote() {
      try {
        const res = await fetchUserInfo()
        if (res && res.code === 200 && res.data) {
          const data = Array.isArray(res.data) ? res.data[0] : res.data
          if (data) this.assignFrom({ ...data, id: this.form.id || data.id })
        }
      } catch (e) {
        // 后端信息不可用时以本地信息为准
      }
    },
    chooseAvatar() {
      uni.chooseImage({
        count: 1,
        success: async (res) => {
          const filePath = res.tempFilePaths && res.tempFilePaths[0]
          if (!filePath) return
          uni.showLoading({ title: this.$t('profileEdit.uploading'), mask: true })
          try {
            const r = await uploadFile(filePath)
            if (r && r.code === 200 && r.data) {
              this.form.avatar = r.data
            } else {
              uni.showToast({ title: this.$t('profileEdit.uploadFailed'), icon: 'none' })
            }
          } catch (e) {
            console.error('头像上传失败:', e)
            uni.showToast({ title: this.$t('profileEdit.uploadFailed'), icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    chooseGender() {
      const options = this.genderOptions
      uni.showActionSheet({
        itemList: options.map((item) => item.label),
        success: (res) => {
          const target = options[res.tapIndex]
          if (target) this.form.gender = target.value
        }
      })
    },
    async onSave() {
      if (this.saving) return
      this.saving = true
      uni.showLoading({ title: this.$t('profileEdit.saving'), mask: true })
      try {
        const payload = { ...this.form }
        const res = await updateUserInfo(payload)
        if (res && res.code === 200) {
          const merged = { ...(getStoredUserInfo() || {}), ...payload }
          setUserInfo(merged)
          setUser(merged)
          uni.showToast({ title: this.$t('profileEdit.saveSuccess'), icon: 'success' })
          setTimeout(() => uni.navigateBack(), 800)
        } else {
          uni.showToast({
            title: (res && res.message) || this.$t('profileEdit.saveFailed'),
            icon: 'none'
          })
        }
      } catch (e) {
        console.error('保存个人信息失败:', e)
        uni.showToast({ title: this.$t('profileEdit.saveFailed'), icon: 'none' })
      } finally {
        uni.hideLoading()
        this.saving = false
      }
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.edit-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
}

.edit-scroll {
  flex: 1;
  height: 0;
}

.edit-page .section-label {
  display: block;
  padding: 30rpx 40rpx 14rpx;
  font-size: var(--font-xs);
  font-weight: 600;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  color: var(--text-tertiary);
}

.group-block {
  margin: 0 24rpx;
}

/* 头像 */
.edit-avatar {
  display: flex;
  align-items: center;
  padding: 28rpx 32rpx;
  transition: background-color var(--transition-fast);
}

.edit-avatar--hover {
  background-color: var(--bg-hover);
}

.edit-avatar__frame {
  padding: 5rpx;
  border-radius: 28rpx;
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  flex-shrink: 0;
}

.edit-avatar__body {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
}

.edit-avatar__title {
  display: block;
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
}

.edit-avatar__desc {
  display: block;
  margin-top: 6rpx;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
}

.edit-avatar__action {
  width: 64rpx;
  height: 64rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background-color: var(--accent-light);
  color: var(--accent-color);
}

/* 表单行 */
.edit-row {
  display: flex;
  align-items: center;
  padding: 26rpx 32rpx;
  position: relative;
  background-color: transparent;
  transition: background-color var(--transition-fast);
}

.edit-row::after {
  content: '';
  position: absolute;
  left: 32rpx;
  right: 32rpx;
  bottom: 0;
  height: 1rpx;
  background-color: var(--divider);
}

.edit-row:last-child::after {
  display: none;
}

.edit-row--hover {
  background-color: var(--bg-hover);
}

.edit-label {
  font-size: var(--font-md);
  color: var(--text-primary);
  flex-shrink: 0;
  min-width: 150rpx;
}

.edit-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12rpx;
  min-width: 0;
}

.edit-value {
  font-size: var(--font-md);
  color: var(--text-secondary);
}

.edit-input {
  flex: 1;
  min-width: 0;
  margin-left: 24rpx;
  text-align: right;
  font-size: var(--font-md);
  color: var(--text-primary);
}

.edit-ph {
  color: var(--text-placeholder);
}

.edit-row--stack {
  flex-direction: column;
  align-items: stretch;
}

.edit-row--stack .edit-label {
  min-width: 0;
}

.edit-textarea {
  width: 100%;
  height: 180rpx;
  margin-top: 14rpx;
  padding: 20rpx;
  box-sizing: border-box;
  font-size: var(--font-base);
  color: var(--text-primary);
  background-color: var(--bg-input);
  border-radius: var(--radius-md);
}

.edit-counter {
  align-self: flex-end;
  margin-top: 8rpx;
  font-size: var(--font-xs);
  color: var(--text-tertiary);
  font-family: var(--font-mono);
}

.edit-scroll__safe {
  height: 32rpx;
}

/* 底部粘性保存 */
.edit-footer {
  flex-shrink: 0;
  padding: 20rpx 24rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: var(--glass-bg);
  border-top: 1rpx solid var(--divider);
  backdrop-filter: saturate(160%) blur(var(--glass-blur));
  -webkit-backdrop-filter: saturate(160%) blur(var(--glass-blur));
  box-shadow: var(--shadow-md);
}

.save-btn {
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10rpx;
  border-radius: 999rpx;
  background-image: var(--accent-gradient);
  box-shadow: var(--accent-glow);
  transition: transform var(--transition-fast), opacity var(--transition-fast);
}

.save-btn--hover {
  transform: scale(0.98);
  opacity: 0.92;
}

.save-btn.is-saving {
  opacity: 0.68;
}

.save-btn text {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--accent-contrast);
  letter-spacing: 0.6rpx;
}
</style>
