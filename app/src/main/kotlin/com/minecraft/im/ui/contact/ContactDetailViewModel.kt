package com.minecraft.im.ui.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.repository.ImRepository
import com.minecraft.im.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    private val repository: ImRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    data class UiState(
        val contact: Contact? = null,
        val members: List<Pair<Long, String>> = emptyList(),
        val loading: Boolean = true,
        val error: String? = null,
        val currentUserId: Long = 0L
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    val starred = sessionManager.starred

    fun load(contactId: Long, isGroup: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val currentUserId = sessionManager.currentUserId
            var contact = repository.findContact(contactId, isGroup)
            if (contact == null) {
                if (isGroup) {
                    contact = repository.getGroup(contactId).getOrNull()
                } else {
                    repository.refreshFriends()
                    contact = repository.findContact(contactId, false)
                }
            }
            val members = if (isGroup) {
                repository.getGroupMembers(contactId).getOrDefault(emptyList())
            } else {
                emptyList()
            }
            if (contact == null) {
                _uiState.update {
                    it.copy(loading = false, error = "联系人不存在", currentUserId = currentUserId)
                }
            } else {
                _uiState.update {
                    it.copy(
                        contact = contact,
                        members = members,
                        loading = false,
                        currentUserId = currentUserId
                    )
                }
            }
        }
    }

    fun saveRemark(friendId: Long, remark: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateRemark(friendId, remark)
            result.onSuccess {
                _uiState.update { state ->
                    state.copy(contact = state.contact?.copy(name = remark.ifBlank { state.contact.name }))
                }
            }
            onResult(result)
        }
    }

    fun deleteFriend(friendId: Long, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch { onResult(repository.deleteFriend(friendId)) }
    }

    fun toggleStar(friendId: Long) {
        viewModelScope.launch { sessionManager.toggleStar(friendId) }
    }

    fun errorText(error: Throwable): String = error.toMessage()
}
