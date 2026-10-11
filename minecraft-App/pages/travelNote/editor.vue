<template>
  <view class="note-editor">
    <text class="page-title">{{ isEdit ? '编辑笔记' : '发布旅游笔记' }}</text>

    <view class="field">
      <text class="label">标题</text>
      <input v-model="form.title" class="input" maxlength="120" placeholder="一句话介绍这次旅行" />
    </view>

    <view class="field">
      <text class="label">摘要</text>
      <textarea v-model="form.summary" class="textarea" maxlength="300" placeholder="将展示在卡片上" />
    </view>

    <view class="field-row">
      <view class="field field--half">
        <text class="label">目的地</text>
        <input v-model="form.destination" class="input" maxlength="100" placeholder="如：云南 · 大理" />
      </view>
      <view class="field field--half">
        <text class="label">出行日期</text>
        <picker mode="date" :value="form.travelDate" @change="onDateChange">
          <view class="input picker-value">{{ form.travelDate || '请选择' }}</view>
        </picker>
      </view>
    </view>

    <view class="field-row">
      <view class="field field--half">
        <text class="label">旅行天数</text>
        <input v-model.number="form.travelDays" class="input" type="number" placeholder="1" />
      </view>
      <view class="field field--half">
        <text class="label">人均花费（元）</text>
        <input v-model.number="form.cost" class="input" type="digit" placeholder="0" />
      </view>
    </view>

    <view class="field">
      <text class="label">图集（第一张为封面）</text>
      <view class="image-list">
        <view v-for="(img, idx) in images" :key="idx" class="image-item">
          <image class="image-thumb" :src="img" mode="aspectFill" />
          <view class="image-remove" @click="removeImage(idx)">×</view>
        </view>
        <view v-if="images.length < 18" class="image-add" @click="chooseImages">
          <text class="image-add-icon">+</text>
        </view>
      </view>
    </view>

    <view class="field">
      <text class="label">正文</text>
      <textarea
        v-model="content"
        class="textarea textarea--large"
        maxlength="5000"
        placeholder="开始记录你的旅程..."
      />
    </view>

    <view class="field">
      <text class="label">标签（逗号分隔，最多10个）</text>
      <input v-model="tagText" class="input" placeholder="亲子游,自驾游,美食" />
    </view>

    <view class="submit-bar">
      <button class="btn btn--ghost" :disabled="saving" @click="save(0)">保存草稿</button>
      <button class="btn btn--ghost" :disabled="saving" @click="save(5)">设为私密</button>
      <button class="btn btn--primary" :disabled="saving" @click="save(1)">提交审核</button>
    </view>
  </view>
</template>

<script>
import { createNote, getNoteDetail, updateNote, uploadNoteImage } from '@/api/travelNote'

export default {
  data() {
    return {
      editId: null,
      saving: false,
      content: '',
      tagText: '',
      images: [],
      form: {
        title: '',
        summary: '',
        destination: '',
        travelDate: '',
        travelDays: 1,
        cost: null
      }
    }
  },
  computed: {
    isEdit() {
      return !!this.editId
    }
  },
  onLoad(options) {
    if (!uni.getStorageSync('token')) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => uni.navigateBack(), 800)
      return
    }
    if (options.id) {
      this.editId = Number(options.id)
      this.loadForEdit()
    }
  },
  methods: {
    async loadForEdit() {
      const res = await getNoteDetail(this.editId)
      if (res.code !== 200 || !res.data) {
        uni.showToast({ title: res.message || '笔记不存在', icon: 'none' })
        setTimeout(() => uni.navigateBack(), 800)
        return
      }
      const d = res.data
      this.form.title = d.title || ''
      this.form.summary = d.summary || ''
      this.form.destination = d.destination || ''
      this.form.travelDate = d.travelDate || ''
      this.form.travelDays = d.travelDays || 1
      this.form.cost = d.cost ? Number(d.cost) : null
      this.content = (d.content || '').replace(/<br\s*\/?>/gi, '\n').replace(/<\/?p>/gi, '')
      this.tagText = (d.tags || []).join(',')
      this.images = (d.mediaList || []).filter((m) => m.mediaType === 1).map((m) => m.url)
    },
    onDateChange(e) {
      this.form.travelDate = e.detail.value
    },
    chooseImages() {
      uni.chooseImage({
        count: 9,
        success: async (res) => {
          for (const path of res.tempFilePaths) {
            try {
              const up = await uploadNoteImage(path)
              if (up.code === 200 && up.data) {
                this.images.push(up.data)
              } else {
                uni.showToast({ title: up.message || '上传失败', icon: 'none' })
              }
            } catch (e) {
              console.error('图片上传失败', e)
            }
          }
        }
      })
    },
    removeImage(idx) {
      this.images.splice(idx, 1)
    },
    buildPayload(status) {
      const tags = this.tagText
        .split(/[,，\s]+/)
        .map((t) => t.trim().replace(/^#/, ''))
        .filter(Boolean)
        .slice(0, 10)
      return {
        title: this.form.title.trim(),
        summary: this.form.summary,
        destination: this.form.destination,
        travelDate: this.form.travelDate || null,
        travelDays: Number(this.form.travelDays) || 1,
        cost: this.form.cost ? Number(this.form.cost) : null,
        coverUrl: this.images[0] || null,
        contentType: 1,
        content: this.content ? this.content.replace(/\n/g, '<br/>') : '',
        status,
        tags,
        mediaList: this.images.map((url, idx) => ({ mediaType: 1, url, sort: idx }))
      }
    },
    async save(status) {
      if (!this.form.title.trim()) {
        uni.showToast({ title: '请填写标题', icon: 'none' })
        return
      }
      if (status === 1 && !this.content.trim()) {
        uni.showToast({ title: '提交审核前请填写正文', icon: 'none' })
        return
      }
      this.saving = true
      uni.showLoading({ title: '提交中' })
      try {
        const payload = this.buildPayload(status)
        const res = this.isEdit ? await updateNote(this.editId, payload) : await createNote(payload)
        uni.hideLoading()
        if (res.code === 200) {
          uni.showToast({
            title: status === 0 ? '草稿已保存' : status === 5 ? '已设为私密' : '已提交审核',
            icon: 'none'
          })
          setTimeout(() => uni.redirectTo({ url: '/pages/travelNote/mine' }), 900)
        } else {
          uni.showToast({ title: res.message || '保存失败', icon: 'none' })
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: '保存失败', icon: 'none' })
      } finally {
        this.saving = false
      }
    }
  }
}
</script>

<style scoped>
.note-editor {
  min-height: 100vh;
  background-color: var(--background-color, #f8faff);
  padding: 24rpx 24rpx 200rpx;
}

.page-title {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: var(--text-color, #1f2329);
  margin-bottom: 24rpx;
}

.field {
  margin-bottom: 24rpx;
}

.field-row {
  display: flex;
  gap: 20rpx;
}

.field--half {
  flex: 1;
}

.label {
  display: block;
  font-size: 26rpx;
  color: var(--text-secondary-color, #7d7e80);
  margin-bottom: 12rpx;
}

.input {
  width: 100%;
  height: 84rpx;
  padding: 0 24rpx;
  background-color: #fff;
  border-radius: 12rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.picker-value {
  display: flex;
  align-items: center;
}

.textarea {
  width: 100%;
  min-height: 140rpx;
  padding: 20rpx 24rpx;
  background-color: #fff;
  border-radius: 12rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.textarea--large {
  min-height: 360rpx;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.image-item {
  position: relative;
  width: 180rpx;
  height: 180rpx;
}

.image-thumb {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
  background-color: #eef2f8;
}

.image-remove {
  position: absolute;
  top: -12rpx;
  right: -12rpx;
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 28rpx;
  text-align: center;
  line-height: 40rpx;
}

.image-add {
  width: 180rpx;
  height: 180rpx;
  border-radius: 12rpx;
  background-color: #fff;
  border: 2rpx dashed #cfd6e4;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-add-icon {
  font-size: 56rpx;
  color: #a0a4ab;
}

.submit-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 16rpx;
  padding: 20rpx 24rpx 40rpx;
  background-color: #fff;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.btn {
  flex: 1;
  height: 84rpx;
  line-height: 84rpx;
  border-radius: 42rpx;
  font-size: 28rpx;
  border: none;
}

.btn--ghost {
  background-color: #f0f4fa;
  color: var(--text-color, #1f2329);
}

.btn--primary {
  background-color: var(--primary-color, #3a80d2);
  color: #fff;
}
</style>
