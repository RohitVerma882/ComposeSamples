package com.composesamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.composesamples.data.model.SampleInfo
import com.composesamples.data.repository.SampleRepository

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(sampleRepository: SampleRepository) : ViewModel() {
    val uiState: StateFlow<MainUiState> = sampleRepository
        .getSamples()
        .map(MainUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading
        )
}

class MainViewModelFactory(
    private val sampleRepository: SampleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(sampleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val samples: List<SampleInfo>) : MainUiState()
}