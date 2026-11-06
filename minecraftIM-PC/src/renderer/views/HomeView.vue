<script setup lang="ts">
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { FolderOpened, Refresh } from '@element-plus/icons-vue';
import { invoke } from '@/bridge';
import { IPC_CHANNELS } from '@shared/constants';
import { useAppStore } from '@/stores/app';
import { useAuthStore } from '@/stores/auth';

const { t } = useI18n();
const appStore = useAppStore();
const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const formError = ref<string | null>(null);
const selectedFile = ref<string | null>(null);
const fileError = ref<string | null>(null);

const info = computed(() => appStore.info);

async function handleLogin(): Promise<void> {
  formError.value = null;
  if (!username.value.trim() || !password.value) {
    formError.value = t('home.auth.emptyError');
    return;
  }

  const ok = await authStore.login({
    username: username.value.trim(),
    password: password.value
  });

  if (ok) {
    username.value = '';
    password.value = '';
  } else {
    formError.value = authStore.error;
  }
}

async function handleSelectFile(): Promise<void> {
  fileError.value = null;
  try {
    const result = await invoke(IPC_CHANNELS.fileSelect, {
      title: t('home.file.select'),
      filters: [{ name: 'All files', extensions: ['*'] }]
    });

    if (!result.canceled && result.filePaths.length > 0) {
      selectedFile.value = result.filePaths[0];
    }
  } catch (error) {
    fileError.value = error instanceof Error ? error.message : String(error);
  }
}
</script>

<template>
  <div class="home">
    <section class="hero theme-card">
      <span class="theme-chip theme-chip--active">Adrenaline.js + Vue 3</span>
      <h2>{{ t('home.title') }}</h2>
      <p class="theme-text-2">{{ t('home.subtitle') }}</p>
    </section>

    <div class="content-grid">
      <section class="theme-card panel">
        <header class="panel-head">
          <h3>{{ t('home.runtime.title') }}</h3>
          <el-button text :loading="appStore.loading" @click="appStore.loadAppInfo()">
            <el-icon><Refresh /></el-icon>
            {{ t('home.runtime.refresh') }}
          </el-button>
        </header>

        <dl v-if="info" class="info-list">
          <div>
            <dt>{{ t('home.runtime.version') }}</dt>
            <dd>{{ info.version }}</dd>
          </div>
          <div>
            <dt>{{ t('home.runtime.platform') }}</dt>
            <dd>{{ info.platform }} / {{ info.arch }}</dd>
          </div>
          <div>
            <dt>{{ t('home.runtime.node') }}</dt>
            <dd>{{ info.nodeVersion }}</dd>
          </div>
          <div>
            <dt>{{ t('home.runtime.mode') }}</dt>
            <dd>{{ info.isDev ? t('home.runtime.dev') : t('home.runtime.prod') }}</dd>
          </div>
        </dl>

        <p v-else class="panel-placeholder theme-text-3">
          {{ appStore.loading ? '…' : appStore.error }}
        </p>
      </section>

      <section class="theme-card panel">
        <header class="panel-head">
          <h3>{{ t('home.auth.title') }}</h3>
        </header>
        <p class="panel-desc theme-text-3">{{ t('home.auth.description') }}</p>

        <template v-if="authStore.state.authenticated">
          <p class="signed-in">
            {{ t('home.auth.welcome', { name: authStore.state.user?.nickname }) }}
          </p>
          <el-button @click="authStore.logout()">{{ t('home.auth.logout') }}</el-button>
        </template>

        <form v-else class="login-form" @submit.prevent="handleLogin">
          <el-input
            v-model="username"
            :aria-label="t('home.auth.username')"
            :placeholder="t('home.auth.usernamePlaceholder')"
          />
          <el-input
            v-model="password"
            type="password"
            show-password
            :aria-label="t('home.auth.password')"
            :placeholder="t('home.auth.passwordPlaceholder')"
          />
          <p v-if="formError" class="form-error">{{ formError }}</p>
          <el-button type="primary" native-type="submit" :loading="authStore.loading">
            {{ authStore.loading ? t('home.auth.submitting') : t('home.auth.submit') }}
          </el-button>
        </form>
      </section>

      <section class="theme-card panel">
        <header class="panel-head">
          <h3>{{ t('home.file.title') }}</h3>
        </header>
        <p class="panel-desc theme-text-3">{{ t('home.file.description') }}</p>

        <el-button @click="handleSelectFile">
          <el-icon><FolderOpened /></el-icon>
          {{ t('home.file.select') }}
        </el-button>

        <p v-if="fileError" class="form-error">{{ fileError }}</p>
        <p v-else class="file-path theme-text-3">
          {{ selectedFile ? t('home.file.selected', { path: selectedFile }) : t('home.file.none') }}
        </p>
      </section>
    </div>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hero {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 28px 30px;
}

.hero h2 {
  margin: 0;
  font-size: 26px;
  font-weight: 700;
  color: var(--theme-text-1);
}

.hero p {
  max-width: 760px;
  margin: 0;
  font-size: 14px;
  line-height: 1.7;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 22px 24px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-head h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 650;
  color: var(--theme-text-1);
}

.panel-desc {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.6;
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin: 0;
}

.info-list > div {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 8px;
  border-bottom: 1px dashed var(--theme-hairline);
}

.info-list > div:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.info-list dt {
  font-size: 12.5px;
  color: var(--theme-text-3);
}

.info-list dd {
  margin: 0;
  overflow: hidden;
  font-size: 13px;
  color: var(--theme-text-1);
  text-overflow: ellipsis;
  text-align: right;
  white-space: nowrap;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.signed-in {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--theme-success);
}

.form-error {
  margin: 0;
  font-size: 12px;
  color: var(--theme-danger);
}

.file-path {
  margin: 0;
  overflow: hidden;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.panel-placeholder {
  margin: 0;
  font-size: 13px;
}
</style>
