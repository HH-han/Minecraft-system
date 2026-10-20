package com.minecraft.im.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.R
import com.minecraft.im.core.toMessage
import com.minecraft.im.data.repository.ImRepository
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.TopBarBack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val repository: ImRepository
) : ViewModel() {

    val friends = repository.friends

    data class UiState(
        val name: String = "",
        val description: String = "",
        val selected: Set<Long> = emptySet(),
        val loading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { repository.refreshFriends() }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, error = null) }

    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }

    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun toggle(id: Long) = _uiState.update { state ->
        val next = state.selected.toMutableSet()
        if (!next.add(id)) next.remove(id)
        state.copy(selected = next)
    }

    fun create(onSuccess: (Contact) -> Unit) {
        val state = _uiState.value
        if (state.loading) return
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "群名称不能为空") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            repository.createGroup(state.name.trim(), state.description.trim(), state.selected.toList())
                .onSuccess {
                    _uiState.update { it.copy(loading = false) }
                    onSuccess(it)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(loading = false, error = error.toMessage()) }
                }
        }
    }
}

@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    onCreated: (Contact) -> Unit,
    viewModel: CreateGroupViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    Scaffold(
        topBar = { TopBarBack(title = stringResource(R.string.create_group_title), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.create_group_name)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(R.string.create_group_desc)) },
                maxLines = 2,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.create_group_members),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.create_group_selected, state.selected.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(friends, key = { it.key }) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggle(friend.id) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = friend.id in state.selected,
                            onCheckedChange = { viewModel.toggle(friend.id) }
                        )
                        Spacer(Modifier.width(8.dp))
                        ImAvatar(name = friend.name, avatar = friend.avatar, size = 40.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(text = friend.name, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Button(
                onClick = { viewModel.create(onCreated) },
                enabled = !state.loading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.create_group_submit))
            }
        }
    }
}
