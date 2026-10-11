<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Card,
  Descriptions,
  Form,
  FormItem,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Select,
  Table,
  Tag,
  message,
} from 'antdv-next';

import {
  auditAfterSale,
  closeAfterSale,
  getAfterSaleDetail,
  getAfterSalePage,
  receiveAfterSale,
  refundAfterSale,
} from '#/api/management/logistics/afterSale';

defineOptions({ name: 'LogisticsAfterSale' });

const loading = ref(false);
const list = ref<any[]>([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const keyword = ref('');

const columns = computed<TableColumnsType>(() => [
  { title: $t('logistics.after_sale_page.fields.afterSaleNo'), dataIndex: 'afterSaleNo', width: 170 },
  { title: $t('logistics.after_sale_page.fields.orderNo'), dataIndex: 'orderNo', width: 140 },
  { title: $t('logistics.after_sale_page.fields.itemName'), dataIndex: 'itemName', ellipsis: true },
  { title: $t('logistics.after_sale_page.fields.quantity'), dataIndex: 'quantity', width: 70 },
  { title: $t('logistics.after_sale_page.fields.type'), key: 'type', width: 100 },
  { title: $t('logistics.after_sale_page.fields.status'), key: 'status', width: 110 },
  { title: $t('logistics.after_sale_page.fields.refundAmount'), key: 'amount', width: 110 },
  { title: $t('logistics.after_sale_page.fields.createTime'), key: 'createTime', width: 170 },
  { title: $t('logistics.common.action'), key: 'action', width: 240, fixed: 'right' },
]);

const filteredList = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return list.value;
  return list.value.filter((r) =>
    [r.afterSaleNo, r.orderNo, r.itemName]
      .filter(Boolean)
      .some((v: string) => String(v).toLowerCase().includes(kw)),
  );
});

function formatTime(v?: string) {
  return v ? String(v).replace('T', ' ') : '-';
}

function statusColor(status?: number) {
  if (status === 6) return 'success';
  if (status === 2 || status === 7) return 'default';
  if (status === 3 || status === 4 || status === 5) return 'warning';
  return 'processing';
}

function amountText(record: any) {
  const v = record.actualAmount ?? record.refundAmount;
  return v === null || v === undefined ? '-' : `¥${v}`;
}

async function fetchList() {
  loading.value = true;
  try {
    const res: any = await getAfterSalePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    });
    list.value = res?.records ?? [];
    total.value = res?.total ?? 0;
  } catch {
    // 统一错误提示
  } finally {
    loading.value = false;
  }
}

function handleTableChange(pagination: any) {
  pageNum.value = pagination.current;
  fetchList();
}

onMounted(fetchList);

// ===== 详情 =====
const detailVisible = ref(false);
const detailLoading = ref(false);
const detail = ref<any>(null);

async function openDetail(record: any) {
  detailVisible.value = true;
  detailLoading.value = true;
  detail.value = null;
  try {
    const res: any = await getAfterSaleDetail(record.id);
    detail.value = res ?? null;
  } finally {
    detailLoading.value = false;
  }
}

// ===== 审核 =====
const auditVisible = ref(false);
const auditSubmitting = ref(false);
const auditTarget = ref<any>(null);
const auditForm = reactive<any>({ approved: true, auditRemark: '', actualAmount: undefined });

function openAudit(record: any) {
  auditTarget.value = record;
  auditForm.approved = true;
  auditForm.auditRemark = '';
  auditForm.actualAmount = record.refundAmount ?? undefined;
  auditVisible.value = true;
}

async function submitAudit() {
  auditSubmitting.value = true;
  try {
    await auditAfterSale(auditTarget.value.id, {
      approved: auditForm.approved,
      auditRemark: auditForm.auditRemark,
      actualAmount: auditForm.approved ? auditForm.actualAmount : undefined,
    });
    message.success($t('logistics.common.save_success'));
    auditVisible.value = false;
    await fetchList();
  } catch {
    // 统一错误提示
  } finally {
    auditSubmitting.value = false;
  }
}

// ===== 其他动作 =====
async function handleReceive(record: any) {
  await receiveAfterSale(record.id);
  message.success($t('logistics.common.save_success'));
  fetchList();
}

async function handleRefund(record: any) {
  await refundAfterSale(record.id);
  message.success($t('logistics.common.save_success'));
  fetchList();
}

async function handleClose(record: any) {
  await closeAfterSale(record.id, $t('logistics.after_sale_page.close_reason_default'));
  message.success($t('logistics.common.save_success'));
  fetchList();
}
</script>

<template>
  <Page :auto-content-height="true">
    <Card :title="$t('logistics.after_sale_page.title')" :styles="{ body: { padding: '0' } }">
      <template #extra>
        <div class="flex items-center gap-2">
          <Input
            v-model:value="keyword"
            :placeholder="$t('logistics.common.search_placeholder')"
            allow-clear
            class="w-56"
          >
            <template #prefix><Search class="size-4 text-muted-foreground" /></template>
          </Input>
          <Button :loading="loading" @click="fetchList">
            <template #icon><RefreshCw class="mr-1 size-4 align-middle" /></template>
            {{ $t('logistics.common.refresh') }}
          </Button>
        </div>
      </template>

      <Table
        :columns="columns"
        :data-source="filteredList"
        :loading="loading"
        :pagination="{ current: pageNum, pageSize, total, showSizeChanger: false }"
        :scroll="{ x: 1280 }"
        size="small"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <Tag>{{ record.typeDesc || '-' }}</Tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <Tag :color="statusColor(record.status)">{{ record.statusDesc || '-' }}</Tag>
          </template>
          <template v-else-if="column.key === 'amount'">{{ amountText(record) }}</template>
          <template v-else-if="column.key === 'createTime'">
            <span class="text-xs text-muted-foreground">{{ formatTime(record.createTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="flex flex-nowrap items-center whitespace-nowrap">
              <Button type="link" size="small" @click="openDetail(record)">
                {{ $t('logistics.common.detail') }}
              </Button>
              <Button v-if="record.status === 0" type="link" size="small" @click="openAudit(record)">
                {{ $t('logistics.after_sale_page.audit_title') }}
              </Button>
              <Button
                v-if="record.status === 4"
                type="link"
                size="small"
                @click="handleReceive(record)"
              >
                {{ $t('logistics.after_sale_page.receive') }}
              </Button>
              <Button
                v-if="record.status === 1 || record.status === 5"
                type="link"
                size="small"
                @click="handleRefund(record)"
              >
                {{ $t('logistics.after_sale_page.refund') }}
              </Button>
              <Popconfirm
                v-if="record.status !== 6 && record.status !== 7"
                :title="$t('logistics.after_sale_page.close')"
                :ok-text="$t('logistics.common.save')"
                :cancel-text="$t('logistics.common.cancel')"
                @confirm="handleClose(record)"
              >
                <Button type="link" size="small" danger>
                  {{ $t('logistics.after_sale_page.close') }}
                </Button>
              </Popconfirm>
            </div>
          </template>
        </template>
        <template #emptyText>
          <span class="text-sm text-muted-foreground">{{ $t('logistics.common.empty') }}</span>
        </template>
      </Table>
    </Card>

    <!-- 详情 -->
    <Modal
      v-model:open="detailVisible"
      :title="$t('logistics.after_sale_page.detail_title')"
      :width="680"
      :footer="null"
    >
      <div v-if="detailLoading" class="py-8 text-center text-muted-foreground">
        {{ $t('logistics.common.refresh') }}
      </div>
      <template v-else-if="detail">
        <Descriptions :column="2" size="small" bordered>
          <Descriptions.Item :label="$t('logistics.after_sale_page.fields.afterSaleNo')">
            {{ detail.afterSaleNo }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('logistics.after_sale_page.fields.status')">
            <Tag :color="statusColor(detail.status)">{{ detail.statusDesc }}</Tag>
          </Descriptions.Item>
          <Descriptions.Item :label="$t('logistics.after_sale_page.fields.itemName')">
            {{ detail.itemName }} ×{{ detail.quantity }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('logistics.after_sale_page.fields.type')">
            {{ detail.typeDesc }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('logistics.after_sale_page.fields.refundAmount')">
            ¥{{ detail.actualAmount ?? detail.refundAmount ?? '-' }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('logistics.after_sale_page.fields.createTime')">
            {{ formatTime(detail.createTime) }}
          </Descriptions.Item>
          <Descriptions.Item :label="$t('logistics.after_sale_page.reason')" :span="2">
            {{ detail.reason || '-' }}
          </Descriptions.Item>
          <Descriptions.Item
            v-if="detail.auditRemark"
            :label="$t('logistics.after_sale_page.audit_remark')"
            :span="2"
          >
            {{ detail.auditRemark }}
          </Descriptions.Item>
          <Descriptions.Item
            v-if="detail.refundNo"
            :label="$t('logistics.after_sale_page.refund')"
            :span="2"
          >
            {{ detail.refundNo }}
          </Descriptions.Item>
        </Descriptions>

        <div class="mt-4 text-sm font-medium">{{ $t('logistics.after_sale_page.messages') }}</div>
        <div class="mt-2 max-h-48 overflow-y-auto">
          <div
            v-for="(m, i) in detail.messages || []"
            :key="i"
            class="border-b border-border py-2 last:border-b-0"
          >
            <div class="text-xs text-muted-foreground">
              {{
                m.senderType === 2
                  ? $t('logistics.after_sale_page.sender.merchant')
                  : m.senderType === 3
                    ? $t('logistics.after_sale_page.sender.system')
                    : $t('logistics.after_sale_page.sender.user')
              }} ·
              {{ formatTime(m.createTime) }}
            </div>
            <div class="text-sm">{{ m.content }}</div>
          </div>
          <div v-if="!(detail.messages || []).length" class="py-4 text-center text-muted-foreground">
            {{ $t('logistics.common.empty') }}
          </div>
        </div>
      </template>
    </Modal>

    <!-- 审核 -->
    <Modal
      v-model:open="auditVisible"
      :title="$t('logistics.after_sale_page.audit_title')"
      :width="520"
      :mask-closable="false"
      :confirm-loading="auditSubmitting"
      :ok-text="$t('logistics.common.save')"
      :cancel-text="$t('logistics.common.cancel')"
      @ok="submitAudit"
    >
      <Form layout="vertical">
        <FormItem :label="$t('logistics.after_sale_page.audit_approved')">
          <Select
            v-model:value="auditForm.approved"
            class="w-full"
            :options="[
              { label: $t('logistics.after_sale_page.audit_approved'), value: true },
              { label: $t('logistics.after_sale_page.audit_reject'), value: false },
            ]"
          />
        </FormItem>
        <FormItem
          v-if="auditForm.approved"
          :label="$t('logistics.after_sale_page.actual_amount')"
        >
          <InputNumber
            v-model:value="auditForm.actualAmount"
            :min="0"
            :precision="2"
            class="w-full"
          />
        </FormItem>
        <FormItem :label="$t('logistics.after_sale_page.audit_remark')">
          <Input.TextArea v-model:value="auditForm.auditRemark" :rows="3" />
        </FormItem>
      </Form>
    </Modal>
  </Page>
</template>
