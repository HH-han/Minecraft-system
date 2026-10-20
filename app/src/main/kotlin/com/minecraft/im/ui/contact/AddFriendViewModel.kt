package com.minecraft.im.ui.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.repository.ImRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel @Inject constructor(
    private val repository: ImRepository
) : ViewModel() {

    data class UiState(
        val phone: String = "",
        val message: String = "",
        val loading: Boolean = false,
        val error: String? = null,
        val success: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value, error = null) }

    fun onMessageChange(value: String) = _uiState.update { it.copy(message = value) }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun submit() {
        val state = _uiState.value
        if (state.loading) return
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            repository.sendFriendRequest(state.phone.trim(), state.message.trim())
                .onSuccess { _uiState.update { it.copy(loading = false, success = true) } }
                .onFailure { error ->
                    _uiState.update { it.copy(loading = false, error = error.toMessage()) }
                }
        }
    }
}
