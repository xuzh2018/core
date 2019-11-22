package com.xzh.core.download

import com.xzh.core.annotations.FeatureScope
import com.xzh.core.base.BaseServiceComponent
import com.xzh.core.net.CoreDataComponent
import dagger.Component

/**
 *  created by xzh on 2019/6/26
 */
@Component(
    modules = [DownLoadModule::class],
    dependencies = [CoreDataComponent::class]
)
@FeatureScope
interface DownLoadComponent : BaseServiceComponent<DownLoadService> {
    @Component.Builder
    interface Builder {
        fun build(): DownLoadComponent
        fun coreDataComponent(coreDataComponent: CoreDataComponent): Builder
    }
}