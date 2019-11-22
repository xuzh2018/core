package com.xzh.core.recycle

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.IdRes

/**
 * Created by xzh on 2019/9/18.
 */
abstract class KotPullRefreshView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(
        context,
        attrs,
        defStyleAttr
    ) {


    protected lateinit var mMainView: View


    init {
        init2(context)
    }

    private fun init2(context: Context) {
        mMainView = onCreateView(context)
        initView()
        addView(mMainView)
    }


    protected abstract fun onCreateView(context: Context): View

    protected abstract fun initView()

    protected fun getView(@IdRes id: Int): View {
        return mMainView.findViewById<View>(id)
    }

    fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight)
        animator.duration = 300
        animator.addUpdateListener { setVisibleHeight(it.animatedValue as Int) }
        animator.start()
    }


    fun getVisibleHeight(): Int {
        return mMainView.layoutParams.height
    }

    fun setVisibleHeight(h: Int) {
        val lp = mMainView.layoutParams
        if (h < 0) {
            lp.height = 0
        } else {
            lp.height = h
        }
        mMainView.layoutParams = lp
    }
}