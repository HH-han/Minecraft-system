package com.minecraft.im.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.SessionState
import com.minecraft.im.data.repository.AuthRepository
import com.minecraft.im.data.repository.ImRepository
import com.minecraft.im.data.remote.dto.UserDto
import com.minecraft.im.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ImRepository,
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val conversations: StateFlow<List<Contact>> = repository.conversations
    val friends: StateFlow<List<Contact>> = repository.friends
    val groups: StateFlow<List<Contact>> = repository.groups
    val requests = repository.pendingRequests
    val totalUnread = repository.totalUnread
    val starred = sessionManager.starred

    val currentUser: StateFlow<UserDto?> = sessionManager.session
        .map { (it as? SessionState.LoggedIn)?.user }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val serverUrl: String get() = sessionManager.currentBaseUrl

    fun refresh() {
        viewModelScope.launch { repository.refreshAll() }
    }

    fun openConversation(contact: Contact) {
        repository.openConversation(contact)
    }

    fun toggleStar(friendId: Long) {
        viewModelScope.launch { sessionManager.toggleStar(friendId) }
    }

    fun acceptRequest(requestId: Long, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            repository.handleFriendRequest(requestId, true)
                .onFailure { onResult(it.message) }
        }
    }

    fun rejectRequest(requestId: Long, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            repository.handleFriendRequest(requestId, false)
                .onFailure { onResult(it.message) }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
