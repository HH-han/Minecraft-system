package com.minecraft.im.data.repository

import com.minecraft.im.core.ApiException
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.remote.ApiProvider
import com.minecraft.im.data.remote.dto.FavoriteSaveRequest
import com.minecraft.im.data.toUi
import com.minecraft.im.domain.model.FavoriteType
import com.minecraft.im.domain.model.FavoriteUi
import javax.inject.Inject
import javax.inject.Singleton

/** 收藏：列表 / 统计 / 新增 / 删除 / 清空，接口与 Web 端「收藏」模块一致 */
@Singleton
class FavoriteRepository @Inject constructor(
    private val apiProvider: ApiProvider,
    private val session: SessionManager
) {
    private val userId: Long get() = session.currentUserId

    suspend fun list(
        category: String,
        tag: String?,
        keyword: String?,
        page: Int,
        size: Int
    ): Result<List<FavoriteUi>> = runCatching {
        val uid = requireUser()
        val res = apiProvider.api().getFavoriteList(
            userId = uid,
            type = FavoriteType.queryOf(category),
            tag = tag?.takeIf { it.isNotBlank() },
            keyword = keyword?.takeIf { it.isNotBlank() },
            page = page,
            size = size
        )
        if (res.code != 200) throw ApiException(res.message ?: "加载收藏失败")
        res.data.orEmpty().map { it.toUi() }
    }

    suspend fun recent(limit: Int = 30): Result<List<FavoriteUi>> = runCatching {
        val res = apiProvider.api().getRecentFavorites(requireUser(), limit)
        if (res.code != 200) throw ApiException(res.message ?: "加载收藏失败")
        res.data.orEmpty().map { it.toUi() }
    }

    suspend fun stats(): Result<Pair<Long, Map<String, Long>>> = runCatching {
        val res = apiProvider.api().getFavoriteStats(requireUser())
        if (res.code != 200) throw ApiException(res.message ?: "加载收藏统计失败")
        (res.data?.total ?: 0L) to res.data?.types.orEmpty()
    }

    suspend fun tags(): Result<List<String>> = runCatching {
        val res = apiProvider.api().getFavoriteTags(requireUser())
        if (res.code != 200) throw ApiException(res.message ?: "加载标签失败")
        res.data.orEmpty()
    }

    suspend fun add(
        type: String,
        title: String?,
        content: String?,
        url: String?,
        tags: List<String>
    ): Result<FavoriteUi> = runCatching {
        val res = apiProvider.api().addFavorite(
            FavoriteSaveRequest(
                userId = requireUser(),
                type = type,
                title = title?.takeIf { it.isNotBlank() },
                content = content?.takeIf { it.isNotBlank() },
                url = url?.takeIf { it.isNotBlank() },
                tags = tags,
                source = session.currentUser?.nickname?.takeIf { it.isNotBlank() }
                    ?: session.currentUser?.username
            )
        )
        if (res.code != 200) throw ApiException(res.message ?: "保存失败")
        res.data?.toUi() ?: throw ApiException("保存失败")
    }

    suspend fun touch(id: Long) {
        runCatching { apiProvider.api().touchFavorite(id, userId) }
    }

    suspend fun delete(id: Long): Result<Unit> = runCatching {
        val res = apiProvider.api().deleteFavorite(id, requireUser())
        if (res.code != 200) throw ApiException(res.message ?: "删除失败")
    }

    suspend fun clear(category: String): Result<Unit> = runCatching {
        val res = apiProvider.api().clearFavorites(
            userId = requireUser(),
            type = FavoriteType.queryOf(category)
        )
        if (res.code != 200) throw ApiException(res.message ?: "清空失败")
    }

    private fun requireUser(): Long {
        val uid = userId
        if (uid == 0L) throw ApiException("请先登录")
        return uid
    }
}
