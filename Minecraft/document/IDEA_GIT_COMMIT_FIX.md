# IDEA Git 提交"指定提交信息"问题解决方案

## 问题描述

在 IDEA 中提交 Git 时，提示"指定提交信息"或提交信息为空的问题。

## 问题分析

### 当前 Git 状态检查

#### 1. Git 配置检查 ✅
```bash
git config --list
```
**结果**：用户名和邮箱配置正常
- user.name="bolan"
- user.email="2560177364@qq.com"

#### 2. Git Hook 检查 ✅
```bash
Get-ChildItem .git\hooks
```
**结果**：没有配置 commit-msg hook，只有 .sample 文件

#### 3. 提交历史检查 ⚠️
```bash
git log --oneline -5
```
**结果**：
```
a14b06c (HEAD -> master) main
c3e1c6d main
```
**问题**：提交信息都是 "main"，这不正常

## 可能的原因

### 原因 1: 提交信息为空或无效 ⭐⭐⭐⭐⭐
IDEA 的 Git 提交对话框中，提交信息输入框为空或只包含空格。

### 原因 2: IDEA Git 插件问题 ⭐⭐⭐⭐
IDEA 的 Git 插件可能存在缓存问题或配置错误。

### 原因 3: Git 配置问题 ⭐⭐⭐
Git 的 commit.template 配置可能导致问题。

### 原因 4: Git Hook 问题 ⭐⭐
虽然当前没有配置 hook，但可能之前配置过。

## 解决方案

### 方案 1: 使用命令行提交（最可靠）⭐⭐⭐⭐⭐

#### 步骤 1: 添加文件到暂存区
```bash
# 添加所有修改的文件
git add .

# 或者添加特定文件
git add src/main/java/com/minecraft/controller/
```

#### 步骤 2: 提交代码
```bash
# 提交并指定提交信息
git commit -m "feat: 修复 Controller 层用户 ID 获取问题

- 创建 SecurityUtils 工具类统一获取当前用户 ID
- 修改所有需要认证的 Controller 使用 SecurityUtils
- 移除 HttpServletRequest 参数，简化代码
- 修复 /api/user/info 500 错误问题"
```

#### 步骤 3: 推送到远程仓库
```bash
# 推送到远程仓库
git push origin master
```

### 方案 2: 修复 IDEA Git 提交对话框 ⭐⭐⭐⭐

#### 步骤 1: 清除 IDEA 缓存
1. 打开 IDEA
2. 点击菜单：`File` → `Invalidate Caches...`
3. 勾选以下选项：
   - ✅ Clear file system cache and Local History
   - ✅ Clear downloaded shared indexes
   - ✅ Clear VCS Log caches and indexes
4. 点击 `Invalidate and Restart`

#### 步骤 2: 检查 IDEA Git 配置
1. 打开 IDEA 设置：`File` → `Settings` → `Version Control` → `Git`
2. 检查以下配置：
   - ✅ Path to Git executable: 应该指向 Git 安装路径
   - ✅ SSH executable: Built-in 或 Native
   - ✅ Use credential helper: 勾选

#### 步骤 3: 检查提交信息格式
1. 在 IDEA 中打开提交对话框：`VCS` → `Commit`
2. 确保提交信息输入框不为空
3. 提交信息格式建议：
   ```
   feat: 简短描述

   详细描述（可选）
   ```

### 方案 3: 配置 Git 提交模板 ⭐⭐⭐

#### 步骤 1: 创建提交模板文件
创建文件 `.gitmessage`：
```
# <type>: <subject>
#
# <body>
#
# <footer>
#
# type: feat, fix, docs, style, refactor, test, chore
# subject: 简短描述（不超过 50 个字符）
# body: 详细描述（每行不超过 72 个字符）
# footer: 关联的 Issue 或 PR
```

#### 步骤 2: 配置 Git 使用模板
```bash
git config commit.template .gitmessage
```

#### 步骤 3: 在 IDEA 中使用模板
1. 打开 IDEA 设置：`File` → `Settings` → `Version Control` → `Commit`
2. 勾选 `Use non-modal commit interface`
3. 提交时会自动加载模板

### 方案 4: 配置 Commit-msg Hook（可选）⭐⭐

#### 步骤 1: 创建 commit-msg hook
创建文件 `.git/hooks/commit-msg`：
```bash
#!/bin/bash

# 检查提交信息是否为空
if [ -z "$(cat $1)" ]; then
    echo "错误：提交信息不能为空"
    exit 1
fi

# 检查提交信息格式
msg=$(cat $1 | head -n 1)
if [[ ! $msg =~ ^(feat|fix|docs|style|refactor|test| chore)\: ]]; then
    echo "错误：提交信息格式不正确"
    echo "正确格式：type: subject"
    echo "type 可以是：feat, fix, docs, style, refactor, test, chore"
    exit 1
fi

exit 0
```

#### 步骤 2: 添加执行权限
```bash
chmod +x .git/hooks/commit-msg
```

#### 步骤 3: 测试 hook
```bash
git commit -m "test: 测试 commit-msg hook"
```

### 方案 5: 检查 IDEA 版本和插件 ⭐⭐

#### 步骤 1: 检查 IDEA 版本
1. 打开 IDEA
2. 点击菜单：`Help` → `About`
3. 查看版本号

#### 步骤 2: 更新 Git 插件
1. 打开 IDEA 设置：`File` → `Settings` → `Plugins`
2. 搜索 `Git`
3. 如果有更新，点击 `Update`

#### 步骤 3: 重启 IDEA
更新插件后重启 IDEA。

## 提交信息规范

### 提交信息格式

```
<type>: <subject>

<body>

<footer>
```

### Type 类型

| Type | 说明 | 示例 |
|------|------|------|
| feat | 新功能 | feat: 添加用户登录功能 |
| fix | 修复 Bug | fix: 修复数据库连接错误 |
| docs | 文档更新 | docs: 更新 API 文档 |
| style | 代码格式调整 | style: 统一代码格式 |
| refactor | 重构代码 | refactor: 重构 Service 层 |
| test | 测试相关 | test: 添加单元测试 |
| chore | 构建/工具相关 | chore: 更新依赖版本 |

### Subject 规则
- 使用祈使句
- 首字母小写
- 不超过 50 个字符
- 结尾不加句号

### Body 规则
- 详细描述本次提交的内容
- 每行不超过 72 个字符
- 说明"是什么"和"为什么"

### Footer 规则
- 关联的 Issue 或 PR
- 破坏性变更说明

## 常见问题

### Q1: 为什么提交信息都是 "main"？
**A**: 这可能是因为：
1. IDEA 的提交对话框中默认填充了 "main"
2. 用户没有修改提交信息就直接提交了
3. Git 配置了错误的 commit.template

**解决方法**：
```bash
# 检查是否有 commit.template 配置
git config --get commit.template

# 如果有，删除配置
git config --unset commit.template
```

### Q2: 如何修改最近的提交信息？
**A**:
```bash
# 修改最后一次提交信息
git commit --amend

# 修改多次提交信息（谨慎使用）
git rebase -i HEAD~3
```

### Q3: IDEA 提交对话框不显示怎么办？
**A**:
1. 检查是否启用了非模态提交界面：
   - `File` → `Settings` → `Version Control` → `Commit`
   - 勾选 `Use non-modal commit interface`

2. 清除缓存并重启 IDEA：
   - `File` → `Invalidate Caches...`
   - 点击 `Invalidate and Restart`

### Q4: 如何批量提交多个文件？
**A**:
```bash
# 方法 1: 添加所有文件
git add .
git commit -m "feat: 批量提交多个文件"

# 方法 2: 添加特定类型的文件
git add src/main/java/com/minecraft/controller/*.java
git commit -m "feat: 更新所有 Controller"

# 方法 3: 在 IDEA 中选择多个文件后提交
```

### Q5: 提交后如何撤销？
**A**:
```bash
# 撤销最后一次提交（保留修改）
git reset --soft HEAD~1

# 撤销最后一次提交（丢弃修改）
git reset --hard HEAD~1

# 撤销已推送的提交（需要强制推送）
git reset --hard HEAD~1
git push origin master --force
```

## 推荐的工作流程

### 1. 查看修改状态
```bash
git status
```

### 2. 查看具体修改内容
```bash
git diff
```

### 3. 添加文件到暂存区
```bash
git add .
```

### 4. 提交代码
```bash
git commit -m "feat: 简短描述"
```

### 5. 推送到远程仓库
```bash
git push origin master
```

## 总结

### 最推荐的解决方案

**方案 1: 使用命令行提交** - 最可靠，不受 IDEA 插件影响

```bash
git add .
git commit -m "feat: 修复 Controller 层用户 ID 获取问题"
git push origin master
```

### 如果必须使用 IDEA

**方案 2: 修复 IDEA Git 提交对话框**
1. 清除 IDEA 缓存
2. 检查 Git 配置
3. 确保提交信息不为空

### 长期建议

**方案 3: 配置 Git 提交模板**
- 创建 `.gitmessage` 文件
- 配置 `git config commit.template .gitmessage`
- 使用规范的提交信息格式

## 相关文件

- [.git/config](file:///d:\IDEA\work\Minecraft\.git\config) - Git 配置文件
- [.git/COMMIT_EDITMSG](file:///d:\IDEA\work\Minecraft\.git\COMMIT_EDITMSG) - 提交信息文件
- [.git/hooks/](file:///d:\IDEA\work\Minecraft\.git\hooks\) - Git Hooks 目录

## 快速参考

### 常用 Git 命令

```bash
# 查看状态
git status

# 添加文件
git add .
git add <file>

# 提交
git commit -m "message"
git commit --amend

# 推送
git push
git push origin master

# 拉取
git pull
git pull origin master

# 查看历史
git log
git log --oneline
git log --graph

# 撤销
git reset --soft HEAD~1
git reset --hard HEAD~1
```