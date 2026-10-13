<template>
  <div class="note-editor">
    <h2 class="note-editor__title">{{ isEdit ? '编辑笔记' : '发布旅游笔记' }}</h2>

    <el-form :model="form" label-position="top" class="note-editor__form">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" maxlength="120" show-word-limit placeholder="一句话介绍这次旅行" />
      </el-form-item>

      <el-form-item label="摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" maxlength="300" show-word-limit
          placeholder="将展示在卡片上，建议 50 字以内" />
      </el-form-item>

      <div class="note-editor__grid">
        <el-form-item label="目的地">
          <el-input v-model="form.destination" maxlength="100" placeholder="如：云南 · 大理" />
        </el-form-item>
        <el-form-item label="出行日期">
          <el-date-picker v-model="form.travelDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期"
            style="width: 100%" />
        </el-form-item>
        <el-form-item label="旅行天数">
          <el-input-number v-model="form.travelDays" :min="1" :max="365" style="width: 100%" />
        </el-form-item>
        <el-form-item label="人均花费（元）">
          <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </div>

      <el-form-item label="封面与图集">
        <div class="note-editor__upload">
          <el-upload v-model:file-list="fileList" list-type="picture-card" :auto-upload="true"
            :http-request="handleUpload" :before-upload="beforeUpload" accept="image/*" multiple :limit="18"
            @remove="handleRemove">
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="note-editor__hint">第一张图将默认作为封面，拖动无需求时上传即按顺序排列</div>
        </div>
      </el-form-item>

      <el-form-item label="正文">
        <div class="rich-editor">
          <div class="rich-editor__toolbar">
            <button type="button" title="加粗" @click="exec('bold')"><b>B</b></button>
            <button type="button" title="斜体" @click="exec('italic')"><i>I</i></button>
            <button type="button" title="标题" @click="exec('formatBlock', 'h2')">H2</button>
            <button type="button" title="引用" @click="exec('formatBlock', 'blockquote')">引</button>
            <button type="button" title="无序列表" @click="exec('insertUnorderedList')">•</button>
            <button type="button" title="有序列表" @click="exec('insertOrderedList')">1.</button>
            <button type="button" title="分隔线" @click="exec('insertHorizontalRule')">—</button>
            <button type="button" title="插入图片" @click="triggerImageUpload">图</button>
          </div>
          <div ref="editorRef" class="rich-editor__area" contenteditable="true" data-placeholder="开始记录你的旅程..."
            @input="syncContent" />
          <input ref="imageInputRef" type="file" accept="image/*" hidden @change="handleEditorImage" />
        </div>
      </el-form-item>

      <el-form-item label="标签（最多10个）">
        <div class="note-editor__tags">
          <el-tag v-for="tag in form.tags" :key="tag" closable @close="removeTag(tag)"># {{ tag }}</el-tag>
          <el-input v-model="tagInput" class="note-editor__tag-input" size="small" maxlength="20"
            placeholder="输入后回车" @keyup.enter="addTag" />
          <el-button size="small" @click="addTag">添加</el-button>
        </div>
      </el-form-item>

      <el-form-item label="关联（景点/酒店/美食/商品）">
        <div class="note-editor__relations">
          <div v-for="(rel, idx) in form.relations" :key="idx" class="note-editor__relation">
            <el-select v-model="rel.bizType" style="width: 110px">
              <el-option label="景点" :value="1" />
              <el-option label="酒店" :value="2" />
              <el-option label="美食" :value="3" />
              <el-option label="商品" :value="4" />
            </el-select>
            <el-input-number v-model="rel.bizId" :min="1" controls-position="right" placeholder="ID"
              style="width: 130px" />
            <el-button text type="danger" @click="form.relations.splice(idx, 1)">移除</el-button>
          </div>
          <el-button size="small" @click="form.relations.push({ bizType: 1, bizId: null })">
            <el-icon class="mr-1"><Plus /></el-icon>添加关联
          </el-button>
        </div>
      </el-form-item>

      <el-form-item label="行程轨迹（可选）">
        <div class="note-editor__routes">
          <div v-for="(route, idx) in form.routes" :key="idx" class="note-editor__route">
            <el-input-number v-model="route.dayIndex" :min="1" controls-position="right" placeholder="天"
              style="width: 90px" />
            <el-input v-model="route.poiName" maxlength="120" placeholder="地点名称" style="flex: 1" />
            <el-input v-model="route.longitude" placeholder="经度" style="width: 120px" />
            <el-input v-model="route.latitude" placeholder="纬度" style="width: 120px" />
            <el-input v-model="route.remark" maxlength="255" placeholder="备注" style="width: 160px" />
            <el-button text type="danger" @click="form.routes.splice(idx, 1)">移除</el-button>
          </div>
          <el-button size="small" @click="form.routes.push({ dayIndex: form.routes.length + 1, poiName: '', longitude: '', latitude: '', remark: '' })">
            <el-icon class="mr-1"><Plus /></el-icon>添加节点
          </el-button>
        </div>
      </el-form-item>

      <div class="note-editor__submit">
        <el-button :loading="saving" @click="save(0)">保存草稿</el-button>
        <el-button :loading="saving" @click="save(5)">设为私密</el-button>
        <el-button type="primary" :loading="saving" @click="save(1)">提交审核</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { createNote, getNoteDetail, updateNote } from '@/api/travelNote'
import { uploadFile } from '@/api/upload'
import { useAuthStore } from '@/stores/auth'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

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

onMounted(() => {
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
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
    ElMessage.error(res.message || '笔记不存在')
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

// 数据变更后自动刷新
useAutoRefresh(loadForEdit)

// ---------- 上传 ----------
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) ElMessage.error('只能上传图片')
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
      ElMessage.error(res.message || '上传失败')
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
      ElMessage.error(res.message || '图片上传失败')
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
    ElMessage.warning('标签已存在')
    return
  }
  if (form.value.tags.length >= 10) {
    ElMessage.warning('最多10个标签')
    return
  }
  form.value.tags.push(tag)
  tagInput.value = ''
}

function removeTag(tag) {
  form.value.tags = form.value.tags.filter((t) => t !== tag)
}

// ---------- 保存 ----------
async function save(status) {
  if (!form.value.title.trim()) {
    ElMessage.warning('请填写标题')
    return
  }
  syncContent()
  if (status === 1 && !form.value.content) {
    ElMessage.warning('提交审核前请填写正文')
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
        status === 0 ? '草稿已保存' : status === 5 ? '已设为私密' : '已提交审核，审核通过后将发布'
      )
      router.push('/travel-note/mine')
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
/* ============ shell ============ */
.note-editor {
  --tn-ink: var(--theme-text-1);
  --tn-ink-2: var(--theme-text-2);
  --tn-ink-3: var(--theme-text-3);
  --tn-accent: var(--theme-primary);
  --tn-accent-2: var(--theme-accent-2);
  --tn-line: var(--theme-border);
  --tn-font: 'SF Pro Display', 'PingFang SC', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;

  position: relative;
  min-height: 100vh;
  max-width: 900px;
  margin: 0 auto;
  margin-top: 100px;
  padding: 40px 20px 96px;
  font-family: var(--tn-font);
  color: var(--tn-ink);
}

.note-editor::before {
  content: '';
  position: fixed;
  inset: 0;
  z-index: -1;
  background:
    radial-gradient(900px 460px at 8% -10%, var(--theme-primary-soft), transparent 60%),
    radial-gradient(760px 420px at 100% 0%, color-mix(in srgb, var(--theme-accent-2) 9%, transparent), transparent 58%),
    var(--theme-bg-surface-2);
}

.note-editor__title {
  margin: 0 0 22px;
  font-size: clamp(24px, 3.4vw, 34px);
  font-weight: 800;
  letter-spacing: -0.02em;
  animation: ne-rise 0.5s cubic-bezier(0.22, 1, 0.36, 1) both;
}

/* form card */
.note-editor__form {
  padding: 28px 30px 24px;
  border-radius: 24px;
  background: var(--theme-bg-surface);
  border: 1px solid var(--tn-line);
  box-shadow: var(--theme-shadow-lg);
  animation: ne-rise 0.55s cubic-bezier(0.22, 1, 0.36, 1) 0.05s both;
}

.note-editor__form :deep(.el-form-item__label) {
  font-weight: 600;
  color: var(--tn-ink);
}

.note-editor__form :deep(.el-input__wrapper),
.note-editor__form :deep(.el-textarea__inner),
.note-editor__form :deep(.el-input-number) {
  border-radius: 12px;
}

.note-editor__grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 0 24px;
}

/* ============ upload ============ */
.note-editor__upload :deep(.el-upload--picture-card) {
  width: 104px;
  height: 104px;
  border-radius: 16px;
  border: 1.5px dashed color-mix(in srgb, var(--theme-primary) 40%, transparent);
  background: var(--theme-primary-soft);
  transition: all 0.25s cubic-bezier(0.22, 1, 0.36, 1);
}

.note-editor__upload :deep(.el-upload--picture-card:hover) {
  border-color: var(--tn-accent);
  background: var(--theme-primary-soft);
  transform: translateY(-2px);
}

.note-editor__upload :deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 104px;
  height: 104px;
  border-radius: 16px;
  overflow: hidden;
}

.note-editor__hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--tn-ink-3);
}

/* ============ rich editor ============ */
.rich-editor {
  width: 100%;
  border: 1px solid var(--tn-line);
  border-radius: 16px;
  overflow: hidden;
  background: var(--theme-bg-surface);
  transition: box-shadow 0.25s ease, border-color 0.25s ease;
}

.rich-editor:focus-within {
  border-color: color-mix(in srgb, var(--theme-primary) 55%, transparent);
  box-shadow: var(--theme-ring);
}

.rich-editor__toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 8px 10px;
  border-bottom: 1px solid var(--tn-line);
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
  color: var(--tn-ink-2);
  transition: all 0.18s ease;
  font-family: inherit;
}

.rich-editor__toolbar button:hover {
  background: var(--theme-bg-surface);
  color: var(--tn-accent);
  box-shadow: var(--theme-shadow-sm);
}

.rich-editor__area {
  min-height: 340px;
  max-height: 580px;
  overflow-y: auto;
  padding: 18px 20px;
  font-size: 15.5px;
  line-height: 1.9;
  outline: none;
  color: var(--theme-text-1);
}

.rich-editor__area:empty::before {
  content: attr(data-placeholder);
  color: var(--tn-ink-3);
}

.rich-editor__area :deep(img) {
  max-width: 100%;
  border-radius: 12px;
}

.rich-editor__area :deep(blockquote) {
  margin: 10px 0;
  padding: 8px 14px;
  border-left: 3px solid var(--tn-accent);
  background: var(--theme-primary-soft);
  border-radius: 0 10px 10px 0;
  color: var(--tn-ink-2);
}

/* ============ tags ============ */
.note-editor__tags {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.note-editor__tags :deep(.el-tag) {
  border: none;
  background: var(--theme-primary-soft);
  color: var(--tn-accent);
  border-radius: 999px;
  font-weight: 500;
}

.note-editor__tag-input {
  width: 150px;
}

/* ============ relation / route rows ============ */
.note-editor__relations,
.note-editor__routes {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

.note-editor__relation,
.note-editor__route {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  padding: 10px 12px;
  border-radius: 14px;
  background: var(--theme-bg-surface-2);
  border: 1px solid var(--theme-hairline);
}

/* ============ submit bar ============ */
.note-editor__submit {
  position: sticky;
  bottom: 16px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
  padding: 12px 16px;
  border-radius: 18px;
  background: var(--theme-bg-glass-strong);
  backdrop-filter: saturate(180%) blur(16px);
  -webkit-backdrop-filter: saturate(180%) blur(16px);
  border: 1px solid var(--theme-border);
  box-shadow: var(--theme-shadow-lg);
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

@media (max-width: 720px) {
  .note-editor {
    margin-top: 84px;
    padding: 20px 12px 88px;
  }
  .note-editor__form {
    padding: 20px 18px;
    border-radius: 20px;
  }
}
</style>
