package com.xzh.core.net

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

data class CoroutinesDiapatcherProvider(
    val main: CoroutineDispatcher,
    val io: CoroutineDispatcher,
    val computation: CoroutineDispatcher
) {
    @Inject
    constructor() : this(Main, IO, Default)
}
