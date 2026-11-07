<template>
  <div class="rw-svc-page">
    <ServiceHero :eyebrow="$t('railway.guidePage.eyebrow')" :title="$t('railway.guidePage.title')"
      :desc="$t('railway.guidePage.desc')" :icon="Reading" tone="mint">
      <div class="rw-svc-hero-badge">
        <strong>{{ guides.length }}</strong>
        <span>{{ $t('railway.guidePage.all') }}</span>
      </div>
    </ServiceHero>

    <div class="rw-svc-body">
      <section class="rw-svc-card">
        <div class="rw-gd-head">
          <div class="rw-svc-tabs" style="margin-bottom: 0">
            <button v-for="category in categories" :key="category.value" type="button" class="rw-svc-tab"
              :class="{ 'is-active': activeCategory === category.value }" @click="changeCategory(category.value)">
              {{ category.value === 'ALL' ? $t('railway.guidePage.all')
                : $t(`railway.guidePage.category.${category.value}`) }}
            </button>
          </div>

          <div class="rw-gd-search">
            <el-input v-model="keyword" :placeholder="$t('railway.guidePage.searchPlaceholder')" clearable
              @keyup.enter="load" @clear="load">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <button type="button" class="rw-svc-btn rw-svc-btn--blue" @click="load">
              {{ $t('railway.infoPage.query') }}
            </button>
          </div>
        </div>

        <div v-if="loading" class="rw-svc-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          {{ $t('railway.common.loading') }}
        </div>

        <div v-else-if="error" class="rw-svc-empty">
          <span class="rw-svc-empty-icon"><el-icon><Warning /></el-icon></span>
          <h4>{{ $t('railway.guidePage.loadFailed') }}</h4>
          <p>{{ error }}</p>
          <div class="rw-svc-actions" style="justify-content: center">
            <button type="button" class="rw-svc-btn rw-svc-btn--ghost rw-svc-btn--sm" @click="load">
              {{ $t('railway.list.retry') }}
            </button>
          </div>
        </div>

        <div v-else-if="guides.length === 0" class="rw-svc-empty">
          <span class="rw-svc-empty-icon"><el-icon><Reading /></el-icon></span>
          <h4>{{ $t('railway.guidePage.empty') }}</h4>
          <p>{{ $t('railway.guidePage.emptyDesc') }}</p>
        </div>

        <div v-else class="rw-gd-grid">
          <GuideCard v-for="guide in guides" :key="guide.id" :guide="guide" />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { Loading, Reading, Search, Warning } from '@element-plus/icons-vue'
import ServiceHero from './components/ServiceHero.vue'
import GuideCard from './components/GuideCard.vue'
import { getRailwayGuides } from '@/api/railway'
import { queryString } from './utils'

const route = useRoute()
const { t } = useI18n()

const categories = [
  { value: 'ALL' },
  { value: 'TICKET' },
  { value: 'SAFETY' },
  { value: 'SERVICE' },
  { value: 'SCENERY' },
  { value: 'DINING' },
]

const activeCategory = ref(queryString(route.query.category, 'ALL') || 'ALL')
const keyword = ref(queryString(route.query.keyword, ''))
const guides = ref([])
const loading = ref(false)
const error = ref('')

const load = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await getRailwayGuides({
      category: activeCategory.value === 'ALL' ? undefined : activeCategory.value,
      keyword: keyword.value.trim() || undefined,
    })
    guides.value = res?.data || []
  } catch (err) {
    error.value = err?.message || t('railway.guidePage.loadFailed')
    guides.value = []
  } finally {
    loading.value = false
  }
}

const changeCategory = (value) => {
  activeCategory.value = value
  load()
}

watch(
  () => [route.query.category, route.query.keyword],
  ([category, queryKeyword]) => {
    const nextCategory = queryString(category, 'ALL') || 'ALL'
    const nextKeyword = queryString(queryKeyword, '')
    if (nextCategory === activeCategory.value && nextKeyword === keyword.value) return
    activeCategory.value = nextCategory
    keyword.value = nextKeyword
    load()
  },
)

onMounted(load)
</script>

<style scoped>
.rw-gd-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.rw-gd-search {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 260px;
  flex: 1;
  max-width: 380px;
}

.rw-gd-search :deep(.el-input__wrapper) {
  border-radius: 10px;
  background: var(--rw-soft-2);
  box-shadow: inset 0 0 0 1px var(--rw-border);
  height: 40px;
}

.rw-gd-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

@media (max-width: 1024px) {
  .rw-gd-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .rw-gd-grid {
    grid-template-columns: 1fr;
  }

  .rw-gd-search {
    max-width: none;
    flex-basis: 100%;
  }
}
</style>
