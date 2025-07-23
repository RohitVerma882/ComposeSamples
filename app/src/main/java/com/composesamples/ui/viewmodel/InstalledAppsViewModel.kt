package com.composesamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.composesamples.data.model.AppModel
import com.composesamples.data.repository.AppFilterType
import com.composesamples.data.repository.AppRepository
import com.composesamples.utils.Resource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class InstalledAppsViewModel(appRepository: AppRepository) : ViewModel() {
    private val _filterType = MutableStateFlow<AppFilterType>(AppFilterType.ALL)
    val filterType: StateFlow<AppFilterType> = _filterType.asStateFlow()

    val apps: StateFlow<Resource<List<AppModel>>> = _filterType
        .flatMapLatest { filterType -> appRepository.getInstalledApps(filterType) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Resource.Loading()
        )

    fun setFilterType(filterType: AppFilterType) {
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