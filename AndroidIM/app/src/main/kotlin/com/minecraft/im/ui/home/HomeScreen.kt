package com.minecraft.im.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.R
import com.minecraft.im.ui.components.SearchField
import com.minecraft.im.ui.favorites.FavoritesScreen

/** 主界面：消息 / 通讯录 / 发现 / 收藏 / 我 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenChat: (Long, Boolean) -> Unit,
    onAddFriend: () -> Unit,
    onFriendRequests: () -> Unit,
    onCreateGroup: () -> Unit,
    onMoments: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val starred by viewModel.starred.collectAsStateWithLifecycle()
    val totalUnread by viewModel.totalUnread.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var tab by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var starredOnly by rememberSaveable { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showGroupSheet by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.refresh() }

    LaunchedEffect(tab) {
        if (tab != 0 && tab != 1) query = ""
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = {
                        Text(
                            text = homeTitle(tab),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        if (tab == 0) {
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = stringResource(R.string.action_more)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.menu_create_group)) },
                                        leadingIcon = {
                                            Icon(Icons.Default.GroupAdd, contentDescription = null)
                                        },
                                        onClick = {
                                            showMenu = false
                                            onCreateGroup()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.menu_add_friend)) },
                                        leadingIcon = {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                                        },
                                        onClick = {
                                            showMenu = false
                                            onAddFriend()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.menu_friend_requests)) },
                                        leadingIcon = {
                                            Icon(Icons.Default.People, contentDescription = null)
                                        },
                                        onClick = {
                                            showMenu = false
                                            onFriendRequests()
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
                if (tab == 0 || tab == 1) {
                    SearchField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = stringResource(R.string.home_search_hint)
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                HomeTabItem(
                    selected = tab == 0,
                    label = stringResource(R.string.tab_messages),
                    selectedIcon = Icons.AutoMirrored.Filled.Chat,
                    unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                    badge = totalUnread,
                    onClick = { tab = 0 }
                )
                HomeTabItem(
                    selected = tab == 1,
                    label = stringResource(R.string.tab_contacts),
                    selectedIcon = Icons.Default.People,
                    unselectedIcon = Icons.Outlined.PeopleOutline,
                    badge = requests.size,
                    onClick = { tab = 1 }
                )
                HomeTabItem(
                    selected = tab == 2,
                    label = stringResource(R.string.tab_discover),
                    selectedIcon = Icons.Default.Explore,
                    unselectedIcon = Icons.Outlined.Explore,
                    onClick = { tab = 2 }
                )
                HomeTabItem(
                    selected = tab == 3,
                    label = stringResource(R.string.tab_favorites),
                    selectedIcon = Icons.Default.Star,
                    unselectedIcon = Icons.Outlined.StarBorder,
                    onClick = { tab = 3 }
                )
                HomeTabItem(
                    selected = tab == 4,
                    label = stringResource(R.string.tab_me),
                    selectedIcon = Icons.Default.Person,
                    unselectedIcon = Icons.Outlined.PersonOutline,
                    onClick = { tab = 4 }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (tab) {
                0 -> ConversationsTab(
                    conversations = conversations,
                    query = query,
                    onOpen = { contact ->
                        viewModel.openConversation(contact)
                        onOpenChat(contact.id, contact.isGroup)
                    }
                )

                1 -> ContactsTab(
                    friends = friends,
                    groups = groups,
                    requests = requests,
                    starred = starred,
                    query = query,
                    starredOnly = starredOnly,
                    onToggleStarredOnly = { starredOnly = !starredOnly },
                    onToggleStar = viewModel::toggleStar,
                    onOpenChat = { contact ->
                        viewModel.openConversation(contact)
                        onOpenChat(contact.id, contact.isGroup)
                    },
                    onFriendRequests = onFriendRequests,
                    onShowGroups = { showGroupSheet = true }
                )

                2 -> DiscoverTab(onMoments = onMoments)

                3 -> FavoritesScreen()

                else -> MeTab(
                    name = currentUser?.nickname?.takeIf { it.isNotBlank() }
                        ?: currentUser?.username.orEmpty(),
                    account = currentUser?.account.orEmpty(),
                    avatar = currentUser?.avatar,
                    onProfile = onProfile,
                    onFavorites = { tab = 3 },
                    onSettings = onSettings,
                    onLogout = { showLogoutConfirm = true }
                )
            }
        }
    }

    if (showGroupSheet) {
        GroupListSheet(
            groups = groups,
            onDismiss = { showGroupSheet = false },
            onSelect = { contact ->
                showGroupSheet = false
                viewModel.openConversation(contact)
                onOpenChat(contact.id, true)
            }
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(stringResource(R.string.settings_logout)) },
            text = { Text(stringResource(R.string.settings_logout_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    }
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun homeTitle(tab: Int): String = when (tab) {
    0 -> stringResource(R.string.tab_messages)
    1 -> stringResource(R.string.tab_contacts)
    2 -> stringResource(R.string.tab_discover)
    3 -> stringResource(R.string.tab_favorites)
    else -> stringResource(R.string.tab_me)
}

@Composable
private fun RowScope.HomeTabItem(
    selected: Boolean,
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    badge: Int = 0,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            BadgedBox(
                badge = {
                    if (badge > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text(if (badge > 99) "99+" else badge.toString())
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = if (selected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
