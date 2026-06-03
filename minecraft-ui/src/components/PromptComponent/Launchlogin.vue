<template>
    <div class="modal-overlay" @click.self="handleCancel">
        <div class="modal-content">
            <!-- 图标区域 -->
            <div class="icon-container">
                <div class="icon-circle">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                        <circle cx="12" cy="12" r="10"></circle>
                        <polyline points="12 6 12 12 16 14"></polyline>
                    </svg>
                </div>
            </div>
            
            <!-- 文字内容 -->
            <div class="content-body">
                <h2 class="title">退出登录</h2>
                <p class="description">退出登录后，您将无法查看收藏的景点、评论、订单等内容。确定要退出吗？</p>
            </div>
            
            <!-- 按钮区域 -->
            <div class="button-container">
                <button class="btn btn-secondary" @click="handleCancel">取消</button>
                <button class="btn btn-primary" @click="handleConfirm">退出登录</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { defineEmits } from 'vue';

const emit = defineEmits(['confirm', 'cancel']);

const handleConfirm = () => {
    emit('confirm');
};

const handleCancel = () => {
    emit('cancel');
};
</script>

<style scoped>
/* 遮罩层 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(20px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
    animation: fadeIn 0.25s ease-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

/* 弹窗内容 */
.modal-content {
    background: #ffffff;
    border-radius: 24px;
    padding: 40px;
    max-width: 420px;
    width: 90%;
    box-shadow: 
        0 20px 60px rgba(0, 0, 0, 0.15),
        0 8px 24px rgba(0, 0, 0, 0.1),
        0 2px 8px rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 28px;
    animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
}

@keyframes slideUp {
    from {
        opacity: 0;
        transform: translateY(20px) scale(0.98);
    }
    to {
        opacity: 1;
        transform: translateY(0) scale(1);
    }
}

/* 图标容器 */
.icon-container {
    width: 80px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.icon-circle {
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: linear-gradient(135deg, #ff3b30 0%, #ff9500 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: 0 8px 24px rgba(255, 59, 48, 0.3);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.icon-circle svg {
    width: 28px;
    height: 28px;
}

.icon-circle:hover {
    transform: scale(1.05);
    box-shadow: 0 12px 32px rgba(255, 59, 48, 0.4);
}

/* 文字内容 */
.content-body {
    text-align: center;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.title {
    font-size: 24px;
    font-weight: 600;
    color: #1d1d1f;
    margin: 0;
    letter-spacing: -0.01em;
}

.description {
    font-size: 14px;
    line-height: 1.5;
    color: #6e6e73;
    margin: 0;
}

/* 按钮容器 */
.button-container {
    display: flex;
    gap: 12px;
    width: 100%;
    padding-top: 8px;
}

/* 按钮样式 */
.btn {
    flex: 1;
    padding: 14px 24px;
    border: none;
    border-radius: 40px;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    position: relative;
    overflow: hidden;
}

.btn::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.3s ease;
}

.btn:hover::before {
    left: 100%;
}

.btn:active {
    transform: scale(0.98);
}

/* 次要按钮 */
.btn-secondary {
    background: #f5f5f7;
    color: #1d1d1f;
}

.btn-secondary:hover {
    background: #e8e8ed;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 主要按钮 */
.btn-primary {
    background: #ff3b30;
    color: white;
}

.btn-primary:hover {
    background: #d62c24;
    box-shadow: 
        0 6px 20px rgba(255, 59, 48, 0.3),
        0 2px 8px rgba(255, 59, 48, 0.2);
}

.btn-primary:active {
    background: #b02619;
}

/* 响应式设计 */
@media (max-width: 480px) {
    .modal-content {
        margin: 20px;
        padding: 32px 24px;
        max-width: none;
        gap: 24px;
    }
    
    .button-container {
        flex-direction: column;
        gap: 10px;
    }
    
    .btn {
        padding: 14px;
    }
    
    .title {
        font-size: 20px;
    }
    
    .description {
        font-size: 13px;
    }
    
    .icon-circle {
        width: 56px;
        height: 56px;
    }
    
    .icon-circle svg {
        width: 24px;
        height: 24px;
    }
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
    .modal-content {
        background: #1d1d1f;
    }
    
    .title {
        color: #ffffff;
    }
    
    .description {
        color: #a1a1a6;
    }
    
    .btn-secondary {
        background: #3a3a3c;
        color: #ffffff;
    }
    
    .btn-secondary:hover {
        background: #4a4a4c;
    }
}
</style>
