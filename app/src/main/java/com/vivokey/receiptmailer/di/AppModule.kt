package com.vivokey.receiptmailer.di

import android.content.Context
import android.content.SharedPreferences
import com.vivokey.receiptmailer.R
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
    fun providesSharedPreference(@ApplicationContext context: Context): SharedPreferences{
        return context.getSharedPreferences(context.getString(R.string.preference_recipient), Context.MODE_PRIVATE)
    }
}