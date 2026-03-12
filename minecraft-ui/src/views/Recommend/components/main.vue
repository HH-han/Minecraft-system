<template>
    <div class="recommend-container">
        <div class="recommend-title">
            <h2>推荐</h2>
        </div>
        <div class="recommend-grid">
            <div v-for="item in recommendList" :key="item.id" class="card">
                <div class="content">
                    <div class="back">
                        <div class="back-content">
                            <img :src="item.image" alt="推荐图片" />
                            <div class="text-overlay">
                                <span>{{ item.name }}</span>
                                <p>{{ item.description }}</p>
                            </div>
                        </div>
                    </div>
                    <div class="front">
                        <div class="img" :style="{ backgroundImage: `url(${item.image})` }">
                            <div class="circle"></div>
                            <div class="circle" id="right"></div>
                            <div class="circle" id="bottom"></div>
                        </div>
                        <div class="front-content">
                            <small class="badge">{{ item.name }}</small>
                            <div class="description">
                                <div class="title">
                                    <p class="title">
                                        <strong>{{ item.name }}</strong>
                                    </p>
                                    <svg fill-rule="nonzero" height="15px" width="15px" viewBox="0,0,256,256"
                                        xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg">
                                        <g style="mix-blend-mode: normal" text-anchor="none" font-size="none"
                                            font-weight="none" font-family="none" stroke-dashoffset="0"
                                            stroke-dasharray="" stroke-miterlimit="10" stroke-linejoin="miter"
                                            stroke-linecap="butt" stroke-width="1" stroke="none" fill-rule="nonzero"
                                            fill="#20c997">
                                            <g transform="scale(8,8)">
                                                <path d="M25,27l-9,-6.75l-9,6.75v-23h18z"></path>
                                            </g>
                                        </g>
                                    </svg>
                                </div>
                                <p class="card-footer">
                                    {{ item.description }}
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getRecommendList } from '@/api/recommend'

const recommendList = ref([])

onMounted(() => {
    loadRecommendList()
})

const loadRecommendList = async () => {
    try {
        const response = await getRecommendList()
        if (response.code === 200 && response.data) {
            recommendList.value = response.data
        }
    } catch (error) {
        console.error('Failed to load recommend list:', error)
    }
}
</script>
<style scoped>
.recommend-container {
    padding: 20px;
}

.recommend-title {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
}

.recommend-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(390px, 1fr));
    gap: 20px;
}

/* From Uiverse.io by ElSombrero2 */
.card {
    overflow: visible;
    width: 100%;
    height: 254px;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    border: 1px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s ease;
    overflow: hidden;
    display: flex;
    padding: 20px;
}

.content {
    width: 100%;
    height: 100%;
    transform-style: preserve-3d;
    transition: transform 300ms;
    box-shadow: 0px 0px 10px 1px #000000ee;
    border-radius: 5px;
}

.front,
.back {
    background-color: #151515;
    position: absolute;
    width: 100%;
    height: 100%;
    backface-visibility: hidden;
    -webkit-backface-visibility: hidden;
    border-radius: 5px;
    overflow: hidden;
}

.back {
    width: 100%;
    height: 100%;
    justify-content: center;
    display: flex;
    align-items: center;
    overflow: hidden;
}

.back::before {
    position: absolute;
    content: ' ';
    display: block;
    width: 400px;
    height: 160%;
    background: linear-gradient(90deg, transparent, #ff9966, #ff9966, #ff9966, #ff9966, transparent);
    animation: rotation_481 5000ms infinite linear;
}

.back-content {
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: #151515;
    border-radius: 5px;
    color: white;
    overflow: hidden;
}

.back-content img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
}

.back-content .text-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
    padding: 20px;
    text-align: center;
}

.back-content span {
    display: block;
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 5px;
    color: white;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.back-content p {
    font-size: 12px;
    margin: 0;
    line-height: 1.4;
    color: rgba(255, 255, 255, 0.9);
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.card:hover .content {
    transform: rotateX(180deg);
}

@keyframes rotation_481 {
    0% {
        transform: rotateZ(0deg);
    }

    100% {
        transform: rotateZ(360deg);
    }
}

.front {
    transform: rotateX(180deg);
    color: white;
}

.front .front-content {
    position: absolute;
    width: 100%;
    height: 100%;
    padding: 10px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.front-content .badge {
    background-color: #00000055;
    padding: 2px 10px;
    border-radius: 10px;
    backdrop-filter: blur(2px);
    width: fit-content;
}

.description {
    box-shadow: 0px 0px 10px 5px #00000088;
    width: 100%;
    padding: 10px;
    background-color: #00000099;
    backdrop-filter: blur(5px);
    border-radius: 5px;
}

.title {
    font-size: 11px;
    max-width: 100%;
    display: flex;
    justify-content: space-between;
}

.title p {
    width: 80%;
    margin: 0;
}

.card-footer {
    color: #ffffff88;
    margin-top: 5px;
    font-size: 8px;
    margin: 5px 0 0 0;
    line-height: 1.3;
}

.front .img {
    position: absolute;
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
}

.circle {
    width: 90px;
    height: 90px;
    border-radius: 50%;
    background-color: #ffbb66;
    position: relative;
    filter: blur(15px);
    animation: floating 2600ms infinite linear;
}

#bottom {
    background-color: #ff8866;
    left: 50px;
    top: 0px;
    width: 150px;
    height: 150px;
    animation-delay: -800ms;
}

#right {
    background-color: #ff2233;
    left: 160px;
    top: -80px;
    width: 30px;
    height: 30px;
    animation-delay: -1800ms;
}

@keyframes floating {
    0% {
        transform: translateY(0px);
    }

    50% {
        transform: translateY(10px);
    }

    100% {
        transform: translateY(0px);
    }
}
</style>