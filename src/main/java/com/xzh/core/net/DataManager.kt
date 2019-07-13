package com.xzh.core.net

import com.orhanobut.logger.Logger
import com.xzh.core.base.BaseItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.atomic.AtomicInteger

data class InFlightRequestData(val key: String, val page: Int)
/**
 *  created by xzh on 2019/6/27
 */
abstract class DataManager<T> constructor(
    private val dispatcherProvider: CoroutinesDiapatcherProvider
) : DataLoadingObserver {


    override fun addDataLoadingObserver(callbacks: DataLoadingObserver.DataLoadingCallbacks) {

    }

    private val parentJob = SupervisorJob()

    protected val scope = CoroutineScope(dispatcherProvider.computation + parentJob)

    protected val parentJobs = mutableMapOf<InFlightRequestData, Job>()

    private val loadingCount = AtomicInteger(0)

    private var loadingCallBacks = mutableListOf<DataLoadingObserver.DataLoadingCallbacks>()

    private var onDataLoadedCallBack: DataLoadingObserver.OnDataLoadedCallback<T>? = null

    /**
     * 数据加载结束 监听
     */
    fun setOnDataLoadedCallBack(dataLoadedCallback: DataLoadingObserver.OnDataLoadedCallback<T>) {
        this.onDataLoadedCallBack = dataLoadedCallback
    }

    /**
     * 数据加载结束 数据回调
     */
    private fun onDataLoaded(data: T) {
        Logger.i("数据回调$data")
        onDataLoadedCallBack?.onDataLoaded(data)
    }


    /**
     * 数据加载
     */
    protected fun loadSource(item: BaseItem) {
        Logger.i("数据加载。。。。$item")
        startLoadingSource(item)
        loadStarted()
    }

    abstract fun startLoadingSource(item: BaseItem)

    /**
     * 数据加载结束->成功
     */
    protected fun sourceLoaded(
        data: T?,
        request: InFlightRequestData
    ) {
        Logger.i("数据加载成功$request")
        if (data != null) {
            onDataLoaded(data)
        }

        loadFinished()
        parentJobs.remove(request)
    }

    /**
     * 数据加载结束->失败
     */
    protected fun loadFailed(request: InFlightRequestData) {
        Logger.i("数据加载失败$request")
        loadFinished()
        parentJobs.remove(request)
    }

    /**
     * 取消指定数据加载工作
     */
    fun cancleJob(request: InFlightRequestData) {
        parentJobs[request]?.cancel()
        Logger.i("取消Job后${parentJobs[request]?.isActive} ${parentJobs[request]?.isCancelled} size${parentJobs.size}")
        parentJobs.remove(request)
        Logger.i("取消Job后${parentJobs[request]?.isActive} ${parentJobs[request]?.isCancelled} size${parentJobs.size}")

    }

    /**
     * 取消所有数据加载工作
     */
    fun cancleLoading() {
        parentJobs.values.forEach { it.cancel() }
        parentJobs.clear()
    }

    private fun loadStarted() {
        /**
         * 以原子方式 +1 之前的原始值
         */
        if (0 == loadingCount.getAndIncrement()) {
            dispatchLoadingStartedCallBack()
        }
    }

    private fun loadFinished() {
        /**
         * 以原子方式 -1 之后的新值
         */
        if (0 == loadingCount.decrementAndGet()) {
            dispatchLoadingFinishedCallBack()
        }
    }

    private fun dispatchLoadingFinishedCallBack() {
        loadingCallBacks.forEach { it.startLoading() }
    }

    private fun dispatchLoadingStartedCallBack() {
        loadingCallBacks.forEach { it.finishLoading() }
    }
}