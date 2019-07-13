package com.xzh.core.download

import androidx.lifecycle.MutableLiveData

/**
 *  created by xzh on 2019/7/8
 */

interface SyncInterceptorListener {

    fun start(long: Long)

    fun onProcess(process: Int)

    fun setSyncCurrentSource(syncSource: MutableLiveData<DownLoadSource>)

    fun getSyncCurrentSource(): MutableLiveData<DownLoadSource>
}