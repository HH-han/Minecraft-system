<template>
  <div class="rw-page">
    <div class="rw-page-inner">
      <div class="rw-page-title">
        <h1>{{ $t('railway.order.title') }}</h1>
        <router-link to="/ticket" class="rw-back">
          <el-icon><ArrowLeft /></el-icon>
          {{ $t('railway.common.back') }}
        </router-link>
      </div>

      <!-- 错误 -->
      <div v-if="error" class="rw-state">
        <span class="rw-state-icon"><el-icon><Warning /></el-icon></span>
        <h3>{{ $t('railway.order.empty') }}</h3>
        <p>{{ error }}</p>
        <button type="button" class="rw-state-btn" @click="$router.push('/ticket')">
          {{ $t('railway.order.backHome') }}
        </button>
      </div>

      <!-- 加载 -->
      <div v-else-if="loading" class="rw-state">
        <span class="rw-state-icon"><el-icon class="is-loading"><Loading /></el-icon></span>
        <p>{{ $t('railway.common.loading') }}</p>
      </div>

      <div v-else class="rw-order-layout">
        <div class="rw-order-main">
          <!-- 车次信息 -->
          <section class="rw-card">
            <div class="rw-card-head">
              <h2>{{ $t('railway.order.trainInfo') }}</h2>
              <span class="rw-card-tag">{{ detail?.serviceDate }}</span>
            </div>
            <div class="rw-train-info">
              <div class="rw-train-info-no">
                <strong>{{ detail?.trainNo }}</strong>
                <span>{{ $t(`railway.list.type.${detail?.trainType}`) }}</span>
              </div>
              <div class="rw-train-info-route">
                <div class="rw-train-info-end">
                  <span class="station">{{ fromStop?.stationName }}</span>
                  <span class="time">{{ fromStop?.departTime || fromStop?.arriveTime }}</span>
                </div>
                <div class="rw-train-info-line">
                  <span class="dash"></span>
                  <el-icon><Right /></el-icon>
                  <span class="dash"></span>
                </div>
                <div class="rw-train-info-end rw-train-info-end--right">
                  <span class="station">{{ toStop?.stationName }}</span>
                  <span class="time">{{ toStop?.arriveTime || toStop?.departTime }}</span>
                </div>
              </div>
            </div>
          </section>

          <!-- 乘车人 -->
          <section class="rw-card">
            <div class="rw-card-head">
              <h2>{{ $t('railway.order.passenger') }}</h2>
              <span class="rw-card-hint">{{ $t('railway.order.maxPassenger') }}</span>
            </div>

            <div v-if="passengers.length === 0" class="rw-empty-passenger">
              <p>{{ $t('railway.order.noPassenger') }}</p>
            </div>

            <div v-else class="rw-passenger-list">
              <label v-for="passenger in passengers" :key="passenger.id" class="rw-passenger"
                :class="{ 'is-selected': selectedIds.includes(passenger.id) }">
                <el-checkbox :model-value="selectedIds.includes(passenger.id)"
                  @change="togglePassenger(passenger.id)" />
                <div class="rw-passenger-main">
                  <div class="rw-passenger-name">
                    <strong>{{ passenger.name }}</strong>
                    <span class="rw-passenger-type">
                      {{ $t(`railway.order.${passengerTypeKey(passenger.passengerType)}`) }}
                    </span>
                  </div>
                  <span class="rw-passenger-id">{{ maskIdCard(passenger.idCardNo) }}</span>
                </div>
                <button type="button" class="rw-passenger-del" :title="$t('railway.order.deletePassenger')"
                  @click.prevent.stop="removePassenger(passenger)">
                  <el-icon><Delete /></el-icon>
                </button>
              </label>
            </div>

            <button type="button" class="rw-passenger-add" @click="openAddDialog">
              <el-icon><Plus /></el-icon>
              {{ $t('railway.order.addPassenger') }}
            </button>
          </section>

          <!-- 联系电话 -->
          <section class="rw-card">
            <div class="rw-card-head">
              <h2>{{ $t('railway.order.contactPhone') }}</h2>
            </div>
            <el-input v-model="contactPhone" :placeholder="$t('railway.order.phonePlaceholder')" maxlength="20"
              class="rw-phone-input">
              <template #prefix>
                <el-icon><Iphone /></el-icon>
              </template>
            </el-input>
          </section>
        </div>

        <!-- 结算 -->
        <aside class="rw-order-aside">
          <div class="rw-summary-card">
            <h2>{{ $t('railway.order.seatType') }}</h2>
            <div class="rw-summary-seat">
              <span class="rw-summary-seat-name">{{ $t(`railway.list.seat.${seatType}`) }}</span>
              <span class="rw-summary-seat-price">¥{{ formatPrice(unitPrice) }}</span>
            </div>
            <dl class="rw-summary-list">
              <div>
                <dt>{{ $t('railway.order.ticketPrice') }}</dt>
                <dd>¥{{ formatPrice(unitPrice) }} × {{ selectedIds.length }}</dd>
              </div>
              <div>
                <dt>{{ $t('railway.order.passenger') }}</dt>
                <dd>{{ selectedIds.length }}</dd>
              </div>
            </dl>
            <div class="rw-summary-total">
              <span>{{ $t('railway.order.total') }}</span>
              <strong>¥{{ formatPrice(totalAmount) }}</strong>
            </div>
            <button type="button" class="rw-submit-order" :disabled="submitting" @click="submitOrder">
              {{ submitting ? $t('railway.order.submitting') : $t('railway.order.submit') }}
            </button>
            <p class="rw-summary-note">{{ $t('railway.search.dateRangeHint') }}</p>
          </div>
        </aside>
      </div>
    </div>

    <!-- 添加乘车人 -->
    <el-dialog v-model="addDialogVisible" :title="$t('railway.order.addPassenger')" width="420px"
      :close-on-click-modal="false">
      <div class="rw-form">
        <label class="rw-form-item">
          <span>{{ $t('railway.order.name') }}</span>
          <el-input v-model="passengerForm.name" :placeholder="$t('railway.order.name')" maxlength="20" />
        </label>
        <label class="rw-form-item">
          <span>{{ $t('railway.order.idCard') }}</span>
          <el-input v-model="passengerForm.idCardNo" :placeholder="$t('railway.order.idCardPlaceholder')"
            maxlength="18" />
        </label>
        <label class="rw-form-item">
          <span>{{ $t('railway.order.passengerType') }}</span>
          <el-select v-model="passengerForm.passengerType" class="w-full">
            <el-option value="ADULT" :label="$t('railway.order.adult')" />
            <el-option value="CHILD" :label="$t('railway.order.child')" />
            <el-option value="STUDENT" :label="$t('railway.order.student')" />
          </el-select>
        </label>
        <label class="rw-form-item">
          <span>{{ $t('railway.order.phone') }}</span>
          <el-input v-model="passengerForm.phone" :placeholder="$t('railway.order.phone')" maxlength="20" />
        </label>
      </div>
      <template #footer>
        <el-button @click="addDialogVisible = false">{{ $t('railway.order.cancel') }}</el-button>
        <el-button type="primary" :loading="adding" @click="addPassenger">
          {{ $t('railway.order.confirmAdd') }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 系统支付面板 -->
    <div v-if="payVisible" class="rw-pay-overlay" @click.self="closePay(false)">
      <div class="rw-pay-modal" @click.stop>
        <button type="button" class="rw-pay-close" :aria-label="$t('railway.common.close')" @click="closePay(false)">
          <el-icon><Close /></el-icon>
        </button>
        <PayPage order-type="railway" :order-id="String(createdOrder?.id || '')" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addRailwayPassenger,
  createRailwayOrder,
  deleteRailwayPassenger,
  getRailwayPassengers,
  getRailwayServiceDetail,
} from '@/api/railway'
import PayPage from '@/components/Payment/PayPage.vue'
import {
  formatPrice,
  queryString,
} from './utils'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const serviceId = Number(queryString(route.query.serviceId, 0))
const fromSeq = Number(queryString(route.query.fromSeq, 0))
const toSeq = Number(queryString(route.query.toSeq, 0))
const seatType = queryString(route.query.seatType, 'SECOND_CLASS')

const loading = ref(true)
const error = ref('')
const detail = ref(null)
const passengers = ref([])
const selectedIds = ref([])
const contactPhone = ref('')
const submitting = ref(false)

const passengerForm = reactive({
  name: '',
  idCardNo: '',
  passengerType: 'ADULT',
  phone: '',
})
const addDialogVisible = ref(false)
const adding = ref(false)

const payVisible = ref(false)
const createdOrder = ref(null)

const fromStop = computed(() => detail.value?.stops?.find((stop) => stop.stopSeq === fromSeq))
const toStop = computed(() => detail.value?.stops?.find((stop) => stop.stopSeq === toSeq))

const detailSeatPrice = computed(
  () => detail.value?.seats?.find((seat) => seat.seatType === seatType)?.priceCents || 0,
)
const unitPrice = computed(() => Number(queryString(route.query.price, 0)) || detailSeatPrice.value)
const totalAmount = computed(() => unitPrice.value * selectedIds.value.length)

const passengerTypeKey = (type) => {
  if (type === 'CHILD') return 'child'
  if (type === 'STUDENT') return 'student'
  return 'adult'
}

const maskIdCard = (idCardNo) => {
  if (!idCardNo) return ''
  return String(idCardNo).replace(/^(.{4}).*(.{4})$/, '$1**********$2')
}

const load = async () => {
  if (!serviceId || !fromSeq || !toSeq) {
    loading.value = false
    error.value = t('railway.order.empty')
    return
  }
  loading.value = true
  error.value = ''
  try {
    const [detailRes, passengerRes] = await Promise.all([
      getRailwayServiceDetail(serviceId),
      getRailwayPassengers(),
    ])
    detail.value = detailRes?.data || null
    passengers.value = passengerRes?.data || []
    if (passengers.value.length > 0) {
      selectedIds.value = [passengers.value[0].id]
      contactPhone.value = passengers.value[0].phone || ''
    }
  } catch (err) {
    error.value = err?.message || t('railway.order.empty')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  load()
  window.addEventListener('message', onPayMessage)
})

onBeforeUnmount(() => {
  window.removeEventListener('message', onPayMessage)
})

const togglePassenger = (id) => {
  const index = selectedIds.value.indexOf(id)
  if (index >= 0) {
    selectedIds.value.splice(index, 1)
    return
  }
  if (selectedIds.value.length >= 5) {
    ElMessage.warning(t('railway.order.maxPassenger'))
    return
  }
  selectedIds.value.push(id)
}

const removePassenger = async (passenger) => {
  try {
    await ElMessageBox.confirm(t('railway.order.deleteConfirm'), t('railway.order.deletePassenger'), {
      confirmButtonText: t('railway.common.confirm'),
      cancelButtonText: t('railway.common.cancel'),
      type: 'warning',
    })
    await deleteRailwayPassenger(passenger.id)
    passengers.value = passengers.value.filter((item) => item.id !== passenger.id)
    selectedIds.value = selectedIds.value.filter((id) => id !== passenger.id)
    ElMessage.success(t('railway.order.deleteSuccess'))
  } catch {
    // 取消删除
  }
}

const openAddDialog = () => {
  passengerForm.name = ''
  passengerForm.idCardNo = ''
  passengerForm.passengerType = 'ADULT'
  passengerForm.phone = ''
  addDialogVisible.value = true
}

const addPassenger = async () => {
  if (!passengerForm.name.trim()) {
    ElMessage.warning(t('railway.order.name'))
    return
  }
  if (!/^\d{15}$|^\d{17}[\dXx]$/.test(passengerForm.idCardNo.trim())) {
    ElMessage.warning(t('railway.order.idCardPlaceholder'))
    return
  }
  adding.value = true
  try {
    const res = await addRailwayPassenger({
      name: passengerForm.name.trim(),
      idCardNo: passengerForm.idCardNo.trim().toUpperCase(),
      passengerType: passengerForm.passengerType,
      phone: passengerForm.phone.trim(),
    })
    addDialogVisible.value = false
    ElMessage.success(t('railway.order.addSuccess'))
    const created = res?.data
    if (created) {
      passengers.value = [created, ...passengers.value]
      if (selectedIds.value.length < 5) selectedIds.value.push(created.id)
      if (!contactPhone.value && created.phone) contactPhone.value = created.phone
    }
  } catch {
    // 接口已提示错误
  } finally {
    adding.value = false
  }
}

const submitOrder = async () => {
  if (selectedIds.value.length === 0) {
    ElMessage.warning(t('railway.order.selectPassenger'))
    return
  }
  submitting.value = true
  try {
    const res = await createRailwayOrder({
      serviceId,
      fromStopSeq: fromSeq,
      toStopSeq: toSeq,
      seatType,
      passengerIds: selectedIds.value,
      contactPhone: contactPhone.value,
    })
    createdOrder.value = res?.data || null
    if (!createdOrder.value?.id) {
      throw new Error(t('railway.order.createFailed'))
    }
    payVisible.value = true
  } catch (err) {
    ElMessage.error(err?.message || t('railway.order.createFailed'))
  } finally {
    submitting.value = false
  }
}

// 支付成功后 PayPage 会发送 closeModal 消息；点击遮罩发送 closePay 消息
const onPayMessage = (event) => {
  if (!payVisible.value) return
  if (event.data?.action === 'closeModal') {
    closePay(true)
  } else if (event.data?.action === 'closePay') {
    closePay()
  }
}

// 关闭支付面板；paid 为 true 时支付成功，跳转我的车票
const closePay = (paid = false) => {
  payVisible.value = false
  createdOrder.value = null
  if (paid) {
    router.push('/ticket/my-orders')
  }
}
</script>

<style scoped>
.rw-page {
  padding: 30px 24px 0;
}

.rw-page-inner {
  max-width: 1080px;
  margin: 0 auto;
}

.rw-page-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.rw-page-title h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.01em;
  color: var(--rw-text);
}

.rw-back {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: var(--rw-text-2);
  text-decoration: none;
  transition: color 0.2s ease;
}

.rw-back:hover {
  color: var(--rw-blue);
}

.rw-order-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 20px;
  align-items: start;
}

.rw-order-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.rw-card {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 16px;
  padding: 22px;
}

.rw-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 18px;
}

.rw-card-head h2 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--rw-text);
  position: relative;
  padding-left: 11px;
}

.rw-card-head h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 2px;
  bottom: 2px;
  width: 3px;
  border-radius: 3px;
  background: var(--rw-blue);
}

.rw-card-tag {
  padding: 3px 10px;
  border-radius: 999px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  font-size: 12px;
  font-weight: 600;
}

.rw-card-hint {
  font-size: 12px;
  color: var(--rw-text-3);
}

/* 车次信息 */
.rw-train-info {
  display: flex;
  align-items: center;
  gap: 26px;
  padding: 18px;
  border-radius: 14px;
  background: var(--rw-soft-2);
}

.rw-train-info-no {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding-right: 24px;
  border-right: 1px solid var(--rw-border);
}

.rw-train-info-no strong {
  font-size: 22px;
  font-weight: 700;
  color: var(--rw-text);
}

.rw-train-info-no span {
  font-size: 11.5px;
  color: var(--rw-blue);
  letter-spacing: 0.06em;
}

.rw-train-info-route {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 14px;
}

.rw-train-info-end {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.rw-train-info-end--right {
  align-items: flex-end;
  text-align: right;
}

.rw-train-info-end .station {
  font-size: 16px;
  font-weight: 600;
  color: var(--rw-text);
}

.rw-train-info-end .time {
  font-size: 20px;
  font-weight: 700;
  color: var(--rw-text);
  font-variant-numeric: tabular-nums;
}

.rw-train-info-line {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--rw-blue);
}

.rw-train-info-line .dash {
  flex: 1;
  height: 1px;
  background: repeating-linear-gradient(90deg, var(--rw-border) 0 5px, transparent 5px 10px);
}

/* 乘车人 */
.rw-passenger-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rw-passenger {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 12px;
  border: 1px solid var(--rw-border);
  background: var(--rw-soft-2);
  cursor: pointer;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.rw-passenger.is-selected {
  border-color: var(--rw-blue);
  background: var(--rw-soft);
}

.rw-passenger :deep(.el-checkbox__label) {
  display: none;
}

.rw-passenger-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.rw-passenger-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.rw-passenger-name strong {
  font-size: 14.5px;
  color: var(--rw-text);
}

.rw-passenger-type {
  padding: 1px 7px;
  border-radius: 4px;
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  font-size: 10.5px;
  color: var(--rw-text-2);
}

.rw-passenger-id {
  font-size: 12px;
  color: var(--rw-text-3);
  letter-spacing: 0.04em;
}

.rw-passenger-del {
  border: none;
  background: none;
  color: var(--rw-text-3);
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  display: inline-flex;
}

.rw-passenger-del:hover {
  color: var(--rw-danger);
  background: rgba(239, 68, 68, 0.08);
}

.rw-passenger-add {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 10px;
  padding: 13px;
  border-radius: 12px;
  border: 1px dashed var(--rw-border);
  background: none;
  color: var(--rw-blue);
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
  transition: border-color 0.2s ease, background 0.2s ease;
}

.rw-passenger-add:hover {
  border-color: var(--rw-blue);
  background: var(--rw-soft);
}

.rw-empty-passenger {
  padding: 10px 0 18px;
  color: var(--rw-text-3);
  font-size: 13px;
}

.rw-empty-passenger p {
  margin: 0 0 14px;
}

.rw-phone-input {
  max-width: 360px;
}

.rw-phone-input :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: inset 0 0 0 1px var(--rw-border);
  background: var(--rw-soft-2);
}

/* 结算 */
.rw-summary-card {
  position: sticky;
  top: 138px;
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 16px;
  padding: 22px;
}

.rw-summary-card h2 {
  margin: 0 0 14px;
  font-size: 14px;
  font-weight: 600;
  color: var(--rw-text-2);
  letter-spacing: 0.06em;
}

.rw-summary-seat {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding-bottom: 16px;
  border-bottom: 1px dashed var(--rw-border);
}

.rw-summary-seat-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--rw-text);
}

.rw-summary-seat-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--rw-orange);
}

.rw-summary-list {
  margin: 14px 0 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rw-summary-list div {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.rw-summary-list dt {
  font-size: 13px;
  color: var(--rw-text-3);
}

.rw-summary-list dd {
  margin: 0;
  font-size: 13px;
  color: var(--rw-text-2);
}

.rw-summary-total {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--rw-border);
}

.rw-summary-total span {
  font-size: 13px;
  color: var(--rw-text-2);
}

.rw-summary-total strong {
  font-size: 26px;
  font-weight: 700;
  color: var(--rw-orange);
  font-variant-numeric: tabular-nums;
}

.rw-submit-order {
  width: 100%;
  margin-top: 18px;
  height: 46px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #ff9a2e, var(--rw-orange));
  color: #ffffff;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.12em;
  cursor: pointer;
  font-family: inherit;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 14px 26px -16px rgba(255, 138, 31, 0.9);
}

.rw-submit-order:hover:not(:disabled) {
  transform: translateY(-2px);
}

.rw-submit-order:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.rw-summary-note {
  margin: 12px 0 0;
  font-size: 11.5px;
  line-height: 1.6;
  color: var(--rw-text-3);
  text-align: center;
}

/* 状态 */
.rw-state {
  background: var(--rw-card);
  border: 1px solid var(--rw-border);
  border-radius: 18px;
  padding: 60px 24px;
  text-align: center;
  color: var(--rw-text-2);
}

.rw-state-icon {
  width: 56px;
  height: 56px;
  border-radius: 18px;
  background: var(--rw-soft);
  color: var(--rw-blue);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin-bottom: 14px;
}

.rw-state h3 {
  margin: 0 0 6px;
  font-size: 17px;
  color: var(--rw-text);
}

.rw-state p {
  margin: 0 0 18px;
  font-size: 13px;
}

.rw-state-btn {
  border: none;
  background: var(--rw-blue);
  color: #ffffff;
  padding: 9px 24px;
  border-radius: 999px;
  font-size: 13px;
  cursor: pointer;
  font-family: inherit;
}

/* 弹窗表单 */
.rw-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.rw-form-item {
  display: flex;
  flex-direction: column;
  gap: 7px;
  font-size: 13px;
  color: var(--rw-text-2);
}

.w-full {
  width: 100%;
}

/* 支付面板 */
.rw-pay-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(6px);
  -webkit-backdrop-filter: blur(6px);
}

.rw-pay-modal {
  position: relative;
  width: min(500px, 94vw);
  max-height: 90vh;
  overflow-y: auto;
  border-radius: 22px;
}

/* PayPage 默认以 fixed 居中，这里改为随弹层流式排布 */
.rw-pay-modal :deep(.glass-pay-container) {
  position: static;
  transform: none;
  min-height: auto;
  z-index: auto;
}

.rw-pay-close {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 10;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.06);
  color: var(--rw-text-2);
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.rw-pay-close:hover {
  background: rgba(0, 0, 0, 0.1);
  color: var(--rw-text);
}

@media (max-width: 900px) {
  .rw-order-layout {
    grid-template-columns: 1fr;
  }

  .rw-summary-card {
    position: static;
  }
}

@media (max-width: 640px) {
  .rw-page {
    padding: 16px 14px 0;
  }

  .rw-page-title h1 {
    font-size: 20px;
  }

  .rw-card {
    padding: 16px;
  }

  .rw-train-info {
    flex-direction: column;
    align-items: stretch;
    gap: 14px;
  }

  .rw-train-info-no {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding-right: 0;
    padding-bottom: 10px;
    border-right: none;
    border-bottom: 1px solid var(--rw-border);
  }

  .rw-train-info-route {
    gap: 8px;
  }
}
</style>
