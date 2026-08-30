<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Empty,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Result,
  Select,
  Skeleton,
  Tag,
  Upload,
} from 'antdv-next';

import officialwebsiteApi from '#/api/management/travel/officialwebsite';
import { uploadFile } from '#/api/management/user/upload';

defineOptions({ name: 'OfficialWebsiteManagement' });

// =========================
// 板块字段配置类型
// =========================
type FieldType = 'active' | 'featured' | 'image' | 'number' | 'text' | 'textarea';

interface SectionField {
  key: string;
  type: FieldType;
  required?: boolean;
}

interface SectionConfig {
  key: string;
  mode: 'list' | 'single';
  fetch: () => Promise<any>;
  remove?: (id: any) => Promise<any>;
  save: (data: any) => Promise<any>;
  /** 列表卡片副标题字段 */
  subtitleKey?: string;
  /** 列表卡片描述字段 */
  descKey?: string;
  /** 列表卡片图片字段 */
  imageKey?: string;
  /** 编辑表单字段（空则使用 fields 全部） */
  fields: SectionField[];
  /** 新增条目默认值 */
  defaults?: Record<string, any>;
}

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

// =========================
// 板块配置（10 个板块）
// =========================
const sectionConfigs = computed<SectionConfig[]>(() => [
  {
    key: 'hero',
    mode: 'single',
    fetch: () => officialwebsiteApi.getHero(),
    save: (d) => officialwebsiteApi.saveHero(d),
    fields: [
      { key: 'badgeText', type: 'text' },
      { key: 'title', type: 'text' },
      { key: 'subtitle', type: 'text' },
      { key: 'description', type: 'textarea' },
      { key: 'bgImage', type: 'image' },
      { key: 'bgVideo', type: 'text' },
      { key: 'btnText', type: 'text' },
      { key: 'isActive', type: 'active' },
    ],
  },
  {
    key: 'destinations',
    mode: 'list',
    fetch: () => officialwebsiteApi.getDestinations(),
    save: (d) => officialwebsiteApi.saveDestination(d),
    remove: (id) => officialwebsiteApi.deleteDestination(id),
    subtitleKey: 'category',
    descKey: 'description',
    imageKey: 'imageUrl',
    fields: [
      { key: 'name', type: 'text', required: true },
      { key: 'category', type: 'text' },
      { key: 'description', type: 'textarea' },
      { key: 'duration', type: 'text' },
      { key: 'imageUrl', type: 'image' },
      { key: 'isFeatured', type: 'featured' },
      { key: 'price', type: 'number' },
      { key: 'priceCurrency', type: 'text' },
      { key: 'sortOrder', type: 'number' },
    ],
    defaults: { isFeatured: 0, sortOrder: 0, priceCurrency: 'CNY' },
  },
  {
    key: 'experiences',
    mode: 'list',
    fetch: () => officialwebsiteApi.getExperiences(),
    save: (d) => officialwebsiteApi.saveExperience(d),
    remove: (id) => officialwebsiteApi.deleteExperience(id),
    descKey: 'description',
    fields: [
      { key: 'title', type: 'text', required: true },
      { key: 'iconClass', type: 'text' },
      { key: 'description', type: 'textarea' },
      { key: 'isActive', type: 'active' },
      { key: 'sortOrder', type: 'number' },
    ],
    defaults: { isActive: 1, sortOrder: 0 },
  },
  {
    key: 'narrative',
    mode: 'list',
    fetch: () => officialwebsiteApi.getNarrative(),
    save: (d) => officialwebsiteApi.saveNarrative(d),
    remove: (id) => officialwebsiteApi.deleteNarrative(id),
    subtitleKey: 'author',
    fields: [
      { key: 'quoteText', type: 'textarea', required: true },
      { key: 'author', type: 'text' },
      { key: 'isActive', type: 'active' },
    ],
    defaults: { isActive: 1 },
  },
  {
    key: 'immersion',
    mode: 'list',
    fetch: () => officialwebsiteApi.getImmersion(),
    save: (d) => officialwebsiteApi.saveImmersion(d),
    remove: (id) => officialwebsiteApi.deleteImmersion(id),
    subtitleKey: 'subtitle',
    descKey: 'content',
    imageKey: 'imageUrl',
    fields: [
      { key: 'title', type: 'text', required: true },
      { key: 'subtitle', type: 'text' },
      { key: 'content', type: 'textarea' },
      { key: 'imageUrl', type: 'image' },
      { key: 'quoteText', type: 'textarea' },
      { key: 'isActive', type: 'active' },
      { key: 'sortOrder', type: 'number' },
    ],
    defaults: { isActive: 1, sortOrder: 0 },
  },
  {
    key: 'stories',
    mode: 'list',
    fetch: () => officialwebsiteApi.getStories(),
    save: (d) => officialwebsiteApi.saveStory(d),
    remove: (id) => officialwebsiteApi.deleteStory(id),
    subtitleKey: 'destination',
    descKey: 'content',
    imageKey: 'authorAvatar',
    fields: [
      { key: 'authorName', type: 'text', required: true },
      { key: 'authorRole', type: 'text' },
      { key: 'authorAvatar', type: 'image' },
      { key: 'destination', type: 'text' },
      { key: 'rating', type: 'number' },
      { key: 'isFeatured', type: 'featured' },
      { key: 'content', type: 'textarea' },
      { key: 'sortOrder', type: 'number' },
    ],
    defaults: { isFeatured: 0, sortOrder: 0, rating: 5 },
  },
  {
    key: 'cta',
    mode: 'single',
    fetch: () => officialwebsiteApi.getCta(),
    save: (d) => officialwebsiteApi.saveCta(d),
    fields: [
      { key: 'title', type: 'text' },
      { key: 'description', type: 'textarea' },
      { key: 'btnText', type: 'text' },
      { key: 'btnUrl', type: 'text' },
      { key: 'bgColor', type: 'text' },
      { key: 'isActive', type: 'active' },
    ],
  },
  {
    key: 'footer',
    mode: 'single',
    fetch: () => officialwebsiteApi.getFooter(),
    save: (d) => officialwebsiteApi.saveFooter(d),
    fields: [
      { key: 'brandName', type: 'text' },
      { key: 'brandDescription', type: 'textarea' },
      { key: 'copyrightText', type: 'text' },
      { key: 'socialLinks', type: 'textarea' },
    ],
  },
  {
    key: 'footerLinks',
    mode: 'list',
    fetch: () => officialwebsiteApi.getFooterLinks(''),
    save: (d) => officialwebsiteApi.saveFooterLinks(d),
    remove: (id) => officialwebsiteApi.deleteFooterLink(id),
    subtitleKey: 'category',
    descKey: 'url',
    fields: [
      { key: 'title', type: 'text', required: true },
      { key: 'category', type: 'text' },
      { key: 'url', type: 'text' },
      { key: 'sortOrder', type: 'number' },
    ],
    defaults: { sortOrder: 0 },
  },
  {
    key: 'settings',
    mode: 'single',
    fetch: () => officialwebsiteApi.getSettings(),
    save: (d) => officialwebsiteApi.saveSettings(d),
    fields: [
      { key: 'site_name', type: 'text' },
      { key: 'site_slogan', type: 'text' },
      { key: 'language', type: 'text' },
      { key: 'site_description', type: 'textarea' },
      { key: 'default_currency', type: 'text' },
    ],
  },
]);

// =========================
// 状态
// =========================
const activeSection = ref('hero');
const loading = ref(false);
const errorMsg = ref('');

// 单对象板块数据
const singleData = ref<Record<string, any>>({});
// 列表板块数据
const listData = ref<any[]>([]);

// 图片加载失败集合（列表 key: `${section}:${id}`）
const imageErrorSet = reactive<Set<string>>(new Set());

const currentConfig = computed(
  () =>
    sectionConfigs.value.find((s) => s.key === activeSection.value) ??
    sectionConfigs.value[0]!,
);

const sectionTabs = computed(() =>
  sectionConfigs.value.map((s) => ({
    key: s.key,
    label: $t(`travel.official_site.sections.${s.key}`),
  })),
);

// =========================
// 工具
// =========================
function fieldLabel(key: string): string {
  return $t(`travel.official_site.fields.${key}`);
}

function sectionLabel(key: string): string {
  return $t(`travel.official_site.sections.${key}`);
}

function displayValue(value: any): string {
  if (value === null || value === undefined || value === '') return '-';
  return String(value);
}

function isImageFailed(id: any): boolean {
  return imageErrorSet.has(`${activeSection.value}:${id}`);
}

function handleImageError(id: any) {
  imageErrorSet.add(`${activeSection.value}:${id}`);
}

/** 状态值显示为 Tag */
function isOn(value: any): boolean {
  return Number(value) === 1;
}

/** 当前板块列表条目的状态 Tag（isActive/isFeatured 二选一） */
function statusFieldOf(record: any): null | string {
  if ('isActive' in record) return 'isActive';
  if ('isFeatured' in record) return 'isFeatured';
  return null;
}

// =========================
// 数据加载
// =========================
async function loadSection() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const cfg = currentConfig.value;
    const res: any = await cfg.fetch();
    if (cfg.mode === 'single') {
      singleData.value = res && typeof res === 'object' ? res : {};
      listData.value = [];
    } else {
      const records: any[] = Array.isArray(res)
        ? res
        : (res?.records ?? res?.list ?? res?.items ?? []);
      listData.value = records;
      singleData.value = {};
    }
    imageErrorSet.clear();
  } catch (error: any) {
    errorMsg.value = error?.message || $t('travel.official_site.error_desc');
  } finally {
    loading.value = false;
  }
}

function handleSwitchSection(key: string) {
  if (activeSection.value === key) return;
  activeSection.value = key;
  loadSection();
}

onMounted(() => {
  loadSection();
});

// =========================
// 弹窗 / 动态表单
// =========================
const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref<InstanceType<typeof Form> | null>(null);
const editingId = ref<null | number | string>(null);
const imageUploading = ref(false);
/** 当前正在上传的目标字段（image 类型字段上传后写回） */
const uploadingField = ref('');

const formRecord = reactive<Record<string, any>>({});

const modalTitle = computed(() => {
  if (currentConfig.value.mode === 'single')
    return `${sectionLabel(activeSection.value)} · ${$t('travel.official_site.edit_single_title')}`;
  return editingId.value === null
    ? `${sectionLabel(activeSection.value)} · ${$t('travel.official_site.create_title')}`
    : `${sectionLabel(activeSection.value)} · ${$t('travel.official_site.edit_single_title')}`;
});

const formRules = computed(() => {
  const rules: Record<string, any[]> = {};
  for (const f of currentConfig.value.fields) {
    if (f.required) {
      rules[f.key] = [
        {
          required: true,
          message: `${fieldLabel(f.key)}`,
          trigger: f.type === 'textarea' ? 'blur' : 'change',
        },
      ];
    }
  }
  return rules;
});

function openEditSingle() {
  editingId.value = (singleData.value as any)?.id ?? null;
  Object.keys(formRecord).forEach((k) => delete formRecord[k]);
  Object.assign(formRecord, singleData.value);
  modalVisible.value = true;
}

function openCreateItem() {
  editingId.value = null;
  Object.keys(formRecord).forEach((k) => delete formRecord[k]);
  Object.assign(formRecord, currentConfig.value.defaults ?? {});
  modalVisible.value = true;
}

function openEditItem(record: any) {
  editingId.value = record?.id ?? null;
  Object.keys(formRecord).forEach((k) => delete formRecord[k]);
  Object.assign(formRecord, record);
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
  formRef.value?.resetFields?.();
  Object.keys(formRecord).forEach((k) => delete formRecord[k]);
}

// =========================
// 图片上传（image 类型字段）
// =========================
async function handleFieldImageUpload(file: File) {
  const field = uploadingField.value;
  if (!field) return;
  if (!file.type.startsWith('image/')) {
    message.error($t('travel.official_site.image_invalid_image'));
    return;
  }
  imageUploading.value = true;
  try {
    const res: any = await uploadFile(file);
    const relativePath =
      res?.url ?? res?.path ?? res?.filePath ?? res?.data?.url ?? '';
    if (!relativePath) {
      message.error($t('travel.official_site.image_upload_failed'));
      return;
    }
    formRecord[field] = String(relativePath);
    message.success($t('travel.official_site.image_upload_success'));
  } catch (error: any) {
    const msg = error?.message || $t('travel.official_site.image_upload_failed');
    message.error(msg);
  } finally {
    imageUploading.value = false;
    uploadingField.value = '';
  }
}

function handleFieldImageChange(event: any) {
  const file = event?.fileList?.[0]?.originFileObj;
  if (file) handleFieldImageUpload(file as File);
}

function hideBrokenImage(e: Event) {
  const target = e.target as HTMLImageElement;
  if (target) target.style.display = 'none';
}

// =========================
// 保存（单对象 / 列表条目）
// =========================
async function handleSave() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  submitting.value = true;
  try {
    const payload: any = { ...formRecord };
    if (
      currentConfig.value.mode === 'list' &&
      editingId.value !== null &&
      editingId.value !== undefined
    ) {
      payload.id = editingId.value;
    }
    await currentConfig.value.save(payload);
    message.success($t('travel.official_site.save_success'));
    closeModal();
    await loadSection();
  } catch (err: any) {
    const msg = err?.message || err?.msg || $t('travel.official_site.save_failed');
    message.error(msg);
  } finally {
    submitting.value = false;
  }
}

// =========================
// 删除（仅列表板块）
// =========================
const deletingId = ref<null | number | string>(null);
async function handleDelete(record: any) {
  const remove = currentConfig.value.remove;
  if (!remove || record?.id === undefined || record?.id === null) return;
  deletingId.value = record.id;
  try {
    await remove(record.id);
    message.success($t('travel.official_site.delete_success'));
    await loadSection();
  } catch (err: any) {
    const msg =
      err?.message || err?.msg || $t('travel.official_site.delete_failed');
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
        v-if="loading"
        :title="$t('travel.official_site.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 8 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg"
        :title="$t('travel.official_site.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('travel.official_site.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="loadSection">
              {{ $t('travel.official_site.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 板块内容 -->
      <Card
        v-else
        :title="`${$t('travel.official_site.title')} · ${sectionLabel(activeSection)}`"
        :styles="{ body: { padding: '16px' } }"
      >
        <template #extra>
          <!-- 单对象：编辑按钮；列表：新增按钮 -->
          <Button
            v-if="currentConfig.mode === 'single'"
            type="primary"
            @click="openEditSingle"
          >
            <template #icon>
              <span class="i-lucide-pencil mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('travel.official_site.edit') }}
          </Button>
          <Button v-else type="primary" @click="openCreateItem">
            <template #icon>
              <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('travel.official_site.add') }}
          </Button>
        </template>

        <!-- 板块切换按钮组 -->
        <div class="section-bar">
          <Button
            v-for="tab in sectionTabs"
            :key="tab.key"
            :type="activeSection === tab.key ? 'primary' : 'default'"
            size="small"
            class="section-pill"
            @click="handleSwitchSection(tab.key)"
          >
            {{ tab.label }}
          </Button>
        </div>

        <!-- 单对象板块：Descriptions 全字段展示 -->
        <template v-if="currentConfig.mode === 'single'">
          <Empty
            v-if="!singleData || Object.keys(singleData).length === 0"
            :description="$t('travel.official_site.single_empty')"
          />
          <Descriptions v-else :column="1" bordered size="small">
            <Descriptions.Item
              v-for="f in currentConfig.fields"
              :key="f.key"
              :label="fieldLabel(f.key)"
            >
              <!-- 图片字段 -->
              <img
                v-if="f.type === 'image' && singleData[f.key]"
                :src="normalizeImageUrl(singleData[f.key])"
                :alt="f.key"
                loading="lazy"
                class="h-16 w-24 rounded border border-border object-cover"
                @error="hideBrokenImage"
              />
              <!-- 状态字段 -->
              <Tag
                v-else-if="f.type === 'active' || f.type === 'featured'"
                :color="isOn(singleData[f.key]) ? 'green' : 'default'"
              >
                {{
                  isOn(singleData[f.key])
                    ? $t(`travel.official_site.${f.type === 'active' ? 'active' : 'featured'}.on`)
                    : $t(`travel.official_site.${f.type === 'active' ? 'active' : 'featured'}.off`)
                }}
              </Tag>
              <span v-else class="whitespace-pre-wrap text-sm">
                {{ displayValue(singleData[f.key]) }}
              </span>
            </Descriptions.Item>
            <!-- ID -->
            <Descriptions.Item
              v-if="singleData.id !== undefined"
              :label="fieldLabel('id')"
            >
              {{ singleData.id }}
            </Descriptions.Item>
          </Descriptions>
        </template>

        <!-- 列表板块：卡片网格 -->
        <template v-else>
          <Empty
            v-if="!listData.length"
            :description="$t('travel.official_site.list_empty')"
          />
          <div v-else class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
            <div
              v-for="item in listData"
              :key="item.id"
              class="section-card"
            >
              <!-- 卡片图片 -->
              <img
                v-if="currentConfig.imageKey && item[currentConfig.imageKey] && !isImageFailed(item.id)"
                :src="normalizeImageUrl(item[currentConfig.imageKey])"
                :alt="item.title ?? item.name ?? item.quoteText ?? item.authorName"
                loading="lazy"
                class="section-card-image"
                @error="handleImageError(item.id)"
              />
              <div
                v-else-if="currentConfig.imageKey"
                class="section-card-image section-card-image-fallback"
              >
                <span class="i-lucide-image-off size-6 text-muted-foreground"></span>
              </div>

              <!-- 卡片内容 -->
              <div class="section-card-body">
                <div class="flex items-start justify-between gap-2">
                  <div class="min-w-0">
                    <div class="truncate text-base font-semibold">
                      {{ displayValue(item.title ?? item.name ?? item.quoteText ?? item.authorName) }}
                    </div>
                    <div
                      v-if="currentConfig.subtitleKey && item[currentConfig.subtitleKey]"
                      class="truncate text-xs text-muted-foreground"
                    >
                      {{ item[currentConfig.subtitleKey] }}
                    </div>
                  </div>
                  <!-- 状态 Tag -->
                  <Tag
                    v-if="statusFieldOf(item)"
                    :color="isOn(item[statusFieldOf(item)!]) ? 'green' : 'default'"
                  >
                    {{
                      isOn(item[statusFieldOf(item)!])
                        ? $t(`travel.official_site.${statusFieldOf(item) === 'isActive' ? 'active' : 'featured'}.on`)
                        : $t(`travel.official_site.${statusFieldOf(item) === 'isActive' ? 'active' : 'featured'}.off`)
                    }}
                  </Tag>
                </div>

                <!-- 描述 -->
                <p
                  v-if="currentConfig.descKey && item[currentConfig.descKey]"
                  class="section-card-desc"
                >
                  {{ item[currentConfig.descKey] }}
                </p>

                <!-- 附加信息：价格 / 时长 / 评分 -->
                <div class="mt-2 flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                  <span v-if="item.price !== undefined && item.price !== null" class="font-medium text-red-500">
                    ¥{{ item.price }}
                    <span v-if="item.duration" class="ml-1 font-normal text-muted-foreground">
                      {{ item.duration }}
                    </span>
                  </span>
                  <span v-if="item.rating !== undefined && item.rating !== null" class="inline-flex items-center gap-1">
                    <span class="i-lucide-star size-3.5"></span>
                    {{ item.rating }}
                  </span>
                  <span v-if="item.sortOrder !== undefined && item.sortOrder !== null">
                    #{{ item.sortOrder }}
                  </span>
                </div>

                <!-- 操作 -->
                <div class="mt-3 flex items-center gap-2 border-t border-border pt-3">
                  <Button type="link" size="small" @click="openEditItem(item)">
                    {{ $t('travel.official_site.edit') }}
                  </Button>
                  <Popconfirm
                    :title="$t('travel.official_site.delete_confirm_title')"
                    :description="$t('travel.official_site.delete_confirm')"
                    :ok-text="$t('travel.official_site.delete')"
                    :cancel-text="$t('travel.official_site.cancel')"
                    :ok-button-props="{ danger: true }"
                    @confirm="handleDelete(item)"
                  >
                    <Button
                      type="link"
                      size="small"
                      danger
                      :loading="deletingId === item.id"
                    >
                      {{ $t('travel.official_site.delete') }}
                    </Button>
                  </Popconfirm>
                </div>
              </div>
            </div>
          </div>
        </template>
      </Card>
    </div>

    <!-- 新增 / 编辑 弹窗（动态字段表单） -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="640"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('travel.official_site.save')"
      :cancel-text="$t('travel.official_site.cancel')"
      :styles="{
        body: {
          maxHeight: 'calc(100vh - 260px)',
          overflowY: 'auto',
          padding: '20px 24px',
        },
      }"
      @ok="handleSave"
      @cancel="closeModal"
    >
      <div
        v-if="editingId !== null && editingId !== undefined && currentConfig.mode === 'list'"
        class="mb-4"
      >
        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="fieldLabel('id')" :span="2">
            {{ editingId }}
          </Descriptions.Item>
        </Descriptions>
      </div>

      <Form
        ref="formRef"
        :model="formRecord"
        :rules="formRules"
        layout="vertical"
        class="w-full"
        style="display: block;"
      >
        <div
          class="grid grid-cols-1 gap-x-6 gap-y-1 md:grid-cols-2"
          style="display: grid;"
        >
          <div
            v-for="f in currentConfig.fields"
            :key="f.key"
            :class="f.type === 'textarea' ? 'col-span-2' : 'col-span-1'"
            style="width: 100%;"
          >
            <FormItem
              :label="fieldLabel(f.key)"
              :name="f.key"
              :prop="f.key"
              style="display: block; width: 100%; margin-bottom: 16px;"
            >
              <Input
                v-if="f.type === 'text'"
                v-model:value="formRecord[f.key]"
                class="w-full"
                :placeholder="fieldLabel(f.key)"
              />
              <Input.TextArea
                v-else-if="f.type === 'textarea'"
                v-model:value="formRecord[f.key]"
                :rows="4"
                class="w-full"
                :placeholder="fieldLabel(f.key)"
              />
              <InputNumber
                v-else-if="f.type === 'number'"
                v-model:value="formRecord[f.key]"
                class="w-full"
                :min="0"
                :max="f.key === 'rating' ? 5 : undefined"
                :step="f.key === 'rating' ? 0.5 : 1"
                :placeholder="fieldLabel(f.key)"
              />
              <Select
                v-else-if="f.type === 'active'"
                v-model:value="formRecord[f.key]"
                class="w-full"
                :options="[
                  { label: $t('travel.official_site.active.on'), value: 1 },
                  { label: $t('travel.official_site.active.off'), value: 0 },
                ]"
              />
              <Select
                v-else-if="f.type === 'featured'"
                v-model:value="formRecord[f.key]"
                class="w-full"
                :options="[
                  { label: $t('travel.official_site.featured.on'), value: 1 },
                  { label: $t('travel.official_site.featured.off'), value: 0 },
                ]"
              />
              <!-- 图片字段：Input + 上传按钮 -->
              <div v-else-if="f.type === 'image'" class="flex items-center gap-2">
                <Input
                  v-model:value="formRecord[f.key]"
                  class="w-full"
                  :placeholder="fieldLabel(f.key)"
                />
                <Upload
                  :max-count="1"
                  :show-upload-list="false"
                  accept="image/*"
                  :before-upload="() => false"
                  @change="uploadingField = f.key; handleFieldImageChange($event)"
                >
                  <Button :loading="imageUploading && uploadingField === f.key">
                    {{ $t('travel.official_site.image_upload') }}
                  </Button>
                </Upload>
              </div>
            </FormItem>
          </div>
        </div>
      </Form>
    </Modal>
  </Page>
</template>

<style scoped>
.section-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.section-pill {
  border-radius: 999px;
}

.section-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border: 1px solid var(--border-color, #f0f0f0);
  border-radius: 8px;
  background-color: var(--content-bg, #fff);
  transition:
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.section-card:hover {
  box-shadow: 0 8px 24px rgb(0 0 0 / 10%);
  transform: translateY(-2px);
}

.section-card-image {
  width: 100%;
  height: 140px;
  object-fit: cover;
}

.section-card-image-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--app-fill-color, rgb(0 0 0 / 4%));
}

.section-card-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  padding: 12px 14px;
}

.section-card-desc {
  display: -webkit-box;
  margin: 8px 0 0;
  overflow: hidden;
  font-size: 13px;
  line-height: 1.6;
  color: var(--muted-foreground-color, rgb(0 0 0 / 45%));
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}
</style>
