package com.minecraft.im.ui.components

import androidx.compose.runtime.staticCompositionLocalOf

/** 当前服务器地址（用于把 localhost 图片地址改写到可访问的域名） */
val LocalBaseUrl = staticCompositionLocalOf { "http://10.0.2.2:8080/api/" }
