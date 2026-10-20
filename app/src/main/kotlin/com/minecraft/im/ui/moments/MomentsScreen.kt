package com.minecraft.im.ui.moments

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.minecraft.im.R
import com.minecraft.im.core.formatConversationTime
import com.minecraft.im.core.localeIsEnglish
import com.minecraft.im.core.mediaUrl
import com.minecraft.im.domain.model.MomentUi
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.LocalBaseUrl
import com.minecraft.im.ui.components.TopBarBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MomentsScreen(
    onBack: () -> Unit,
    viewModel: MomentsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var showPublish by remember { mutableStateOf(false) }
    var commentTarget by remember { mutableStateOf<MomentUi?>(null) }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .collect { lastVisible ->
                if (lastVisible >= state.moments.size - 2) viewModel.loadMore()
            }
    }

    Scaffold(
        topBar = {
            TopBarBack(
                title = stringResource(R.string.moments_title),
                onBack = onBack,
                actions = {
                    IconButton(onClick = { showPublish = true }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.moments_publish))
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Send, contentDescription = stringResource(R.string.action_refresh))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (state.loading && state.moments.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.moments.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.moments_empty),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(state.moments, key = { it.id }) { moment ->
                        MomentCard(
                            moment = moment,
                            isMine = moment.userId == state.currentUserId,
                            onLike = { viewModel.toggleLike(moment) },
                            onComment = { commentTarget = moment },
                            onDelete = {
                                viewModel.deleteMoment(moment.id) { error ->
                                    scope.launch { snackbar.showSnackbar(error ?: "OK") }
                                }
                            }
                        )
                    }
                    if (state.loadingMore) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }

            if (state.loadingMore && state.moments.isNotEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth().align(Alignment.TopCenter))
            }
        }
    }

    if (showPublish) {
        PublishDialog(
            onDismiss = { showPublish = false },
            onPublish = { content, images, location, visibility ->
                viewModel.publish(content, images, location, visibility) { error ->
                    scope.launch {
                        snackbar.showSnackbar(error ?: "OK")
                        if (error == null) showPublish = false
                    }
                }
            }
        )
    }

    commentTarget?.let { moment ->
        CommentDialog(
            moment = moment,
            onDismiss = { commentTarget = null },
            onSubmit = { content ->
                viewModel.addComment(moment.id, content, null) { error ->
                    scope.launch {
                        snackbar.showSnackbar(error ?: "OK")
                        if (error == null) commentTarget = null
                    }
                }
            }
        )
    }
}

@Composable
private fun MomentCard(
    moment: MomentUi,
    isMine: Boolean,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onDelete: () -> Unit
) {
    val baseUrl = LocalBaseUrl.current
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImAvatar(name = moment.name, avatar = moment.avatar, size = 42.dp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = moment.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatConversationTime(moment.time, localeIsEnglish()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isMine) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.moments_delete)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }

        if (moment.content.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(text = moment.content, style = MaterialTheme.typography.bodyMedium)
        }

        if (moment.images.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                moment.images.chunked(3).forEach { rowImages ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        rowImages.forEach { image ->
                            AsyncImage(
                                model = mediaUrl(baseUrl, image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        }
                        repeat(3 - rowImages.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        if (!moment.location.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "@ ${moment.location}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(8.dp))
        if (moment.likeNames.isNotEmpty()) {
            Text(
                text = "❤ " + moment.likeNames.joinToString(", "),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (moment.comments.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                moment.comments.forEach { comment ->
                    val reply = comment.replyToName?.let { " ▸ $it" }.orEmpty()
                    Text(
                        text = "${comment.name}$reply：${comment.content}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onLike) {
                Icon(
                    imageVector = if (moment.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (moment.liked) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (moment.likeCount > 0) moment.likeCount.toString()
                    else stringResource(R.string.moments_like)
                )
            }
            TextButton(onClick = onComment) {
                Text(stringResource(R.string.moments_comment))
            }
        }
    }
}

@Composable
private fun PublishDialog(
    onDismiss: () -> Unit,
    onPublish: (String, List<UploadImage>, String, Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var content by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(0) }
    var images by remember { mutableStateOf<List<UploadImage>>(emptyList()) }
    var uriPreviews by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var uploading by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(9)
    ) { uris: List<Uri> ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        val remaining = 9 - images.size
        if (remaining <= 0) return@rememberLauncherForActivityResult
        scope.launch {
            uploading = true
            val picked = uris.take(remaining)
            val loaded = withContext(Dispatchers.IO) {
                picked.mapNotNull { uri ->
                    val bytes = runCatching {
                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    }.getOrNull() ?: return@mapNotNull null
                    UploadImage(
                        bytes = bytes,
                        mime = context.contentResolver.getType(uri) ?: "image/jpeg",
                        name = "moment_${System.currentTimeMillis()}.jpg"
                    )
                }
            }
            images = images + loaded
            uriPreviews = uriPreviews + picked.take(loaded.size)
            uploading = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.moments_publish),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text(stringResource(R.string.moments_publish_hint)) },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                if (uriPreviews.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        uriPreviews.forEachIndexed { index, uri ->
                            Box {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onError,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error)
                                        .clickable {
                                            images = images.filterIndexed { i, _ -> i != index }
                                            uriPreviews = uriPreviews.filterIndexed { i, _ -> i != index }
                                        }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = {
                            picker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.moments_images))
                    }
                    Spacer(Modifier.weight(1f))
                    if (uploading) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(stringResource(R.string.moments_location)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val options = listOf(
                        0 to stringResource(R.string.moments_visibility_public),
                        1 to stringResource(R.string.moments_visibility_friends),
                        2 to stringResource(R.string.moments_visibility_private)
                    )
                    options.forEach { (value, label) ->
                        FilterChip(
                            selected = visibility == value,
                            onClick = { visibility = value },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(
                        onClick = { onPublish(content, images, location, visibility) },
                        enabled = !uploading && (content.isNotBlank() || images.isNotEmpty())
                    ) {
                        Text(stringResource(R.string.moments_publish_action))
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentDialog(
    moment: MomentUi,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.moments_comment)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text(stringResource(R.string.moments_comment_hint)) },
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onSubmit(text) }, enabled = text.isNotBlank()) {
                Text(stringResource(R.string.action_send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
