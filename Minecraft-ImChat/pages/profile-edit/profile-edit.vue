<template>
  <view class="edit-page">
    <CustomNavBar
      :show-back="true"
      title="个人信息"
      title-align="center"
      @back="goBack"
    />

    <scroll-view class="edit-scroll" scroll-y :show-scrollbar="false">
      <view class="edit-group">
        <view class="edit-row" hover-class="edit-row--hover" hover-stay-time="60" @click="chooseAvatar">
          <text class="edit-label">头像</text>
          <view class="edit-right">
            <UiAvatar shape="square" :size="100" :src="form.avatar" :name="form.nickname" />
            <UiIcon name="chevron-right" :size="34" color="#C8C8CD" />
          </view>
        </view>

        <view class="edit-row">
          <text class="edit-label">昵称</text>
          <input class="edit-input" v-model="form.nickname" placeholder="请输入昵称" placeholder-class="edit-ph" />
        </view>

        <view class="edit-row" hover-class="edit-row--hover" hover-stay-time="60" @click="chooseGender">
          <text class="edit-label">性别</text>
          <view class="edit-right">
            <text class="edit-value">{{ genderText }}</text>
            <UiIcon name="chevron-right" :size="34" color="#C8C8CD" />
          </view>
        </view>

        <view class="edit-row">
          <text class="edit-label">年龄</text>
          <input class="edit-input" type="number" v-model="form.age" placeholder="请输入年龄" placeholder-class="edit-ph" />
        </view>
      </view>

      <view class="edit-group">
        <view class="edit-row">
          <text class="edit-label">手机号</text>
          <input class="edit-input" type="number" v-model="form.phone" placeholder="请输入手机号" placeholder-class="edit-ph" />
        </view>

        <view class="edit-row">
          <text class="edit-label">邮箱</text>
          <input class="edit-input" v-model="form.email" placeholder="请输入邮箱" placeholder-class="edit-ph" />
        </view>

        <view class="edit-row">
          <text class="edit-label">职业</text>
          <input class="edit-input" v-model="form.occupation" placeholder="请输入职业" placeholder-class="edit-ph" />
        </view>
      </view>

      <view class="edit-group">
        <view class="edit-row">
          <text class="edit-label">个性签名</text>
          <input class="edit-input" v-model="form.signature" placeholder="请输入个性签名" placeholder-class="edit-ph" />
        </view>

        <view class="edit-row">
          <text class="edit-label">爱好</text>
          <input class="edit-input" v-model="form.hobby" placeholder="请输入爱好" placeholder-class="edit-ph" />
        </view>

        <view class="edit-row edit-row--textarea">
          <text class="edit-label">个人简介</text>
          <textarea
            class="edit-textarea"
            v-model="form.bio"
            placeholder="请输入个人简介"
            placeholder-class="edit-ph"
            :maxlength="200"
            :auto-height="false"
          />
        </view>

        <view class="edit-row">
          <text class="edit-label">经验</text>
          <input class="edit-input" type="number" v-model="form.experience" placeholder="请输入经验" placeholder-class="edit-ph" />
        </view>
      </view>

      <view class="edit-scroll__safe"></view>
    </scroll-view>

    <view class="edit-footer">
      <view class="save-btn" hover-class="save-btn--hover" hover-stay-time="60" @click="onSave">
        <text>保存</text>
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
    genderText() {
      const g = this.form.gender
      if (g === 'male' || g === '男') return '男'
      if (g === 'female' || g === '女') return '女'
      return '不显示'
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
          uni.showLoading({ title: '上传中…', mask: true })
          try {
            const r = await uploadFile(filePath)
            if (r && r.code === 200 && r.data) {
              this.form.avatar = r.data
            } else {
              uni.showToast({ title: '上传失败', icon: 'none' })
            }
          } catch (e) {
            console.error('头像上传失败:', e)
            uni.showToast({ title: '上传失败', icon: 'none' })
          } finally {
            uni.hideLoading()
          }
        }
      })
    },
    chooseGender() {
      uni.showActionSheet({
        itemList: ['男', '女', '不显示'],
        success: (res) => {
          this.form.gender = ['男', '女', '不显示'][res.tapIndex]
        }
      })
    },
    async onSave() {
      if (this.saving) return
      this.saving = true
      uni.showLoading({ title: '保存中…', mask: true })
      try {
        const payload = { ...this.form }
        const res = await updateUserInfo(payload)
        if (res && res.code === 200) {
          const merged = { ...(getStoredUserInfo() || {}), ...payload }
          setUserInfo(merged)
          setUser(merged)
          uni.showToast({ title: '保存成功', icon: 'success' })
          setTimeout(() => uni.navigateBack(), 800)
        } else {
          uni.showToast({ title: (res && res.message) || '保存失败', icon: 'none' })
        }
      } catch (e) {
        console.error('保存个人信息失败:', e)
        uni.showToast({ title: '保存失败', icon: 'none' })
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
  background-color: #ededed;
}

.edit-scroll {
  flex: 1;
  height: 0;
}

.edit-group {
  background-color: #ffffff;
  margin-top: 20rpx;
}

.edit-row {
  display: flex;
  align-items: center;
  padding: 26rpx 32rpx;
  background-color: #ffffff;
  position: relative;
}

.edit-row::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1rpx;
  background-color: #ededed;
  transform: scaleY(0.5);
}

.edit-group .edit-row:last-child::after {
  display: none;
}

.edit-row--hover {
  background-color: #ececec;
}

.edit-label {
  font-size: 32rpx;
  color: #191919;
  flex-shrink: 0;
  min-width: 140rpx;
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
  font-size: 30rpx;
  color: #888888;
}

.edit-input {
  flex: 1;
  text-align: right;
  margin-left: 24rpx;
  font-size: 30rpx;
  color: #191919;
}

.edit-textarea {
  flex: 1;
  text-align: right;
  margin-left: 24rpx;
  height: 160rpx;
  font-size: 30rpx;
  color: #191919;
}

.edit-ph {
  color: #b2b2b2;
}

.edit-row--textarea {
  align-items: flex-start;
}

.edit-row--textarea .edit-label {
  padding-top: 6rpx;
}

.edit-scroll__safe {
  height: 40rpx;
}

.edit-footer {
  flex-shrink: 0;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: #ededed;
}

.save-btn {
  height: 88rpx;
  background-color: #07c160;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.save-btn--hover {
  background-color: #06ad56;
}

.save-btn text {
  font-size: 32rpx;
  color: #ffffff;
  font-weight: 500;
}
</style>
