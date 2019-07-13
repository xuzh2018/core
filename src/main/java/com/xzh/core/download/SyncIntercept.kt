package com.xzh.core.download

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.*

/**
 *  created by xzh on 2019/7/8
 */
class SyncIntercept constructor(private val syncListener: SyncInterceptorListener) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return response.newBuilder().body(SyncResponseBody(response.body(), syncListener)).build()
    }

    private class SyncResponseBody(
        val body: ResponseBody?,
        val syncListener: SyncInterceptorListener
    ) : ResponseBody() {
        private var mBufferedSource: BufferedSource? = null
        override fun contentLength(): Long {
            return body!!.contentLength()
        }


        override fun source(): BufferedSource {
            if (mBufferedSource == null) {
                mBufferedSource = Okio.buffer(attach(body?.source()))
            }
            return mBufferedSource!!
        }

        private fun attach(source: BufferedSource?): Source {
            return SyncSource(source!!, body, syncListener)
        }

        override fun contentType(): MediaType? {
            return body!!.contentType()
        }

    }

    private class SyncSource(
        delegate: Source,
        body: ResponseBody?,
        val syncListener: SyncInterceptorListener
    ) : ForwardingSource(delegate) {
        private var totalBytesRead = 0L
        private val length: Long = body!!.contentLength()
        override fun read(sink: Buffer, byteCount: Long): Long {
            val read = super.read(sink, byteCount)
            totalBytesRead += if (read != -1L) read else 0
            val percent = totalBytesRead.toFloat() / length * 100
            syncListener.onProcess((if (read == -1L) 100F else percent).toInt())
            return read
        }
    }
}