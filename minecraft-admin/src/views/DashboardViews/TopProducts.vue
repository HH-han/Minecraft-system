<template>
  <div class="dashboard-card" style="grid-column: 1 / -1;">
    <h3 class="dashboard-card-title">表现最佳的产品</h3>
    <div class="products-table">
      <div class="table-header">
        <div>产品名称</div>
        <div>类别</div>
        <div>销售</div>
        <div>收益</div>
        <div>标签</div>
      </div>

      <div v-for="(product, index) in products" :key="index" class="table-row"
        :style="`animation-delay: ${index * 0.1}s`">
        <div class="product-info">
          <div class="product-name">{{ product.name }}</div>
        </div>
        <div>
          <span class="product-category" :style="{ backgroundColor: product.categoryColor }">
            {{ product.category }}
          </span>
        </div>
        <div class="product-sales">{{ product.sales }}</div>
        <div class="product-revenue">{{ product.revenue }}</div>
        <div class="product-tags">
          <span v-for="(tag, tagIndex) in product.tags" :key="tagIndex" class="tag-icon">
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const products = [
  {
    name: '黄山',
    category: '旅游景区',
    categoryColor: 'rgba(0, 184, 148, 0.15)',
    sales: 234,
    revenue: '￥45,200',
    tags: ['📱', '⚡']
  },
  {
    name: '北京烤鸭',
    category: '美食',
    categoryColor: 'rgba(253, 203, 110, 0.15)',
    sales: 189,
    revenue: '￥32,150',
    tags: ['📺', '🔄']
  },
  {
    name: '巴黎铁塔',
    category: '海外',
    categoryColor: 'rgba(108, 92, 231, 0.15)',
    sales: 156,
    revenue: '￥28,750',
    tags: ['🎧', '🔋']
  },
  {
    name: '普洱茶',
    category: '特产',
    categoryColor: 'rgba(9, 132, 227, 0.15)',
    sales: 132,
    revenue: '￥24,890',
    tags: ['⌚', '❤️']
  }
]
</script>

<style scoped>
.products-table {
  width: 100%;
}

.table-header {
  display: grid;
  grid-template-columns: 3fr 1fr 1fr 1fr 1fr;
  padding: 1rem 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  font-weight: 600;
  color: #4b5563;
  opacity: 0.8;
  font-size: 0.9rem;
}

.table-row {
  display: grid;
  grid-template-columns: 3fr 1fr 1fr 1fr 1fr;
  padding: 1rem 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  animation: fadeIn 0.5s ease-out forwards;
  opacity: 0;
}

.table-row:hover {
  background: rgba(108, 92, 231, 0.03);
  transform: translateX(5px);
}

.product-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.product-name {
  font-weight: 500;
  color: #1f2937;
}

.product-category {
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  color: #4b5563;
}

.product-sales {
  font-weight: 500;
  color: #374151;
}

.product-revenue {
  color: #6c5ce7;
  font-weight: 600;
}

.product-tags {
  display: flex;
  gap: 0.5rem;
}

.tag-icon {
  font-size: 1.1rem;
  transition: transform 0.3s ease;
}

.tag-icon:hover {
  transform: scale(1.5);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .table-header {
    grid-template-columns: 2fr 1fr 1fr;
    gap: 0.5rem;
  }
  
  .table-row {
    grid-template-columns: 2fr 1fr 1fr;
    gap: 0.5rem;
  }
  
  .product-revenue,
  .product-tags {
    display: none;
  }
}
</style>