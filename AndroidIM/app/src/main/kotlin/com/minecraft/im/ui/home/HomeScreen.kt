package com.minecraft.im.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.R
import com.minecraft.im.ui.components.SearchField

/** 主界面：消息 / 通讯录 / 发现 / 我 */
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
    var searchOpen by rememberSaveable { mutableStateOf(false) }
    var starredOnly by rememberSaveable { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showGroupSheet by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.refresh() }

    LaunchedEffect(tab) {
        if (tab != 0 && tab != 1) {
            searchOpen = false
            query = ""
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = { Text(text = homeTitle(tab)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        if (tab == 0 || tab == 1) {
                            IconButton(onClick = { searchOpen = !searchOpen }) {
                                Icon(Icons.Default.Search, contentDescription = stringResource(R.string.action_search))
                            }
                        }
                        if (tab == 0) {
                            Box {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_more))
                                }
                                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.menu_create_group)) },
                                        leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            onCreateGroup()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.menu_add_friend)) },
                                        leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                                        onClick = {
                                            showMenu = false
                                            onAddFriend()
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.menu_friend_requests)) },
                                        leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
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
                if (searchOpen && (tab == 0 || tab == 1)) {
                    SearchField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = stringResource(R.string.home_search_hint)
                    )
                }
                androidx.compose.material3.HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                HomeTabItem(
                    selected = tab == 0,
                    label = stringResource(R.string.tab_messages),
                    icon = Icons.AutoMirrored.Filled.Chat,
                    badge = totalUnread,
                    onClick = { tab = 0 }
                )
                HomeTabItem(
                    selected = tab == 1,
                    label = stringResource(R.string.tab_contacts),
                    icon = Icons.Default.People,
                    onClick = { tab = 1 }
                )
                HomeTabItem(
                    selected = tab == 2,
                    label = stringResource(R.string.tab_discover),
                    icon = Icons.Default.Explore,
                    onClick = { tab = 2 }
                )
                HomeTabItem(
                    selected = tab == 3,
                    label = stringResource(R.string.tab_me),
                    icon = Icons.Default.Person,
                    onClick = { tab = 3 }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
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

                else -> MeTab(
                    name = currentUser?.nickname?.takeIf { it.isNotBlank() }
                        ?: currentUser?.username.orEmpty(),
                    account = currentUser?.account.orEmpty(),
                    avatar = currentUser?.avatar,
                    onProfile = onProfile,
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
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(stringResource(R.string.settings_logout)) },
            text = { Text(stringResource(R.string.settings_logout_confirm)) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    }
                ) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showLogoutConfirm = false }) {
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
    else -> stringResource(R.string.tab_me)
}

@Composable
private fun RowScope.HomeTabItem(
    selected: Boolean,
    label: String,
    icon: ImageVector,
    badge: Int = 0,
    onClick: () -> Unit
) {
    val tint = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                if (badge > 0) {
                    Badge { Text(if (badge > 99) "99+" else badge.toString()) }
                }
            }
        ) {
            Icon(icon, contentDescription = label, tint = tint)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint
        )
    }
}
