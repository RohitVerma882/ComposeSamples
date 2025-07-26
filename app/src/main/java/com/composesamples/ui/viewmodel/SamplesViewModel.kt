package com.composesamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.composesamples.AppContainer
import com.composesamples.data.model.SampleInfo
import com.composesamples.data.repository.SampleRepository
import com.composesamples.utils.Resource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SamplesViewModel(private val sampleRepository: SampleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SamplesUiState())
    val uiState: StateFlow<SamplesUiState> = _uiState.asStateFlow()

    init {
        loadSamples()
    }

    private fun loadSamples() {
        viewModelScope.launch {
            sampleRepository.getSamples().collect { samples ->
                when (samples) {
                    is Resource.Loading -> {
                        _uiState.update { currentState ->
                            currentState.copy(isLoading = true)
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                samples = samples.data
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

class SamplesViewModelFactory(private val appContainer: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SamplesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SamplesViewModel(
                sampleRepository = appContainer.sampleRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class SamplesUiState(
    val isLoading: Boolean = true,
    val samples: List<SampleInfo> = emptyList()
)