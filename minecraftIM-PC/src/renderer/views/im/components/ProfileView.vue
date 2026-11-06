<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { ElMessage } from 'element-plus';
import { ArrowLeft, Camera, Check, EditPen } from '@element-plus/icons-vue';
import { uploadAvatar, updateUserInfo } from '@/api/user';
import { resolveMediaUrl } from '@/config';
import { useAuthStore } from '@/stores/auth';
import type { UserInfo } from '@/types/im';

const emit = defineEmits<{
  back: [];
}>();

const { t } = useI18n();
const authStore = useAuthStore();

const editing = ref(false);
const saving = ref(false);
const uploading = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const form = ref<Record<string, string | number>>({});

const user = computed<UserInfo | null>(() => authStore.user);

const displayName = computed(
  () => user.value?.nickname || user.value?.username || t('im.profile.noAccount')
);

const avatarUrl = computed(() => resolveMediaUrl(user.value?.avatar));

const account = computed(() => user.value?.account || user.value?.username || '-');

const points = computed(() => Number(user.value?.points ?? 0));

const genderLabel = computed(() => {
  const gender = String(user.value?.gender ?? '');
  if (!gender) return t('im.profile.emptyValue');
  if (gender === '0' || gender === 'male' || gender === '男') return t('im.profile.male');
  if (gender === '1' || gender === 'female' || gender === '女') return t('im.profile.female');
  if (gender === 'unknown' || gender === 'secret') return t('im.profile.unknown');
  return gender;
});

function normalizeGender(raw?: string): string {
  const value = String(raw ?? '');
  if (value === '0' || value === 'male' || value === '男') return 'male';
  if (value === '1' || value === 'female' || value === '女') return 'female';
  if (value === 'unknown' || value === 'secret') return 'unknown';
  return '';
}

const createTime = computed(() => {
  const raw = user.value?.createTime;
  if (!raw) return t('im.profile.emptyValue');
  const date = new Date(raw);
  if (Number.isNaN(date.getTime())) return raw;
  return date.toLocaleDateString();
});

const infoRows = computed(() => [
  { key: 'account', label: t('im.profile.account'), value: account.value },
  { key: 'phone', label: t('im.profile.phone'), value: user.value?.phone || t('im.profile.emptyValue') },
  { key: 'email', label: t('im.profile.email'), value: user.value?.email || t('im.profile.emptyValue') },
  { key: 'gender', label: t('im.profile.gender'), value: genderLabel.value },
  {
    key: 'age',
    label: t('im.profile.age'),
    value: user.value?.age ? String(user.value.age) : t('im.profile.emptyValue')
  },
  { key: 'occupation', label: t('im.profile.occupation'), value: user.value?.occupation || t('im.profile.emptyValue') },
  { key: 'points', label: t('im.profile.pointsLabel'), value: String(points.value) },
  { key: 'createTime', label: t('im.profile.joinTime'), value: createTime.value }
]);

function startEdit(): void {
  const current = user.value;
  form.value = {
    nickname: current?.nickname ?? '',
    signature: current?.signature ?? '',
    gender: normalizeGender(current?.gender),
    age: current?.age ?? '',
    occupation: current?.occupation ?? '',
    hobbies: current?.hobbies ?? '',
    bio: current?.bio ?? '',
    experience: current?.experience ?? '',
    email: current?.email ?? '',
    phone: current?.phone ?? ''
  };
  editing.value = true;
}

function cancelEdit(): void {
  editing.value = false;
}

async function save(): Promise<void> {
  if (saving.value) return;
  const id = authStore.userId;
  if (!id) return;
  saving.value = true;
  try {
    const payload: Partial<UserInfo> & { id?: number } = {
      id,
      nickname: String(form.value.nickname ?? '').trim(),
      signature: String(form.value.signature ?? '').trim(),
      gender: String(form.value.gender ?? '').trim(),
      occupation: String(form.value.occupation ?? '').trim(),
      hobbies: String(form.value.hobbies ?? '').trim(),
      bio: String(form.value.bio ?? '').trim(),
      experience: String(form.value.experience ?? '').trim(),
      email: String(form.value.email ?? '').trim(),
      phone: String(form.value.phone ?? '').trim()
    };
    const ageValue = Number(form.value.age);
    if (Number.isFinite(ageValue) && ageValue > 0) payload.age = ageValue;

    await updateUserInfo(payload);
    await authStore.refreshUser();
    editing.value = false;
    ElMessage.success(t('im.profile.saved'));
  } catch {
    /* 请求层已提示错误 */
  } finally {
    saving.value = false;
  }
}

function pickAvatar(): void {
  fileInput.value?.click();
}

async function onAvatarChange(event: Event): Promise<void> {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file || uploading.value) return;
  uploading.value = true;
  try {
    const res = await uploadAvatar(file);
    authStore.patchUser({ avatar: res.data });
    ElMessage.success(t('im.profile.avatarUpdated'));
  } catch {
    /* 请求层已提示错误 */
  } finally {
    uploading.value = false;
  }
}
</script>

<template>
  <section class="profile-view">
    <header class="profile-head">
      <button class="im-icon-btn" :title="t('im.common.back')" @click="emit('back')">
        <el-icon :size="16"><ArrowLeft /></el-icon>
      </button>
      <h2 class="profile-title">{{ t('im.profile.title') }}</h2>

      <div class="profile-head__actions">
        <template v-if="editing">
          <el-button text @click="cancelEdit">{{ t('im.common.cancel') }}</el-button>
          <el-button class="im-action-primary" :loading="saving" @click="save">
            <el-icon v-if="!saving" :size="14"><Check /></el-icon>
            <span>{{ t('im.profile.save') }}</span>
          </el-button>
        </template>
        <el-button v-else class="im-action-primary" @click="startEdit">
          <el-icon :size="14"><EditPen /></el-icon>
          <span>{{ t('im.profile.edit') }}</span>
        </el-button>
      </div>
    </header>

    <div class="im-scroll profile-scroll">
      <div class="profile-hero">
        <button
          class="profile-avatar"
          type="button"
          :disabled="uploading"
          :title="t('im.profile.changeAvatar')"
          @click="pickAvatar"
        >
          <img v-if="avatarUrl" :src="avatarUrl" :alt="displayName" />
          <span v-else>{{ displayName.charAt(0).toUpperCase() }}</span>
          <span class="profile-avatar__mask">
            <el-icon :size="18"><Camera /></el-icon>
          </span>
        </button>
        <input
          ref="fileInput"
          class="profile-file"
          type="file"
          accept="image/*"
          @change="onAvatarChange"
        />

        <div class="profile-hero__body">
          <h3 class="profile-name">{{ displayName }}</h3>
          <p class="profile-signature">
            {{ user?.signature || t('im.profile.signatureEmpty') }}
          </p>
          <div class="profile-chips">
            <span class="im-chip">{{ t('im.profile.account') }} · {{ account }}</span>
            <span class="im-chip">{{ t('im.profile.pointsLabel') }} · {{ points }}</span>
          </div>
        </div>
      </div>

      <template v-if="editing">
        <div class="profile-card">
          <h4 class="profile-card__title">{{ t('im.profile.basicInfo') }}</h4>
          <div class="profile-form">
            <label class="profile-field">
              <span>{{ t('im.profile.nickname') }}</span>
              <el-input v-model="form.nickname" maxlength="24" />
            </label>
            <label class="profile-field">
              <span>{{ t('im.profile.age') }}</span>
              <el-input v-model="form.age" type="number" min="0" max="120" />
            </label>
            <label class="profile-field">
              <span>{{ t('im.profile.gender') }}</span>
              <el-select v-model="form.gender" :placeholder="t('im.profile.genderPlaceholder')">
                <el-option :label="t('im.profile.male')" value="male" />
                <el-option :label="t('im.profile.female')" value="female" />
                <el-option :label="t('im.profile.unknown')" value="unknown" />
              </el-select>
            </label>
            <label class="profile-field">
              <span>{{ t('im.profile.occupation') }}</span>
              <el-input v-model="form.occupation" maxlength="40" />
            </label>
            <label class="profile-field">
              <span>{{ t('im.profile.email') }}</span>
              <el-input v-model="form.email" maxlength="80" />
            </label>
            <label class="profile-field">
              <span>{{ t('im.profile.phone') }}</span>
              <el-input v-model="form.phone" maxlength="20" />
            </label>
          </div>
          <label class="profile-field">
            <span>{{ t('im.profile.signature') }}</span>
            <el-input v-model="form.signature" maxlength="60" />
          </label>
        </div>

        <div class="profile-card">
          <h4 class="profile-card__title">{{ t('im.profile.moreInfo') }}</h4>
          <label class="profile-field">
            <span>{{ t('im.profile.hobbies') }}</span>
            <el-input v-model="form.hobbies" maxlength="120" />
          </label>
          <label class="profile-field">
            <span>{{ t('im.profile.bio') }}</span>
            <el-input v-model="form.bio" type="textarea" :rows="3" maxlength="300" show-word-limit />
          </label>
          <label class="profile-field">
            <span>{{ t('im.profile.experience') }}</span>
            <el-input
              v-model="form.experience"
              type="textarea"
              :rows="4"
              maxlength="600"
              show-word-limit
            />
          </label>
        </div>
      </template>

      <template v-else>
        <div class="profile-card">
          <h4 class="profile-card__title">{{ t('im.profile.basicInfo') }}</h4>
          <div class="profile-table">
            <div v-for="row in infoRows" :key="row.key" class="profile-table__row">
              <span class="profile-table__label">{{ row.label }}</span>
              <span class="profile-table__value">{{ row.value }}</span>
            </div>
          </div>
        </div>

        <div class="profile-card">
          <h4 class="profile-card__title">{{ t('im.profile.moreInfo') }}</h4>
          <div class="profile-block">
            <span class="profile-block__label">{{ t('im.profile.hobbies') }}</span>
            <p class="profile-block__text">
              {{ user?.hobbies || t('im.profile.emptyValue') }}
            </p>
          </div>
          <div class="profile-block">
            <span class="profile-block__label">{{ t('im.profile.bio') }}</span>
            <p class="profile-block__text">{{ user?.bio || t('im.profile.emptyValue') }}</p>
          </div>
          <div class="profile-block">
            <span class="profile-block__label">{{ t('im.profile.experience') }}</span>
            <p class="profile-block__text">{{ user?.experience || t('im.profile.emptyValue') }}</p>
          </div>
        </div>
      </template>
    </div>
  </section>
</template>

<style scoped>
.profile-view {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background: var(--theme-bg-app);
  overflow: hidden;
}

.profile-head {
  height: 64px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 24px;
  border-bottom: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
}

.profile-title {
  margin: 0;
  flex: 1;
  min-width: 0;
  font-size: 16px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.profile-head__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.profile-scroll {
  padding: 22px 26px 40px;
}

.profile-hero {
  display: flex;
  align-items: center;
  gap: 22px;
  padding: 24px 26px;
  border-radius: 18px;
  border: 1px solid var(--theme-hairline);
  background: linear-gradient(
    135deg,
    color-mix(in srgb, var(--theme-primary) 10%, var(--theme-bg-surface)),
    var(--theme-bg-surface) 62%
  );
  box-shadow: var(--theme-shadow-sm);
}

.profile-avatar {
  position: relative;
  width: 96px;
  height: 96px;
  flex-shrink: 0;
  border: none;
  border-radius: 50%;
  padding: 0;
  overflow: hidden;
  cursor: pointer;
  background: var(--theme-accent-gradient);
  color: #fff;
  font-size: 32px;
  font-weight: 700;
  display: grid;
  place-items: center;
  box-shadow: 0 14px 30px -18px color-mix(in srgb, var(--theme-primary) 70%, transparent);
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-avatar__mask {
  position: absolute;
  inset: auto 0 0 0;
  height: 32%;
  display: grid;
  place-items: center;
  color: #fff;
  background: rgba(15, 23, 42, 0.42);
  opacity: 0;
  transition: opacity var(--theme-duration-fast) var(--theme-ease);
}

.profile-avatar:hover .profile-avatar__mask {
  opacity: 1;
}

.profile-file {
  display: none;
}

.profile-hero__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.profile-name {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.profile-signature {
  margin: 0;
  font-size: 13px;
  color: var(--theme-text-3);
}

.profile-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.profile-card {
  margin-top: 16px;
  padding: 18px 22px 22px;
  border-radius: 16px;
  border: 1px solid var(--theme-hairline);
  background: var(--theme-bg-surface);
  box-shadow: var(--theme-shadow-sm);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.profile-card__title {
  margin: 0;
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.4px;
  color: var(--theme-text-2);
}

.profile-table {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 2px 26px;
}

.profile-table__row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid var(--theme-hairline);
}

.profile-table__label {
  width: 84px;
  flex-shrink: 0;
  font-size: 12px;
  color: var(--theme-text-3);
}

.profile-table__value {
  flex: 1;
  min-width: 0;
  font-size: 13px;
  color: var(--theme-text-1);
  word-break: break-all;
}

.profile-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.profile-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 12px;
  color: var(--theme-text-3);
}

.profile-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.profile-block__label {
  font-size: 12px;
  color: var(--theme-text-3);
}

.profile-block__text {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: var(--theme-text-1);
  white-space: pre-wrap;
}

@media (max-width: 960px) {
  .profile-table,
  .profile-form {
    grid-template-columns: 1fr;
  }
}
</style>
