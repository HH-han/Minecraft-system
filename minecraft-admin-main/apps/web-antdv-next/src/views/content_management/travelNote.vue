<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import {
  Bookmark,
  CircleCheckBig,
  CircleX,
  LayoutGrid,
  MapPin,
  RefreshCw,
  Search,
  Star,
} from '@vben/icons';

import {
  Button,
  Card,
  Form,
  FormItem,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Select,
  Skeleton,
  Table,
  Tag,
} from 'antdv-next';

import {
  auditTravelNote,
  createTravelNoteTag,
  deleteTravelNoteTag,
  featureTravelNote,
  getTravelNotePage,
  getTravelNoteReports,
  getTravelNoteStat,
  getTravelNoteTagPage,
  handleTravelNoteReport,
  offlineTravelNote,
  topTravelNote,
  updateTravelNoteTag,
} from '#/api/management/content/travelNote';

defineOptions({ name: 'TravelNoteManagement' });

interface NoteTag {
  id?: number;
  name?: string;
}

interface NoteItem {
  avatar?: null | string;
  commentCount?: null | number;
  coverUrl?: null | string;
  createTime?: null | string;
  destination?: null | string;
  id: number;
  isFeatured?: null | number;
  isTop?: null | number;
  likeCount?: null | number;
  publishTime?: null | string;
  rejectReason?: null | string;
  status: number;
  tags?: NoteTag[] | string[];
  title?: null | string;
  userId?: null | number;
  username?: null | string;
  viewCount?: null | number;
}

// =========================
// 状态定义
// =========================
const STATUS_OPTIONS = [
  { label: '草稿', value: 0 },
  { label: '待审核', value: 1 },
  { label: '已发布', value: 2 },
  { label: '已驳回', value: 3 },
  { label: '已下架', value: 4 },
  { label: '私密', value: 5 },
];

const STATUS_MAP: Record<number, { color: string; label: string }> = {
  0: { color: 'default', label: '草稿' },
  1: { color: 'orange', label: '待审核' },
  2: { color: 'green', label: '已发布' },
  3: { color: 'red', label: '已驳回' },
  4: { color: 'gray', label: '已下架' },
  5: { color: 'blue', label: '私密' },
};

function statusLabel(status?: null | number): string {
  return STATUS_MAP[Number(status)]?.label ?? '-';
}

function statusColor(status?: null | number): string {
  return STATUS_MAP[Number(status)]?.color ?? 'default';
}

function displayValue(value: any): string {
  if (value === null || value === undefined || value === '') return '-';
  return String(value);
}

function formatTime(value?: null | string): string {
  if (!value) return '-';
  return String(value).replace('T', ' ').slice(0, 19);
}

function tagNames(note: NoteItem): string {
  if (!note.tags || !Array.isArray(note.tags)) return '-';
  return note.tags
    .map((t: any) => (typeof t === 'string' ? t : t.name))
    .filter(Boolean)
    .join(' / ') || '-';
}

// =========================
// 列表
// =========================
const loading = ref(false);
const noteList = ref<NoteItem[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const filterStatus = ref<number | undefined>(undefined);
const keyword = ref('');

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: '封面', key: 'cover', width: 80 },
  { title: '标题', key: 'title', width: 220, ellipsis: true },
  { title: '作者', key: 'author', width: 130, ellipsis: true },
  { title: '目的地', key: 'destination', width: 120, ellipsis: true },
  { title: '标签', key: 'tags', width: 160, ellipsis: true },
  { title: '状态', key: 'status', width: 90 },
  { title: '数据（浏览/赞/评）', key: 'stats', width: 140 },
  { title: '发布时间', key: 'publishTime', width: 160 },
  { title: '操作', key: 'action', width: 260, fixed: 'right' },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getTravelNotePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: filterStatus.value ?? undefined,
      keyword: keyword.value.trim() || undefined,
    });
    noteList.value = res?.records ?? [];
    total.value = Number(res?.total ?? 0);
  } catch (error: any) {
    message.error(error?.message || '加载失败');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

function handleSearch() {
  pageNum.value = 1;
  fetchData();
}

function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

function handlePageSizeChange(_page: number, size: number) {
  pageSize.value = size;
  pageNum.value = 1;
  fetchData();
}

// =========================
// 详情弹窗
// =========================
const detailVisible = ref(false);
const detailLoading = ref(false);
const detail = ref<any>(null);

async function openDetail(record: NoteItem) {
  detailVisible.value = true;
  detailLoading.value = true;
  detail.value = null;
  try {
    const res: any = await getTravelNoteDetail(record.id);
    detail.value = res ?? record;
  } catch {
    detail.value = record;
  } finally {
    detailLoading.value = false;
  }
}

const BIZ_TYPES: Record<number, string> = {
  1: '景点',
  2: '酒店',
  3: '美食',
  4: '商品',
  5: '行程',
};

// =========================
// 审核弹窗
// =========================
const auditVisible = ref(false);
const auditSubmitting = ref(false);
const auditId = ref<null | number>(null);
const auditForm = reactive({ approved: true, reason: '' });

function openAudit(record: NoteItem, approved: boolean) {
  auditId.value = record.id;
  auditForm.approved = approved;
  auditForm.reason = '';
  auditVisible.value = true;
}

async function handleAudit() {
  if (!auditForm.approved && !auditForm.reason.trim()) {
    message.warning('驳回时请填写原因');
    return;
  }
  auditSubmitting.value = true;
  try {
    await auditTravelNote(auditId.value, {
      approved: auditForm.approved,
      reason: auditForm.reason.trim() || undefined,
    });
    message.success(auditForm.approved ? '已通过并发布' : '已驳回');
    auditVisible.value = false;
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || '操作失败');
  } finally {
    auditSubmitting.value = false;
  }
}

// =========================
// 置顶 / 加精 / 下架
// =========================
async function handleTop(record: NoteItem) {
  try {
    await topTravelNote(record.id);
    message.success(record.isTop ? '已取消置顶' : '已置顶');
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || '操作失败');
  }
}

async function handleFeature(record: NoteItem) {
  try {
    await featureTravelNote(record.id);
    message.success(record.isFeatured ? '已取消加精' : '已加精');
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || '操作失败');
  }
}

async function handleOffline(record: NoteItem) {
  try {
    await offlineTravelNote(record.id);
    message.success('已下架');
    await fetchData();
  } catch (error: any) {
    message.error(error?.message || '操作失败');
  }
}

// =========================
// 标签管理
// =========================
const tagVisible = ref(false);
const tagLoading = ref(false);
const tagList = ref<any[]>([]);
const tagTotal = ref(0);
const tagPage = reactive({ pageNum: 1, pageSize: 8 });
const tagKeyword = ref('');
const tagForm = reactive({ id: null as null | number, name: '', isHot: 0 });
const tagSubmitting = ref(false);

async function openTagModal() {
  tagVisible.value = true;
  await fetchTags();
}

async function fetchTags() {
  tagLoading.value = true;
  try {
    const res: any = await getTravelNoteTagPage({
      pageNum: tagPage.pageNum,
      pageSize: tagPage.pageSize,
      keyword: tagKeyword.value.trim() || undefined,
    });
    tagList.value = res?.records ?? [];
    tagTotal.value = Number(res?.total ?? 0);
  } finally {
    tagLoading.value = false;
  }
}

function openTagEdit(tag: any) {
  tagForm.id = tag.id;
  tagForm.name = tag.name;
  tagForm.isHot = Number(tag.isHot ?? 0);
}

function resetTagForm() {
  tagForm.id = null;
  tagForm.name = '';
  tagForm.isHot = 0;
}

async function handleTagSave() {
  if (!tagForm.name.trim()) {
    message.warning('请输入标签名');
    return;
  }
  tagSubmitting.value = true;
  try {
    if (tagForm.id === null) {
      await createTravelNoteTag({ name: tagForm.name.trim(), isHot: tagForm.isHot });
    } else {
      await updateTravelNoteTag(tagForm.id, {
        name: tagForm.name.trim(),
        isHot: tagForm.isHot,
      });
    }
    message.success('保存成功');
    resetTagForm();
    await fetchTags();
  } catch (error: any) {
    message.error(error?.message || '保存失败');
  } finally {
    tagSubmitting.value = false;
  }
}

async function handleTagDelete(tag: any) {
  try {
    await deleteTravelNoteTag(tag.id);
    message.success('删除成功');
    await fetchTags();
  } catch (error: any) {
    message.error(error?.message || '删除失败');
  }
}

// =========================
// 数据看板
// =========================
const statVisible = ref(false);
const statLoading = ref(false);
const stat = ref<any>(null);

async function openStat() {
  statVisible.value = true;
  statLoading.value = true;
  try {
    stat.value = await getTravelNoteStat();
  } catch {
    stat.value = null;
  } finally {
    statLoading.value = false;
  }
}

const statCards = computed(() => [
  { label: '笔记总数', value: stat.value?.totalNotes ?? 0 },
  { label: '已发布', value: stat.value?.publishedCount ?? 0 },
  { label: '待审核', value: stat.value?.pendingCount ?? 0 },
  { label: '总浏览', value: stat.value?.totalViews ?? 0 },
  { label: '总点赞', value: stat.value?.totalLikes ?? 0 },
  { label: '总收藏', value: stat.value?.totalCollects ?? 0 },
  { label: '总评论', value: stat.value?.totalComments ?? 0 },
  { label: '待处理举报', value: stat.value?.totalReports ?? 0 },
]);

// =========================
// 举报处理
// =========================
const reportVisible = ref(false);
const reportLoading = ref(false);
const reportList = ref<any[]>([]);
const reportTotal = ref(0);
const reportPage = reactive({ pageNum: 1, pageSize: 8 });
const reportStatus = ref<number | undefined>(0);

const reportColumns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: '笔记', dataIndex: 'noteTitle', width: 200, ellipsis: true },
  { title: '举报人', dataIndex: 'reporterName', width: 120 },
  { title: '原因', dataIndex: 'reason', ellipsis: true },
  { title: '状态', key: 'status', width: 90 },
  { title: '时间', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 150 },
]);

async function openReportModal() {
  reportVisible.value = true;
  await fetchReports();
}

async function fetchReports() {
  reportLoading.value = true;
  try {
    const res: any = await getTravelNoteReports({
      pageNum: reportPage.pageNum,
      pageSize: reportPage.pageSize,
      status: reportStatus.value ?? undefined,
    });
    reportList.value = res?.records ?? [];
    reportTotal.value = Number(res?.total ?? 0);
  } finally {
    reportLoading.value = false;
  }
}

async function handleReport(record: any, status: number) {
  try {
    await handleTravelNoteReport(record.id, status);
    message.success(status === 1 ? '已处理并下架笔记' : '已驳回举报');
    await fetchReports();
  } catch (error: any) {
    message.error(error?.message || '操作失败');
  }
}

function reportStatusLabel(status: number): string {
  return status === 1 ? '已处理' : status === 2 ? '已驳回' : '待处理';
}
</script>

<template>
  <Page :auto-content-height="true">
    <div class="mx-auto w-full">
      <Card
        title="旅游笔记管理"
        :styles="{ body: { padding: '0' } }"
      >
        <template #extra>
          <div class="flex flex-wrap items-center gap-2">
            <Select
              v-model:value="filterStatus"
              :options="STATUS_OPTIONS"
              allow-clear
              class="w-32"
              placeholder="全部状态"
              @change="handleSearch"
            />
            <Input
              v-model:value="keyword"
              placeholder="搜索标题 / 目的地"
              allow-clear
              class="w-52"
              @press-enter="handleSearch"
            >
              <template #prefix>
                <Search class="size-4 text-muted-foreground" />
              </template>
            </Input>
            <Button @click="handleSearch">
              <template #icon>
                <Search class="mr-1 size-4 align-middle" />
              </template>
              查询
            </Button>
            <Button :loading="loading" @click="fetchData">
              <template #icon>
                <RefreshCw class="mr-1 size-4 align-middle" />
              </template>
              刷新
            </Button>
            <Button @click="openReportModal">举报处理</Button>
            <Button @click="openTagModal">
              <template #icon>
                <Bookmark class="mr-1 size-4 align-middle" />
              </template>
              标签管理
            </Button>
            <Button type="primary" @click="openStat">
              <template #icon>
                <LayoutGrid class="mr-1 size-4 align-middle" />
              </template>
              数据看板
            </Button>
          </div>
        </template>

        <Table
          :columns="columns"
          :data-source="noteList"
          :loading="loading"
          :pagination="{
            current: pageNum,
            pageSize: pageSize,
            showSizeChanger: true,
            pageSizeOptions: ['10', '20', '50'],
            total: total,
            showTotal: (t: number) => `共 ${t} 条`,
            onChange: handlePageChange,
            onShowSizeChange: handlePageSizeChange,
          }"
          :scroll="{ x: 1440 }"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'cover'">
              <img
                v-if="record.coverUrl"
                :src="record.coverUrl"
                alt=""
                class="size-10 rounded-md object-cover"
              />
              <span v-else class="text-muted-foreground">-</span>
            </template>

            <template v-else-if="column.key === 'title'">
              <span class="font-medium">{{ displayValue(record.title) }}</span>
              <Tag v-if="record.isTop" class="ml-1" color="red">顶</Tag>
              <Tag v-if="record.isFeatured" class="ml-1" color="gold">精</Tag>
            </template>

            <template v-else-if="column.key === 'author'">
              <span>{{ displayValue(record.username) }}</span>
              <span class="ml-1 text-xs text-muted-foreground">#{{ record.userId }}</span>
            </template>

            <template v-else-if="column.key === 'tags'">
              <span class="text-xs text-muted-foreground">{{ tagNames(record) }}</span>
            </template>

            <template v-else-if="column.key === 'status'">
              <Tag :color="statusColor(record.status)">{{ statusLabel(record.status) }}</Tag>
            </template>

            <template v-else-if="column.key === 'stats'">
              <span class="text-xs tabular-nums">
                {{ record.viewCount ?? 0 }} / {{ record.likeCount ?? 0 }} / {{ record.commentCount ?? 0 }}
              </span>
            </template>

            <template v-else-if="column.key === 'publishTime'">
              <span class="text-xs text-muted-foreground">{{ formatTime(record.publishTime) }}</span>
            </template>

            <template v-else-if="column.key === 'action'">
              <div class="flex flex-nowrap items-center whitespace-nowrap">
                <Button type="link" size="small" @click="openDetail(record)">详情</Button>
                <Button
                  v-if="record.status === 1"
                  type="link"
                  size="small"
                  @click="openAudit(record, true)"
                >
                  通过
                </Button>
                <Button
                  v-if="record.status === 1"
                  type="link"
                  size="small"
                  danger
                  @click="openAudit(record, false)"
                >
                  驳回
                </Button>
                <Button type="link" size="small" @click="handleTop(record)">
                  {{ record.isTop ? '取消置顶' : '置顶' }}
                </Button>
                <Button type="link" size="small" @click="handleFeature(record)">
                  {{ record.isFeatured ? '取消加精' : '加精' }}
                </Button>
                <Popconfirm
                  v-if="record.status === 2"
                  title="确定下架该笔记吗？"
                  ok-text="下架"
                  cancel-text="取消"
                  @confirm="handleOffline(record)"
                >
                  <Button type="link" size="small" danger>下架</Button>
                </Popconfirm>
              </div>
            </template>
          </template>

          <template #emptyText>
            <span class="text-sm text-muted-foreground">暂无笔记</span>
          </template>
        </Table>
      </Card>
    </div>

    <!-- 详情弹窗 -->
    <Modal
      v-model:open="detailVisible"
      title="笔记详情"
      :width="720"
      :footer="null"
      :styles="{ body: { maxHeight: 'calc(100vh - 220px)', overflowY: 'auto', padding: '20px 24px' } }"
    >
      <Skeleton v-if="detailLoading" :active="true" :paragraph="{ rows: 6 }" />
      <template v-else-if="detail">
        <div class="mb-3 flex items-center gap-2">
          <span class="text-lg font-semibold">{{ displayValue(detail.title) }}</span>
          <Tag :color="statusColor(detail.status)">{{ statusLabel(detail.status) }}</Tag>
        </div>
        <div v-if="detail.rejectReason" class="mb-3 rounded-md border border-red-200 bg-red-50 p-2 text-sm text-red-600">
          驳回原因：{{ detail.rejectReason }}
        </div>

        <div class="mb-3 flex flex-wrap gap-2 text-xs text-muted-foreground">
          <span>作者：{{ displayValue(detail.username) }} #{{ detail.userId }}</span>
          <span>目的地：{{ displayValue(detail.destination) }}</span>
          <span v-if="detail.travelDays">天数：{{ detail.travelDays }}</span>
          <span v-if="detail.cost">人均：¥{{ detail.cost }}</span>
          <span v-if="detail.travelDate">出行：{{ detail.travelDate }}</span>
          <span>浏览：{{ detail.viewCount ?? 0 }}</span>
          <span>点赞：{{ detail.likeCount ?? 0 }}</span>
          <span>收藏：{{ detail.collectCount ?? 0 }}</span>
          <span>评论：{{ detail.commentCount ?? 0 }}</span>
        </div>

        <div v-if="detail.tags && detail.tags.length" class="mb-3 flex flex-wrap gap-1">
          <Tag v-for="tag in detail.tags" :key="typeof tag === 'string' ? tag : tag.id">
            #{{ typeof tag === 'string' ? tag : tag.name }}
          </Tag>
        </div>

        <div v-if="detail.coverUrl" class="mb-3">
          <img :src="detail.coverUrl" alt="" class="max-h-48 rounded-md object-cover" />
        </div>

        <div
          v-if="detail.summary"
          class="mb-3 whitespace-pre-wrap rounded-md border border-border bg-accent/40 p-3 text-sm"
        >
          {{ detail.summary }}
        </div>

        <!-- 正文 -->
        <div
          v-if="detail.contentType === 2"
          class="whitespace-pre-wrap rounded-md border border-border bg-accent/40 p-3 text-sm leading-6"
        >
          {{ detail.content }}
        </div>
        <!-- eslint-disable-next-line vue/no-v-html -->
        <div v-else class="rich-preview mb-3" v-html="detail.content" />

        <!-- 关联 -->
        <div v-if="detail.relations && detail.relations.length" class="mb-3">
          <div class="mb-1 text-sm font-medium">关联内容</div>
          <div class="flex flex-wrap gap-2">
            <Tag v-for="rel in detail.relations" :key="rel.id" color="blue">
              {{ BIZ_TYPES[rel.bizType] ?? '相关' }}：{{ displayValue(rel.bizName) }}
            </Tag>
          </div>
        </div>

        <!-- 轨迹 -->
        <div v-if="detail.routes && detail.routes.length">
          <div class="mb-1 text-sm font-medium">行程轨迹</div>
          <div class="rounded-md border border-border p-2">
            <div v-for="route in detail.routes" :key="route.id" class="text-sm">
              <span class="text-muted-foreground">Day {{ route.dayIndex ?? '-' }}</span>
              {{ displayValue(route.poiName) }}
              <span v-if="route.longitude" class="text-xs text-muted-foreground">
                ({{ route.longitude }}, {{ route.latitude }})
              </span>
            </div>
          </div>
        </div>
      </template>
    </Modal>

    <!-- 审核弹窗 -->
    <Modal
      v-model:open="auditVisible"
      :title="auditForm.approved ? '审核通过' : '审核驳回'"
      :width="480"
      :confirm-loading="auditSubmitting"
      :ok-text="auditForm.approved ? '确认通过' : '确认驳回'"
      cancel-text="取消"
      @ok="handleAudit"
    >
      <Form layout="vertical">
        <FormItem v-if="!auditForm.approved" label="驳回原因（必填）" required>
          <Input.TextArea
            v-model:value="auditForm.reason"
            :rows="3"
            maxlength="255"
            placeholder="请填写驳回原因，将展示给作者"
          />
        </FormItem>
        <div v-else class="text-sm text-muted-foreground">
          确认通过后笔记将立即发布，并推送给订阅用户。
        </div>
      </Form>
    </Modal>

    <!-- 标签管理弹窗 -->
    <Modal
      v-model:open="tagVisible"
      title="标签管理"
      :width="640"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <div class="mb-3 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="tagForm.name"
          placeholder="标签名"
          class="w-44"
          maxlength="50"
        />
        <InputNumber v-model:value="tagForm.isHot" :min="0" :max="1" class="w-20" />
        <span class="text-xs text-muted-foreground">是否热门(0/1)</span>
        <Button type="primary" size="small" :loading="tagSubmitting" @click="handleTagSave">
          {{ tagForm.id === null ? '新增' : '保存修改' }}
        </Button>
        <Button v-if="tagForm.id !== null" size="small" @click="resetTagForm">取消编辑</Button>
        <div class="flex-1" />
        <Input
          v-model:value="tagKeyword"
          placeholder="搜索标签"
          allow-clear
          class="w-40"
          @press-enter="((tagPage.pageNum = 1), fetchTags())"
        />
      </div>

      <Table
        :columns="[
          { title: 'ID', dataIndex: 'id', width: 70 },
          { title: '标签名', dataIndex: 'name' },
          { title: '使用次数', dataIndex: 'useCount', width: 90 },
          { title: '热门', key: 'hot', width: 70 },
          { title: '操作', key: 'action', width: 130 },
        ]"
        :data-source="tagList"
        :loading="tagLoading"
        :pagination="{
          current: tagPage.pageNum,
          pageSize: tagPage.pageSize,
          total: tagTotal,
          size: 'small',
          onChange: (p: number) => ((tagPage.pageNum = p), fetchTags()),
        }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'hot'">
            <Tag :color="record.isHot ? 'orange' : 'default'">{{ record.isHot ? '是' : '否' }}</Tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <Button type="link" size="small" @click="openTagEdit(record)">编辑</Button>
            <Popconfirm title="删除后笔记关联将一并移除，确认？" @confirm="handleTagDelete(record)">
              <Button type="link" size="small" danger>删除</Button>
            </Popconfirm>
          </template>
        </template>
      </Table>
    </Modal>

    <!-- 数据看板弹窗 -->
    <Modal
      v-model:open="statVisible"
      title="数据看板"
      :width="760"
      :footer="null"
      :styles="{ body: { maxHeight: 'calc(100vh - 220px)', overflowY: 'auto', padding: '20px 24px' } }"
    >
      <Skeleton v-if="statLoading" :active="true" :paragraph="{ rows: 6 }" />
      <template v-else-if="stat">
        <div class="mb-4 grid grid-cols-2 gap-3 md:grid-cols-4">
          <div
            v-for="card in statCards"
            :key="card.label"
            class="rounded-lg border border-border p-3 text-center"
          >
            <div class="text-xl font-semibold tabular-nums">{{ card.value }}</div>
            <div class="mt-1 text-xs text-muted-foreground">{{ card.label }}</div>
          </div>
        </div>

        <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
          <div>
            <div class="mb-2 flex items-center gap-1 text-sm font-medium">
              <MapPin class="size-4" /> 热门目的地 TOP5
            </div>
            <div
              v-for="(item, idx) in stat.hotDestinations || []"
              :key="idx"
              class="flex justify-between border-b border-border py-1.5 text-sm"
            >
              <span>{{ item.destination }}</span>
              <span class="tabular-nums text-muted-foreground">{{ item.noteCount }} 篇</span>
            </div>
            <div v-if="!(stat.hotDestinations || []).length" class="text-sm text-muted-foreground">暂无数据</div>
          </div>

          <div>
            <div class="mb-2 flex items-center gap-1 text-sm font-medium">
              <Star class="size-4" /> 创作者榜单 TOP5
            </div>
            <div
              v-for="author in stat.topAuthors || []"
              :key="author.userId"
              class="flex items-center justify-between border-b border-border py-1.5 text-sm"
            >
              <span class="flex items-center gap-2">
                <img
                  v-if="author.avatar"
                  :src="author.avatar"
                  alt=""
                  class="size-6 rounded-full object-cover"
                />
                {{ author.username || `用户#${author.userId}` }}
              </span>
              <span class="tabular-nums text-muted-foreground">
                {{ author.noteCount }} 篇 / {{ author.totalLikes }} 赞
              </span>
            </div>
            <div v-if="!(stat.topAuthors || []).length" class="text-sm text-muted-foreground">暂无数据</div>
          </div>
        </div>
      </template>
    </Modal>

    <!-- 举报处理弹窗 -->
    <Modal
      v-model:open="reportVisible"
      title="举报处理"
      :width="1200"
      :footer="null"
      :styles="{ body: { padding: '20px 24px' } }"
    >
      <div class="mb-3 flex items-center gap-2">
        <Select
          v-model:value="reportStatus"
          :options="[
            { label: '待处理', value: 0 },
            { label: '已处理', value: 1 },
            { label: '已驳回', value: 2 },
          ]"
          allow-clear
          class="w-32"
          placeholder="全部状态"
          @change="((reportPage.pageNum = 1), fetchReports())"
        />
      </div>
      <Table
        :columns="reportColumns"
        :data-source="reportList"
        :loading="reportLoading"
        :pagination="{
          current: reportPage.pageNum,
          pageSize: reportPage.pageSize,
          total: reportTotal,
          size: 'small',
          onChange: (p: number) => ((reportPage.pageNum = p), fetchReports()),
        }"
        size="small"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <Tag :color="record.status === 0 ? 'orange' : record.status === 1 ? 'green' : 'default'">
              {{ reportStatusLabel(record.status) }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            <span class="text-xs text-muted-foreground">{{ formatTime(record.createTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <template v-if="record.status === 0">
              <Popconfirm title="确认举报成立？笔记将被下架。" @confirm="handleReport(record, 1)">
                <Button type="link" size="small" danger>
                  <CircleCheckBig class="mr-1 size-3.5" /> 举报成立
                </Button>
              </Popconfirm>
              <Popconfirm title="确认驳回该举报？" @confirm="handleReport(record, 2)">
                <Button type="link" size="small">
                  <CircleX class="mr-1 size-3.5" /> 驳回
                </Button>
              </Popconfirm>
            </template>
            <span v-else class="text-xs text-muted-foreground">已处理</span>
          </template>
        </template>
      </Table>
    </Modal>
  </Page>
</template>

<style scoped>
.rich-preview {
  border: 1px solid var(--border);
  border-radius: 6px;
  background-color: var(--accent);
  padding: 12px;
  font-size: 14px;
  line-height: 1.8;
  word-break: break-word;
}

.rich-preview :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}
</style>
