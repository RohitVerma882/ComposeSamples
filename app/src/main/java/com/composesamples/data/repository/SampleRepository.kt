package com.composesamples.data.repository

import com.composesamples.data.model.SampleInfo

import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    fun getSamples(): Flow<List<SampleInfo>>
}