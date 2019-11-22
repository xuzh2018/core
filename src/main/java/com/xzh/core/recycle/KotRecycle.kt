package com.xzh.core.recycle

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xzh.core.adapter.KotPull2RefreshAdapter

/**
 * Created by xzh on 2019/9/18.
 */
class KotRecycle
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    RecyclerView(
        context,
        attrs,
        defStyle
    ) {

    private var pullListener: KotRecyclePullListener? = null
    private var mHeadRefreshState = HEAD_STATE_NORMAL
    private var mMoreRefreshState = MORE_STATE_NORMAL
    private var isCanRefresh = false //刷新
    private var isCanLoadMore = false //加载
    internal var kotRecycleHeadView: KotRecycleHeadView? = null
    internal var kotRecycleFootView: KotRecycleFootView? = null


    private lateinit var kotPull2RefreshAdapter: KotPull2RefreshAdapter
    private val dataObserver: AdapterDataObserver = KotAdapterDataObserver()


    init {
        init2()
    }

    private fun init2() {
        initConfig()
//        setUseLoadMore(isCanLoadMore())
    }

    private fun initConfig() {
        if (mSysConfig != null) {
            kotRecycleHeadView = mSysConfig?.getHeadView()
//            kotRecycleFootView = mSysConfig?.getFootView()
        } else {
            kotRecycleHeadView = KotNormalHeadView(context)
//            kotRecycleFootView = KotNormalFootView(context)
        }
    }

    override fun setAdapter(adapter: Adapter<ViewHolder>?) {
        kotPull2RefreshAdapter = KotPull2RefreshAdapter(adapter!!, this)
        super.setAdapter(kotPull2RefreshAdapter)
//        adapter.registerAdapterDataObserver(dataObserver)
//        dataObserver.onChanged()
    }


    override fun getAdapter(): Adapter<ViewHolder> {
        return kotPull2RefreshAdapter
    }

    //上下拉刷新监听
    fun setPullListener(pullListener: KotRecyclePullListener) {
        this.pullListener = pullListener
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        if (layout is GridLayoutManager) {
            with(layout) {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (kotPull2RefreshAdapter.isRefreshHeader(position)
                            || kotPull2RefreshAdapter.isLoadMoreFooter(position)
                        ) spanCount else 1
                    }

                }
            }
        }
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE
            && isCanLoadMore()
            && !isLoadMore()
            && !isRefresh()
        ) {
                layoutManager.let {
                val lastCompletelyVisibleItemPosition: Int
                lastCompletelyVisibleItemPosition = when (it) {
                    is GridLayoutManager -> it
                        .findLastCompletelyVisibleItemPosition()
                    is StaggeredGridLayoutManager -> {
                        val into = IntArray(it.spanCount)
                        it.findLastCompletelyVisibleItemPositions(into)
                        findMax(into)
                    }
                    else -> (it as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()
                }
                if (it.childCount > 0 && lastCompletelyVisibleItemPosition == kotPull2RefreshAdapter.itemCount - 1) {
                    kotRecycleFootView?.onLoadingMore()
                    pullListener?.onLoadMore()
                    mMoreRefreshState = MORE_STATE_REFRESHING
                }
            }

        }
    }


    private var lastY = -1F

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> lastY = e.rawY
            MotionEvent.ACTION_MOVE -> if (isCanRefresh) {
                lastY = if (lastY == -1f) e.rawY else lastY
                val moveY = e.rawY - lastY
                lastY = e.rawY
                if (isCanRefresh() && kotRecycleHeadView?.getVisibleHeight() == 0 && moveY < 0) {
                    return super.onTouchEvent(e)
                }
                if (isOnTop() && !isRefresh()) {
                    onMove((moveY / DRAG_RATE).toInt())
                    return false
                }
            }
            MotionEvent.ACTION_UP -> checkRefresh()
        }

        return super.onTouchEvent(e)
    }

    /**
     * 触摸事件结束后检查是否需要刷新
     */
    private fun checkRefresh() {
        if (isCanRefresh()) {
            kotRecycleHeadView?.let {

                if (it.getVisibleHeight() <= 0) {
                    return
                }
                if (mHeadRefreshState == HEAD_STATE_NORMAL) {
                    it.smoothScrollTo(0)
                    mHeadRefreshState = HEAD_STATE_DONE
                } else if (mHeadRefreshState == HEAD_STATE_RELEASE_TO_REFRESH) {
                    setRefreshState(HEAD_STATE_REFRESHING)
                }
            }
        }
    }

    //设置要执行状态
    private fun setRefreshState(state: Int) {
        if (isExistRefreshView() && mHeadRefreshState != state) {
            when (state) {
                HEAD_STATE_REFRESHING//切换到刷新状态
                -> {
                    kotRecycleHeadView?.run {
                        onRefreshing()
                        smoothScrollTo(refreshHeight)
                    }
                    pullListener?.onRefresh()
                }
                HEAD_STATE_DONE//切换到刷新完成或者加载成功的状态
                -> if (mHeadRefreshState == HEAD_STATE_REFRESHING) {
                    kotRecycleHeadView?.run {
                        onResultSuccess()
                        postDelayed({ smoothScrollTo(0) }, 500)
                    }

                }
                HEAD_STATE_FAIL//切换到刷新失败或者加载失败的状态
                -> if (mHeadRefreshState == HEAD_STATE_REFRESHING) {
                    kotRecycleHeadView?.run {
                        onResultFail()
                        postDelayed({ smoothScrollTo(0) }, 500)
                    }
                }
            }
            mHeadRefreshState = state
        }

    }

    //判断手势状态
    private fun onMove(move: Int) {
        if (isCanRefresh() && !isRefresh()) {
            kotRecycleHeadView?.run {
                val newVisibleHeight = getVisibleHeight() + move
                if (newVisibleHeight >= refreshHeight && mHeadRefreshState != HEAD_STATE_RELEASE_TO_REFRESH) {
                    mHeadRefreshState = HEAD_STATE_RELEASE_TO_REFRESH
                    onReleaseState()
                }
                if (newVisibleHeight < refreshHeight && mHeadRefreshState != HEAD_STATE_NORMAL) {
                    mHeadRefreshState = HEAD_STATE_NORMAL
                    onPullingDown()
                }
                setVisibleHeight(getVisibleHeight() + move)

            }

        }
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }


    //设置头部刷新释放可用
    fun setUseRefresh(refresh: Boolean) {
        this.isCanRefresh = refresh
    }

    //设置底部刷新是否可用
    fun setUseLoadMore(loadMore: Boolean) {
        if (isExistLoadMoreView()) {
            val visible = kotRecycleFootView?.visibility
            if (visible != (if (loadMore) VISIBLE else GONE)) {
                kotRecycleFootView?.visibility = if (loadMore) VISIBLE else GONE
            }
        }
        isCanLoadMore = loadMore

    }


    //主动触发头部刷新
    fun onRefresh() {
        if (isCanRefresh() && !isLoadMore() && !isRefresh()) {
            mHeadRefreshState = HEAD_STATE_REFRESHING
            kotRecycleHeadView?.run {
                onRefreshing()
                smoothScrollTo(refreshHeight)
            }
            pullListener?.onRefresh()
        }
    }

    //主动触发底部刷新
    fun onLoadMore() {
        if (isCanLoadMore() && !isLoadMore() && !isRefresh()) {
            mMoreRefreshState = MORE_STATE_REFRESHING
            kotRecycleFootView?.onLoadingMore()
            pullListener?.onLoadMore()
        }
    }


    /**
     * 上下拉完成
     *
     * @param success 下拉或上滑是否成功
     */
    fun onComplete(success: Boolean) {
        if (isRefresh()) {
            if (success) {
                onPullComplete()
            } else {
                onPullFail()
            }

        }
        if (isLoadMore()) {
            onLoadMoreResult(success)

        }
    }

    //加载结果回调
    private fun onLoadMoreResult(success: Boolean) {
        if (isLoadMore()) {
            kotRecycleFootView?.run {
                if (success) {
                    onResultSuccess()
                } else {
                    onResultFail()
                }
                postDelayed({
                    mMoreRefreshState = MORE_STATE_NORMAL
                    onNormalState()
                }, 500)
            }
        }
    }


    //下拉刷新成功
    private fun onPullComplete() {
        setRefreshState(HEAD_STATE_DONE)
    }

    //下拉刷新失败
    private fun onPullFail() {
        setRefreshState(HEAD_STATE_FAIL)
    }


    //是否正在刷新
    private fun isRefresh(): Boolean {
        return (mHeadRefreshState == HEAD_STATE_REFRESHING) && isExistRefreshView()
    }

    //是否正在加载更多
    private fun isLoadMore(): Boolean {
        return (mMoreRefreshState == MORE_STATE_REFRESHING) && isExistLoadMoreView()
    }

    /**
     * 判断列表是否滑到顶部
     */
    private fun isOnTop(): Boolean {
        return isExistRefreshView() && kotRecycleHeadView?.parent != null
    }

    private fun isCanRefresh(): Boolean {
        return isExistRefreshView() && isCanRefresh
    }

    private fun isCanLoadMore(): Boolean {
        return isCanLoadMore && isExistLoadMoreView()
    }


    internal fun isExistRefreshView(): Boolean {
        return kotRecycleHeadView != null
    }

    internal fun isExistLoadMoreView(): Boolean {
        return kotRecycleFootView != null
    }


    companion object {
        const val HEAD_STATE_NORMAL = 0
        const val HEAD_STATE_RELEASE_TO_REFRESH = 1
        const val HEAD_STATE_REFRESHING = 2
        const val HEAD_STATE_DONE = 3
        const val HEAD_STATE_FAIL = 4

        const val MORE_STATE_NORMAL = 0
        const val MORE_STATE_REFRESHING = 1
        var mSysConfig: PullSysConfig? = null

        /**
         * 摩擦力
         */
        private const val DRAG_RATE = 2

        fun setPullSysConfig(config: PullSysConfig) {
            mSysConfig = config
        }
    }

    class PullSysConfig(val builder: Builder) {
        fun getHeadView(): KotRecycleHeadView {
            return builder.mHeadView
        }

        fun getFootView(): KotRecycleFootView {
            return builder.mFootView
        }

        companion object
        class Builder {
            internal lateinit var mHeadView: KotRecycleHeadView
            internal lateinit var mFootView: KotRecycleFootView
            fun refreshView(headView: KotRecycleHeadView): Builder {
                mHeadView = headView
                return this@Builder
            }

            fun footView(footView: KotRecycleFootView): Builder {
                mFootView = footView
                return this@Builder
            }

            fun build(): PullSysConfig {
                return PullSysConfig(this@Builder)
            }
        }

    }

    inner class KotAdapterDataObserver : AdapterDataObserver() {
        override fun onChanged() {
            kotPull2RefreshAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            kotPull2RefreshAdapter.notifyItemRangeInserted(
                if (isExistRefreshView())
                    positionStart + 1
                else
                    positionStart, itemCount
            )
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            kotPull2RefreshAdapter.notifyItemRangeChanged(
                if (isExistRefreshView())
                    positionStart + 1 else positionStart, itemCount
            )
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            kotPull2RefreshAdapter.notifyItemRangeRemoved(
                if (isExistRefreshView())
                    positionStart + 1
                else
                    positionStart, itemCount
            )
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            kotPull2RefreshAdapter.notifyItemMoved(
                if (isExistRefreshView())
                    fromPosition + 1
                else
                    fromPosition, if (isExistRefreshView()) toPosition + 1 else toPosition
            )
        }
    }


}