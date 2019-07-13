package com.xzh.core.net

import java.io.IOException

/**
 *  created by xzh on 2019/6/27
 */
suspend fun <T : Any> safeApiCall(
    call: suspend () -> ApiResult<T>,
    errorMessage: String
): ApiResult<T> {
    return try {
        call()
    } catch (e: Exception) {
        ApiResult.Error(IOException(errorMessage, e))
    }
}