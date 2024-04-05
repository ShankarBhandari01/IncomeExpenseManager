package com.example.incomeexpensemanager.di

import android.content.Context
import com.example.incomeexpensemanager.data.local.datastore.UIModeDataStore
import com.example.incomeexpensemanager.data.local.datastore.UIModeImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePreferenceManager(@ApplicationContext context: Context): UIModeImpl {
        return UIModeDataStore(context)
    }

}