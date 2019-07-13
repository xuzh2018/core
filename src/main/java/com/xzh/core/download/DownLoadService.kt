package com.xzh.core.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.ResultReceiver
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.orhanobut.logger.Logger
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 *  created by xzh on 2019/6/26
 */
class DownLoadService : LifecycleService() {

    companion object {
        const val DOWNLOAD_PAUSE = "download_pause"
        const val DOWNLOAD_RESTART = "download_restart"
        const val UPLOAD_START = "upload_restart"
    }

    private val DOWNLOAD_URL = "download_url"
    private val RESULT_RECEIVER = "result_receiver"
    private val BUNDLE_DOWNLOAD_SEND = "bundle_download_send"
    private val DOWNLOAD_SAVE_PATH = "download_save_path"
    private val DOWNLOAD_PAUSE = "download_pause"

    private val RESULTCODE_DOWNLOAD_SEND = 66

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
        mDownLoadDataManager.syncListener.getSyncCurrentSource().observe(this, Observer {
            mResultReceiver?.send(RESULTCODE_DOWNLOAD_SEND, Bundle().apply { putParcelable(BUNDLE_DOWNLOAD_SEND, it) })
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
        mResultReceiver = intent?.getParcelableExtra<ResultReceiver>(RESULT_RECEIVER)
        localPath = intent?.getStringExtra(DOWNLOAD_SAVE_PATH)
        mDownLoadDataManager.startDownLoad(localPath!!)
    }

    private val downloadReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent!!.action) {
                DOWNLOAD_PAUSE -> mDownLoadDataManager.stopDownLoad()
                DOWNLOAD_RESTART -> mDownLoadDataManager.startDownLoad(localPath!!)
                UPLOAD_START -> mDownLoadDataManager.startUpLoad(localPath!!,intent.getParcelableExtra<MultipartBody>())
            }
        }

    }

}