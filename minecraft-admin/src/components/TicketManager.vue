<template>
  <div class="ticket-manager">
    <label>{{ label }}:</label>
    <div class="ticket-list">
      <div v-for="(ticket, index) in localTickets" :key="ticket.id || index" class="ticket-item">
        <div class="ticket-header">
          <span>{{ title }} {{ index + 1 }}</span>
          <button type="button" class="btn remove-ticket-btn" @click="removeTicket(index, ticket)">删除</button>
        </div>
        <div class="ticket-form">
          <input v-model="ticket.name" placeholder="名称" class="ticket-input" />
          <input v-model="ticket.description" placeholder="描述" class="ticket-input" />
          <input v-model="ticket.price" placeholder="价格" type="number" step="0.01" class="ticket-input" />
        </div>
      </div>
      <button type="button" class="btn add-ticket-btn" @click="addTicket">添加{{ title }}</button>
      <button type="button" class="btn save-ticket-btn" @click="emitUpdate">保存{{ title }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { deleteAttractionTicket } from '@/api/attractionTicket';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  label: {
    type: String,
    default: '门票列表'
  },
  title: {
    type: String,
    default: '门票'
  }
});

const emit = defineEmits(['update:modelValue']);

const localTickets = ref((props.modelValue && props.modelValue.length > 0) ? [...props.modelValue] : [{
  id: null,
  name: '',
  description: '',
  price: '',
  rules: []
}]);

watch(() => props.modelValue, (newVal) => {
  const newValue = (newVal && newVal.length > 0) ? [...newVal] : [{
    id: null,
    name: '',
    description: '',
    price: '',
    rules: []
  }];
  
  if (JSON.stringify(localTickets.value) !== JSON.stringify(newValue)) {
    localTickets.value = newValue;
  }
});

const emitUpdate = () => {
  emit('update:modelValue', [...localTickets.value]);
};

const addTicket = () => {
  localTickets.value.push({
    id: null,
    name: '',
    description: '',
    price: '',
    rules: []
  });
};

const removeTicket = async (index, ticket) => {
  console.log('删除门票被点击');
  console.log('Index:', index);
  console.log('Ticket:', ticket);
  console.log('当前门票列表:', localTickets.value);
  
  if (localTickets.value.length <= 1) {
    ElMessage.warning('至少保留一个门票');
    return;
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除"${ticket.name || '该门票'}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    // 如果有 id 就调用删除 API
    if (ticket.id !== null && ticket.id !== undefined && ticket.id !== '') {
      console.log('调用删除 API, ID:', ticket.id);
      await deleteAttractionTicket(ticket.id);
      console.log('删除 API 调用成功');
      ElMessage.success('删除成功');
    }
    
    // 从本地数组中移除
    localTickets.value.splice(index, 1);
    
    // 通知父组件更新
    emitUpdate();
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error);
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message || '未知错误'));
    }
  }
};
</script>

<style scoped>
.ticket-manager {
  width: 100%;
}

.ticket-manager label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.ticket-list {
  margin-top: 8px;
}

.ticket-item {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 12px;
  margin-bottom: 8px;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-weight: bold;
}

.ticket-form {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ticket-input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.add-ticket-btn, .remove-ticket-btn, .save-ticket-btn {
  padding: 6px 12px;
  font-size: 14px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-ticket-btn {
  background-color: #1976d2;
  color: white;
  margin-top: 8px;
  margin-right: 8px;
}

.add-ticket-btn:hover {
  background-color: #1565c0;
}

.remove-ticket-btn {
  background-color: #ef5350;
  color: white;
}

.remove-ticket-btn:hover {
  background-color: #e53935;
}

.save-ticket-btn {
  background-color: #4caf50;
  color: white;
  margin-top: 8px;
}

.save-ticket-btn:hover {
  background-color: #43a047;
}
</style>
