package com.minecraft.im.ui.favorites

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.minecraft.im.R
import com.minecraft.im.core.formatConversationTime
import com.minecraft.im.core.localeIsEnglish
import com.minecraft.im.core.mediaUrl
import com.minecraft.im.domain.model.FavoriteType
import com.minecraft.im.domain.model.FavoriteUi
import com.minecraft.im.ui.components.LocalBaseUrl
import com.minecraft.im.ui.components.SearchField

private data class FavCategory(
    val key: String,
    val labelRes: Int,
    val icon: ImageVector
)

private val CATEGORIES = listOf(
    FavCategory(FavoritesViewModel.CATEGORY_ALL, R.string.favorites_cat_all, Icons.Default.Star),
    FavCategory(FavoritesViewModel.CATEGORY_RECENT, R.string.favorites_cat_recent, Icons.AutoMirrored.Filled.ArrowForward),
    FavCategory(FavoriteType.NOTE, R.string.favorites_cat_note, Icons.Default.Edit),
    FavCategory(FavoriteType.LINK, R.string.favorites_cat_link, Icons.Default.Link),
    FavCategory(FavoriteType.IMAGE, R.string.favorites_cat_image, Icons.Default.Image),
    FavCategory(FavoriteType.FILE, R.string.favorites_cat_file, Icons.AutoMirrored.Filled.InsertDriveFile),
    FavCategory(FavoriteType.CHAT, R.string.favorites_cat_chat, Icons.AutoMirrored.Filled.Chat),
    FavCategory(FavoriteType.VOICE, R.string.favorites_cat_voice, Icons.Default.Mic),
    FavCategory(FavoriteType.LOCATION, R.string.favorites_cat_location, Icons.Default.Place)
)

/** 收藏 Tab：分类筛选、搜索、新建笔记/链接、查看详情 */
@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    var showComposer by remember { mutableStateOf(false) }
    var detail by remember { mutableStateOf<FavoriteUi?>(null) }
    var showClearConfirm by remember { mutableStateOf(false) }

    val savedText = stringResource(R.string.favorites_saved)
    val deletedText = stringResource(R.string.favorites_deleted)
    val clearedText = stringResource(R.string.favorites_cleared)

    LaunchedEffect(state.event) {
        val message = when (state.event) {
            FavoritesViewModel.Event.SAVED -> savedText
            FavoritesViewModel.Event.DELETED -> deletedText
            FavoritesViewModel.Event.CLEARED -> clearedText
            null -> null
        }
        if (message != null) {
            snackbar.showSnackbar(message)
            viewModel.consumeEvent()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            SearchField(
                value = state.keyword,
                onValueChange = viewModel::onKeywordChange,
                placeholder = stringResource(R.string.favorites_search_hint)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CATEGORIES, key = { it.key }) { category ->
                    val count = when (category.key) {
                        FavoritesViewModel.CATEGORY_ALL -> state.total
                        FavoritesViewModel.CATEGORY_RECENT -> null
                        else -> state.typeCounts[category.key]
                    }
                    val label = stringResource(category.labelRes) +
                        if (count != null && count > 0) " $count" else ""
                    FilterChip(
                        selected = state.category == category.key,
                        onClick = { viewModel.selectCategory(category.key) },
                        label = { Text(label, maxLines = 1) },
                        leadingIcon = {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.favorites_count, state.items.size),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (state.category != FavoritesViewModel.CATEGORY_ALL &&
                    state.category != FavoritesViewModel.CATEGORY_RECENT
                ) {
                    IconButton(onClick = { showClearConfirm = true }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = stringResource(R.string.favorites_clear),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            when {
                state.loading && state.items.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                    }
                }

                state.items.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = if (state.keyword.isBlank()) {
                                stringResource(R.string.favorites_empty)
                            } else {
                                stringResource(R.string.favorites_empty_search)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 2.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            FavoriteCard(item = item, onClick = {
                                detail = item
                                viewModel.touch(item)
                            })
                        }
                        item(key = "footer") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    state.loadingMore -> CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    state.hasMore -> TextButton(onClick = viewModel::loadMore) {
                                        Text(stringResource(R.string.favorites_load_more))
                                    }

                                    else -> Text(
                                        text = stringResource(R.string.favorites_no_more),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showComposer = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 18.dp, bottom = 18.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.favorites_add))
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp)
        )
    }

    if (showComposer) {
        FavoriteComposerDialog(
            saving = state.saving,
            onDismiss = { showComposer = false },
            onSubmit = { type, title, content, url, tags ->
                viewModel.save(type, title, content, url, tags)
                showComposer = false
            }
        )
    }

    detail?.let { item ->
        FavoriteDetailDialog(
            item = item,
            onDismiss = { detail = null },
            onDelete = {
                detail = null
                viewModel.delete(item)
            }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text(stringResource(R.string.favorites_clear)) },
            text = { Text(stringResource(R.string.favorites_clear_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showClearConfirm = false
                    viewModel.clearCategory()
                }) {
                    Text(
                        text = stringResource(R.string.favorites_clear),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun FavoriteCard(item: FavoriteUi, onClick: () -> Unit) {
    val baseUrl = LocalBaseUrl.current
    val cover = mediaUrl(baseUrl, item.cover)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.displayTitle(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!item.content.isNullOrBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = item.content.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.typeLabel(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatConversationTime(item.updatedTime, localeIsEnglish()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!cover.isNullOrBlank()) {
                    AsyncImage(
                        model = cover,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = item.typeIcon(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteComposerDialog(
    saving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (type: String, title: String, content: String, url: String, tags: List<String>) -> Unit
) {
    var type by remember { mutableStateOf(FavoriteType.NOTE) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val urlRequired = stringResource(R.string.favorites_url_required)
    val contentRequired = stringResource(R.string.favorites_required)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            if (type == FavoriteType.LINK) R.string.favorites_new_link
                            else R.string.favorites_new_note
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == FavoriteType.NOTE,
                        onClick = { type = FavoriteType.NOTE },
                        label = { Text(stringResource(R.string.favorites_cat_note)) }
                    )
                    FilterChip(
                        selected = type == FavoriteType.LINK,
                        onClick = { type = FavoriteType.LINK },
                        label = { Text(stringResource(R.string.favorites_cat_link)) }
                    )
                }

                Spacer(Modifier.height(6.dp))
                if (type == FavoriteType.LINK) {
                    androidx.compose.material3.OutlinedTextField(
                        value = url,
                        onValueChange = { url = it; error = null },
                        label = { Text(stringResource(R.string.favorites_field_url)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                }

                androidx.compose.material3.OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.favorites_field_title)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = content,
                    onValueChange = { content = it; error = null },
                    label = { Text(stringResource(R.string.favorites_field_content)) },
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 96.dp)
                )
                Spacer(Modifier.height(10.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text(stringResource(R.string.favorites_field_tags)) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = error.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(16.dp))
                androidx.compose.material3.Button(
                    onClick = {
                        if (type == FavoriteType.LINK && url.isBlank()) {
                            error = urlRequired
                            return@Button
                        }
                        if (title.isBlank() && content.isBlank() && url.isBlank()) {
                            error = contentRequired
                            return@Button
                        }
                        val tags = tagsText
                            .split(",", "，", " ", "\n")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                        onSubmit(type, title, content, url, tags)
                    },
                    enabled = !saving,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    if (saving) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteDetailDialog(
    item: FavoriteUi,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val baseUrl = LocalBaseUrl.current
    val cover = mediaUrl(baseUrl, item.cover)
    var confirmDelete by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = item.typeIcon(),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = item.typeLabel(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.action_close))
                    }
                }

                if (!cover.isNullOrBlank()) {
                    AsyncImage(
                        model = cover,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.height(12.dp))
                }

                if (!item.title.isNullOrBlank()) {
                    Text(
                        text = item.title.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                }
                if (!item.content.isNullOrBlank()) {
                    Text(
                        text = item.content.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (!item.url.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { runCatching { uriHandler.openUri(item.url.orEmpty()) } }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = item.url.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }
                if (item.tags.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item.tags.take(6).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(Modifier.height(10.dp))
                Text(
                    text = formatConversationTime(
                        item.updatedTime.takeIf { it > 0 } ?: item.createdTime,
                        localeIsEnglish()
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = {
                            val text = item.url?.takeIf { it.isNotBlank() }
                                ?: item.content?.takeIf { it.isNotBlank() }
                                ?: item.title.orEmpty()
                            clipboard.setText(AnnotatedString(text))
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.favorites_copy))
                    }
                    androidx.compose.material3.OutlinedButton(
                        onClick = { confirmDelete = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(R.string.action_delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.action_delete)) },
            text = { Text(stringResource(R.string.favorites_delete_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun FavoriteUi.displayTitle(): String {
    if (!title.isNullOrBlank()) return title.orEmpty()
    if (!fileName.isNullOrBlank()) return fileName.orEmpty()
    if (type == FavoriteType.LINK && !url.isNullOrBlank()) return url.orEmpty()
    if (!content.isNullOrBlank()) return content.orEmpty().take(40)
    return stringResource(R.string.favorites_untitled)
}

@Composable
private fun FavoriteUi.typeLabel(): String = stringResource(
    when (type) {
        FavoriteType.LINK -> R.string.favorites_cat_link
        FavoriteType.IMAGE -> R.string.favorites_cat_image
        FavoriteType.VIDEO -> R.string.favorites_cat_image
        FavoriteType.FILE -> R.string.favorites_cat_file
        FavoriteType.CHAT -> R.string.favorites_cat_chat
        FavoriteType.VOICE -> R.string.favorites_cat_voice
        FavoriteType.LOCATION -> R.string.favorites_cat_location
        else -> R.string.favorites_cat_note
    }
)

private fun FavoriteUi.typeIcon(): ImageVector = when (type) {
    FavoriteType.LINK -> Icons.Default.Link
    FavoriteType.IMAGE, FavoriteType.VIDEO -> Icons.Default.Image
    FavoriteType.FILE -> Icons.AutoMirrored.Filled.InsertDriveFile
    FavoriteType.CHAT -> Icons.AutoMirrored.Filled.Chat
    FavoriteType.VOICE -> Icons.Default.Mic
    FavoriteType.LOCATION -> Icons.Default.Place
    else -> Icons.Default.Edit
}
