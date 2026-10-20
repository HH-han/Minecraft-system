package com.minecraft.im.ui.chat

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.minecraft.im.R
import com.minecraft.im.core.formatMessageDivider
import com.minecraft.im.core.localeIsEnglish
import com.minecraft.im.core.mediaUrl
import com.minecraft.im.domain.model.MessageKind
import com.minecraft.im.domain.model.MessageUi
import com.minecraft.im.ui.components.EMOJI_GROUPS
import com.minecraft.im.ui.components.EmptyState
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.LocalBaseUrl
import com.minecraft.im.ui.components.TopBarBack
import com.minecraft.im.ui.theme.LocalImColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TIME_GAP_MILLIS = 5 * 60 * 1000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    contactId: Long,
    isGroup: Boolean,
    onBack: () -> Unit,
    onOpenProfile: (Long, Boolean) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    LaunchedEffect(contactId, isGroup) { viewModel.load(contactId, isGroup) }

    val contact by viewModel.contact.collectAsStateWithLifecycle()
    val messageList by viewModel.messages.collectAsStateWithLifecycle()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val starred by viewModel.starred.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    val colors = LocalImColors.current
    val baseUrl = LocalBaseUrl.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showEmoji by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var previewImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val bytes = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }.getOrNull()
            }
            if (bytes == null) {
                snackbar.showSnackbar(context.getString(R.string.image_pick_failed))
                return@launch
            }
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            viewModel.sendImage(bytes, mime, "im_${System.currentTimeMillis()}.jpg")
        }
    }

    Scaffold(
        topBar = {
            TopBarBack(
                title = contact?.name ?: stringResource(R.string.tab_messages),
                onBack = onBack,
                subtitle = contact?.let { c ->
                    if (c.isGroup) stringResource(R.string.contact_member_count, c.memberCount)
                    else if (c.online) stringResource(R.string.chat_online)
                    else stringResource(R.string.chat_offline)
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.action_more))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_menu_profile)) },
                                onClick = {
                                    showMenu = false
                                    onOpenProfile(contactId, isGroup)
                                }
                            )
                            if (!isGroup) {
                                val isStarred = starred.contains(contactId)
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            stringResource(
                                                if (isStarred) R.string.contacts_unstar else R.string.contacts_star
                                            )
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (isStarred) Icons.Default.Star else Icons.Outlined.StarBorder,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        viewModel.toggleStar(contactId)
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colors.chatBackground)
                .imePadding()
        ) {
            if (state.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Box(Modifier.weight(1f)) {
                if (messageList.isEmpty()) {
                    EmptyState(text = stringResource(R.string.chat_no_messages))
                } else {
                    MessageList(
                        messages = messageList,
                        isGroup = isGroup,
                        myAvatar = currentUser?.avatar,
                        myName = currentUser?.nickname?.takeIf { it.isNotBlank() }
                            ?: currentUser?.username.orEmpty(),
                        onImageClick = { previewImage = it }
                    )
                }
            }

            ChatInputBar(
                value = state.input,
                sending = state.sending,
                onValueChange = viewModel::onInputChange,
                onSend = viewModel::send,
                onToggleEmoji = { showEmoji = !showEmoji },
                onPickImage = {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            if (showEmoji) {
                EmojiPanel(
                    onPick = { emoji ->
                        viewModel.sendEmoji(emoji)
                        showEmoji = false
                    }
                )
            }
        }
    }

    previewImage?.let { raw ->
        ImagePreviewDialog(url = mediaUrl(baseUrl, raw).orEmpty(), onDismiss = { previewImage = null })
    }
}

@Composable
private fun MessageList(
    messages: List<MessageUi>,
    isGroup: Boolean,
    myAvatar: String?,
    myName: String,
    onImageClick: (String) -> Unit
) {
    val isEnglish = localeIsEnglish()
    val rows = remember(messages, isEnglish) {
        buildList<ChatRow> {
            var lastTime = 0L
            messages.forEach { message ->
                if (message.time - lastTime > TIME_GAP_MILLIS) {
                    add(ChatRow.TimeDivider(message.time))
                }
                add(ChatRow.Message(message))
                lastTime = message.time
            }
        }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(rows.size) {
        if (rows.isNotEmpty()) {
            listState.animateScrollToItem(rows.lastIndex)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(rows) { _, row ->
            when (row) {
                is ChatRow.TimeDivider -> Text(
                    text = formatMessageDivider(row.time, isEnglish),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                is ChatRow.Message -> MessageRow(
                    message = row.message,
                    showSenderName = isGroup && !row.message.isMine,
                    myAvatar = myAvatar,
                    myName = myName,
                    onImageClick = onImageClick
                )
            }
        }
    }
}

private sealed interface ChatRow {
    data class TimeDivider(val time: Long) : ChatRow
    data class Message(val message: MessageUi) : ChatRow
}

@Composable
private fun MessageRow(
    message: MessageUi,
    showSenderName: Boolean,
    myAvatar: String?,
    myName: String,
    onImageClick: (String) -> Unit
) {
    val colors = LocalImColors.current
    val baseUrl = LocalBaseUrl.current
    val mine = message.isMine

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!mine) {
            ImAvatar(
                name = message.senderName.orEmpty().ifBlank { "#${message.senderId}" },
                avatar = message.senderAvatar,
                size = 38.dp
            )
            Spacer(Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (mine) Alignment.End else Alignment.Start) {
            if (showSenderName) {
                Text(
                    text = message.senderName.orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.muted,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .widthIn(max = 268.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (mine) 16.dp else 4.dp,
                            bottomEnd = if (mine) 4.dp else 16.dp
                        )
                    )
                    .background(if (mine) colors.sentBubble else colors.receivedBubble)
                    .padding(
                        if (message.kind == MessageKind.IMAGE) 4.dp else 12.dp
                    )
            ) {
                when (message.kind) {
                    MessageKind.IMAGE -> AsyncImage(
                        model = mediaUrl(baseUrl, message.content),
                        contentDescription = stringResource(R.string.message_image_preview),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .widthIn(max = 240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onImageClick(message.content) }
                    )

                    MessageKind.EMOJI -> Text(
                        text = message.content,
                        fontSize = 30.sp
                    )

                    else -> Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (mine) colors.sentText else colors.receivedText
                    )
                }
            }
        }

        if (mine) {
            Spacer(Modifier.width(8.dp))
            ImAvatar(name = myName.ifBlank { "Me" }, avatar = myAvatar, size = 38.dp)
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    sending: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onToggleEmoji: () -> Unit,
    onPickImage: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(onClick = onToggleEmoji) {
                Icon(Icons.Default.EmojiEmotions, contentDescription = stringResource(R.string.chat_emoji))
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(stringResource(R.string.chat_input_hint)) },
                maxLines = 4,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onPickImage) {
                Icon(Icons.Default.Image, contentDescription = stringResource(R.string.chat_send_image))
            }
            FilledIconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !sending
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = stringResource(R.string.action_send))
            }
        }
    }
}

@Composable
private fun EmojiPanel(onPick: (String) -> Unit) {
    val emojis = remember { EMOJI_GROUPS.flatMap { it.second } }
    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(emojis) { emoji ->
            Text(
                text = emoji,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable { onPick(emoji) }
                    .padding(6.dp)
            )
        }
    }
}

@Composable
private fun ImagePreviewDialog(url: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
