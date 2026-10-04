<template>
    <div class="destination-list-container">
        <!-- 页头 -->
        <header class="page-header">
            <h1 class="destination-list-title">热门目的地</h1>
            <p class="page-subtitle" v-if="!loading && !error">
                覆盖 <b>{{ stats.countryCount }}</b> 个国家 · <b>{{ stats.cityCount }}</b> 座城市
            </p>
        </header>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在加载目的地数据...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
            <div class="error-icon">⚠️</div>
            <h3>数据加载失败</h3>
            <p>{{ error }}</p>
            <button @click="fetchDestinationData" class="retry-button">重新加载</button>
        </div>

        <!-- 正常显示 -->
        <template v-else>
            <!-- 搜索 + 大洲筛选 -->
            <div class="filter-toolbar">
                <div class="search-box">
                    <span class="search-icon">🔍</span>
                    <input
                        v-model="keyword"
                        type="text"
                        class="search-input"
                        placeholder="搜索城市 / 国家 / 亮点"
                    />
                    <button v-if="keyword" class="search-clear" @click="keyword = ''">×</button>
                </div>
                <div class="tabs">
                    <button
                        v-for="tab in continentTabs"
                        :key="tab.id"
                        :class="{ active: currentContinent === tab.id }"
                        class="tab-button"
                        @click="currentContinent = tab.id"
                    >
                        {{ tab.name }}
                        <span class="tab-count">{{ tab.count }}</span>
                    </button>
                </div>
            </div>

            <!-- 内容区 -->
            <div class="destinations-container">
                <transition name="fade" mode="out-in">
                    <div v-if="visibleContinents.length > 0" class="continent-groups">
                        <section
                            v-for="continent in visibleContinents"
                            :key="continent.id"
                            class="continent-section"
                        >
                            <h2 class="continent-title">
                                <span class="continent-bar"></span>
                                {{ continent.name }}
                                <span class="continent-count">{{ continent.cityCount }} 座城市</span>
                            </h2>

                            <div class="destinations-grid-container">
                                <article
                                    v-for="country in continent.countries"
                                    :key="country.id"
                                    class="region-card"
                                >
                                    <header class="region-header">
                                        <span class="region-flag">{{ country.flagEmoji }}</span>
                                        <div class="region-name">
                                            <h3 class="region-title">{{ country.chineseName }}</h3>
                                            <p class="region-en">{{ country.name }}</p>
                                        </div>
                                        <span class="region-badge">{{ country.cities.length }} 城市</span>
                                    </header>

                                    <ul class="city-list">
                                        <li
                                            v-for="city in country.cities"
                                            :key="city.id"
                                            class="city-item"
                                            :class="{ expanded: expandedIds.has(city.id) }"
                                            @click="toggleExpand(city.id)"
                                        >
                                            <div class="city-name-row">
                                                <h4 class="city-name">{{ city.chineseName }}</h4>
                                                <span class="city-en">{{ city.name }}</span>
                                                <span v-if="city.isCapital" class="capital-tag">首都</span>
                                            </div>

                                            <p v-if="city.famousFor" class="city-famous">
                                                🌟 {{ city.famousFor }}
                                            </p>

                                            <div
                                                v-if="city.population || city.area || city.timezone || city.bestSeason"
                                                class="city-meta"
                                            >
                                                <span v-if="city.population" class="meta-chip">
                                                    👥 {{ city.population }}万
                                                </span>
                                                <span v-if="city.area" class="meta-chip">
                                                    📐 {{ city.area }} km²
                                                </span>
                                                <span v-if="city.timezone" class="meta-chip">
                                                    🕐 {{ city.timezone }}
                                                </span>
                                                <span v-if="city.bestSeason" class="meta-chip season">
                                                    ☀️ {{ city.bestSeason }}
                                                </span>
                                            </div>

                                            <p v-if="city.description" class="city-description">
                                                {{ city.description }}
                                            </p>

                                            <div class="city-footer">
                                                <a
                                                    v-if="city.latitude && city.longitude"
                                                    class="map-link"
                                                    :href="`https://www.google.com/maps?q=${city.latitude},${city.longitude}`"
                                                    target="_blank"
                                                    rel="noopener"
                                                    @click.stop
                                                >
                                                    📍 查看地图
                                                </a>
                                                <span class="expand-hint">
                                                    {{ expandedIds.has(city.id) ? '收起' : '展开简介' }}
                                                </span>
                                            </div>
                                        </li>
                                    </ul>
                                </article>
                            </div>
                        </section>
                    </div>

                    <!-- 空状态 -->
                    <div v-else class="empty-state">
                        <div class="empty-icon">🌎</div>
                        <h3>{{ keyword ? '未找到匹配的目的地' : '更多目的地即将上线' }}</h3>
                        <p>{{ keyword ? '换个关键词试试吧' : '我们正在努力添加更多精彩旅行目的地' }}</p>
                    </div>
                </transition>
            </div>
        </template>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import citiesApi from '@/api/cities.js';
import countriesApi from '@/api/countries.js';

// 大洲编号 -> 名称（对应 continents 表 1-6）
const CONTINENT_NAMES = {
    1: '亚洲',
    2: '欧洲',
    3: '北美洲',
    4: '南美洲',
    5: '非洲',
    6: '大洋洲'
};

const loading = ref(false);
const error = ref(null);
const keyword = ref('');
const currentContinent = ref('all');
const expandedIds = reactive(new Set());

// 原始数据
const cityList = ref([]);
const countryMap = ref(new Map());

// 人口单位兼容：新数据为“万”，旧数据为绝对人数
const formatPopulation = (population) => {
    const value = Number(population);
    if (!value) return null;
    return value > 10000 ? Math.round(value / 10000) : value;
};

// 面积格式化：千位分隔，超千米取整
const formatArea = (area) => {
    const value = Number(area);
    if (!value) return null;
    return value >= 1000
        ? Math.round(value).toLocaleString()
        : Number(value.toFixed(1)).toLocaleString();
};

// 获取国家 + 城市数据
const fetchDestinationData = async () => {
    loading.value = true;
    error.value = null;

    try {
        const [cityRes, countryRes] = await Promise.all([
            citiesApi.getCitiesList(1, 200),
            countriesApi.getCountriesList(1, 120)
        ]);

        if (cityRes.code !== 200 || countryRes.code !== 200) {
            throw new Error(cityRes.message || countryRes.message || '接口返回异常');
        }

        // 国家映射：id -> { 中文名 / 英文名 / 国旗 / 大洲 }
        const map = new Map();
        (countryRes.data?.records ?? []).forEach((country) => {
            map.set(country.id, {
                id: country.id,
                chineseName: country.chineseName,
                name: country.name,
                flagEmoji: country.flagEmoji || '🏳️',
                continentId: country.continentId
            });
        });
        countryMap.value = map;

        // 城市去重：同国家同名的旧数据（绝对人口）让位于新数据（单位：万）
        const latestById = new Map();
        (cityRes.data?.records ?? []).forEach((city) => {
            if (!map.has(city.countryId)) return;
            latestById.set(city.id, city);
        });
        const seen = new Map();
        const uniqueCities = [];
        [...latestById.values()]
            .sort((a, b) => a.id - b.id)
            .forEach((city) => {
                const key = `${city.countryId}-${city.chineseName}`;
                seen.set(key, city); // 后写覆盖先写，保留较大 id
            });
        seen.forEach((city) => uniqueCities.push(city));

        cityList.value = uniqueCities.map((city) => ({
            ...city,
            isCapital: !!city.isCapital,
            population: formatPopulation(city.population),
            area: formatArea(city.area)
        }));
    } catch (err) {
        console.error('获取热门目的地失败:', err);
        error.value = err.message || '网络请求失败，请稍后重试';
    } finally {
        loading.value = false;
    }
};

// 大洲 -> 国家 -> 城市 三级分组（首都优先）
const continentGroups = computed(() => {
    const groups = new Map();
    cityList.value.forEach((city) => {
        const country = countryMap.value.get(city.countryId);
        if (!country) return;
        const continentId = country.continentId;
        if (!CONTINENT_NAMES[continentId]) return;

        if (!groups.has(continentId)) {
            groups.set(continentId, {
                id: continentId,
                name: CONTINENT_NAMES[continentId],
                countries: new Map(),
                cityCount: 0
            });
        }
        const group = groups.get(continentId);
        if (!group.countries.has(country.id)) {
            group.countries.set(country.id, { ...country, cities: [] });
        }
        group.countries.get(country.id).cities.push(city);
        group.cityCount += 1;
    });

    return [...groups.values()]
        .sort((a, b) => a.id - b.id)
        .map((group) => ({
            ...group,
            countries: [...group.countries.values()].map((country) => ({
                ...country,
                cities: [...country.cities].sort(
                    (a, b) => Number(b.isCapital) - Number(a.isCapital) || a.id - b.id
                )
            }))
        }));
});

// 大洲筛选标签（含全部）
const continentTabs = computed(() => {
    const tabs = [{ id: 'all', name: '全部', count: cityList.value.length }];
    continentGroups.value.forEach((group) => {
        tabs.push({ id: group.id, name: group.name, count: group.cityCount });
    });
    return tabs;
});

// 关键词 + 大洲双重过滤
const visibleContinents = computed(() => {
    const kw = keyword.value.trim().toLowerCase();
    return continentGroups.value
        .filter((group) => currentContinent.value === 'all' || group.id === currentContinent.value)
        .map((group) => ({
            ...group,
            countries: group.countries
                .map((country) => {
                    const countryMatch =
                        !kw ||
                        country.chineseName.toLowerCase().includes(kw) ||
                        country.name.toLowerCase().includes(kw);
                    const cities = countryMatch
                        ? country.cities
                        : country.cities.filter(
                              (city) =>
                                  city.chineseName.toLowerCase().includes(kw) ||
                                  (city.name || '').toLowerCase().includes(kw) ||
                                  (city.famousFor || '').toLowerCase().includes(kw)
                          );
                    return { ...country, cities };
                })
                .filter((country) => country.cities.length > 0)
        }))
        .filter((group) => group.countries.length > 0);
});

// 统计信息
const stats = computed(() => {
    const countryIds = new Set(cityList.value.map((city) => city.countryId));
    return { countryCount: countryIds.size, cityCount: cityList.value.length };
});

// 展开 / 收起城市简介
const toggleExpand = (cityId) => {
    if (expandedIds.has(cityId)) {
        expandedIds.delete(cityId);
    } else {
        expandedIds.add(cityId);
    }
};

onMounted(() => {
    fetchDestinationData();
});
</script>

<style scoped>
.destination-list-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 1rem 1rem 2rem;
    color: #2d3436;
}

/* 页头 */
.page-header {
    text-align: center;
    margin-bottom: 24px;
}

.destination-list-title {
    font-size: 40px;
    margin-bottom: 8px;
    font-weight: bold;
}

.page-subtitle {
    margin: 0;
    color: #636e72;
    font-size: 14px;
}

.page-subtitle b {
    color: #4a6bff;
}

/* 筛选工具栏 */
.filter-toolbar {
    position: sticky;
    top: 0;
    z-index: 10;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 14px;
    padding: 14px 0;
    margin-bottom: 20px;
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: blur(8px);
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.search-box {
    position: relative;
    display: flex;
    align-items: center;
    width: min(420px, 100%);
}

.search-icon {
    position: absolute;
    left: 12px;
    font-size: 14px;
    pointer-events: none;
}

.search-input {
    width: 100%;
    padding: 10px 36px 10px 36px;
    border: 1.5px solid #e2e6ee;
    border-radius: 999px;
    background: #f7f8fc;
    font-size: 14px;
    color: #2d3436;
    outline: none;
    transition: all 0.25s ease;
}

.search-input:focus {
    border-color: #4a6bff;
    background: #ffffff;
    box-shadow: 0 0 0 4px rgba(74, 107, 255, 0.12);
}

.search-clear {
    position: absolute;
    right: 10px;
    width: 20px;
    height: 20px;
    border: none;
    border-radius: 50%;
    background: #dfe3ec;
    color: #636e72;
    font-size: 13px;
    line-height: 1;
    cursor: pointer;
}

.search-clear:hover {
    background: #cdd3e0;
}

.tabs {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
}

.tab-button {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 18px;
    font-size: 14px;
    font-weight: 600;
    color: #636e72;
    background: #f3f5fa;
    border: 1.5px solid transparent;
    border-radius: 999px;
    cursor: pointer;
    transition: all 0.25s ease;
}

.tab-button:hover {
    color: #4a6bff;
    border-color: rgba(74, 107, 255, 0.35);
}

.tab-button.active {
    color: #ffffff;
    background: linear-gradient(135deg, #4a6bff, #6c5ce7);
    box-shadow: 0 6px 16px rgba(74, 107, 255, 0.32);
}

.tab-count {
    font-size: 12px;
    font-weight: 500;
    padding: 1px 8px;
    border-radius: 999px;
    background: rgba(255, 255, 255, 0.55);
    color: inherit;
}

.tab-button:not(.active) .tab-count {
    background: #e6eaf4;
    color: #636e72;
}

/* 大洲分组 */
.continent-section {
    margin-bottom: 36px;
}

.continent-title {
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 0 0 16px;
    font-size: 20px;
    font-weight: 700;
}

.continent-bar {
    width: 5px;
    height: 20px;
    border-radius: 3px;
    background: linear-gradient(180deg, #4a6bff, #6c5ce7);
}

.continent-count {
    font-size: 13px;
    font-weight: 500;
    color: #636e72;
}

/* 国家卡片 */
.destinations-grid-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
    gap: 24px;
}

.region-card {
    background: #ffffff;
    border: 1px solid rgba(0, 0, 0, 0.04);
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;
}

.region-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 14px 30px rgba(74, 107, 255, 0.14);
}

.region-header {
    display: flex;
    align-items: center;
    gap: 12px;
    padding-bottom: 14px;
    margin-bottom: 14px;
    border-bottom: 1px dashed #e6eaf4;
}

.region-flag {
    font-size: 30px;
    line-height: 1;
}

.region-name {
    flex: 1;
    min-width: 0;
}

.region-title {
    margin: 0;
    font-size: 18px;
    color: #4a6bff;
}

.region-en {
    margin: 2px 0 0;
    font-size: 12px;
    color: #9aa1b2;
}

.region-badge {
    flex-shrink: 0;
    font-size: 12px;
    color: #4a6bff;
    background: rgba(74, 107, 255, 0.1);
    padding: 3px 10px;
    border-radius: 999px;
}

/* 城市列表 */
.city-list {
    list-style: none;
    padding: 0;
    margin: 0;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 12px;
}

.city-item {
    position: relative;
    padding: 14px;
    border-radius: 12px;
    cursor: pointer;
    background: #f8f9fd;
    border: 1px solid #edf0f8;
    transition: all 0.25s ease;
}

.city-item:hover {
    border-color: rgba(74, 107, 255, 0.4);
    background: #ffffff;
    box-shadow: 0 6px 16px rgba(74, 107, 255, 0.1);
    transform: translateY(-2px);
}

.city-name-row {
    display: flex;
    align-items: baseline;
    flex-wrap: wrap;
    gap: 6px;
}

.city-name {
    margin: 0;
    font-size: 16px;
    font-weight: 700;
    color: #2d3436;
}

.city-en {
    font-size: 12px;
    color: #9aa1b2;
}

.capital-tag {
    font-size: 11px;
    color: #b8860b;
    background: rgba(255, 193, 7, 0.16);
    padding: 1px 7px;
    border-radius: 999px;
    font-weight: 600;
}

.city-famous {
    margin: 8px 0 0;
    font-size: 12px;
    color: #4a6bff;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.city-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-top: 10px;
}

.meta-chip {
    font-size: 11px;
    color: #636e72;
    background: #eef1f8;
    padding: 2px 8px;
    border-radius: 999px;
}

.meta-chip.season {
    color: #0f9d58;
    background: rgba(15, 157, 88, 0.1);
}

.city-description {
    margin: 10px 0 0;
    font-size: 13px;
    line-height: 1.55;
    color: #636e72;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.city-item.expanded .city-description {
    -webkit-line-clamp: unset;
}

.city-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}

.map-link {
    font-size: 12px;
    color: #4a6bff;
    text-decoration: none;
}

.map-link:hover {
    text-decoration: underline;
}

.expand-hint {
    font-size: 11px;
    color: #b3b9c9;
}

/* 加载状态样式 */
.loading-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 60px 0;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #4a6bff;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 16px;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

.loading-state p {
    margin: 0;
    color: #636e72;
    font-size: 16px;
}

/* 错误状态样式 */
.error-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 48px 0;
}

.error-icon {
    font-size: 48px;
    margin-bottom: 16px;
}

.error-state h3 {
    margin: 0 0 8px;
    color: #e74c3c;
}

.error-state p {
    margin: 0 0 20px;
    color: #636e72;
    max-width: 400px;
}

.retry-button {
    padding: 10px 24px;
    background: linear-gradient(135deg, #4a6bff, #6c5ce7);
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}

.retry-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(74, 107, 255, 0.3);
}

/* 空状态样式 */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    padding: 48px 0;
}

.empty-icon {
    font-size: 64px;
    margin-bottom: 24px;
    opacity: 0.7;
}

.empty-state h3 {
    margin: 0 0 8px;
    color: #2d3436;
}

.empty-state p {
    margin: 0;
    color: #636e72;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
    .destination-list-title {
        font-size: 30px;
    }

    .filter-toolbar {
        position: static;
    }

    .destinations-grid-container {
        grid-template-columns: 1fr;
    }

    .city-list {
        grid-template-columns: 1fr;
    }
}
</style>
