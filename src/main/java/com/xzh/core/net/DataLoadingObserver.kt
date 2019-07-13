package com.xzh.core.net

/**
 *  created by xzh on 2019/6/27
 */
interface DataLoadingObserver {
    fun addDataLoadingObserver(callbacks: DataLoadingCallbacks)
    interface DataLoadingCallbacks {
        fun startLoading()
        fun finishLoading()
    }

    interface OnDataLoadedCallback<T> {
        fun onDataLoaded(data: T)
    }
}