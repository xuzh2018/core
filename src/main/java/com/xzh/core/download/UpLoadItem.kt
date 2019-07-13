package com.xzh.core.download

import com.xzh.core.base.BaseItem
import okhttp3.MultipartBody

data class UpLoadItem(
    val upLoadUrl: String,
    val upLoadBody: MultipartBody
) : BaseItem(REQUEST_KEY) {
    companion object {
        const val REQUEST_KEY = "UPLOAD"
    }
}
