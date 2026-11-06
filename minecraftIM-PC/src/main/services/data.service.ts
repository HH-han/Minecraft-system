import fs from 'node:fs/promises';
import path from 'node:path';
import { paths } from '../infra/config';

type StoreShape = Record<string, unknown>;

/**
 * 极简 JSON 键值存储（渲染进程不直接碰文件系统，全部经由 IPC）。
 * 后续可替换为 SQLite / 加密存储，接口保持不变。
 */
const storeFile = path.join(paths.dataDir, 'store.json');
let cache: StoreShape | null = null;

async function load(): Promise<StoreShape> {
  if (cache) return cache;

  try {
    const raw = await fs.readFile(storeFile, 'utf8');
    const parsed = JSON.parse(raw) as unknown;
    cache = parsed && typeof parsed === 'object' ? (parsed as StoreShape) : {};
  } catch {
    cache = {};
  }
  return cache;
}

async function persist(data: StoreShape): Promise<void> {
  await fs.mkdir(paths.dataDir, { recursive: true });
  await fs.writeFile(storeFile, JSON.stringify(data, null, 2), 'utf8');
}

export async function getValue(key: string): Promise<unknown> {
  const data = await load();
  return data[key];
}

export async function setValue(key: string, value: unknown): Promise<void> {
  const data = await load();
  data[key] = value;
  await persist(data);
}
