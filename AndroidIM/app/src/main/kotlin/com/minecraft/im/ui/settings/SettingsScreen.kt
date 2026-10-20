package com.minecraft.im.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.BuildConfig
import com.minecraft.im.R
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.ThemeMode
import com.minecraft.im.data.repository.AuthRepository
import com.minecraft.im.ui.components.HorizontalDivider
import com.minecraft.im.ui.components.IconTile
import com.minecraft.im.ui.components.SectionCard
import com.minecraft.im.ui.components.SettingRow
import com.minecraft.im.ui.components.TopBarBack
import com.minecraft.im.ui.theme.AccentBlue
import com.minecraft.im.ui.theme.AccentPurple
import com.minecraft.im.ui.theme.AccentRed
import com.minecraft.im.ui.theme.AccentTeal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    val theme = sessionManager.theme
    val language = sessionManager.language
    val serverUrl: String get() = sessionManager.currentBaseUrl

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { sessionManager.setTheme(mode) }
    }

    fun setLanguage(language: String, onApplied: () -> Unit) {
        viewModelScope.launch {
            sessionManager.setLanguage(language)
            onApplied()
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBarBack(title = stringResource(R.string.settings_title), onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            SectionLabel(stringResource(R.string.settings_general))
            SectionCard {
                SettingRow(
                    title = stringResource(R.string.settings_theme),
                    subtitle = themeLabel(theme),
                    onClick = { showThemeDialog = true },
                    leading = {
                        IconTile(icon = Icons.Default.Palette, tint = AccentPurple, size = 34.dp)
                    }
                )
                HorizontalDivider(Modifier.padding(start = 62.dp))
                SettingRow(
                    title = stringResource(R.string.settings_language),
                    subtitle = languageLabel(language),
                    onClick = { showLanguageDialog = true },
                    leading = {
                        IconTile(icon = Icons.Default.Language, tint = AccentBlue, size = 34.dp)
                    }
                )
            }

            SectionLabel(stringResource(R.string.settings_account))
            SectionCard {
                SettingRow(
                    title = stringResource(R.string.settings_server),
                    subtitle = viewModel.serverUrl,
                    leading = {
                        IconTile(icon = Icons.Default.Dns, tint = AccentTeal, size = 34.dp)
                    }
                )
            }

            SectionLabel(stringResource(R.string.settings_about))
            SectionCard {
                SettingRow(
                    title = BuildConfig.APPLICATION_ID,
                    subtitle = stringResource(R.string.settings_version) + " " + BuildConfig.VERSION_NAME,
                    leading = {
                        IconTile(icon = Icons.Default.Info, tint = AccentBlue, size = 34.dp)
                    }
                )
            }

            Spacer(Modifier.height(12.dp))
            SectionCard {
                SettingRow(
                    title = stringResource(R.string.settings_logout),
                    onClick = { showLogoutConfirm = true },
                    titleColor = MaterialTheme.colorScheme.error,
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

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.settings_theme)) },
            text = {
                Column {
                    ThemeMode.entries.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(selected = theme == mode) {
                                    viewModel.setTheme(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = theme == mode,
                                onClick = {
                                    viewModel.setTheme(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(text = themeLabel(mode), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text(stringResource(R.string.action_close))
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column {
                    listOf("zh" to R.string.settings_language_zh, "en" to R.string.settings_language_en)
                        .forEach { (code, labelRes) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(selected = language == code) {
                                        applyLanguage(viewModel, code)
                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = language == code,
                                    onClick = {
                                        applyLanguage(viewModel, code)
                                        showLanguageDialog = false
                                    }
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = stringResource(labelRes),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.action_close))
                }
            }
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(stringResource(R.string.settings_logout)) },
            text = { Text(stringResource(R.string.settings_logout_confirm)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutConfirm = false
                    viewModel.logout()
                }) { Text(stringResource(R.string.action_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

private fun applyLanguage(viewModel: SettingsViewModel, code: String) {
    viewModel.setLanguage(code) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(if (code == "en") "en" else "zh-CN")
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 4.dp)
    )
}

@Composable
private fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_system)
    ThemeMode.LIGHT -> stringResource(R.string.settings_theme_light)
    ThemeMode.DARK -> stringResource(R.string.settings_theme_dark)
    ThemeMode.GAME -> stringResource(R.string.settings_theme_game)
}

@Composable
private fun languageLabel(language: String): String = if (language == "en") {
    stringResource(R.string.settings_language_en)
} else {
    stringResource(R.string.settings_language_zh)
}
