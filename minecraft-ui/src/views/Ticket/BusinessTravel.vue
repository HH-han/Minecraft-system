<template>
  <div class="rw-svc-page">
    <ServiceHero :eyebrow="$t('railway.businessPage.eyebrow')" :title="$t('railway.businessPage.title')"
      :desc="$t('railway.businessPage.desc')" :icon="Briefcase" tone="deep">
      <div v-if="account" class="rw-svc-hero-badge">
        <el-icon><OfficeBuilding /></el-icon>
        <strong>{{ account.companyName }}</strong>
      </div>
    </ServiceHero>

    <div class="rw-svc-body">
      <!-- 未登录 -->
      <section v-if="!loggedIn" class="rw-svc-card">
        <div class="rw-svc-empty">
          <span class="rw-svc-empty-icon"><el-icon><Briefcase /></el-icon></span>
          <h4>{{ $t('railway.businessPage.loginTitle') }}</h4>
          <p>{{ $t('railway.businessPage.loginDesc') }}</p>
          <div class="rw-svc-actions" style="justify-content: center">
            <button type="button" class="rw-svc-btn" @click="goLogin">
              {{ $t('railway.businessPage.login') }}
            </button>
          </div>
        </div>
      </section>

      <div v-else-if="loading" class="rw-svc-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        {{ $t('railway.common.loading') }}
      </div>

      <section v-else-if="error" class="rw-svc-card">
        <div class="rw-svc-empty">
          <span class="rw-svc-empty-icon"><el-icon><Warning /></el-icon></span>
          <h4>{{ $t('railway.businessPage.loadFailed') }}</h4>
          <p>{{ error }}</p>
          <div class="rw-svc-actions" style="justify-content: center">
            <button type="button" class="rw-svc-btn rw-svc-btn--ghost" @click="loadAll">
              {{ $t('railway.list.retry') }}
            </button>
          </div>
        </div>
      </section>

      <template v-else>
        <!-- 企业账户表单 -->
        <section v-if="!account || editingAccount" class="rw-svc-card">
          <div class="rw-svc-card-head">
            <div>
              <h2 class="rw-svc-card-title">{{ $t('railway.businessPage.accountTitle') }}</h2>
              <p class="rw-svc-card-sub">{{ $t('railway.businessPage.accountDesc') }}</p>
            </div>
            <button v-if="account" type="button" class="rw-svc-link" @click="editingAccount = false">
              {{ $t('railway.businessPage.cancelEdit') }}
            </button>
          </div>

          <div class="rw-svc-form">
            <div class="rw-svc-field rw-svc-field--full">
              <label>{{ $t('railway.businessPage.company') }}</label>
              <el-input v-model="accountForm.companyName"
                :placeholder="$t('railway.businessPage.companyPlaceholder')" maxlength="100" />
            </div>
            <div class="rw-svc-field rw-svc-field--full">
              <label>{{ $t('railway.businessPage.taxNo') }}</label>
              <el-input v-model="accountForm.taxNo" :placeholder="$t('railway.businessPage.taxNoPlaceholder')"
                maxlength="32" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.businessPage.contact') }}</label>
              <el-input v-model="accountForm.contactName" maxlength="50" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.businessPage.phone') }}</label>
              <el-input v-model="accountForm.contactPhone" maxlength="11" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.businessPage.budget') }}（{{ $t('railway.businessPage.budgetUnit') }}）</label>
              <el-input-number v-model="accountForm.monthlyBudget" :min="1000" :step="1000"
                :precision="2" controls-position="right" />
            </div>
          </div>

          <div class="rw-svc-actions">
            <button type="button" class="rw-svc-btn" :disabled="savingAccount" @click="saveAccount">
              <el-icon><OfficeBuilding /></el-icon>
              {{ $t('railway.businessPage.saveAccount') }}
            </button>
          </div>
        </section>

        <template v-else>
          <!-- 预算概览 -->
          <section class="rw-svc-card">
            <div class="rw-svc-card-head">
              <div>
                <h2 class="rw-svc-card-title">
                  {{ $t('railway.businessPage.summaryTitle', { month: summary?.month || '' }) }}
                </h2>
                <p class="rw-svc-card-sub">{{ account.companyName }} · {{ account.contactName }}</p>
              </div>
              <button type="button" class="rw-svc-link" @click="startEditAccount">
                {{ $t('railway.businessPage.editAccount') }}
              </button>
            </div>

            <div class="rw-svc-kpi-grid">
              <div class="rw-svc-kpi">
                <small>{{ $t('railway.businessPage.used') }}</small>
                <strong>¥{{ formatPrice(summary?.usedCents) }}</strong>
              </div>
              <div class="rw-svc-kpi">
                <small>{{ $t('railway.businessPage.remaining') }}</small>
                <strong>¥{{ formatPrice(summary?.remainingCents) }}</strong>
              </div>
              <div class="rw-svc-kpi">
                <small>{{ $t('railway.businessPage.approved') }}</small>
                <strong>{{ summary?.approvedTrips || 0 }}</strong>
              </div>
              <div class="rw-svc-kpi">
                <small>{{ $t('railway.businessPage.usage') }}</small>
                <strong>{{ summary?.usagePercent || 0 }}%</strong>
              </div>
            </div>

            <div class="rw-svc-progress rw-svc-progress--dark">
              <i :style="{ width: `${summary?.usagePercent || 0}%` }"></i>
            </div>
          </section>

          <div class="rw-svc-layout" style="margin-top: 20px">
            <!-- 出差申请 -->
            <section class="rw-svc-card">
              <div class="rw-svc-card-head">
                <div>
                  <h2 class="rw-svc-card-title">{{ $t('railway.businessPage.tripFormTitle') }}</h2>
                  <p class="rw-svc-card-sub">{{ $t('railway.businessPage.tripFormSub') }}</p>
                </div>
              </div>

              <div class="rw-svc-form">
                <div class="rw-svc-field">
                  <label>{{ $t('railway.businessPage.from') }}</label>
                  <StationAutocomplete v-model="tripForm.fromStation"
                    :placeholder="$t('railway.search.placeholderFrom')" />
                </div>
                <div class="rw-svc-field">
                  <label>{{ $t('railway.businessPage.to') }}</label>
                  <StationAutocomplete v-model="tripForm.toStation"
                    :placeholder="$t('railway.search.placeholderTo')" />
                </div>
                <div class="rw-svc-field">
                  <label>{{ $t('railway.businessPage.departDate') }}</label>
                  <el-date-picker v-model="tripForm.departDate" type="date" value-format="YYYY-MM-DD"
                    format="YYYY-MM-DD" :disabled-date="disabledDate" :clearable="false" />
                </div>
                <div class="rw-svc-field">
                  <label>{{ $t('railway.businessPage.returnDate') }}</label>
                  <el-date-picker v-model="tripForm.returnDate" type="date" value-format="YYYY-MM-DD"
                    format="YYYY-MM-DD" :disabled-date="disabledDate" clearable />
                </div>
                <div class="rw-svc-field">
                  <label>{{ $t('railway.businessPage.count') }}</label>
                  <el-input-number v-model="tripForm.passengerCount" :min="1" :max="50"
                    controls-position="right" />
                </div>
                <div class="rw-svc-field">
                  <label>{{ $t('railway.businessPage.amount') }}（{{ $t('railway.businessPage.amountUnit') }}）</label>
                  <el-input-number v-model="tripForm.estimatedAmount" :min="1" :step="100" :precision="2"
                    controls-position="right" />
                </div>
                <div class="rw-svc-field rw-svc-field--full">
                  <label>{{ $t('railway.businessPage.purpose') }}</label>
                  <el-input v-model="tripForm.purpose"
                    :placeholder="$t('railway.businessPage.purposePlaceholder')" maxlength="255" />
                </div>
              </div>

              <div class="rw-svc-note rw-svc-note--warm" style="margin-top: 16px">
                {{ $t('railway.businessPage.rejectedHint') }}
              </div>

              <div class="rw-svc-actions">
                <button type="button" class="rw-svc-btn" :disabled="submitting" @click="submitTrip">
                  <el-icon><Promotion /></el-icon>
                  {{ submitting ? $t('railway.businessPage.submitting') : $t('railway.businessPage.submit') }}
                </button>
              </div>
            </section>

            <!-- 出差申请列表 -->
            <section class="rw-svc-card">
              <div class="rw-svc-card-head">
                <h2 class="rw-svc-card-title">{{ $t('railway.businessPage.tripsTitle') }}</h2>
                <span class="rw-svc-card-head-right">
                  {{ $t('railway.common.count', { n: total }) }}
                </span>
              </div>

              <div v-if="tripsLoading" class="rw-svc-loading">
                <el-icon class="is-loading"><Loading /></el-icon>
                {{ $t('railway.common.loading') }}
              </div>

              <div v-else-if="trips.length === 0" class="rw-svc-empty">
                <span class="rw-svc-empty-icon"><el-icon><Tickets /></el-icon></span>
                <h4>{{ $t('railway.businessPage.tripsEmpty') }}</h4>
                <p>{{ $t('railway.businessPage.tripsEmptyDesc') }}</p>
              </div>

              <template v-else>
                <div class="rw-svc-list">
                  <article v-for="item in trips" :key="item.id" class="rw-svc-item">
                    <div class="rw-svc-item-head">
                      <span class="rw-svc-item-title">{{ item.fromStation }} → {{ item.toStation }}</span>
                      <span class="rw-svc-status" :class="`rw-svc-status--${item.status}`">
                        {{ $t(`railway.businessPage.status.${item.status}`) }}
                      </span>
                    </div>
                    <div class="rw-svc-item-meta">
                      <span>
                        <el-icon><Calendar /></el-icon>
                        {{ item.departDate }}
                      </span>
                      <span v-if="item.returnDate">→ {{ item.returnDate }}</span>
                      <span>
                        <el-icon><UserFilled /></el-icon>
                        {{ item.passengerCount }}
                      </span>
                      <span>{{ item.purpose }}</span>
                    </div>
                    <p v-if="item.auditRemark" class="rw-bt-audit">
                      {{ $t('railway.businessPage.auditRemark') }}：{{ item.auditRemark }}
                    </p>
                    <div class="rw-svc-item-foot">
                      <span class="rw-svc-amount">
                        {{ `¥${formatPrice(item.estimatedAmountCents)}` }}
                      </span>
                      <button v-if="item.status === 'APPROVED'" type="button"
                        class="rw-svc-btn rw-svc-btn--ghost rw-svc-btn--sm" @click="cancelTrip(item)">
                        {{ $t('railway.businessPage.cancel') }}
                      </button>
                    </div>
                  </article>
                </div>

                <div v-if="total > pageSize" class="rw-svc-pagination">
                  <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize"
                    :current-page="pageNum" @current-change="handlePage" />
                </div>
              </template>
            </section>
          </div>
        </template>
      </template>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Briefcase,
  Calendar,
  Loading,
  OfficeBuilding,
  Promotion,
  Tickets,
  UserFilled,
  Warning,
} from '@element-plus/icons-vue'
import ServiceHero from './components/ServiceHero.vue'
import StationAutocomplete from './components/StationAutocomplete.vue'
import {
  cancelRailwayBusinessTrip,
  createRailwayBusinessTrip,
  getRailwayBusinessAccount,
  getRailwayBusinessSummary,
  getRailwayBusinessTrips,
  saveRailwayBusinessAccount,
} from '@/api/railway'
import { formatPrice, hasLogin, todayString } from './utils'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const loggedIn = ref(hasLogin())
const loading = ref(false)
const error = ref('')
const account = ref(null)
const summary = ref(null)
const editingAccount = ref(false)
const savingAccount = ref(false)

const accountForm = reactive({
  companyName: '',
  taxNo: '',
  contactName: '',
  contactPhone: '',
  monthlyBudget: 10000,
})

const tripForm = reactive({
  fromStation: '',
  toStation: '',
  departDate: todayString(),
  returnDate: '',
  purpose: '',
  passengerCount: 1,
  estimatedAmount: 500,
})

const trips = ref([])
const tripsLoading = ref(false)
const pageNum = ref(1)
const pageSize = 5
const total = ref(0)
const submitting = ref(false)

const disabledDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return date.getTime() < today.getTime()
}

const fillAccountForm = () => {
  if (!account.value) return
  accountForm.companyName = account.value.companyName || ''
  accountForm.taxNo = account.value.taxNo || ''
  accountForm.contactName = account.value.contactName || ''
  accountForm.contactPhone = account.value.contactPhone || ''
  accountForm.monthlyBudget = Number(account.value.monthlyBudgetCents || 0) / 100
}

const loadAll = async () => {
  if (!hasLogin()) {
    loggedIn.value = false
    return
  }
  loggedIn.value = true
  loading.value = true
  error.value = ''
  try {
    const accountRes = await getRailwayBusinessAccount()
    account.value = accountRes?.data || null
    fillAccountForm()
    if (account.value) {
      const summaryRes = await getRailwayBusinessSummary()
      summary.value = summaryRes?.data || null
      await loadTrips()
    }
  } catch (err) {
    error.value = err?.message || t('railway.businessPage.loadFailed')
  } finally {
    loading.value = false
  }
}

const loadTrips = async () => {
  tripsLoading.value = true
  try {
    const res = await getRailwayBusinessTrips({ pageNum: pageNum.value, pageSize })
    trips.value = res?.data?.records || []
    total.value = Number(res?.data?.total || 0)
  } catch {
    trips.value = []
    total.value = 0
  } finally {
    tripsLoading.value = false
  }
}

const saveAccount = async () => {
  if (!accountForm.companyName?.trim()) {
    ElMessage.warning(t('railway.businessPage.company'))
    return
  }
  if (!accountForm.contactName?.trim()) {
    ElMessage.warning(t('railway.businessPage.contact'))
    return
  }
  if (!/^1[3-9]\d{9}$/.test(accountForm.contactPhone || '')) {
    ElMessage.warning(t('railway.businessPage.phone'))
    return
  }
  if (!accountForm.monthlyBudget || accountForm.monthlyBudget < 1000) {
    ElMessage.warning(t('railway.businessPage.budget'))
    return
  }
  savingAccount.value = true
  try {
    const res = await saveRailwayBusinessAccount({
      companyName: accountForm.companyName.trim(),
      taxNo: accountForm.taxNo?.trim() || null,
      contactName: accountForm.contactName.trim(),
      contactPhone: accountForm.contactPhone.trim(),
      monthlyBudget: accountForm.monthlyBudget,
    })
    account.value = res?.data || null
    editingAccount.value = false
    fillAccountForm()
    ElMessage.success(t('railway.businessPage.accountSaved'))
    await loadAll()
  } catch (err) {
    ElMessage.error(err?.message || t('railway.businessPage.loadFailed'))
  } finally {
    savingAccount.value = false
  }
}

const startEditAccount = () => {
  fillAccountForm()
  editingAccount.value = true
}

const submitTrip = async () => {
  if (!tripForm.fromStation?.trim()) {
    ElMessage.warning(t('railway.search.needFrom'))
    return
  }
  if (!tripForm.toStation?.trim()) {
    ElMessage.warning(t('railway.search.needTo'))
    return
  }
  if (tripForm.fromStation.trim() === tripForm.toStation.trim()) {
    ElMessage.warning(t('railway.search.sameCity'))
    return
  }
  if (!tripForm.departDate) {
    ElMessage.warning(t('railway.search.needDate'))
    return
  }
  if (tripForm.returnDate && tripForm.returnDate < tripForm.departDate) {
    ElMessage.warning(t('railway.businessPage.returnDate'))
    return
  }
  if (!tripForm.purpose?.trim()) {
    ElMessage.warning(t('railway.businessPage.purpose'))
    return
  }
  if (!tripForm.estimatedAmount || tripForm.estimatedAmount <= 0) {
    ElMessage.warning(t('railway.businessPage.amount'))
    return
  }
  submitting.value = true
  try {
    const res = await createRailwayBusinessTrip({
      fromStation: tripForm.fromStation.trim(),
      toStation: tripForm.toStation.trim(),
      departDate: tripForm.departDate,
      returnDate: tripForm.returnDate || null,
      purpose: tripForm.purpose.trim(),
      passengerCount: tripForm.passengerCount,
      estimatedAmount: tripForm.estimatedAmount,
    })
    const created = res?.data
    if (created?.status === 'REJECTED') {
      ElMessage.warning(created.auditRemark || t('railway.businessPage.rejectedHint'))
    } else {
      ElMessage.success(t('railway.businessPage.submitSuccess'))
    }
    tripForm.purpose = ''
    pageNum.value = 1
    await loadAll()
  } catch (err) {
    ElMessage.error(err?.message || t('railway.businessPage.loadFailed'))
  } finally {
    submitting.value = false
  }
}

const handlePage = (value) => {
  pageNum.value = value
  loadTrips()
}

const cancelTrip = async (item) => {
  try {
    await ElMessageBox.confirm(t('railway.businessPage.cancelConfirm'), t('railway.common.confirm'), {
      type: 'warning',
    })
    await cancelRailwayBusinessTrip(item.id)
    ElMessage.success(t('railway.businessPage.cancelSuccess'))
    await loadAll()
  } catch (err) {
    if (err !== 'cancel' && err !== 'close') {
      ElMessage.error(err?.message || t('railway.businessPage.loadFailed'))
    }
  }
}

const goLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } })

onMounted(loadAll)
</script>

<style scoped>
.rw-bt-audit {
  margin: 10px 0 0;
  padding: 8px 11px;
  border-radius: 9px;
  background: var(--rw-soft-2);
  font-size: 11.5px;
  line-height: 1.6;
  color: var(--rw-text-3);
}
</style>
