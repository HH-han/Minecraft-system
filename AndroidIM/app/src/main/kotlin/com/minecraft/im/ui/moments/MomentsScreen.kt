package com.minecraft.im.ui.moments

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowCompat
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
    val context = LocalContext.current
    var showPublish by remember { mutableStateOf(false) }
    var commentTarget by remember { mutableStateOf<MomentUi?>(null) }
    val successText = stringResource(R.string.label_success)
    val publishSuccessText = stringResource(R.string.moments_publish_success)
    val coverUpdatedText = stringResource(R.string.moments_cover_updated)

    // 上滑封面后顶栏逐渐变成实心，并切换为深色图标
    val collapseFraction by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) 1f
            else (listState.firstVisibleItemScrollOffset / 180f).coerceIn(0f, 1f)
        }
    }

    // 封面为深色图片时状态栏图标用白色，顶栏收起后再切回深色
    val view = LocalView.current
    val insetsController = remember(view) {
        view.context.findActivity()?.window?.let { WindowCompat.getInsetsController(it, view) }
    }
    val previousLightStatusBars = remember {
        insetsController?.isAppearanceLightStatusBars
    }
    val useDarkStatusIcons by remember {
        derivedStateOf { collapseFraction > 0.5f }
    }
    LaunchedEffect(useDarkStatusIcons) {
        insetsController?.isAppearanceLightStatusBars = useDarkStatusIcons
    }
    DisposableEffect(Unit) {
        onDispose {
            if (insetsController != null && previousLightStatusBars != null) {
                insetsController.isAppearanceLightStatusBars = previousLightStatusBars
            }
        }
    }

    val coverPicker = rememberLauncherForActivityResult(
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
            viewModel.uploadCover(
                bytes = bytes,
                mime = mime,
                fileName = "moment_cover_${System.currentTimeMillis()}.jpg"
            ) { error ->
                scope.launch { snackbar.showSnackbar(error ?: coverUpdatedText) }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .collect { lastVisible ->
                if (lastVisible >= state.moments.size - 1) viewModel.loadMore()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item(key = "cover") {
                MomentsCover(
                    coverUrl = state.coverUrl,
                    userName = state.currentUserName.ifBlank {
                        stringResource(R.string.label_me)
                    },
                    userAvatar = state.currentUserAvatar,
                    uploading = state.coverUploading,
                    onClick = {
                        if (!state.coverUploading) {
                            coverPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    }
                )
            }

            if (state.loading && state.moments.isEmpty()) {
                item(key = "loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else if (state.moments.isEmpty()) {
                item(key = "empty") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.moments_empty),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.moments, key = { it.id }) { moment ->
                    MomentCard(
                        moment = moment,
                        isMine = moment.userId == state.currentUserId,
                        onLike = { viewModel.toggleLike(moment) },
                        onComment = { commentTarget = moment },
                        onDelete = {
                            viewModel.deleteMoment(moment.id) { error ->
                                scope.launch { snackbar.showSnackbar(error ?: successText) }
                            }
                        }
                    )
                }
                if (state.loadingMore) {
                    item(key = "footer") {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }
        }

        MomentsTopBar(
            collapseFraction = collapseFraction,
            onBack = onBack,
            onRefresh = { viewModel.refresh() },
            onPublish = { showPublish = true },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }

    if (showPublish) {
        PublishDialog(
            onDismiss = { showPublish = false },
            onPublish = { content, images, location, visibility ->
                viewModel.publish(content, images, location, visibility) { error ->
                    scope.launch {
                        snackbar.showSnackbar(error ?: publishSuccessText)
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
                        snackbar.showSnackbar(error ?: successText)
                        if (error == null) commentTarget = null
                    }
                }
            }
        )
    }
}

/** 朋友圈封面：背景图 + 渐变遮罩 + 悬浮顶栏 + 右下角昵称头像 */
@Composable
private fun MomentsCover(
    coverUrl: String?,
    userName: String,
    userAvatar: String?,
    uploading: Boolean,
    onClick: () -> Unit
) {
    val resolved = mediaUrl(LocalBaseUrl.current, coverUrl)
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val coverHeight = 230.dp + statusBarHeight
    val avatarSize = 72.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(coverHeight + 36.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverHeight)
                .clickable(onClick = onClick)
        ) {
            if (resolved.isNullOrBlank()) {
                // 默认封面：草方块绿渐变
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF245A2E),
                                    Color(0xFF3FA34D),
                                    Color(0xFF8BD450)
                                )
                            )
                        )
                )
            } else {
                AsyncImage(
                    model = resolved,
                    contentDescription = stringResource(R.string.moments_cover_hint),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.42f),
                            0.35f to Color.Transparent,
                            0.75f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.38f)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Black.copy(alpha = 0.32f))
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = stringResource(R.string.moments_cover_hint),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Text(
                text = userName,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 108.dp, bottom = 22.dp)
            )

            if (uploading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.moments_cover_uploading),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp)
        ) {
            CoverAvatar(name = userName, avatar = userAvatar, size = avatarSize)
        }
    }
}

/** 封面右下角的方形头像（白边） */
@Composable
private fun CoverAvatar(name: String, avatar: String?, size: Dp) {
    val resolved = mediaUrl(LocalBaseUrl.current, avatar)
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().take(1).ifBlank { "?" }.uppercase(),
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (!resolved.isNullOrBlank()) {
            AsyncImage(
                model = resolved,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .border(3.dp, Color.White, shape)
        )
    }
}

/** 悬浮顶栏：随封面上滑由透明渐变到实心 */
@Composable
private fun MomentsTopBar(
    collapseFraction: Float,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onPublish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surface = MaterialTheme.colorScheme.surface
    val contentColor = lerp(Color.White, MaterialTheme.colorScheme.onSurface, collapseFraction)
    val buttonBackground = if (collapseFraction < 0.5f) {
        Color.Black.copy(alpha = 0.22f)
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(surface.copy(alpha = collapseFraction))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 6.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(buttonBackground)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back),
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = stringResource(R.string.moments_title),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor,
                modifier = Modifier.align(Alignment.Center)
            )

            Row(
                modifier = Modifier.align(Alignment.CenterEnd),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(buttonBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.action_refresh),
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onPublish,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(buttonBackground)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.moments_publish),
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
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
