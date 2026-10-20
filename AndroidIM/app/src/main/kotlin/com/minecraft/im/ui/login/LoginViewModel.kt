package com.minecraft.im.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    data class UiState(
        val account: String = "",
        val password: String = "",
        val serverUrl: String = "",
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState(serverUrl = authRepository.currentBaseUrl))
    val uiState = _uiState.asStateFlow()

    fun onAccountChange(value: String) = _uiState.update { it.copy(account = value, error = null) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, error = null) }

    fun onServerUrlChange(value: String) = _uiState.update { it.copy(serverUrl = value, error = null) }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun submit() {
        val state = _uiState.value
        if (state.loading) return
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            authRepository.login(state.account, state.password, state.serverUrl)
                .onFailure { error ->
                    _uiState.update { it.copy(loading = false, error = error.toMessage()) }
                }
                .onSuccess {
                    _uiState.update { it.copy(loading = false) }
                }
        }
    }
}
