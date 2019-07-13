package com.xzh.core.download

import com.orhanobut.logger.Logger
import com.xzh.core.base.BaseItem
import com.xzh.core.net.ApiResult
import com.xzh.core.net.CoroutinesDiapatcherProvider
import com.xzh.core.net.DataManager
import com.xzh.core.net.InFlightRequestData
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 *  created by xzh on 2019/6/28
 */
class DownLoadDataManager @Inject constructor(
    private val dispatcherProvider: CoroutinesDiapatcherProvider,
    private val downLoadRepository: DownLoadRepository
) : DataManager<DownLoadSource>(dispatcherProvider) {

    fun startDownLoad(path: String) {
        Logger.i("开始下载............")
        loadSource(
            DownLoadItem(
                downLoadRepository.localLength.toString(),
                "http://sd.iqilu.com/public/download/shandian-2.3.6.apk",
                path
            )
        )
    }

    fun startUpLoad(path: String, multipartBody: MultipartBody) {
        Logger.i("开始下载............")
        loadSource(
            UpLoadItem(
                "http://sd.iqilu.com/paike/publish",
                multipartBody
            )
        )
    }


    val syncListener: SyncInterceptorListener
        get() = downLoadRepository._listener

    fun stopDownLoad() {
        Logger.i("暂停下载............")
        cancleJob(InFlightRequestData("DOWNLOAD", 0))
//        cancleLoading()
    }

    override fun startLoadingSource(item: BaseItem) {
        val data = InFlightRequestData(item.REQUEST_KEY, 0)
        when (item.REQUEST_KEY) {
            DownLoadItem.REQUEST_KEY -> {
                parentJobs[data] = downLoadSource(data, item as DownLoadItem)
                Logger.i("请求选择download$item")
            }
            UpLoadItem.REQUEST_KEY -> {
                parentJobs[data] = upLoadSource(data, item as UpLoadItem)
                Logger.i("请求选择upload$item")
            }
        }
    }

    private fun downLoadSource(data: InFlightRequestData, item: DownLoadItem) = scope.launch {
        when (val result = downLoadRepository.downLoad(item.downLoadStart, item.downLoadUrl, item.downLoadLocalPath)) {
            is ApiResult.Success -> sourceLoaded(result.data, data)
            is ApiResult.Error -> loadFailed(data)
        }
    }

    private fun upLoadSource(data: InFlightRequestData, item: UpLoadItem) = scope.launch {
        when (val result = downLoadRepository.upLoad(item.upLoadUrl, item.upLoadBody)) {
            is ApiResult.Success -> sourceLoaded(result.data, data)
            is ApiResult.Error -> loadFailed(data)
        }
    }
}