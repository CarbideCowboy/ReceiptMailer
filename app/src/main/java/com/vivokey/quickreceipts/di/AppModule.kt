package com.vivokey.quickreceipts.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import com.vivokey.quickreceipts.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesSharedPreference(@ApplicationContext context: Context): SharedPreferences{
        return context.getSharedPreferences(context.getString(R.string.preference_recipient), Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun providesOutputDirectory(@ApplicationContext context: Context): File {
        val dir = Environment.getExternalStorageDirectory().absolutePath.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return dir
    }

    @Singleton
    @Provides
    fun providesCameraExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }
}