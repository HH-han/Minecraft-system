<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import {
  Bookmark,
  Eye,
  Heart,
  ImageIcon,
  ImageOff,
  MessageCircle,
  Plus,
} from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Avatar,
  Button,
  Card,
  Descriptions,
  Divider,
  Empty,
  Form,
  FormItem,
  Input,
  message,
  Modal,
  Popconfirm,
  Result,
  Select,
  Skeleton,
  Space,
  Table,
  Tag,
  Upload,
} from 'antdv-next';

import {
  addNews,
  deleteNews,
  getNewsList,
  updateNews,
} from '#/api/management/travel/news';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'NewsManagement' });

// =========================
// 新闻数据类型
// =========================
interface NewsFormState {
  id?: number | string;
  title: string;
  source: string;
  coverImage: string;
  content: string;
  // 1 已发布 / 0 未发布
  status: number;
}

// =========================
// 基础状态（服务端分页）
// =========================
const loading = ref(false);
const errorMsg = ref('');
const dataSource = ref<any[]>([]);

const pageNum = ref(1);
const pageSize = ref(10);
const pages = ref(0);
const totalCount = ref(0);

// 封面加载失败的记录 id 集合
const coverErrorSet = reactive<Set<number | string>>(new Set());

// =========================
// 弹窗 / 表单状态
// =========================
type ModalMode = 'create' | 'detail' | 'edit' | null;
const modalMode = ref<ModalMode>(null);
const modalVisible = ref(false);
const submitting = ref(false);

const formRef = ref<InstanceType<typeof Form> | null>(null);
const coverUploading = ref(false);

// 详情视图当前记录
const currentRecord = ref<any>(null);

function emptyForm(): NewsFormState {
  return {
    title: '',
    source: '',
    coverImage: '',
    content: '',
    status: 1,
  };
}

const formData = reactive<NewsFormState>(emptyForm());

const rules = computed(() => ({
  title: [
    {
      required: true,
      message: $t('travel.news_list.form.title_required'),
      trigger: 'blur',
    },
  ],
}));

const statusOptions = computed(() => [
  { label: $t('travel.news_list.status.on'), value: 1 },
  { label: $t('travel.news_list.status.off'), value: 0 },
]);

// =========================
// 图片 URL 规范化 / 工具
// =========================
const BACKEND_STATIC_BASE = 'http://localhost:8080';

function normalizeImageUrl(url?: null | string): string {
  if (!url) return '';
  const raw = String(url).trim();
  if (!raw || raw === 'null' || raw === 'undefined') return '';
  if (/^https?:\/\//i.test(raw)) return raw;
  if (raw.startsWith('/')) return BACKEND_STATIC_BASE + raw;
  return `${BACKEND_STATIC_BASE}/${raw}`;
}

/** 大数字千分位格式化 */
function formatNumber(value: null | number | undefined): string {
  if (value === null || value === undefined) return '0';
  return Number(value).toLocaleString('en-US');
}

// =========================
// 数据加载 + 规范化
// 响应结构：{ pageNum, pageSize, pages, records }
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getNewsList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    const records: any[] = Array.isArray(res)
      ? res
      : (res?.records ?? res?.list ?? res?.items ?? res?.rows ?? []);
    dataSource.value = normalizeList(records);
    pageNum.value = Number(res?.pageNum ?? res?.current ?? pageNum.value);
    pages.value = Number(res?.pages ?? res?.totalPages ?? 0);
    totalCount.value = Number(
      res?.total ??
        (pages.value ? pages.value * pageSize.value : records.length),
    );
    coverErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.news_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/** 行数据规范化：coverImage 补全域名 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.coverImage = normalizeImageUrl(row.coverImage);
    return row;
  });
}

onMounted(() => {
  fetchData();
});

// =========================
// 分页（服务端）
// =========================
const total = computed(() => {
  if (totalCount.value) return totalCount.value;
  if (!pages.value) return dataSource.value.length;
  if (
    pageNum.value === pages.value &&
    dataSource.value.length < pageSize.value
  ) {
    return (pages.value - 1) * pageSize.value + dataSource.value.length;
  }
  return pages.value * pageSize.value;
});

const pagination = computed(() => ({
  current: pageNum.value,
  pageSize: pageSize.value,
  total: total.value,
  showSizeChanger: true,
  pageSizeOptions: ['5', '10', '20', '50'],
  showTotal: (t: number) => $t('travel.news_list.total', { total: t }),
}));

function handleTableChange(pag: any) {
  pageNum.value = pag.current;
  pageSize.value = pag.pageSize;
  fetchData();
}

// =========================
// 表格列定义
// =========================
const columns = computed<TableColumnsType>(() => [
  {
    title: $t('travel.news_list.fields.id'),
    dataIndex: 'id',
    width: 70,
    fixed: 'left',
  },
  {
    title: $t('travel.news_list.fields.coverImage'),
    dataIndex: 'coverImage',
    width: 100,
    fixed: 'left',
  },
  {
    title: $t('travel.news_list.fields.title'),
    dataIndex: 'title',
    width: 320,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.news_list.fields.source'),
    dataIndex: 'source',
    width: 130,
    ellipsis: true,
  },
  {
    title: $t('travel.news_list.fields.status'),
    dataIndex: 'status',
    width: 100,
  },
  {
    title: $t('travel.news_list.fields.viewCount'),
    dataIndex: 'viewCount',
    width: 100,
  },
  {
    title: $t('travel.news_list.fields.likeCount'),
    dataIndex: 'likeCount',
    width: 100,
  },
  {
    title: $t('travel.news_list.fields.commentCount'),
    dataIndex: 'commentCount',
    width: 100,
  },
  {
    title: $t('travel.news_list.fields.collectCount'),
    dataIndex: 'collectCount',
    width: 100,
  },
  {
    title: $t('travel.news_list.fields.createTime'),
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: $t('travel.news_list.action.operation'),
    key: 'actions',
    width: 220,
    fixed: 'right',
  },
]);

// =========================
// 辅助函数
// =========================
function isCoverFailed(id: number | string): boolean {
  return coverErrorSet.has(id);
}

function handleCoverError(id: number | string) {
  coverErrorSet.add(id);
}

/** 状态：1 已发布 / 0 未发布 */
function isPublished(status: null | number | string | undefined): boolean {
  return Number(status) === 1;
}

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): NewsFormState {
  return {
    id: rec.id,
    title: String(rec.title ?? ''),
    source: String(rec.source ?? ''),
    coverImage: normalizeImageUrl(rec.coverImage),
    content: String(rec.content ?? ''),
    status: Number(rec.status ?? 1),
  };
}

// 表单内封面预览 URL（实时规范化）
const formCoverDisplayUrl = computed(() =>
  normalizeImageUrl(formData.coverImage),
);

// =========================
// 弹窗开关
// =========================
function openCreate() {
  currentRecord.value = null;
  Object.assign(formData, emptyForm());
  modalMode.value = 'create';
  modalVisible.value = true;
}

function openEdit(record: any) {
  currentRecord.value = record;
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'edit';
  modalVisible.value = true;
}

function openDetail(record: any) {
  currentRecord.value = record;
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'detail';
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  modalMode.value = null;
  currentRecord.value = null;
  formRef.value?.resetFields?.();
  Object.assign(formData, emptyForm());
}

const modalTitle = computed(() => {
  if (modalMode.value === 'create')
    return $t('travel.news_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.news_list.modal.edit_title');
  return $t('travel.news_list.modal.detail_title');
});

// =========================
// 封面图片上传
// =========================
async function handleCoverUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.news_list.form.cover_invalid_image'));
    return;
  }
  coverUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/news/upload');
    // requestClient 解包成功响应后 data 即 URL 字符串，这里同时兼容对象形式
    const relativePath =
      typeof res === 'string'
        ? res
        : (res?.url ??
          res?.path ??
          res?.filePath ??
          res?.data?.url ??
          res?.data ??
          '');
    if (!relativePath) {
      message.error($t('travel.news_list.form.cover_upload_failed'));
      return;
    }
    formData.coverImage = String(relativePath);
    message.success($t('travel.news_list.form.cover_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message || $t('travel.news_list.form.cover_upload_failed');
    message.error(msg);
  } finally {
    coverUploading.value = false;
  }
}

function handleCoverChange(event: any) {
  const file = event?.fileList?.[0]?.originFileObj;
  if (file) handleCoverUpload(file as File);
}

function hideBrokenImage(e: Event) {
  const target = e.target as HTMLImageElement;
  if (!target) return;
  target.style.display = 'none';
  // 图片地址更新并重新加载成功后自动恢复显示，避免更换封面后预览一直被隐藏
  target.onload = () => {
    target.style.display = '';
  };
}

// =========================
// 提交：新增 / 修改
// =========================
async function handleSubmit() {
  if (modalMode.value === 'detail') {
    closeModal();
    return;
  }
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload: any = {
      title: formData.title,
      source: formData.source || null,
      coverImage: formData.coverImage || null,
      content: formData.content || null,
      status: Number(formData.status),
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updateNews(payload);
      message.success($t('travel.news_list.action.update_success'));
    } else {
      await addNews(payload);
      message.success($t('travel.news_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.news_list.action.create_failed')
        : msg || $t('travel.news_list.action.update_failed'),
    );
  } finally {
    submitting.value = false;
  }
}

// =========================
// 删除（带二次确认）
// =========================
const deletingId = ref<null | number | string>(null);
async function handleDelete(record: any) {
  if (!record || record?.id === undefined || record?.id === null) return;
  deletingId.value = record.id;
  try {
    await deleteNews(record.id);
    message.success($t('travel.news_list.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message || err?.msg || $t('travel.news_list.action.delete_failed');
    message.error(msg);
  } finally {
    deletingId.value = null;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏（仅首次加载） -->
      <Card
        v-if="loading && !dataSource.length"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && !dataSource.length"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('travel.news_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.news_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 新闻列表 -->
      <Card
        v-else
        :title="$t('travel.news_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.news_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1880 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 封面缩略图（圆形） -->
            <template v-if="column.dataIndex === 'coverImage'">
              <div class="cover-cell">
                <img
                  v-if="record.coverImage && !isCoverFailed(record.id)"
                  :src="record.coverImage"
                  :alt="record.title"
                  loading="lazy"
                  class="cover-thumb"
                  @error="handleCoverError(record.id)"
                />
                <span v-else class="cover-fallback">
                  {{ $t('travel.news_list.no_cover') }}
                </span>
              </div>
            </template>

            <!-- 来源 -->
            <template v-else-if="column.dataIndex === 'source'">
              <Tag v-if="record.source" color="blue">
                {{ record.source }}
              </Tag>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 状态：1 已发布 / 0 未发布 -->
            <template v-else-if="column.dataIndex === 'status'">
              <Tag :color="isPublished(record.status) ? 'green' : 'default'">
                {{
                  isPublished(record.status)
                    ? $t('travel.news_list.status.on')
                    : $t('travel.news_list.status.off')
                }}
              </Tag>
            </template>

            <!-- 统计：浏览 / 点赞 / 评论 / 收藏 -->
            <template v-else-if="column.dataIndex === 'viewCount'">
              <span class="stat-cell">
                <Eye class="size-3.5 text-muted-foreground" />
                {{ formatNumber(record.viewCount) }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'likeCount'">
              <span class="stat-cell">
                <Heart class="size-3.5 text-muted-foreground" />
                {{ formatNumber(record.likeCount) }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'commentCount'">
              <span class="stat-cell">
                <MessageCircle
                  class="size-3.5 text-muted-foreground"
                />
                {{ formatNumber(record.commentCount) }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'collectCount'">
              <span class="stat-cell">
                <Bookmark
                  class="size-3.5 text-muted-foreground"
                />
                {{ formatNumber(record.collectCount) }}
              </span>
            </template>

            <!-- 创建时间 -->
            <template v-else-if="column.dataIndex === 'createTime'">
              {{ record.createTime || '-' }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.news_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.news_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.news_list.action.confirm_delete_title')"
                  :description="$t('travel.news_list.action.confirm_delete')"
                  :ok-text="$t('travel.news_list.action.delete_text')"
                  :cancel-text="$t('travel.news_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.news_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.news_list.empty')" />
          </template>
        </Table>
      </Card>
    </div>

    <!-- 新增 / 修改 / 详情 弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="720"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('travel.news_list.action.save')"
      :cancel-text="$t('travel.news_list.action.cancel')"
      :styles="{
        body: {
          maxHeight: 'calc(100vh - 260px)',
          overflowY: 'auto',
          padding: '20px 24px',
        },
      }"
      @ok="handleSubmit"
      @cancel="closeModal"
    >
      <!-- 详情视图 -->
      <div v-if="modalMode === 'detail' && currentRecord">
        <!-- 头部：封面 + 标题 + 标签 -->
        <div class="mb-4 flex items-start gap-4">
          <Avatar
            v-if="formCoverDisplayUrl"
            :src="formCoverDisplayUrl"
            :size="96"
            shape="circle"
            class="border border-border overflow-hidden object-cover flex-shrink-0"
          />
          <div v-else class="cover-placeholder-circle">
            <ImageOff class="size-8 text-muted-foreground" />
          </div>
          <div class="min-w-0">
            <div class="text-xl font-semibold leading-7 line-clamp-2">
              {{ formData.title }}
            </div>
            <Space class="mt-1" :size="6">
              <Tag v-if="formData.source" color="blue">
                {{ formData.source }}
              </Tag>
              <Tag :color="isPublished(formData.status) ? 'green' : 'default'">
                {{
                  isPublished(formData.status)
                    ? $t('travel.news_list.status.on')
                    : $t('travel.news_list.status.off')
                }}
              </Tag>
            </Space>
            <!-- 互动统计 -->
            <div class="mt-2 flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
              <span class="inline-flex items-center gap-1">
                <Eye class="size-4" />
                {{ formatNumber(currentRecord.viewCount) }}
              </span>
              <span class="inline-flex items-center gap-1">
                <Heart class="size-4" />
                {{ formatNumber(currentRecord.likeCount) }}
              </span>
              <span class="inline-flex items-center gap-1">
                <MessageCircle class="size-4" />
                {{ formatNumber(currentRecord.commentCount) }}
              </span>
              <span class="inline-flex items-center gap-1">
                <Bookmark class="size-4" />
                {{ formatNumber(currentRecord.collectCount) }}
              </span>
            </div>
          </div>
        </div>

        <Divider class="my-4">
          {{ $t('travel.news_list.detail.basic_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.news_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.news_list.fields.source')">
            {{ formData.source || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.news_list.fields.createTime')">
            {{ currentRecord.createTime || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.news_list.fields.updateTime')">
            {{ currentRecord.updateTime || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">
          {{ $t('travel.news_list.detail.content_info') }}
        </Divider>
        <p class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground">
          {{ formData.content || '-' }}
        </p>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="news-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.news_list.fields.id')"
              :span="2"
            >
              {{ (formData as any).id }}
            </Descriptions.Item>
          </Descriptions>
        </div>

        <Form
          ref="formRef"
          :model="formData"
          :rules="rules"
          layout="vertical"
          class="w-full"
          style="display: block;"
        >
          <div
            class="grid grid-cols-1 gap-x-6 gap-y-1 md:grid-cols-2"
            style="display: grid;"
          >
            <FormItem
              :label="$t('travel.news_list.form.title_label')"
              name="title"
              prop="title"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.title"
                class="w-full"
                :placeholder="$t('travel.news_list.form.title_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.news_list.form.source_label')"
              name="source"
              prop="source"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.source"
                class="w-full"
                :placeholder="$t('travel.news_list.form.source_label')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.news_list.form.status_label')"
              name="status"
              prop="status"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Select
                v-model:value="formData.status"
                class="w-full"
                :options="statusOptions"
              />
            </FormItem>

            <!-- 封面上传：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.news_list.form.cover_label')"
                name="coverImage"
                prop="coverImage"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Upload
                  :max-count="1"
                  :show-upload-list="false"
                  accept="image/*"
                  :before-upload="() => false"
                  @change="handleCoverChange"
                >
                  <Button
                    class="cover-upload-trigger flex w-full items-center gap-3 border-dashed"
                    :loading="coverUploading"
                    style="height: auto; padding: 8px 12px;"
                  >
                    <img
                      v-if="formCoverDisplayUrl"
                      :src="formCoverDisplayUrl"
                      :alt="$t('travel.news_list.form.cover_label')"
                      class="size-12 rounded-full border border-border flex-shrink-0 object-cover"
                      @error="hideBrokenImage"
                    />
                    <ImageIcon
                      v-else
                      class="size-12 flex-shrink-0 text-muted-foreground"
                    />
                    <div class="flex flex-col text-left">
                      <span class="text-sm font-medium">
                        {{
                          formData.coverImage
                            ? $t('travel.news_list.form.cover_change')
                            : $t('travel.news_list.form.cover_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('travel.news_list.form.cover_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 正文：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('travel.news_list.form.content_label')"
                name="content"
                prop="content"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.content"
                  :rows="6"
                  class="w-full"
                  :placeholder="$t('travel.news_list.form.content_label')"
                />
              </FormItem>
            </div>
          </div>
        </Form>
      </div>
    </Modal>
  </Page>
</template>

<style scoped>
.cover-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  overflow: hidden;
  background-color: var(--app-fill-color, rgb(0 0 0 / 4%));
  flex-shrink: 0;
}

.cover-thumb {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-fallback {
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
  font-size: 11px;
  text-align: center;
}

.cover-placeholder-circle {
  display: flex;
  width: 96px;
  height: 96px;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: var(--app-fill-color, rgb(0 0 0 / 4%));
  flex-shrink: 0;
}

.cover-upload-trigger {
  text-align: left;
}

.stat-cell {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}
</style>