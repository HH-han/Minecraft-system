<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Form,
  FormItem,
  Input,
  message,
  Modal,
  Popconfirm,
  Result,
  Skeleton,
  Tag,
} from 'antdv-next';

import websiteIntroductionApi from '#/api/management/content/websiteIntroduction';

defineOptions({ name: 'WebsiteIntroductionManagement' });

interface FeatureItem {
  featureId?: null | number;
  id: number;
  itemName?: null | string;
  sortOrder?: null | number;
}

interface Feature {
  bgColor?: null | string;
  createdAt?: null | string;
  description?: null | string;
  icon?: null | string;
  id: number;
  items?: FeatureItem[];
  sortOrder?: null | number;
  title?: null | string;
  updatedAt?: null | string;
}

interface Milestone {
  createdAt?: null | string;
  description?: null | string;
  id: number;
  milestoneDate?: null | string;
  sortOrder?: null | number;
  title?: null | string;
  updatedAt?: null | string;
}

interface PlatformStat {
  id: number;
  sortOrder?: null | number;
  statIcon?: null | string;
  statLabel?: null | string;
  statValue?: null | string;
  updatedAt?: null | string;
}

interface TechItem {
  categoryId?: null | number;
  description?: null | string;
  id: number;
  sortOrder?: null | number;
  techName?: null | string;
}

interface TechCategory {
  categoryName?: null | string;
  createdAt?: null | string;
  id: number;
  items?: TechItem[];
  sortOrder?: null | number;
  themeClass?: null | string;
}

type EditType = 'feature' | 'milestone' | 'stat' | 'tech';

// =========================
// 状态
// =========================
const loading = ref(false);
const errorMsg = ref('');
const initialized = ref(false);
const statList = ref<PlatformStat[]>([]);
const featureList = ref<Feature[]>([]);
const techList = ref<TechCategory[]>([]);
const milestoneList = ref<Milestone[]>([]);
const deletingKey = ref('');

// =========================
// 工具
// =========================
function displayValue(value: any): string {
  if (value === null || value === undefined || value === '') return '-';
  return String(value);
}

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ');
}

function toArray(value: any): any[] {
  if (Array.isArray(value)) return value;
  if (Array.isArray(value?.data)) return value.data;
  return [];
}

function sortByOrder<T extends { id: number; sortOrder?: null | number }>(
  list: T[],
): T[] {
  return [...list].sort(
    (a, b) => Number(a.sortOrder ?? 0) - Number(b.sortOrder ?? 0) || a.id - b.id,
  );
}

function toNumberOrNull(value: string): null | number {
  const trimmed = value.trim();
  if (!trimmed) return null;
  const n = Number(trimmed);
  return Number.isFinite(n) ? n : null;
}

const isEmptyAll = computed(
  () =>
    statList.value.length === 0 &&
    featureList.value.length === 0 &&
    techList.value.length === 0 &&
    milestoneList.value.length === 0,
);

// =========================
// 数据加载
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const [stats, features, techs, milestones] = await Promise.all([
      websiteIntroductionApi.getPlatformStatList(),
      websiteIntroductionApi.getFeatureList(),
      websiteIntroductionApi.getTechCategoryList(),
      websiteIntroductionApi.getMilestoneList(),
    ]);
    statList.value = sortByOrder(toArray(stats));
    featureList.value = sortByOrder(toArray(features));
    techList.value = sortByOrder(toArray(techs));
    milestoneList.value = sortByOrder(toArray(milestones));
  } catch (error: any) {
    errorMsg.value = error?.message || $t('content.intro_page.error_desc');
  } finally {
    initialized.value = true;
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

// =========================
// 新增 / 编辑
// =========================
const modalVisible = ref(false);
const submitting = ref(false);
const formRef = ref<InstanceType<typeof Form> | null>(null);
const editType = ref<EditType>('feature');
const editingId = ref<null | number>(null);

const formRecord = reactive({
  bgColor: '',
  categoryName: '',
  description: '',
  icon: '',
  milestoneDate: '',
  sortOrder: '',
  statIcon: '',
  statLabel: '',
  statValue: '',
  themeClass: '',
  title: '',
});

const SECTION_KEY: Record<EditType, string> = {
  feature: 'features',
  milestone: 'milestones',
  stat: 'platform_stats',
  tech: 'tech_categories',
};

function sectionLabel(type: EditType): string {
  return $t(`content.intro_page.section.${SECTION_KEY[type]}`);
}

const modalTitle = computed(() => {
  const base =
    editingId.value === null
      ? $t('content.intro_page.create_title')
      : $t('content.intro_page.edit_title');
  return `${base} · ${sectionLabel(editType.value)}`;
});

const formRules = computed(() => {
  const req = (msg: string) => [{ required: true, message: msg }];
  if (editType.value === 'stat') {
    return {
      statLabel: req($t('content.intro_page.label_required')),
      statValue: req($t('content.intro_page.value_required')),
    };
  }
  if (editType.value === 'feature') {
    return { title: req($t('content.intro_page.title_required')) };
  }
  if (editType.value === 'milestone') {
    return {
      milestoneDate: req($t('content.intro_page.date_required')),
      title: req($t('content.intro_page.title_required')),
    };
  }
  return { categoryName: req($t('content.intro_page.name_required')) };
});

function resetForm() {
  formRecord.bgColor = '';
  formRecord.categoryName = '';
  formRecord.description = '';
  formRecord.icon = '';
  formRecord.milestoneDate = '';
  formRecord.sortOrder = '';
  formRecord.statIcon = '';
  formRecord.statLabel = '';
  formRecord.statValue = '';
  formRecord.themeClass = '';
  formRecord.title = '';
  formRef.value?.clearValidate?.();
}

function openCreate(type: EditType) {
  editType.value = type;
  editingId.value = null;
  resetForm();
  modalVisible.value = true;
}

function openEdit(type: EditType, record: any) {
  editType.value = type;
  editingId.value = record.id;
  resetForm();
  if (type === 'stat') {
    formRecord.statLabel = record.statLabel ?? '';
    formRecord.statValue = record.statValue ?? '';
    formRecord.statIcon = record.statIcon ?? '';
  } else if (type === 'feature') {
    formRecord.title = record.title ?? '';
    formRecord.description = record.description ?? '';
    formRecord.icon = record.icon ?? '';
    formRecord.bgColor = record.bgColor ?? '';
  } else if (type === 'milestone') {
    formRecord.milestoneDate = record.milestoneDate ?? '';
    formRecord.title = record.title ?? '';
    formRecord.description = record.description ?? '';
  } else {
    formRecord.categoryName = record.categoryName ?? '';
    formRecord.themeClass = record.themeClass ?? '';
  }
  formRecord.sortOrder =
    record.sortOrder === null || record.sortOrder === undefined
      ? ''
      : String(record.sortOrder);
  modalVisible.value = true;
}

function closeModal() {
  modalVisible.value = false;
  editingId.value = null;
  formRef.value?.resetFields?.();
}

async function handleSave() {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }
  const id = editingId.value;
  const sortOrder = toNumberOrNull(formRecord.sortOrder);
  submitting.value = true;
  try {
    if (editType.value === 'stat') {
      const payload: any = {
        statIcon: formRecord.statIcon.trim() || null,
        statLabel: formRecord.statLabel,
        statValue: formRecord.statValue,
        sortOrder,
      };
      if (id !== null) payload.id = id;
      await websiteIntroductionApi.savePlatformStat(payload);
    } else if (editType.value === 'feature') {
      const payload: any = {
        title: formRecord.title,
        description: formRecord.description.trim() || null,
        icon: formRecord.icon.trim() || null,
        bgColor: formRecord.bgColor.trim() || null,
        sortOrder,
      };
      if (id !== null) payload.id = id;
      await websiteIntroductionApi.saveFeature(payload);
    } else if (editType.value === 'milestone') {
      const payload: any = {
        milestoneDate: formRecord.milestoneDate.trim(),
        title: formRecord.title,
        description: formRecord.description.trim() || null,
        sortOrder,
      };
      if (id !== null) payload.id = id;
      await websiteIntroductionApi.saveMilestone(payload);
    } else {
      const payload: any = {
        categoryName: formRecord.categoryName,
        themeClass: formRecord.themeClass.trim() || null,
        sortOrder,
      };
      if (id !== null) payload.id = id;
      await websiteIntroductionApi.saveTechCategory(payload);
    }
    message.success($t('content.intro_page.save_success'));
    closeModal();
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.intro_page.save_failed'));
  } finally {
    submitting.value = false;
  }
}

// =========================
// 删除
// =========================
async function handleDelete(type: EditType, record: any) {
  deletingKey.value = `${type}-${record.id}`;
  try {
    if (type === 'stat') {
      await websiteIntroductionApi.deletePlatformStat(record.id);
    } else if (type === 'feature') {
      await websiteIntroductionApi.deleteFeature(record.id);
    } else if (type === 'milestone') {
      await websiteIntroductionApi.deleteMilestone(record.id);
    } else {
      await websiteIntroductionApi.deleteTechCategory(record.id);
    }
    message.success($t('content.intro_page.delete_success'));
    await fetchData();
  } catch (err: any) {
    message.error(err?.message || $t('content.intro_page.delete_failed'));
  } finally {
    deletingKey.value = '';
  }
}

// =========================
// 子项管理（功能子项 / 技术项）
// =========================
const itemsModalVisible = ref(false);
const itemsParentType = ref<'feature' | 'tech'>('feature');
const itemsParent = ref<any>(null);
const newItemName = ref('');
const newItemDesc = ref('');
const addingItem = ref(false);
const deletingItemId = ref<null | number>(null);

const itemsParentTitle = computed(() => {
  if (!itemsParent.value) return '';
  return itemsParentType.value === 'feature'
    ? displayValue(itemsParent.value.title)
    : displayValue(itemsParent.value.categoryName);
});

const parentItems = computed<any[]>(() => itemsParent.value?.items ?? []);

function openItems(type: 'feature' | 'tech', record: any) {
  itemsParentType.value = type;
  itemsParent.value = { ...record, items: [...(record.items ?? [])] };
  newItemName.value = '';
  newItemDesc.value = '';
  itemsModalVisible.value = true;
}

function syncItemsParent() {
  const list =
    itemsParentType.value === 'feature' ? featureList.value : techList.value;
  const updated = list.find((x: any) => x.id === itemsParent.value?.id);
  if (updated) {
    itemsParent.value = { ...updated, items: [...(updated.items ?? [])] };
  }
}

async function handleAddItem() {
  const name = newItemName.value.trim();
  if (!name || !itemsParent.value) return;
  addingItem.value = true;
  try {
    if (itemsParentType.value === 'feature') {
      await websiteIntroductionApi.saveFeatureItem({
        featureId: itemsParent.value.id,
        itemName: name,
        sortOrder: parentItems.value.length + 1,
      });
    } else {
      await websiteIntroductionApi.saveTechItem({
        categoryId: itemsParent.value.id,
        techName: name,
        description: newItemDesc.value.trim() || null,
        sortOrder: parentItems.value.length + 1,
      });
    }
    newItemName.value = '';
    newItemDesc.value = '';
    await fetchData();
    syncItemsParent();
  } catch (err: any) {
    message.error(err?.message || $t('content.intro_page.save_failed'));
  } finally {
    addingItem.value = false;
  }
}

async function handleDeleteItem(item: any) {
  deletingItemId.value = item.id;
  try {
    if (itemsParentType.value === 'feature') {
      await websiteIntroductionApi.deleteFeatureItem(item.id);
    } else {
      await websiteIntroductionApi.deleteTechItem(item.id);
    }
    message.success($t('content.intro_page.item_delete_success'));
    await fetchData();
    syncItemsParent();
  } catch (err: any) {
    message.error(err?.message || $t('content.intro_page.delete_failed'));
  } finally {
    deletingItemId.value = null;
  }
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <!-- 加载中：骨架屏（仅首次加载） -->
      <Card
        v-if="loading && !initialized"
        :title="$t('content.intro_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Skeleton :active="true" :paragraph="{ rows: 10 }" />
      </Card>

      <!-- 加载失败：错误占位 + 重试 -->
      <Card
        v-else-if="errorMsg && isEmptyAll"
        :title="$t('content.intro_page.title')"
        :styles="{ body: { padding: '24px' } }"
      >
        <Result
          status="error"
          :title="$t('content.intro_page.error_title')"
          :sub-title="errorMsg"
        >
          <template #extra>
            <Button type="primary" @click="fetchData">
              {{ $t('content.intro_page.retry') }}
            </Button>
          </template>
        </Result>
      </Card>

      <!-- 内容 -->
      <Card
        v-else
        :title="$t('content.intro_page.title')"
        :styles="{ body: { padding: '16px 20px' } }"
      >
        <template #extra>
          <Button :loading="loading" @click="fetchData">
            <template #icon>
              <span class="i-lucide-refresh-cw mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('content.intro_page.refresh') }}
          </Button>
        </template>

        <!-- 平台数据 -->
        <section>
          <div class="mb-3 flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="text-base font-semibold">
                {{ $t('content.intro_page.section.platform_stats') }}
              </span>
              <Tag>{{ statList.length }}</Tag>
            </div>
            <Button size="small" type="primary" @click="openCreate('stat')">
              <template #icon>
                <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
              </template>
              {{ $t('content.intro_page.add') }}
            </Button>
          </div>

          <div
            v-if="statList.length > 0"
            class="grid grid-cols-2 gap-3 md:grid-cols-4"
          >
            <div
              v-for="stat in statList"
              :key="stat.id"
              class="group relative rounded-lg border p-4 text-center transition hover:border-primary/40 hover:shadow-sm"
            >
              <div class="text-2xl font-semibold tabular-nums text-primary">
                {{ displayValue(stat.statValue) }}
              </div>
              <div class="mt-1 text-xs text-muted-foreground">
                {{ displayValue(stat.statLabel) }}
              </div>
              <div
                class="absolute right-2 top-2 hidden items-center gap-1 group-hover:flex"
              >
                <Button
                  type="link"
                  size="small"
                  @click="openEdit('stat', stat)"
                >
                  {{ $t('content.intro_page.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('content.intro_page.delete_confirm_title')"
                  :description="$t('content.intro_page.delete_confirm')"
                  :ok-text="$t('content.intro_page.delete')"
                  :cancel-text="$t('content.intro_page.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete('stat', stat)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingKey === `stat-${stat.id}`"
                  >
                    {{ $t('content.intro_page.delete') }}
                  </Button>
                </Popconfirm>
              </div>
            </div>
          </div>
          <div v-else class="text-sm text-muted-foreground">
            {{ $t('content.intro_page.list_empty') }}
          </div>
        </section>

        <div class="my-5 border-t border-border"></div>

        <!-- 核心功能 -->
        <section>
          <div class="mb-3 flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="text-base font-semibold">
                {{ $t('content.intro_page.section.features') }}
              </span>
              <Tag>{{ featureList.length }}</Tag>
            </div>
            <Button size="small" type="primary" @click="openCreate('feature')">
              <template #icon>
                <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
              </template>
              {{ $t('content.intro_page.add') }}
            </Button>
          </div>

          <div
            v-if="featureList.length > 0"
            class="grid grid-cols-1 gap-3 lg:grid-cols-2"
          >
            <div
              v-for="feature in featureList"
              :key="feature.id"
              class="overflow-hidden rounded-lg border transition hover:shadow-sm"
            >
              <div
                class="h-1.5 w-full"
                :style="{
                  background:
                    feature.bgColor ||
                    'linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)',
                }"
              ></div>
              <div class="p-4">
                <div class="flex items-start gap-3">
                  <img
                    v-if="feature.icon"
                    :src="feature.icon"
                    :alt="displayValue(feature.title)"
                    class="size-10 shrink-0 rounded-lg object-cover"
                  />
                  <div
                    v-else
                    class="flex size-10 shrink-0 items-center justify-center rounded-lg bg-accent"
                  >
                    <span
                      class="i-lucide-sparkles size-5 text-muted-foreground"
                    ></span>
                  </div>
                  <div class="min-w-0 flex-1">
                    <div class="font-medium">
                      {{ displayValue(feature.title) }}
                    </div>
                    <div class="mt-1 text-sm text-muted-foreground">
                      {{ displayValue(feature.description) }}
                    </div>
                  </div>
                </div>

                <div class="mt-3 flex flex-wrap gap-1.5">
                  <Tag
                    v-for="item in feature.items ?? []"
                    :key="item.id"
                    color="blue"
                  >
                    {{ displayValue(item.itemName) }}
                  </Tag>
                  <span
                    v-if="!(feature.items ?? []).length"
                    class="text-xs text-muted-foreground"
                  >
                    {{ $t('content.intro_page.item_empty') }}
                  </span>
                </div>

                <div
                  class="mt-3 flex items-center justify-between border-t border-border pt-2"
                >
                  <span class="text-xs text-muted-foreground">
                    {{
                      $t('content.intro_page.items_count', {
                        count: (feature.items ?? []).length,
                      })
                    }}
                  </span>
                  <div class="flex items-center gap-1">
                    <Button
                      type="link"
                      size="small"
                      @click="openItems('feature', feature)"
                    >
                      {{ $t('content.intro_page.item_manage') }}
                    </Button>
                    <Button
                      type="link"
                      size="small"
                      @click="openEdit('feature', feature)"
                    >
                      {{ $t('content.intro_page.edit') }}
                    </Button>
                    <Popconfirm
                      :title="$t('content.intro_page.delete_confirm_title')"
                      :description="$t('content.intro_page.delete_confirm')"
                      :ok-text="$t('content.intro_page.delete')"
                      :cancel-text="$t('content.intro_page.cancel')"
                      :ok-button-props="{ danger: true }"
                      @confirm="handleDelete('feature', feature)"
                    >
                      <Button
                        type="link"
                        size="small"
                        danger
                        :loading="deletingKey === `feature-${feature.id}`"
                      >
                        {{ $t('content.intro_page.delete') }}
                      </Button>
                    </Popconfirm>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-sm text-muted-foreground">
            {{ $t('content.intro_page.list_empty') }}
          </div>
        </section>

        <div class="my-5 border-t border-border"></div>

        <!-- 技术分类 -->
        <section>
          <div class="mb-3 flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="text-base font-semibold">
                {{ $t('content.intro_page.section.tech_categories') }}
              </span>
              <Tag>{{ techList.length }}</Tag>
            </div>
            <Button size="small" type="primary" @click="openCreate('tech')">
              <template #icon>
                <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
              </template>
              {{ $t('content.intro_page.add') }}
            </Button>
          </div>

          <div
            v-if="techList.length > 0"
            class="grid grid-cols-1 gap-3 lg:grid-cols-2 xl:grid-cols-3"
          >
            <div
              v-for="category in techList"
              :key="category.id"
              class="flex flex-col rounded-lg border p-4 transition hover:shadow-sm"
            >
              <div class="flex flex-wrap items-center gap-2">
                <span class="font-medium">
                  {{ displayValue(category.categoryName) }}
                </span>
                <Tag v-if="category.themeClass" color="purple">
                  {{ displayValue(category.themeClass) }}
                </Tag>
              </div>

              <div class="mt-3 flex-1 space-y-2">
                <div
                  v-for="item in category.items ?? []"
                  :key="item.id"
                  class="flex items-center justify-between gap-2 rounded-md bg-accent/40 px-3 py-1.5"
                >
                  <span class="text-sm font-medium">
                    {{ displayValue(item.techName) }}
                  </span>
                  <span class="truncate text-xs text-muted-foreground">
                    {{ displayValue(item.description) }}
                  </span>
                </div>
                <div
                  v-if="!(category.items ?? []).length"
                  class="text-xs text-muted-foreground"
                >
                  {{ $t('content.intro_page.item_empty') }}
                </div>
              </div>

              <div
                class="mt-3 flex items-center justify-end gap-1 border-t border-border pt-2"
              >
                <Button
                  type="link"
                  size="small"
                  @click="openItems('tech', category)"
                >
                  {{ $t('content.intro_page.item_manage') }}
                </Button>
                <Button
                  type="link"
                  size="small"
                  @click="openEdit('tech', category)"
                >
                  {{ $t('content.intro_page.edit') }}
                </Button>
                <Popconfirm
                  :title="$t('content.intro_page.delete_confirm_title')"
                  :description="$t('content.intro_page.delete_confirm')"
                  :ok-text="$t('content.intro_page.delete')"
                  :cancel-text="$t('content.intro_page.cancel')"
                  :ok-button-props="{ danger: true }"
                  @confirm="handleDelete('tech', category)"
                >
                  <Button
                    type="link"
                    size="small"
                    danger
                    :loading="deletingKey === `tech-${category.id}`"
                  >
                    {{ $t('content.intro_page.delete') }}
                  </Button>
                </Popconfirm>
              </div>
            </div>
          </div>
          <div v-else class="text-sm text-muted-foreground">
            {{ $t('content.intro_page.list_empty') }}
          </div>
        </section>

        <div class="my-5 border-t border-border"></div>

        <!-- 发展历程 -->
        <section>
          <div class="mb-3 flex items-center justify-between">
            <div class="flex items-center gap-2">
              <span class="text-base font-semibold">
                {{ $t('content.intro_page.section.milestones') }}
              </span>
              <Tag>{{ milestoneList.length }}</Tag>
            </div>
            <Button size="small" type="primary" @click="openCreate('milestone')">
              <template #icon>
                <span class="i-lucide-plus mr-1 size-4 align-middle"></span>
              </template>
              {{ $t('content.intro_page.add') }}
            </Button>
          </div>

          <div
            v-if="milestoneList.length > 0"
            class="ml-2 border-l-2 border-border pl-6"
          >
            <div
              v-for="milestone in milestoneList"
              :key="milestone.id"
              class="relative pb-5 last:pb-0"
            >
              <span
                class="absolute -left-[31px] top-1.5 size-3 rounded-full border-2 border-primary bg-background"
              ></span>
              <div class="flex flex-wrap items-center gap-2">
                <Tag class="tabular-nums" color="blue">
                  {{ displayValue(milestone.milestoneDate) }}
                </Tag>
                <span class="font-medium">
                  {{ displayValue(milestone.title) }}
                </span>
                <div class="ml-auto flex items-center gap-1">
                  <Button
                    type="link"
                    size="small"
                    @click="openEdit('milestone', milestone)"
                  >
                    {{ $t('content.intro_page.edit') }}
                  </Button>
                  <Popconfirm
                    :title="$t('content.intro_page.delete_confirm_title')"
                    :description="$t('content.intro_page.delete_confirm')"
                    :ok-text="$t('content.intro_page.delete')"
                    :cancel-text="$t('content.intro_page.cancel')"
                    :ok-button-props="{ danger: true }"
                    @confirm="handleDelete('milestone', milestone)"
                  >
                    <Button
                      type="link"
                      size="small"
                      danger
                      :loading="deletingKey === `milestone-${milestone.id}`"
                    >
                      {{ $t('content.intro_page.delete') }}
                    </Button>
                  </Popconfirm>
                </div>
              </div>
              <div
                v-if="milestone.description"
                class="mt-1 text-sm leading-6 text-muted-foreground"
              >
                {{ milestone.description }}
              </div>
            </div>
          </div>
          <div v-else class="text-sm text-muted-foreground">
            {{ $t('content.intro_page.list_empty') }}
          </div>
        </section>
      </Card>
    </div>

    <!-- 新增 / 编辑弹窗 -->
    <Modal
      v-model:open="modalVisible"
      :title="modalTitle"
      :width="600"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('content.intro_page.save')"
      :cancel-text="$t('content.intro_page.cancel')"
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
      <Form
        ref="formRef"
        :model="formRecord"
        :rules="formRules"
        layout="vertical"
        class="w-full"
      >
        <!-- 平台数据 -->
        <template v-if="editType === 'stat'">
          <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
            <FormItem
              :label="$t('content.intro_page.fields.statLabel')"
              name="statLabel"
            >
              <Input
                v-model:value="formRecord.statLabel"
                class="w-full"
                :placeholder="$t('content.intro_page.fields.statLabel')"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.statValue')"
              name="statValue"
            >
              <Input
                v-model:value="formRecord.statValue"
                class="w-full"
                :placeholder="$t('content.intro_page.fields.statValue')"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.statIcon')"
              name="statIcon"
            >
              <Input
                v-model:value="formRecord.statIcon"
                class="w-full"
                placeholder="-"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.sortOrder')"
              name="sortOrder"
            >
              <Input
                v-model:value="formRecord.sortOrder"
                class="w-full"
                placeholder="0"
              />
            </FormItem>
          </div>
        </template>

        <!-- 核心功能 -->
        <template v-else-if="editType === 'feature'">
          <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
            <FormItem
              :label="$t('content.intro_page.fields.title')"
              name="title"
            >
              <Input
                v-model:value="formRecord.title"
                class="w-full"
                :placeholder="$t('content.intro_page.fields.title')"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.sortOrder')"
              name="sortOrder"
            >
              <Input
                v-model:value="formRecord.sortOrder"
                class="w-full"
                placeholder="0"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.icon')"
              name="icon"
            >
              <Input
                v-model:value="formRecord.icon"
                class="w-full"
                :placeholder="'http://...'"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.bgColor')"
              name="bgColor"
            >
              <Input
                v-model:value="formRecord.bgColor"
                class="w-full"
                :placeholder="$t('content.intro_page.bg_hint')"
              />
            </FormItem>
          </div>
          <FormItem
            :label="$t('content.intro_page.fields.description')"
            name="description"
          >
            <Input.TextArea
              v-model:value="formRecord.description"
              :rows="3"
              class="w-full"
              :placeholder="$t('content.intro_page.fields.description')"
            />
          </FormItem>
        </template>

        <!-- 发展历程 -->
        <template v-else-if="editType === 'milestone'">
          <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
            <FormItem
              :label="$t('content.intro_page.fields.milestoneDate')"
              name="milestoneDate"
            >
              <Input
                v-model:value="formRecord.milestoneDate"
                class="w-full"
                :placeholder="$t('content.intro_page.date_hint')"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.sortOrder')"
              name="sortOrder"
            >
              <Input
                v-model:value="formRecord.sortOrder"
                class="w-full"
                placeholder="0"
              />
            </FormItem>
          </div>
          <FormItem
            :label="$t('content.intro_page.fields.title')"
            name="title"
          >
            <Input
              v-model:value="formRecord.title"
              class="w-full"
              :placeholder="$t('content.intro_page.fields.title')"
            />
          </FormItem>
          <FormItem
            :label="$t('content.intro_page.fields.description')"
            name="description"
          >
            <Input.TextArea
              v-model:value="formRecord.description"
              :rows="3"
              class="w-full"
              :placeholder="$t('content.intro_page.fields.description')"
            />
          </FormItem>
        </template>

        <!-- 技术分类 -->
        <template v-else>
          <div class="grid grid-cols-1 gap-x-6 md:grid-cols-2">
            <FormItem
              :label="$t('content.intro_page.fields.categoryName')"
              name="categoryName"
            >
              <Input
                v-model:value="formRecord.categoryName"
                class="w-full"
                :placeholder="$t('content.intro_page.fields.categoryName')"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.themeClass')"
              name="themeClass"
            >
              <Input
                v-model:value="formRecord.themeClass"
                class="w-full"
                :placeholder="$t('content.intro_page.theme_hint')"
              />
            </FormItem>
            <FormItem
              :label="$t('content.intro_page.fields.sortOrder')"
              name="sortOrder"
            >
              <Input
                v-model:value="formRecord.sortOrder"
                class="w-full"
                placeholder="0"
              />
            </FormItem>
          </div>
        </template>
      </Form>
    </Modal>

    <!-- 子项管理弹窗 -->
    <Modal
      v-model:open="itemsModalVisible"
      :title="`${$t('content.intro_page.item_manage')} · ${itemsParentTitle}`"
      :width="520"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <div class="space-y-2">
        <div
          v-for="item in parentItems"
          :key="item.id"
          class="flex items-center justify-between gap-2 rounded-md border px-3 py-2"
        >
          <div class="min-w-0">
            <div class="text-sm font-medium">
              {{
                itemsParentType === 'feature'
                  ? displayValue(item.itemName)
                  : displayValue(item.techName)
              }}
            </div>
            <div
              v-if="itemsParentType === 'tech' && item.description"
              class="text-xs text-muted-foreground"
            >
              {{ item.description }}
            </div>
          </div>
          <Popconfirm
            :title="$t('content.intro_page.delete_confirm_title')"
            :description="$t('content.intro_page.delete_confirm')"
            :ok-text="$t('content.intro_page.delete')"
            :cancel-text="$t('content.intro_page.cancel')"
            :ok-button-props="{ danger: true }"
            @confirm="handleDeleteItem(item)"
          >
            <Button
              type="link"
              size="small"
              danger
              :loading="deletingItemId === item.id"
            >
              {{ $t('content.intro_page.delete') }}
            </Button>
          </Popconfirm>
        </div>
        <div v-if="parentItems.length === 0" class="text-xs text-muted-foreground">
          {{ $t('content.intro_page.item_empty') }}
        </div>
      </div>

      <div class="mt-4 border-t border-border pt-4">
        <div class="mb-2 text-sm font-medium">
          {{ $t('content.intro_page.item_add') }}
        </div>
        <div class="flex gap-2">
          <Input
            v-model:value="newItemName"
            class="flex-1"
            :placeholder="
              itemsParentType === 'feature'
                ? $t('content.intro_page.item_add_placeholder')
                : $t('content.intro_page.fields.techName')
            "
            @press-enter="handleAddItem"
          />
          <Input
            v-if="itemsParentType === 'tech'"
            v-model:value="newItemDesc"
            class="flex-1"
            :placeholder="$t('content.intro_page.item_desc_placeholder')"
          />
          <Button
            type="primary"
            :loading="addingItem"
            :disabled="!newItemName.trim()"
            @click="handleAddItem"
          >
            {{ $t('content.intro_page.item_add') }}
          </Button>
        </div>
      </div>
    </Modal>
  </Page>
</template>
