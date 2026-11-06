import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export type ThemeId = 'light' | 'dark' | 'game';

export interface ThemeOption {
  id: ThemeId;
  labelKey: string;
}

export const THEME_LIST: ThemeOption[] = [
  { id: 'light', labelKey: 'settings.theme.light' },
  { id: 'dark', labelKey: 'settings.theme.dark' },
  { id: 'game', labelKey: 'settings.theme.game' }
];

const STORAGE_KEY = 'theme';

const isValidTheme = (value: string | null): value is ThemeId =>
  value === 'light' || value === 'dark' || value === 'game';

export function getStoredTheme(): ThemeId {
  const saved = localStorage.getItem(STORAGE_KEY);
  if (isValidTheme(saved)) return saved;
  const prefersDark =
    typeof window.matchMedia === 'function' &&
    window.matchMedia('(prefers-color-scheme: dark)').matches;
  return prefersDark ? 'dark' : 'light';
}

/** 把主题落到 <html data-theme> 与 .dark，供 CSS 令牌与 Element Plus 深色使用 */
export function applyTheme(theme: ThemeId): ThemeId {
  const root = document.documentElement;
  root.setAttribute('data-theme', theme);
  root.classList.toggle('dark', theme !== 'light');
  return theme;
}

export function initTheme(): ThemeId {
  return applyTheme(getStoredTheme());
}

export const useThemeStore = defineStore('theme', () => {
  const currentTheme = ref<ThemeId>(getStoredTheme());
  const options = ref(THEME_LIST);
  const isDark = computed(() => currentTheme.value !== 'light');

  function setTheme(theme: ThemeId): void {
    currentTheme.value = applyTheme(theme);
    localStorage.setItem(STORAGE_KEY, currentTheme.value);
  }

  function cycleTheme(): void {
    const index = options.value.findIndex((item) => item.id === currentTheme.value);
    const next = options.value[(index + 1) % options.value.length];
    setTheme(next.id);
  }

  function init(): void {
    currentTheme.value = initTheme();
  }

  return {
    currentTheme,
    options,
    isDark,
    setTheme,
    cycleTheme,
    init
  };
});
