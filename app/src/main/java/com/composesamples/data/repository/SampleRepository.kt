package com.composesamples.data.repository

import com.composesamples.data.model.SampleInfo
import com.composesamples.utils.Resource

import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    fun getSamples(): Flow<Resource<List<SampleInfo>>>
}