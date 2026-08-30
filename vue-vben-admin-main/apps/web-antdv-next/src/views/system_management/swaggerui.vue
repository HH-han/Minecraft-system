<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { Button, Card } from 'antdv-next';

defineOptions({ name: 'SwaggerUIPage' });

const SWAGGER_URL = 'http://localhost:8080/swagger-ui.html';

const loading = ref(true);
const frameSrc = ref(SWAGGER_URL);

function reload() {
  loading.value = true;
  frameSrc.value = `${SWAGGER_URL}?t=${Date.now()}`;
}

function handleLoad() {
  loading.value = false;
}

function openWindow() {
  window.open(SWAGGER_URL, '_blank');
}

onMounted(() => {
  loading.value = true;
});
</script>

<template>
  <Page :auto-content-height="true">
    <Card
      :title="$t('system.swagger_page.title')"
      :styles="{ body: { padding: '0' } }"
    >
      <template #extra>
        <div class="flex items-center gap-2">
          <Button @click="reload">
            <template #icon>
              <span class="i-lucide-refresh-cw mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('system.swagger_page.refresh') }}
          </Button>
          <Button type="primary" @click="openWindow">
            <template #icon>
              <span class="i-lucide-external-link mr-1 size-4 align-middle"></span>
            </template>
            {{ $t('system.swagger_page.new_window') }}
          </Button>
        </div>
      </template>

      <div class="relative h-[calc(100vh-200px)] min-h-[480px] w-full">
        <!-- 加载遮罩 -->
        <div
          v-if="loading"
          class="absolute inset-0 z-10 flex items-center justify-center bg-background/60"
        >
          <div class="flex flex-col items-center gap-3">
            <span
              class="i-lucide-loader-circle size-8 animate-spin text-primary"
            ></span>
            <span class="text-sm text-muted-foreground">
              {{ $t('system.swagger_page.loading') }}
            </span>
          </div>
        </div>

        <iframe
          :src="frameSrc"
          class="size-full border-0"
          @load="handleLoad"
        ></iframe>
      </div>
    </Card>
  </Page>
</template>
