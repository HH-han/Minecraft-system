<template>
  <article class="rw-gd-card" role="button" tabindex="0" @click="open" @keyup.enter="open">
    <div class="rw-gd-cover" :class="`is-${guide.coverTone || 'blue'}`">
      <el-icon><component :is="categoryIcon" /></el-icon>
      <span class="rw-gd-cat">{{ $t(`railway.guidePage.category.${guide.category}`) }}</span>
    </div>
    <div class="rw-gd-body">
      <h3>{{ guide.title }}</h3>
      <p>{{ guide.summary }}</p>
      <div v-if="tags.length" class="rw-gd-tags">
        <span v-for="tag in tags" :key="tag">{{ tag }}</span>
      </div>
      <div class="rw-gd-foot">
        <span>
          <el-icon><View /></el-icon>
          {{ $t('railway.guidePage.views', { n: guide.views || 0 }) }}
        </span>
        <em>{{ $t('railway.guidePage.read') }}</em>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { KnifeFork, Picture, Service, Tickets, Warning, View } from '@element-plus/icons-vue'

const props = defineProps({
  guide: { type: Object, required: true },
})

const router = useRouter()

const iconMap = {
  TICKET: Tickets,
  SAFETY: Warning,
  SERVICE: Service,
  SCENERY: Picture,
  DINING: KnifeFork,
}

const categoryIcon = computed(() => iconMap[props.guide.category] || Tickets)
const tags = computed(() =>
  (props.guide.tags || '')
    .split(',')
    .map((tag) => tag.trim())
    .filter(Boolean),
)

const open = () => router.push(`/ticket/guide/${props.guide.id}`)
</script>

<style scoped>
.rw-gd-card {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--rw-border);
  border-radius: 17px;
  background: var(--rw-card);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
}

.rw-gd-card:hover {
  transform: translateY(-3px);
  border-color: rgba(59, 124, 255, 0.42);
  box-shadow: 0 20px 38px -32px rgba(16, 47, 100, 0.6);
}

.rw-gd-cover {
  position: relative;
  height: 104px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  color: #ffffff;
  font-size: 34px;
  overflow: hidden;
}

.rw-gd-cover::after {
  content: '';
  position: absolute;
  right: -26px;
  bottom: -54px;
  width: 140px;
  height: 140px;
  border-radius: 50%;
  border: 20px solid rgba(255, 255, 255, 0.12);
}

.rw-gd-cover.is-blue {
  background: linear-gradient(135deg, #1b4fbf, #3b7cff);
}

.rw-gd-cover.is-orange {
  background: linear-gradient(135deg, #ef7a16, #ffab4d);
}

.rw-gd-cover.is-mint {
  background: linear-gradient(135deg, #0d9488, #2dd4bf);
}

.rw-gd-cover.is-violet {
  background: linear-gradient(135deg, #5b4bd6, #8b7cf6);
}

.rw-gd-cat {
  position: relative;
  z-index: 1;
  font-size: 11px;
  letter-spacing: 0.1em;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.24);
}

.rw-gd-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px 18px 17px;
  flex: 1;
}

.rw-gd-body h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: var(--rw-text);
  line-height: 1.45;
}

.rw-gd-body p {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--rw-text-2);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rw-gd-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.rw-gd-tags span {
  font-size: 10.5px;
  padding: 2px 7px;
  border-radius: 5px;
  background: var(--rw-soft);
  color: var(--rw-blue);
}

.rw-gd-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px dashed var(--rw-border);
  font-size: 11.5px;
  color: var(--rw-text-3);
}

.rw-gd-foot span {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.rw-gd-foot em {
  font-style: normal;
  color: var(--rw-blue);
  font-weight: 600;
}
</style>
