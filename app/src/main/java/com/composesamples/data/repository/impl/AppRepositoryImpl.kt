package com.composesamples.data.repository.impl

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

import androidx.core.util.TypedValueCompat

import com.composesamples.data.model.AppMetadata
import com.composesamples.data.repository.AppFilterType
import com.composesamples.data.repository.AppRepository
import com.composesamples.data.repository.AppSortOrder
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

    private val cachedApps = mutableMapOf<String, AppMetadata>()
    private val cacheMutex = Mutex()

    override fun getInstalledApps(
        filterType: AppFilterType,
        sortOrder: AppSortOrder,
        force: Boolean
    ): Flow<Resource<List<AppMetadata>>> = flow {
        emit(Resource.Loading())

        try {
            val apps = withContext(Dispatchers.IO) {
                cacheMutex.withLock {
                    if (cachedApps.isEmpty() || force) {
                        cachedApps.clear()

                        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                            .filterNot { appInfo -> appInfo.packageName == context.packageName }
                            .forEach { appInfo ->
                                val pkgInfo = packageManager.getPackageInfo(appInfo.packageName, 0)

                                val isSystemApp =
                                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                                val app = AppMetadata(
                                    name = appInfo.loadLabel(packageManager).toString(),
                                    packageName = appInfo.packageName,
                                    icon = appIconLoader.loadIcon(appInfo),
                                    isSystemApp = isSystemApp,
                                    installTime = pkgInfo.firstInstallTime
                                )
                                cachedApps[app.packageName] = app
                            }
                    }
                    cachedApps.values.toList()
                }
            }

            val filteredApps = when (filterType) {
                AppFilterType.ALL -> {
                    apps
                }

                AppFilterType.USER -> {
                    apps.filterNot { app -> app.isSystemApp }
                }

                AppFilterType.SYSTEM -> {
                    apps.filter { app -> app.isSystemApp }
                }
            }

            val sortedApps = filteredApps.sortedWith { app1, app2 ->
                when (sortOrder) {
                    AppSortOrder.NAME_ASCENDING -> {
                        app1.name.compareTo(app2.name, ignoreCase = true)
                    }

                    AppSortOrder.NAME_DESCENDING -> {
                        app2.name.compareTo(app1.name, ignoreCase = true)
                    }

                    AppSortOrder.INSTALL_DATE_ASCENDING -> {
                        app1.installTime.compareTo(app2.installTime)
                    }

                    AppSortOrder.INSTALL_DATE_DESCENDING -> {
                        app2.installTime.compareTo(app1.installTime)
                    }
                }
            }

            delay(400)
            emit(Resource.Success(sortedApps))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to get installed apps: ${e.message ?: "Unknown error"}"))
        }
    }

    override fun getAppDetails(packageName: String): Flow<Resource<AppMetadata>> = flow {
        emit(Resource.Loading())

        val app = cacheMutex.withLock {
            cachedApps[packageName]
        }

        if (app == null) {
            try {
                withContext(Dispatchers.IO) {
                    val appInfo =
                        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    val pkgInfo = packageManager.getPackageInfo(appInfo.packageName, 0)

                    val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                    val app = AppMetadata(
                        name = appInfo.loadLabel(packageManager).toString(),
                        packageName = appInfo.packageName,
                        icon = appIconLoader.loadIcon(appInfo),
                        isSystemApp = isSystemApp,
                        installTime = pkgInfo.firstInstallTime
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