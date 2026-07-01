<template>
  <section class="group-hero">
    <div class="hero-content">
      <h1>旅行群组</h1>
      <p class="hero-subtitle">与志同道合的旅伴一起规划、分享、记录精彩旅程</p>
      <div class="hero-actions">
        <button class="btn-primary" @click="$emit('create')">
          创建群组
        </button>
        <button class="btn-secondary" @click="$emit('join')">
          加入群组
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
defineEmits(['create', 'join'])
</script>

<style scoped>
.group-hero {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-60) var(--spacing-40);
  text-align: center;
  margin-bottom: var(--spacing-40);
  position: relative;
  overflow: hidden;
}

.group-hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(41, 151, 255, 0.06) 0%, transparent 70%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-content h1 {
  font-size: 56px;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1.05;
  margin-bottom: var(--spacing-12);
  color: var(--color-text);
}

.hero-subtitle {
  font-size: 21px;
  font-weight: 400;
  color: var(--color-text-secondary);
  line-height: 1.4;
  margin-bottom: var(--spacing-32);
}

.hero-actions {
  display: flex;
  gap: var(--spacing-32);
  justify-content: center;
  align-items: center;
}

.hero-actions .btn-primary::after,
.hero-actions .btn-secondary::after {
  content: '›';
  font-size: 18px;
  font-weight: 300;
  transition: transform var(--transition-fast);
}

.hero-actions .btn-primary:hover::after,
.hero-actions .btn-secondary:hover::after {
  transform: translateX(2px);
}

@media (max-width: 1199px) and (min-width: 768px) {
  .hero-content h1 {
    font-size: 44px;
  }
  .hero-subtitle {
    font-size: 19px;
  }
}

@media (max-width: 767px) {
  .group-hero {
    padding: var(--spacing-40) var(--spacing-24);
    border-radius: var(--radius-lg);
  }
  .hero-content h1 {
    font-size: 36px;
    letter-spacing: -0.01em;
  }
  .hero-subtitle {
    font-size: 17px;
  }
  .hero-actions {
    flex-direction: column;
    align-items: center;
    gap: var(--spacing-16);
  }
  .hero-actions .btn-primary,
  .hero-actions .btn-secondary {
    width: 100%;
    justify-content: center;
    padding: var(--spacing-12) var(--spacing-24);
  }
}
</style>