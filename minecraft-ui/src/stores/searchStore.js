import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useSearchStore = defineStore('search', () => {
  const searchHistory = ref([])
  const currentQuery = ref('')
  const recentSearches = ref([])
  const searchResults = ref([])
  const isSearching = ref(false)
  const maxHistoryLength = 10

  const topSearches = computed(() => {
    const counts = {}
    searchHistory.value.forEach(query => {
      counts[query] = (counts[query] || 0) + 1
    })
    return Object.entries(counts)
      .sort((a, b) => b[1] - a[1])
      .map(([query]) => query)
      .slice(0, 5)
  })

  function setQuery(query) {
    currentQuery.value = query
  }

  function addToHistory(query) {
    if (!query.trim()) return
    
    const index = searchHistory.value.indexOf(query)
    if (index !== -1) {
      searchHistory.value.splice(index, 1)
    }
    
    searchHistory.value.unshift(query)
    
    if (searchHistory.value.length > maxHistoryLength) {
      searchHistory.value = searchHistory.value.slice(0, maxHistoryLength)
    }
  }

  function removeFromHistory(query) {
    const index = searchHistory.value.indexOf(query)
    if (index !== -1) {
      searchHistory.value.splice(index, 1)
    }
  }

  function clearHistory() {
    searchHistory.value = []
  }

  function setResults(results) {
    searchResults.value = results
  }

  function setSearching(isSearchingValue) {
    isSearching.value = isSearchingValue
  }

  function addRecentSearch(query) {
    if (!query.trim()) return
    
    const index = recentSearches.value.indexOf(query)
    if (index !== -1) {
      recentSearches.value.splice(index, 1)
    }
    
    recentSearches.value.unshift(query)
    
    if (recentSearches.value.length > 5) {
      recentSearches.value = recentSearches.value.slice(0, 5)
    }
  }

  return {
    searchHistory,
    currentQuery,
    recentSearches,
    searchResults,
    isSearching,
    topSearches,
    setQuery,
    addToHistory,
    removeFromHistory,
    clearHistory,
    setResults,
    setSearching,
    addRecentSearch
  }
}, {
  persist: true
})