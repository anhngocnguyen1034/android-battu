package com.anhnn.battu.di

import com.anhnn.battu.data.repository.ChartRepositoryImpl
import com.anhnn.battu.domain.repository.ChartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindChartRepository(impl: ChartRepositoryImpl): ChartRepository
}
