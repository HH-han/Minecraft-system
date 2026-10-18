<template>
  <section class="recommend">
    <div class="section-head">
      <div class="head-copy">
        <span class="eyebrow">{{ t('strategy.recommend.eyebrow') }}</span>
        <h2>{{ t('strategy.recommend.title') }}</h2>
        <p class="head-sub">{{ t('strategy.recommend.subtitle') }}</p>
      </div>
      <span v-if="travelCards.length" class="head-count">{{ travelCards.length }}</span>
    </div>

    <div v-if="travelCards.length === 0" class="recommend-empty">
      {{ t('strategy.recommend.empty') }}
    </div>

    <div v-else class="card-grid">
      <article
        v-for="(card, index) in travelCards"
        :key="card.id || index"
        class="travel-card"
        :style="{ '--i': index }"
        @click="showModal(card)"
      >
        <div class="card-image">
          <img :src="card.image" :alt="card.title" loading="lazy" />
          <span :class="['card-tag', card.status === 1 ? 'active' : 'ended']">
            {{ card.status === 1 ? t('strategy.recommend.recruiting') : t('strategy.recommend.ended') }}
          </span>
          <span class="card-members">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <circle cx="9" cy="8" r="3.2" />
              <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
              <path d="M16 5.6a3 3 0 0 1 0 5.8M17.6 14.4A5 5 0 0 1 20.5 19" />
            </svg>
            {{ t('strategy.recommend.members', { count: card.members || 0 }) }}
          </span>
        </div>

        <div class="card-body">
          <h3>{{ card.title }}</h3>
          <p class="card-desc">{{ card.description }}</p>

          <div class="card-footer">
            <span v-if="card.price > 0" class="card-price">
              ¥{{ card.price }}<em>{{ t('strategy.recommend.priceFrom') }}</em>
            </span>
            <span v-else class="card-price muted">
              {{ t('strategy.recommend.recruiting') }}
            </span>

            <button class="join-btn" type="button" @click.stop="showModal(card)">
              {{ t('strategy.recommend.join') }}
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M5 12h14M13 6l6 6-6 6" />
              </svg>
            </button>
          </div>
        </div>
      </article>
    </div>

    <!-- 群组信息模态框 -->
    <div class="group-modal" :class="{ active: showGroupModal }" @click.self="closeModal">
      <div class="group-modal-panel" role="dialog" aria-modal="true">
        <header class="group-modal-hero">
          <img v-if="currentGroup.image" :src="currentGroup.image" :alt="currentGroup.title" class="group-modal-cover" />
          <div class="group-modal-scrim" aria-hidden="true"></div>
          <div class="group-modal-title">
            <span class="group-modal-eyebrow">
              {{ t('strategy.recommend.members', { count: currentGroup.members || 0 }) }}
            </span>
            <h2>{{ currentGroup.title }}</h2>
          </div>
          <button class="group-modal-close" type="button" @click="closeModal">
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M6 6l12 12M18 6L6 18" />
            </svg>
          </button>
        </header>

        <div class="group-modal-body">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <circle cx="9" cy="8" r="3.2" />
                  <path d="M3.5 19a5.5 5.5 0 0 1 11 0" />
                  <path d="M16 5.6a3 3 0 0 1 0 5.8M17.6 14.4A5 5 0 0 1 20.5 19" />
                </svg>
              </span>
              <div class="info-content">
                <h3>{{ t('strategy.recommend.modal.groupName') }}</h3>
                <p>{{ currentGroup.groupName || currentGroup.title }}</p>
              </div>
            </div>

            <div class="info-item">
              <span class="info-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <circle cx="12" cy="12" r="9" />
                  <path d="M12 11v6M12 7.5v.5" />
                </svg>
              </span>
              <div class="info-content">
                <h3>{{ t('strategy.recommend.modal.groupDesc') }}</h3>
                <p>{{ currentGroup.groupDescription || currentGroup.description }}</p>
              </div>
            </div>

            <div class="info-item">
              <span class="info-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <rect x="3" y="5" width="18" height="16" rx="3" />
                  <path d="M8 3v4M16 3v4M3 10h18" />
                </svg>
              </span>
              <div class="info-content">
                <h3>{{ t('strategy.recommend.modal.dates') }}</h3>
                <p>{{ currentGroup.dates || '—' }}</p>
              </div>
            </div>

            <div class="info-item">
              <span class="info-icon">
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M12 21s7-5.5 7-11a7 7 0 1 0-14 0c0 5.5 7 11 7 11Z" />
                  <circle cx="12" cy="10" r="2.5" />
                </svg>
              </span>
              <div class="info-content">
                <h3>{{ t('strategy.recommend.modal.highlights') }}</h3>
                <p>{{ currentGroup.highlights || '—' }}</p>
              </div>
            </div>
          </div>

          <div class="members-section">
            <h3>{{ t('strategy.recommend.modal.members', { count: currentGroup.members || 0 }) }}</h3>
            <div class="members-grid">
              <div v-for="(member, index) in groupMembers" :key="index" class="member-card">
                <div class="member-avatar" :style="{ backgroundImage: `url(${member.avatar})` }"></div>
                <span class="member-name">{{ member.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <footer class="group-modal-footer">
          <button class="confirm-btn" type="button">
            {{ t('strategy.recommend.modal.confirm') }}
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M5 13l4 4L19 7" />
            </svg>
          </button>
        </footer>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getGroupList } from '@/api/group'
import { useAutoRefresh } from '@/hooks/useAutoRefresh'

const { t } = useI18n()

const DEFAULT_COVER = 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800'

// 卡片数据
const travelCards = ref([])

const fetchCards = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 10
    }
    const response = await getGroupList(params)
    if (response.data && response.data.records) {
      travelCards.value = response.data.records.map(group => ({
        id: group.id,
        title: group.name,
        image: group.images ? group.images.split(',')[0] : DEFAULT_COVER,
        members: group.currentMembers || 0,
        description: group.description || '',
        price: group.price || 0,
        status: group.status,
        groupName: group.name,
        groupDescription: group.description,
        dates: group.startDate && group.endDate ? `${group.startDate} - ${group.endDate}` : '',
        highlights: group.description
      }))
    } else {
      travelCards.value = []
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    travelCards.value = []
  }
}

// 数据变更后自动刷新
useAutoRefresh(fetchCards)

// 群组成员数据
const groupMembers = ref([
  { name: '张明', avatar: 'https://randomuser.me/api/portraits/men/32.jpg' },
  { name: '李华', avatar: 'https://randomuser.me/api/portraits/women/44.jpg' }
])

// 模态框状态
const showGroupModal = ref(false)
const currentGroup = ref({})

function showModal(group) {
  currentGroup.value = group
  showGroupModal.value = true
  document.body.style.overflow = 'hidden'
}

function closeModal() {
  showGroupModal.value = false
  document.body.style.overflow = ''
}

onMounted(() => {
  fetchCards()
})
</script>

<style scoped>
.recommend {
  min-width: 0;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 30px;
}

.eyebrow {
  display: block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.24em;
  color: var(--theme-primary);
  margin-bottom: 8px;
}

.head-copy h2 {
  font-size: clamp(24px, 2.6vw, 32px);
  font-weight: 750;
  line-height: 1.15;
  letter-spacing: -0.02em;
  color: var(--theme-text-1);
}

.head-sub {
  margin-top: 6px;
  font-size: 14px;
  color: var(--theme-text-2);
}

.head-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 30px;
  height: 30px;
  padding: 0 10px;
  border-radius: 999px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
  font-size: 13px;
  font-weight: 700;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 22px;
}

.travel-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-lg);
  cursor: pointer;
  transition: transform var(--theme-duration-slow) var(--theme-ease),
    box-shadow var(--theme-duration-slow) var(--theme-ease),
    border-color var(--theme-duration-slow) var(--theme-ease);
  animation: card-in 0.5s var(--theme-ease) both;
  animation-delay: calc(var(--i, 0) * 60ms);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.travel-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--theme-shadow-lg);
  border-color: var(--theme-border-strong);
}

.card-image {
  position: relative;
  aspect-ratio: 16 / 10;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}

.card-image::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0) 45%, rgba(0, 0, 0, 0.5) 100%);
  pointer-events: none;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s var(--theme-ease);
}

.travel-card:hover .card-image img {
  transform: scale(1.06);
}

.card-tag {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 1;
  padding: 5px 11px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: rgba(12, 15, 20, 0.5);
  backdrop-filter: blur(10px);
}

.card-tag.active {
  color: #d1fae5;
}

.card-tag.ended {
  color: rgba(255, 255, 255, 0.62);
}

.card-members {
  position: absolute;
  left: 12px;
  bottom: 12px;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 11px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  background: rgba(12, 15, 20, 0.5);
  backdrop-filter: blur(10px);
}

.card-members svg {
  width: 13px;
  height: 13px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.card-body h3 {
  font-size: 18px;
  font-weight: 650;
  line-height: 1.3;
  color: var(--theme-text-1);
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-desc {
  font-size: 13px;
  line-height: 1.6;
  color: var(--theme-text-2);
  margin-bottom: 18px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: auto;
  padding-top: 14px;
  border-top: 1px solid var(--theme-border);
}

.card-price {
  font-size: 19px;
  font-weight: 750;
  color: var(--theme-highlight);
  line-height: 1.2;
}

.card-price em {
  font-size: 11px;
  font-style: normal;
  font-weight: 500;
  color: var(--theme-text-3);
}

.card-price.muted {
  font-size: 12px;
  font-weight: 600;
  color: var(--theme-text-3);
}

.join-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
  padding: 8px 16px;
  border: none;
  border-radius: 999px;
  background: var(--theme-primary);
  color: var(--theme-primary-text);
  font-family: inherit;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background var(--theme-duration-fast), transform var(--theme-duration-fast);
}

.join-btn svg {
  width: 14px;
  height: 14px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
  transition: transform var(--theme-duration-fast);
}

.join-btn:hover {
  background: var(--theme-primary-hover);
}

.join-btn:hover svg {
  transform: translateX(2px);
}

.join-btn:active {
  transform: scale(0.96);
}

.recommend-empty {
  padding: 44px;
  text-align: center;
  border: 1px dashed var(--theme-border);
  border-radius: var(--theme-radius-lg);
  color: var(--theme-text-3);
  font-size: 13px;
}

/* 弹窗 */
.group-modal {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: var(--theme-overlay);
  backdrop-filter: blur(16px);
  opacity: 0;
  visibility: hidden;
  transition: opacity var(--theme-duration) var(--theme-ease),
    visibility var(--theme-duration) var(--theme-ease);
}

.group-modal.active {
  opacity: 1;
  visibility: visible;
}

.group-modal-panel {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 860px;
  max-height: 88vh;
  overflow: hidden;
  background: var(--theme-bg-elevated);
  border: 1px solid var(--theme-border);
  border-radius: var(--theme-radius-xl);
  box-shadow: var(--theme-shadow-lg);
  transform: translateY(24px) scale(0.98);
  transition: transform var(--theme-duration-slow) var(--theme-ease);
}

.group-modal.active .group-modal-panel {
  transform: translateY(0) scale(1);
}

.group-modal-hero {
  position: relative;
  flex-shrink: 0;
  height: 220px;
  overflow: hidden;
  background: var(--theme-bg-surface-2);
}

.group-modal-cover {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.group-modal-scrim {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(8, 10, 14, 0.12) 0%, rgba(8, 10, 14, 0.74) 100%);
}

.group-modal-title {
  position: absolute;
  left: 28px;
  right: 80px;
  bottom: 24px;
  color: #fff;
}

.group-modal-eyebrow {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.78);
}

.group-modal-title h2 {
  font-size: 28px;
  font-weight: 750;
  letter-spacing: -0.02em;
  line-height: 1.15;
}

.group-modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 50%;
  background: rgba(12, 15, 20, 0.4);
  color: #fff;
  cursor: pointer;
  backdrop-filter: blur(10px);
  transition: background var(--theme-duration-fast), transform var(--theme-duration-fast);
}

.group-modal-close svg {
  width: 17px;
  height: 17px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
}

.group-modal-close:hover {
  background: rgba(12, 15, 20, 0.66);
  transform: rotate(90deg);
}

.group-modal-body {
  flex: 1;
  padding: 26px 28px;
  overflow-y: auto;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px 26px;
}

.info-item {
  display: flex;
  gap: 14px;
}

.info-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 14px;
  background: var(--theme-primary-soft);
  color: var(--theme-primary);
}

.info-icon svg {
  width: 19px;
  height: 19px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.7;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.info-content h3 {
  margin-bottom: 4px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: var(--theme-text-3);
}

.info-content p {
  font-size: 14px;
  line-height: 1.6;
  color: var(--theme-text-1);
}

.members-section {
  margin-top: 26px;
  padding-top: 22px;
  border-top: 1px solid var(--theme-border);
}

.members-section h3 {
  margin-bottom: 16px;
  font-size: 14px;
  font-weight: 650;
  color: var(--theme-text-1);
}

.members-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
}

.member-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  width: 76px;
}

.member-avatar {
  width: 58px;
  height: 58px;
  border-radius: 50%;
  background-size: cover;
  background-position: center;
  border: 2px solid var(--theme-primary-soft);
}

.member-name {
  font-size: 12px;
  color: var(--theme-text-2);
  text-align: center;
}

.group-modal-footer {
  flex-shrink: 0;
  padding: 16px 28px 22px;
  border-top: 1px solid var(--theme-border);
  display: flex;
  justify-content: flex-end;
}

.confirm-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 11px 26px;
  border: none;
  border-radius: 999px;
  background: var(--theme-gradient);
  color: var(--theme-primary-text);
  font-family: inherit;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 12px 26px -14px var(--theme-primary);
  transition: transform var(--theme-duration-fast), box-shadow var(--theme-duration-fast);
}

.confirm-btn svg {
  width: 15px;
  height: 15px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2.2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.confirm-btn:hover {
  transform: translateY(-1px);
}

.confirm-btn:active {
  transform: scale(0.97);
}

@media (max-width: 1199px) and (min-width: 768px) {
  .section-head {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
  }
}

@media (max-width: 767px) {
  .section-head {
    align-items: flex-start;
    flex-direction: column;
    gap: 14px;
    margin-bottom: 20px;
  }

  .card-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .card-body {
    padding: 16px;
  }

  .group-modal {
    padding: 12px;
  }

  .group-modal-panel {
    max-height: 92vh;
    border-radius: var(--theme-radius-lg);
  }

  .group-modal-hero {
    height: 168px;
  }

  .group-modal-title {
    left: 18px;
    right: 64px;
    bottom: 16px;
  }

  .group-modal-title h2 {
    font-size: 22px;
  }

  .group-modal-body {
    padding: 18px;
  }

  .info-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .group-modal-footer {
    padding: 14px 18px 18px;
  }

  .confirm-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
