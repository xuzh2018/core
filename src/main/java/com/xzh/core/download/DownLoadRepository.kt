package com.xzh.core.download

import androidx.lifecycle.MutableLiveData
import com.orhanobut.logger.Logger
import com.xzh.core.net.ApiResult
import com.xzh.core.net.safeApiCall
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 *  created by xzh on 2019/6/27
 */
class DownLoadRepository constructor(
    private val downLoadApi: DownLoadApi,
    private val listener: DownLoadModule.PerSyncInterceptorListener,
    var _syncLiveData: MutableLiveData<DownLoadSource> = MutableLiveData()
) {
    init {
        with(listener) { setSyncCurrentSource(_syncLiveData) }

    }

    val _listener: DownLoadModule.PerSyncInterceptorListener
        get() = listener

    val localLength: Long
        get() = _localLength
    private var _localLength: Long = 0L
    suspend fun downLoad(start: String, url: String, path: String) = safeApiCall(
        call = { startDownLoad(start, url, path) },
        errorMessage = "download error"
    )

    suspend fun upLoad(url: String, multipartBody: MultipartBody) = safeApiCall(
        call = { startUpLoad(url, multipartBody) },
        errorMessage = "upload error"
    )

    private suspend fun startUpLoad(url: String, multipartBody: MultipartBody): ApiResult<DownLoadSource> {
        val response = downLoadApi.upLoadAsync(url, multipartBody).await()
        if (response.isSuccessful) {
            if (response.body() != null) {
                return ApiResult.Success(DownLoadSource(100, 0))
            }
        }
        return ApiResult.Error(IOException("upload error${response.message()}"))
    }


    /**
     * 下载大文件同时回调进度
     */
    private suspend fun startDownLoad(start: String, url: String, path: String): ApiResult<DownLoadSource> {
        val response = downLoadApi.downLoadAsync(start, url).await()

        Logger.i("请求结束")
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                write2File(body.byteStream(), path)
                return ApiResult.Success(DownLoadSource(100, body.contentLength()))
            }
        }
        Logger.i("error downLoad data ${response.code()} ${response.message()}")
        return ApiResult.Error(
            IOException("error downLoad data ${response.code()} ${response.message()}")
        )
    }

    private fun write2File(
        inputStream: InputStream?,
        path: String
    ) {
        Logger.i("写入文件")
        val out: FileOutputStream
        try {
            out = FileOutputStream(File(path))
            val b = ByteArray(1024 * 8)
            var len: Int
            while ((inputStream?.read(b).also { len = it!! }) != -1) {
                out.write(b, 0, len)
                _localLength += len

            }
            inputStream?.close()
            out.close()

        } catch (e: IOException) {
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: DownLoadRepository? = null

        fun instance(
            downLoadApi: DownLoadApi,
            listener: DownLoadModule.PerSyncInterceptorListener
        ): DownLoadRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DownLoadRepository(downLoadApi, listener).also { INSTANCE = it }
            }
        }
    }
}