package com.xzh.core.recycle

import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.xzh.core.R
import me.jessyan.autosize.utils.ScreenUtils

/**
 * Created by xzh on 2019/9/19.
 */
class KotNormalHeadView(context: Context) : KotRecycleHeadView(context) {

    private lateinit var mIVImg: ImageView
    private lateinit var mTVRefresh: TextView

    override fun onCreateRefreshLimitHeight() = ScreenUtils.getScreenSize(context)[1] / 6

    override fun onPullingDown() {
        mTVRefresh.text = "下拉刷新"
        mIVImg.visibility = View.VISIBLE
        mIVImg.setImageResource(R.drawable.ic_refresh_arrow)
        rotationAnimator(0f)
        Log.i("xuzh0", "下拉刷新")
    }

    override fun onReleaseState() {
        mIVImg.visibility = View.VISIBLE
        mIVImg.setImageResource(R.drawable.ic_refresh_arrow)
        mTVRefresh.text = "释放立即刷新"
        rotationAnimator(180f)
        Log.i("xuzh0", "释放立即刷新")
    }

    override fun onRefreshing() {
        mTVRefresh.text = "正在刷新"
        mIVImg.visibility = View.VISIBLE
        mIVImg.setImageResource(R.drawable.ic_refreshing)
        mIVImg.startAnimation(AnimationUtils.loadAnimation(context, R.anim.kot_refresh))
        Log.i("xuzh0", "正在刷新")
    }

    override fun onResultSuccess() {
        mIVImg.clearAnimation()
        mTVRefresh.text = "刷新成功"
        mIVImg.visibility = View.INVISIBLE
        Log.i("xuzh0", "刷新成功")
    }

    override fun onResultFail() {
        mIVImg.clearAnimation()
        mTVRefresh.text = "刷新失败"
        mIVImg.visibility = View.INVISIBLE
        Log.i("xuzh0", "刷新失败")
    }


    override fun onCreateView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.kot_recycle_head_view, this, false)
    }

    override fun initView() {
        mIVImg = getView(R.id.iv_refresh) as ImageView
        mTVRefresh = getView(R.id.tv_refresh) as TextView
    }

    private fun rotationAnimator(rotation: Float) {
        val animator = ValueAnimator.ofFloat(mIVImg.rotation, rotation)
        animator.addUpdateListener { p0 -> mIVImg.rotation = p0?.animatedValue as Float }
        animator.setDuration(200).start()
    }
}