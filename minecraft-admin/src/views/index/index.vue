<template>
  <div class="dashboard-container">
    <HeaderInfo />
    
    <div class="dashboard-grid">
      <SalesProfitChart />
      <AnnualProfit />
      <ProductSalesPie />
      <MarketingReport />
      <TopProducts />
      <RecentTransactions />
    </div>
  </div>
</template>

<script setup>
import HeaderInfo from '../DashboardViews/HeaderInfo.vue'
import SalesProfitChart from '../DashboardViews/SalesProfitChart.vue'
import AnnualProfit from '../DashboardViews/AnnualProfit.vue'
import ProductSalesPie from '../DashboardViews/ProductSalesPie.vue'
import MarketingReport from '../DashboardViews/MarketingReport.vue'
import TopProducts from '../DashboardViews/TopProducts.vue'
import RecentTransactions from '../DashboardViews/RecentTransactions.vue'
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  padding: 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
}

.dashboard-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.dashboard-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
}

.dashboard-card-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 1.5rem 0;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 1rem;
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }

  .dashboard-card {
    padding: 1rem;
  }
}
</style>