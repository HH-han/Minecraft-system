package com.minecraft.im.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.repository.FavoriteRepository
import com.minecraft.im.domain.model.FavoriteUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoriteRepository
) : ViewModel() {

    enum class Event { SAVED, DELETED, CLEARED }

    data class UiState(
        val items: List<FavoriteUi> = emptyList(),
        val category: String = CATEGORY_ALL,
        val keyword: String = "",
        val total: Long = 0L,
        val typeCounts: Map<String, Long> = emptyMap(),
        val loading: Boolean = false,
        val loadingMore: Boolean = false,
        val saving: Boolean = false,
        val hasMore: Boolean = true,
        val page: Int = 1,
        val error: String? = null,
        val event: Event? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var listJob: Job? = null
    private var searchJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        load(reset = true)
        loadMeta()
    }

    fun selectCategory(category: String) {
        if (category == _uiState.value.category) return
        _uiState.update { it.copy(category = category) }
        load(reset = true)
    }

    fun onKeywordChange(value: String) {
        _uiState.update { it.copy(keyword = value) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            load(reset = true)
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.loading || state.loadingMore || !state.hasMore) return
        load(reset = false)
    }

    fun save(
        type: String,
        title: String,
        content: String,
        url: String,
        tags: List<String>
    ) {
        if (_uiState.value.saving) return
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, error = null) }
            repository.add(type, title, content, url, tags)
                .onSuccess {
                    _uiState.update { state -> state.copy(saving = false, event = Event.SAVED) }
                    _uiState.update { it.copy(category = CATEGORY_ALL) }
                    load(reset = true)
                    loadMeta()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(saving = false, error = error.toMessage()) }
                }
        }
    }

    fun delete(item: FavoriteUi) {
        viewModelScope.launch {
            repository.delete(item.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            items = state.items.filterNot { it.id == item.id },
                            total = (state.total - 1).coerceAtLeast(0),
                            event = Event.DELETED
                        )
                    }
                    loadMeta()
                }
                .onFailure { error -> _uiState.update { it.copy(error = error.toMessage()) } }
        }
    }

    fun clearCategory() {
        val category = _uiState.value.category
        if (category == CATEGORY_ALL || category == CATEGORY_RECENT) return
        viewModelScope.launch {
            repository.clear(category)
                .onSuccess {
                    _uiState.update { state -> state.copy(items = emptyList(), event = Event.CLEARED) }
                    load(reset = true)
                    loadMeta()
                }
                .onFailure { error -> _uiState.update { it.copy(error = error.toMessage()) } }
        }
    }

    fun touch(item: FavoriteUi) {
        viewModelScope.launch { repository.touch(item.id) }
    }

    fun consumeEvent() = _uiState.update { it.copy(event = null) }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    private fun load(reset: Boolean) {
        listJob?.cancel()
        listJob = viewModelScope.launch {
            val state = _uiState.value
            val page = if (reset) 1 else state.page + 1
            _uiState.update {
                it.copy(loading = reset, loadingMore = !reset, error = null)
            }
            val result = if (state.category == CATEGORY_RECENT &&
                state.keyword.isBlank()
            ) {
                repository.recent()
            } else {
                repository.list(state.category, null, state.keyword, page, PAGE_SIZE)
            }
            result
                .onSuccess { list ->
                    _uiState.update { current ->
                        current.copy(
                            items = if (reset) list else current.items + list,
                            page = page,
                            hasMore = if (state.category == CATEGORY_RECENT) false
                            else list.size >= PAGE_SIZE,
                            loading = false,
                            loadingMore = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(loading = false, loadingMore = false, error = error.toMessage()) }
                }
        }
    }

    private fun loadMeta() {
        viewModelScope.launch {
            repository.stats().onSuccess { (total, types) ->
                _uiState.update { it.copy(total = total, typeCounts = types) }
            }
        }
    }

    companion object {
        const val CATEGORY_ALL = "all"
        const val CATEGORY_RECENT = "recent"
        private const val PAGE_SIZE = 20
    }
}
