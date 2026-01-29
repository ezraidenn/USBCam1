package com.biostar.camtest.di

import android.content.Context
import com.biostar.camtest.data.preferences.UserPreferences
import com.biostar.camtest.data.repository.CameraRepository
import com.biostar.camtest.data.repository.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCameraRepository(
        @ApplicationContext context: Context
    ): CameraRepository {
        return CameraRepository(context)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(
        @ApplicationContext context: Context
    ): MediaRepository {
        return MediaRepository(context)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context
    ): UserPreferences {
        return UserPreferences(context)
    }
}
