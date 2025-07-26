package com.composesamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.composesamples.AppContainer
import com.composesamples.data.model.AppMetadata
import com.composesamples.data.repository.AppRepository
import com.composesamples.utils.Resource

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class InstalledAppsViewModel(private val appRepository: AppRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(InstalledAppsUiState())
    val uiState: StateFlow<InstalledAppsUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            appRepository.getInstalledApps().collect { apps ->
                when (apps) {
                    is Resource.Loading -> {
                        _uiState.update { currentState ->
                            currentState.copy(isLoading = true)
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                apps = apps.data
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
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
    val apps: List<AppMetadata> = emptyList()
)