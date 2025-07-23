package com.composesamples.data.repository

import com.composesamples.data.model.AppModel
import com.composesamples.utils.Resource

import kotlinx.coroutines.flow.Flow

enum class AppFilterType {
    ALL,
    USER,
    SYSTEM
}

interface AppRepository {
    fun getInstalledApps(filterType: AppFilterType): Flow<Resource<List<AppModel>>>
    fun getAppDetails(packageName: String): Flow<Resource<AppModel>>
}