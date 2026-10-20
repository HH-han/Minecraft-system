package com.minecraft.im.ui.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.minecraft.im.ui.components.HorizontalDivider
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.LoadingScreen
import com.minecraft.im.ui.components.SectionCard
import com.minecraft.im.ui.components.TopBarBack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupInfoViewModel @Inject constructor(
    private val repository: ImRepository
) : ViewModel() {

    data class UiState(
        val group: Contact? = null,
        val members: List<Pair<Long, String>> = emptyList(),
        val name: String = "",
        val description: String = "",
        val loading: Boolean = true,
        val saving: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    val friends = repository.friends

    fun load(groupId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            val group = repository.getGroup(groupId).getOrNull()
            val members = repository.getGroupMembers(groupId).getOrDefault(emptyList())
            repository.refreshFriends()
            _uiState.update {
                it.copy(
                    group = group,
                    members = members,
                    name = group?.name.orEmpty(),
                    description = group?.description.orEmpty(),
                    loading = false
                )
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }

    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }

    fun save(groupId: Long, onDone: (String?) -> Unit) {
        val state = _uiState.value
        if (state.saving) return
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, error = null) }
            val result = repository.updateGroup(groupId, state.name.trim(), state.description.trim())
            _uiState.update { it.copy(saving = false) }
            onDone(result.exceptionOrNull()?.toMessage())
            if (result.isSuccess) load(groupId)
        }
    }

    fun invite(groupId: Long, friendIds: List<Long>, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.inviteToGroup(groupId, friendIds)
            onDone(result.exceptionOrNull()?.toMessage())
            if (result.isSuccess) load(groupId)
        }
    }
}

@Composable
fun GroupInfoScreen(
    groupId: Long,
    onBack: () -> Unit,
    viewModel: GroupInfoViewModel = hiltViewModel()
) {
    LaunchedEffect(groupId) { viewModel.load(groupId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showInvite by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBarBack(title = stringResource(R.string.group_info_title), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (state.loading) {
            Box(Modifier.padding(padding)) { LoadingScreen() }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImAvatar(name = state.name, avatar = state.group?.avatar, isGroup = true, size = 64.dp)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(text = state.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = stringResource(R.string.contact_member_count, state.members.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text(stringResource(R.string.group_info_name)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text(stringResource(R.string.group_info_desc)) },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.save(groupId) { error ->
                            scope.launch {
                                snackbar.showSnackbar(error ?: "OK")
                            }
                        }
                    },
                    enabled = !state.saving,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }

            SectionCard {
                Text(
                    text = stringResource(R.string.group_info_members, state.members.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                state.members.forEachIndexed { index, (_, nickname) ->
                    if (index > 0) HorizontalDivider(Modifier.padding(start = 60.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImAvatar(name = nickname, avatar = null, size = 36.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(text = nickname, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            OutlinedButton(
                onClick = { showInvite = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.group_info_invite))
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showInvite) {
        val memberIds = state.members.map { it.first }.toSet()
        val candidates = friends.filter { it.id !in memberIds }
        var selected by remember { mutableStateOf<Set<Long>>(emptySet()) }
        AlertDialog(
            onDismissRequest = { showInvite = false },
            title = { Text(stringResource(R.string.group_info_invite)) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                    items(candidates, key = { it.key }) { friend ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val next = selected.toMutableSet()
                                    if (!next.add(friend.id)) next.remove(friend.id)
                                    selected = next
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = friend.id in selected,
                                onCheckedChange = {
                                    val next = selected.toMutableSet()
                                    if (!next.add(friend.id)) next.remove(friend.id)
                                    selected = next
                                }
                            )
                            ImAvatar(name = friend.name, avatar = friend.avatar, size = 34.dp)
                            Spacer(Modifier.width(8.dp))
                            Text(friend.name, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (candidates.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.label_empty),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showInvite = false
                        if (selected.isNotEmpty()) {
                            viewModel.invite(groupId, selected.toList()) { error ->
                                scope.launch { snackbar.showSnackbar(error ?: "OK") }
                            }
                        }
                    }
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showInvite = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
