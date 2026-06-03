package com.anhnn.battu.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * App-level Hilt module.
 * Bind Repositories / DataSources here as the project grows
 * (e.g. NetworkModule, DatabaseModule, RepositoryModule).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
