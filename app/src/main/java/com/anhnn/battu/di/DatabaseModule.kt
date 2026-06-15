package com.anhnn.battu.di

import android.content.Context
import com.anhnn.battu.data.local.BatTuDatabase
import com.anhnn.battu.data.local.SuKienDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BatTuDatabase =
        BatTuDatabase.getInstance(context)

    @Provides
    fun provideSuKienDao(db: BatTuDatabase): SuKienDao = db.suKienDao()
}
