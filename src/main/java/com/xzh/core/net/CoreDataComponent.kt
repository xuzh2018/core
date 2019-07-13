package com.xzh.core.net

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Component
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 *  created by xzh on 2019/6/26
 */
@Component(modules = [CoreDataModule::class])
@Singleton
interface CoreDataComponent {
    @Component.Builder
    interface Builder {
        fun build(): CoreDataComponent
    }

    fun provideOkhttpClient():OkHttpClient
    fun provideGson():Gson
    fun provideGsonConverterFactory(): GsonConverterFactory
    fun provideCallAdapterFactory(): CoroutineCallAdapterFactory
}