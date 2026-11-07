<template>
  <div class="rw-svc-page">
    <ServiceHero :eyebrow="$t('railway.groupPage.eyebrow')" :title="$t('railway.groupPage.title')"
      :desc="$t('railway.groupPage.desc')" :icon="ShoppingCartFull" tone="warm">
      <div class="rw-svc-hero-badge">
        <strong>20–500</strong>
        <span>{{ $t('railway.groupPage.badgeMax') }}</span>
      </div>
    </ServiceHero>

    <div class="rw-svc-body">
      <div class="rw-svc-layout">
        <div class="rw-svc-stack">
          <!-- 团购需求 -->
          <section class="rw-svc-card">
            <div class="rw-svc-card-head">
              <div>
                <h2 class="rw-svc-card-title">{{ $t('railway.groupPage.formTitle') }}</h2>
                <p class="rw-svc-card-sub">{{ $t('railway.groupPage.formSub') }}</p>
              </div>
            </div>

            <div class="rw-svc-form">
              <div class="rw-svc-field rw-svc-field--full">
                <label>{{ $t('railway.groupPage.company') }}</label>
                <el-input v-model="form.companyName" :placeholder="$t('railway.groupPage.companyPlaceholder')"
                  maxlength="100" />
              </div>

              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.contact') }}</label>
                <el-input v-model="form.contactName" :placeholder="$t('railway.groupPage.contactPlaceholder')"
                  maxlength="50" />
              </div>
              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.phone') }}</label>
                <el-input v-model="form.contactPhone" :placeholder="$t('railway.groupPage.phonePlaceholder')"
                  maxlength="11" />
              </div>

              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.from') }}</label>
                <StationAutocomplete v-model="form.fromStation"
                  :placeholder="$t('railway.search.placeholderFrom')" />
              </div>
              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.to') }}</label>
                <StationAutocomplete v-model="form.toStation" :placeholder="$t('railway.search.placeholderTo')" />
              </div>

              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.date') }}</label>
                <el-date-picker v-model="form.travelDate" type="date" value-format="YYYY-MM-DD"
                  format="YYYY-MM-DD" :disabled-date="disabledTravelDate" :clearable="false"
                  :placeholder="$t('railway.groupPage.date')" />
              </div>
              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.returnDate') }}</label>
                <el-date-picker v-model="form.returnDate" type="date" value-format="YYYY-MM-DD"
                  format="YYYY-MM-DD" :disabled-date="disabledTravelDate" clearable
                  :placeholder="$t('railway.groupPage.returnDate')" />
              </div>

              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.count') }}</label>
                <el-input-number v-model="form.passengerCount" :min="20" :max="500" :step="1"
                  controls-position="right" />
              </div>
              <div class="rw-svc-field">
                <label>{{ $t('railway.groupPage.seatType') }}</label>
                <el-select v-model="form.seatType">
                  <el-option v-for="seat in seatOptions" :key="seat" :label="$t(`railway.list.seat.${seat}`)"
                    :value="seat" />
                </el-select>
              </div>

              <div class="rw-svc-field rw-svc-field--full">
                <label>{{ $t('railway.groupPage.trainNo') }}</label>
                <el-input v-model="form.trainNo" :placeholder="$t('railway.groupPage.trainNoPlaceholder')"
                  maxlength="20" />
              </div>

              <div class="rw-svc-field rw-svc-field--full">
                <label>{{ $t('railway.groupPage.remark') }}</label>
                <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="500" show-word-limit
                  :placeholder="$t('railway.groupPage.remarkPlaceholder')" />
              </div>
            </div>

            <div class="rw-svc-actions">
              <button type="button" class="rw-svc-btn rw-svc-btn--ghost" :disabled="quoting" @click="fetchQuote">
                <el-icon><Money /></el-icon>
                {{ quoting ? $t('railway.groupPage.quoting') : $t('railway.groupPage.quote') }}
              </button>
              <button type="button" class="rw-svc-btn" :disabled="submitting" @click="submit">
                <el-icon><Promotion /></el-icon>
                {{ submitting ? $t('railway.groupPage.submitting') : $t('railway.groupPage.submit') }}
              </button>
            </div>
          </section>

          <!-- 我的团购申请 -->
          <section class="rw-svc-card">
            <div class="rw-svc-card-head">
              <h2 class="rw-svc-card-title">{{ $t('railway.groupPage.recordsTitle') }}</h2>
              <span v-if="loggedIn" class="rw-svc-card-head-right">{{ total }} 条</span>
            </div>

            <div v-if="!loggedIn" class="rw-svc-empty">
              <span class="rw-svc-empty-icon"><el-icon><User /></el-icon></span>
              <h4>{{ $t('railway.groupPage.loginHint') }}</h4>
              <p>{{ $t('railway.groupPage.rule3') }}</p>
              <div class="rw-svc-actions" style="justify-content: center">
                <button type="button" class="rw-svc-btn rw-svc-btn--ghost rw-svc-btn--sm" @click="goLogin">
                  {{ $t('railway.top.login') }}
                </button>
              </div>
            </div>

            <div v-else-if="recordsLoading" class="rw-svc-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              {{ $t('railway.common.loading') }}
            </div>

            <div v-else-if="records.length === 0" class="rw-svc-empty">
              <span class="rw-svc-empty-icon"><el-icon><Tickets /></el-icon></span>
              <h4>{{ $t('railway.groupPage.recordsEmpty') }}</h4>
              <p>{{ $t('railway.groupPage.recordsEmptyDesc') }}</p>
            </div>

            <template v-else>
              <div class="rw-svc-list">
                <article v-for="item in records" :key="item.id" class="rw-svc-item">
                  <div class="rw-svc-item-head">
                    <span class="rw-svc-item-title">{{ item.companyName }}</span>
                    <span class="rw-svc-status" :class="`rw-svc-status--${item.status}`">
                      {{ $t(`railway.groupPage.status.${item.status}`) }}
                    </span>
                  </div>
                  <div class="rw-svc-item-meta">
                    <span>
                      <el-icon><Position /></el-icon>
                      {{ item.fromStation }} → {{ item.toStation }}
                    </span>
                    <span>
                      <el-icon><Calendar /></el-icon>
                      {{ item.travelDate }}
                    </span>
                    <span>
                      <el-icon><UserFilled /></el-icon>
                      {{ $t('railway.groupPage.passengers', { n: item.passengerCount }) }}
                    </span>
                    <span>{{ $t(`railway.list.seat.${item.seatType}`) }}</span>
                    <span class="rw-svc-item-no">{{ item.bookingNo }}</span>
                  </div>
                  <div class="rw-svc-item-foot">
                    <span class="rw-svc-amount">
                      <small>{{ $t('railway.groupPage.quoteTotal') }}</small>
                      {{ item.expectedAmountCents > 0 ? `¥${formatPrice(item.expectedAmountCents)}` : '—' }}
                    </span>
                    <button v-if="item.status === 'PENDING'" type="button"
                      class="rw-svc-btn rw-svc-btn--ghost rw-svc-btn--sm" @click="cancel(item)">
                      {{ $t('railway.groupPage.cancel') }}
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

        <!-- 右侧：报价 + 须知 -->
        <aside class="rw-svc-stack">
          <section class="rw-svc-card">
            <div class="rw-svc-card-head">
              <h2 class="rw-svc-card-title">{{ $t('railway.groupPage.quoteTitle') }}</h2>
            </div>

            <div v-if="quote" class="rw-svc-quote">
              <div class="rw-svc-quote-head">
                <el-icon><InfoFilled /></el-icon>
                {{ quote.matched
                  ? $t('railway.groupPage.quoteMatched', { train: quote.trainNo })
                  : $t('railway.groupPage.quoteUnmatched') }}
              </div>
              <div class="rw-svc-quote-grid">
                <div>
                  <small>{{ $t('railway.groupPage.quoteUnit') }}</small>
                  <strong>{{ quote.unitPriceCents > 0 ? `¥${formatPrice(quote.unitPriceCents)}` : '—' }}</strong>
                </div>
                <div>
                  <small>{{ $t('railway.groupPage.quoteDiscount') }}</small>
                  <strong>{{ discountText }}</strong>
                </div>
                <div>
                  <small>{{ $t('railway.groupPage.quoteTotal') }}</small>
                  <strong class="is-highlight">
                    {{ quote.totalAmountCents > 0 ? `¥${formatPrice(quote.totalAmountCents)}` : '—' }}
                  </strong>
                </div>
              </div>
            </div>

            <div v-else class="rw-svc-note">
              {{ $t('railway.groupPage.quoteMatched', { train: '—' }) }}
              <br />
              {{ $t('railway.groupPage.discountHint') }}
            </div>
          </section>

          <section class="rw-svc-card">
            <div class="rw-svc-card-head">
              <h2 class="rw-svc-card-title">{{ $t('railway.groupPage.rulesTitle') }}</h2>
            </div>
            <ul class="rw-svc-rule-list">
              <li>{{ $t('railway.groupPage.rule1') }}</li>
              <li>{{ $t('railway.groupPage.rule2') }}</li>
              <li>{{ $t('railway.groupPage.rule3') }}</li>
              <li>{{ $t('railway.groupPage.rule4') }}</li>
            </ul>
          </section>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar,
  InfoFilled,
  Loading,
  Money,
  Position,
  Promotion,
  ShoppingCartFull,
  Tickets,
  User,
  UserFilled,
} from '@element-plus/icons-vue'
import ServiceHero from './components/ServiceHero.vue'
import StationAutocomplete from './components/StationAutocomplete.vue'
import {
  cancelRailwayGroupBooking,
  createRailwayGroupBooking,
  getRailwayGroupBookings,
  quoteRailwayGroup,
} from '@/api/railway'
import { formatPrice, hasLogin, offsetDate } from './utils'

const router = useRouter()
const route = useRoute()
const { t, locale } = useI18n()

const seatOptions = ['SECOND_CLASS', 'FIRST_CLASS', 'SLEEPER']

const loggedIn = ref(hasLogin())

const form = reactive({
  companyName: '',
  contactName: '',
  contactPhone: '',
  fromStation: '',
  toStation: '',
  travelDate: offsetDate(3),
  returnDate: '',
  passengerCount: 20,
  seatType: 'SECOND_CLASS',
  trainNo: '',
  remark: '',
})

const quote = ref(null)
const quoting = ref(false)
const submitting = ref(false)

const records = ref([])
const recordsLoading = ref(false)
const pageNum = ref(1)
const pageSize = 5
const total = ref(0)

const discountText = computed(() => {
  const rate = Number(quote.value?.discountRate || 1)
  if (locale.value === 'en-US') {
    return t('railway.memberPage.discountValue', { n: Math.round((1 - rate) * 100) })
  }
  return `${(rate * 10).toFixed(1).replace(/\.0$/, '')} 折`
})

const disabledTravelDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const max = new Date(today)
  max.setDate(max.getDate() + 60)
  return date.getTime() < today.getTime() || date.getTime() > max.getTime()
}

const validateTrip = () => {
  if (!form.fromStation?.trim()) {
    ElMessage.warning(t('railway.search.needFrom'))
    return false
  }
  if (!form.toStation?.trim()) {
    ElMessage.warning(t('railway.search.needTo'))
    return false
  }
  if (form.fromStation.trim() === form.toStation.trim()) {
    ElMessage.warning(t('railway.search.sameCity'))
    return false
  }
  if (!form.travelDate) {
    ElMessage.warning(t('railway.search.needDate'))
    return false
  }
  if (form.passengerCount < 20 || form.passengerCount > 500) {
    ElMessage.warning(t('railway.groupPage.countRange'))
    return false
  }
  if (form.returnDate && form.returnDate < form.travelDate) {
    ElMessage.warning(t('railway.groupPage.returnDate'))
    return false
  }
  return true
}

const validateFull = () => {
  if (!form.companyName?.trim()) {
    ElMessage.warning(t('railway.groupPage.company'))
    return false
  }
  if (!form.contactName?.trim()) {
    ElMessage.warning(t('railway.groupPage.contact'))
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(form.contactPhone || '')) {
    ElMessage.warning(t('railway.groupPage.phonePlaceholder'))
    return false
  }
  return validateTrip()
}

const fetchQuote = async () => {
  if (!validateTrip()) return
  quoting.value = true
  try {
    const res = await quoteRailwayGroup({
      fromStation: form.fromStation.trim(),
      toStation: form.toStation.trim(),
      travelDate: form.travelDate,
      passengerCount: form.passengerCount,
      seatType: form.seatType,
      trainNo: form.trainNo?.trim() || undefined,
    })
    quote.value = res?.data || null
  } catch (err) {
    ElMessage.error(err?.message || t('railway.groupPage.loadFailed'))
  } finally {
    quoting.value = false
  }
}

const submit = async () => {
  if (!validateFull()) return
  if (!hasLogin()) {
    ElMessage.warning(t('railway.groupPage.needLogin'))
    goLogin()
    return
  }
  submitting.value = true
  try {
    await createRailwayGroupBooking({
      companyName: form.companyName.trim(),
      contactName: form.contactName.trim(),
      contactPhone: form.contactPhone.trim(),
      fromStation: form.fromStation.trim(),
      toStation: form.toStation.trim(),
      travelDate: form.travelDate,
      returnDate: form.returnDate || null,
      passengerCount: form.passengerCount,
      seatType: form.seatType,
      trainNo: form.trainNo?.trim() || null,
      remark: form.remark?.trim() || null,
    })
    ElMessage.success(t('railway.groupPage.submitSuccess'))
    quote.value = null
    pageNum.value = 1
    await loadRecords()
  } catch (err) {
    ElMessage.error(err?.message || t('railway.groupPage.loadFailed'))
  } finally {
    submitting.value = false
  }
}

const loadRecords = async () => {
  if (!hasLogin()) {
    loggedIn.value = false
    return
  }
  loggedIn.value = true
  recordsLoading.value = true
  try {
    const res = await getRailwayGroupBookings({ pageNum: pageNum.value, pageSize })
    records.value = res?.data?.records || []
    total.value = Number(res?.data?.total || 0)
  } catch {
    records.value = []
    total.value = 0
  } finally {
    recordsLoading.value = false
  }
}

const handlePage = (value) => {
  pageNum.value = value
  loadRecords()
}

const cancel = async (item) => {
  try {
    await ElMessageBox.confirm(t('railway.groupPage.cancelConfirm'), t('railway.common.confirm'), {
      type: 'warning',
    })
    await cancelRailwayGroupBooking(item.id)
    ElMessage.success(t('railway.groupPage.cancelSuccess'))
    await loadRecords()
  } catch (err) {
    if (err !== 'cancel' && err !== 'close') {
      ElMessage.error(err?.message || t('railway.groupPage.loadFailed'))
    }
  }
}

const goLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } })

onMounted(loadRecords)
</script>
