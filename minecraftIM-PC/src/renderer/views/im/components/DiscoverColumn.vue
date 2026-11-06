<script setup lang="ts">
import { computed, ref, type Component } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import {
  ArrowRight,
  Camera,
  FullScreen,
  Location,
  Plus,
  Refresh,
  Search
} from '@element-plus/icons-vue';

/** 「发现」左栏：搜索 + 功能入口，参考微信发现页布局 */
const emit = defineEmits<{
  publish: [];
}>();

const { t } = useI18n();

const keyword = ref('');

type EntryKey = 'moments' | 'scan' | 'nearby' | 'shake';

const entries = computed<Array<{ key: EntryKey; label: string; icon: Component }>>(() => [
  { key: 'moments', label: t('im.discover.menu.moments'), icon: Camera },
  { key: 'scan', label: t('im.discover.menu.scan'), icon: FullScreen },
  { key: 'nearby', label: t('im.discover.menu.nearby'), icon: Location },
  { key: 'shake', label: t('im.discover.menu.shake'), icon: Refresh }
]);

const visibleEntries = computed(() => {
  const kw = keyword.value.trim().toLowerCase();
  if (!kw) return entries.value;
  return entries.value.filter((entry) => entry.label.toLowerCase().includes(kw));
});

function openEntry(key: EntryKey): void {
  // 朋友圈内容常驻右侧，其余功能暂未开放
  if (key === 'moments') return;
  ElMessage.info(t('im.common.comingSoon'));
}
</script>

<template>
  <aside class="im-column discover-column">
    <header class="im-column-head">
      <h2 class="im-column-title">{{ t('im.discover.pageTitle') }}</h2>
    </header>

    <div class="discover-search">
      <el-input
        v-model="keyword"
        :placeholder="t('im.discover.searchPlaceholder')"
        clearable
      >
        <template #prefix>
          <el-icon :size="14"><Search /></el-icon>
        </template>
      </el-input>
      <button
        type="button"
        class="discover-add"
        :title="t('im.discover.publish')"
        @click="emit('publish')"
      >
        <el-icon :size="17"><Plus /></el-icon>
      </button>
    </div>

    <div class="im-scroll discover-scroll">
      <div class="discover-list">
        <button
          v-for="entry in visibleEntries"
          :key="entry.key"
          type="button"
          class="discover-entry"
          @click="openEntry(entry.key)"
        >
          <span class="discover-entry__icon">
            <el-icon :size="19"><component :is="entry.icon" /></el-icon>
          </span>
          <span class="discover-entry__label">{{ entry.label }}</span>
          <el-icon class="discover-entry__arrow" :size="14"><ArrowRight /></el-icon>
        </button>
      </div>

      <p v-if="!visibleEntries.length" class="discover-empty">
        {{ t('im.conversation.noResult') }}
      </p>
    </div>
  </aside>
</template>

<style scoped>
.discover-column {
  width: 292px;
  flex-shrink: 0;
  border-right: 1px solid var(--theme-hairline);
}

.discover-search {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 16px 12px;
  flex-shrink: 0;
}

.discover-search :deep(.el-input__wrapper) {
  border-radius: var(--theme-radius-pill);
  background: var(--theme-bg-surface-2);
  box-shadow: none;
  padding: 4px 14px;
}

.discover-search :deep(.el-input__inner) {
  font-size: 13px;
}

.discover-add {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  display: grid;
  place-items: center;
  border-radius: 50%;
  border: 1px solid var(--theme-border);
  background: var(--theme-bg-surface);
  color: var(--theme-text-2);
  cursor: pointer;
  transition:
    border-color var(--theme-duration-fast) var(--theme-ease),
    color var(--theme-duration-fast) var(--theme-ease),
    background var(--theme-duration-fast) var(--theme-ease);
}

.discover-add:hover {
  border-color: var(--theme-primary);
  color: var(--theme-primary);
  background: var(--theme-primary-soft);
}

.discover-scroll {
  padding: 4px 12px 16px;
}

.discover-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.discover-entry {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: 12px;
  background: transparent;
  cursor: pointer;
  text-align: left;
  transition: background var(--theme-duration-fast) var(--theme-ease);
}

.discover-entry:hover {
  background: var(--theme-bg-hover);
}

.discover-entry__icon {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.discover-entry__label {
  flex: 1;
  min-width: 0;
  font-size: 14px;
  color: var(--theme-text-1);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.discover-entry__arrow {
  flex-shrink: 0;
  color: var(--theme-text-3);
}

.discover-empty {
  margin: 40px 0 0;
  text-align: center;
  font-size: 12.5px;
  color: var(--theme-text-3);
}
</style>
