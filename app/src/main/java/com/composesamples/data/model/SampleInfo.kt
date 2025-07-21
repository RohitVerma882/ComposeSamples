package com.composesamples.data.model

import androidx.annotation.StringRes

import com.composesamples.ui.navigation.AppRoutes

data class SampleInfo(
    @StringRes val name: Int,
    val appRoute: AppRoutes
)