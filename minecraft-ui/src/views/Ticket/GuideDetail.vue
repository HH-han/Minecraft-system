<template>
  <div class="rw-svc-page">
    <ServiceHero :eyebrow="categoryLabel" :title="guide?.title || $t('railway.guidePage.title')"
      :desc="guide?.summary || ''" :icon="categoryIcon" tone="mint" />

    <div class="rw-svc-body">
      <section class="rw-svc-card">
        <div v-if="loading" class="rw-svc-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          {{ $t('railway.common.loading') }}
        </div>

        <div v-else-if="error" class="rw-svc-empty">
          <span class="rw-svc-empty-icon"><el-icon><Warning /></el-icon></span>
          <h4>{{ $t('railway.guidePage.notFound') }}</h4>
          <p>{{ error }}</p>
          <div class="rw-svc-actions" style="justify-content: center">
            <button type="button" class="rw-svc-btn rw-svc-btn--ghost rw-svc-btn--sm" @click="goList">
              {{ $t('railway.guidePage.back') }}
            </button>
          </div>
        </div>

        <template v-else-if="guide">
          <div class="rw-gdt-head">
            <div class="rw-gdt-meta">
              <span class="rw-gdt-cat">{{ categoryLabel }}</span>
              <span>{{ $t('railway.guidePage.publishAt', { date: publishDate }) }}</span>
              <span>{{ $t('railway.guidePage.views', { n: guide.views || 0 }) }}</span>
            </div>
            <button type="button" class="rw-svc-link" @click="goList">
              {{ $t('railway.guidePage.back') }}
            </button>
          </div>

          <article class="rw-gdt-article">
            <p v-for="(line, index) in paragraphs" :key="index" :class="{ 'is-gap': !line }">
              {{ line }}
            </p>
          </article>

          <div v-if="tags.length" class="rw-gdt-tags">
            <span v-for="tag in tags" :key="tag">{{ tag }}</span>
          </div>
        </template>
      </section>

      <section v-if="related.length" style="margin-top: 22px">
        <div class="rw-gdt-related-head">
          <h2 class="rw-svc-card-title">{{ $t('railway.guidePage.related') }}</h2>
        </div>
        <div class="rw-gdt-grid">
          <GuideCard v-for="item in related" :key="item.id" :guide="item" />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import {
  KnifeFork,
  Loading,
  Picture,
  Service,
  Tickets,
  Warning,
} from '@element-plus/icons-vue'
import ServiceHero from './components/ServiceHero.vue'
import GuideCard from './components/GuideCard.vue'
import { getRailwayGuideDetail, getRailwayGuideRelated } from '@/api/railway'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const iconMap = {
  TICKET: Tickets,
  SAFETY: Warning,
  SERVICE: Service,
  SCENERY: Picture,
  DINING: KnifeFork,
}

const guide = ref(null)
const related = ref([])
const loading = ref(false)
const error = ref('')

const categoryLabel = computed(() =>
  guide.value ? t(`railway.guidePage.category.${guide.value.category}`) : t('railway.guidePage.title'),
)

const categoryIcon = computed(() => iconMap[guide.value?.category] || Tickets)

const paragraphs = computed(() =>
  (guide.value?.content || '').split(/\r?\n/).map((line) => line.trim()),
)

const tags = computed(() =>
  (guide.value?.tags || '')
    .split(',')
    .map((tag) => tag.trim())
    .filter(Boolean),
)

const publishDate = computed(() => {
  const value = guide.value?.publishTime
  if (!value) return ''
  return String(value).slice(0, 10)
})

const goList = () => router.push('/ticket/guide')

const load = async (id) => {
  if (!id) return
  loading.value = true
  error.value = ''
  guide.value = null
  related.value = []
  try {
    const res = await getRailwayGuideDetail(id)
    guide.value = res?.data || null
    if (guide.value) {
      const relatedRes = await getRailwayGuideRelated(id, 3)
      related.value = relatedRes?.data || []
    }
  } catch (err) {
    error.value = err?.message || t('railway.guidePage.loadFailed')
  } finally {
    loading.value = false
  }
}

watch(
  () => route.params.id,
  (id) => load(id),
  { immediate: true },
)
</script>

<style scoped>
.rw-gdt-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
  padding-bottom: 18px;
  border-bottom: 1px solid var(--rw-border);
}

.rw-gdt-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px 16px;
  font-size: 12px;
  color: var(--rw-text-3);
}

.rw-gdt-cat {
  padding: 3px 10px;
  border-radius: 999px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  font-size: 11px;
  font-weight: 600;
}

.rw-gdt-article {
  padding: 20px 2px 6px;
}

.rw-gdt-article p {
  margin: 0 0 14px;
  font-size: 14px;
  line-height: 1.95;
  color: var(--rw-text-2);
  letter-spacing: 0.01em;
}

.rw-gdt-article p.is-gap {
  margin: 0;
  height: 4px;
}

.rw-gdt-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px dashed var(--rw-border);
}

.rw-gdt-tags span {
  font-size: 11.5px;
  padding: 4px 10px;
  border-radius: 6px;
  background: var(--rw-soft);
  color: var(--rw-blue);
}

.rw-gdt-related-head {
  margin-bottom: 14px;
}

.rw-gdt-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

@media (max-width: 1024px) {
  .rw-gdt-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .rw-gdt-grid {
    grid-template-columns: 1fr;
  }

  .rw-gdt-article p {
    font-size: 13.5px;
    line-height: 1.85;
  }
}
</style>
