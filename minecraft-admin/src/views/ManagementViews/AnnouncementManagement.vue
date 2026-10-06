<template>
    <div class="management-page-background">
        <div class="container-management">
            <!-- 操作栏 -->
            <div class="action-bar">
                <div class="search-bar">
                    <div class="search-box-management">
                        <input type="text" v-model="searchKeyword" placeholder="输入公告标题搜索"
                            class="search-input-management" @keyup.enter="handleSearch" />
                    </div>
                    <button class="btn search-btn" @click="handleSearch">搜索</button>
                    <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px"
                        @change="handleFilter">
                        <el-option label="全部" :value="null" />
                        <el-option label="草稿" :value="0" />
                        <el-option label="已发布" :value="1" />
                        <el-option label="已下架" :value="2" />
                    </el-select>
                    <el-select v-model="filterType" placeholder="类型" clearable style="width: 130px"
                        @change="handleFilter">
                        <el-option v-for="t in TYPE_OPTIONS" :key="t.value" :label="t.label" :value="t.value" />
                    </el-select>
                </div>
                <div class="operate-bar">
                    <button class="btn add-btn" @click="showAddDialog">新建公告</button>
                </div>
            </div>
            <!-- 数据表格 -->
            <div class="data-table-container">
                <div class="data-table-wrapper">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>标题</th>
                                <th>类型</th>
                                <th>级别</th>
                                <th>展示方式</th>
                                <th>状态</th>
                                <th>置顶</th>
                                <th>浏览量</th>
                                <th>已读数</th>
                                <th>发布时间</th>
                                <th>过期时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-if="loading">
                                <td colspan="12" style="text-align:center">加载中...</td>
                            </tr>
                            <tr v-else-if="!rows.length">
                                <td colspan="12" style="text-align:center">暂无数据</td>
                            </tr>
                            <tr v-for="row in rows" :key="row.id">
                                <td>{{ row.id }}</td>
                                <td class="title-cell" :title="row.title">
                                    <span v-if="row.isTop" class="top-badge">置顶</span>{{ row.title }}
                                </td>
                                <td>{{ typeText(row.type) }}</td>
                                <td>
                                    <span :class="['level-tag', 'level-' + row.level]">{{ levelText(row.level) }}</span>
                                </td>
                                <td>{{ displayModeText(row.displayMode) }}</td>
                                <td>
                                    <span :class="['status-tag', 'status-' + row.status]">{{ statusText(row.status)
                                    }}</span>
                                </td>
                                <td>{{ row.isTop === 1 ? '是' : '否' }}</td>
                                <td>{{ row.viewCount || 0 }}</td>
                                <td>{{ row.readCount || 0 }}</td>
                                <td>{{ formatDateTime(row.publishTime) }}</td>
                                <td>{{ formatDateTime(row.expireTime) }}</td>
                                <td class="table-btn-display">
                                    <button class="btn edit-btn" @click="showEditDialog(row)">编辑</button>
                                    <button v-if="row.status !== 1" class="btn search-btn"
                                        @click="handlePublish(row)">发布</button>
                                    <button v-if="row.status === 1" class="btn import-btn"
                                        @click="handleOffline(row)">下架</button>
                                    <button class="btn details-btn" @click="handleTop(row)">
                                        {{ row.isTop === 1 ? '取消置顶' : '置顶' }}
                                    </button>
                                    <button class="btn details-btn" @click="showStatDialog(row)">统计</button>
                                    <button class="btn delete-btn" @click="handleDelete(row.id)">删除</button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <!-- 分页器 -->
            <div class="block">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="total">
                </el-pagination>
            </div>

            <!-- 新建/编辑弹窗 -->
            <el-dialog v-model="showDialog" :title="isEditing ? '编辑公告' : '新建公告'" width="760px" top="4vh"
                :close-on-click-modal="false" destroy-on-close>
                <el-form :model="form" label-width="100px">
                    <el-form-item label="公告标题" required>
                        <el-input v-model="form.title" maxlength="200" show-word-limit placeholder="请输入公告标题" />
                    </el-form-item>
                    <el-form-item label="公告摘要">
                        <el-input v-model="form.summary" type="textarea" :rows="2" maxlength="500" show-word-limit
                            placeholder="列表/弹窗摘要（可留空）" />
                    </el-form-item>
                    <el-row :gutter="12">
                        <el-col :span="8">
                            <el-form-item label="类型">
                                <el-select v-model="form.type">
                                    <el-option v-for="t in TYPE_OPTIONS" :key="t.value" :label="t.label"
                                        :value="t.value" />
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="级别">
                                <el-select v-model="form.level">
                                    <el-option label="普通" :value="1" />
                                    <el-option label="重要" :value="2" />
                                    <el-option label="紧急" :value="3" />
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="展示方式">
                                <el-select v-model="form.displayMode">
                                    <el-option label="仅列表" :value="1" />
                                    <el-option label="弹窗" :value="2" />
                                    <el-option label="轮播" :value="3" />
                                    <el-option label="顶部横幅" :value="4" />
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="12">
                        <el-col :span="8">
                            <el-form-item label="目标人群">
                                <el-select v-model="form.targetAudience">
                                    <el-option label="全体" :value="1" />
                                    <el-option label="登录用户" :value="2" />
                                    <el-option label="新用户" :value="3" />
                                    <el-option label="指定用户" :value="4" />
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="排序权重">
                                <el-input-number v-model="form.sortWeight" :min="0" :max="9999" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item label="是否置顶">
                                <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item v-if="form.targetAudience === 4" label="定向用户ID">
                        <el-input v-model="targetUserIdsText" placeholder="多个用户ID用英文逗号分隔，如 1,2,3" />
                    </el-form-item>
                    <el-row :gutter="12">
                        <el-col :span="12">
                            <el-form-item label="定时发布">
                                <el-date-picker v-model="form.publishTime" type="datetime"
                                    placeholder="留空且勾选立即发布则为立即发布" value-format="YYYY-MM-DDTHH:mm:ss"
                                    style="width: 100%" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="过期时间">
                                <el-date-picker v-model="form.expireTime" type="datetime" placeholder="留空=永不过期"
                                    value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item label="封面图URL">
                        <el-input v-model="form.coverImage" placeholder="https://..." />
                    </el-form-item>
                    <el-form-item label="公告内容" required>
                        <div class="rich-editor">
                            <div class="editor-toolbar">
                                <button type="button" title="加粗" @click="exec('bold')"><b>B</b></button>
                                <button type="button" title="斜体" @click="exec('italic')"><i>I</i></button>
                                <button type="button" title="下划线" @click="exec('underline')"><u>U</u></button>
                                <button type="button" title="标题" @click="exec('formatBlock', 'h2')">H2</button>
                                <button type="button" title="正文" @click="exec('formatBlock', 'p')">P</button>
                                <button type="button" title="无序列表" @click="exec('insertUnorderedList')">•≡</button>
                                <button type="button" title="有序列表" @click="exec('insertOrderedList')">1≡</button>
                                <button type="button" title="插入链接" @click="insertLink">🔗</button>
                                <button type="button" title="插入图片" @click="insertImage">🖼</button>
                                <button type="button" title="清除格式" @click="exec('removeFormat')">✕</button>
                                <button type="button" title="HTML 源码" @click="toggleSource"
                                    :class="{ active: sourceMode }">{ }</button>
                            </div>
                            <textarea v-if="sourceMode" v-model="sourceContent" class="editor-source"
                                spellcheck="false" />
                            <div v-else ref="editorRef" class="editor-body" contenteditable="true"
                                @input="onEditorInput" />
                        </div>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="showDialog = false">取消</el-button>
                    <el-button @click="submit(0)">存为草稿</el-button>
                    <el-button type="primary" @click="submit(1)">
                        {{ futurePublishTime ? '定时发布' : '立即发布' }}
                    </el-button>
                </template>
            </el-dialog>

            <!-- 阅读统计弹窗 -->
            <el-dialog v-model="showStat" title="阅读统计" width="420px">
                <el-descriptions :column="1" border v-if="stat">
                    <el-descriptions-item label="公告标题">{{ stat.title }}</el-descriptions-item>
                    <el-descriptions-item label="浏览量">{{ stat.viewCount }}</el-descriptions-item>
                    <el-descriptions-item label="已读数">{{ stat.readCount }}</el-descriptions-item>
                    <el-descriptions-item label="阅读率">{{ stat.readRate }}%</el-descriptions-item>
                </el-descriptions>
            </el-dialog>

            <!-- 删除提示框组件 -->
            <DeleteConfirmation v-if="isDeletePromptVisible" @close="closeDeletePrompt" @confirm="confirmDelete" />
            <!-- 自定义提示框组件 -->
            <ToastType v-if="showToast" :toastMessage="toastMessage" :toastType="toastType" />
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import {
    adminPageAnnouncements, adminSaveAnnouncement, adminUpdateAnnouncement,
    adminDeleteAnnouncement, adminPublishAnnouncement, adminOfflineAnnouncement,
    adminTopAnnouncement, adminReadStat
} from '@/api/announcement';
import DeleteConfirmation from '@/components/PromptComponent/DeleteConfirmation.vue';
import ToastType from '@/components/PromptComponent/ToastType.vue';

const TYPE_OPTIONS = [
    { value: 1, label: '系统公告' },
    { value: 2, label: '活动通知' },
    { value: 3, label: '维护通知' },
    { value: 4, label: '版本更新' }
];

const rows = ref([]);
const loading = ref(false);
const searchKeyword = ref('');
const filterStatus = ref(null);
const filterType = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const showDialog = ref(false);
const isEditing = ref(false);
const showStat = ref(false);
const stat = ref(null);
const editorRef = ref(null);
const sourceMode = ref(false);
const sourceContent = ref('');
const targetUserIdsText = ref('');

const emptyForm = () => ({
    id: null,
    title: '',
    summary: '',
    content: '',
    type: 1,
    level: 1,
    displayMode: 1,
    targetAudience: 1,
    isTop: 0,
    sortWeight: 0,
    publishTime: null,
    expireTime: null,
    coverImage: ''
});
const form = ref(emptyForm());

const futurePublishTime = computed(() => {
    if (!form.value.publishTime) return false;
    return new Date(form.value.publishTime).getTime() > Date.now();
});

onMounted(fetchList);

async function fetchList() {
    loading.value = true;
    try {
        const res = await adminPageAnnouncements({
            page: currentPage.value,
            size: pageSize.value,
            keyword: searchKeyword.value || undefined,
            status: filterStatus.value ?? undefined,
            type: filterType.value ?? undefined
        });
        rows.value = res.data?.records || [];
        total.value = res.data?.total || 0;
    } catch (e) {
        rows.value = [];
        total.value = 0;
    } finally {
        loading.value = false;
    }
}

function handleSearch() {
    currentPage.value = 1;
    fetchList();
}
function handleFilter() {
    currentPage.value = 1;
    fetchList();
}
function handleSizeChange(size) {
    pageSize.value = size;
    currentPage.value = 1;
    fetchList();
}
function handleCurrentChange(page) {
    currentPage.value = page;
    fetchList();
}

function showAddDialog() {
    isEditing.value = false;
    form.value = emptyForm();
    targetUserIdsText.value = '';
    sourceMode.value = false;
    sourceContent.value = '';
    showDialog.value = true;
    nextTick(() => {
        if (editorRef.value) editorRef.value.innerHTML = '';
    });
}

function showEditDialog(row) {
    isEditing.value = true;
    form.value = { ...emptyForm(), ...row };
    targetUserIdsText.value = '';
    sourceMode.value = false;
    sourceContent.value = row.content || '';
    showDialog.value = true;
    nextTick(() => {
        if (editorRef.value) editorRef.value.innerHTML = row.content || '';
    });
}

// ==================== 富文本编辑器 ====================
function exec(command, value = null) {
    focusEditor();
    document.execCommand(command, false, value);
    syncContent();
}
function focusEditor() {
    if (sourceMode.value) return;
    if (editorRef.value && document.activeElement !== editorRef.value) {
        editorRef.value.focus();
    }
}
function onEditorInput() {
    syncContent();
}
function syncContent() {
    if (editorRef.value) {
        form.value.content = editorRef.value.innerHTML;
    }
}
function toggleSource() {
    if (!sourceMode.value) {
        syncContent();
        sourceContent.value = form.value.content;
        sourceMode.value = true;
    } else {
        sourceMode.value = false;
        nextTick(() => {
            if (editorRef.value) editorRef.value.innerHTML = sourceContent.value;
            form.value.content = sourceContent.value;
        });
    }
}
function insertLink() {
    const url = window.prompt('请输入链接地址：', 'https://');
    if (url) exec('createLink', url);
}
function insertImage() {
    const url = window.prompt('请输入图片地址：', 'https://');
    if (url) exec('insertImage', url);
}

// ==================== 提交 ====================
function submit(status) {
    if (sourceMode.value) {
        form.value.content = sourceContent.value;
    }
    if (!form.value.title || !form.value.title.trim()) {
        showToastMessage('请填写公告标题', 'error');
        return;
    }
    if (!form.value.content || !form.value.content.replace(/<[^>]+>/g, '').trim()) {
        showToastMessage('请填写公告内容', 'error');
        return;
    }
    if (form.value.expireTime && form.value.publishTime &&
        new Date(form.value.expireTime) <= new Date(form.value.publishTime)) {
        showToastMessage('过期时间必须晚于发布时间', 'error');
        return;
    }
    const data = {
        ...form.value,
        status,
        targetUserIds: form.value.targetAudience === 4
            ? targetUserIdsText.value.split(',').map(s => s.trim()).filter(s => s && !isNaN(s)).map(Number)
            : undefined
    };
    const action = async () => {
        if (isEditing.value) {
            await adminUpdateAnnouncement(form.value.id, data);
            showToastMessage(isPublishAction(status) ? '更新并发布成功' : '更新成功');
        } else {
            await adminSaveAnnouncement(data);
            showToastMessage(futurePublishTime.value ? '定时发布已保存' : (isPublishAction(status) ? '发布成功' : '草稿已保存'));
        }
        showDialog.value = false;
        await fetchList();
    };
    action().catch((e) => showToastMessage(e?.response?.data?.message || '操作失败', 'error'));
}
function isPublishAction(status) {
    return status === 1 && !futurePublishTime.value;
}

// ==================== 操作 ====================
async function handlePublish(row) {
    try {
        await adminPublishAnnouncement(row.id);
        showToastMessage('发布成功');
        await fetchList();
    } catch (e) {
        showToastMessage(e?.response?.data?.message || '发布失败', 'error');
    }
}
async function handleOffline(row) {
    try {
        await adminOfflineAnnouncement(row.id);
        showToastMessage('下架成功');
        await fetchList();
    } catch (e) {
        showToastMessage(e?.response?.data?.message || '下架失败', 'error');
    }
}
async function handleTop(row) {
    try {
        await adminTopAnnouncement(row.id, row.isTop === 1 ? 0 : 1);
        showToastMessage(row.isTop === 1 ? '已取消置顶' : '置顶成功');
        await fetchList();
    } catch (e) {
        showToastMessage(e?.response?.data?.message || '操作失败', 'error');
    }
}
async function showStatDialog(row) {
    try {
        const res = await adminReadStat(row.id);
        stat.value = res.data;
        showStat.value = true;
    } catch (e) {
        showToastMessage('获取统计失败', 'error');
    }
}

// ==================== 删除 ====================
const isDeletePromptVisible = ref(false);
const deleteId = ref(null);
function handleDelete(id) {
    deleteId.value = id;
    isDeletePromptVisible.value = true;
}
function closeDeletePrompt() {
    isDeletePromptVisible.value = false;
    deleteId.value = null;
}
async function confirmDelete() {
    if (!deleteId.value) return;
    try {
        await adminDeleteAnnouncement(deleteId.value);
        showToastMessage('删除成功');
        await fetchList();
    } catch (e) {
        showToastMessage('删除失败', 'error');
    } finally {
        closeDeletePrompt();
    }
}

// ==================== 工具 ====================
const showToast = ref(false);
const toastMessage = ref('');
const toastType = ref('success');
function showToastMessage(message, type = 'success') {
    toastMessage.value = message;
    toastType.value = type;
    showToast.value = true;
    setTimeout(() => { showToast.value = false; }, 3000);
}

function statusText(s) {
    return { 0: '草稿', 1: '已发布', 2: '已下架' }[s] ?? '未知';
}
function typeText(t) {
    return TYPE_OPTIONS.find(o => o.value === t)?.label || '公告';
}
function levelText(l) {
    return { 1: '普通', 2: '重要', 3: '紧急' }[l] || '普通';
}
function displayModeText(m) {
    return { 1: '仅列表', 2: '弹窗', 3: '轮播', 4: '横幅' }[m] || '仅列表';
}
function formatDateTime(t) {
    if (!t) return '—';
    const d = new Date(t);
    return isNaN(d.getTime()) ? '—' : d.toLocaleString('zh-CN', { hour12: false });
}
</script>

<style scoped>
@import '@/css/Management/BackgroundManagement.css';

.title-cell {
    max-width: 220px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.top-badge {
    display: inline-block;
    margin-right: 4px;
    padding: 1px 5px;
    border-radius: 4px;
    background: linear-gradient(135deg, #f97316, #ef4444);
    color: #fff;
    font-size: 12px;
}
.status-tag,
.level-tag {
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
}
.status-0 { background: #f0f2f5; color: #909399; }
.status-1 { background: #e8f7ee; color: #22a35c; }
.status-2 { background: #fdeaea; color: #dd4b4b; }
.level-1 { background: #f0f2f5; color: #909399; }
.level-2 { background: #fff6e5; color: #e68a00; }
.level-3 { background: #fdeaea; color: #dd4b4b; }

/* 富文本编辑器 */
.rich-editor {
    width: 100%;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    overflow: hidden;
}
.editor-toolbar {
    display: flex;
    gap: 4px;
    padding: 6px 8px;
    background: #f5f7fa;
    border-bottom: 1px solid #e4e7ed;
    flex-wrap: wrap;
}
.editor-toolbar button {
    min-width: 30px;
    height: 28px;
    border: 1px solid transparent;
    border-radius: 4px;
    background: transparent;
    cursor: pointer;
    font-size: 13px;
    color: #444;
}
.editor-toolbar button:hover {
    background: #e8ebf0;
}
.editor-toolbar button.active {
    border-color: #4a7cf7;
    color: #4a7cf7;
}
.editor-body {
    min-height: 220px;
    max-height: 360px;
    overflow-y: auto;
    padding: 12px 14px;
    font-size: 14px;
    line-height: 1.8;
    outline: none;
    background: #fff;
}
.editor-body:empty::before {
    content: '请输入公告内容...';
    color: #bbb;
}
.editor-source {
    width: 100%;
    min-height: 220px;
    max-height: 360px;
    border: none;
    padding: 12px 14px;
    font-family: Consolas, Monaco, monospace;
    font-size: 13px;
    line-height: 1.6;
    outline: none;
    resize: vertical;
    box-sizing: border-box;
}
</style>
