package com.xzh.core.download

import androidx.lifecycle.MutableLiveData
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.xzh.core.annotations.FeatureScope
import com.xzh.core.net.CoroutinesDiapatcherProvider
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *  created by xzh on 2019/6/26
 */
@Module
class DownLoadModule {


    @Provides
    @FeatureScope
    fun provideSyncListener() = PerSyncInterceptorListener()

    @Provides
    @FeatureScope
    fun provideSyncIntercepter(listener: PerSyncInterceptorListener) =
        SyncIntercept(listener)

    @Provides
    @FeatureScope
    fun provideDownLoadDataManager(downLoadRepository: DownLoadRepository) =
        DownLoadDataManager(CoroutinesDiapatcherProvider(), downLoadRepository)

    @Provides
    @FeatureScope
    fun provideDownLoadRepository(downLoadApi: DownLoadApi): DownLoadRepository =
        DownLoadRepository.instance(downLoadApi)

    @Provides
    @FeatureScope
    fun provideDownLoadApi(
        okHttpClient: Lazy<OkHttpClient>,
        converterFactory: GsonConverterFactory,
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory,
        intercept: SyncIntercept
    ): DownLoadApi {
        return Retrofit.Builder()
            .baseUrl(DownLoadApi.BASE_URL)
            .callFactory {
                okHttpClient
                    .get()
                    .newBuilder()
                    .addInterceptor(intercept)
                    .build()
                    .newCall(it)
            }
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(coroutineCallAdapterFactory)
            .build()
            .create(DownLoadApi::class.java)
    }


    class PerSyncInterceptorListener : SyncInterceptorListener {


        private lateinit var progressBeanLiveData: MutableLiveData<DownLoadSource>

        private var totalLength: Long = 0L

        override fun start(long: Long) {
            totalLength = long
        }

        override fun setSyncCurrentSource(syncSource: MutableLiveData<DownLoadSource>) {
            progressBeanLiveData = syncSource
        }

        override fun getSyncCurrentSource(): MutableLiveData<DownLoadSource> {
            return progressBeanLiveData
        }

        override fun onProcess(process: Int) {
            var source = progressBeanLiveData.value
            if (source == null) {
                source = DownLoadSource(process, totalLength)
            } else {
                source.totalDuration = totalLength
                source.currentProgress = process
            }
            progressBeanLiveData.postValue(source)
        }

    }
}