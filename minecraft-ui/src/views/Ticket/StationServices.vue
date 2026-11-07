<template>
  <div class="rw-svc-page">
    <ServiceHero :eyebrow="$t('railway.stationPage.eyebrow')" :title="$t('railway.stationPage.title')"
      :desc="$t('railway.stationPage.desc')" :icon="Service" tone="deep">
      <div class="rw-svc-hero-badge">
        <strong>{{ services.length }}</strong>
        <span>{{ $t('railway.stationPage.catalogTitle') }}</span>
      </div>
    </ServiceHero>

    <div class="rw-svc-body">
      <!-- 服务项目 -->
      <section class="rw-svc-card">
        <div class="rw-svc-card-head">
          <div>
            <h2 class="rw-svc-card-title">{{ $t('railway.stationPage.catalogTitle') }}</h2>
            <p class="rw-svc-card-sub">{{ $t('railway.stationPage.catalogSub') }}</p>
          </div>
        </div>

        <div v-if="servicesLoading" class="rw-svc-loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          {{ $t('railway.common.loading') }}
        </div>

        <div v-else class="rw-svc-grid-cards">
          <button v-for="item in services" :key="item.id" type="button" class="rw-svc-service"
            :class="{ 'is-active': selectedServiceId === item.id }" @click="selectService(item)">
            <span class="rw-svc-service-icon">
              <el-icon><component :is="serviceIcon(item.icon)" /></el-icon>
            </span>
            <span class="rw-svc-service-main">
              <strong>{{ item.name }}</strong>
              <small>{{ item.description }}</small>
              <span class="rw-svc-service-price">
                {{ item.priceDesc }} · {{ $t(`railway.stationPage.category.${item.category}`) }}
              </span>
            </span>
          </button>
        </div>
      </section>

      <div class="rw-svc-layout" style="margin-top: 20px">
        <!-- 预约表单 -->
        <section class="rw-svc-card">
          <div class="rw-svc-card-head">
            <div>
              <h2 class="rw-svc-card-title">{{ $t('railway.stationPage.formTitle') }}</h2>
              <p class="rw-svc-card-sub">{{ $t('railway.stationPage.formSub') }}</p>
            </div>
          </div>

          <div v-if="selectedService" class="rw-svc-note">
            <strong>{{ selectedService.name }}</strong>
            <span> · {{ selectedService.priceDesc }}</span>
            <br />
            {{ selectedService.description }}
          </div>
          <div v-else class="rw-svc-note rw-svc-note--warm">
            {{ $t('railway.stationPage.selectService') }}
          </div>

          <div class="rw-svc-form" style="margin-top: 16px">
            <div class="rw-svc-field">
              <label>{{ $t('railway.stationPage.station') }}</label>
              <StationAutocomplete v-model="form.stationName"
                :placeholder="$t('railway.stationPage.station')" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.stationPage.date') }}</label>
              <el-date-picker v-model="form.serviceDate" type="date" value-format="YYYY-MM-DD"
                format="YYYY-MM-DD" :disabled-date="disabledServiceDate" :clearable="false"
                :placeholder="$t('railway.stationPage.date')" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.stationPage.count') }}</label>
              <el-input-number v-model="form.passengerCount" :min="1" :max="20" :step="1"
                controls-position="right" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.stationPage.contact') }}</label>
              <el-input v-model="form.contactName" :placeholder="$t('railway.stationPage.contact')"
                maxlength="50" />
            </div>
            <div class="rw-svc-field">
              <label>{{ $t('railway.stationPage.phone') }}</label>
              <el-input v-model="form.contactPhone" :placeholder="$t('railway.stationPage.phone')"
                maxlength="11" />
            </div>
            <div class="rw-svc-field rw-svc-field--full">
              <label>{{ $t('railway.stationPage.remark') }}</label>
              <el-input v-model="form.remark" type="textarea" :rows="3" maxlength="500" show-word-limit
                :placeholder="$t('railway.stationPage.remarkPlaceholder')" />
            </div>
          </div>

          <div class="rw-svc-actions">
            <button type="button" class="rw-svc-btn" :disabled="submitting" @click="submit">
              <el-icon><Promotion /></el-icon>
              {{ submitting ? $t('railway.stationPage.submitting') : $t('railway.stationPage.submit') }}
            </button>
          </div>
        </section>

        <!-- 我的预约 -->
        <section class="rw-svc-card">
          <div class="rw-svc-card-head">
            <h2 class="rw-svc-card-title">{{ $t('railway.stationPage.recordsTitle') }}</h2>
            <span v-if="loggedIn" class="rw-svc-card-head-right">{{ total }} 条</span>
          </div>

          <div v-if="!loggedIn" class="rw-svc-empty">
            <span class="rw-svc-empty-icon"><el-icon><User /></el-icon></span>
            <h4>{{ $t('railway.stationPage.loginHint') }}</h4>
            <p>{{ $t('railway.stationPage.formSub') }}</p>
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
            <h4>{{ $t('railway.stationPage.recordsEmpty') }}</h4>
            <p>{{ $t('railway.stationPage.recordsEmptyDesc') }}</p>
          </div>

          <template v-else>
            <div class="rw-svc-list">
              <article v-for="item in records" :key="item.id" class="rw-svc-item">
                <div class="rw-svc-item-head">
                  <span class="rw-svc-item-title">{{ item.serviceName }}</span>
                  <span class="rw-svc-status" :class="`rw-svc-status--${item.status}`">
                    {{ $t(`railway.stationPage.status.${item.status}`) }}
                  </span>
                </div>
                <div class="rw-svc-item-meta">
                  <span>
                    <el-icon><Position /></el-icon>
                    {{ item.stationName }}
                  </span>
                  <span>
                    <el-icon><Calendar /></el-icon>
                    {{ item.serviceDate }}
                  </span>
                  <span>
                    <el-icon><UserFilled /></el-icon>
                    {{ item.passengerCount }}
                  </span>
                  <span>{{ item.contactName }} {{ item.contactPhone }}</span>
                </div>
                <div v-if="item.remark" class="rw-svc-item-meta">
                  <span>{{ item.remark }}</span>
                </div>
                <div class="rw-svc-item-foot">
                  <span class="rw-svc-item-no">{{ item.requestNo }}</span>
                  <button v-if="item.status === 'PENDING' || item.status === 'ACCEPTED'" type="button"
                    class="rw-svc-btn rw-svc-btn--ghost rw-svc-btn--sm" @click="cancel(item)">
                    {{ $t('railway.stationPage.cancel') }}
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
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Box,
  Calendar,
  ChatDotRound,
  Coffee,
  FirstAidKit,
  KnifeFork,
  Loading,
  MapLocation,
  Position,
  Promotion,
  Search,
  Service,
  Tickets,
  User,
  UserFilled,
  Van,
} from '@element-plus/icons-vue'
import ServiceHero from './components/ServiceHero.vue'
import StationAutocomplete from './components/StationAutocomplete.vue'
import {
  cancelRailwayServiceRequest,
  createRailwayServiceRequest,
  getRailwayServiceRequests,
  getRailwayStationServices,
} from '@/api/railway'
import { hasLogin, queryString, todayString } from './utils'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()

const iconMap = {
  FirstAidKit,
  Search,
  Box,
  MapLocation,
  KnifeFork,
  Coffee,
  Van,
  ChatDotRound,
}

const services = ref([])
const servicesLoading = ref(false)
const selectedServiceId = ref(null)
const selectedService = ref(null)

const loggedIn = ref(hasLogin())
const submitting = ref(false)

const form = reactive({
  stationName: queryString(route.query.station, ''),
  serviceDate: todayString(),
  passengerCount: 1,
  contactName: '',
  contactPhone: '',
  remark: '',
})

const records = ref([])
const recordsLoading = ref(false)
const pageNum = ref(1)
const pageSize = 5
const total = ref(0)

const serviceIcon = (icon) => iconMap[icon] || Service

const disabledServiceDate = (date) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const max = new Date(today)
  max.setDate(max.getDate() + 30)
  return date.getTime() < today.getTime() || date.getTime() > max.getTime()
}

const selectService = (item) => {
  selectedServiceId.value = item.id
  selectedService.value = item
}

const loadServices = async () => {
  servicesLoading.value = true
  try {
    const res = await getRailwayStationServices()
    services.value = res?.data || []
    const code = queryString(route.query.service, '')
    if (code) {
      const matched = services.value.find((item) => item.serviceCode === code)
      if (matched) selectService(matched)
    }
  } catch (err) {
    ElMessage.error(err?.message || t('railway.stationPage.loadFailed'))
  } finally {
    servicesLoading.value = false
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
    const res = await getRailwayServiceRequests({ pageNum: pageNum.value, pageSize })
    records.value = res?.data?.records || []
    total.value = Number(res?.data?.total || 0)
  } catch {
    records.value = []
    total.value = 0
  } finally {
    recordsLoading.value = false
  }
}

const submit = async () => {
  if (!selectedServiceId.value) {
    ElMessage.warning(t('railway.stationPage.selectService'))
    return
  }
  if (!form.stationName?.trim()) {
    ElMessage.warning(t('railway.stationPage.station'))
    return
  }
  if (!form.serviceDate) {
    ElMessage.warning(t('railway.stationPage.date'))
    return
  }
  if (!form.contactName?.trim()) {
    ElMessage.warning(t('railway.stationPage.contact'))
    return
  }
  if (!/^1[3-9]\d{9}$/.test(form.contactPhone || '')) {
    ElMessage.warning(t('railway.stationPage.phone'))
    return
  }
  if (!hasLogin()) {
    ElMessage.warning(t('railway.stationPage.needLogin'))
    goLogin()
    return
  }
  submitting.value = true
  try {
    await createRailwayServiceRequest({
      serviceId: selectedServiceId.value,
      stationName: form.stationName.trim(),
      serviceDate: form.serviceDate,
      passengerCount: form.passengerCount,
      contactName: form.contactName.trim(),
      contactPhone: form.contactPhone.trim(),
      remark: form.remark?.trim() || null,
    })
    ElMessage.success(t('railway.stationPage.submitSuccess'))
    form.remark = ''
    pageNum.value = 1
    await loadRecords()
  } catch (err) {
    ElMessage.error(err?.message || t('railway.stationPage.loadFailed'))
  } finally {
    submitting.value = false
  }
}

const handlePage = (value) => {
  pageNum.value = value
  loadRecords()
}

const cancel = async (item) => {
  try {
    await ElMessageBox.confirm(t('railway.stationPage.cancelConfirm'), t('railway.common.confirm'), {
      type: 'warning',
    })
    await cancelRailwayServiceRequest(item.id)
    ElMessage.success(t('railway.stationPage.cancelSuccess'))
    await loadRecords()
  } catch (err) {
    if (err !== 'cancel' && err !== 'close') {
      ElMessage.error(err?.message || t('railway.stationPage.loadFailed'))
    }
  }
}

const goLogin = () => router.push({ path: '/login', query: { redirect: route.fullPath } })

onMounted(() => {
  loadServices()
  loadRecords()
})
</script>
