package com.minecraft.im.ui.home

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minecraft.im.R
import com.minecraft.im.core.contactInitial
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.domain.model.FriendRequestUi
import com.minecraft.im.ui.components.GroupCard
import com.minecraft.im.ui.components.GroupHeader
import com.minecraft.im.ui.components.HorizontalDivider
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.NavRow
import com.minecraft.im.ui.components.OnlineDot
import com.minecraft.im.ui.components.UnreadBadge
import com.minecraft.im.ui.theme.AccentAmber
import com.minecraft.im.ui.theme.AccentBlue
import com.minecraft.im.ui.theme.EmberOrange
import com.minecraft.im.ui.theme.GrassGreen
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.Locale

/**
 * 通讯录：新的朋友 / 仅聊天的朋友 / 群聊 / 标签 / 公众号 / 服务号 + 企业及企业联系人 +
 * 星标朋友 + 按首字母分组的联系人 + 右侧字母索引。
 */
@Composable
fun ContactsTab(
    friends: List<Contact>,
    groups: List<Contact>,
    requests: List<FriendRequestUi>,
    starred: Set<Long>,
    query: String,
    onToggleStar: (Long) -> Unit,
    onOpenChat: (Contact) -> Unit,
    onFriendRequests: () -> Unit,
    onShowGroups: () -> Unit
) {
    val context = LocalContext.current
    val unavailableText = stringResource(R.string.feature_unavailable)
    val unavailable = { Toast.makeText(context, unavailableText, Toast.LENGTH_SHORT).show() }

    val collator = remember { Collator.getInstance(Locale.CHINA) }
    val filtered = remember(friends, query) {
        friends
            .filter { query.isBlank() || it.name.contains(query, ignoreCase = true) }
            .sortedWith(compareBy(collator) { it.name })
    }

    val starredFriends = remember(filtered, starred) {
        filtered.filter { it.id in starred }
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

    // 星标朋友之前有：入口分组（1）+ 企业标题 + 企业分组（2），星标自己占 2 项
    val firstLetterIndex = 3 + if (starredFriends.isNotEmpty()) 2 else 0
    val letterIndexes = remember(letters, firstLetterIndex) {
        letters.withIndex().associate { (i, letter) -> letter to firstLetterIndex + i }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            item(key = "entries") {
                GroupCard {
                    NavRow(
                        title = stringResource(R.string.contacts_new_friend),
                        icon = Icons.Default.PersonAdd,
                        iconTint = EmberOrange,
                        badge = requests.size,
                        onClick = onFriendRequests
                    )
                    HorizontalDivider(Modifier.padding(start = 58.dp))
                    NavRow(
                        title = stringResource(R.string.contacts_chat_only),
                        icon = Icons.Default.ChatBubble,
                        iconTint = EmberOrange,
                        onClick = unavailable
                    )
                    HorizontalDivider(Modifier.padding(start = 58.dp))
                    NavRow(
                        title = stringResource(R.string.contacts_group_chat),
                        icon = Icons.Default.Groups,
                        iconTint = GrassGreen,
                        subtitle = groups.size.takeIf { it > 0 }?.toString(),
                        onClick = onShowGroups
                    )
                    HorizontalDivider(Modifier.padding(start = 58.dp))
                    NavRow(
                        title = stringResource(R.string.contacts_tags),
                        icon = Icons.Default.Sell,
                        iconTint = AccentBlue,
                        onClick = unavailable
                    )
                    HorizontalDivider(Modifier.padding(start = 58.dp))
                    NavRow(
                        title = stringResource(R.string.contacts_official),
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        iconTint = AccentBlue,
                        onClick = unavailable
                    )
                    HorizontalDivider(Modifier.padding(start = 58.dp))
                    NavRow(
                        title = stringResource(R.string.contacts_service),
                        icon = Icons.Default.Widgets,
                        iconTint = AccentBlue,
                        onClick = unavailable
                    )
                }
            }

            item(key = "enterprise_header") {
                GroupHeader(stringResource(R.string.contacts_enterprise))
            }
            item(key = "enterprise") {
                GroupCard {
                    NavRow(
                        title = stringResource(R.string.contacts_wecom),
                        icon = Icons.AutoMirrored.Filled.Chat,
                        iconTint = AccentBlue,
                        onClick = unavailable
                    )
                    HorizontalDivider(Modifier.padding(start = 58.dp))
                    NavRow(
                        title = stringResource(R.string.contacts_enterprise_app),
                        icon = Icons.Default.Home,
                        iconTint = AccentBlue,
                        onClick = unavailable
                    )
                }
            }

            if (starredFriends.isNotEmpty()) {
                item(key = "starred_header") {
                    GroupHeader(stringResource(R.string.contacts_starred))
                }
                item(key = "starred") {
                    GroupCard {
                        starredFriends.forEachIndexed { index, contact ->
                            if (index > 0) HorizontalDivider(Modifier.padding(start = 72.dp))
                            FriendRow(
                                contact = contact,
                                isStarred = true,
                                onToggleStar = { onToggleStar(contact.id) },
                                onClick = { onOpenChat(contact) }
                            )
                        }
                    }
                }
            }

            letters.forEach { letter ->
                item(key = "letter_$letter") {
                    Column {
                        LetterHeader(letter)
                        GroupCard {
                            grouped[letter].orEmpty().forEachIndexed { index, contact ->
                                if (index > 0) HorizontalDivider(Modifier.padding(start = 72.dp))
                                FriendRow(
                                    contact = contact,
                                    isStarred = contact.id in starred,
                                    onToggleStar = { onToggleStar(contact.id) },
                                    onClick = { onOpenChat(contact) }
                                )
                            }
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                item(key = "empty") {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
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
        }

        AlphabetIndexBar(
            letters = ALPHABET,
            available = letters.toSet(),
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

private val ALPHABET = buildList {
    for (code in 'A'.code..'Z'.code) add(code.toChar().toString())
    add("#")
}

@Composable
private fun LetterHeader(letter: String) {
    Text(
        text = letter,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 10.dp, bottom = 6.dp)
    )
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
            .padding(start = 16.dp, end = 6.dp, top = 7.dp, bottom = 7.dp),
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
        IconButton(onClick = onToggleStar, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = if (isStarred) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = stringResource(
                    if (isStarred) R.string.contacts_unstar else R.string.contacts_star
                ),
                tint = if (isStarred) AccentAmber else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

/** 右侧 A-Z 字母索引；没有联系人的字母置灰且不可跳转 */
@Composable
private fun AlphabetIndexBar(
    letters: List<String>,
    available: Set<String>,
    modifier: Modifier = Modifier,
    onLetter: (String) -> Unit
) {
    Column(
        modifier = modifier
            .width(22.dp)
            .fillMaxHeight()
            .padding(vertical = 10.dp)
            .pointerInput(letters, available) {
                val step = size.height.toFloat() / letters.size
                fun letterAt(y: Float): String =
                    letters[(y / step).toInt().coerceIn(0, letters.lastIndex)]

                detectVerticalDragGestures(
                    onDragStart = { offset -> letterAt(offset.y).let(onLetter) },
                    onVerticalDrag = { change, _ -> letterAt(change.position.y).let(onLetter) }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        letters.forEach { letter ->
            val enabled = letter in available
            Text(
                text = letter,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onLetter(letter) },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
