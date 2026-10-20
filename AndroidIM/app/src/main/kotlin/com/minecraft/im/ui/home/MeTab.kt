package com.minecraft.im.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.minecraft.im.R
import com.minecraft.im.ui.components.IconTile
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.SectionCard
import com.minecraft.im.ui.components.SettingRow
import com.minecraft.im.ui.theme.AccentAmber
import com.minecraft.im.ui.theme.AccentBlue
import com.minecraft.im.ui.theme.AccentRed

/** 我：个人资料、收藏、设置、退出登录 */
@Composable
fun MeTab(
    name: String,
    account: String,
    avatar: String?,
    onProfile: () -> Unit,
    onFavorites: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onProfile)
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImAvatar(name = name.ifBlank { "?" }, avatar = avatar, size = 66.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = name.ifBlank { stringResource(R.string.label_me) },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (account.isBlank()) "" else "@$account",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(6.dp))
        SectionCard {
            SettingRow(
                title = stringResource(R.string.me_favorites),
                onClick = onFavorites,
                leading = {
                    IconTile(icon = Icons.Default.Star, tint = AccentAmber, size = 34.dp)
                }
            )
        }

        Spacer(Modifier.height(4.dp))
        SectionCard {
            SettingRow(
                title = stringResource(R.string.settings_title),
                onClick = onSettings,
                leading = {
                    IconTile(icon = Icons.Default.Settings, tint = AccentBlue, size = 34.dp)
                }
            )
        }

        Spacer(Modifier.height(4.dp))
        SectionCard {
            SettingRow(
                title = stringResource(R.string.settings_logout),
                onClick = onLogout,
                leading = {
                    IconTile(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        tint = AccentRed,
                        size = 34.dp
                    )
                }
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}
