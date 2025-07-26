package com.composesamples.data.repository

import com.composesamples.data.model.AppMetadata
import com.composesamples.utils.Resource

import kotlinx.coroutines.flow.Flow

enum class AppFilterType {
    ALL,
    USER,
    SYSTEM
}

enum class AppSortOrder {
    NAME_ASCENDING,
    NAME_DESCENDING,
    INSTALL_DATE_ASCENDING,
    INSTALL_DATE_DESCENDING
}

interface AppRepository {
    fun getInstalledApps(
        filterType: AppFilterType = AppFilterType.ALL,
        sortOrder: AppSortOrder = AppSortOrder.INSTALL_DATE_DESCENDING,
        force: Boolean = false
    ): Flow<Resource<List<AppMetadata>>>

    fun getAppDetails(packageName: String): Flow<Resource<AppMetadata>>
}