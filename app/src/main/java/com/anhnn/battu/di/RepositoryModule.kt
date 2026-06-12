package com.anhnn.battu.di

import com.anhnn.battu.data.repository.ChartRepositoryImpl
import com.anhnn.battu.data.repository.FeedbackRepositoryImpl
import com.anhnn.battu.data.repository.SavedChartRepositoryImpl
import com.anhnn.battu.data.repository.SettingsRepositoryImpl
import com.anhnn.battu.domain.repository.ChartRepository
import com.anhnn.battu.domain.repository.FeedbackRepository
import com.anhnn.battu.domain.repository.SavedChartRepository
import com.anhnn.battu.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindChartRepository(impl: ChartRepositoryImpl): ChartRepository

    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    abstract fun bindFeedbackRepository(impl: FeedbackRepositoryImpl): FeedbackRepository

    @Binds
    abstract fun bindSavedChartRepository(impl: SavedChartRepositoryImpl): SavedChartRepository
}
