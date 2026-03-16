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
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.tourist-info-form h5 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: bold;
}

.tourist-form {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.form-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group.half {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
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