package com.xzh.core.base

import android.app.Activity
import android.app.Service
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog

/**
 *  created by xzh on 2019/6/26
 */
interface BaseComponent<T> {
    fun inject(target: T)
}

/**
 * dagger base component use in activity
 */
interface BaseActivityComponent<T : Activity> : BaseComponent<T>

/**
 * dagger base component use in service
 */
interface BaseServiceComponent<T: Service> :BaseComponent<T>

/**
 * dagger base component use in activity
 */
interface BaseFragmentComponent<T : Fragment> : BaseComponent<T>

/**
 * dagger base component use in dialog
 */
interface BaseDialogComponent<T : Any> : BaseComponent<T>