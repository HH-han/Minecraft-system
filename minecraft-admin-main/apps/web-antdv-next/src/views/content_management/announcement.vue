<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, nextTick, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Plus, RefreshCw, Search } from '@vben/icons';

import {
  Button,
  Descriptions,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Select,
  Switch,
  Table,
  Tag,
} from 'antdv-next';

import {
  addAnnouncement,
  deleteAnnouncement,
  getAnnouncementPage,
  getReadStat,
  offlineAnnouncement,
  publishAnnouncement,
  topAnnouncement,
  updateAnnouncement,
} from '#/api/management/content/announcement';

defineOptions({ name: 'AnnouncementManagement' });

interface AnnouncementInfo {
  content?: null | string;
  coverImage?: null | string;
  creatorName?: null | string;
  displayMode?: null | number;
  expireTime?: null | string;
  id: number;
  isTop?: null | number;
  level?: null | number;
  publishTime?: null | string;
  readCount?: null | number;
  sortWeight?: null | number;
  status?: null | number;
  summary?: null | string;
  targetAudience?: null | number;
  title?: null | string;
  type?: null | number;
  viewCount?: null | number;
}

const TYPE_OPTIONS = [
  { value: 1, label: '系统公告' },
  { value: 2, label: '活动通知' },
  { value: 3, label: '维护通知' },
  { value: 4, label: '版本更新' },
];
const LEVEL_OPTIONS = [
  { value: 1, label: '普通' },
  { value: 2, label: '重要' },
  { value: 3, label: '紧急' },
];
const DISPLAY_MODES = [
  { value: 1, label: '仅列表' },
  { value: 2, label: '弹窗' },
  { value: 3, label: '轮播' },
  { value: 4, label: '顶部横幅' },
];
const AUDIENCES = [
  { value: 1, label: '全体' },
  { value: 2, label: '登录用户' },
  { value: 3, label: '新用户' },
  { value: 4, label: '指定用户' },
];
const STATUS_OPTIONS = [
  { value: 0, label: '草稿' },
  { value: 1, label: '已发布' },
  { value: 2, label: '已下架' },
];

// =========================
// 状态
// =========================
const loading = ref(false);
const rows = ref<AnnouncementInfo[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref('');
const statusFilter = ref<null | number>(null);
const typeFilter = ref<null | number>(null);

const showDialog = ref(false);
const isEditing = ref(false);
const submitting = ref(false);
const editorRef = ref<HTMLElement>();
const sourceMode = ref(false);
const sourceContent = ref('');
const targetUserIdsText = ref('');

const statVisible = ref(false);
const stat = reactive({ readCount: 0, readRate: 0, title: '', viewCount: 0 });

const emptyForm = () => ({
  content: '',
  coverImage: '',
  displayMode: 1,
  expireTime: null as null | string,
  id: null as null | number,
  isTop: 0,
  level: 1,
  publishTime: null as null | string,
  sortWeight: 0,
  status: 0,
  summary: '',
  targetAudience: 1,
  title: '',
  type: 1,
});
const form = ref(emptyForm());

const futurePublish = computed(
  () =>
    !!form.value.publishTime &&
    new Date(form.value.publishTime).getTime() > Date.now(),
);

// =========================
// 工具
// =========================
function typeText(t?: null | number): string {
  return TYPE_OPTIONS.find((o) => o.value === Number(t))?.label ?? '-';
}
function typeColor(t?: null | number): string {
  return { 1: 'blue', 2: 'orange', 3: 'red', 4: 'green' }[Number(t)] ?? 'default';
}
function levelText(l?: null | number): string {
  return LEVEL_OPTIONS.find((o) => o.value === Number(l))?.label ?? '-';
}
function levelColor(l?: null | number): string {
  return { 1: 'default', 2: 'orange', 3: 'red' }[Number(l)] ?? 'default';
}
function statusText(s?: null | number): string {
  return STATUS_OPTIONS.find((o) => o.value === Number(s))?.label ?? '-';
}
function statusColor(s?: null | number): string {
  return { 0: 'default', 1: 'green', 2: 'red' }[Number(s)] ?? 'default';
}
function displayModeText(m?: null | number): string {
  return DISPLAY_MODES.find((o) => o.value === Number(m))?.label ?? '-';
}
function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}
function formatStatTime(value?: null | string): string {
  if (!value) return '-';
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? '-' : d.toLocaleString('zh-CN', { hour12: false });
}

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: '标题', key: 'title', width: 220, ellipsis: true },
  { title: '类型', key: 'type', width: 100 },
  { title: '级别', key: 'level', width: 80 },
  { title: '展示方式', key: 'displayMode', width: 100 },
  { title: '状态', key: 'status', width: 90 },
  { title: '浏览量', dataIndex: 'viewCount', width: 90 },
  { title: '已读数', dataIndex: 'readCount', width: 90 },
  { title: '发布时间', key: 'publishTime', width: 160 },
  { title: '过期时间', key: 'expireTime', width: 160 },
  { title: '创建人', dataIndex: 'creatorName', width: 100, ellipsis: true },
  { title: '操作', key: 'action', width: 300, fixed: 'right' },
]);

// =========================
// 数据加载（服务端分页）
// =========================
async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getAnnouncementPage({
      page: pageNum.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      status: statusFilter.value ?? undefined,
      type: typeFilter.value ?? undefined,
    });
    rows.value = res?.records ?? [];
    total.value = Number(res?.total ?? 0);
  } catch {
    rows.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pageNum.value = 1;
  fetchData();
}
function handleRefresh() {
  keyword.value = '';
  statusFilter.value = null;
  typeFilter.value = null;
  pageNum.value = 1;
  fetchData();
}
function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

// =========================
// 新建 / 编辑
// =========================
function showAddDialog() {
  isEditing.value = false;
  form.value = emptyForm();
  targetUserIdsText.value = '';
  sourceMode.value = false;
  sourceContent.value = '';
  showDialog.value = true;
  nextTick(() => {
    if (editorRef.value) editorRef.value.innerHTML = '';
  });
}

function showEditDialog(row: AnnouncementInfo) {
  isEditing.value = true;
  form.value = { ...emptyForm(), ...row };
  targetUserIdsText.value = '';
  sourceMode.value = false;
  sourceContent.value = row.content ?? '';
  showDialog.value = true;
  nextTick(() => {
    if (editorRef.value) editorRef.value.innerHTML = row.content ?? '';
  });
}

// ---- 富文本编辑器 ----
function exec(command: string, value: string | null = null) {
  if (sourceMode.value) return;
  editorRef.value?.focus();
  document.execCommand(command, false, value);
  if (editorRef.value) form.value.content = editorRef.value.innerHTML;
}
function onEditorInput() {
  if (editorRef.value) form.value.content = editorRef.value.innerHTML;
}
function toggleSource() {
  if (!sourceMode.value) {
    if (editorRef.value) form.value.content = editorRef.value.innerHTML;
    sourceContent.value = form.value.content;
    sourceMode.value = true;
  } else {
    sourceMode.value = false;
    form.value.content = sourceContent.value;
    nextTick(() => {
      if (editorRef.value) editorRef.value.innerHTML = sourceContent.value;
    });
  }
}
function insertLink() {
  const url = window.prompt('请输入链接地址：', 'https://');
  if (url) exec('createLink', url);
}
function insertImage() {
  const url = window.prompt('请输入图片地址：', 'https://');
  if (url) exec('insertImage', url);
}

// ---- 提交 ----
async function submit(status: 0 | 1) {
  if (sourceMode.value) form.value.content = sourceContent.value;
  if (!form.value.title?.trim()) {
    message.error('请填写公告标题');
    return;
  }
  const plain = (form.value.content ?? '').replace(/<[^>]+>/g, '').trim();
  if (!plain) {
    message.error('请填写公告内容');
    return;
  }
  if (
    form.value.expireTime &&
    form.value.publishTime &&
    new Date(form.value.expireTime) <= new Date(form.value.publishTime)
  ) {
    message.error('过期时间必须晚于发布时间');
    return;
  }
  submitting.value = true;
  try {
    const data: any = {
      ...form.value,
      status,
      targetUserIds:
        form.value.targetAudience === 4
          ? targetUserIdsText.value
              .split(',')
              .map((s) => s.trim())
              .filter((s) => s && !Number.isNaN(Number(s)))
              .map(Number)
          : undefined,
    };
    if (isEditing.value && form.value.id != null) {
      await updateAnnouncement(form.value.id, data);
    } else {
      await addAnnouncement(data);
    }
    message.success(futurePublish.value ? '定时发布已保存' : status === 1 ? '发布成功' : '草稿已保存');
    showDialog.value = false;
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || '操作失败');
  } finally {
    submitting.value = false;
  }
}

// =========================
// 行操作
// =========================
async function handlePublish(row: AnnouncementInfo) {
  try {
    await publishAnnouncement(row.id);
    message.success('发布成功');
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || '发布失败');
  }
}
async function handleOffline(row: AnnouncementInfo) {
  try {
    await offlineAnnouncement(row.id);
    message.success('下架成功');
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || '下架失败');
  }
}
async function handleTop(row: AnnouncementInfo) {
  try {
    await topAnnouncement(row.id, Number(row.isTop) === 1 ? 0 : 1);
    message.success(Number(row.isTop) === 1 ? '已取消置顶' : '置顶成功');
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || '操作失败');
  }
}
async function showStatDialog(row: AnnouncementInfo) {
  try {
    const res: any = await getReadStat(row.id);
    Object.assign(stat, {
      readCount: res?.readCount ?? 0,
      readRate: res?.readRate ?? 0,
      title: res?.title ?? row.title,
      viewCount: res?.viewCount ?? 0,
    });
    statVisible.value = true;
  } catch {
    message.error('获取统计失败');
  }
}
async function handleDelete(row: AnnouncementInfo) {
  try {
    await deleteAnnouncement(row.id);
    message.success('删除成功');
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || '删除失败');
  }
}

onMounted(fetchData);
</script>

<template>
  <Page title="公告管理" description="系统公告的创建、发布、下架与阅读统计">
    <div class="bg-card rounded-lg p-4">
      <!-- 搜索/操作栏 -->
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          placeholder="输入公告标题搜索"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          搜索
        </Button>
        <Select
          v-model:value="statusFilter"
          placeholder="状态"
          allow-clear
          class="w-28"
          :options="STATUS_OPTIONS"
          @change="handleSearch"
        />
        <Select
          v-model:value="typeFilter"
          placeholder="类型"
          allow-clear
          class="w-28"
          :options="TYPE_OPTIONS"
          @change="handleSearch"
        />
        <div class="flex-1"></div>
        <Button @click="handleRefresh">
          <template #icon><RefreshCw class="size-4" /></template>
          重置
        </Button>
        <Button type="primary" @click="showAddDialog">
          <template #icon><Plus class="size-4" /></template>
          新建公告
        </Button>
      </div>

      <!-- 数据表格 -->
      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (t: number) => `共 ${t} 条`,
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1560 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <Tag v-if="record.isTop === 1" color="volcano">置顶</Tag>
            <span>{{ record.title }}</span>
          </template>
          <template v-else-if="column.key === 'type'">
            <Tag :color="typeColor(record.type)">{{ typeText(record.type) }}</Tag>
          </template>
          <template v-else-if="column.key === 'level'">
            <Tag :color="levelColor(record.level)">{{ levelText(record.level) }}</Tag>
          </template>
          <template v-else-if="column.key === 'displayMode'">
            {{ displayModeText(record.displayMode) }}
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">{{ statusText(record.status) }}</Tag>
          </template>
          <template v-else-if="column.key === 'publishTime'">
            {{ formatTime(record.publishTime) }}
          </template>
          <template v-else-if="column.key === 'expireTime'">
            {{ formatTime(record.expireTime) }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Button type="link" size="small" @click="showEditDialog(record)">编辑</Button>
            <Button
              v-if="record.status !== 1"
              type="link"
              size="small"
              @click="handlePublish(record)"
            >
              发布
            </Button>
            <Button
              v-if="record.status === 1"
              type="link"
              size="small"
              @click="handleOffline(record)"
            >
              下架
            </Button>
            <Button type="link" size="small" @click="handleTop(record)">
              {{ record.isTop === 1 ? '取消置顶' : '置顶' }}
            </Button>
            <Button type="link" size="small" @click="showStatDialog(record)">统计</Button>
            <Popconfirm title="确认删除该公告？" @confirm="handleDelete(record)">
              <Button type="link" size="small" danger>删除</Button>
            </Popconfirm>
          </template>
        </template>
      </Table>
    </div>

    <!-- 新建/编辑弹窗 -->
    <Modal
      v-model:open="showDialog"
      :title="isEditing ? '编辑公告' : '新建公告'"
      width="760px"
      :mask-closable="false"
      destroy-on-close
    >
      <Form layout="horizontal" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem label="公告标题" required>
          <Input v-model:value="form.title" :maxlength="200" show-count placeholder="请输入公告标题" />
        </FormItem>
        <FormItem label="公告摘要">
          <Input.TextArea
            v-model:value="form.summary"
            :rows="2"
            :maxlength="500"
            show-count
            placeholder="列表/弹窗摘要（可留空）"
          />
        </FormItem>
        <FormItem label="类型 / 级别">
          <div class="flex gap-2">
            <Select v-model:value="form.type" class="w-1/2" :options="TYPE_OPTIONS" />
            <Select v-model:value="form.level" class="w-1/2" :options="LEVEL_OPTIONS" />
          </div>
        </FormItem>
        <FormItem label="展示方式">
          <Select v-model:value="form.displayMode" :options="DISPLAY_MODES" />
        </FormItem>
        <FormItem label="目标人群">
          <Select v-model:value="form.targetAudience" :options="AUDIENCES" />
        </FormItem>
        <FormItem v-if="form.targetAudience === 4" label="定向用户ID">
          <Input v-model:value="targetUserIdsText" placeholder="多个用户ID用英文逗号分隔，如 1,2,3" />
        </FormItem>
        <FormItem label="定时发布">
          <Input
            v-model:value="form.publishTime"
            placeholder="ISO 格式，留空且点立即发布则为立即发布，如 2026-01-15T10:00:00"
          />
        </FormItem>
        <FormItem label="过期时间">
          <Input v-model:value="form.expireTime" placeholder="ISO 格式，留空=永不过期" />
        </FormItem>
        <FormItem label="封面图URL">
          <Input v-model:value="form.coverImage" placeholder="https://..." />
        </FormItem>
        <FormItem label="置顶 / 权重">
          <div class="flex items-center gap-4">
            <Switch :checked="form.isTop === 1" @change="(v: any) => (form.isTop = v ? 1 : 0)" />
            <InputNumber v-model:value="form.sortWeight" :min="0" :max="9999" />
          </div>
        </FormItem>
        <FormItem label="公告内容" required>
          <div class="rich-editor w-full">
            <div class="editor-toolbar">
              <button type="button" title="加粗" @click="exec('bold')"><b>B</b></button>
              <button type="button" title="斜体" @click="exec('italic')"><i>I</i></button>
              <button type="button" title="下划线" @click="exec('underline')"><u>U</u></button>
              <button type="button" title="标题" @click="exec('formatBlock', 'h2')">H2</button>
              <button type="button" title="正文" @click="exec('formatBlock', 'p')">P</button>
              <button type="button" title="无序列表" @click="exec('insertUnorderedList')">•≡</button>
              <button type="button" title="有序列表" @click="exec('insertOrderedList')">1≡</button>
              <button type="button" title="插入链接" @click="insertLink">链接</button>
              <button type="button" title="插入图片" @click="insertImage">图片</button>
              <button type="button" title="清除格式" @click="exec('removeFormat')">清除</button>
              <button
                type="button"
                title="HTML 源码"
                :class="{ active: sourceMode }"
                @click="toggleSource"
              >
                { }
              </button>
            </div>
            <textarea
              v-if="sourceMode"
              v-model="sourceContent"
              class="editor-source"
              spellcheck="false"
            ></textarea>
            <div
              v-else
              ref="editorRef"
              class="editor-body"
              contenteditable="true"
              @input="onEditorInput"
            ></div>
          </div>
        </FormItem>
      </Form>
      <template #footer>
        <Button @click="showDialog = false">取消</Button>
        <Button :disabled="submitting" @click="submit(0)">存为草稿</Button>
        <Button type="primary" :loading="submitting" @click="submit(1)">
          {{ futurePublish ? '定时发布' : '立即发布' }}
        </Button>
      </template>
    </Modal>

    <!-- 阅读统计弹窗 -->
    <Modal v-model:open="statVisible" title="阅读统计" width="440px" :footer="null">
      <Descriptions :column="1" bordered size="small">
        <Descriptions.Item label="公告标题">{{ stat.title }}</Descriptions.Item>
        <Descriptions.Item label="浏览量">{{ stat.viewCount }}</Descriptions.Item>
        <Descriptions.Item label="已读数">{{ stat.readCount }}</Descriptions.Item>
        <Descriptions.Item label="阅读率">{{ stat.readRate }}%</Descriptions.Item>
      </Descriptions>
    </Modal>
  </Page>
</template>

<style scoped>
.rich-editor {
  overflow: hidden;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
}

.editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  padding: 6px 8px;
  border-bottom: 1px solid #eee;
  background: #f5f7fa;
}

.editor-toolbar button {
  min-width: 30px;
  height: 28px;
  border: 1px solid transparent;
  border-radius: 4px;
  background: transparent;
  color: #444;
  font-size: 13px;
  cursor: pointer;
}

.editor-toolbar button:hover {
  background: #e8ebf0;
}

.editor-toolbar button.active {
  border-color: #1677ff;
  color: #1677ff;
}

.editor-body {
  min-height: 200px;
  max-height: 320px;
  padding: 12px 14px;
  overflow-y: auto;
  font-size: 14px;
  line-height: 1.8;
  background: #fff;
  outline: none;
}

.editor-body:empty::before {
  color: #bbb;
  content: '请输入公告内容...';
}

.editor-source {
  box-sizing: border-box;
  min-height: 200px;
  max-height: 320px;
  padding: 12px 14px;
  border: none;
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  line-height: 1.6;
  resize: vertical;
  outline: none;
}
</style>
