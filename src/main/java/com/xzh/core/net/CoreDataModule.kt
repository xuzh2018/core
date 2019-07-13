package com.xzh.core.net

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.xzh.core.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 *  created by xzh on 2019/6/26
 */
@Module
class CoreDataModule {

    @Provides
    fun provideOkhttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    fun provideLoggerInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    fun provideCallAdapterFactory(): CoroutineCallAdapterFactory = CoroutineCallAdapterFactory()

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)
}