package com.xzh.core.recycle

import android.content.Context
import android.view.Gravity

/**
 * Created by xzh on 2019/9/18.
 */
abstract class KotRecycleHeadView(context: Context) : KotPullRefreshView(context) {

    init {
        mMainView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0)
        gravity = Gravity.BOTTOM
    }

    val refreshHeight: Int
        get() = onCreateRefreshLimitHeight()

    //触发刷新的最小高度
    protected abstract fun onCreateRefreshLimitHeight(): Int

    //正在下拉
    abstract fun onPullingDown()

    //已经达到可以刷新的状态
    abstract fun onReleaseState()

    //执行刷新
    abstract fun onRefreshing()

    //刷新成功
    abstract fun onResultSuccess()

    //刷新失败
    abstract fun onResultFail()


}