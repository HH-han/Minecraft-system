package com.minecraft.im.ui.contact

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.R
import com.minecraft.im.data.repository.ImRepository
import com.minecraft.im.ui.components.EmptyState
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.TopBarBack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendRequestsViewModel @Inject constructor(
    private val repository: ImRepository
) : ViewModel() {

    val requests = repository.pendingRequests

    init {
        viewModelScope.launch { repository.refreshRequests() }
    }

    fun accept(requestId: Long) {
        viewModelScope.launch { repository.handleFriendRequest(requestId, true) }
    }

    fun reject(requestId: Long) {
        viewModelScope.launch { repository.handleFriendRequest(requestId, false) }
    }
}

@Composable
fun FriendRequestsScreen(
    onBack: () -> Unit,
    viewModel: FriendRequestsViewModel = hiltViewModel()
) {
    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    Scaffold(
        topBar = { TopBarBack(title = stringResource(R.string.requests_title), onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        if (requests.isEmpty()) {
            Column(Modifier.padding(padding).fillMaxSize()) {
                EmptyState(stringResource(R.string.requests_empty))
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(requests, key = { it.id }) { request ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImAvatar(name = request.name, avatar = request.avatar, size = 46.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = request.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = request.message?.takeIf { it.isNotBlank() }
                                ?: stringResource(R.string.requests_default_message),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.reject(request.id) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.requests_reject))
                        }
                        Button(
                            onClick = { viewModel.accept(request.id) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(stringResource(R.string.requests_accept))
                        }
                    }
                }
            }
        }
    }
}
