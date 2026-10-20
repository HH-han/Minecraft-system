package com.minecraft.im.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minecraft.im.R
import com.minecraft.im.core.contactInitial
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.domain.model.FriendRequestUi
import com.minecraft.im.ui.components.HorizontalDivider
import com.minecraft.im.ui.components.IconTile
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.OnlineDot
import com.minecraft.im.ui.components.UnreadBadge
import com.minecraft.im.ui.theme.AccentAmber
import com.minecraft.im.ui.theme.AccentBlue
import com.minecraft.im.ui.theme.AccentTeal
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

/**
 * 通讯录：新的朋友 / 群聊 / 星标入口 + 按首字母分组的联系人 + 右侧字母索引。
 */
@Composable
fun ContactsTab(
    friends: List<Contact>,
    groups: List<Contact>,
    requests: List<FriendRequestUi>,
    starred: Set<Long>,
    query: String,
    starredOnly: Boolean,
    onToggleStarredOnly: () -> Unit,
    onToggleStar: (Long) -> Unit,
    onOpenChat: (Contact) -> Unit,
    onFriendRequests: () -> Unit,
    onShowGroups: () -> Unit
) {
    val collator = remember { Collator.getInstance(Locale.CHINA) }
    val filtered = remember(friends, query, starredOnly, starred) {
        friends
            .filter { contact ->
                (query.isBlank() || contact.name.contains(query, ignoreCase = true)) &&
                    (!starredOnly || contact.id in starred)
            }
            .sortedWith(compareBy(collator) { it.name })
    }

    val grouped = remember(filtered) {
        filtered.groupBy { contactInitial(it.name) }
    }
    val letters = remember(grouped) {
        buildList {
            for (code in 'A'.code..'Z'.code) add(code.toChar().toString())
            add("#")
        }.filter { grouped.containsKey(it) }
    }

    // 条目索引：1 个入口卡片 + 每个字母的标题行
    val letterIndexes = remember(letters, grouped) {
        val map = mutableMapOf<String, Int>()
        var index = 1
        letters.forEach { letter ->
            map[letter] = index
            index += 1 + (grouped[letter]?.size ?: 0)
        }
        map
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            item(key = "entries") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    EntryRow(
                        icon = Icons.Default.PersonAdd,
                        tint = AccentBlue,
                        title = stringResource(R.string.contacts_new_friend),
                        badge = requests.size,
                        onClick = onFriendRequests
                    )
                    HorizontalDivider(Modifier.padding(start = 66.dp))
                    EntryRow(
                        icon = Icons.Default.Group,
                        tint = AccentTeal,
                        title = stringResource(R.string.contacts_group_chat),
                        subtitle = groups.size.takeIf { it > 0 }?.toString(),
                        onClick = onShowGroups
                    )
                    HorizontalDivider(Modifier.padding(start = 66.dp))
                    EntryRow(
                        icon = Icons.Default.Star,
                        tint = AccentAmber,
                        title = stringResource(R.string.contacts_starred),
                        subtitle = if (starredOnly) "✓" else starred.size.takeIf { it > 0 }?.toString(),
                        onClick = onToggleStarredOnly
                    )
                }
            }

            if (filtered.isEmpty()) {
                item(key = "empty") {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (query.isBlank()) stringResource(R.string.empty_contacts)
                            else stringResource(R.string.empty_search),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            letters.forEach { letter ->
                item(key = "header_$letter") {
                    Text(
                        text = letter,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(start = 18.dp, top = 8.dp, bottom = 4.dp)
                    )
                }
                items(grouped[letter].orEmpty(), key = { it.key }) { contact ->
                    FriendRow(
                        contact = contact,
                        isStarred = contact.id in starred,
                        onToggleStar = { onToggleStar(contact.id) },
                        onClick = { onOpenChat(contact) }
                    )
                }
            }
        }

        AlphabetIndexBar(
            letters = letters,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 2.dp),
            onLetter = { letter ->
                letterIndexes[letter]?.let { index ->
                    scope.launch { listState.scrollToItem(index) }
                }
            }
        )
    }
}

@Composable
private fun EntryRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    title: String,
    subtitle: String? = null,
    badge: Int = 0,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconTile(icon = icon, tint = tint, size = 40.dp)
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(8.dp))
        }
        UnreadBadge(badge)
    }
}

@Composable
private fun FriendRow(
    contact: Contact,
    isStarred: Boolean,
    onToggleStar: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            ImAvatar(name = contact.name, avatar = contact.avatar, size = 44.dp)
            if (contact.online) {
                OnlineDot(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 1.dp, bottom = 1.dp),
                    size = 11.dp
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = contact.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (contact.unreadCount > 0) {
            UnreadBadge(contact.unreadCount)
            Spacer(Modifier.width(6.dp))
        }
        IconButton(onClick = onToggleStar) {
            Icon(
                imageVector = if (isStarred) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = stringResource(
                    if (isStarred) R.string.contacts_unstar else R.string.contacts_star
                ),
                tint = if (isStarred) AccentAmber else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** 右侧 A-Z 字母索引，支持点击与拖动 */
@Composable
private fun AlphabetIndexBar(
    letters: List<String>,
    modifier: Modifier = Modifier,
    onLetter: (String) -> Unit
) {
    if (letters.isEmpty()) return
    Column(
        modifier = modifier
            .width(24.dp)
            .fillMaxHeight()
            .padding(vertical = 12.dp)
            .pointerInput(letters) {
                val step = size.height.toFloat() / letters.size
                fun letterAt(y: Float): String =
                    letters[(y / step).toInt().coerceIn(0, letters.lastIndex)]

                detectVerticalDragGestures(
                    onDragStart = { offset -> onLetter(letterAt(offset.y)) },
                    onVerticalDrag = { change, _ -> onLetter(letterAt(change.position.y)) }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        letters.forEach { letter ->
            Text(
                text = letter,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onLetter(letter) }
                    .padding(horizontal = 2.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
