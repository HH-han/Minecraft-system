/**
 * 弹窗层级栈：保证多个弹窗叠加时只有最顶层响应 Esc 键。
 * 与具体组件解耦，可被任意浮层复用。
 */
const stack: symbol[] = [];

export function pushLayer(id: symbol): void {
  if (stack.includes(id)) return;
  stack.push(id);
}

export function popLayer(id: symbol): void {
  const index = stack.indexOf(id);
  if (index >= 0) stack.splice(index, 1);
}

export function isTopLayer(id: symbol): boolean {
  return stack.length > 0 && stack[stack.length - 1] === id;
}
