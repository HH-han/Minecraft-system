<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Bookmark, Heart, ImagePlus, MessageCircle, Plus } from '@vben/icons';
import { $t } from '@vben/locales';

import {
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
  Skeleton,
  Space,
  Table,
  Tag,
  Upload,
} from 'antdv-next';

import {
  createPost,
  deletePost,
  getPostList,
  updatePost,
} from '#/api/management/travel/community';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'CommunityManagement' });

// =========================
// 帖子数据类型
// =========================
interface PostFormState {
  id?: number | string;
  title: string;
  content: string;
  // 帖子图片 URL 列表
  imageList: string[];
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

// 头像 / 帖子图片加载失败的 id 集合
const avatarErrorSet = reactive<Set<number | string>>(new Set());
// key: `${recordId}:${imageIndex}`
const postImageErrorSet = reactive<Set<string>>(new Set());

function isPostImageFailed(id: any, index: number): boolean {
  return postImageErrorSet.has(`${id}:${index}`);
}

function handlePostImageError(id: any, index: number) {
  postImageErrorSet.add(`${id}:${index}`);
}

/** 列表帖子图片（规范化后，最多展示 3 张） */
function postThumbs(record: any): string[] {
  return parseImageList(record?.images)
    .slice(0, 3)
    .map((img) => normalizeImageUrl(img));
}

// =========================
// 弹窗 / 表单状态
// =========================
type ModalMode = 'create' | 'detail' | 'edit' | null;
const modalMode = ref<ModalMode>(null);
const modalVisible = ref(false);
const submitting = ref(false);

const formRef = ref<InstanceType<typeof Form> | null>(null);
const imagesUploading = ref(false);

// 详情视图当前记录
const currentRecord = ref<any>(null);

function emptyForm(): PostFormState {
  return {
    title: '',
    content: '',
    imageList: [],
  };
}

const formData = reactive<PostFormState>(emptyForm());

const rules = computed(() => ({
  title: [
    {
      required: true,
      message: $t('travel.community_list.form.title_required'),
      trigger: 'blur',
    },
  ],
}));

// =========================
// 图片 URL 规范化 / 工具
// =========================
const BACKEND_STATIC_BASE = 'http://localhost:8080';

function normalizeImageUrl(url?: null | string): string {
  if (!url) return '';
  const raw = String(url).trim();
  if (!raw || raw === 'null' || raw === 'undefined') return '';
  if (/^data:/i.test(raw)) return raw;
  if (/^https?:\/\//i.test(raw)) return raw;
  if (raw.startsWith('/')) return BACKEND_STATIC_BASE + raw;
  return `${BACKEND_STATIC_BASE}/${raw}`;
}

/** 逗号分隔字符串 → 数组 */
function splitList(value: null | string | undefined): string[] {
  if (!value) return [];
  return String(value)
    .split(',')
    .map((s) => s.trim())
    .filter((s) => s.length > 0);
}

/** 后端 images 字段解析：兼容 JSON 数组与逗号分隔两种存储格式 */
function parseImageList(value: null | string | undefined): string[] {
  const raw = String(value ?? '').trim();
  if (!raw) return [];
  if (raw.startsWith('[')) {
    try {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) {
        return parsed.map((s: any) => String(s ?? '').trim()).filter(Boolean);
      }
    } catch {
      // JSON 解析失败时回退到逗号分隔解析
    }
    return splitList(raw.slice(1, -1));
  }
  return splitList(raw);
}

// =========================
// 数据加载 + 规范化
// 响应结构：{ pageNum, pageSize, pages, records }
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res: any = await getPostList({
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
    avatarErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.community_list.error_desc');
  } finally {
    loading.value = false;
  }
}

/** 行数据规范化：avatar 补全域名 */
function normalizeList(records: any[]): any[] {
  return (records ?? []).map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.avatar = normalizeImageUrl(row.avatar);
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
  showTotal: (t: number) => $t('travel.community_list.total', { total: t }),
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
    title: $t('travel.community_list.fields.id'),
    dataIndex: 'id',
    width: 70,
    fixed: 'left',
  },
  {
    title: $t('travel.community_list.fields.author'),
    key: 'author',
    width: 150,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.community_list.fields.title'),
    dataIndex: 'title',
    width: 200,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('travel.community_list.fields.content'),
    dataIndex: 'content',
    width: 280,
    ellipsis: true,
  },
  {
    title: $t('travel.community_list.fields.images'),
    key: 'imageCount',
    width: 150,
  },
  {
    title: $t('travel.community_list.fields.likeCount'),
    dataIndex: 'likeCount',
    width: 90,
  },
  {
    title: $t('travel.community_list.fields.commentCount'),
    dataIndex: 'commentCount',
    width: 90,
  },
  {
    title: $t('travel.community_list.fields.collectCount'),
    dataIndex: 'collectCount',
    width: 90,
  },
  {
    title: $t('travel.community_list.fields.createTime'),
    dataIndex: 'createTime',
    width: 180,
  },
  {
    title: $t('travel.community_list.action.operation'),
    key: 'actions',
    width: 220,
    fixed: 'right',
  },
]);

// =========================
// 辅助函数
// =========================
function isAvatarFailed(id: number | string): boolean {
  return avatarErrorSet.has(id);
}

function handleAvatarError(id: number | string) {
  avatarErrorSet.add(id);
}

/** 详情图片列表（规范化后） */
const detailImages = computed(() =>
  parseImageList(currentRecord.value?.images).map((img) =>
    normalizeImageUrl(img),
  ),
);

/**
 * 把后端单行记录转换为弹窗表单字段
 */
function recordToForm(rec: any): PostFormState {
  return {
    id: rec.id,
    title: String(rec.title ?? ''),
    content: String(rec.content ?? ''),
    imageList: parseImageList(rec.images),
  };
}

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
    return $t('travel.community_list.modal.create_title');
  if (modalMode.value === 'edit')
    return $t('travel.community_list.modal.edit_title');
  return $t('travel.community_list.modal.detail_title');
});

// =========================
// 图片上传（上传后追加到 images 字段）
// =========================
async function handleImageUpload(file: File) {
  if (!file) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.community_list.form.images_invalid_image'));
    return;
  }
  imagesUploading.value = true;
  try {
    const res: any = await uploadFile(file, '/community/upload');
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
      message.error($t('travel.community_list.form.images_upload_failed'));
      return;
    }
    formData.imageList.push(String(relativePath));
    message.success($t('travel.community_list.form.images_upload_success'));
  } catch (error: any) {
    const msg =
      error?.message ||
      $t('travel.community_list.form.images_upload_failed');
    message.error(msg);
  } finally {
    imagesUploading.value = false;
  }
}

function handleImagesChange(event: any) {
  const file = event?.fileList?.[0]?.originFileObj;
  if (file) handleImageUpload(file as File);
}

/** 移除待提交的帖子图片 */
function removeImage(index: number) {
  formData.imageList.splice(index, 1);
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
      content: formData.content || null,
      // 与库内既有格式保持一致（JSON 数组字符串）
      images: formData.imageList.length
        ? JSON.stringify(formData.imageList)
        : null,
    };
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      await updatePost(payload);
      message.success($t('travel.community_list.action.update_success'));
    } else {
      await createPost(payload);
      message.success($t('travel.community_list.action.create_success'));
    }
    closeModal();
    pageNum.value = 1;
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    message.error(
      modalMode.value === 'create'
        ? msg || $t('travel.community_list.action.create_failed')
        : msg || $t('travel.community_list.action.update_failed'),
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
    await deletePost(record.id);
    message.success($t('travel.community_list.action.delete_success'));
    // 删除后若当前页空了，回退一页
    if (dataSource.value.length === 1 && pageNum.value > 1) {
      pageNum.value -= 1;
    }
    await fetchData();
  } catch (err: any) {
    const msg =
      err?.message ||
      err?.msg ||
      $t('travel.community_list.action.delete_failed');
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
          :title="$t('travel.community_list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('travel.community_list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 社区帖子列表 -->
      <Card
        v-else
        :title="$t('travel.community_list.title')"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <Button type="primary" :loading="loading" @click="openCreate">
            <template #icon>
              <Plus class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('travel.community_list.action.add') }}
          </Button>
        </template>

        <Table
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :scroll="{ x: 1720 }"
          :bordered="true"
          size="middle"
          row-key="id"
          @change="handleTableChange"
        >
          <template #bodyCell="{ column, record }">
            <!-- 作者：头像 + 用户名 -->
            <template v-if="column.key === 'author'">
              <div class="author-cell">
                <img
                  v-if="record.avatar && !isAvatarFailed(record.id)"
                  :src="record.avatar"
                  :alt="record.username"
                  loading="lazy"
                  class="author-avatar"
                  @error="handleAvatarError(record.id)"
                />
                <span v-else class="author-avatar author-avatar-fallback">
                  {{ (record.username || '?').slice(0, 1) }}
                </span>
                <div class="author-info">
                  <span class="author-name">
                    {{ record.username || '-' }}
                  </span>
                  <span
                    v-if="record.bio"
                    class="author-bio"
                  >
                    {{ record.bio }}
                  </span>
                </div>
              </div>
            </template>

            <!-- 标题 -->
            <template v-else-if="column.dataIndex === 'title'">
              <span class="font-medium">{{ record.title || '-' }}</span>
            </template>

            <!-- 正文预览 -->
            <template v-else-if="column.dataIndex === 'content'">
              <span class="text-sm text-muted-foreground">
                {{ record.content || '-' }}
              </span>
            </template>

            <!-- 帖子图片缩略图（最多展示 3 张） -->
            <template v-else-if="column.key === 'imageCount'">
              <div
                v-if="postThumbs(record).length > 0"
                class="thumb-group"
              >
                <img
                  v-for="(img, idx) in postThumbs(record)"
                  v-show="!isPostImageFailed(record.id, idx)"
                  :key="idx"
                  :src="img"
                  :alt="`${record.title || 'post'}-${idx}`"
                  loading="lazy"
                  class="thumb-img"
                  @error="handlePostImageError(record.id, idx)"
                />
                <span
                  v-if="
                    parseImageList(record.images).length >
                    postThumbs(record).length
                  "
                  class="thumb-more"
                >
                  +{{
                    parseImageList(record.images).length -
                    postThumbs(record).length
                  }}
                </span>
              </div>
              <span v-else class="text-xs text-muted-foreground">-</span>
            </template>

            <!-- 统计：点赞 / 评论 / 收藏 -->
            <template v-else-if="column.dataIndex === 'likeCount'">
              <span class="stat-cell">
                <Heart class="size-3.5 text-muted-foreground" />
                {{ record.likeCount ?? 0 }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'commentCount'">
              <span class="stat-cell">
                <MessageCircle
                  class="size-3.5 text-muted-foreground"
                />
                {{ record.commentCount ?? 0 }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'collectCount'">
              <span class="stat-cell">
                <Bookmark
                  class="size-3.5 text-muted-foreground"
                />
                {{ record.collectCount ?? 0 }}
              </span>
            </template>

            <!-- 发布时间 -->
            <template v-else-if="column.dataIndex === 'createTime'">
              {{ record.createTime || '-' }}
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button type="link" size="small" @click="openDetail(record)">
                  {{ $t('travel.community_list.action.detail') }}
                </Button>
                <Button type="link" size="small" @click="openEdit(record)">
                  {{ $t('travel.community_list.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('travel.community_list.action.confirm_delete_title')"
                  :description="$t('travel.community_list.action.confirm_delete')"
                  :ok-text="$t('travel.community_list.action.delete_text')"
                  :cancel-text="$t('travel.community_list.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('travel.community_list.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            <Empty :description="$t('travel.community_list.empty')" />
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
      :ok-text="$t('travel.community_list.action.save')"
      :cancel-text="$t('travel.community_list.action.cancel')"
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
        <!-- 作者区 -->
        <div class="mb-4 flex items-center gap-3">
          <img
            v-if="currentRecord.avatar && !isAvatarFailed(currentRecord.id)"
            :src="currentRecord.avatar"
            :alt="currentRecord.username"
            class="size-10 rounded-full border border-border object-cover flex-shrink-0"
            @error="handleAvatarError(currentRecord.id)"
          />
          <span
            v-else
            class="author-avatar-fallback size-10 rounded-full flex-shrink-0"
            style="font-size: 16px;"
          >
            {{ (currentRecord.username || '?').slice(0, 1) }}
          </span>
          <div class="min-w-0">
            <div class="font-semibold">{{ currentRecord.username || '-' }}</div>
            <div
              v-if="currentRecord.bio"
              class="truncate text-xs text-muted-foreground"
            >
              {{ currentRecord.bio }}
            </div>
          </div>
        </div>

        <div class="text-lg font-semibold leading-7">
          {{ formData.title }}
        </div>

        <!-- 互动统计 -->
        <div
          class="mt-2 flex flex-wrap items-center gap-4 text-sm text-muted-foreground"
        >
          <span class="inline-flex items-center gap-1">
            <Heart class="size-4" />
            {{ currentRecord.likeCount ?? 0 }}
          </span>
          <span class="inline-flex items-center gap-1">
            <MessageCircle class="size-4" />
            {{ currentRecord.commentCount ?? 0 }}
          </span>
          <span class="inline-flex items-center gap-1">
            <Bookmark class="size-4" />
            {{ currentRecord.collectCount ?? 0 }}
          </span>
          <Tag color="blue">
            {{ currentRecord.createTime || '-' }}
          </Tag>
        </div>

        <Divider class="my-4">
          {{ $t('travel.community_list.detail.content_info') }}
        </Divider>
        <p
          class="m-0 whitespace-pre-wrap text-sm leading-6 text-muted-foreground"
        >
          {{ formData.content || '-' }}
        </p>

        <!-- 帖子图片 -->
        <template v-if="detailImages.length > 0">
          <Divider class="my-4">
            {{ $t('travel.community_list.detail.images_info') }}
          </Divider>
          <div class="grid grid-cols-3 gap-3">
            <img
              v-for="(img, idx) in detailImages"
              :key="idx"
              :src="img"
              :alt="`post-image-${idx}`"
              loading="lazy"
              class="aspect-square w-full cursor-pointer rounded border border-border object-cover"
              @error="hideBrokenImage"
            />
          </div>
        </template>

        <Divider class="my-4">
          {{ $t('travel.community_list.detail.basic_info') }}
        </Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('travel.community_list.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.community_list.fields.userId')">
            {{ currentRecord.userId ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('travel.community_list.fields.videos')">
            {{ currentRecord.videos || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            :label="$t('travel.community_list.fields.createTime')"
          >
            {{ currentRecord.createTime || '-' }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <!-- 创建 / 修改：Form -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="community-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 编辑模式：展示 ID -->
        <div v-if="modalMode === 'edit' && (formData as any).id" class="mb-4">
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('travel.community_list.fields.id')"
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
            class="grid grid-cols-1 gap-x-6 gap-y-1"
            style="display: grid;"
          >
            <FormItem
              :label="$t('travel.community_list.form.title_label')"
              name="title"
              prop="title"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.title"
                class="w-full"
                :placeholder="$t('travel.community_list.form.title_required')"
              />
            </FormItem>

            <FormItem
              :label="$t('travel.community_list.form.content_label')"
              name="content"
              prop="content"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input.TextArea
                v-model:value="formData.content"
                :rows="8"
                class="w-full"
                :placeholder="$t('travel.community_list.form.content_label')"
              />
            </FormItem>

            <!-- 图片：缩略图预览（可删除） + 上传追加 -->
            <div class="col-span-1" style="width: 100%;">
              <FormItem
                :label="$t('travel.community_list.form.images_label')"
                :extra="$t('travel.community_list.form.images_tip')"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <div
                  v-if="formData.imageList.length > 0"
                  class="form-image-grid"
                >
                  <div
                    v-for="(img, idx) in formData.imageList"
                    :key="idx"
                    class="form-image-item"
                  >
                    <img
                      :src="normalizeImageUrl(img)"
                      :alt="`post-image-${idx}`"
                      loading="lazy"
                      class="form-image-thumb"
                      @error="hideBrokenImage"
                    />
                    <button
                      class="form-image-remove"
                      type="button"
                      @click="removeImage(idx)"
                    >
                      ×
                    </button>
                  </div>
                </div>
                <Upload
                  :max-count="1"
                  :show-upload-list="false"
                  accept="image/*"
                  :before-upload="() => false"
                  @change="handleImagesChange"
                >
                  <Button
                    class="mt-2"
                    size="small"
                    :loading="imagesUploading"
                  >
                    <template #icon>
                      <ImagePlus
                        class="mr-1 size-4 align-middle"
                      />
                    </template>
                    {{ $t('travel.community_list.form.images_upload') }}
                  </Button>
                </Upload>
              </FormItem>
            </div>
          </div>
        </Form>
      </div>
    </Modal>
  </Page>
</template>

<style scoped>
.author-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.author-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--border-color, #f0f0f0);
  flex-shrink: 0;
}

.author-avatar-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgb(59 130 246 / 12%);
  color: rgb(59 130 246);
  font-size: 13px;
  font-weight: 500;
}

.author-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.author-name {
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.author-bio {
  font-size: 11px;
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-cell {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.thumb-group {
  display: flex;
  align-items: center;
  gap: 4px;
}

.thumb-img {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  border: 1px solid var(--border-color, #f0f0f0);
  object-fit: cover;
  flex-shrink: 0;
}

.thumb-more {
  font-size: 12px;
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
}

/* 表单内帖子图片缩略图 */
.form-image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 4px;
}

.form-image-item {
  position: relative;
  width: 88px;
  height: 88px;
}

.form-image-thumb {
  width: 100%;
  height: 100%;
  border: 1px solid var(--border-color, #f0f0f0);
  border-radius: 6px;
  object-fit: cover;
}

.form-image-remove {
  position: absolute;
  top: -6px;
  right: -6px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: rgb(0 0 0 / 60%);
  color: #fff;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
}

.form-image-remove:hover {
  background: rgb(0 0 0 / 80%);
}
</style>