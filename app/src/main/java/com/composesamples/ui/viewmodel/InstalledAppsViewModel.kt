package com.composesamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.composesamples.AppContainer
import com.composesamples.data.model.AppModel
import com.composesamples.data.repository.AppFilterType
import com.composesamples.data.repository.AppRepository
import com.composesamples.utils.Resource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class InstalledAppsViewModel(private val appRepository: AppRepository) : ViewModel() {
    private val _filterType = MutableStateFlow(AppFilterType.ALL)

    private val _uiState = MutableStateFlow(InstalledAppsUiState())
    val uiState: StateFlow<InstalledAppsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadApps() {
        viewModelScope.launch {
            combine(
                _filterType,
                _filterType.flatMapLatest { filterType ->
                    appRepository.getInstalledApps(filterType)
                }
            ) { filterType, apps ->
                InstalledAppsUiState(
                    isLoading = apps is Resource.Loading,
                    filterType = filterType,
                    apps = when (apps) {
                        is Resource.Success -> {
                            apps.data
                        }

                        else -> emptyList()
                    }
                )
            }.collect { uiState ->
                _uiState.value = uiState
            }
        }
    }

    fun setFilterType(filterType: AppFilterType) {
        _filterType.value = filterType
    }
}

class InstalledAppsViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstalledAppsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstalledAppsViewModel(
                appRepository = appContainer.appRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class InstalledAppsUiState(
    val isLoading: Boolean = true,
    val filterType: AppFilterType = AppFilterType.ALL,
    val apps: List<AppModel> = emptyList()
)