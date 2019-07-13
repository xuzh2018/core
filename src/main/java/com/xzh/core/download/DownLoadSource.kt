package com.xzh.core.download

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  created by xzh on 2019/7/2
 */
@Parcelize
data class DownLoadSource(
    var currentProgress: Int?,
    var totalDuration: Long?
) : Parcelable