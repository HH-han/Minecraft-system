<template>
    <transition name="fade">
        <div v-if="isVisible" class="back-to-top-container">
            <button class="back-to-top" @click="scrollToTop" @mouseenter="showTooltip = true"
                @mouseleave="showTooltip = false" aria-label="返回顶部">
                <svg t="1774108083765" class="icon" viewBox="0 0 1024 1024" version="1.1"
                    xmlns="http://www.w3.org/2000/svg" p-id="3536" width="256" height="256">
                    <path
                        d="M511.5 102.5c-225.3 0-409.6 184.3-409.6 409.6s184.3 409.6 409.6 409.6 409.6-184.3 409.6-409.6-184.3-409.6-409.6-409.6z m-56.3 716.8h-30.7v-30.7h30.7v30.7z m0-51.2h-30.7v-30.7h30.7v30.7z m76.8 87.1h-46.1v-41h41v41h5.1z m0-61.5h-46.1v-41h41v41h5.1z m56.3 25.6h-30.7v-30.7h30.7v30.7z m0-51.2h-30.7v-30.7h30.7v30.7z m0-302.1v240.6H424.4V466H306.7l204.8-245.8L716.3 466h-128z m0 0"
                        fill="#83CDD9" p-id="3537"></path>
                </svg>
            </button>
            <transition name="tooltip">
                <div v-if="showTooltip" class="tooltip">
                    返回顶部
                </div>
            </transition>
        </div>
    </transition>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const isVisible = ref(false);
const showTooltip = ref(false);
const scrollThreshold = 300; // 滚动阈值

// 滚动监听函数
const handleScroll = () => {
    isVisible.value = window.scrollY > scrollThreshold;
};

// 滚动到顶部
const scrollToTop = () => {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
};

// 生命周期钩子
onMounted(() => {
    window.addEventListener('scroll', handleScroll);
    // 初始检查
    handleScroll();
});

onUnmounted(() => {
    window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
.back-to-top-container {
    position: fixed;
    bottom: 30px;
    right: 30px;
    z-index: 999;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.back-to-top {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.tooltip {
    position: absolute;
    bottom: 100%;
    right: 0;
    margin-bottom: 10px;
    padding: 6px 12px;
    background: rgba(0, 0, 0, 0.8);
    color: #fff;
    border-radius: 6px;
    font-size: 12px;
    white-space: nowrap;
    z-index: 1000;
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
}

.tooltip::after {
    content: '';
    position: absolute;
    top: 100%;
    right: 20px;
    border-width: 5px;
    border-style: solid;
    border-color: rgba(0, 0, 0, 0.8) transparent transparent transparent;
}

.back-to-top:hover {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-5px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

.back-to-top:active {
    transform: translateY(-2px);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
    transform: translateY(20px);
}

/* 气泡提示过渡动画 */
.tooltip-enter-active,
.tooltip-leave-active {
    transition: opacity 0.2s ease, transform 0.2s ease;
}

.tooltip-enter-from,
.tooltip-leave-to {
    opacity: 0;
    transform: translateY(10px);
}

/* 响应式设计 */
@media (max-width: 768px) {
    .back-to-top-container {
        bottom: 20px;
        right: 20px;
    }

    .back-to-top {
        width: 45px;
        height: 45px;
    }
}

@media (max-width: 480px) {
    .back-to-top-container {
        bottom: 15px;
        right: 15px;
    }

    .back-to-top {
        width: 40px;
        height: 40px;
    }

    .tooltip {
        font-size: 11px;
        padding: 4px 10px;
    }
}
</style>