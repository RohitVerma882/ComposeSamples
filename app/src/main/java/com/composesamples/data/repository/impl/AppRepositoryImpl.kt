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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

import me.zhanghai.android.appiconloader.AppIconLoader

class AppRepositoryImpl(private val context: Context) : AppRepository {
    private val packageManager = context.packageManager

    private val appIconSize = TypedValueCompat.dpToPx(40f, context.resources.displayMetrics).toInt()
    private val appIconLoader = AppIconLoader(appIconSize, true, context)

    override fun getInstalledApps(filterType: AppFilterType): Flow<Resource<List<AppModel>>> =
        flow {
            emit(Resource.Loading())
            try {
                val apps = withContext(Dispatchers.IO) {
                    packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                        .filterNot { info -> info.packageName == context.packageName }
                        .map { info -> createAppModel(info) }
                        .sortedBy { app -> app.name }
                }

                val filteredApps = when (filterType) {
                    AppFilterType.ALL -> apps
                    AppFilterType.USER -> apps.filterNot { app -> app.isSystemApp }
                    AppFilterType.SYSTEM -> apps.filter { app -> app.isSystemApp }
                }

                emit(Resource.Success(filteredApps))
            } catch (e: Exception) {
                emit(Resource.Error("Failed to get installed apps: ${e.message}"))
            }
        }

    override fun getAppDetails(packageName: String): Flow<Resource<AppModel>> = flow {
        emit(Resource.Loading())
        try {
            val app = withContext(Dispatchers.IO) {
                val info =
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                createAppModel(info)
            }
            emit(Resource.Success(app))
        } catch (_: PackageManager.NameNotFoundException) {
            emit(Resource.Error("App with package name '$packageName' not found."))
        } catch (e: Exception) {
            emit(Resource.Error("Failed to get app details: ${e.message}"))
        }
    }

    private fun createAppModel(info: ApplicationInfo): AppModel {
        val isSystemApp = (info.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        return AppModel(
            name = info.loadLabel(packageManager).toString(),
            packageName = info.packageName,
            icon = appIconLoader.loadIcon(info),
            isSystemApp = isSystemApp,
        )
    }
}