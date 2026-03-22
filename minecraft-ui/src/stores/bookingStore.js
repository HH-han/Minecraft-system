// 使用 Pinia 管理预订相关数据
import { defineStore } from 'pinia'

export const useBookingStore = defineStore('booking', {
  state: () => ({
    hotelData: null,
    attractionData: null,
    activeTab: 'hotel'
  }),
  
  actions: {
    bookHotel(hotelData) {
      this.hotelData = hotelData
      this.activeTab = 'hotel'
    },
    
    bookAttraction(attractionData) {
      this.attractionData = attractionData
      this.activeTab = 'attraction'
    },
    
    clearData() {
      this.hotelData = null
      this.attractionData = null
    }
  },
  
  getters: {
    getHotelData: (state) => state.hotelData,
    getAttractionData: (state) => state.attractionData,
    getActiveTab: (state) => state.activeTab
  }
})
