package com.composesamples.data.repository.impl

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

import androidx.core.util.TypedValueCompat

import com.composesamples.data.model.AppModel
import com.composesamples.data.repository.AppFilterType
import com.composesamples.data.repository.AppRepository
import com.composesamples.utils.Resource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

import me.zhanghai.android.appiconloader.AppIconLoader

class AppRepositoryImpl(private val context: Context) : AppRepository {
    private val packageManager = context.packageManager

    private val appIconSize = TypedValueCompat.dpToPx(40f, context.resources.displayMetrics).toInt()
    private val appIconLoader = AppIconLoader(appIconSize, true, context)

    private val cachedApps = mutableMapOf<String, AppModel>()
    private val cacheMutex = Mutex()

    override fun getInstalledApps(
        filterType: AppFilterType,
        force: Boolean
    ): Flow<Resource<List<AppModel>>> = flow {
        emit(Resource.Loading())

        try {
            val apps = withContext(Dispatchers.IO) {
                cacheMutex.withLock {
                    if (cachedApps.isEmpty() || force) {
                        cachedApps.clear()

                        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                            .filterNot { info -> info.packageName == context.packageName }
                            .forEach { info ->
                                val app = AppModel(
                                    name = info.loadLabel(packageManager).toString(),
                                    packageName = info.packageName,
                                    icon = appIconLoader.loadIcon(info),
                                    isSystemApp = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                                )
                                cachedApps[app.packageName] = app
                            }
                    }
                    cachedApps.values.toList()
                }
            }

            val filteredApps = when (filterType) {
                AppFilterType.ALL -> apps
                AppFilterType.USER -> apps.filterNot { app -> app.isSystemApp }
                AppFilterType.SYSTEM -> apps.filter { app -> app.isSystemApp }
            }

            val sortedApps = filteredApps.sortedBy { app -> app.name }

            delay(300)
            emit(Resource.Success(sortedApps))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to get installed apps: ${e.message ?: "Unknown error"}"))
        }
    }

    override fun getAppDetails(packageName: String): Flow<Resource<AppModel>> = flow {
        emit(Resource.Loading())

        val app = cacheMutex.withLock {
            cachedApps[packageName]
        }

        if (app == null) {
            try {
                withContext(Dispatchers.IO) {
                    val info = packageManager
                        .getApplicationInfo(packageName, PackageManager.GET_META_DATA)

                    val app = AppModel(
                        name = info.loadLabel(packageManager).toString(),
                        packageName = info.packageName,
                        icon = appIconLoader.loadIcon(info),
                        isSystemApp = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
                    )

                    cacheMutex.withLock {
                        cachedApps[packageName] = app
                    }
                    emit(Resource.Success(app))
                }
            } catch (_: PackageManager.NameNotFoundException) {
                emit(Resource.Error("App with package name '$packageName' not found."))
            } catch (e: Exception) {
                emit(Resource.Error("Failed to get app details for '$packageName': ${e.message ?: "Unknown error"}"))
            }
        } else {
            emit(Resource.Success(app))
        }
    }
}