package com.composesamples

import android.content.Context

import com.composesamples.data.repository.AppRepository
import com.composesamples.data.repository.SampleRepository
import com.composesamples.data.repository.impl.AppRepositoryImpl
import com.composesamples.data.repository.impl.SampleRepositoryImpl

interface AppContainer {
    val sampleRepository: SampleRepository
    val appRepository: AppRepository
}

class AppContainerImpl(private val context: Context) : AppContainer {
    override val sampleRepository: SampleRepository get() = SampleRepositoryImpl()
    override val appRepository: AppRepository get() = AppRepositoryImpl(context)
}