package com.composesamples.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.composesamples.data.model.SampleModel
import com.composesamples.data.repository.SampleRepository
import com.composesamples.utils.Resource

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(sampleRepository: SampleRepository) : ViewModel() {
    val samples: StateFlow<Resource<List<SampleModel>>> = sampleRepository
        .getSamples()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Resource.Loading()
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