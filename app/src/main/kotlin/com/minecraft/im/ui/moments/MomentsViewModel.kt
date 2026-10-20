package com.minecraft.im.ui.moments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.repository.MomentRepository
import com.minecraft.im.domain.model.MomentUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 待上传的本地图片 */
data class UploadImage(
    val bytes: ByteArray,
    val mime: String,
    val name: String
) {
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = System.identityHashCode(this)
}

@HiltViewModel
class MomentsViewModel @Inject constructor(
    private val repository: MomentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    data class UiState(
        val moments: List<MomentUi> = emptyList(),
        val loading: Boolean = false,
        val loadingMore: Boolean = false,
        val page: Int = 0,
        val end: Boolean = false,
        val error: String? = null,
        val currentUserId: Long = 0L,
        val currentUserName: String = "",
        val currentUserAvatar: String? = null
    )

    private val _uiState = MutableStateFlow(
        UiState(
            currentUserId = sessionManager.currentUserId,
            currentUserName = sessionManager.currentUser?.nickname
                ?: sessionManager.currentUser?.username.orEmpty(),
            currentUserAvatar = sessionManager.currentUser?.avatar
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        if (_uiState.value.loading) return
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            repository.feed(page = 1)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            moments = list,
                            page = 1,
                            end = list.size < PAGE_SIZE
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(loading = false, error = error.toMessage()) }
                }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.loading || state.loadingMore || state.end) return
        viewModelScope.launch {
            _uiState.update { it.copy(loadingMore = true) }
            repository.feed(page = state.page + 1)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            loadingMore = false,
                            moments = it.moments + list,
                            page = it.page + 1,
                            end = list.size < PAGE_SIZE
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state -> state.copy(loadingMore = false) }
                }
        }
    }

    fun publish(
        content: String,
        images: List<UploadImage>,
        location: String,
        visibility: Int,
        onDone: (String?) -> Unit
    ) {
        if (content.isBlank() && images.isEmpty()) {
            onDone("请输入内容或选择图片")
            return
        }
        viewModelScope.launch {
            val urls = mutableListOf<String>()
            for (image in images) {
                val result = repository.uploadImage(image.bytes, image.name, image.mime)
                result.onFailure { error ->
                    onDone(error.toMessage())
                    return@launch
                }
                urls += result.getOrNull().orEmpty()
            }
            repository.publish(content.trim(), urls, location.trim(), visibility)
                .onSuccess {
                    onDone(null)
                    refresh()
                }
                .onFailure { onDone(it.toMessage()) }
        }
    }

    fun toggleLike(moment: MomentUi) {
        viewModelScope.launch {
            repository.toggleLike(moment.id)
                .onSuccess { result ->
                    _uiState.update { state ->
                        state.copy(
                            moments = state.moments.map {
                                if (it.id == moment.id) {
                                    it.copy(liked = result.liked, likeCount = result.likeCount)
                                } else {
                                    it
                                }
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.toMessage()) }
                }
        }
    }

    fun deleteMoment(momentId: Long, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            repository.delete(momentId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(moments = state.moments.filterNot { it.id == momentId })
                    }
                    onDone(null)
                }
                .onFailure { onDone(it.toMessage()) }
        }
    }

    fun addComment(momentId: Long, content: String, replyToUserId: Long?, onDone: (String?) -> Unit) {
        if (content.isBlank()) return
        viewModelScope.launch {
            repository.addComment(momentId, content.trim(), replyToUserId)
                .onSuccess {
                    onDone(null)
                    refresh()
                }
                .onFailure { onDone(it.toMessage()) }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    private companion object {
        const val PAGE_SIZE = 10
    }
}
