<template>
    <div class="admin-settings-container">
        <aside class="admin-sidebar">
            <div class="admin-logo">
                <span>项目导航</span>
                <button @click="handleClose">
                    <svg t="1752995368833" class="admin-nav-icon" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="4451" width="256" height="256">
                        <path
                            d="M512 1024A512 512 0 1 1 512 0a512 512 0 0 1 0 1024zM305.956571 370.395429L447.488 512 305.956571 653.604571a45.568 45.568 0 1 0 64.438858 64.438858L512 576.512l141.604571 141.531429a45.568 45.568 0 0 0 64.438858-64.438858L576.512 512l141.531429-141.604571a45.568 45.568 0 1 0-64.438858-64.438858L512 447.488 370.395429 305.956571a45.568 45.568 0 0 0-64.438858 64.438858z"
                            fill="#FF2525" p-id="4452"></path>
                    </svg>
                    <span class="admin-nav-text">关闭</span>
                </button>
            </div>
            <nav class="admin-nav-menu">
                <ul>
                    <li
                        v-for="menu in navMenu"
                        :key="menu.id"
                        :class="{ 'admin-active': activeMenu === menu.id }"
                        @click="handleMenuClick(menu.id)"
                    >   
                    <div class="admin-nav-item">
                        <span class="admin-nav-icon">{{ menu.icon }}</span>
                        <span class="admin-nav-text">{{ menu.title }}</span>
                        <span class="menu-arrow" :class="{ expanded: activeMenu === menu.id }">›</span>
                    </div>

                        <ul v-if="activeMenu === menu.id" class="sub-menu">
                            <li
                                v-for="child in menu.children"
                                :key="child.id"
                                class="sub-menu-item"
                            >
                                <span>{{ child.title }}</span>
                            </li>
                        </ul>
                    </li>
                </ul>
            </nav>
            <div class="admin-sidebar-footer">
                <div class="admin-user-info">
                    <img :src="userInfo.avatar" alt="用户头像" class="admin-user-avatar">
                    <span class="admin-user-name">{{ userInfo.username }}</span>
                </div>
            </div>
        </aside>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

// 关闭
const emit = defineEmits(['close']);
const handleClose = () => {
    emit('close');
};
// 用户信息
const userInfo = ref({
    image: '',
    username: '',
});

// 导航菜单模拟数据
const navMenu = ref([
    {
        id: 1,
        title: '系统设置',
        icon: '⚙️',
        children: [
            { id: 11, title: '基础配置', path: '/admin/settings/basic' },
            { id: 12, title: '权限管理', path: '/admin/settings/permissions' },
            { id: 13, title: '日志管理', path: '/admin/settings/logs' },
        ],
    },
    {
        id: 2,
        title: '内容管理',
        icon: '📝',
        children: [
            { id: 21, title: '月度推荐', path: '/admin/content/monthly' },
            { id: 22, title: '用户管理', path: '/admin/content/users' },
            { id: 23, title: '美食管理', path: '/admin/content/foods' },
            { id: 24, title: '轮播图管理', path: '/admin/content/banners' },
        ],
    },
    {
        id: 3,
        title: '数据统计',
        icon: '📊',
        children: [
            { id: 31, title: '访问统计', path: '/admin/analytics/visits' },
            { id: 32, title: '用户分析', path: '/admin/analytics/users' },
            { id: 33, title: '销售报表', path: '/admin/analytics/sales' },
        ],
    },
    {
        id: 4,
        title: '系统监控',
        icon: '🔍',
        children: [
            { id: 41, title: '服务器状态', path: '/admin/monitor/server' },
            { id: 42, title: '数据库监控', path: '/admin/monitor/database' },
            { id: 43, title: '缓存状态', path: '/admin/monitor/cache' },
        ],
    },
]);

// 当前激活的菜单
const activeMenu = ref(1);

// 切换菜单
const handleMenuClick = (menuId) => {
    activeMenu.value = menuId;
};

const loading = ref(false);
// 获取用户信息
const fetchUserInfo = async () => {
    // 先从本地存储获取，优化用户体验
    const localUser = JSON.parse(localStorage.getItem('user') || '{}');
    userInfo.value = { ...localUser };
};

onMounted(() => {
    fetchUserInfo();
});
</script>

<style scoped>
.admin-settings-container {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    justify-content: flex-end;
    background-color: rgba(0, 0, 0, 0.3);
    z-index: 1000;
    transition: opacity 0.2s ease;
    backdrop-filter: blur(10px);
}

/* 侧边栏样式 */
.admin-sidebar {
    width: 280px;
    background: #ffffff;
    box-shadow: 0 0 40px rgba(0, 0, 0, 0.1), 0 20px 60px -20px rgba(0, 0, 0, 0.05);
    height: 100vh;
    transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    display: flex;
    flex-direction: column;
    border-left: 1px solid #d2d2d6;
}

.admin-logo {
    display: flex;
    justify-content: space-between;
    flex-direction: row;
    align-items: center;
    padding: 16px 20px;
    font-size: 16px;
    font-weight: 600;
    text-align: center;
    color: #1d1d1f;
    border-bottom: 1px solid #d2d2d6;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}
.admin-logo button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 12px;
    color: #6e6e73;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.2s ease;
    font-size: 13px;
    font-weight: 500;
}
.admin-logo button:hover { 
    background-color: #f5f5f7;
    color: #2997ff;
}
.admin-nav-menu {
    flex: 1;
    overflow-y: auto;
    padding: 16px 0;
}

.admin-nav-menu > ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.admin-nav-menu > ul > li {
    margin: 4px 16px;
    padding: 10px 16px;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
    color: #1d1d1f;
    position: relative;
    display: flex;
    align-items: flex-start;
    flex-direction: column;
}
.admin-nav-item {
    display: flex;
    align-items: center;
    flex-direction: row;
    justify-content: flex-start;
    gap: 12px;
}   

.admin-nav-menu > ul > li:hover {
    background-color: #f5f5f7;
    transform: scale(1.01);
}

.admin-nav-menu > ul > li.admin-active {
    background-color: #f5f5f7;
    color: #2997ff;
}

.admin-nav-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 18px;
    height: 18px;
    font-size: 16px;
    color: #6e6e73;
    transition: color 0.2s ease;
    flex-shrink: 0;
}

.admin-nav-menu > ul > li:hover .admin-nav-icon,
.admin-nav-menu > ul > li.admin-active .admin-nav-icon {
    color: #2997ff;
}

.admin-nav-text {
    font-size: 14px;
    font-weight: 500;
    line-height: 1;
    flex: 1;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}

.menu-arrow {
    font-size: 14px;
    color: #a1a1a6;
    transition: transform 0.2s ease, color 0.2s ease;
    flex-shrink: 0;
}

.admin-nav-menu > ul > li:hover .menu-arrow,
.admin-nav-menu > ul > li.admin-active .menu-arrow {
    color: #2997ff;
}

.menu-arrow.expanded {
    transform: rotate(90deg);
}

.sub-menu {
    list-style: none;
    padding: 8px 0;
    margin: 4px 0;
    border-top: 1px solid #d2d2d6;
}

.sub-menu-item {
    padding: 10px 16px 10px 0px;
    margin: 0;
    font-size: 13px;
    color: #6e6e73;
    cursor: pointer;
    transition: all 0.2s ease;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}

.sub-menu-item:hover {
    color: #000000;
    background-color: rgba(55, 158, 255, 0.514);
    padding-left: 56px;
    border-radius: 0 12px 12px 0;
}

.admin-sidebar-footer {
    padding: 16px 20px;
    border-top: 1px solid #d2d2d6;
    background-color: #f5f5f7;
}

.admin-user-info {
    display: flex;
    align-items: center;
    flex-direction: row;
    justify-content: flex-start;
}

.admin-user-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    margin-right: 12px;
    object-fit: cover;
    border: 2px solid #ffffff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.admin-user-name {
    font-size: 14px;
    font-weight: 600;
    color: #1d1d1f;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}

.admin-user-detail {
    display: flex;
    flex-direction: column;
}

.admin-user-role {
    display: block;
    font-size: 12px;
    color: #6e6e73;
    margin-top: 2px;
    font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
}

.admin-user-status {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    margin-top: 12px;
    padding-top: 12px;
    border-top: 1px solid #d2d2d6;
    font-size: 12px;
    color: #6e6e73;
}

.status-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: #a1a1a6;
}

.status-dot.online {
    background-color: #34c759;
    box-shadow: 0 0 8px rgba(52, 199, 89, 0.5);
}

.status-dot.offline {
    background-color: #a1a1a6;
}
</style>