package com.minecraft.im.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.minecraft.im.R
import com.minecraft.im.ui.components.GroupCard
import com.minecraft.im.ui.components.HorizontalDivider
import com.minecraft.im.ui.components.IconTile
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.NavRow
import com.minecraft.im.ui.theme.AccentAmber
import com.minecraft.im.ui.theme.AccentBlue
import com.minecraft.im.ui.theme.AccentPink
import com.minecraft.im.ui.theme.AccentPurple
import com.minecraft.im.ui.theme.AccentRed
import com.minecraft.im.ui.theme.AccentTeal
import com.minecraft.im.ui.theme.EmberOrange

private data class DiscoverEntry(
    val key: String,
    val title: String,
    val icon: ImageVector,
    val tint: Color,
    val badge: Int = 0,
    val onClick: (() -> Unit)?
)

/** 发现：朋友圈、视频号、直播、扫一扫、听一听、看一看、搜一搜、附近的人、游戏、小程序 */
@Composable
fun DiscoverTab(
    query: String,
    selfName: String,
    selfAvatar: String?,
    onMoments: () -> Unit
) {
    val context = LocalContext.current
    val unavailableText = stringResource(R.string.feature_unavailable)
    val unavailable = { Toast.makeText(context, unavailableText, Toast.LENGTH_SHORT).show() }

    val entries = listOf(
        DiscoverEntry("moments", stringResource(R.string.moments_title), Icons.Default.CameraAlt, AccentTeal, onClick = onMoments),
        DiscoverEntry("channels", stringResource(R.string.discover_channels), Icons.Default.PlayCircle, EmberOrange, badge = 5, onClick = unavailable),
        DiscoverEntry("live", stringResource(R.string.discover_live), Icons.Default.Podcasts, EmberOrange, onClick = unavailable),
        DiscoverEntry("scan", stringResource(R.string.discover_scan), Icons.Default.QrCodeScanner, AccentBlue, onClick = unavailable),
        DiscoverEntry("listen", stringResource(R.string.discover_listen), Icons.Default.MusicNote, AccentPink, onClick = unavailable),
        DiscoverEntry("look", stringResource(R.string.discover_look), Icons.Default.WbSunny, AccentAmber, onClick = unavailable),
        DiscoverEntry("search", stringResource(R.string.discover_search), Icons.Default.Search, AccentRed, onClick = unavailable),
        DiscoverEntry("nearby", stringResource(R.string.discover_nearby), Icons.Default.PeopleAlt, AccentBlue, onClick = unavailable),
        DiscoverEntry("games", stringResource(R.string.discover_games), Icons.Default.SportsEsports, AccentTeal, onClick = unavailable),
        DiscoverEntry("mini", stringResource(R.string.discover_mini_programs), Icons.Default.Apps, AccentPurple, onClick = unavailable)
    )

    val filtered = if (query.isBlank()) entries
    else entries.filter { it.title.contains(query, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        GroupCard {
            filtered.forEachIndexed { index, entry ->
                if (index > 0) HorizontalDivider(Modifier.padding(start = 58.dp))
                NavRow(
                    title = entry.title,
                    badge = entry.badge,
                    onClick = entry.onClick,
                    leftContent = {
                        if (entry.key == "moments") {
                            MomentsTile()
                        } else {
                            IconTile(
                                icon = entry.icon,
                                tint = entry.tint,
                                size = 30.dp,
                                solid = true
                            )
                        }
                    },
                    trailing = if (entry.key == "moments") {
                        {
                            ImAvatar(
                                name = selfName.ifBlank { "?" },
                                avatar = selfAvatar,
                                size = 26.dp
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

/** 朋友圈入口：彩色渐变圆角图标 */
@Composable
private fun MomentsTile() {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(
                Brush.sweepGradient(
                    listOf(
                        Color(0xFF4CAF50),
                        Color(0xFF03A9F4),
                        Color(0xFF9C27B0),
                        Color(0xFFFF9800),
                        Color(0xFF4CAF50)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(17.dp)
        )
    }
}
