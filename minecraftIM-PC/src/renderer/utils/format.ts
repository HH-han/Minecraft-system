/** 时间与文件大小格式化（跟随界面语言使用 Intl） */

export function toDate(value?: string | number | Date | null): Date | null {
  if (!value) return null;
  const date = value instanceof Date ? value : new Date(value);
  return Number.isNaN(date.getTime()) ? null : date;
}

function isSameDay(a: Date, b: Date): boolean {
  return (
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  );
}

/** 会话列表时间：今天显示时刻，昨天显示文案，更早显示月日 */
export function formatListTime(
  value: string | Date | null | undefined,
  locale: string,
  yesterdayLabel = 'Yesterday'
): string {
  const date = toDate(value);
  if (!date) return '';
  const now = new Date();
  if (isSameDay(date, now)) {
    return new Intl.DateTimeFormat(locale, {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    }).format(date);
  }
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  if (isSameDay(date, yesterday)) return yesterdayLabel;
  return new Intl.DateTimeFormat(locale, { month: 'numeric', day: 'numeric' }).format(date);
}

/** 聊天记录中的日期分隔：8月30日 23:05 */
export function formatDaySeparator(value: string | Date | null | undefined, locale: string): string {
  const date = toDate(value);
  if (!date) return '';
  return new Intl.DateTimeFormat(locale, {
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(date);
}

/** 完整时间：用于详情、评论等 */
export function formatFullTime(value: string | Date | null | undefined, locale: string): string {
  const date = toDate(value);
  if (!date) return '';
  return new Intl.DateTimeFormat(locale, {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(date);
}

export function formatClock(value: string | Date | null | undefined, locale: string): string {
  const date = toDate(value);
  if (!date) return '';
  return new Intl.DateTimeFormat(locale, {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  }).format(date);
}

export function formatFileSize(bytes?: number | null): string {
  if (!bytes || bytes <= 0) return '';
  const units = ['B', 'KB', 'MB', 'GB'];
  let value = bytes;
  let index = 0;
  while (value >= 1024 && index < units.length - 1) {
    value /= 1024;
    index += 1;
  }
  return `${value.toFixed(value >= 10 || index === 0 ? 0 : 1)} ${units[index]}`;
}

/** 动态 / 评论的相对时间（刚刚、x 分钟前）——文案由调用方通过 labels 提供 */
export function relativeTime(
  value: string | Date | null | undefined,
  labels: { justNow: string; minutes: string; hours: string; days: string }
): string {
  const date = toDate(value);
  if (!date) return '';
  const diff = Date.now() - date.getTime();
  const minutes = Math.floor(diff / 60000);
  if (minutes < 1) return labels.justNow;
  if (minutes < 60) return labels.minutes.replace('{n}', String(minutes));
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return labels.hours.replace('{n}', String(hours));
  const days = Math.floor(hours / 24);
  return labels.days.replace('{n}', String(days));
}
