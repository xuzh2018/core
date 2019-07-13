package com.xzh.core.utils

import android.content.Context
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator

/**
 *  created by xzh on 2019/6/24
 */
class AnimUT {
    private var fastOutSlowIn: Interpolator? = null
    fun getFastOutSlowInInterpolator(context: Context): Interpolator {
        if (fastOutSlowIn == null) {
            fastOutSlowIn = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in)
        }
        return fastOutSlowIn!!
    }
}