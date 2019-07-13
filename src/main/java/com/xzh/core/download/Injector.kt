package com.xzh.core.download

import com.xzh.core.net.CoreDataComponent
import com.xzh.core.net.DaggerCoreDataComponent

/**
 *  created by xzh on 2019/6/27
 */
fun inject(service: DownLoadService) {
    DaggerDownLoadComponent
        .builder()
        .coreDataComponent(coreDataComponent)
        .build()
        .inject(service)
}

private val coreDataComponent: CoreDataComponent by lazy { DaggerCoreDataComponent.create() }

