<script lang="ts" setup>
import { computed, onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { Copy, ExternalLink, Link, LoaderCircle, RefreshCw } from '@vben/icons';
import { $t } from '@vben/locales';

import { Button, Card, Input, message, Result, Tag } from 'antdv-next';

defineOptions({ name: 'OpenAPIJSONPage' });

const OPENAPI_URL = 'http://localhost:8080/v3/api-docs';

const loading = ref(false);
const errorMsg = ref('');
const rawJson = ref('');

// =========================
// 工具
// =========================
function escapeHtml(text: string): string {
  return text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

/** 简易 JSON 语法高亮 */
function highlightJson(json: string): string {
  return escapeHtml(json).replace(
    /("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false)\b|\bnull\b|-?\d+(?:\.\d*)?(?:[eE][+-]?\d+)?)/g,
    (match) => {
      let cls = 'text-orange-600 dark:text-orange-400';
      if (match.startsWith('"')) {
        cls = match.trimEnd().endsWith(':')
          ? 'text-primary'
          : 'text-green-600 dark:text-green-400';
      } else if (match === 'true' || match === 'false') {
        cls = 'text-blue-600 dark:text-blue-400';
      } else if (match === 'null') {
        cls = 'text-muted-foreground';
      }
      return `<span class="${cls}">${match}</span>`;
    },
  );
}

const prettyJson = computed(() => {
  if (!rawJson.value) return '';
  try {
    return JSON.stringify(JSON.parse(rawJson.value), null, 2);
  } catch {
    return rawJson.value;
  }
});

const highlightedHtml = computed(() =>
  rawJson.value ? highlightJson(prettyJson.value) : '',
);

const docInfo = computed(() => {
  try {
    const doc = JSON.parse(rawJson.value);
    return {
      title: doc?.info?.title ?? '-',
      version: doc?.info?.version ?? '-',
      paths: Object.keys(doc?.paths ?? {}).length,
      schemas: Object.keys(doc?.components?.schemas ?? {}).length,
    };
  } catch {
    return null;
  }
});

// =========================
// 数据加载
// =========================
async function fetchData() {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res = await fetch(OPENAPI_URL);
    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`);
    }
    rawJson.value = await res.text();
  } catch (err: any) {
    errorMsg.value =
      err?.message || $t('system.openapi_page.load_failed');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  fetchData();
});

// =========================
// 操作
// =========================
async function copyJson() {
  try {
    await navigator.clipboard.writeText(prettyJson.value);
    message.success($t('system.openapi_page.copy_success'));
  } catch {
    message.error($t('system.openapi_page.load_failed'));
  }
}

function openWindow() {
  window.open(OPENAPI_URL, '_blank');
}
</script>

<template>
  <Page :auto-content-height="true">
    <!-- 加载中 -->
    <Card
      v-if="loading && !rawJson"
      :title="$t('system.openapi_page.title')"
      :styles="{ body: { padding: '24px' } }"
    >
      <div class="flex flex-col items-center gap-3 py-16">
        <LoaderCircle class="size-8 animate-spin text-primary" />
        <span class="text-sm text-muted-foreground">
          {{ $t('system.openapi_page.loading') }}
        </span>
      </div>
    </Card>

    <!-- 加载失败 -->
    <Card
      v-else-if="errorMsg && !rawJson"
      :title="$t('system.openapi_page.title')"
      :styles="{ body: { padding: '24px' } }"
    >
      <Result
        status="error"
        :title="$t('system.openapi_page.error_title')"
        :sub-title="errorMsg"
      >
        <template #extra>
          <Button type="primary" @click="fetchData">
            {{ $t('system.openapi_page.retry') }}
          </Button>
        </template>
      </Result>
    </Card>

    <!-- 内容 -->
    <Card
      v-else
      :title="$t('system.openapi_page.title')"
      :styles="{ body: { padding: '16px 20px' } }"
    >
      <template #extra>
        <div class="flex items-center gap-2">
          <Button @click="copyJson">
            <template #icon>
              <Copy class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('system.openapi_page.copy') }}
          </Button>
          <Button @click="fetchData">
            <template #icon>
              <RefreshCw class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('system.openapi_page.refresh') }}
          </Button>
          <Button type="primary" @click="openWindow">
            <template #icon>
              <ExternalLink class="mr-1 size-4 align-middle" />
            </template>
            {{ $t('system.openapi_page.new_window') }}
          </Button>
        </div>
      </template>

      <!-- 文档信息 -->
      <div class="mb-3 flex flex-wrap items-center gap-2">
        <Tag color="blue">
          {{ $t('system.openapi_page.version') }}:
          {{ docInfo?.version ?? '-' }}
        </Tag>
        <Tag>
          {{ $t('system.openapi_page.paths') }}:
          {{ docInfo?.paths ?? 0 }}
        </Tag>
        <Tag>
          {{ $t('system.openapi_page.schemas') }}:
          {{ docInfo?.schemas ?? 0 }}
        </Tag>
      </div>

      <div class="mb-3 flex items-center gap-2">
        <span class="shrink-0 text-sm text-muted-foreground">
          {{ $t('system.openapi_page.endpoint') }}
        </span>
        <Input :value="OPENAPI_URL" readonly class="flex-1 font-mono text-xs">
          <template #prefix>
            <Link class="size-4 text-muted-foreground" />
          </template>
        </Input>
      </div>

      <!-- JSON 高亮渲染 -->
      <pre
        class="max-h-[calc(100vh-380px)] min-h-[320px] overflow-auto rounded-md border border-border bg-accent/30 p-4 font-mono text-xs leading-5"
        v-html="highlightedHtml"
      ></pre>
    </Card>
  </Page>
</template>