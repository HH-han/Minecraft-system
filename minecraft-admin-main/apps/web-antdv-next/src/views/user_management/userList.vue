<script lang="ts" setup>
import type { TableColumnsType, UploadChangeParam } from 'antdv-next';

import type { UserDetail } from '#/api/management/user/user';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page, VCropper } from '@vben/common-ui';
import { Plus } from '@vben/icons';
import { $t } from '@vben/locales';
import { preferences } from '@vben/preferences';

import {
  Avatar,
  Button,
  Card,
  Descriptions,
  Divider,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Radio,
  Result,
  Skeleton,
  Space,
  Switch,
  Table,
  Tag,
  Upload,
} from 'antdv-next';
import dayjs from 'dayjs';

import { uploadFile } from '#/api/management/user/upload';
import {
  createUser,
  deleteUser,
  getAllUsers,
  updateUserInfo,
} from '#/api/management/user/user';

defineOptions({ name: 'UserList' });

// =========================
// 基础状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const dataSource = ref<UserDetail[]>([]);
const keyword = ref('');

// =========================
// 弹窗 / 表单状态
// =========================
type ModalMode = 'create' | 'detail' | 'edit' | null;
const modalMode = ref<ModalMode>(null);
const modalVisible = ref(false);
const submitting = ref(false);

const formRef = ref<InstanceType<typeof Form> | null>(null);

interface UserFormState {
  id?: number | string;
  account: string;
  password: string;
  username: string;
  nickname: string;
  gender: number; // 1 男 / 2 女 / 0 未知
  age: null | number;
  email: string;
  phone: string;
  avatar: string;
  occupation: string;
  experience: string;
  signature: string;
  bio: string;
  hobbies: string;
  points: number;
  status: number; // 1 启用 / 0 停用
  permissions: string;
  // 详情视图专用（表单不提交）
  online: any;
  createTime?: number | string;
  updateTime?: number | string;
}

function emptyForm(): UserFormState {
  return {
    account: '',
    password: '',
    username: '',
    nickname: '',
    gender: 0,
    age: null,
    email: '',
    phone: '',
    avatar: '',
    occupation: '',
    experience: '',
    signature: '',
    bio: '',
    hobbies: '',
    points: 0,
    status: 1,
    permissions: '',
    online: false,
    createTime: undefined,
    updateTime: undefined,
  };
}

const formData = reactive<UserFormState>(emptyForm());

// 动态校验规则：创建时密码必填，修改时留空表示不修改
const rules = computed(() => ({
  account: [
    {
      required: true,
      message: $t('user.form.account_required'),
      trigger: 'blur',
    },
  ],
  password: [
    {
      required: modalMode.value === 'create',
      message: $t('user.form.password_required'),
      trigger: 'blur',
    },
  ],
  username: [
    {
      required: true,
      message: $t('user.form.username_required'),
      trigger: 'blur',
    },
  ],
  nickname: [
    {
      required: true,
      message: $t('user.form.nickname_required'),
      trigger: 'blur',
    },
  ],
  email: [
    {
      type: 'email',
      message: $t('user.form.email_invalid'),
      trigger: 'blur',
    },
  ],
  age: [
    {
      type: 'number',
      min: 0,
      max: 150,
      message: $t('user.form.age_range'),
      trigger: 'blur',
    },
  ],
  points: [
    {
      type: 'number',
      min: 0,
      message: $t('user.form.points_range'),
      trigger: 'blur',
    },
  ],
}));

// =========================
// 头像 URL 规范化
// 规则：
//   1) 空 / null / undefined / 纯空白 → 默认头像
//   2) 字符串值为 'null' / 'undefined' → 默认头像
//   3) 已以 http(s):// 开头的绝对 URL → 原样返回（不剥离 localhost 主机名，
//      否则在前端开发服务器端口与后端不同时会出现 404）
//   4) 相对路径（如 /upload/xxx.jpeg、upload/xxx.jpeg）→ 拼接后端静态资源前缀
//      （后端通常在 8080 端口提供 /upload 静态目录）
// =========================
function normalizeAvatarUrl(url?: null | string): string {
  if (!url) return preferences.app.defaultAvatar;
  const raw = String(url).trim();
  if (!raw) return preferences.app.defaultAvatar;
  if (raw === 'null' || raw === 'undefined') return preferences.app.defaultAvatar;

  // 绝对 URL：直接使用
  if (/^https?:\/\//i.test(raw)) {
    return raw;
  }

  // 相对路径 → 统一补全到后端 upload 服务
  // 若以后上线要改成同源路径，只需改这里
  const BACKEND_STATIC_BASE = 'http://localhost:8080';
  if (raw.startsWith('/')) {
    return BACKEND_STATIC_BASE + raw;
  }
  // 无前导斜杠
  return `${BACKEND_STATIC_BASE}/${raw}`;
}

// =========================
// 数据加载 + 规范化
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res = await getAllUsers();
    dataSource.value = normalizeList(res);
  } catch (error: any) {
    errorMsg.value = error?.message || $t('user.list.error_desc');
  } finally {
    loading.value = false;
  }
}

/**
 * 兼容后端可能返回的多种列表结构，并对每一行做字段规范化：
 * - avatar：本地绝对地址 → 相对路径（走代理）
 * - gender：字符串 "1"/"2" → 数字 1/2/0
 * - online：数字 0/1 → 布尔 false/true（用于渲染 Tag 颜色）
 * - 删除敏感字段：password 永远不在前端渲染
 */
function normalizeList(res: any): UserDetail[] {
  let arr: any[] = [];
  if (Array.isArray(res)) {
    arr = res;
  } else if (res && typeof res === 'object') {
    arr = res.items ?? res.list ?? res.records ?? res.data ?? res.rows ?? [];
  }
  return arr.map((raw) => {
    const row: any = { ...(raw ?? {}) };
    row.avatar = normalizeAvatarUrl(row.avatar);
    // gender 兼容字符串 / 数字两种
    const g = Number(row.gender);
    row.gender = Number.isFinite(g) ? g : 0;
    // online 0/1 → boolean
    row.online = Number(row.online) === 1;
    // 避免密码字段意外泄漏
    delete row.password;
    return row as UserDetail;
  });
}

onMounted(() => {
  fetchData();
});

// =========================
// 客户端搜索过滤（按用户名 / 昵称 / 账号）
// =========================
const filteredData = computed<UserDetail[]>(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) {
    return dataSource.value;
  }
  return dataSource.value.filter((item: any) => {
    return [item.username, item.nickname, item.account]
      .filter(Boolean)
      .some((field) => String(field).toLowerCase().includes(kw));
  });
});

// =========================
// 表格列定义（按后端 JSON 的全部字段渲染）
// =========================
const columns = computed<TableColumnsType>(() => [
  {
    title: $t('user.detail.fields.id'),
    dataIndex: 'id',
    width: 90,
    fixed: 'left',
  },
  {
    title: $t('user.detail.fields.account'),
    dataIndex: 'account',
    width: 160,
    fixed: 'left',
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.username'),
    dataIndex: 'user',
    width: 240,
    fixed: 'left',
  },
  {
    title: $t('user.detail.fields.nickname'),
    dataIndex: 'nickname',
    width: 120,
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.gender'),
    dataIndex: 'gender',
    width: 90,
    customRender: ({ text }: { text: any }) => genderText(text),
  },
  {
    title: $t('user.detail.fields.age'),
    dataIndex: 'age',
    width: 80,
  },
  {
    title: $t('user.detail.fields.email'),
    dataIndex: 'email',
    width: 220,
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.phone'),
    dataIndex: 'phone',
    width: 140,
  },
  {
    title: $t('user.detail.fields.occupation'),
    dataIndex: 'occupation',
    width: 120,
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.experience'),
    dataIndex: 'experience',
    width: 140,
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.hobbies'),
    dataIndex: 'hobbies',
    width: 140,
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.signature'),
    dataIndex: 'signature',
    width: 160,
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.bio'),
    dataIndex: 'bio',
    width: 180,
    ellipsis: true,
  },
  {
    title: $t('user.detail.fields.points'),
    dataIndex: 'points',
    width: 100,
    customRender: ({ text }: { text: number }) =>
      `${text ?? 0} ${$t('user.detail.points_unit')}`,
  },
  {
    title: $t('user.detail.fields.permissions'),
    dataIndex: 'permissions',
    width: 110,
  },
  {
    title: $t('user.detail.fields.status'),
    dataIndex: 'status',
    width: 100,
  },
  {
    title: $t('user.detail.fields.online'),
    dataIndex: 'online',
    width: 100,
  },
  {
    title: $t('user.detail.fields.createTime'),
    dataIndex: 'createTime',
    width: 180,
    customRender: ({ text }: { text: number | string }) =>
      formatDateTime(text),
  },
  {
    title: $t('user.detail.fields.updateTime'),
    dataIndex: 'updateTime',
    width: 180,
    customRender: ({ text }: { text: number | string }) =>
      formatDateTime(text),
  },
  {
    title: $t('user.action.operation'),
    key: 'actions',
    width: 220,
    fixed: 'right',
  },
]);

const pagination = computed(() => ({
  pageSize: 10,
  showSizeChanger: true,
  showTotal: (total: number) => $t('user.list.total', { total }),
}));

// =========================
// 辅助函数
// =========================
function genderText(gender: any): string {
  const g = Number(gender);
  switch (g) {
    case 1: {
      return $t('user.detail.gender.male');
    }
    case 2: {
      return $t('user.detail.gender.female');
    }
    default: {
      return $t('user.detail.gender.unknown');
    }
  }
}

function formatDateTime(value: number | string | undefined): string {
  if (value === null || value === undefined || value === '') {
    return '-';
  }
  if (typeof value === 'number') {
    return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
  }
  const parsed = dayjs(value);
  return parsed.isValid() ? parsed.format('YYYY-MM-DD HH:mm:ss') : String(value);
}

/**
 * 把后端单行记录转换为弹窗表单字段（类型规范化，去除 password）
 * 头像字段同样走 normalizeAvatarUrl，避免编辑态预览时头像不显示
 */
function recordToForm(rec: any): UserFormState {
  const g = Number(rec.gender);
  const rawAvatar =
    rec.avatar ??
    rec.userAvatar ??
    rec.picture ??
    rec.avatarUrl ??
    rec.headImg ??
    '';
  return {
    id: rec.id,
    account: String(rec.account ?? ''),
    password: '', // 不在表单中预填密码
    username: String(rec.username ?? ''),
    nickname: String(rec.nickname ?? ''),
    gender: Number.isFinite(g) ? g : 0,
    age:
      rec.age === undefined || rec.age === null || rec.age === ''
        ? null
        : Number(rec.age),
    email: String(rec.email ?? ''),
    phone: String(rec.phone ?? ''),
    avatar: normalizeAvatarUrl(rawAvatar as string),
    occupation: String(rec.occupation ?? ''),
    experience: String(rec.experience ?? ''),
    signature: String(rec.signature ?? ''),
    bio: String(rec.bio ?? ''),
    hobbies: String(rec.hobbies ?? ''),
    points: Number(rec.points) || 0,
    status: Number(rec.status) === 0 ? 0 : 1,
    permissions: String(rec.permissions ?? ''),
    online: Number(rec.online) === 1 || rec.online === true,
    createTime: rec.createTime,
    updateTime: rec.updateTime,
  };
}

// =========================
// 按钮：打开弹窗
// =========================
function openCreate() {
  Object.assign(formData, emptyForm());
  modalMode.value = 'create';
  modalVisible.value = true;
}

function openEdit(record: any) {
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'edit';
  modalVisible.value = true;
}

function openDetail(record: any) {
  Object.assign(formData, recordToForm(record));
  modalMode.value = 'detail';
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  modalMode.value = null;
  formRef.value?.resetFields?.();
  Object.assign(formData, emptyForm());
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
    // 校验失败提示由 form 自行展示
    return;
  }
  submitting.value = true;
  try {
    // 组装载荷：去除空密码（编辑时若未填则不发送）
    const payload: any = { ...formData };
    // id / userId 必需，若表单里有则带上
    if (formData.id !== undefined && formData.id !== null) {
      payload.id = formData.id;
      payload.userId = formData.id;
    }
    // 发送到后端
    if (modalMode.value === 'create') {
      if (!payload.password) {
        // 创建场景：已由规则保证必填，此处保险
        return;
      }
      await createUser(payload);
      message.success($t('user.action.create_success'));
    } else if (modalMode.value === 'edit') {
      // 编辑：若密码为空，后端约定不更新密码，则移除该字段避免写空
      if (!payload.password) {
        delete payload.password;
      }
      await updateUserInfo(payload);
      message.success($t('user.action.update_success'));
    }
    closeModal();
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || String(err || '');
    if (modalMode.value === 'create') {
      message.error(msg || $t('user.action.create_failed'));
    } else {
      message.error(msg || $t('user.action.update_failed'));
    }
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
    await deleteUser(record.id);
    message.success($t('user.action.delete_success'));
    await fetchData();
  } catch (err: any) {
    const msg = err?.message || err?.msg || $t('user.action.delete_failed');
    message.error(msg);
  } finally {
    deletingId.value = null;
  }
}

// 弹窗标题
const modalTitle = computed(() => {
  if (modalMode.value === 'create') return $t('user.modal.create_title');
  if (modalMode.value === 'edit') return $t('user.modal.edit_title');
  return $t('user.modal.detail_title');
});

// 表单内头像预览实时规范化：用户在输入框中粘贴/修改后即时生效
const formAvatarDisplayUrl = computed(() =>
  normalizeAvatarUrl(formData.avatar),
);

// =========================
// 头像裁剪弹窗相关状态
// =========================
const cropperVisible = ref(false);
const cropperImgUrl = ref('');
const cropperRef = ref<InstanceType<typeof VCropper> | null>(null);
const cropperUploading = ref(false);
const cropping = ref(false);

/**
 * 选择本地图片：通过 Upload 选择文件后读取为 DataURL，打开裁剪弹窗
 */
function handleAvatarSelect(event: UploadChangeParam) {
  const file = event.fileList?.[0]?.originFileObj;
  if (!file) return;

  if (!file.type.startsWith('image/')) {
    message.error($t('user.form.avatar_invalid_image'));
    return;
  }

  const reader = new FileReader();
  reader.addEventListener('load', (e) => {
    cropperImgUrl.value = e.target?.result as string;
    cropperVisible.value = true;
  });
  reader.addEventListener('error', () => {
    message.error($t('user.form.avatar_read_failed'));
  });
  reader.readAsDataURL(file);
}

/**
 * 关闭裁剪弹窗
 */
function closeCropper() {
  cropperVisible.value = false;
  cropperImgUrl.value = '';
}

/**
 * base64 字符串转 Blob（用于 FormData 上传）
 */
function base64ToBlob(base64: string, mime = 'image/jpeg'): Blob {
  const base64Data = base64.replace(/^data:image\/\w+;base64,/, '');
  const binaryString = atob(base64Data);
  const len = binaryString.length;
  const bytes = new Uint8Array(len);
  for (let i = 0; i < len; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }
  return new Blob([bytes], { type: mime });
}

/**
 * 裁剪并上传头像：
 * 1) 从 VCropper 获取 base64
 * 2) 转为 Blob + FormData 调用 uploadFile
 * 3) 将服务器返回的相对路径写入 formData.avatar，并触发校验刷新
 */
async function confirmCropperAvatar() {
  if (!cropperRef.value) return;
  cropping.value = true;
  try {
    const base64 = (await cropperRef.value.getCropImage(
      'image/jpeg',
      0.92,
      'base64',
    )) as string | undefined;

    if (!base64) {
      message.error($t('user.form.avatar_crop_failed'));
      return;
    }

    const blob = base64ToBlob(base64, 'image/jpeg');
    const uploadPayload = new FormData();
    uploadPayload.append(
      'file',
      blob,
      `avatar-${Date.now()}.jpg`,
    );

    cropperUploading.value = true;
    const res = await uploadFile(uploadPayload);

    // 兼容后端可能返回的字段：url / path / filePath
    const relativePath =
      res?.url ??
      res?.path ??
      res?.filePath ??
      res?.data?.url ??
      res?.data?.path ??
      res?.data?.filePath ??
      '';

    if (!relativePath) {
      message.error($t('user.form.avatar_upload_failed'));
      return;
    }

    // 写入表单：使用相对路径，由 normalizeAvatarUrl 统一补全
    formData.avatar = String(relativePath);
    message.success($t('user.form.avatar_upload_success'));

    // 主动触发表单校验刷新
    formRef.value?.validateField?.('avatar').catch(() => {});

    closeCropper();
  } catch (error: any) {
    const msg = error?.message || $t('user.form.avatar_upload_failed');
    message.error(msg);
  } finally {
    cropping.value = false;
    cropperUploading.value = false;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏 -->
      <Card v-if="loading" :styles="{ body: { padding: '24px' } }">
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card v-else-if="errorMsg" :styles="{ body: { padding: '24px' } }">
        <Result
          status="error"
          :title="$t('user.list.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('user.list.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 用户列表 -->
      <Card v-else :title="$t('user.list.title')" :styles="{ body: { padding: '0' } }">
        <template #extra>
          <Space>
            <Input
              v-model:value="keyword"
              :placeholder="$t('user.list.search_placeholder')"
              allow-clear
              class="w-64"
            />
            <Button type="primary" @click="openCreate">
              <template #icon>
                <Plus class="mr-1 size-4 align-middle" />
              </template>
              {{ $t('user.action.add') }}
            </Button>
          </Space>
        </template>

        <Table
          :columns="columns"
          :data-source="filteredData"
          :pagination="pagination"
          :scroll="{ x: 2500 }"
          :bordered="true"
          size="middle"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <!-- 用户列：头像 + 用户名 + 昵称 -->
            <template v-if="column.dataIndex === 'user'">
              <div class="flex items-center gap-2">
                <Avatar
                  :src="normalizeAvatarUrl(record.avatar as string)"
                  :size="36"
                  class="border border-border overflow-hidden object-cover"
                />
                <div class="flex flex-col leading-tight">
                  <span class="font-medium">{{ record.username }}</span>
                  <span class="text-xs text-muted-foreground">
                    {{ record.nickname }}
                  </span>
                </div>
              </div>
            </template>

            <!-- 状态 Tag -->
            <template v-else-if="column.dataIndex === 'status'">
              <Tag :color="Number(record.status) === 1 ? 'success' : 'error'">
                {{
                  $t(
                    Number(record.status) === 1
                      ? 'user.detail.status.active'
                      : 'user.detail.status.disabled',
                  )
                }}
              </Tag>
            </template>

            <!-- 在线状态 Tag -->
            <template v-else-if="column.dataIndex === 'online'">
              <Tag
                :color="
                  Number(record.online) === 1 || record.online === true
                    ? 'processing'
                    : 'default'
                "
              >
                {{
                  $t(
                    Number(record.online) === 1 || record.online === true
                      ? 'user.detail.online.online'
                      : 'user.detail.online.offline',
                  )
                }}
              </Tag>
            </template>

            <!-- 权限 Tag -->
            <template v-else-if="column.dataIndex === 'permissions'">
              <Tag color="blue">
                {{
                  record.permissions === undefined ||
                  record.permissions === null ||
                  record.permissions === ''
                    ? $t('user.detail.no_permissions')
                    : String(record.permissions)
                }}
              </Tag>
            </template>

            <!-- 操作列：详情 / 修改 / 删除 -->
            <template v-else-if="column.key === 'actions'">
              <Space :size="4" wrap>
                <Button
                  type="link"
                  size="small"
                  @click="openDetail(record)"
                >
                  {{ $t('user.action.detail') }}
                </Button>
                <Button
                  type="link"
                  size="small"
                  @click="openEdit(record)"
                >
                  {{ $t('user.action.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('user.action.confirm_delete_title')"
                  :description="$t('user.action.confirm_delete')"
                  :ok-text="$t('user.action.delete_text')"
                  :cancel-text="$t('user.action.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete(record)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingId === record.id"
                  >
                    {{ $t('user.action.delete') }}
                  </Button>
                </Popconfirm>
              </Space>
            </template>
          </template>

          <template #emptyText>
            {{ $t('user.list.empty') }}
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
      :ok-text="$t('user.action.save')"
      :cancel-text="$t('user.action.cancel')"
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
      <div v-if="modalMode === 'detail'">
        <div class="mb-4 flex items-center gap-4">
          <Avatar
            :src="formAvatarDisplayUrl"
            :size="72"
            class="border border-border overflow-hidden object-cover"
          />
          <div>
            <div class="text-xl font-semibold">{{ formData.username }}</div>
            <div class="text-muted-foreground">
              {{ formData.nickname }} · {{ formData.account }}
            </div>
            <Space class="mt-1" :size="4">
              <Tag
                :color="
                  Number(formData.status) === 1 ? 'success' : 'error'
                "
              >
                {{
                  $t(
                    Number(formData.status) === 1
                      ? 'user.detail.status.active'
                      : 'user.detail.status.disabled',
                  )
                }}
              </Tag>
              <Tag
                :color="
                  Number(formData.online as any) === 1
                    ? 'processing'
                    : 'default'
                "
              >
                {{
                  $t(
                    Number(formData.online as any) === 1
                      ? 'user.detail.online.online'
                      : 'user.detail.online.offline',
                  )
                }}
              </Tag>
              <Tag color="blue">
                {{ $t('user.detail.fields.points') }}: {{ formData.points }}
              </Tag>
            </Space>
          </div>
        </div>

        <Divider class="my-4">{{ $t('user.detail.basic_info') }}</Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('user.detail.fields.id')">
            {{ (formData as any).id ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.account')">
            {{ formData.account || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.username')">
            {{ formData.username || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.nickname')">
            {{ formData.nickname || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.gender')">
            {{ genderText(formData.gender) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.age')">
            {{ formData.age ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.occupation')">
            {{ formData.occupation || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.experience')">
            {{ formData.experience || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.permissions')">
            <Tag color="blue">
              {{ formData.permissions || $t('user.detail.no_permissions') }}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.online')">
            {{
              $t(
                Number(formData.online as any) === 1
                  ? 'user.detail.online.online'
                  : 'user.detail.online.offline',
              )
            }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">{{ $t('user.detail.contact_info') }}</Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('user.detail.fields.email')">
            {{ formData.email || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.phone')">
            {{ formData.phone || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">{{ $t('user.detail.extra_info') }}</Divider>
        <Descriptions :column="1" bordered size="small">
          <Descriptions.Item :label="$t('user.detail.fields.avatar')">
            <Avatar
              :src="formAvatarDisplayUrl"
              :size="48"
              class="border border-border overflow-hidden object-cover"
            />
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.hobbies')">
            {{ formData.hobbies || $t('user.detail.no_hobbies') }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.signature')">
            {{ formData.signature || '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.bio')">
            {{ formData.bio || '-' }}
          </Descriptions.Item>
        </Descriptions>

        <Divider class="my-4">{{ $t('user.detail.time_info') }}</Divider>
        <Descriptions :column="2" bordered size="small">
          <Descriptions.Item :label="$t('user.detail.fields.createTime')">
            {{ formatDateTime((formData as any).createTime) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('user.detail.fields.updateTime')">
            {{ formatDateTime((formData as any).updateTime) }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <!-- 创建 / 修改：专用外容器，彻底避开 Form 内部 Row/Col 塌陷问题 -->
      <div
        v-if="modalMode === 'create' || modalMode === 'edit'"
        class="user-form-wrapper"
        style="display: block; width: 100%;"
      >
        <!-- 用户 ID（仅修改模式）放在 Form 外作为兄弟节点，避免干扰 Form 布局 -->
        <div
          v-if="modalMode === 'edit' && (formData as any).id"
          class="mb-4"
        >
          <Descriptions :column="2" size="small" bordered>
            <Descriptions.Item
              :label="$t('user.detail.fields.id')"
              :span="2"
            >
              {{ (formData as any).id }}
            </Descriptions.Item>
          </Descriptions>
        </div>

        <!--
          Form 关键改动：
            1) layout="vertical"  — label 在上、控件在下，不依赖 Form 内部
                                    Row/Col flex，从根源规避 Form.Items 高度塌陷为 0 的 bug
            2) 外层使用 grid grid-cols-1 md:grid-cols-2 gap-* — 由 Tailwind Grid
                                    承担分列责任，每个 Form.Item 都是真实 grid cell，
                                    拥有实际宽高，不再受 Form 布局算法影响
        -->
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
            <!-- ==== 第一列 / 左列 ===== -->
            <FormItem
              :label="$t('user.form.field_account')"
              name="account"
              prop="account"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.account"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_account')}`"
                :disabled="modalMode === 'edit'"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_password')"
              name="password"
              prop="password"
              :extra="$t('user.form.password_hint')"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input.Password
                v-model:value="formData.password"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_password')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_username')"
              name="username"
              prop="username"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.username"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_username')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_nickname')"
              name="nickname"
              prop="nickname"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.nickname"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_nickname')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_gender')"
              name="gender"
              prop="gender"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Radio.Group v-model:value="formData.gender">
                <Radio :value="1">
                  {{ $t('user.form.gender_option_male') }}
                </Radio>
                <Radio :value="2">
                  {{ $t('user.form.gender_option_female') }}
                </Radio>
                <Radio :value="0">
                  {{ $t('user.form.gender_option_unknown') }}
                </Radio>
              </Radio.Group>
            </FormItem>

            <FormItem
              :label="$t('user.form.field_age')"
              name="age"
              prop="age"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.age"
                class="w-full"
                :min="0"
                :max="150"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_age')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_email')"
              name="email"
              prop="email"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.email"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_email')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_phone')"
              name="phone"
              prop="phone"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.phone"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_phone')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_occupation')"
              name="occupation"
              prop="occupation"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.occupation"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_occupation')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_experience')"
              name="experience"
              prop="experience"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.experience"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_experience')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_hobbies')"
              name="hobbies"
              prop="hobbies"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.hobbies"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_hobbies')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_signature')"
              name="signature"
              prop="signature"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.signature"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_signature')}`"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_points')"
              name="points"
              prop="points"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <InputNumber
                v-model:value="formData.points"
                class="w-full"
                :min="0"
              />
            </FormItem>

            <FormItem
              :label="$t('user.form.field_permissions')"
              name="permissions"
              prop="permissions"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-model:value="formData.permissions"
                class="w-full"
                :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_permissions')}`"
              />
            </FormItem>

            <!-- ==== 全宽区域（跨两列） ==== -->

            <!-- 头像：点击上传 + 裁剪，需占满全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('user.form.field_avatar')"
                name="avatar"
                prop="avatar"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <!-- 点击上传图片区域 -->
                <Upload
                  :max-count="1"
                  :show-upload-list="false"
                  accept="image/*"
                  :before-upload="() => false"
                  @change="handleAvatarSelect"
                >
                  <Button
                    class="avatar-upload-trigger flex w-full items-center gap-3 border-dashed"
                    style="height: auto; padding: 8px 12px;"
                  >
                    <Avatar
                      :src="formAvatarDisplayUrl"
                      :size="48"
                      class="border border-border flex-shrink-0 overflow-hidden object-cover"
                    />
                    <div class="flex flex-col text-left">
                      <span class="text-sm font-medium">
                        {{
                          formData.avatar
                            ? $t('user.form.avatar_change')
                            : $t('user.form.avatar_upload')
                        }}
                      </span>
                      <span class="text-xs text-muted-foreground">
                        {{ $t('user.form.avatar_tip') }}
                      </span>
                    </div>
                  </Button>
                </Upload>
              </FormItem>
            </div>

            <!-- 简介：大文本域，全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('user.form.field_bio')"
                name="bio"
                prop="bio"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Input.TextArea
                  v-model:value="formData.bio"
                  :rows="3"
                  class="w-full"
                  :placeholder="`${$t('user.form.placeholder_text')}${$t('user.form.field_bio')}`"
                />
              </FormItem>
            </div>

            <!-- 状态开关：全宽 -->
            <div class="col-span-2" style="width: 100%;">
              <FormItem
                :label="$t('user.form.field_status')"
                name="status"
                prop="status"
                style="display: block; width: 100%; margin-bottom: 16px;"
              >
                <Switch
                  v-model:checked="(formData.status as unknown as boolean)"
                  :checked-value="1"
                  :un-checked-value="0"
                  :checked-children="$t('user.form.status_enabled')"
                  :un-checked-children="$t('user.form.status_disabled')"
                />
              </FormItem>
            </div>
          </div>
        </Form>
      </div>
    </Modal>
    <!-- 头像裁剪弹窗 -->
    <Modal
      v-model:open="cropperVisible"
      :title="$t('user.form.avatar_cropper_title')"
      :width="720"
      :mask-closable="false"
      :confirm-loading="cropping || cropperUploading"
      :ok-text="$t('user.form.avatar_confirm')"
      :cancel-text="$t('user.action.cancel')"
      @ok="confirmCropperAvatar"
      @cancel="closeCropper"
    >
      <div v-if="cropperImgUrl" class="flex flex-col items-center">
        <VCropper
          ref="cropperRef"
          :img="cropperImgUrl"
          aspect-ratio="1:1"
          :width="600"
          :height="400"
        />
        <div class="mt-2 text-xs text-muted-foreground">
          {{ $t('user.form.avatar_cropper_tip') }}
        </div>
      </div>
    </Modal>
  </Page>
</template>