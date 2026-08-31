<template>
  <div class="room-selector">
    <!-- 固定区：标题不随滚动移动 -->
    <h4 class="section-title">选择房型</h4>
    <!-- 滚动区：列表限定高度，超出部分内部滚动（隐藏滚动条但保留滚动能力） -->
    <div class="room-list-scroll scroll-container" role="list" aria-label="房型列表可滚动区域">
      <div
        class="room-option"
        v-for="room in rooms"
        :key="room.id"
        :class="{ selected: selectedRoom?.id === room.id }"
        @click="selectRoom(room)"
        role="listitem"
      >
        <div class="room-info">
          <h5 class="room-name">{{ room.name }}</h5>
          <p v-if="room.description" class="room-desc">{{ room.description }}</p>
          <div v-if="room.facilities?.length" class="room-facilities">
            <span class="facility" v-for="facility in room.facilities" :key="facility">
              {{ facility }}
            </span>
          </div>
        </div>
        <div class="room-price">
          <div class="price-block">
            <span class="price-value">¥{{ room.price }}</span>
            <span class="price-unit">/晚</span>
          </div>
          <button
            type="button"
            class="book-btn"
            :class="{ active: selectedRoom?.id === room.id }"
            @click.stop="selectRoom(room)"
          >
            {{ selectedRoom?.id === room.id ? '已选择' : '选择' }}
          </button>
        </div>
      </div>
      <div v-if="!rooms || rooms.length === 0" class="empty-hint">
        暂无房型信息
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

defineProps({
  rooms: {
    type: Array,
    default: () => []
  },
  selectedRoom: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['roomSelect'])

const selectRoom = (room) => {
  emit('roomSelect', room)
}
</script>

<style scoped>
.room-selector {
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 20px;
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.3;
}

.room-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  padding: 24px;
  box-sizing: border-box;
  background: #f5f5f7;
  border-radius: 24px;
  margin-bottom: 12px;
  border: 1.5px solid transparent;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease, background 0.2s ease;
  cursor: pointer;
}

.room-option:hover {
  transform: scale(1.005);
  box-shadow: 0 8px 24px -12px rgba(0, 0, 0, 0.15);
}

.room-option.selected {
  background: #ffffff;
  border-color: #2997ff;
  box-shadow: 0 8px 24px -12px rgba(41, 151, 255, 0.3);
}

.room-info {
  flex: 1;
  min-width: 0;
}

.room-name {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  line-height: 1.3;
}

.room-desc {
  margin: 0 0 12px;
  color: #6e6e73;
  font-size: 14px;
  line-height: 1.4;
}

.room-facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.facility {
  background: #ffffff;
  color: #1d1d1f;
  border: 1px solid #d2d2d6;
  border-radius: 40px;
  padding: 5px 14px;
  font-size: 12px;
  line-height: 1.4;
}

.room-price {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  flex-shrink: 0;
}

.price-block {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.1;
}

.price-unit {
  font-size: 13px;
  color: #6e6e73;
}

.book-btn {
  min-width: 88px;
  padding: 8px 20px;
  background: #2997ff;
  color: #ffffff;
  border: none;
  border-radius: 40px;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.2;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
  white-space: nowrap;
}

.book-btn:hover {
  background: #0066cc;
}

.book-btn.active {
  background: rgba(41, 151, 255, 0.1);
  color: #2997ff;
}

.book-btn:focus-visible {
  outline: 2px solid #2997ff;
  outline-offset: 2px;
}

.empty-hint {
  padding: 40px 16px;
  text-align: center;
  color: #6e6e73;
  font-size: 14px;
  border-radius: 24px;
  background: #f5f5f7;
}

/* ===== 滚动容器：固定高度 + 内部滚动 + 隐藏滚动条（参考 Recall 639323 两层结构） ===== */
.room-list-scroll {
  max-height: 520px;              /* 桌面：约能放 4.5 张卡，提示下方还有可滚动 */
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding-right: 2px;
  box-sizing: border-box;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch; /* iOS 惯性滚动 */
}
/* Chrome / Safari / Edge 新版 隐藏滚动条 */
.room-list-scroll::-webkit-scrollbar {
  display: none;
  width: 0;
  height: 0;
}
/* Firefox 隐藏滚动条 */
.room-list-scroll {
  scrollbar-width: none;
}
/* IE / 旧 Edge 隐藏滚动条 */
.room-list-scroll {
  -ms-overflow-style: none;
}
/* 原 .room-option 有 margin-bottom:16px；用 flex gap 后去重避免双倍 */
.room-list-scroll > .room-option {
  margin-bottom: 0 !important;
}

/* 平板：缩小到 460px */
@media (max-width: 1199px) {
  .room-list-scroll {
    max-height: 460px;
  }
}
/* 手机：再缩 400px，手机屏本来就短不占太多空间 */
@media (max-width: 767px) {
  .room-list-scroll {
    max-height: 400px;
    gap: 12px;
  }
}

/* 响应式：手机竖排 */
@media (max-width: 767px) {
  .room-option {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    padding: 20px;
  }

  .room-price {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
  }

  .section-title {
    font-size: 17px;
  }

  .price-value {
    font-size: 20px;
  }
}
</style>
