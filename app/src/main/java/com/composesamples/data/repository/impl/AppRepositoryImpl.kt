package com.composesamples.data.repository.impl

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

import com.composesamples.data.model.AppInfo
import com.composesamples.data.repository.AppRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class AppRepositoryImpl(private val context: Context) : AppRepository {
    private val packageManager: PackageManager = context.packageManager

    override fun getInstalledApps(): Flow<List<AppInfo>> = flow {
        val apps = withContext(Dispatchers.IO) {
            val appInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            appInfos
                .filterNot { it.packageName == context.packageName }
                .map { appInfo ->
                    val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                    AppInfo(
                        name = appInfo.loadLabel(packageManager).toString(),
                        packageName = appInfo.packageName,
                        icon = appInfo.loadIcon(packageManager),
                        isSystemApp = isSystemApp,
                    )
                }
        }
        emit(apps)
    }

    override fun getAppDetails(packageName: String): Flow<AppInfo?> = flow {
        val appInfo = withContext(Dispatchers.IO) {
            try {
                val appInfo =
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                AppInfo(
                    name = appInfo.loadLabel(packageManager).toString(),
                    packageName = appInfo.packageName,
                    icon = appInfo.loadIcon(packageManager),
                    isSystemApp = isSystemApp,
                )
            } catch (_: PackageManager.NameNotFoundException) {
                null
            }
        }
        emit(appInfo)
    }
}