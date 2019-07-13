package com.xzh.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 *  created by xzh on 2019/6/19
 */
abstract class BaseFragment :Fragment(){

    private var dataLoaded = false
    private var viewPrepared = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(initLayout(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPrepared = true
        initView()
        lazyLoadIfPrepared()
    }

    @LayoutRes
    abstract fun initLayout(): Int

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser){
            lazyLoadIfPrepared()
        }
    }

    private fun lazyLoadIfPrepared() {
        if (userVisibleHint && !dataLoaded && viewPrepared){
            lazyLoad()
        }
    }

    abstract fun lazyLoad()
    abstract fun initView()

}