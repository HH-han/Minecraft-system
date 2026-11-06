<template>
  <div class="services-pane">
    <!-- 顶部导航 -->
    <header class="svc-head">
      <button class="svc-back" :title="$t('imServices.title')" @click="$emit('close')">
        <Icon name="arrow-left" :size="'19px'" />
      </button>
      <h2 class="svc-title">{{ $t('imServices.title') }}</h2>
    </header>

    <div class="svc-body">
      <div class="svc-content">
        <!-- 钱包卡片 -->
        <section class="svc-wallet">
          <div class="svc-wallet-top">
            <span class="svc-wallet-label">{{ $t('imServices.wallet.balance') }}</span>
            <div class="svc-wallet-amount">
              <span class="svc-currency">¥</span>
              <span class="svc-amount">{{ formatMoney(wallet.balance) }}</span>
            </div>
          </div>

          <div class="svc-wallet-stats">
            <div class="svc-stat">
              <span class="svc-stat-value">{{ formatMoney(wallet.totalRecharge) }}</span>
              <span class="svc-stat-label">{{ $t('imServices.wallet.totalRecharge') }}</span>
            </div>
            <div class="svc-stat-divider"></div>
            <div class="svc-stat">
              <span class="svc-stat-value">{{ formatMoney(wallet.totalExpense) }}</span>
              <span class="svc-stat-value-label">{{ $t('imServices.wallet.totalExpense') }}</span>
            </div>
          </div>

          <div class="svc-wallet-actions">
            <button class="svc-action-btn" @click="openDialog('recharge')">
              {{ $t('imServices.wallet.recharge') }}
            </button>
            <button class="svc-action-btn" @click="openDialog('withdraw')">
              {{ $t('imServices.wallet.withdraw') }}
            </button>
            <button class="svc-action-btn" @click="openDialog('transfer')">
              {{ $t('imServices.wallet.transfer') }}
            </button>
          </div>

          <div v-if="!wallet.hasPayPassword" class="svc-pwd-tip">
            <Icon name="info" :size="'14px'" />
            <span class="svc-pwd-tip-text">{{ $t('imServices.wallet.needPasswordTip') }}</span>
            <button class="svc-pwd-set" @click="openDialog('password')">
              {{ $t('imServices.wallet.setNow') }}
            </button>
          </div>
          <button v-else class="svc-pwd-modify" @click="openDialog('password')">
            {{ $t('imServices.wallet.setPayPassword') }}
          </button>
        </section>

        <!-- 服务宫格 -->
        <section v-for="cat in categoryList" :key="cat.key" class="svc-group">
          <h3 class="svc-group-title">{{ $t('imServices.category.' + cat.key) }}</h3>
          <div class="svc-grid">
            <button
              v-for="item in cat.items"
              :key="item.id"
              class="svc-grid-item"
              @click="handleServiceClick(item)"
            >
              <span class="svc-grid-icon">
                <Icon :name="item.icon" :size="'22px'" />
              </span>
              <span class="svc-grid-name">{{ itemName(item) }}</span>
            </button>
          </div>
        </section>

        <!-- 零钱明细 -->
        <section class="svc-group">
          <h3 class="svc-group-title">{{ $t('imServices.wallet.transactions') }}</h3>
          <div class="svc-tx-card">
            <div v-if="txLoading && !transactions.length" class="svc-state">
              {{ $t('imServices.wallet.loadMore') }}…
            </div>
            <div v-else-if="!transactions.length" class="svc-state">
              {{ $t('imServices.wallet.noTransactions') }}
            </div>
            <template v-else>
              <div v-for="tx in transactions" :key="tx.id" class="svc-tx">
                <span class="svc-tx-icon" :class="amountClass(tx)">
                  <Icon :name="txIcon(tx)" :size="'16px'" />
                </span>
                <div class="svc-tx-main">
                  <div class="svc-tx-line">
                    <span class="svc-tx-type">{{ txTypeLabel(tx) }}</span>
                    <span class="svc-tx-amount" :class="amountClass(tx)">
                      {{ amountPrefix(tx) }}{{ formatMoney(tx.amount) }}
                    </span>
                  </div>
                  <div class="svc-tx-line svc-tx-sub">
                    <span class="svc-tx-remark">{{ tx.remark || txTypeLabel(tx) }}</span>
                    <span class="svc-tx-time">{{ tx.createTime }}</span>
                  </div>
                </div>
              </div>
              <div class="svc-tx-more">
                <button v-if="txHasMore" class="svc-more-btn" :disabled="txLoading" @click="loadMoreTx">
                  {{ $t('imServices.wallet.loadMore') }}
                </button>
                <span v-else class="svc-more-end">— {{ $t('imServices.wallet.noMore') }} —</span>
              </div>
            </template>
          </div>
        </section>
      </div>
    </div>

    <!-- 钱包操作弹窗 -->
    <transition name="fade">
      <div v-if="dialog.visible" class="svc-overlay" @click.self="dialog.visible = false">
        <div class="svc-window">
          <header class="svc-window-head">
            <button class="svc-window-cancel" @click="dialog.visible = false">
              {{ $t('imServices.wallet.cancel') }}
            </button>
            <h3>{{ dialogTitle }}</h3>
            <button class="svc-window-submit" :disabled="submitting" @click="submitDialog">
              {{ $t('imServices.wallet.confirm') }}
            </button>
          </header>
          <div class="svc-window-body">
            <template v-if="dialog.type !== 'password'">
              <label class="svc-field-label">{{ $t('imServices.wallet.amount') }}</label>
              <div class="svc-amount-field">
                <span class="svc-amount-prefix">¥</span>
                <input
                  v-model="dialog.amount"
                  class="svc-field svc-amount-input"
                  type="number"
                  min="0.01"
                  step="0.01"
                  :placeholder="$t('imServices.wallet.amountPlaceholder')"
                />
              </div>
            </template>

            <template v-if="dialog.type === 'transfer'">
              <label class="svc-field-label">{{ $t('imServices.wallet.transferTarget') }}</label>
              <input
                v-model="dialog.toUserId"
                class="svc-field"
                type="number"
                :placeholder="$t('imServices.wallet.transferTargetPlaceholder')"
              />
              <label class="svc-field-label">{{ $t('imServices.wallet.transferRemark') }}</label>
              <input
                v-model="dialog.remark"
                class="svc-field"
                type="text"
                maxlength="100"
                :placeholder="$t('imServices.wallet.transferRemarkPlaceholder')"
              />
            </template>

            <template v-if="dialog.type === 'password' && wallet.hasPayPassword">
              <label class="svc-field-label">{{ $t('imServices.wallet.oldPayPassword') }}</label>
              <input
                v-model="dialog.oldPayPassword"
                class="svc-field"
                type="password"
                maxlength="6"
                inputmode="numeric"
                :placeholder="$t('imServices.wallet.oldPayPasswordPlaceholder')"
              />
              <label class="svc-field-label">{{ $t('imServices.wallet.newPayPassword') }}</label>
              <input
                v-model="dialog.payPassword"
                class="svc-field"
                type="password"
                maxlength="6"
                inputmode="numeric"
                :placeholder="$t('imServices.wallet.newPayPasswordPlaceholder')"
              />
            </template>
            <template v-else-if="dialog.type !== 'recharge'">
              <label class="svc-field-label">{{ $t('imServices.wallet.payPassword') }}</label>
              <input
                v-model="dialog.payPassword"
                class="svc-field"
                type="password"
                maxlength="6"
                inputmode="numeric"
                :placeholder="$t('imServices.wallet.payPasswordPlaceholder')"
              />
            </template>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useI18n } from 'vue-i18n'
import Icon from './Icon.vue'
import {
  getServiceItems,
  getWalletInfo,
  getWalletTransactions,
  rechargeWallet,
  withdrawWallet,
  transferWallet,
  setPayPassword
} from '@/api/service'

const props = defineProps({
  currentUserId: { type: [Number, String], required: true }
})
defineEmits(['close'])

const { t, locale } = useI18n()
const router = useRouter()

const wallet = ref({ balance: 0, totalRecharge: 0, totalExpense: 0, hasPayPassword: false })
const serviceItems = ref([])
const transactions = ref([])
const txPage = ref(1)
const txSize = 20
const txHasMore = ref(false)
const txLoading = ref(false)

const dialog = ref({
  visible: false,
  type: '', // recharge | withdraw | transfer | password
  amount: '',
  toUserId: '',
  remark: '',
  payPassword: '',
  oldPayPassword: ''
})
const submitting = ref(false)

const CATEGORY_ORDER = ['finance', 'life', 'travel', 'shopping']

const categoryList = computed(() => {
  return CATEGORY_ORDER
    .map(key => ({ key, items: serviceItems.value.filter(i => i.category === key) }))
    .filter(c => c.items.length)
})

const dialogTitle = computed(() => {
  const map = {
    recharge: 'imServices.wallet.rechargeTitle',
    withdraw: 'imServices.wallet.withdrawTitle',
    transfer: 'imServices.wallet.transferTitle',
    password: 'imServices.wallet.setPayPassword'
  }
  return t(map[dialog.value.type] || '')
})

const itemName = (item) => (locale.value === 'en-US' && item.nameEn ? item.nameEn : item.name)

const formatMoney = (val) => Number(val || 0).toFixed(2)

const isIncome = (tx) => tx.type === 'recharge' || tx.type === 'transfer_in'

const txTypeLabel = (tx) => {
  const map = {
    recharge: 'imServices.wallet.typeRecharge',
    withdraw: 'imServices.wallet.typeWithdraw',
    transfer_out: 'imServices.wallet.typeTransferOut',
    transfer_in: 'imServices.wallet.typeTransferIn'
  }
  return t(map[tx.type] || '', tx.type)
}

const txIcon = (tx) => {
  const map = {
    recharge: 'plus',
    withdraw: 'upload',
    transfer_out: 'send',
    transfer_in: 'check'
  }
  return map[tx.type] || 'tag'
}

const amountPrefix = (tx) => (isIncome(tx) ? '+' : '-')
const amountClass = (tx) => (isIncome(tx) ? 'income' : 'expense')

const loadWallet = async () => {
  try {
    const res = await getWalletInfo(props.currentUserId)
    if (res.code === 200) wallet.value = res.data || {}
  } catch (e) {
    ElMessage.error(t('imServices.loadFailed'))
  }
}

const loadServices = async () => {
  try {
    const res = await getServiceItems()
    if (res.code === 200) serviceItems.value = res.data || []
  } catch (e) {
    ElMessage.error(t('imServices.loadFailed'))
  }
}

const loadTransactions = async (append = false) => {
  txLoading.value = true
  try {
    const res = await getWalletTransactions({
      userId: props.currentUserId,
      page: txPage.value,
      size: txSize
    })
    if (res.code === 200) {
      const list = res.data || []
      transactions.value = append ? [...transactions.value, ...list] : list
      txHasMore.value = list.length >= txSize
    }
  } catch (e) {
    ElMessage.error(t('imServices.loadFailed'))
  } finally {
    txLoading.value = false
  }
}

const loadMoreTx = () => {
  txPage.value += 1
  loadTransactions(true)
}

const handleServiceClick = (item) => {
  if (item.linkType === 'route' && item.link) {
    router.push(item.link)
  } else if (item.linkType === 'url' && item.link) {
    window.open(item.link, '_blank')
  } else {
    ElMessage.info(t('imServices.comingSoon'))
  }
}

const openDialog = (type) => {
  dialog.value = {
    visible: true,
    type,
    amount: '',
    toUserId: '',
    remark: '',
    payPassword: '',
    oldPayPassword: ''
  }
}

const validAmount = () => {
  const n = Number(dialog.value.amount)
  return Number.isFinite(n) && n >= 0.01
}

const submitDialog = async () => {
  if (submitting.value) return
  const d = dialog.value
  const userId = props.currentUserId

  if (d.type !== 'password' && !validAmount()) {
    ElMessage.warning(t('imServices.wallet.amountPlaceholder'))
    return
  }
  if (d.type === 'transfer' && !d.toUserId) {
    ElMessage.warning(t('imServices.wallet.transferTargetPlaceholder'))
    return
  }
  if (d.type !== 'recharge' && !/^\d{6}$/.test(d.payPassword)) {
    ElMessage.warning(t('imServices.wallet.payPasswordPlaceholder'))
    return
  }
  if (d.type === 'password' && wallet.value.hasPayPassword && !/^\d{6}$/.test(d.oldPayPassword)) {
    ElMessage.warning(t('imServices.wallet.oldPayPasswordPlaceholder'))
    return
  }

  submitting.value = true
  try {
    let res
    const amount = Number(d.amount).toFixed(2)
    if (d.type === 'recharge') {
      res = await rechargeWallet({ userId, amount })
    } else if (d.type === 'withdraw') {
      res = await withdrawWallet({ userId, amount, payPassword: d.payPassword })
    } else if (d.type === 'transfer') {
      res = await transferWallet({
        userId,
        toUserId: Number(d.toUserId),
        amount,
        remark: d.remark,
        payPassword: d.payPassword
      })
    } else {
      res = await setPayPassword({
        userId,
        payPassword: d.payPassword,
        oldPayPassword: wallet.value.hasPayPassword ? d.oldPayPassword : undefined
      })
    }

    if (res.code === 200) {
      const msgMap = {
        recharge: 'imServices.wallet.rechargeSuccess',
        withdraw: 'imServices.wallet.withdrawSuccess',
        transfer: 'imServices.wallet.transferSuccess',
        password: 'imServices.wallet.setPasswordSuccess'
      }
      ElMessage.success(t(msgMap[d.type]))
      d.visible = false
      await loadWallet()
      txPage.value = 1
      await loadTransactions()
    } else {
      ElMessage.error(res.message || t('imServices.loadFailed'))
    }
  } catch (e) {
    ElMessage.error(e?.message || t('imServices.loadFailed'))
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadWallet()
  loadServices()
  loadTransactions()
})
</script>

<style scoped>
.services-pane {
  flex: 1;
  min-width: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-surface);
  overflow: hidden;
  position: relative;
}

/* ===== 顶部导航 ===== */
.svc-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--theme-hairline);
  flex-shrink: 0;
}

.svc-back {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: var(--theme-text-1);
  cursor: pointer;
  border-radius: 8px;
}
.svc-back:hover {
  background: var(--theme-bg-hover);
}

.svc-title {
  font-size: 17px;
  font-weight: 600;
  margin: 0;
}

/* ===== 内容区 ===== */
.svc-body {
  flex: 1;
  overflow-y: auto;
  background: var(--theme-bg-app, var(--theme-bg-surface));
}

.svc-content {
  max-width: 680px;
  margin: 0 auto;
  padding: 20px 20px 32px;
}

/* ===== 钱包卡片 ===== */
.svc-wallet {
  position: relative;
  border-radius: 16px;
  padding: 24px 24px 20px;
  margin-bottom: 20px;
  color: var(--theme-primary-text, #fff);
  background: linear-gradient(135deg, var(--theme-primary) 0%, var(--theme-primary-hover, var(--theme-primary)) 100%);
  box-shadow: var(--theme-shadow-sm);
  overflow: hidden;
}

.svc-wallet-top {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.svc-wallet-label {
  font-size: 13px;
  opacity: 0.85;
}

.svc-wallet-amount {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.svc-currency {
  font-size: 20px;
  font-weight: 600;
}

.svc-amount {
  font-size: 38px;
  font-weight: 700;
  letter-spacing: 0.5px;
  line-height: 1.1;
}

.svc-wallet-stats {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.25);
}

.svc-stat {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.svc-stat-value {
  font-size: 15px;
  font-weight: 600;
}

.svc-stat-label,
.svc-stat-value-label {
  font-size: 11px;
  opacity: 0.8;
}

.svc-stat-divider {
  width: 1px;
  height: 26px;
  background: rgba(255, 255, 255, 0.25);
}

.svc-wallet-actions {
  display: flex;
  gap: 10px;
  margin-top: 18px;
}

.svc-action-btn {
  flex: 1;
  padding: 9px 0;
  border: 1px solid rgba(255, 255, 255, 0.6);
  background: rgba(255, 255, 255, 0.14);
  color: inherit;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.svc-action-btn:hover {
  background: rgba(255, 255, 255, 0.28);
}

.svc-pwd-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 14px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.14);
  border-radius: 8px;
  font-size: 12px;
}

.svc-pwd-tip-text {
  flex: 1;
  opacity: 0.9;
}

.svc-pwd-set {
  border: none;
  background: #fff;
  color: var(--theme-primary);
  font-size: 12px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 12px;
  cursor: pointer;
}

.svc-pwd-modify {
  margin-top: 12px;
  border: none;
  background: transparent;
  color: inherit;
  opacity: 0.8;
  font-size: 12px;
  cursor: pointer;
  width: 100%;
  text-align: center;
}
.svc-pwd-modify:hover {
  opacity: 1;
  text-decoration: underline;
}

/* ===== 服务分组 ===== */
.svc-group {
  margin-bottom: 20px;
}

.svc-group-title {
  font-size: 13px;
  font-weight: 500;
  color: var(--theme-text-3);
  margin: 0 0 8px 6px;
}

.svc-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  border-radius: 14px;
  overflow: hidden;
}

.svc-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 18px 4px 14px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: var(--theme-text-1);
  transition: background 0.2s;
}
.svc-grid-item:hover {
  background: var(--theme-bg-hover);
}
.svc-grid-item:hover .svc-grid-icon {
  transform: translateY(-2px);
}

.svc-grid-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 12px;
  background: var(--theme-primary-soft, var(--theme-bg-hover));
  color: var(--theme-primary);
  transition: transform 0.2s;
}

.svc-grid-name {
  font-size: 12px;
  color: var(--theme-text-2);
  line-height: 1.25;
  text-align: center;
  word-break: break-all;
}

/* ===== 零钱明细 ===== */
.svc-tx-card {
  background: var(--theme-bg-surface);
  border: 1px solid var(--theme-hairline);
  border-radius: 14px;
  overflow: hidden;
}

.svc-state {
  padding: 28px 0;
  text-align: center;
  font-size: 13px;
  color: var(--theme-text-3);
}

.svc-tx {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 16px;
}
.svc-tx + .svc-tx {
  border-top: 1px solid var(--theme-hairline);
}

.svc-tx-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
  background: var(--theme-bg-hover);
  color: var(--theme-text-2);
}
.svc-tx-icon.income {
  background: var(--theme-primary-soft, var(--theme-bg-hover));
  color: var(--theme-primary);
}

.svc-tx-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.svc-tx-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.svc-tx-type {
  font-size: 14px;
  font-weight: 500;
}

.svc-tx-amount {
  font-size: 15px;
  font-weight: 600;
  flex-shrink: 0;
}
.svc-tx-amount.income {
  color: var(--theme-primary);
}
.svc-tx-amount.expense {
  color: var(--theme-text-1);
}

.svc-tx-sub {
  font-size: 12px;
  color: var(--theme-text-3);
}

.svc-tx-remark {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.svc-tx-time {
  flex-shrink: 0;
  font-size: 11px;
}

.svc-tx-more {
  text-align: center;
  padding: 12px 0;
  border-top: 1px solid var(--theme-hairline);
}

.svc-more-btn {
  border: none;
  background: transparent;
  color: var(--theme-primary);
  font-size: 13px;
  cursor: pointer;
}
.svc-more-btn:disabled {
  color: var(--theme-text-3);
  cursor: default;
}

.svc-more-end {
  font-size: 12px;
  color: var(--theme-text-3);
}

/* ===== 弹窗 ===== */
.svc-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.svc-window {
  width: 360px;
  max-width: calc(100vw - 32px);
  background: var(--theme-bg-surface);
  border-radius: 14px;
  box-shadow: var(--theme-shadow-md, var(--theme-shadow-sm));
  overflow: hidden;
}

.svc-window-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 13px 16px;
  border-bottom: 1px solid var(--theme-hairline);
}
.svc-window-head h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
}

.svc-window-cancel,
.svc-window-submit {
  border: none;
  background: transparent;
  font-size: 14px;
  cursor: pointer;
}
.svc-window-cancel {
  color: var(--theme-text-2);
}
.svc-window-submit {
  color: var(--theme-primary);
  font-weight: 500;
}
.svc-window-submit:disabled {
  color: var(--theme-text-3);
  cursor: default;
}

.svc-window-body {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.svc-field-label {
  font-size: 12px;
  color: var(--theme-text-3);
  margin-top: 8px;
}

.svc-field {
  padding: 10px 12px;
  border: 1px solid var(--theme-hairline);
  border-radius: 8px;
  background: var(--theme-bg-app, var(--theme-bg-surface));
  color: var(--theme-text-1);
  font-size: 14px;
  outline: none;
  width: 100%;
  box-sizing: border-box;
}
.svc-field:focus {
  border-color: var(--theme-primary);
}

.svc-amount-field {
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid var(--theme-hairline);
  border-radius: 8px;
  padding: 4px 12px;
  background: var(--theme-bg-app, var(--theme-bg-surface));
}
.svc-amount-field:focus-within {
  border-color: var(--theme-primary);
}

.svc-amount-prefix {
  font-size: 18px;
  font-weight: 600;
  color: var(--theme-text-1);
}

.svc-amount-input {
  border: none;
  background: transparent;
  font-size: 22px;
  font-weight: 600;
  padding: 8px 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .svc-content {
    padding: 14px 12px 24px;
  }
  .svc-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  .svc-amount {
    font-size: 32px;
  }
}
</style>
