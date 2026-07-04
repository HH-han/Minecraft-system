import { ref, computed } from 'vue'

export function usePagination(initialPage = 1, initialPageSize = 10) {
  const currentPage = ref(initialPage)
  const pageSize = ref(initialPageSize)
  const total = ref(0)
  const totalPages = ref(0)
  const isLoading = ref(false)
  const data = ref([])

  const hasNextPage = computed(() => currentPage.value < totalPages.value)
  const hasPrevPage = computed(() => currentPage.value > 1)
  const startIndex = computed(() => (currentPage.value - 1) * pageSize.value + 1)
  const endIndex = computed(() => Math.min(currentPage.value * pageSize.value, total.value))

  function setTotal(newTotal) {
    total.value = newTotal
    totalPages.value = Math.ceil(newTotal / pageSize.value)
  }

  function setData(newData) {
    data.value = newData
  }

  function nextPage() {
    if (hasNextPage.value) {
      currentPage.value++
    }
  }

  function prevPage() {
    if (hasPrevPage.value) {
      currentPage.value--
    }
  }

  function goToPage(page) {
    if (page >= 1 && page <= totalPages.value) {
      currentPage.value = page
    }
  }

  function setPageSize(size) {
    pageSize.value = size
    currentPage.value = 1
    totalPages.value = Math.ceil(total.value / size)
  }

  function reset() {
    currentPage.value = initialPage
    pageSize.value = initialPageSize
    total.value = 0
    totalPages.value = 0
    data.value = []
    isLoading.value = false
  }

  function getQueryParams() {
    return {
      page: currentPage.value,
      pageSize: pageSize.value
    }
  }

  return {
    currentPage,
    pageSize,
    total,
    totalPages,
    isLoading,
    data,
    hasNextPage,
    hasPrevPage,
    startIndex,
    endIndex,
    setTotal,
    setData,
    nextPage,
    prevPage,
    goToPage,
    setPageSize,
    reset,
    getQueryParams
  }
}