# travel.json 国际化文件补全实施计划

## 代码研究结论

**当前状态**：
- 文件 `apps/web-antdv-next/src/locales/langs/zh-CN/travel.json` 仅含 5 个占位字段（`title`、`user_info`、`order`、`login`、`vben`），其中 `vben` 为空对象
- 项目使用 `import.meta.glob('./langs/**/*.json')` 自动加载 langs 下所有 JSON 作为 i18n 资源（见 [index.ts#L22](file:///d:/vue/vue-vben-admin-main/apps/web-antdv-next/src/locales/index.ts#L22)）
- 无 `en-US/travel.json` 文件（缺失英文翻译）
- 无 travel 对应的视图或路由文件，此 locale 文件为独立静态资源

**截图中识别的菜单项**（中文标签 → 建议英文 key）：

| 中文标签 | 建议 key |
|---|---|
| 酒店管理 | `hotel` |
| 景区管理 | `scenic` |
| 推荐管理 | `recommend` |
| 美食管理 | `food` |
| 世界旅行 | `world`（父级，含子项） |
| 攻略管理 | `strategy` |
| 旅游团管理 | `tourGroup` |
| 轮播管理 | `carousel` |
| 热门推荐 | `hotRecommend` |
| 世界之旅 | `worldTour` |
| 旅行新闻 | `news` |
| 目的地管理 | `destination` |
| 商品管理 | `product` |
| 社区管理 | `community` |
| 月度管理 | `monthly` |
| 官方网站 | `website` |

## 要修改的文件

- `apps/web-antdv-next/src/locales/langs/zh-CN/travel.json`：补全中文翻译字段
- `apps/web-antdv-next/src/locales/langs/en-US/travel.json`：**新建** 英文翻译文件

## 实施步骤

1. **重构 zh-CN/travel.json**：保留 `title: "旅行管理"`，将模板中的占位字段替换为截图中的 16 个菜单项，使用语义化英文 key + 中文值。结构采用扁平键值对（与项目现有 locale 文件风格一致，如 `content.json`、`demos.json`）。`vben` 对象因无实际内容，保持为空。

2. **创建 en-US/travel.json**：镜像 zh-CN 结构，提供英文翻译。

## 依赖与注意事项

- 键名使用 camelCase 英文，值为目标语言（zh-CN → 中文，en-US → 英文）
- 保持 JSON 格式规范，无 trailing commas
- 因项目通过 glob 自动发现文件，无需修改 index.ts 注册

## 验证

- 使用 `pnpm run lint` 检查 JSON 格式（或 `json` 语法检查）
- 检查两个文件的 key 集合是否一致

## 风险

- **无实际路由引用**：项目当前没有 travel 对应的页面/路由，locale 文件补全后不会在 UI 中立即看到效果。这是预期行为，用户明确要求按截图编写 locale 数据。
- **占位字段保留与否**：`user_info`、`order`、`login` 来自原模板，但截图中没有对应菜单项。计划：保留 `title`，移除无关占位字段，替换为真实菜单数据。
