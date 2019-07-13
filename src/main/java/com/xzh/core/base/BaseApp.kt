package com.xzh.core.base

import android.app.Activity
import android.app.Application
import android.content.Context
import com.xzh.core.net.CoreDataComponent
import com.xzh.core.net.DaggerCoreDataComponent

/**
 *  created by xzh on 2019/6/27
 */
class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()

    }

    private val coreDataComponent: CoreDataComponent by lazy { DaggerCoreDataComponent.create() }

    companion object {
        @JvmStatic
        fun coreDataComponent(context: Context): CoreDataComponent {
            return (context as BaseApp).coreDataComponent
        }
    }
}

fun Activity.coreDataComponent() = BaseApp.coreDataComponent(this)