package com.xzh.core.recycle

import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.xzh.core.R

/**
 * Created by xzh on 2019/9/19.
 */
class KotNormalFootView(context: Context) : KotRecycleFootView(context) {

    private lateinit var mIVImg: ImageView
    private lateinit var mTVLoad: TextView
    private lateinit var mAnim: ValueAnimator
    override fun onNormalState() {
        mTVLoad.text = "上滑加载更多"
        stopAnimation()
        mTVLoad.visibility = View.VISIBLE
        mIVImg.visibility = View.GONE
    }


    override fun onLoadingMore() {
        mTVLoad.text = "正在加载"
        mIVImg.visibility = View.VISIBLE
        startAnimation()
    }

    override fun onResultSuccess() {
        stopAnimation()
        mTVLoad.text = "加载成功"
        onResult()

    }

    override fun onResultFail() {
        stopAnimation()
        mTVLoad.text = "加载失败"
        onResult()
    }

    override fun onCreateView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.kot_recycle_foot_view, this@KotNormalFootView,false)
    }

    override fun initView() {
        mIVImg = getView(R.id.iv_load_more) as ImageView
        mTVLoad = getView(R.id.tv_load_more) as TextView
    }

    private fun onResult() {
        mTVLoad.visibility = View.GONE
        mIVImg.visibility = View.GONE
    }

    private fun startAnimation() {
        mAnim = ValueAnimator.ofFloat(mIVImg.rotation, mIVImg.rotation + 359)

        mAnim.run {
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            duration = 1000
            addUpdateListener { animation -> mIVImg.rotation = animation.animatedValue as Float }
            start()
        }
    }

    private fun stopAnimation() {
        mAnim.end()
    }

}