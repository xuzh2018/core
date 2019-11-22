package com.xzh.core.download

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by xzh on 2019/7/16.
 */
@Parcelize
data class UpLoadBody(
    val upLoadType: Int,
    val request: Map<Int, Map<String,String>>
) : Parcelable {
    companion object {
        const val TYPE_NORMAL = 0
        const val TYPE_PIC = 1
        const val TYPE_VIDEO = 2
    }

}