package com.xzh.core.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.ResultReceiver
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.orhanobut.logger.Logger
import com.xzh.core.net.DataLoadingObserver
import com.xzh.core.net.InFlightRequestData
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

/**
 *  created by xzh on 2019/6/26
 */
class DownLoadService : LifecycleService() {

    companion object {
        const val DOWNLOAD_PAUSE = "download_pause"
        const val DOWNLOAD_RESTART = "download_restart"
        const val UPLOAD_START = "upload_start"
        const val RESULT_RECEIVER = "result_receiver"
        const val RESULTCODE_DOWNLOAD_SEND = 66
        const val BUNDLE_DOWNLOAD_SEND = "bundle_download_send"
        const val DOWNLOAD_SAVE_PATH = "download_save_path"
        const val UPLOAD_BODY = "upload_body"
    }

    private val DOWNLOAD_URL = "download_url"


    private val DOWNLOAD_PAUSE = "download_pause"


    private var mResultReceiver: ResultReceiver? = null
    private var localPath: String? = null


    @Inject
    lateinit var mDownLoadDataManager: DownLoadDataManager

    override fun onCreate() {
        super.onCreate()
        inject(this)
        Logger.i("服务创建............")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mDownLoadDataManager.setOnDataLoadedCallBack(object : DataLoadingObserver.OnDataLoadedCallback<DownLoadSource> {
            override fun onDataLoaded(data: DownLoadSource, item: InFlightRequestData) {
                if (item.key == DownLoadItem.REQUEST_KEY) {
                    mResultReceiver?.send(
                        RESULTCODE_DOWNLOAD_SEND,
                        Bundle().apply { putParcelable(BUNDLE_DOWNLOAD_SEND, data) })
                }
            }


        })

        val filter = IntentFilter()
        filter.addAction(DOWNLOAD_RESTART)
        filter.addAction(DOWNLOAD_PAUSE)
        filter.addAction(UPLOAD_START)
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(downloadReceiver, filter)
        handleAction(intent)
        return super.onStartCommand(intent, flags, startId)
    }


    private fun handleAction(intent: Intent?) {
        mResultReceiver = intent?.getParcelableExtra(RESULT_RECEIVER)
        localPath = intent?.getStringExtra(DOWNLOAD_SAVE_PATH)
        mDownLoadDataManager.startDownLoad(localPath!!)
    }

    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                DOWNLOAD_PAUSE -> mDownLoadDataManager.stopDownLoad()
                DOWNLOAD_RESTART -> mDownLoadDataManager.startDownLoad(localPath!!)
                UPLOAD_START -> {
                    mDownLoadDataManager
                        .startUpLoad(
                            localPath!!,
                            multipartBody = analyseUpLoadBody(intent.getParcelableExtra(UPLOAD_BODY))
                        )
                }
            }
        }

    }

    /**
     *分析上传数据
     */
    private fun analyseUpLoadBody(body: UpLoadBody): MultipartBody {
        val multipartBody = MultipartBody.Builder()
        body.request.forEach {
            when (it.key) {
                UpLoadBody.TYPE_NORMAL -> it.value.forEach { entry ->
                    multipartBody.addFormDataPart(
                        entry.key,
                        entry.value
                    )
                }
                UpLoadBody.TYPE_PIC -> it.value.forEach { entry ->
                    val imgFile = File(entry.value)
                    val imgBody = RequestBody.create(MediaType.parse("image/*"), imgFile)
                    multipartBody.addFormDataPart(
                        entry.key,
                        imgFile.name,
                        imgBody
                    )
                }
                UpLoadBody.TYPE_VIDEO -> it.value.forEach { entry ->
                    val videoFile = File(entry.value)
                    val videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile)
                    multipartBody.addFormDataPart(
                        entry.key,
                        videoFile.name,
                        videoBody
                    )
                }
            }
        }
        return multipartBody.build()

    }

}