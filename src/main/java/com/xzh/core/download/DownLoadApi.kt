package com.xzh.core.download

import com.xzh.core.net.ApiResult
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 *  created by xzh on 2019/6/27
 */
interface DownLoadApi {

    /**
     * $RANGE 提供断点续传功能
     */
    @GET
    @Streaming
    fun downLoadAsync(@Header("RANGE") start: String, @Url url: String): Deferred<Response<ResponseBody>>

    /**
     * 提供大文件、多文件上传
     */
    @POST
    @Streaming
    fun upLoadAsync(@Url url: String, @Body multipartBody: MultipartBody): Deferred<Response<ResponseBody>>

    companion object {
        const val BASE_URL = "http://sd.iqilu.com/"
    }
}