package com.xzh.core.recycle

import android.content.Context

/**
 * Created by xzh on 2019/9/18.
 */
abstract class KotRecycleFootView(context: Context) : KotPullRefreshView(context) {
    init {
        mMainView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    //默认状态
    abstract fun onNormalState()

    //正在加载
    abstract fun onLoadingMore()

    //刷新成功
    abstract fun onResultSuccess()

    //刷新失败
    abstract fun onResultFail()

}