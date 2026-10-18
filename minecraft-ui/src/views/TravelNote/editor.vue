<template>
  <div class="note-editor">
    <!-- Hero -->
    <header class="note-editor__hero">
      <div class="note-editor__hero-main">
        <button type="button" class="note-editor__back" @click="router.push('/travel-note/mine')">
          <el-icon><ArrowLeft /></el-icon>
          <span>{{ $t('travelNote.editor.backToMine') }}</span>
        </button>
        <p class="note-editor__eyebrow">{{ $t('travelNote.editor.eyebrow') }}</p>
        <h1 class="note-editor__title">
          {{ isEdit ? $t('travelNote.editor.editTitle') : $t('travelNote.editor.createTitle') }}
        </h1>
        <p class="note-editor__sub">{{ $t('travelNote.editor.subtitle') }}</p>
      </div>
      <span class="note-editor__badge">
        <i class="note-editor__badge-dot" />
        {{ isEdit ? $t('travelNote.editor.badgeEdit') : $t('travelNote.editor.badgeNew') }}
      </span>
    </header>

    <el-form :model="form" label-position="top" class="note-editor__form" @submit.prevent>
      <main class="note-editor__main">
        <!-- 01 基础信息 -->
        <section class="ne-card">
          <header class="ne-card__head">
            <span class="ne-card__num">01</span>
            <div class="ne-card__head-text">
              <h2 class="ne-card__title">{{ $t('travelNote.editor.section.basic') }}</h2>
              <p class="ne-card__desc">{{ $t('travelNote.editor.section.basicDesc') }}</p>
            </div>
          </header>

          <el-form-item :label="$t('travelNote.editor.fields.title')" required>
            <el-input v-model="form.title" class="ne-title-input" size="large" maxlength="120" show-word-limit
              :placeholder="$t('travelNote.editor.fields.titlePlaceholder')" />
          </el-form-item>

          <el-form-item :label="$t('travelNote.editor.fields.summary')">
            <el-input v-model="form.summary" type="textarea" :rows="3" resize="none" maxlength="300" show-word-limit
              :placeholder="$t('travelNote.editor.fields.summaryPlaceholder')" />
          </el-form-item>

          <div class="ne-grid">
            <el-form-item :label="$t('travelNote.editor.fields.destination')">
              <el-input v-model="form.destination" maxlength="100"
                :placeholder="$t('travelNote.editor.fields.destinationPlaceholder')" />
            </el-form-item>
            <el-form-item :label="$t('travelNote.editor.fields.travelDate')">
              <el-date-picker v-model="form.travelDate" type="date" value-format="YYYY-MM-DD"
                :placeholder="$t('travelNote.editor.fields.travelDatePlaceholder')" style="width: 100%" />
            </el-form-item>
            <el-form-item :label="$t('travelNote.editor.fields.travelDays')">
              <el-input-number v-model="form.travelDays" :min="1" :max="365" style="width: 100%" />
            </el-form-item>
            <el-form-item :label="$t('travelNote.editor.fields.cost')">
              <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </div>
        </section>

        <!-- 02 封面与图集 -->
        <section class="ne-card">
          <header class="ne-card__head">
            <span class="ne-card__num">02</span>
            <div class="ne-card__head-text">
              <h2 class="ne-card__title">{{ $t('travelNote.editor.section.media') }}</h2>
              <p class="ne-card__desc">{{ $t('travelNote.editor.section.mediaDesc') }}</p>
            </div>
            <span v-if="form.mediaList.length" class="ne-card__aside">{{ form.mediaList.length }}</span>
          </header>

          <div class="note-editor__upload">
            <el-upload v-model:file-list="fileList" list-type="picture-card" :auto-upload="true"
              :http-request="handleUpload" :before-upload="beforeUpload" accept="image/*" multiple :limit="18"
              @remove="handleRemove">
              <div class="ne-upload__trigger">
                <el-icon><Plus /></el-icon>
                <span>{{ $t('travelNote.editor.upload.add') }}</span>
              </div>
            </el-upload>
            <p class="ne-hint">{{ $t('travelNote.editor.mediaHint') }}</p>
          </div>
        </section>

        <!-- 03 正文 -->
        <section class="ne-card">
          <header class="ne-card__head">
            <span class="ne-card__num">03</span>
            <div class="ne-card__head-text">
              <h2 class="ne-card__title">{{ $t('travelNote.editor.section.content') }}</h2>
              <p class="ne-card__desc">{{ $t('travelNote.editor.section.contentDesc') }}</p>
            </div>
            <span class="ne-card__aside">{{ $t('travelNote.editor.wordCount', { count: contentLength }) }}</span>
          </header>

          <div class="rich-editor">
            <div class="rich-editor__toolbar">
              <button type="button" :title="$t('travelNote.editor.toolbar.bold')" @click="exec('bold')"><b>B</b></button>
              <button type="button" :title="$t('travelNote.editor.toolbar.italic')" @click="exec('italic')"><i>I</i></button>
              <button type="button" :title="$t('travelNote.editor.toolbar.heading')"
                @click="exec('formatBlock', 'h2')">H2</button>
              <button type="button" :title="$t('travelNote.editor.toolbar.quote')"
                @click="exec('formatBlock', 'blockquote')">“”</button>
              <button type="button" :title="$t('travelNote.editor.toolbar.unorderedList')"
                @click="exec('insertUnorderedList')">•</button>
              <button type="button" :title="$t('travelNote.editor.toolbar.orderedList')"
                @click="exec('insertOrderedList')">1.</button>
              <button type="button" :title="$t('travelNote.editor.toolbar.divider')"
                @click="exec('insertHorizontalRule')">—</button>
              <button type="button" class="rich-editor__toolbar-icon" :title="$t('travelNote.editor.toolbar.image')"
                @click="triggerImageUpload">
                <el-icon><Picture /></el-icon>
              </button>
            </div>
            <div ref="editorRef" class="rich-editor__area" contenteditable="true"
              :data-placeholder="$t('travelNote.editor.editorPlaceholder')" @input="syncContent" />
            <input ref="imageInputRef" type="file" accept="image/*" hidden @change="handleEditorImage" />
          </div>
        </section>

        <!-- 04 标签 -->
        <section class="ne-card">
          <header class="ne-card__head">
            <span class="ne-card__num">04</span>
            <div class="ne-card__head-text">
              <h2 class="ne-card__title">{{ $t('travelNote.editor.section.tags') }}</h2>
              <p class="ne-card__desc">{{ $t('travelNote.editor.section.tagsDesc') }}</p>
            </div>
            <span class="ne-card__aside">{{ $t('travelNote.editor.tagsCount', { count: form.tags.length, max: 10 }) }}</span>
          </header>

          <div class="ne-tags">
            <el-tag v-for="tag in form.tags" :key="tag" closable @close="removeTag(tag)"># {{ tag }}</el-tag>
            <el-input v-model="tagInput" class="ne-tag-input" size="default" maxlength="20"
              :placeholder="$t('travelNote.editor.fields.tagPlaceholder')" @keyup.enter.prevent="addTag"
              @keydown.enter.prevent />
            <el-button type="primary" plain round size="default" @click="addTag">
              {{ $t('travelNote.editor.fields.addTag') }}
            </el-button>
          </div>
        </section>

        <!-- 05 关联内容 -->
        <section class="ne-card">
          <header class="ne-card__head">
            <span class="ne-card__num">05</span>
            <div class="ne-card__head-text">
              <h2 class="ne-card__title">{{ $t('travelNote.editor.section.relations') }}</h2>
              <p class="ne-card__desc">{{ $t('travelNote.editor.section.relationsDesc') }}</p>
            </div>
          </header>

          <div class="ne-rows">
            <div v-for="(rel, idx) in form.relations" :key="idx" class="ne-row">
              <el-select v-model="rel.bizType" class="ne-relation__type">
                <el-option :label="$t('travelNote.editor.bizType.attraction')" :value="1" />
                <el-option :label="$t('travelNote.editor.bizType.hotel')" :value="2" />
                <el-option :label="$t('travelNote.editor.bizType.food')" :value="3" />
                <el-option :label="$t('travelNote.editor.bizType.product')" :value="4" />
              </el-select>
              <el-input-number v-model="rel.bizId" :min="1" controls-position="right"
                :placeholder="$t('travelNote.editor.fields.bizId')" class="ne-relation__id" />
              <el-button text type="danger" @click="form.relations.splice(idx, 1)">
                {{ $t('travelNote.editor.fields.remove') }}
              </el-button>
            </div>
            <button type="button" class="ne-add" @click="form.relations.push({ bizType: 1, bizId: null })">
              <el-icon><Plus /></el-icon>
              {{ $t('travelNote.editor.addRelation') }}
            </button>
          </div>
        </section>

        <!-- 06 行程轨迹 -->
        <section class="ne-card">
          <header class="ne-card__head">
            <span class="ne-card__num">06</span>
            <div class="ne-card__head-text">
              <h2 class="ne-card__title">{{ $t('travelNote.editor.section.routes') }}</h2>
              <p class="ne-card__desc">{{ $t('travelNote.editor.section.routesDesc') }}</p>
            </div>
          </header>

          <div class="ne-rows">
            <div v-for="(node, idx) in form.routes" :key="idx" class="ne-row ne-route">
              <el-input-number v-model="node.dayIndex" :min="1" controls-position="right"
                :placeholder="$t('travelNote.editor.fields.day')" class="ne-route__day" />
              <el-input v-model="node.poiName" maxlength="120"
                :placeholder="$t('travelNote.editor.fields.poiName')" class="ne-route__poi" />
              <el-input v-model="node.longitude" :placeholder="$t('travelNote.editor.fields.longitude')"
                class="ne-route__coord" />
              <el-input v-model="node.latitude" :placeholder="$t('travelNote.editor.fields.latitude')"
                class="ne-route__coord" />
              <el-input v-model="node.remark" maxlength="255" :placeholder="$t('travelNote.editor.fields.remark')"
                class="ne-route__remark" />
              <el-button text type="danger" class="ne-route__remove" @click="form.routes.splice(idx, 1)">
                {{ $t('travelNote.editor.fields.remove') }}
              </el-button>
            </div>
            <button type="button" class="ne-add"
              @click="form.routes.push({ dayIndex: form.routes.length + 1, poiName: '', longitude: '', latitude: '', remark: '' })">
              <el-icon><Plus /></el-icon>
              {{ $t('travelNote.editor.addRoute') }}
            </button>
          </div>
        </section>
      </main>

      <!-- 右侧：卡片预览 + 发布 -->
      <aside class="note-editor__rail">
        <div class="ne-rail">
          <div class="ne-preview">
            <div class="ne-preview__cover">
              <img v-if="form.coverUrl" :src="form.coverUrl" alt="" />
              <div v-else class="ne-preview__cover-empty">
                <el-icon :size="26"><Picture /></el-icon>
              </div>
              <span class="ne-preview__label">{{ $t('travelNote.editor.preview.label') }}</span>
            </div>
            <div class="ne-preview__body">
              <h3 class="ne-preview__title" :class="{ 'is-placeholder': !form.title }">
                {{ form.title || $t('travelNote.editor.preview.titleFallback') }}
              </h3>
              <p class="ne-preview__summary">{{ form.summary || $t('travelNote.editor.preview.summaryFallback') }}</p>
              <div class="ne-preview__meta">
                <span>
                  <el-icon><Location /></el-icon>
                  {{ form.destination || $t('travelNote.editor.preview.destinationFallback') }}
                </span>
                <span v-if="form.travelDays">
                  <el-icon><Calendar /></el-icon>
                  {{ $t('travelNote.editor.preview.days', { days: form.travelDays }) }}
                </span>
              </div>
            </div>
          </div>

          <div class="ne-check">
            <p class="ne-check__title">{{ $t('travelNote.editor.check.title') }}</p>
            <ul>
              <li v-for="item in checklist" :key="item.key" :class="{ 'is-done': item.done }">{{ item.label }}</li>
            </ul>
          </div>

          <div class="ne-actions">
            <el-button type="primary" round class="ne-actions__primary" :loading="saving" @click="save(1)">
              {{ $t('travelNote.editor.actions.submit') }}
            </el-button>
            <div class="ne-actions__row">
              <el-button round :loading="saving" @click="save(0)">
                {{ $t('travelNote.editor.actions.draft') }}
              </el-button>
              <el-button round :loading="saving" @click="save(5)">
                {{ $t('travelNote.editor.actions.private') }}
              </el-button>
            </div>
            <p class="ne-actions__tip">{{ $t('travelNote.editor.actions.tip') }}</p>
          </div>
        </div>
      </aside>
    </el-form>

    <!-- 移动端底部操作栏 -->
    <div class="note-editor__dock">
      <el-button round :loading="saving" @click="save(0)">{{ $t('travelNote.editor.actions.draft') }}</el-button>
      <el-button round :loading="saving" @click="save(5)">{{ $t('travelNote.editor.actions.private') }}</el-button>
      <el-button type="primary" round :loading="saving" @click="save(1)">
        {{ $t('travelNote.editor.actions.submit') }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Calendar, Location, Picture, Plus } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { createNote, getNoteDetail, updateNote } from '@/api/travelNote'
import { uploadFile } from '@/api/upload'
import { useAuthStore } from '@/stores/auth'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const { t } = useI18n()

const editId = route.query.id ? Number(route.query.id) : null
const isEdit = computed(() => !!editId)

const form = ref({
  title: '',
  summary: '',
  destination: '',
  travelDate: null,
  travelDays: 1,
  cost: null,
  coverUrl: '',
  content: '',
  contentType: 1,
  tags: [],
  mediaList: [],
  relations: [],
  routes: []
})

const fileList = ref([])
const tagInput = ref('')
const saving = ref(false)
const editorRef = ref(null)
const imageInputRef = ref(null)

/** 正文字数（去除 HTML 标签） */
const contentLength = computed(() => {
  const text = String(form.value.content || '')
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/g, ' ')
    .trim()
  return text.length
})

/** 发布前检查项 */
const checklist = computed(() => [
  { key: 'title', label: t('travelNote.editor.check.titleLabel'), done: !!form.value.title.trim() },
  { key: 'cover', label: t('travelNote.editor.check.coverLabel'), done: !!form.value.coverUrl },
  { key: 'content', label: t('travelNote.editor.check.contentLabel'), done: contentLength.value > 0 }
])

onMounted(() => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning(t('travelNote.editor.messages.loginRequired'))
    router.replace('/login')
    return
  }
  if (isEdit.value) {
    loadForEdit()
  }
})

async function loadForEdit() {
  const res = await getNoteDetail(editId)
  if (res.code !== 200 || !res.data) {
    ElMessage.error(res.message || t('travelNote.editor.messages.noteNotFound'))
    router.replace('/travel-note/mine')
    return
  }
  const d = res.data
  form.value = {
    title: d.title || '',
    summary: d.summary || '',
    destination: d.destination || '',
    travelDate: d.travelDate || null,
    travelDays: d.travelDays || 1,
    cost: d.cost ? Number(d.cost) : null,
    coverUrl: d.coverUrl || '',
    content: d.content || '',
    contentType: d.contentType || 1,
    tags: d.tags || [],
    mediaList: (d.mediaList || []).map((m) => ({ mediaType: m.mediaType, url: m.url, thumbUrl: m.thumbUrl, sort: m.sort })),
    relations: (d.relations || []).map((r) => ({ bizType: r.bizType, bizId: r.bizId })),
    routes: (d.routes || []).map((r) => ({
      dayIndex: r.dayIndex,
      poiName: r.poiName,
      longitude: r.longitude != null ? String(r.longitude) : '',
      latitude: r.latitude != null ? String(r.latitude) : '',
      remark: r.remark || ''
    }))
  }
  fileList.value = (d.mediaList || [])
    .filter((m) => m.mediaType === 1)
    .map((m) => ({ name: m.url.split('/').pop(), url: m.url }))
  if (editorRef.value) {
    editorRef.value.innerHTML = d.contentType === 1 ? d.content || '' : ''
  }
}

// 数据变更后自动刷新（仅编辑态）
useAutoRefresh(() => {
  if (isEdit.value) loadForEdit()
})

// ---------- 上传 ----------
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) ElMessage.error(t('travelNote.editor.upload.onlyImage'))
  return isImage
}

async function handleUpload({ file, onSuccess, onError }) {
  try {
    const res = await uploadFile(file)
    if (res.code === 200 && res.data) {
      form.value.mediaList.push({ mediaType: 1, url: res.data, thumbUrl: '', sort: form.value.mediaList.length })
      if (!form.value.coverUrl) form.value.coverUrl = res.data
      onSuccess(res)
    } else {
      ElMessage.error(res.message || t('travelNote.editor.upload.failed'))
      onError(new Error(res.message))
    }
  } catch (e) {
    onError(e)
  }
}

// ---------- 富文本 ----------
function exec(command, value = null) {
  editorRef.value?.focus()
  document.execCommand(command, false, value)
  syncContent()
}

function syncContent() {
  form.value.content = editorRef.value?.innerHTML || ''
}

function triggerImageUpload() {
  imageInputRef.value?.click()
}

async function handleEditorImage(e) {
  const file = e.target.files?.[0]
  if (!file) return
  try {
    const res = await uploadFile(file)
    if (res.code === 200 && res.data) {
      exec('insertHTML', `<img src="${res.data}" alt="" />`)
      form.value.mediaList.push({ mediaType: 1, url: res.data, thumbUrl: '', sort: form.value.mediaList.length })
    } else {
      ElMessage.error(res.message || t('travelNote.editor.upload.imageFailed'))
    }
  } finally {
    e.target.value = ''
  }
}

watch(fileList, (list) => {
  // 上传成功后维护封面：无封面时取第一张
  if (!form.value.coverUrl) {
    form.value.coverUrl = list.find((f) => f.url)?.url || ''
  }
})

function handleRemove(file) {
  form.value.mediaList = form.value.mediaList.filter((m) => m.url !== file.url)
  if (form.value.coverUrl === file.url) {
    form.value.coverUrl = form.value.mediaList.find((m) => m.mediaType === 1)?.url || ''
  }
}

// ---------- 标签 ----------
function addTag() {
  const tag = tagInput.value.trim().replace(/^#/, '')
  if (!tag) return
  if (form.value.tags.includes(tag)) {
    ElMessage.warning(t('travelNote.editor.messages.tagExists'))
    return
  }
  if (form.value.tags.length >= 10) {
    ElMessage.warning(t('travelNote.editor.messages.maxTags'))
    return
  }
  form.value.tags.push(tag)
  tagInput.value = ''
}

function removeTag(tag) {
  form.value.tags = form.value.tags.filter((item) => item !== tag)
}

// ---------- 保存 ----------
async function save(status) {
  if (!form.value.title.trim()) {
    ElMessage.warning(t('travelNote.editor.messages.titleRequired'))
    return
  }
  syncContent()
  if (status === 1 && !form.value.content) {
    ElMessage.warning(t('travelNote.editor.messages.contentRequired'))
    return
  }
  saving.value = true
  try {
    const payload = {
      ...form.value,
      status,
      mediaList: form.value.mediaList.map((m, idx) => ({ ...m, sort: idx })),
      relations: form.value.relations.filter((r) => r.bizId != null),
      routes: form.value.routes
        .filter((r) => r.poiName && r.poiName.trim())
        .map((r, idx) => ({
          ...r,
          sort: idx,
          longitude: r.longitude ? Number(r.longitude) : null,
          latitude: r.latitude ? Number(r.latitude) : null
        }))
    }
    const res = isEdit.value ? await updateNote(editId, payload) : await createNote(payload)
    if (res.code === 200) {
      ElMessage.success(
        status === 0
          ? t('travelNote.editor.messages.draftSaved')
          : status === 5
            ? t('travelNote.editor.messages.privateSaved')
            : t('travelNote.editor.messages.submitted')
      )
      router.push('/travel-note/mine')
    } else {
      ElMessage.error(res.message || t('travelNote.editor.messages.saveFailed'))
    }
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
/* ============ shell ============ */
.note-editor {
  --ne-ink: var(--theme-text-1);
  --ne-ink-2: var(--theme-text-2);
  --ne-ink-3: var(--theme-text-3);
  --ne-accent: var(--theme-primary);
  --ne-accent-2: var(--theme-accent-2);
  --ne-surface: var(--theme-bg-surface);
  --ne-line: var(--theme-border);
  --ne-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  max-width: 1180px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 40px 24px 96px;
  font-family: var(--ne-font);
  color: var(--ne-ink);
}

.note-editor::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(980px 520px at 6% -10%, var(--theme-primary-soft), transparent 60%),
    radial-gradient(820px 460px at 102% 0%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%),
    radial-gradient(780px 560px at 50% 118%, var(--theme-success-soft), transparent 62%),
    var(--theme-bg-surface-2);
}

/* ============ hero ============ */
.note-editor__hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 26px;
  animation: ne-rise 0.55s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.note-editor__back {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 16px;
  padding: 0;
  border: none;
  background: transparent;
  font-family: inherit;
  font-size: 13px;
  font-weight: 500;
  color: var(--ne-ink-3);
  cursor: pointer;
  transition: color 0.2s ease, transform 0.2s ease;
}

.note-editor__back:hover {
  color: var(--ne-accent);
  transform: translateX(-2px);
}

.note-editor__eyebrow {
  margin: 0 0 10px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: var(--ne-accent);
}

.note-editor__title {
  margin: 0;
  font-size: clamp(26px, 3.6vw, 38px);
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.12;
}

.note-editor__sub {
  margin: 10px 0 0;
  font-size: 14.5px;
  color: var(--ne-ink-2);
}

.note-editor__badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
  padding: 8px 14px;
  border-radius: 999px;
  border: 1px solid var(--ne-line);
  background: var(--theme-bg-glass-strong);
  backdrop-filter: saturate(180%) blur(12px);
  -webkit-backdrop-filter: saturate(180%) blur(12px);
  font-size: 12.5px;
  font-weight: 600;
  color: var(--ne-ink-2);
  box-shadow: var(--theme-shadow-sm);
}

.note-editor__badge-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--theme-success);
  box-shadow: 0 0 0 4px var(--theme-success-soft);
}

/* ============ layout ============ */
.note-editor__form {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 316px;
  gap: 22px;
  align-items: start;
  margin: 0;
}

.note-editor__main {
  display: flex;
  flex-direction: column;
  gap: 18px;
  min-width: 0;
}

.note-editor__rail {
  position: sticky;
  top: 92px;
  animation: ne-rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) 0.12s both;
}

/* ============ cards ============ */
.ne-card {
  position: relative;
  padding: 22px 24px 6px;
  border-radius: 22px;
  background: var(--ne-surface);
  border: 1px solid var(--ne-line);
  box-shadow: var(--theme-shadow-sm);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  animation: ne-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.ne-card:hover {
  border-color: color-mix(in srgb, var(--theme-primary) 20%, var(--ne-line));
  box-shadow: var(--theme-shadow-md);
}

.note-editor__main > .ne-card:nth-child(2) {
  animation-delay: 0.04s;
}

.note-editor__main > .ne-card:nth-child(3) {
  animation-delay: 0.08s;
}

.note-editor__main > .ne-card:nth-child(4) {
  animation-delay: 0.12s;
}

.note-editor__main > .ne-card:nth-child(5) {
  animation-delay: 0.16s;
}

.note-editor__main > .ne-card:nth-child(6) {
  animation-delay: 0.2s;
}

.ne-card__head {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 18px;
}

.ne-card__num {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 12px;
  border: 1px solid color-mix(in srgb, var(--theme-primary) 20%, transparent);
  background: linear-gradient(135deg, var(--theme-primary-soft), color-mix(in srgb, var(--theme-accent-2) 14%, transparent));
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.04em;
  color: var(--ne-accent);
}

.ne-card__head-text {
  min-width: 0;
}

.ne-card__title {
  margin: 0;
  font-size: 16.5px;
  font-weight: 700;
  letter-spacing: -0.01em;
  line-height: 1.3;
}

.ne-card__desc {
  margin: 4px 0 0;
  font-size: 12.5px;
  color: var(--ne-ink-3);
}

.ne-card__aside {
  margin-left: auto;
  align-self: center;
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: 999px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
  font-size: 11.5px;
  font-weight: 600;
  color: var(--ne-ink-3);
  white-space: nowrap;
}

/* ============ form controls ============ */
.note-editor__form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.note-editor__form :deep(.el-form-item__label) {
  padding-bottom: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--ne-ink-2);
  line-height: 1.4;
}

.note-editor__form :deep(.el-input__wrapper),
.note-editor__form :deep(.el-textarea__inner) {
  border-radius: 12px;
  background: var(--theme-bg-surface-2);
  box-shadow: 0 0 0 1px var(--theme-hairline) inset;
  transition: box-shadow 0.22s ease, background 0.22s ease;
}

.note-editor__form :deep(.el-input__wrapper:hover),
.note-editor__form :deep(.el-textarea__inner:hover) {
  box-shadow: 0 0 0 1px color-mix(in srgb, var(--theme-primary) 32%, transparent) inset;
}

.note-editor__form :deep(.el-input__wrapper.is-focus),
.note-editor__form :deep(.el-textarea__inner:focus) {
  background: var(--ne-surface);
  box-shadow: 0 0 0 1px var(--ne-accent) inset, var(--theme-ring);
}

.note-editor__form :deep(.el-input-number) {
  width: 100%;
}

.ne-title-input :deep(.el-input__wrapper) {
  padding: 10px 16px;
}

.ne-title-input :deep(.el-input__inner) {
  height: 30px;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.01em;
}

.ne-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(0, 1fr) minmax(0, 1fr) minmax(0, 1fr);
  gap: 0 16px;
}

/* ============ upload ============ */
.note-editor__upload :deep(.el-upload--picture-card) {
  width: 108px;
  height: 108px;
  border-radius: 16px;
  border: 1.5px dashed color-mix(in srgb, var(--theme-primary) 38%, transparent);
  background: var(--theme-primary-soft);
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
}

.note-editor__upload :deep(.el-upload--picture-card:hover) {
  border-color: var(--ne-accent);
  background: color-mix(in srgb, var(--theme-primary) 14%, transparent);
  transform: translateY(-2px);
}

.note-editor__upload :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 108px;
  height: 108px;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--theme-hairline);
}

.note-editor__upload :deep(.el-upload-list--picture-card .el-upload-list__item:hover) {
  border-color: color-mix(in srgb, var(--theme-primary) 45%, transparent);
}

.ne-upload__trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 11.5px;
  font-weight: 600;
  color: var(--ne-accent);
}

.ne-upload__trigger .el-icon {
  font-size: 20px;
}

.ne-hint {
  margin: 12px 0 0;
  font-size: 12px;
  line-height: 1.6;
  color: var(--ne-ink-3);
}

/* ============ rich editor ============ */
.rich-editor {
  width: 100%;
  margin-bottom: 16px;
  border: 1px solid var(--ne-line);
  border-radius: 16px;
  overflow: hidden;
  background: var(--ne-surface);
  transition: box-shadow 0.25s ease, border-color 0.25s ease;
}

.rich-editor:focus-within {
  border-color: color-mix(in srgb, var(--theme-primary) 55%, transparent);
  box-shadow: var(--theme-ring);
}

.rich-editor__toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 4px;
  padding: 8px 10px;
  border-bottom: 1px solid var(--ne-line);
  background: var(--theme-bg-surface-2);
}

.rich-editor__toolbar button {
  min-width: 32px;
  height: 30px;
  padding: 0 8px;
  border: none;
  border-radius: 8px;
  background: transparent;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  font-family: inherit;
  color: var(--ne-ink-2);
  transition: all 0.18s ease;
}

.rich-editor__toolbar button:hover {
  background: var(--ne-surface);
  color: var(--ne-accent);
  box-shadow: var(--theme-shadow-sm);
}

.rich-editor__toolbar-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.rich-editor__area {
  min-height: 380px;
  max-height: 640px;
  overflow-y: auto;
  padding: 20px 22px;
  font-size: 15.5px;
  line-height: 1.9;
  outline: none;
  color: var(--theme-text-1);
}

.rich-editor__area:empty::before {
  content: attr(data-placeholder);
  color: var(--ne-ink-3);
  pointer-events: none;
}

.rich-editor__area :deep(p) {
  margin: 0 0 12px;
}

.rich-editor__area :deep(h2) {
  margin: 18px 0 10px;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -0.01em;
  line-height: 1.35;
}

.rich-editor__area :deep(ul),
.rich-editor__area :deep(ol) {
  margin: 0 0 12px;
  padding-left: 22px;
}

.rich-editor__area :deep(li) {
  margin: 2px 0;
}

.rich-editor__area :deep(hr) {
  margin: 20px 0;
  border: none;
  border-top: 1px dashed var(--ne-line);
}

.rich-editor__area :deep(img) {
  max-width: 100%;
  border-radius: 12px;
}

.rich-editor__area :deep(blockquote) {
  margin: 10px 0;
  padding: 10px 16px;
  border-left: 3px solid var(--ne-accent);
  background: var(--theme-primary-soft);
  border-radius: 0 10px 10px 0;
  color: var(--ne-ink-2);
}

/* ============ tags ============ */
.ne-tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 18px;
}

.ne-tags :deep(.el-tag) {
  height: 30px;
  padding: 0 12px;
  border: none;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--ne-accent);
  font-weight: 500;
}

.ne-tag-input {
  width: 170px;
}

.ne-tags :deep(.el-button) {
  margin-left: 0;
}

/* ============ rows (relation / route) ============ */
.ne-rows {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 18px;
}

.ne-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  padding: 10px 12px;
  border-radius: 14px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
  transition: border-color 0.22s ease;
}

.ne-row:focus-within {
  border-color: color-mix(in srgb, var(--theme-primary) 32%, transparent);
}

.ne-row :deep(.el-button) {
  margin-left: 0;
}

.ne-relation__type {
  width: 118px;
}

.ne-relation__id {
  width: 140px;
}

.ne-route {
  display: grid;
  grid-template-columns: 88px minmax(120px, 1.2fr) 104px 104px minmax(120px, 1fr) auto;
  gap: 8px;
}

.ne-route :deep(.el-input-number),
.ne-route :deep(.el-input) {
  width: 100%;
}

.ne-route__remove {
  justify-self: end;
}

.ne-add {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 100%;
  height: 44px;
  border: 1.5px dashed var(--theme-border);
  border-radius: 14px;
  background: transparent;
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  color: var(--ne-ink-2);
  cursor: pointer;
  transition: all 0.22s cubic-bezier(0.22, 1, 0.36, 1);
}

.ne-add:hover {
  border-color: var(--ne-accent);
  background: var(--theme-primary-soft);
  color: var(--ne-accent);
}

/* ============ rail ============ */
.ne-rail {
  padding: 16px;
  border-radius: 22px;
  background: var(--ne-surface);
  border: 1px solid var(--ne-line);
  box-shadow: var(--theme-shadow-md);
}

.ne-preview {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface-2);
}

.ne-preview__cover {
  position: relative;
  aspect-ratio: 16 / 10;
  background: linear-gradient(135deg, var(--theme-primary-soft), color-mix(in srgb, var(--theme-accent-2) 12%, transparent));
}

.ne-preview__cover img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ne-preview__cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  color: color-mix(in srgb, var(--theme-primary) 60%, var(--ne-ink-3));
}

.ne-preview__label {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 3px 9px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.42);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #fff;
}

.ne-preview__body {
  padding: 13px 15px 15px;
  background: var(--ne-surface);
}

.ne-preview__title {
  display: -webkit-box;
  margin: 0;
  overflow: hidden;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 15px;
  font-weight: 700;
  line-height: 1.4;
  letter-spacing: -0.01em;
  color: var(--ne-ink);
}

.ne-preview__title.is-placeholder {
  font-weight: 600;
  color: var(--ne-ink-3);
}

.ne-preview__summary {
  display: -webkit-box;
  margin: 7px 0 0;
  overflow: hidden;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 12px;
  line-height: 1.6;
  color: var(--ne-ink-2);
}

.ne-preview__meta {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 12px;
  margin-top: 10px;
  font-size: 11.5px;
  color: var(--ne-ink-3);
}

.ne-preview__meta span {
  display: inline-flex;
  align-items: center;
  gap: 3px;
}

.ne-check {
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px dashed var(--theme-hairline);
}

.ne-check__title {
  margin: 0 0 10px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--ne-ink-3);
}

.ne-check ul {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin: 0;
  padding: 0;
  list-style: none;
}

.ne-check li {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--ne-ink-3);
  transition: color 0.2s ease;
}

.ne-check li::before {
  content: '✓';
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  border: 1.5px solid var(--theme-border-strong);
  border-radius: 50%;
  font-size: 10px;
  font-weight: 700;
  color: transparent;
  transition: all 0.22s ease;
}

.ne-check li.is-done {
  color: var(--ne-ink);
}

.ne-check li.is-done::before {
  border-color: transparent;
  background: var(--theme-success);
  color: #fff;
}

.ne-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 16px;
}

.ne-actions :deep(.el-button) {
  margin-left: 0;
}

.ne-actions__primary {
  width: 100%;
  height: 44px;
  border: none;
  font-weight: 700;
  color: var(--theme-primary-text);
  background: var(--theme-gradient);
  box-shadow: 0 12px 24px -14px color-mix(in srgb, var(--theme-primary) 80%, transparent);
}

.ne-actions__primary:hover {
  color: var(--theme-primary-text);
  filter: brightness(1.04);
  transform: translateY(-1px);
}

.ne-actions__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.ne-actions__row :deep(.el-button) {
  width: 100%;
}

.ne-actions__tip {
  margin: 0;
  font-size: 11.5px;
  line-height: 1.6;
  color: var(--ne-ink-3);
  text-align: center;
}

/* ============ mobile dock ============ */
.note-editor__dock {
  display: none;
}

@keyframes ne-rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 1024px) {
  .note-editor__form {
    grid-template-columns: minmax(0, 1fr);
  }

  .note-editor__rail {
    display: none;
  }

  .note-editor__dock {
    position: fixed;
    bottom: 16px;
    left: 50%;
    z-index: 30;
    display: flex;
    gap: 8px;
    width: calc(100% - 32px);
    max-width: 680px;
    padding: 10px;
    border-radius: 18px;
    border: 1px solid var(--theme-border);
    background: var(--theme-bg-glass-strong);
    backdrop-filter: saturate(180%) blur(16px);
    -webkit-backdrop-filter: saturate(180%) blur(16px);
    box-shadow: var(--theme-shadow-lg);
    transform: translateX(-50%);
  }

  .note-editor__dock :deep(.el-button) {
    flex: 1;
    margin-left: 0;
  }
}

@media (max-width: 760px) {
  .note-editor {
    margin-top: 84px;
    padding: 24px 14px 120px;
  }

  .note-editor__hero {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .ne-card {
    padding: 18px 16px 4px;
    border-radius: 18px;
  }

  .ne-grid {
    grid-template-columns: 1fr 1fr;
  }

  .ne-route {
    grid-template-columns: 1fr 1fr;
  }

  .ne-route__remove {
    grid-column: 2;
  }

  .rich-editor__area {
    min-height: 300px;
    padding: 16px;
  }
}

@media (max-width: 520px) {
  .ne-grid {
    grid-template-columns: 1fr;
  }

  .ne-tag-input {
    width: 140px;
  }

  .note-editor__dock :deep(.el-button) {
    padding-right: 10px;
    padding-left: 10px;
    font-size: 12.5px;
  }
}
</style>
