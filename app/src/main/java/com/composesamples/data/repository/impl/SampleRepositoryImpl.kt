package com.composesamples.data.repository.impl

import com.composesamples.R
import com.composesamples.data.model.SampleModel
import com.composesamples.data.repository.SampleRepository
import com.composesamples.ui.navigation.AppRoutes
import com.composesamples.utils.Resource

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleRepositoryImpl : SampleRepository {
    private val samples by lazy {
        listOf(
            SampleModel(R.string.sample_hello_world_name, AppRoutes.HelloWorldScreen),
            SampleModel(R.string.sample_installed_apps_name, AppRoutes.InstalledAppsScreen)
        )
    }

    override fun getSamples(): Flow<Resource<List<SampleModel>>> = flow {
        emit(Resource.Loading())
        delay(500)
        emit(Resource.Success(samples))
    }
}