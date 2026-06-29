<template>
    <div class="pagination">
        <button class="pagination-btn" :class="{ 'is-disabled': currentPage === 1 }" @click="goFirst">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 15l-6-6-6 6"/>
            </svg>
        </button>
        <button class="pagination-btn" :class="{ 'is-disabled': currentPage === 1 }" @click="goPrev">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M15 18l-6-6 6-6"/>
            </svg>
        </button>
        
        <template v-if="totalPages <= 7">
            <button 
                v-for="page in totalPages" 
                :key="page"
                class="pagination-num"
                :class="{ 'is-active': page === currentPage }"
                @click="goPage(page)"
            >
                {{ page }}
            </button>
        </template>
        <template v-else>
            <button 
                v-if="currentPage > 3" 
                class="pagination-num" 
                @click="goPage(1)"
            >
                1
            </button>
            <span v-if="currentPage > 4" class="pagination-ellipsis">...</span>
            
            <button 
                v-if="currentPage > 2" 
                class="pagination-num" 
                @click="goPage(currentPage - 2)"
            >
                {{ currentPage - 2 }}
            </button>
            <button 
                v-if="currentPage > 1" 
                class="pagination-num" 
                @click="goPage(currentPage - 1)"
            >
                {{ currentPage - 1 }}
            </button>
            
            <button class="pagination-num is-active" @click="goPage(currentPage)">
                {{ currentPage }}
            </button>
            
            <button 
                v-if="currentPage < totalPages" 
                class="pagination-num" 
                @click="goPage(currentPage + 1)"
            >
                {{ currentPage + 1 }}
            </button>
            <button 
                v-if="currentPage < totalPages - 1" 
                class="pagination-num" 
                @click="goPage(currentPage + 2)"
            >
                {{ currentPage + 2 }}
            </button>
            
            <span v-if="currentPage < totalPages - 3" class="pagination-ellipsis">...</span>
            <button 
                v-if="currentPage < totalPages - 2" 
                class="pagination-num" 
                @click="goPage(totalPages)"
            >
                {{ totalPages }}
            </button>
        </template>
        
        <button class="pagination-btn" :class="{ 'is-disabled': currentPage === totalPages }" @click="goNext">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 18l6-6-6-6"/>
            </svg>
        </button>
        <button class="pagination-btn" :class="{ 'is-disabled': currentPage === totalPages }" @click="goLast">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M6 15l6-6 6 6"/>
            </svg>
        </button>
        
        <span class="pagination-info">
            共 {{ totalPages }} 页
        </span>
    </div>
</template>
<script setup>
import { computed } from 'vue'

const props = defineProps({
    totalPages: {
        type: Number,
        default: 0
    },
    currentPage: {
        type: Number,
        default: 1
    }
})

const emit = defineEmits(['update:currentPage'])

const goFirst = () => {
    if (props.currentPage !== 1) {
        emit('update:currentPage', 1)
    }
}

const goPrev = () => {
    if (props.currentPage > 1) {
        emit('update:currentPage', props.currentPage - 1)
    }
}

const goPage = (page) => {
    if (page !== props.currentPage) {
        emit('update:currentPage', page)
    }
}

const goNext = () => {
    if (props.currentPage < props.totalPages) {
        emit('update:currentPage', props.currentPage + 1)
    }
}

const goLast = () => {
    if (props.currentPage !== props.totalPages) {
        emit('update:currentPage', props.totalPages)
    }
}
</script>
<style scoped>
.pagination {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 20px 0;
    flex-wrap: wrap;
}

.pagination-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    background: white;
    color: #666;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    font-size: 14px;
}

.pagination-btn:hover:not(.is-disabled) {
    border-color: #e74c3c;
    color: #e74c3c;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(231, 76, 60, 0.15);
}

.pagination-btn.is-disabled {
    opacity: 0.4;
    cursor: not-allowed;
}

.pagination-btn svg {
    width: 16px;
    height: 16px;
}

.pagination-num {
    min-width: 36px;
    height: 36px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    background: white;
    color: #666;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    font-size: 14px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 8px;
}

.pagination-num:hover:not(.is-active) {
    border-color: #e74c3c;
    color: #e74c3c;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(231, 76, 60, 0.15);
}

.pagination-num.is-active {
    background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
    border-color: #e74c3c;
    color: white;
    box-shadow: 0 4px 16px rgba(231, 76, 60, 0.3);
    transform: translateY(-2px);
}

.pagination-ellipsis {
    color: #999;
    font-size: 14px;
    padding: 0 4px;
}

.pagination-info {
    margin-left: 12px;
    color: #999;
    font-size: 14px;
}
</style>
