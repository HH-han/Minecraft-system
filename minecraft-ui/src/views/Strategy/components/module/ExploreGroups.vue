<template>
  <div class="tab-content">
    <div class="section-header">
      <h2>发现群组</h2>
      <div class="filter-tabs">
        <button
          v-for="filter in filters"
          :key="filter.id"
          :class="['filter-btn', { active: activeFilter === filter.id }]"
          @click="$emit('update:activeFilter', filter.id)"
        >
          {{ filter.name }}
        </button>
      </div>
    </div>

    <div v-if="groups.length === 0" class="empty-state">
      <div class="empty-icon">🔍</div>
      <h3>暂无公开群组</h3>
      <p>成为第一个创建公开群组的人吧</p>
    </div>

    <div v-else class="group-grid">
      <div
        v-for="group in groups"
        :key="group.id"
        class="group-card"
        @click="$emit('detail', group)"
      >
        <div class="card-image">
          <img :src="group.coverUrl || defaultCover" :alt="group.name" />
          <span class="member-count-overlay">
            {{ group.currentMembers || 0 }} 成员
          </span>
        </div>
        <div class="card-content">
          <h3>{{ group.name }}</h3>
          <p class="destination">
            <span class="location-icon">◎</span>
            {{ group.destination || '未设置目的地' }}
          </p>
          <p class="description">{{ group.description || '暂无描述' }}</p>
          <div class="card-footer">
            <span class="creator">创建者：{{ group.creatorName }}</span>
            <button
              v-if="!group.isJoined"
              class="join-btn"
              @click.stop="$emit('join', group.id)"
            >
              加入
            </button>
            <span v-else class="joined-tag">已加入</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  groups: {
    type: Array,
    default: () => []
  },
  filters: {
    type: Array,
    required: true
  },
  activeFilter: {
    type: String,
    required: true
  },
  defaultCover: {
    type: String,
    default: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800'
  }
})

defineEmits(['update:activeFilter', 'detail', 'join'])
</script>

<style scoped>
.tab-content {
  animation: fadeIn var(--transition-normal);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-24);
}

.section-header h2 {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.1;
}

.filter-tabs {
  display: flex;
  gap: var(--spacing-8);
}

.filter-btn {
  padding: var(--spacing-8) var(--spacing-16);
  background: var(--color-bg-secondary);
  border: none;
  border-radius: var(--radius-full);
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.filter-btn:hover {
  background: rgba(0, 0, 0, 0.06);
  color: var(--color-text);
}

.filter-btn.active {
  background: var(--color-text);
  color: #fff;
}

.empty-state {
  text-align: center;
  padding: 80px 40px;
  background: var(--color-bg-secondary);
  border-radius: 28px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 22px;
  margin-bottom: 8px;
}

.empty-state p {
  color: var(--color-text-secondary);
  margin-bottom: 24px;
}

.group-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-24);
}

.group-card {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  transition: all var(--transition-slow);
  cursor: pointer;
  border: 1px solid transparent;
}

.group-card:hover {
  transform: scale(1.01);
  box-shadow: var(--shadow-lg);
  border-color: rgba(0, 0, 0, 0.04);
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-slow);
}

.group-card:hover .card-image img {
  transform: scale(1.03);
}

.member-count-overlay {
  position: absolute;
  bottom: var(--spacing-12);
  right: var(--spacing-12);
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(10px);
  color: #fff;
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.card-content {
  padding: var(--spacing-24) var(--spacing-24) var(--spacing-20);
}

.card-content h3 {
  font-size: 22px;
  font-weight: 600;
  line-height: 1.2;
  margin-bottom: var(--spacing-8);
}

.destination {
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: var(--spacing-12);
  display: flex;
  align-items: center;
  gap: var(--spacing-8);
}

.location-icon {
  color: var(--color-primary);
}

.description {
  color: var(--color-text-secondary);
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: var(--spacing-12);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: var(--spacing-12);
  border-top: 1px solid var(--color-border);
  margin-top: var(--spacing-8);
}

.creator {
  font-size: 13px;
  color: var(--color-text-secondary);
}

.join-btn {
  background: var(--color-primary);
  color: #fff;
  border: none;
  padding: var(--spacing-6) var(--spacing-16);
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.join-btn:hover {
  background: var(--color-primary-hover);
}

.join-btn:active {
  transform: scale(0.96);
}

.joined-tag {
  color: var(--color-success);
  font-size: 13px;
  font-weight: 500;
}

@media (max-width: 1199px) and (min-width: 768px) {
  .section-header {
    flex-direction: column;
    gap: var(--spacing-16);
    align-items: flex-start;
  }
  .group-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: var(--spacing-20);
  }
  .card-content {
    padding: var(--spacing-20) var(--spacing-20) var(--spacing-16);
  }
}

@media (max-width: 767px) {
  .section-header {
    flex-direction: column;
    gap: var(--spacing-12);
    align-items: flex-start;
  }
  .group-grid {
    grid-template-columns: 1fr;
    gap: var(--spacing-20);
  }
  .group-card {
    border-radius: var(--radius-lg);
  }
  .card-content {
    padding: var(--spacing-20) var(--spacing-20) var(--spacing-16);
  }
  .card-content h3 {
    font-size: 20px;
  }
}
</style>