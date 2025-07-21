package com.composesamples.data.repository

import com.composesamples.data.model.AppInfo

import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getInstalledApps(): Flow<List<AppInfo>>
    fun getAppDetails(packageName: String): Flow<AppInfo?>
}