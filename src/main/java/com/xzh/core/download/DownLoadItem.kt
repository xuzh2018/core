package com.xzh.core.download

import com.xzh.core.base.BaseItem

/**
 *  created by xzh on 2019/7/2
 */

data class DownLoadItem(
    val downLoadStart: String,
    val downLoadUrl: String,
    val downLoadLocalPath: String
) : BaseItem(REQUEST_KEY) {
    companion object {
        const val REQUEST_KEY = "DOWNLOAD"
    }
}