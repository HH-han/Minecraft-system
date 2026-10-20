package com.minecraft.im.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.SessionState
import com.minecraft.im.data.repository.ImRepository
import com.minecraft.im.data.remote.dto.UserDto
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.domain.model.MessageUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ImRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val conversationKey = MutableStateFlow("")

    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val messages: StateFlow<List<MessageUi>> =
        combine(repository.messages, conversationKey) { map, key -> map[key].orEmpty() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val starred = sessionManager.starred

    val currentUser: StateFlow<UserDto?> = sessionManager.session
        .map { (it as? SessionState.LoggedIn)?.user }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    data class UiState(
        val input: String = "",
        val loading: Boolean = false,
        val sending: Boolean = false,
        val error: String? = null
    )

    fun load(contactId: Long, isGroup: Boolean) {
        if (_contact.value?.id == contactId && _contact.value?.isGroup == isGroup) return
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            var found = repository.findContact(contactId, isGroup)
            if (found == null) {
                if (isGroup) {
                    found = repository.getGroup(contactId).getOrNull()
                } else {
                    repository.refreshFriends()
                    found = repository.findContact(contactId, false)
                }
            }
            _contact.value = found
            if (found == null) {
                _uiState.update { it.copy(loading = false, error = "会话不存在") }
                return@launch
            }
            conversationKey.value = found.key
            repository.openConversation(found)
            repository.loadHistory(found)
                .onFailure { error -> _uiState.update { it.copy(error = error.toMessage()) } }
            _uiState.update { it.copy(loading = false) }
        }
    }

    fun onInputChange(value: String) = _uiState.update { it.copy(input = value, error = null) }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun toggleStar(friendId: Long) {
        viewModelScope.launch { sessionManager.toggleStar(friendId) }
    }

    fun send() {
        val text = _uiState.value.input.trim()
        val contact = _contact.value ?: return
        if (text.isEmpty() || _uiState.value.sending) return
        viewModelScope.launch {
            _uiState.update { it.copy(sending = true, error = null) }
            repository.sendText(contact, text)
                .onSuccess { _uiState.update { it.copy(input = "") } }
                .onFailure { error -> _uiState.update { it.copy(error = error.toMessage()) } }
            _uiState.update { it.copy(sending = false) }
        }
    }

    fun sendEmoji(emoji: String) {
        val contact = _contact.value ?: return
        viewModelScope.launch {
            repository.sendEmoji(contact, emoji)
                .onFailure { error -> _uiState.update { it.copy(error = error.toMessage()) } }
        }
    }

    fun sendImage(bytes: ByteArray, mime: String, fileName: String) {
        val contact = _contact.value ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(sending = true, error = null) }
            repository.sendImage(contact, bytes, fileName, mime)
                .onFailure { error -> _uiState.update { it.copy(error = error.toMessage()) } }
            _uiState.update { it.copy(sending = false) }
        }
    }

    override fun onCleared() {
        repository.closeConversation(conversationKey.value)
        super.onCleared()
    }
}
