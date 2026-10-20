package com.minecraft.im.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.minecraft.im.data.SessionState
import com.minecraft.im.ui.chat.ChatScreen
import com.minecraft.im.ui.components.LoadingScreen
import com.minecraft.im.ui.contact.AddFriendScreen
import com.minecraft.im.ui.contact.ContactDetailScreen
import com.minecraft.im.ui.contact.FriendRequestsScreen
import com.minecraft.im.ui.group.CreateGroupScreen
import com.minecraft.im.ui.group.GroupInfoScreen
import com.minecraft.im.ui.home.HomeScreen
import com.minecraft.im.ui.login.LoginScreen
import com.minecraft.im.ui.moments.MomentsScreen
import com.minecraft.im.ui.profile.ProfileScreen
import com.minecraft.im.ui.settings.SettingsScreen

object ImRoutes {
    const val HOME = "home"
    const val CHAT = "chat/{contactId}/{isGroup}"
    const val CONTACT_DETAIL = "contact/{contactId}/{isGroup}"
    const val GROUP_INFO = "group/{groupId}"
    const val ADD_FRIEND = "add_friend"
    const val FRIEND_REQUESTS = "friend_requests"
    const val CREATE_GROUP = "create_group"
    const val MOMENTS = "moments"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    fun chat(contactId: Long, isGroup: Boolean) = "chat/$contactId/$isGroup"
    fun contactDetail(contactId: Long, isGroup: Boolean) = "contact/$contactId/$isGroup"
    fun groupInfo(groupId: Long) = "group/$groupId"
}

/** 根据登录态切换登录页与主界面 */
@Composable
fun ImAppRoot(session: SessionState) {
    when (session) {
        SessionState.Loading -> LoadingScreen()
        SessionState.LoggedOut -> LoginScreen()
        is SessionState.LoggedIn -> MainNavigation()
    }
}

@Composable
private fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ImRoutes.HOME) {
        composable(ImRoutes.HOME) {
            HomeScreen(
                onOpenChat = { contactId, isGroup ->
                    navController.navigate(ImRoutes.chat(contactId, isGroup))
                },
                onAddFriend = { navController.navigate(ImRoutes.ADD_FRIEND) },
                onFriendRequests = { navController.navigate(ImRoutes.FRIEND_REQUESTS) },
                onCreateGroup = { navController.navigate(ImRoutes.CREATE_GROUP) },
                onMoments = { navController.navigate(ImRoutes.MOMENTS) },
                onProfile = { navController.navigate(ImRoutes.PROFILE) },
                onSettings = { navController.navigate(ImRoutes.SETTINGS) }
            )
        }

        composable(
            route = ImRoutes.CHAT,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType },
                navArgument("isGroup") { type = NavType.BoolType }
            )
        ) { entry ->
            val contactId = entry.arguments?.getLong("contactId") ?: 0L
            val isGroup = entry.arguments?.getBoolean("isGroup") ?: false
            ChatScreen(
                contactId = contactId,
                isGroup = isGroup,
                onBack = { navController.popBackStack() },
                onOpenProfile = { id, group ->
                    navController.navigate(ImRoutes.contactDetail(id, group))
                }
            )
        }

        composable(
            route = ImRoutes.CONTACT_DETAIL,
            arguments = listOf(
                navArgument("contactId") { type = NavType.LongType },
                navArgument("isGroup") { type = NavType.BoolType }
            )
        ) { entry ->
            val contactId = entry.arguments?.getLong("contactId") ?: 0L
            val isGroup = entry.arguments?.getBoolean("isGroup") ?: false
            ContactDetailScreen(
                contactId = contactId,
                isGroup = isGroup,
                onBack = { navController.popBackStack() },
                onSendMessage = { id, group ->
                    navController.navigate(ImRoutes.chat(id, group)) {
                        launchSingleTop = true
                    }
                },
                onOpenGroupInfo = { groupId ->
                    navController.navigate(ImRoutes.groupInfo(groupId))
                }
            )
        }

        composable(ImRoutes.ADD_FRIEND) {
            AddFriendScreen(onBack = { navController.popBackStack() })
        }

        composable(ImRoutes.FRIEND_REQUESTS) {
            FriendRequestsScreen(onBack = { navController.popBackStack() })
        }

        composable(ImRoutes.CREATE_GROUP) {
            CreateGroupScreen(
                onBack = { navController.popBackStack() },
                onCreated = { contact ->
                    navController.navigate(ImRoutes.chat(contact.id, true)) {
                        popUpTo(ImRoutes.HOME)
                    }
                }
            )
        }

        composable(
            route = ImRoutes.GROUP_INFO,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType })
        ) { entry ->
            val groupId = entry.arguments?.getLong("groupId") ?: 0L
            GroupInfoScreen(groupId = groupId, onBack = { navController.popBackStack() })
        }

        composable(ImRoutes.MOMENTS) {
            MomentsScreen(onBack = { navController.popBackStack() })
        }

        composable(ImRoutes.PROFILE) {
            ProfileScreen(onBack = { navController.popBackStack() })
        }

        composable(ImRoutes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
