package com.minecraft.im.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.R
import com.minecraft.im.ui.components.ImAvatar
import com.minecraft.im.ui.components.TopBarBack
import com.minecraft.im.ui.theme.GrassGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val bytes = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }.getOrNull()
            }
            if (bytes == null) {
                snackbar.showSnackbar(context.getString(R.string.image_pick_failed))
                return@launch
            }
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            viewModel.uploadAvatar(bytes, mime, "avatar_${System.currentTimeMillis()}.jpg") { error ->
                if (error != null) {
                    scope.launch { snackbar.showSnackbar(error) }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBarBack(
                title = stringResource(R.string.profile_title),
                onBack = onBack,
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.save { error ->
                                scope.launch {
                                    snackbar.showSnackbar(
                                        error ?: context.getString(R.string.profile_saved)
                                    )
                                }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                ImAvatar(
                    name = state.user.nickname ?: state.user.username.orEmpty(),
                    avatar = state.user.avatar,
                    size = 88.dp
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(GrassGreen)
                        .clickable {
                            picker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.uploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = stringResource(R.string.profile_change_avatar),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.profile_change_avatar),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.profile_gender),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )
                val options = listOf(
                    "0" to stringResource(R.string.gender_unknown),
                    "1" to stringResource(R.string.gender_male),
                    "2" to stringResource(R.string.gender_female)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    options.forEach { (value, label) ->
                        FilterChip(
                            selected = (state.user.gender ?: "0") == value,
                            onClick = { viewModel.update { it.copy(gender = value) } },
                            label = { Text(label) }
                        )
                    }
                }
            }

            ProfileField(
                label = stringResource(R.string.profile_nickname),
                value = state.user.nickname.orEmpty(),
                onChange = { value -> viewModel.update { it.copy(nickname = value) } }
            )
            ProfileField(
                label = stringResource(R.string.profile_age),
                value = state.user.age?.toString().orEmpty(),
                keyboardType = KeyboardType.Number,
                onChange = { value ->
                    viewModel.update { it.copy(age = value.toIntOrNull()) }
                }
            )
            ProfileField(
                label = stringResource(R.string.profile_phone),
                value = state.user.phone.orEmpty(),
                keyboardType = KeyboardType.Phone,
                onChange = { value -> viewModel.update { it.copy(phone = value) } }
            )
            ProfileField(
                label = stringResource(R.string.profile_email),
                value = state.user.email.orEmpty(),
                keyboardType = KeyboardType.Email,
                onChange = { value -> viewModel.update { it.copy(email = value) } }
            )
            ProfileField(
                label = stringResource(R.string.profile_occupation),
                value = state.user.occupation.orEmpty(),
                onChange = { value -> viewModel.update { it.copy(occupation = value) } }
            )
            ProfileField(
                label = stringResource(R.string.profile_signature),
                value = state.user.signature.orEmpty(),
                onChange = { value -> viewModel.update { it.copy(signature = value) } }
            )
            ProfileField(
                label = stringResource(R.string.profile_hobbies),
                value = state.user.hobbies.orEmpty(),
                onChange = { value -> viewModel.update { it.copy(hobbies = value) } }
            )
            ProfileField(
                label = stringResource(R.string.profile_bio),
                value = state.user.bio.orEmpty(),
                maxLines = 3,
                onChange = { value -> viewModel.update { it.copy(bio = value) } }
            )
            ProfileField(
                label = stringResource(R.string.profile_experience),
                value = state.user.experience.orEmpty(),
                maxLines = 3,
                onChange = { value -> viewModel.update { it.copy(experience = value) } }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = maxLines == 1,
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    )
}
