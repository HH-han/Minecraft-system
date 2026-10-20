package com.minecraft.im

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minecraft.im.data.SessionManager
import com.minecraft.im.data.repository.ImRepository
import com.minecraft.im.ui.components.LocalBaseUrl
import com.minecraft.im.ui.navigation.ImAppRoot
import com.minecraft.im.ui.theme.MinecraftImTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var imRepository: ImRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        imRepository.start()
        setContent {
            val session by sessionManager.session.collectAsStateWithLifecycle()
            val theme by sessionManager.theme.collectAsStateWithLifecycle()
            CompositionLocalProvider(LocalBaseUrl provides sessionManager.currentBaseUrl) {
                MinecraftImTheme(mode = theme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ImAppRoot(session = session)
                    }
                }
            }
        }
    }
}
