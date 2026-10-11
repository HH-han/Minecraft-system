<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, nextTick, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Plus, RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

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

const typeOptions = computed(() => [
  { value: 1, label: $t('content.announcement_page.type.system') },
  { value: 2, label: $t('content.announcement_page.type.activity') },
  { value: 3, label: $t('content.announcement_page.type.maintenance') },
  { value: 4, label: $t('content.announcement_page.type.version') },
]);
const levelOptions = computed(() => [
  { value: 1, label: $t('content.announcement_page.level.normal') },
  { value: 2, label: $t('content.announcement_page.level.important') },
  { value: 3, label: $t('content.announcement_page.level.urgent') },
]);
const displayModes = computed(() => [
  { value: 1, label: $t('content.announcement_page.displayMode.list') },
  { value: 2, label: $t('content.announcement_page.displayMode.popup') },
  { value: 3, label: $t('content.announcement_page.displayMode.carousel') },
  { value: 4, label: $t('content.announcement_page.displayMode.banner') },
]);
const audiences = computed(() => [
  { value: 1, label: $t('content.announcement_page.audience.all') },
  { value: 2, label: $t('content.announcement_page.audience.login') },
  { value: 3, label: $t('content.announcement_page.audience.new') },
  { value: 4, label: $t('content.announcement_page.audience.specified') },
]);
const statusOptions = computed(() => [
  { value: 0, label: $t('content.announcement_page.status.draft') },
  { value: 1, label: $t('content.announcement_page.status.published') },
  { value: 2, label: $t('content.announcement_page.status.offline') },
]);

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
  return typeOptions.value.find((o) => o.value === Number(t))?.label ?? '-';
}
function typeColor(t?: null | number): string {
  return { 1: 'blue', 2: 'orange', 3: 'red', 4: 'green' }[Number(t)] ?? 'default';
}
function levelText(l?: null | number): string {
  return levelOptions.value.find((o) => o.value === Number(l))?.label ?? '-';
}
function levelColor(l?: null | number): string {
  return { 1: 'default', 2: 'orange', 3: 'red' }[Number(l)] ?? 'default';
}
function statusText(s?: null | number): string {
  return statusOptions.value.find((o) => o.value === Number(s))?.label ?? '-';
}
function statusColor(s?: null | number): string {
  return { 0: 'default', 1: 'green', 2: 'red' }[Number(s)] ?? 'default';
}
function displayModeText(m?: null | number): string {
  return displayModes.value.find((o) => o.value === Number(m))?.label ?? '-';
}
function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}

// =========================
// 列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  {
    title: $t('content.announcement_page.columns.title'),
    key: 'title',
    width: 220,
    ellipsis: true,
  },
  { title: $t('content.announcement_page.columns.type'), key: 'type', width: 100 },
  { title: $t('content.announcement_page.columns.level'), key: 'level', width: 80 },
  {
    title: $t('content.announcement_page.columns.displayMode'),
    key: 'displayMode',
    width: 100,
  },
  {
    title: $t('content.announcement_page.columns.status'),
    key: 'status',
    width: 90,
  },
  {
    title: $t('content.announcement_page.columns.viewCount'),
    dataIndex: 'viewCount',
    width: 90,
  },
  {
    title: $t('content.announcement_page.columns.readCount'),
    dataIndex: 'readCount',
    width: 90,
  },
  {
    title: $t('content.announcement_page.columns.publishTime'),
    key: 'publishTime',
    width: 160,
  },
  {
    title: $t('content.announcement_page.columns.expireTime'),
    key: 'expireTime',
    width: 160,
  },
  {
    title: $t('content.announcement_page.columns.creatorName'),
    dataIndex: 'creatorName',
    width: 100,
    ellipsis: true,
  },
  {
    title: $t('content.announcement_page.columns.action'),
    key: 'action',
    width: 300,
    fixed: 'right',
  },
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
  const url = window.prompt(
    $t('content.announcement_page.link_prompt'),
    'https://',
  );
  if (url) exec('createLink', url);
}
function insertImage() {
  const url = window.prompt(
    $t('content.announcement_page.image_prompt'),
    'https://',
  );
  if (url) exec('insertImage', url);
}

// ---- 提交 ----
async function submit(status: 0 | 1) {
  if (sourceMode.value) form.value.content = sourceContent.value;
  if (!form.value.title?.trim()) {
    message.error($t('content.announcement_page.title_required'));
    return;
  }
  const plain = (form.value.content ?? '').replace(/<[^>]+>/g, '').trim();
  if (!plain) {
    message.error($t('content.announcement_page.content_required'));
    return;
  }
  if (
    form.value.expireTime &&
    form.value.publishTime &&
    new Date(form.value.expireTime) <= new Date(form.value.publishTime)
  ) {
    message.error($t('content.announcement_page.expire_before_publish'));
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
    message.success(
      futurePublish.value
        ? $t('content.announcement_page.scheduled_success')
        : status === 1
          ? $t('content.announcement_page.publish_success')
          : $t('content.announcement_page.save_draft_success'),
    );
    showDialog.value = false;
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('content.announcement_page.action_failed'));
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
    message.success($t('content.announcement_page.publish_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('content.announcement_page.publish_failed'));
  }
}
async function handleOffline(row: AnnouncementInfo) {
  try {
    await offlineAnnouncement(row.id);
    message.success($t('content.announcement_page.offline_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('content.announcement_page.offline_failed'));
  }
}
async function handleTop(row: AnnouncementInfo) {
  try {
    await topAnnouncement(row.id, Number(row.isTop) === 1 ? 0 : 1);
    message.success(
      Number(row.isTop) === 1
        ? $t('content.announcement_page.cancel_top_success')
        : $t('content.announcement_page.top_success'),
    );
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('content.announcement_page.action_failed'));
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
    message.error($t('content.announcement_page.stat_failed'));
  }
}
async function handleDelete(row: AnnouncementInfo) {
  try {
    await deleteAnnouncement(row.id);
    message.success($t('content.announcement_page.delete_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('content.announcement_page.delete_failed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page
    :title="$t('content.announcement_page.title')"
    :description="$t('content.announcement_page.description')"
  >
    <div class="bg-card rounded-lg p-4">
      <!-- 搜索/操作栏 -->
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('content.announcement_page.search_placeholder')"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('content.announcement_page.search') }}
        </Button>
        <Select
          v-model:value="statusFilter"
          :placeholder="$t('content.announcement_page.filter.status')"
          allow-clear
          class="w-28"
          :options="statusOptions"
          @change="handleSearch"
        />
        <Select
          v-model:value="typeFilter"
          :placeholder="$t('content.announcement_page.filter.type')"
          allow-clear
          class="w-28"
          :options="typeOptions"
          @change="handleSearch"
        />
        <div class="flex-1"></div>
        <Button @click="handleRefresh">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('content.announcement_page.reset') }}
        </Button>
        <Button type="primary" @click="showAddDialog">
          <template #icon><Plus class="size-4" /></template>
          {{ $t('content.announcement_page.add') }}
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
          showTotal: (t: number) =>
            $t('content.announcement_page.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1560 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <Tag v-if="record.isTop === 1" color="volcano">
              {{ $t('content.announcement_page.topped') }}
            </Tag>
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
            <Button type="link" size="small" @click="showEditDialog(record)">
              {{ $t('content.announcement_page.edit') }}
            </Button>
            <Button
              v-if="record.status !== 1"
              type="link"
              size="small"
              @click="handlePublish(record)"
            >
              {{ $t('content.announcement_page.publish') }}
            </Button>
            <Button
              v-if="record.status === 1"
              type="link"
              size="small"
              @click="handleOffline(record)"
            >
              {{ $t('content.announcement_page.offline') }}
            </Button>
            <Button type="link" size="small" @click="handleTop(record)">
              {{
                record.isTop === 1
                  ? $t('content.announcement_page.cancel_top')
                  : $t('content.announcement_page.top')
              }}
            </Button>
            <Button type="link" size="small" @click="showStatDialog(record)">
              {{ $t('content.announcement_page.stat_button') }}
            </Button>
            <Popconfirm
              :title="$t('content.announcement_page.delete_confirm')"
              @confirm="handleDelete(record)"
            >
              <Button type="link" size="small" danger>
                {{ $t('content.announcement_page.delete') }}
              </Button>
            </Popconfirm>
          </template>
        </template>
      </Table>
    </div>

    <!-- 新建/编辑弹窗 -->
    <Modal
      v-model:open="showDialog"
      :title="
        isEditing
          ? $t('content.announcement_page.edit_title')
          : $t('content.announcement_page.create_title')
      "
      width="760px"
      :mask-closable="false"
      destroy-on-close
    >
      <Form layout="horizontal" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem :label="$t('content.announcement_page.form.title')" required>
          <Input
            v-model:value="form.title"
            :maxlength="200"
            show-count
            :placeholder="$t('content.announcement_page.title_placeholder')"
          />
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.summary')">
          <Input.TextArea
            v-model:value="form.summary"
            :rows="2"
            :maxlength="500"
            show-count
            :placeholder="$t('content.announcement_page.summary_placeholder')"
          />
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.type_level')">
          <div class="flex gap-2">
            <Select v-model:value="form.type" class="w-1/2" :options="typeOptions" />
            <Select v-model:value="form.level" class="w-1/2" :options="levelOptions" />
          </div>
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.display_mode')">
          <Select v-model:value="form.displayMode" :options="displayModes" />
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.target_audience')">
          <Select v-model:value="form.targetAudience" :options="audiences" />
        </FormItem>
        <FormItem
          v-if="form.targetAudience === 4"
          :label="$t('content.announcement_page.form.target_user_ids')"
        >
          <Input
            v-model:value="targetUserIdsText"
            :placeholder="$t('content.announcement_page.target_users_placeholder')"
          />
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.publish_time')">
          <Input
            v-model:value="form.publishTime"
            :placeholder="$t('content.announcement_page.publish_time_placeholder')"
          />
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.expire_time')">
          <Input
            v-model:value="form.expireTime"
            :placeholder="$t('content.announcement_page.expire_time_placeholder')"
          />
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.cover_image')">
          <Input
            v-model:value="form.coverImage"
            :placeholder="$t('content.announcement_page.cover_placeholder')"
          />
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.top_weight')">
          <div class="flex items-center gap-4">
            <Switch :checked="form.isTop === 1" @change="(v: any) => (form.isTop = v ? 1 : 0)" />
            <InputNumber v-model:value="form.sortWeight" :min="0" :max="9999" />
          </div>
        </FormItem>
        <FormItem :label="$t('content.announcement_page.form.content')" required>
          <div class="rich-editor w-full">
            <div class="editor-toolbar">
              <button
                type="button"
                :title="$t('content.announcement_page.editor.bold')"
                @click="exec('bold')"
              >
                <b>B</b>
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.italic')"
                @click="exec('italic')"
              >
                <i>I</i>
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.underline')"
                @click="exec('underline')"
              >
                <u>U</u>
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.heading')"
                @click="exec('formatBlock', 'h2')"
              >
                H2
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.paragraph')"
                @click="exec('formatBlock', 'p')"
              >
                P
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.unorderedList')"
                @click="exec('insertUnorderedList')"
              >
                •≡
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.orderedList')"
                @click="exec('insertOrderedList')"
              >
                1≡
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.link')"
                @click="insertLink"
              >
                {{ $t('content.announcement_page.editor.link') }}
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.image')"
                @click="insertImage"
              >
                {{ $t('content.announcement_page.editor.image') }}
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.clear')"
                @click="exec('removeFormat')"
              >
                {{ $t('content.announcement_page.editor.clear') }}
              </button>
              <button
                type="button"
                :title="$t('content.announcement_page.editor.source')"
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
              :data-placeholder="$t('content.announcement_page.content_placeholder')"
              @input="onEditorInput"
            ></div>
          </div>
        </FormItem>
      </Form>
      <template #footer>
        <Button @click="showDialog = false">
          {{ $t('content.announcement_page.cancel') }}
        </Button>
        <Button :disabled="submitting" @click="submit(0)">
          {{ $t('content.announcement_page.save_draft') }}
        </Button>
        <Button type="primary" :loading="submitting" @click="submit(1)">
          {{
            futurePublish
              ? $t('content.announcement_page.publish_scheduled')
              : $t('content.announcement_page.publish_now')
          }}
        </Button>
      </template>
    </Modal>

    <!-- 阅读统计弹窗 -->
    <Modal
      v-model:open="statVisible"
      :title="$t('content.announcement_page.title') + ' · ' + $t('content.announcement_page.stat_button')"
      width="440px"
      :footer="null"
    >
      <Descriptions :column="1" bordered size="small">
        <Descriptions.Item :label="$t('content.announcement_page.stat.title')">
          {{ stat.title }}
        </Descriptions.Item>
        <Descriptions.Item :label="$t('content.announcement_page.stat.viewCount')">
          {{ stat.viewCount }}
        </Descriptions.Item>
        <Descriptions.Item :label="$t('content.announcement_page.stat.readCount')">
          {{ stat.readCount }}
        </Descriptions.Item>
        <Descriptions.Item :label="$t('content.announcement_page.stat.readRate')">
          {{ stat.readRate }}%
        </Descriptions.Item>
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
  content: attr(data-placeholder);
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
