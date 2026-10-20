package com.minecraft.im.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.minecraft.im.R
import com.minecraft.im.core.formatConversationTime
import com.minecraft.im.core.localeIsEnglish
import com.minecraft.im.domain.model.Contact
import com.minecraft.im.ui.components.EmptyState
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.UnreadBadge
import com.minecraft.im.ui.theme.LocalImColors

/** 会话列表 */
@Composable
fun ConversationsTab(
    conversations: List<Contact>,
    query: String,
    onOpen: (Contact) -> Unit
) {
    val filtered = remember(conversations, query) {
        if (query.isBlank()) conversations
        else conversations.filter { it.name.contains(query, ignoreCase = true) }
    }

    if (filtered.isEmpty()) {
        EmptyState(
            text = if (query.isBlank()) stringResource(R.string.empty_conversations)
            else stringResource(R.string.empty_search)
        )
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(filtered, key = { it.key }) { contact ->
            ConversationRow(contact = contact, onClick = { onOpen(contact) })
        }
    }
}

@Composable
private fun ConversationRow(contact: Contact, onClick: () -> Unit) {
    val colors = LocalImColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            ImAvatar(name = contact.name, avatar = contact.avatar, isGroup = contact.isGroup, size = 48.dp)
            if (!contact.isGroup && contact.online) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(11.dp)
                        .clip(CircleShape)
                        .background(colors.online)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (contact.lastMessageTime > 0) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatConversationTime(contact.lastMessageTime, localeIsEnglish()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.padding(top = 3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = previewText(contact),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                UnreadBadge(
                    count = contact.unreadCount,
                    maxText = stringResource(R.string.unread_badge_99)
                )
            }
        }
    }
}

@Composable
private fun previewText(contact: Contact): String {
    val raw = contact.lastMessage
    return when {
        raw.isBlank() -> if (contact.isGroup) {
            stringResource(R.string.contact_member_count, contact.memberCount)
        } else {
            ""
        }
        raw.startsWith("http") -> stringResource(R.string.message_image_preview)
        else -> raw
    }
}
