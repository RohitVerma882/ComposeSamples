package com.composesamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.composesamples.data.model.AppInfo
import com.composesamples.data.repository.AppRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class InstalledAppsViewModel(appRepository: AppRepository) : ViewModel() {
    private val _filterType = MutableStateFlow<FilterType>(FilterType.ALL_APPS)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    val uiState: StateFlow<InstalledAppsUiState> = combine(
        appRepository.getInstalledApps(),
        _filterType
    ) { allApps, currentFilter ->
        val filteredApps = when (currentFilter) {
            FilterType.ALL_APPS -> allApps
            FilterType.USER_APPS -> allApps.filterNot { it.isSystemApp }
            FilterType.SYSTEM_APPS -> allApps.filter { it.isSystemApp }
        }
        InstalledAppsUiState.Success(filteredApps)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InstalledAppsUiState.Loading
    )

    fun setFilter(filterType: FilterType) {
        _filterType.value = filterType
    }
}

class InstalledAppsViewModelFactory(
    private val appRepository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstalledAppsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstalledAppsViewModel(appRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class FilterType {
    ALL_APPS,
    USER_APPS,
    SYSTEM_APPS
}

sealed class InstalledAppsUiState {
    object Loading : InstalledAppsUiState()
    data class Success(val apps: List<AppInfo>) : InstalledAppsUiState()
}