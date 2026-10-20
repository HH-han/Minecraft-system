package com.minecraft.im.ui.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.R
import com.minecraft.im.ui.components.HorizontalDivider
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.LoadingScreen
import com.minecraft.im.ui.components.SectionCard
import com.minecraft.im.ui.components.SettingRow
import com.minecraft.im.ui.components.TopBarBack
import kotlinx.coroutines.launch

@Composable
fun ContactDetailScreen(
    contactId: Long,
    isGroup: Boolean,
    onBack: () -> Unit,
    onSendMessage: (Long, Boolean) -> Unit,
    onOpenGroupInfo: (Long) -> Unit,
    viewModel: ContactDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(contactId, isGroup) { viewModel.load(contactId, isGroup) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showRemarkDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val contact = state.contact

    Scaffold(
        topBar = {
            TopBarBack(title = stringResource(R.string.contact_detail_title), onBack = onBack)
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (state.loading) {
            Box(Modifier.padding(padding)) { LoadingScreen() }
            return@Scaffold
        }
        if (contact == null) {
            Box(Modifier.padding(padding)) {
                Text(
                    text = state.error ?: stringResource(R.string.label_empty),
                    modifier = Modifier.padding(24.dp)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // 头部
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImAvatar(
                    name = contact.name,
                    avatar = contact.avatar,
                    isGroup = contact.isGroup,
                    size = 72.dp
                )
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (contact.isGroup) {
                            stringResource(R.string.contact_member_count, contact.memberCount)
                        } else if (contact.online) {
                            stringResource(R.string.chat_online)
                        } else {
                            stringResource(R.string.chat_offline)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            SectionCard {
                SettingRow(
                    title = stringResource(R.string.contact_id),
                    subtitle = contact.id.toString()
                )
                if (!contact.isGroup) {
                    HorizontalDivider(Modifier.padding(start = 16.dp))
                    SettingRow(
                        title = stringResource(R.string.contact_phone),
                        subtitle = contact.phone?.takeIf { it.isNotBlank() } ?: "-"
                    )
                    HorizontalDivider(Modifier.padding(start = 16.dp))
                    SettingRow(
                        title = stringResource(R.string.contact_email),
                        subtitle = contact.email?.takeIf { it.isNotBlank() } ?: "-"
                    )
                } else {
                    HorizontalDivider(Modifier.padding(start = 16.dp))
                    SettingRow(
                        title = stringResource(R.string.group_info_desc),
                        subtitle = contact.description?.takeIf { it.isNotBlank() } ?: "-"
                    )
                    HorizontalDivider(Modifier.padding(start = 16.dp))
                    SettingRow(
                        title = stringResource(R.string.contact_group_creator),
                        subtitle = if (contact.creatorId == state.currentUserId) {
                            stringResource(R.string.label_me)
                        } else {
                            contact.creatorId?.toString().orEmpty()
                        }
                    )
                }
            }

            if (contact.isGroup && state.members.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                SectionCard {
                    Text(
                        text = stringResource(R.string.group_info_members, state.members.size),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    state.members.forEachIndexed { index, (userId, nickname) ->
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
                            if (userId == state.currentUserId) {
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "(${stringResource(R.string.label_me)})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onSendMessage(contact.id, contact.isGroup) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.contact_send_message))
                }
                if (contact.isGroup) {
                    OutlinedButton(
                        onClick = { onOpenGroupInfo(contact.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Group, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.contact_edit_group))
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            viewModel.toggleStar(contact.id)
                            scope.launch { snackbar.showSnackbar("★") }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.contacts_star))
                    }
                    OutlinedButton(
                        onClick = { showRemarkDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.contact_edit_remark))
                    }
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.contact_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(Modifier.height(28.dp))
        }
    }

    if (showRemarkDialog && contact != null) {
        var remark by remember { mutableStateOf(contact.remark.orEmpty()) }
        AlertDialog(
            onDismissRequest = { showRemarkDialog = false },
            title = { Text(stringResource(R.string.contact_remark_title)) },
            text = {
                OutlinedTextField(
                    value = remark,
                    onValueChange = { remark = it },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.contact_remark_hint)) }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showRemarkDialog = false
                    viewModel.saveRemark(contact.id, remark) { result ->
                        scope.launch {
                            snackbar.showSnackbar(
                                result.exceptionOrNull()?.let { viewModel.errorText(it) }
                                    ?: "OK"
                            )
                        }
                    }
                }) { Text(stringResource(R.string.action_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showRemarkDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showDeleteDialog && contact != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.contact_delete)) },
            text = { Text(stringResource(R.string.contact_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteFriend(contact.id) { result ->
                        scope.launch {
                            val error = result.exceptionOrNull()
                            if (error == null) {
                                snackbar.showSnackbar("OK")
                                onBack()
                            } else {
                                snackbar.showSnackbar(viewModel.errorText(error))
                            }
                        }
                    }
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
