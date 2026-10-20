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
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
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

private const val TAB_MESSAGES = 0
private const val TAB_CONTACTS = 1
private const val TAB_DISCOVER = 2
private const val TAB_ME = 3

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
    onFavorites: () -> Unit,
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

    var tab by rememberSaveable { mutableIntStateOf(TAB_MESSAGES) }
    var query by rememberSaveable { mutableStateOf("") }
    var searchActive by rememberSaveable { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showGroupSheet by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.refresh() }

    LaunchedEffect(tab) {
        query = ""
        searchActive = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                when (tab) {
                    TAB_MESSAGES -> {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.tab_messages),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            actions = {
                                AddMenuButton(
                                    expanded = showMenu,
                                    onExpandedChange = { showMenu = it },
                                    onCreateGroup = onCreateGroup,
                                    onAddFriend = onAddFriend,
                                    onFriendRequests = onFriendRequests
                                )
                            }
                        )
                        SearchField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = stringResource(R.string.home_search_hint)
                        )
                    }

                    TAB_CONTACTS -> {
                        CenterTopBar(
                            title = stringResource(R.string.tab_contacts),
                            onSearch = { searchActive = !searchActive },
                            expanded = showMenu,
                            onExpandedChange = { showMenu = it },
                            onCreateGroup = onCreateGroup,
                            onAddFriend = onAddFriend,
                            onFriendRequests = onFriendRequests
                        )
                        if (searchActive) SearchField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = stringResource(R.string.home_search_hint)
                        )
                    }

                    TAB_DISCOVER -> {
                        CenterTopBar(
                            title = stringResource(R.string.tab_discover),
                            onSearch = { searchActive = !searchActive },
                            expanded = showMenu,
                            onExpandedChange = { showMenu = it },
                            menuItems = {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.moments_publish)) },
                                    leadingIcon = {
                                        Icon(Icons.Default.Explore, contentDescription = null)
                                    },
                                    onClick = {
                                        showMenu = false
                                        onMoments()
                                    }
                                )
                            }
                        )
                        if (searchActive) SearchField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = stringResource(R.string.home_search_hint)
                        )
                    }
                }
                if (tab != TAB_ME) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                HomeTabItem(
                    selected = tab == TAB_MESSAGES,
                    label = stringResource(R.string.tab_messages),
                    selectedIcon = Icons.AutoMirrored.Filled.Chat,
                    unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                    badge = totalUnread,
                    onClick = { tab = TAB_MESSAGES }
                )
                HomeTabItem(
                    selected = tab == TAB_CONTACTS,
                    label = stringResource(R.string.tab_contacts),
                    selectedIcon = Icons.Default.People,
                    unselectedIcon = Icons.Outlined.PeopleOutline,
                    badge = requests.size,
                    onClick = { tab = TAB_CONTACTS }
                )
                HomeTabItem(
                    selected = tab == TAB_DISCOVER,
                    label = stringResource(R.string.tab_discover),
                    selectedIcon = Icons.Default.Explore,
                    unselectedIcon = Icons.Outlined.Explore,
                    onClick = { tab = TAB_DISCOVER }
                )
                HomeTabItem(
                    selected = tab == TAB_ME,
                    label = stringResource(R.string.tab_me),
                    selectedIcon = Icons.Default.Person,
                    unselectedIcon = Icons.Outlined.PersonOutline,
                    onClick = { tab = TAB_ME }
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
                TAB_MESSAGES -> ConversationsTab(
                    conversations = conversations,
                    query = query,
                    onOpen = { contact ->
                        viewModel.openConversation(contact)
                        onOpenChat(contact.id, contact.isGroup)
                    }
                )

                TAB_CONTACTS -> ContactsTab(
                    friends = friends,
                    groups = groups,
                    requests = requests,
                    starred = starred,
                    query = query,
                    onToggleStar = viewModel::toggleStar,
                    onOpenChat = { contact ->
                        viewModel.openConversation(contact)
                        onOpenChat(contact.id, contact.isGroup)
                    },
                    onFriendRequests = onFriendRequests,
                    onShowGroups = { showGroupSheet = true }
                )

                TAB_DISCOVER -> DiscoverTab(
                    query = query,
                    selfName = currentUser?.nickname?.takeIf { it.isNotBlank() }
                        ?: currentUser?.username.orEmpty(),
                    selfAvatar = currentUser?.avatar,
                    onMoments = onMoments
                )

                else -> MeTab(
                    name = currentUser?.nickname?.takeIf { it.isNotBlank() }
                        ?: currentUser?.username.orEmpty(),
                    account = currentUser?.account.orEmpty(),
                    avatar = currentUser?.avatar,
                    unread = totalUnread,
                    onProfile = onProfile,
                    onFavorites = onFavorites,
                    onMoments = onMoments,
                    onSettings = onSettings
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
}

/** 居中式顶栏：搜索 + 圆形加号菜单 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CenterTopBar(
    title: String,
    onSearch: () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCreateGroup: (() -> Unit)? = null,
    onAddFriend: (() -> Unit)? = null,
    onFriendRequests: (() -> Unit)? = null,
    menuItems: (@Composable () -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        actions = {
            IconButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.action_search),
                    modifier = Modifier.size(21.dp)
                )
            }
            Box {
                IconButton(onClick = { onExpandedChange(true) }) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircleOutline,
                        contentDescription = stringResource(R.string.action_more),
                        modifier = Modifier.size(22.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    if (menuItems != null) {
                        menuItems()
                    } else {
                        if (onAddFriend != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_add_friend)) },
                                leadingIcon = {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                                },
                                onClick = {
                                    onExpandedChange(false)
                                    onAddFriend()
                                }
                            )
                        }
                        if (onCreateGroup != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_create_group)) },
                                leadingIcon = {
                                    Icon(Icons.Default.GroupAdd, contentDescription = null)
                                },
                                onClick = {
                                    onExpandedChange(false)
                                    onCreateGroup()
                                }
                            )
                        }
                        if (onFriendRequests != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.menu_friend_requests)) },
                                leadingIcon = {
                                    Icon(Icons.Default.People, contentDescription = null)
                                },
                                onClick = {
                                    onExpandedChange(false)
                                    onFriendRequests()
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

/** 消息页的加号菜单 */
@Composable
private fun AddMenuButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onCreateGroup: () -> Unit,
    onAddFriend: () -> Unit,
    onFriendRequests: () -> Unit
) {
    Box {
        IconButton(onClick = { onExpandedChange(true) }) {
            Icon(
                imageVector = Icons.Outlined.AddCircleOutline,
                contentDescription = stringResource(R.string.action_more),
                modifier = Modifier.size(22.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_create_group)) },
                leadingIcon = { Icon(Icons.Default.GroupAdd, contentDescription = null) },
                onClick = {
                    onExpandedChange(false)
                    onCreateGroup()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_add_friend)) },
                leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                onClick = {
                    onExpandedChange(false)
                    onAddFriend()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_friend_requests)) },
                leadingIcon = { Icon(Icons.Default.People, contentDescription = null) },
                onClick = {
                    onExpandedChange(false)
                    onFriendRequests()
                }
            )
        }
    }
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
