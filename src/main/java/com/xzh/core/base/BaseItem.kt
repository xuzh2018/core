package com.xzh.core.base

open class BaseItem(val REQUEST_KEY: String, val PAGE: Int = 1)

data class DataStateModel(
    val isLoading: Boolean
)

abstract class KotAdapterItem(
    @Transient open val ID: Long)


