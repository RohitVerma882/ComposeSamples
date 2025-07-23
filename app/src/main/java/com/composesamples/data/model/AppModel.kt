package com.composesamples.data.model

import android.graphics.Bitmap

data class AppModel(
    val name: String,
    val packageName: String,
    val icon: Bitmap,
    val isSystemApp: Boolean
)
