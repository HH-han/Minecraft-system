<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { useAuthStore } from '@/stores/auth';
import { getFavoriteList } from '@/api/favorite';
import type { FavoriteItem } from '@/types/im';

const { t } = useI18n();
const authStore = useAuthStore();

const loading = ref(false);
const list = ref<FavoriteItem[]>([]);

function formatDate(value?: string): string {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
}

async function load(): Promise<void> {
  const userId = authStore.userId;
  if (!userId) return;
  loading.value = true;
  try {
    const res = await getFavoriteList(userId);
    list.value = res.data ?? [];
  } catch {
    ElMessage.error(t('im.favorites.loadFailed'));
  } finally {
    loading.value = false;
  }
}

onMounted(load);
</script>

<template>
  <div class="favorites-column">
    <header class="column-header">
      <h3>{{ t('im.favorites.title') }}</h3>
    </header>

    <div class="list-scroll">
      <el-skeleton v-if="loading" :rows="4" animated class="skeleton" />
      <el-empty
        v-else-if="!list.length"
        :description="t('im.favorites.empty')"
        :image-size="90"
      />
      <div
        v-for="(item, index) in list"
        v-else
        :key="item.id ?? index"
        class="fav-card"
      >
        <h4 class="fav-title">{{ item.title || t('im.favorites.title') }}</h4>
        <p class="fav-content">{{ item.content }}</p>
        <span class="fav-time">{{ formatDate(item.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.favorites-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--theme-bg-app);
}

.column-header {
  padding: 16px 18px 12px;
  border-bottom: 1px solid var(--theme-hairline);
}

.column-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.list-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.skeleton {
  padding: 8px;
}

.fav-card {
  padding: 12px 14px;
  margin-bottom: 10px;
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-md);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-shadow-sm);
  cursor: pointer;
  transition:
    transform var(--theme-duration-fast) var(--theme-ease),
    box-shadow var(--theme-duration-fast) var(--theme-ease);
}

.fav-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--theme-shadow-md);
}

.fav-title {
  margin: 0 0 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.fav-content {
  margin: 0 0 8px;
  font-size: 12px;
  line-height: 1.6;
  color: var(--theme-text-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.fav-time {
  font-size: 11px;
  color: var(--theme-text-3);
}
</style>
