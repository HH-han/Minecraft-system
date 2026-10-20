package com.minecraft.im.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.remote.dto.UserDto
import com.minecraft.im.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    data class UiState(
        val user: UserDto = UserDto(),
        val loading: Boolean = false,
        val saving: Boolean = false,
        val uploading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState(user = sessionManager.currentUser ?: UserDto()))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            authRepository.refreshProfile()
                .onSuccess { user -> _uiState.update { it.copy(user = user, loading = false) } }
                .onFailure { _uiState.update { it.copy(loading = false) } }
        }
    }

    fun update(transform: (UserDto) -> UserDto) {
        _uiState.update { it.copy(user = transform(it.user), error = null) }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun save(onDone: (String?) -> Unit) {
        val state = _uiState.value
        if (state.saving) return
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, error = null) }
            authRepository.updateProfile(state.user)
                .onSuccess { updated ->
                    _uiState.update { it.copy(user = updated, saving = false) }
                    onDone(null)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(saving = false, error = error.toMessage()) }
                    onDone(error.toMessage())
                }
        }
    }

    fun uploadAvatar(bytes: ByteArray, mime: String, fileName: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(uploading = true) }
            authRepository.uploadAvatar(bytes, fileName, mime)
                .onSuccess { url ->
                    _uiState.update {
                        it.copy(user = it.user.copy(avatar = url), uploading = false)
                    }
                    onDone(null)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(uploading = false) }
                    onDone(error.toMessage())
                }
        }
    }
}
