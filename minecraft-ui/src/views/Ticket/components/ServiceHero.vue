<template>
  <section class="rw-svc-hero" :class="toneClass">
    <div class="rw-svc-hero-inner">
      <span class="rw-svc-hero-icon" aria-hidden="true">
        <el-icon><component :is="icon" /></el-icon>
      </span>
      <div class="rw-svc-hero-copy">
        <span class="rw-svc-hero-eyebrow">{{ eyebrow }}</span>
        <h1>{{ title }}</h1>
        <p>{{ desc }}</p>
      </div>
      <div v-if="$slots.default" class="rw-svc-hero-side">
        <slot />
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  eyebrow: { type: String, default: '' },
  title: { type: String, required: true },
  desc: { type: String, default: '' },
  icon: { type: [Object, Function], required: true },
  tone: { type: String, default: 'blue' },
})

const toneClass = computed(() => (props.tone === 'blue' ? '' : `rw-svc-hero--${props.tone}`))
</script>
