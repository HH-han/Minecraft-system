<template>
  <div class="railway-shell">
    <RailwayHeader />
    <main class="railway-main">
      <router-view />
    </main>
    <footer class="railway-footer">
      <div class="railway-footer-inner">
        <div class="footer-links">
          <router-link to="/aboutweb">{{ $t('railway.footer.about') }}</router-link>
          <span class="dot"></span>
          <a href="mailto:support@minecraft-travel.com">{{ $t('railway.footer.contact') }}</a>
          <span class="dot"></span>
          <router-link to="/aboutweb">{{ $t('railway.footer.terms') }}</router-link>
          <span class="dot"></span>
          <router-link to="/aboutweb">{{ $t('railway.footer.privacy') }}</router-link>
        </div>
        <p class="copyright">{{ $t('railway.footer.copyright') }}</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import RailwayHeader from './components/RailwayHeader.vue'
</script>

<style scoped>
.railway-shell {
  /* 铁路票务局部主题令牌（亮色） */
  --rw-blue: #3b7cff;
  --rw-blue-deep: #1b4fbf;
  --rw-blue-ink: #0f2e66;
  --rw-orange: #ff8a1f;
  --rw-orange-hover: #f07800;
  --rw-bg: #f3f7fd;
  --rw-card: #ffffff;
  --rw-text: #10233f;
  --rw-text-2: #5c6f8c;
  --rw-text-3: #93a3ba;
  --rw-border: #e3ebf7;
  --rw-soft: #eef4ff;
  --rw-soft-2: #f7faff;
  --rw-danger: #ef4444;
  --rw-success: #10b981;
  --rw-shadow: 0 18px 44px -26px rgba(16, 47, 100, 0.35);
  --rw-radius: 16px;

  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--rw-bg);
  color: var(--rw-text);
  font-family: 'Noto Sans SC', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

html.dark .railway-shell {
  --rw-blue: #4d8bff;
  --rw-blue-deep: #2a5cbf;
  --rw-blue-ink: #9dc0ff;
  --rw-orange: #ff9b3d;
  --rw-orange-hover: #ffad61;
  --rw-bg: #0f141c;
  --rw-card: #171f2b;
  --rw-text: #eef3fb;
  --rw-text-2: #a6b6cc;
  --rw-text-3: #74869f;
  --rw-border: #253143;
  --rw-soft: #1b2636;
  --rw-soft-2: #141c27;
  --rw-danger: #f87171;
  --rw-success: #34d399;
  --rw-shadow: 0 18px 44px -26px rgba(0, 0, 0, 0.8);
}

.railway-main {
  flex: 1;
  width: 100%;
}

.railway-footer {
  margin-top: 64px;
  background: var(--rw-card);
  border-top: 1px solid var(--rw-border);
}

.railway-footer-inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 26px 24px 32px;
  text-align: center;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 13px;
  color: var(--rw-text-2);
}

.footer-links a {
  color: var(--rw-text-2);
  text-decoration: none;
  transition: color 0.2s ease;
}

.footer-links a:hover {
  color: var(--rw-blue);
}

.dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--rw-text-3);
}

.copyright {
  margin: 12px 0 0;
  font-size: 12px;
  color: var(--rw-text-3);
  letter-spacing: 0.02em;
}

@media (max-width: 640px) {
  .railway-footer {
    margin-top: 40px;
  }

  .railway-footer-inner {
    padding: 20px 16px 26px;
  }
}
</style>
