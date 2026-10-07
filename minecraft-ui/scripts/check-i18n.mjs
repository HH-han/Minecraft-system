/**
 * 语言包完整性校验脚本
 * 用法：node scripts/check-i18n.mjs
 * 校验项：
 * 1. 各语言目录下的 json 文件集合一致
 * 2. 各语言的翻译 key 与基准语言（zh-CN）完全一致（检出缺失 / 多余翻译）
 */
import { readdirSync, readFileSync } from 'node:fs';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const BASE_LOCALE = 'zh-CN';
const langsDir = join(dirname(fileURLToPath(import.meta.url)), '../src/locales/langs');

const locales = readdirSync(langsDir, { withFileTypes: true })
  .filter((d) => d.isDirectory())
  .map((d) => d.name);

function flatten(obj, prefix = '') {
  return Object.entries(obj).flatMap(([key, value]) =>
    value && typeof value === 'object'
      ? flatten(value, `${prefix}${key}.`)
      : [`${prefix}${key}`]
  );
}

function loadKeys(locale) {
  const files = readdirSync(join(langsDir, locale)).filter((f) => f.endsWith('.json'));
  const keys = files.flatMap((f) =>
    flatten(JSON.parse(readFileSync(join(langsDir, locale, f), 'utf8')))
  );
  return { files, keys: new Set(keys) };
}

const base = loadKeys(BASE_LOCALE);
let failed = false;

for (const locale of locales) {
  if (locale === BASE_LOCALE) continue;
  const target = loadKeys(locale);

  const missingFiles = base.files.filter((f) => !target.files.includes(f));
  const extraFiles = target.files.filter((f) => !base.files.includes(f));
  const missingKeys = [...base.keys].filter((k) => !target.keys.has(k));
  const extraKeys = [...target.keys].filter((k) => !base.keys.has(k));

  if (missingFiles.length || extraFiles.length || missingKeys.length || extraKeys.length) {
    failed = true;
    console.error(`\n[FAIL] ${locale}`);
    missingFiles.forEach((f) => console.error(`  缺失文件: ${f}`));
    extraFiles.forEach((f) => console.error(`  多余文件: ${f}`));
    missingKeys.forEach((k) => console.error(`  缺失翻译: ${k}`));
    extraKeys.forEach((k) => console.error(`  多余翻译: ${k}`));
  } else {
    console.log(`[OK] ${locale} — ${base.keys.size} 个翻译 key 全部对齐`);
  }
}

if (failed) {
  console.error('\n语言包校验未通过');
  process.exit(1);
}
console.log('\n语言包校验通过');
