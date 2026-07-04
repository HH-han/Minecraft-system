import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const isLoading = ref(false)

  const totalItems = computed(() => items.value.reduce((sum, item) => sum + item.quantity, 0))

  const totalPrice = computed(() => items.value.reduce((sum, item) => sum + (item.price * item.quantity), 0))

  function addItem(item) {
    const existingItem = items.value.find(i => i.id === item.id)
    if (existingItem) {
      existingItem.quantity += item.quantity || 1
    } else {
      items.value.push({ ...item, quantity: item.quantity || 1 })
    }
  }

  function removeItem(itemId) {
    const index = items.value.findIndex(i => i.id === itemId)
    if (index !== -1) {
      items.value.splice(index, 1)
    }
  }

  function updateQuantity(itemId, quantity) {
    const item = items.value.find(i => i.id === itemId)
    if (item) {
      if (quantity <= 0) {
        removeItem(itemId)
      } else {
        item.quantity = quantity
      }
    }
  }

  function clearCart() {
    items.value = []
  }

  function getItemById(itemId) {
    return items.value.find(i => i.id === itemId)
  }

  function isInCart(itemId) {
    return items.value.some(i => i.id === itemId)
  }

  return {
    items,
    isLoading,
    totalItems,
    totalPrice,
    addItem,
    removeItem,
    updateQuantity,
    clearCart,
    getItemById,
    isInCart
  }
}, {
  persist: true
})