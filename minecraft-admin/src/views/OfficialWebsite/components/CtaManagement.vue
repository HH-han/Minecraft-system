<template>
  <div class="management-page-background">
    <div class="container-management">
      <div class="action-bar">
        <button class="btn edit-btn" @click="showEditDialog(ctaData)" v-if="ctaData">编辑行动号召</button>
        <button class="btn add-btn" @click="showAddDialog" v-else>新增行动号召</button>
      </div>

      <div class="data-table-container">
        <div class="data-table-wrapper">
          <table class="data-table">
            <thead>
              <tr>
                <th v-for="col in columns" :key="col.key">{{ col.title }}</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="ctaData">
                <td>{{ ctaData.id }}</td>
                <td>{{ ctaData.title }}</td>
                <td>{{ ctaData.description }}</td>
                <td>{{ ctaData.btnText }}</td>
                <td>{{ ctaData.btnUrl }}</td>
                <td>
                  <div class="color-preview" :style="{ backgroundColor: ctaData.bgColor }"></div>
                </td>
                <td>{{ ctaData.isActive ? '启用' : '禁用' }}</td>
                <td>{{ formatDate(ctaData.createdAt) }}</td>
                <td class="table-btn-display">
                  <button class="btn details-btn" @click="showDetailsDialog(ctaData)">详情</button>
                  <button class="btn edit-btn" @click="showEditDialog(ctaData)">编辑</button>
                </td>
              </tr>
              <tr v-else>
                <td colspan="9" style="text-align: center;">暂无数据</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <FormDialog
        v-model:visible="showDialog"
        title="行动号召"
        :isEdit="isEditing"
        :fields="formFields"
        :initialData="formData"
        :validateFn="validateForm"
        :submitFn="handleSubmit"
        @error="handleError"
      />

      <div v-if="showDetails" class="dialog-overlay" @click.self="closeDetailsDialog">
        <div class="dialog" @click.stop>
          <h2>行动号召详情</h2>
          <div class="details-container">
            <div class="detail-item">
              <label>标题:</label>
              <span>{{ selectedItem?.title }}</span>
            </div>
            <div class="detail-item">
              <label>描述:</label>
              <span>{{ selectedItem?.description }}</span>
            </div>
            <div class="detail-item">
              <label>按钮文本:</label>
              <span>{{ selectedItem?.btnText }}</span>
            </div>
            <div class="detail-item">
              <label>按钮链接:</label>
              <span>{{ selectedItem?.btnUrl }}</span>
            </div>
            <div class="detail-item">
              <label>背景颜色:</label>
              <div class="color-preview" :style="{ backgroundColor: selectedItem?.bgColor }"></div>
              <span>{{ selectedItem?.bgColor }}</span>
            </div>
            <div class