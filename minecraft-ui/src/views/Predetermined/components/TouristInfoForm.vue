<template>
  <div class="tourist-info-form">
    <h5>{{ ticketName }} ({{ quantity }}人)</h5>
    <div class="tourist-form" v-for="i in quantity" :key="i">
      <div class="form-row">
        <div class="form-group half">
          <label>游客{{ i }}姓名</label>
          <input 
            type="text" 
            v-model="tourists[i-1].name" 
            placeholder="请输入游客姓名"
            @input="updateTouristInfo"
          >
        </div>
        <div class="form-group half">
          <label>游客{{ i }}身份证号</label>
          <input 
            type="text" 
            v-model="tourists[i-1].idCard" 
            placeholder="请输入身份证号"
            @input="updateTouristInfo"
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    ticketName: {
      type: String,
      required: true
    },
    quantity: {
      type: Number,
      default: 0
    },
    initialTourists: {
      type: Array,
      default: () => []
    }
  },
  emits: ['touristInfoChange'],
  data() {
    return {
      tourists: []
    }
  },
  watch: {
    quantity: {
      handler(newQuantity) {
        this.updateTouristsArray(newQuantity)
      },
      immediate: true
    },
    initialTourists: {
      handler(newTourists) {
        if (newTourists.length > 0) {
          this.tourists = [...newTourists]
        }
      },
      immediate: true
    }
  },
  methods: {
    updateTouristsArray(quantity) {
      const newTourists = []
      for (let i = 0; i < quantity; i++) {
        newTourists.push({
          name: this.tourists[i]?.name || '',
          idCard: this.tourists[i]?.idCard || ''
        })
      }
      this.tourists = newTourists
      this.updateTouristInfo()
    },
    updateTouristInfo() {
      this.$emit('touristInfoChange', this.tourists)
    }
  }
}
</script>

<style scoped>
.tourist-info-form {
  margin-top: 25px;
  padding-top: 25px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}

.tourist-info-form h5 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.tourist-form {
  background: rgba(255, 255, 255, 0.3);
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 15px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group.half {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #ff6a00;
  box-shadow: 0 0 0 3px rgba(255, 106, 0, 0.1);
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }
  
  .form-group.half {
    width: 100%;
  }
}
</style>