<template>
  <div class="tab-content">
    <div class="section-header">
      <h2>我的群组</h2>
      <div class="search-bar">
        <input
          v-model="localKeyword"
          type="text"
          placeholder="搜索群组..."
          @keyup.enter="$emit('search')"
        />
        <button @click="$emit('search')">
          <i class="search-icon">⌕</i>
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="groups.length === 0" class="empty-state">
      <div class="empty-icon">👥</div>
      <h3>还没有加入任何群组</h3>
      <p>创建或加入一个旅行群组，开始你的旅程</p>
      <button class="btn-primary" @click="$emit('create')">创建群组</button>
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
          <span v-if="group.status === 1" class="status-badge active">进行中</span>
          <span v-else class="status-badge ended">已结束</span>
        </div>
        <div class="card-content">
          <h3>{{ group.name }}</h3>
          <p class="destination">
            <span class="location-icon">◎</span>
            {{ group.destination || '未设置目的地' }}
          </p>
          <div class="card-meta">
            <span class="member-count">
              <span class="icon">👤</span>
              {{ group.currentMembers || 0 }}/{{ group.maxMembers || 20 }}
            </span>
            <span class="date-range" v-if="group.startDate">
              {{ formatDate(group.startDate) }} - {{ formatDate(group.endDate) }}
            </span>
          </div>
          <div class="card-role" v-if="group.userRole">
            <span :class="['role-tag', group.userRole]">
              {{ getRoleName(group.userRole) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  groups: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  searchKeyword: {
    type: String,
    default: ''
  },
  defaultCover: {
    type: String,
    default: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800'
  }
})

const emit = defineEmits(['search', 'create', 'detail', 'update:searchKeyword'])

const localKeyword = ref(props.searchKeyword)

watch(() => props.searchKeyword, (val) => {
  localKeyword.value = val
})

watch(localKeyword, (val) => {
  emit('update:searchKeyword', val)
})

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

function getRoleName(role) {
  const map = { owner: '群主', admin: '管理员', member: '成员' }
  return map[role] || role
}
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

.search-bar {
  display: flex;
  align-items: center;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: box-shadow var(--transition-normal);
}

.search-bar:focus-within {
  box-shadow: var(--shadow-sm);
}

.search-bar input {
  border: none;
  background: transparent;
  padding: var(--spacing-12) var(--spacing-20);
  font-size: 15px;
  outline: none;
  width: 240px;
  color: var(--color-text);
}

.search-bar input::placeholder {
  color: var(--color-text-secondary);
}

.search-bar button {
  background: transparent;
  border: none;
  padding: var(--spacing-12) var(--spacing-20);
  cursor: pointer;
  font-size: 18px;
  color: var(--color-text-secondary);
  transition: color var(--transition-fast);
}

.search-bar button:hover {
  color: var(--color-text);
}

.loading-state {
  text-align: center;
  padding: 60px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-bg-secondary);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
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

.status-badge {
  position: absolute;
  top: var(--spacing-12);
  right: var(--spacing-12);
  padding: var(--spacing-4) var(--spacing-12);
  border-radius: var(--radius-full);
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.status-badge.active {
  background: var(--color-success);
  color: #fff;
}

.status-badge.ended {
  background: var(--color-text-secondary);
  color: #fff;
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

.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-12);
}

.member-count {
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
}

.card-role {
  margin-top: var(--spacing-8);
}

.role-tag {
  display: inline-block;
  padding: var(--spacing-4) var(--spacing-10);
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-weight: 500;
}

.role-tag.owner {
  background: var(--color-warning);
  color: #000;
}

.role-tag.admin {
  background: var(--color-primary);
  color: #fff;
}

.role-tag.member {
  background: var(--color-bg);
  color: var(--color-text);
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
  .search-bar input {
    width: 100%;
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