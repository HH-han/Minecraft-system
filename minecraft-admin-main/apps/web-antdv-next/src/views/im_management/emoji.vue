<script lang="ts" setup>
import type { TableColumnsType } from 'antdv-next';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Plus, RefreshCw, Search } from '@vben/icons';
import { $t } from '@vben/locales';

import {
  Button,
  Empty,
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
  createEmoji,
  deleteEmoji,
  getEmojiPage,
  updateEmoji,
} from '#/api/management/im/emoji';

defineOptions({ name: 'ImEmojiManagement' });

interface EmojiRow {
  id: number;
  emojiCode?: null | string;
  emojiName?: null | string;
  category?: null | string;
  unicode?: null | string;
  description?: null | string;
  sortOrder?: null | number;
  isActive?: null | number;
  createTime?: null | string;
  updateTime?: null | string;
}

const loading = ref(false);
const rows = ref<EmojiRow[]>([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const keyword = ref('');
const activeFilter = ref<null | number>(null);

const showDialog = ref(false);
const isEditing = ref(false);
const submitting = ref(false);

const activeOptions = computed(() => [
  { value: 1, label: $t('im.emoji.active.enabled') },
  { value: 0, label: $t('im.emoji.active.disabled') },
]);

function emptyForm() {
  return {
    id: null as null | number,
    emojiCode: '',
    emojiName: '',
    category: '',
    unicode: '',
    description: '',
    sortOrder: 0,
    isActive: 1,
  };
}
const form = reactive(emptyForm());

const columns = computed<TableColumnsType>(() => [
  { title: 'ID', dataIndex: 'id', width: 70 },
  { title: $t('im.emoji.columns.code'), dataIndex: 'emojiCode', width: 140, ellipsis: true },
  { title: $t('im.emoji.columns.name'), dataIndex: 'emojiName', width: 140, ellipsis: true },
  { title: $t('im.emoji.columns.category'), dataIndex: 'category', width: 110 },
  { title: $t('im.emoji.columns.unicode'), dataIndex: 'unicode', width: 110 },
  {
    title: $t('im.emoji.columns.description'),
    dataIndex: 'description',
    width: 200,
    ellipsis: true,
  },
  { title: $t('im.emoji.columns.sortOrder'), dataIndex: 'sortOrder', width: 90 },
  { title: $t('im.emoji.columns.isActive'), key: 'isActive', width: 90 },
  { title: $t('im.emoji.columns.updateTime'), key: 'updateTime', width: 170 },
  { title: $t('im.emoji.columns.action'), key: 'action', width: 140, fixed: 'right' },
]);

async function fetchData() {
  loading.value = true;
  try {
    const res: any = await getEmojiPage({
      page: pageNum.value,
      size: pageSize.value,
      keyword: keyword.value.trim() || undefined,
      isActive: activeFilter.value ?? undefined,
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
function handleReset() {
  keyword.value = '';
  activeFilter.value = null;
  pageNum.value = 1;
  fetchData();
}
function handlePageChange(page: number, size: number) {
  pageNum.value = page;
  pageSize.value = size;
  fetchData();
}

function openCreate() {
  isEditing.value = false;
  Object.assign(form, emptyForm());
  showDialog.value = true;
}
function openEdit(row: any) {
  isEditing.value = true;
  Object.assign(form, {
    id: row.id,
    emojiCode: row.emojiCode ?? '',
    emojiName: row.emojiName ?? '',
    category: row.category ?? '',
    unicode: row.unicode ?? '',
    description: row.description ?? '',
    sortOrder: row.sortOrder ?? 0,
    isActive: Number(row.isActive) === 1 ? 1 : 0,
  });
  showDialog.value = true;
}

async function handleSubmit() {
  if (!form.emojiCode.trim()) {
    message.error($t('im.emoji.form.code_required'));
    return;
  }
  if (!form.emojiName.trim()) {
    message.error($t('im.emoji.form.name_required'));
    return;
  }
  submitting.value = true;
  try {
    const payload = {
      emojiCode: form.emojiCode,
      emojiName: form.emojiName,
      category: form.category,
      unicode: form.unicode,
      description: form.description,
      sortOrder: form.sortOrder,
      isActive: form.isActive,
    };
    if (isEditing.value && form.id != null) {
      await updateEmoji(form.id, payload);
      message.success($t('im.emoji.update_success'));
    } else {
      await createEmoji(payload);
      message.success($t('im.emoji.save_success'));
    }
    showDialog.value = false;
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.emoji.failed'));
  } finally {
    submitting.value = false;
  }
}

async function handleDelete(row: any) {
  try {
    await deleteEmoji(row.id);
    message.success($t('im.emoji.delete_success'));
    await fetchData();
  } catch (e: any) {
    message.error(e?.message || $t('im.emoji.failed'));
  }
}

onMounted(fetchData);
</script>

<template>
  <Page :title="$t('im.emoji.title')" :description="$t('im.emoji.description')">
    <div class="bg-card rounded-lg p-4">
      <div class="mb-4 flex flex-wrap items-center gap-2">
        <Input
          v-model:value="keyword"
          :placeholder="$t('im.emoji.search_placeholder')"
          class="w-52"
          allow-clear
          @press-enter="handleSearch"
        />
        <Select
          v-model:value="activeFilter"
          :placeholder="$t('im.emoji.filter.isActive')"
          allow-clear
          class="w-32"
          :options="activeOptions"
          @change="handleSearch"
        />
        <Button type="primary" @click="handleSearch">
          <template #icon><Search class="size-4" /></template>
          {{ $t('im.emoji.search') }}
        </Button>
        <div class="flex-1"></div>
        <Button @click="handleReset">
          <template #icon><RefreshCw class="size-4" /></template>
          {{ $t('im.emoji.reset') }}
        </Button>
        <Button type="primary" @click="openCreate">
          <template #icon><Plus class="size-4" /></template>
          {{ $t('im.emoji.action.add') }}
        </Button>
      </div>

      <Table
        :columns="columns"
        :data-source="rows"
        :loading="loading"
        :pagination="{
          current: pageNum,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (t: number) => $t('im.emoji.total', { total: t }),
          onChange: handlePageChange,
        }"
        :scroll="{ x: 1200 }"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'isActive'">
            <Tag :color="Number(record.isActive) === 1 ? 'green' : 'default'">
              {{
                Number(record.isActive) === 1
                  ? $t('im.emoji.active.enabled')
                  : $t('im.emoji.active.disabled')
              }}
            </Tag>
          </template>
          <template v-else-if="column.key === 'updateTime'">
            {{ record.updateTime ? String(record.updateTime).replace('T', ' ').slice(0, 19) : '-' }}
          </template>
          <template v-else-if="column.key === 'action'">
            <Button type="link" size="small" @click="openEdit(record)">
              {{ $t('im.emoji.action.edit') }}
            </Button>
            <Popconfirm :title="$t('im.emoji.action.delete_confirm')" @confirm="handleDelete(record)">
              <Button type="link" size="small" danger>
                {{ $t('im.emoji.action.delete') }}
              </Button>
            </Popconfirm>
          </template>
        </template>
        <template #emptyText>
          <Empty :description="$t('im.emoji.empty')" />
        </template>
      </Table>
    </div>

    <Modal
      v-model:open="showDialog"
      :title="
        isEditing ? $t('im.emoji.modal.edit_title') : $t('im.emoji.modal.create_title')
      "
      width="560px"
      :mask-closable="false"
      :confirm-loading="submitting"
      :ok-text="$t('im.emoji.save')"
      :cancel-text="$t('im.emoji.cancel')"
      @ok="handleSubmit"
    >
      <Form layout="horizontal" :label-col="{ span: 5 }" :wrapper-col="{ span: 18 }">
        <FormItem :label="$t('im.emoji.form.code')" required>
          <Input
            v-model:value="form.emojiCode"
            :disabled="isEditing"
            :maxlength="50"
            :placeholder="$t('im.emoji.form.code_placeholder')"
          />
        </FormItem>
        <FormItem :label="$t('im.emoji.form.name')" required>
          <Input v-model:value="form.emojiName" :maxlength="100" />
        </FormItem>
        <FormItem :label="$t('im.emoji.form.category')">
          <Input v-model:value="form.category" :maxlength="50" />
        </FormItem>
        <FormItem :label="$t('im.emoji.form.unicode')">
          <Input v-model:value="form.unicode" :maxlength="50" />
        </FormItem>
        <FormItem :label="$t('im.emoji.form.description')">
          <Input.TextArea v-model:value="form.description" :rows="2" :maxlength="200" />
        </FormItem>
        <FormItem :label="$t('im.emoji.form.sortOrder')">
          <InputNumber v-model:value="form.sortOrder" :min="0" :precision="0" class="w-full" />
        </FormItem>
        <FormItem :label="$t('im.emoji.form.isActive')">
          <Switch
            :checked="form.isActive === 1"
            @change="(v: any) => (form.isActive = v ? 1 : 0)"
          />
        </FormItem>
      </Form>
    </Modal>
  </Page>
</template>
