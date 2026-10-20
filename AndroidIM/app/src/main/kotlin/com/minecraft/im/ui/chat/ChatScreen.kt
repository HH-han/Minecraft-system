package com.minecraft.im.ui.chat

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.minecraft.im.R
import com.minecraft.im.core.formatMessageDivider
import com.minecraft.im.core.localeIsEnglish
import com.minecraft.im.core.mediaUrl
import com.minecraft.im.data.remote.dto.UserEmojiDto
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.domain.model.MessageKind
import com.minecraft.im.domain.model.MessageUi
import com.minecraft.im.ui.components.EMOJI_GROUPS
import com.minecraft.im.ui.components.EmptyState
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.LocalBaseUrl
import com.minecraft.im.ui.components.OnlineDot
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
    val customEmojis by viewModel.customEmojis.collectAsStateWithLifecycle()

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

    val emojiPicker = rememberLauncherForActivityResult(
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
            val mime = context.contentResolver.getType(uri) ?: "image/png"
            viewModel.uploadEmoji(bytes, mime, "emoji_${System.currentTimeMillis()}.png")
        }
    }

    Scaffold(
        containerColor = colors.chatBackground,
        topBar = {
            ChatHeader(
                contact = contact,
                isGroup = isGroup,
                onBack = onBack,
                onOpenProfile = { onOpenProfile(contactId, isGroup) },
                showMenu = showMenu,
                onShowMenu = { showMenu = it },
                starred = starred.contains(contactId),
                onToggleStar = { viewModel.toggleStar(contactId) }
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
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                )
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
                    customEmojis = customEmojis,
                    uploading = state.emojiUploading,
                    onPick = { emoji ->
                        viewModel.sendEmoji(emoji)
                        showEmoji = false
                    },
                    onUpload = {
                        emojiPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onDelete = viewModel::deleteEmoji
                )
            }
        }
    }

    previewImage?.let { raw ->
        ImagePreviewDialog(
            url = mediaUrl(baseUrl, raw).orEmpty(),
            onDismiss = { previewImage = null }
        )
    }
}

@Composable
private fun ChatHeader(
    contact: Contact?,
    isGroup: Boolean,
    onBack: () -> Unit,
    onOpenProfile: () -> Unit,
    showMenu: Boolean,
    onShowMenu: (Boolean) -> Unit,
    starred: Boolean,
    onToggleStar: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.action_back)
                    )
                }
                if (contact != null) {
                    Box {
                        ImAvatar(
                            name = contact.name,
                            avatar = contact.avatar,
                            isGroup = contact.isGroup,
                            size = 34.dp
                        )
                        if (!contact.isGroup && contact.online) {
                            OnlineDot(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 1.dp, bottom = 1.dp),
                                size = 10.dp
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = contact?.name ?: stringResource(R.string.tab_messages),
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    contact?.let {
                        Text(
                            text = if (it.isGroup) {
                                stringResource(R.string.contact_member_count, it.memberCount)
                            } else if (it.online) {
                                stringResource(R.string.chat_online)
                            } else {
                                stringResource(R.string.chat_offline)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (!it.isGroup && it.online) {
                                LocalImColors.current.online
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                Box {
                    IconButton(onClick = { onShowMenu(true) }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.action_more)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { onShowMenu(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.chat_menu_profile)) },
                            onClick = {
                                onShowMenu(false)
                                onOpenProfile()
                            }
                        )
                        if (!isGroup) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(
                                            if (starred) R.string.contacts_unstar
                                            else R.string.contacts_star
                                        )
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (starred) Icons.Default.Star
                                        else Icons.Outlined.StarBorder,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    onShowMenu(false)
                                    onToggleStar()
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }
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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(rows) { _, row ->
            when (row) {
                is ChatRow.TimeDivider -> Text(
                    text = formatMessageDivider(row.time, isEnglish),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalImColors.current.muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
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

@OptIn(ExperimentalFoundationApi::class)
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
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var menuVisible by remember { mutableStateOf(false) }

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
            when (message.kind) {
                MessageKind.IMAGE -> AsyncImage(
                    model = mediaUrl(baseUrl, message.content),
                    contentDescription = stringResource(R.string.message_image_preview),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .widthIn(max = 220.dp)
                        .heightIn(max = 260.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onImageClick(message.content) }
                )

                MessageKind.EMOJI -> {
                    val isImage = message.content.startsWith("http", true) ||
                        message.content.startsWith("/")
                    if (isImage) {
                        AsyncImage(
                            model = mediaUrl(baseUrl, message.content),
                            contentDescription = stringResource(R.string.chat_emoji),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(112.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onImageClick(message.content) }
                        )
                    } else {
                        Text(
                            text = message.content,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }

                else -> Row(verticalAlignment = Alignment.Top) {
                    if (!mine) {
                        BubbleTail(color = colors.receivedBubble, pointingLeft = true)
                    }
                    Box {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 264.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (mine) colors.sentBubble else colors.receivedBubble)
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = { menuVisible = true }
                                )
                                .padding(horizontal = 12.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = message.content,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (mine) colors.sentText else colors.receivedText
                            )
                        }
                        DropdownMenu(
                            expanded = menuVisible,
                            onDismissRequest = { menuVisible = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.chat_copy)) },
                                onClick = {
                                    menuVisible = false
                                    clipboard.setText(AnnotatedString(message.content))
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.chat_copied),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                    if (mine) {
                        BubbleTail(color = colors.sentBubble, pointingLeft = false)
                    }
                }
            }
        }

        if (mine) {
            Spacer(Modifier.width(8.dp))
            ImAvatar(name = myName.ifBlank { "Me" }, avatar = myAvatar, size = 38.dp)
        }
    }
}

/** 气泡小尖角 */
@Composable
private fun BubbleTail(color: Color, pointingLeft: Boolean) {
    Box(Modifier.padding(top = 11.dp)) {
        Canvas(Modifier.size(width = 6.dp, height = 12.dp)) {
            val path = Path()
            if (pointingLeft) {
                path.moveTo(size.width, 0f)
                path.lineTo(0f, size.height / 2f)
                path.lineTo(size.width, size.height)
            } else {
                path.moveTo(0f, 0f)
                path.lineTo(size.width, size.height / 2f)
                path.lineTo(0f, size.height)
            }
            path.close()
            drawPath(path, color)
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
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(onClick = onToggleEmoji, modifier = Modifier.size(42.dp)) {
                    Icon(
                        imageVector = Icons.Default.EmojiEmotions,
                        contentDescription = stringResource(R.string.chat_emoji),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(R.string.chat_input_hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                IconButton(onClick = onPickImage, modifier = Modifier.size(42.dp)) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = stringResource(R.string.chat_send_image),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FilledIconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank() && !sending,
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .padding(start = 2.dp, end = 2.dp)
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.action_send),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EmojiPanel(
    customEmojis: List<UserEmojiDto>,
    uploading: Boolean,
    onPick: (String) -> Unit,
    onUpload: () -> Unit,
    onDelete: (Long) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    var groupIndex by remember { mutableIntStateOf(0) }
    var pendingDelete by remember { mutableStateOf<UserEmojiDto?>(null) }
    val baseUrl = LocalBaseUrl.current

    Surface(color = MaterialTheme.colorScheme.surface) {
        Column {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EmojiTab(
                    text = stringResource(R.string.emoji_tab_system),
                    selected = tab == 0,
                    onClick = { tab = 0 }
                )
                EmojiTab(
                    text = stringResource(R.string.emoji_tab_custom),
                    selected = tab == 1,
                    onClick = { tab = 1 }
                )
                Spacer(Modifier.weight(1f))
                if (tab == 1) {
                    IconButton(onClick = onUpload, enabled = !uploading) {
                        if (uploading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.emoji_tab_custom),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            if (tab == 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EMOJI_GROUPS.forEachIndexed { index, group ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (index == groupIndex) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable { groupIndex = index }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(text = group.first, fontSize = 18.sp)
                        }
                    }
                }
                val emojis = EMOJI_GROUPS[groupIndex].second
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(emojis) { emoji ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onPick(emoji) }
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                if (customEmojis.isEmpty() && !uploading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.emoji_empty),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        items(customEmojis, key = { it.id }) { emoji ->
                            val url = mediaUrl(baseUrl, emoji.emojiUrl).orEmpty()
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .combinedClickable(
                                        onClick = { onPick(url) },
                                        onLongClick = { pendingDelete = emoji }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = emoji.emojiName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { emoji ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            text = { Text(stringResource(R.string.emoji_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    pendingDelete = null
                    onDelete(emoji.id)
                }) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun EmojiTab(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ImagePreviewDialog(url: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.action_close),
                    tint = Color.White
                )
            }
        }
    }
}
