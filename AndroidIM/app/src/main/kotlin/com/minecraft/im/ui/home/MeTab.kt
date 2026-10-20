package com.minecraft.im.ui.home

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.minecraft.im.R
import com.minecraft.im.ui.components.GroupCard
import com.minecraft.im.ui.components.HorizontalDivider
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.NavRow
import com.minecraft.im.ui.theme.AccentAmber
import com.minecraft.im.ui.theme.AccentBlue
import com.minecraft.im.ui.theme.EmberOrange
import com.minecraft.im.ui.theme.GrassGreen

/** 我：个人资料卡 + 服务/收藏/朋友圈/作品/小店与卡包/表情 + 设置 */
@Composable
fun MeTab(
    name: String,
    account: String,
    avatar: String?,
    unread: Int,
    onProfile: () -> Unit,
    onFavorites: () -> Unit,
    onMoments: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val unavailableText = stringResource(R.string.feature_unavailable)
    val unavailable = { Toast.makeText(context, unavailableText, Toast.LENGTH_SHORT).show() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        GroupCard(modifier = Modifier.clickable(onClick = onProfile)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 10.dp, top = 18.dp, bottom = 18.dp),
                verticalAlignment = Alignment.Top
            ) {
                ImAvatar(name = name.ifBlank { "?" }, avatar = avatar, size = 62.dp)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = name.ifBlank { stringResource(R.string.label_me) },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (account.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.me_wechat_id, account),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoChip(
                            icon = Icons.Default.Add,
                            text = stringResource(R.string.me_status),
                            onClick = onProfile
                        )
                        if (unread > 0) {
                            InfoChip(
                                icon = Icons.Outlined.ChatBubbleOutline,
                                text = stringResource(R.string.me_new_messages, unread)
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onProfile, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = stringResource(R.string.me_qrcode),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        GroupCard {
            NavRow(
                title = stringResource(R.string.me_services),
                icon = Icons.Default.SupportAgent,
                iconTint = GrassGreen,
                onClick = unavailable
            )
            HorizontalDivider(Modifier.padding(start = 58.dp))
            NavRow(
                title = stringResource(R.string.me_favorites),
                icon = Icons.Default.Star,
                iconTint = EmberOrange,
                onClick = onFavorites
            )
            HorizontalDivider(Modifier.padding(start = 58.dp))
            NavRow(
                title = stringResource(R.string.moments_title),
                icon = Icons.Default.PhotoLibrary,
                iconTint = AccentBlue,
                onClick = onMoments
            )
            HorizontalDivider(Modifier.padding(start = 58.dp))
            NavRow(
                title = stringResource(R.string.me_works),
                icon = Icons.Default.VideoLibrary,
                iconTint = AccentBlue,
                subtitle = stringResource(R.string.me_works_hint),
                onClick = unavailable
            )
            HorizontalDivider(Modifier.padding(start = 58.dp))
            NavRow(
                title = stringResource(R.string.me_store),
                icon = Icons.Default.Storefront,
                iconTint = EmberOrange,
                onClick = unavailable
            )
            HorizontalDivider(Modifier.padding(start = 58.dp))
            NavRow(
                title = stringResource(R.string.me_stickers),
                icon = Icons.Default.SentimentSatisfiedAlt,
                iconTint = AccentAmber,
                onClick = unavailable
            )
        }

        Spacer(Modifier.height(8.dp))
        GroupCard {
            NavRow(
                title = stringResource(R.string.settings_title),
                icon = Icons.Default.Settings,
                iconTint = AccentBlue,
                onClick = onSettings
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .border(
                width = 0.6.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                shape = CircleShape
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
