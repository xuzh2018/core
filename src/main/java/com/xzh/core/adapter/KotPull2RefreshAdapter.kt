package com.xzh.core.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xzh.core.recycle.KotRecycle

/**
 * Created by xzh on 2019/9/18.
 */
class KotPull2RefreshAdapter(
    private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    private val mRecycle: KotRecycle
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_REFRESH_HEADER = 10000//头部下拉刷新类型
        private const val TYPE_LOAD_MORE_FOOTER = 10001//底部加载更多类型
    }


    fun getAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder> {
        return adapter
    }


    internal fun isLoadMoreFooter(position: Int): Boolean {
        return mRecycle.isExistLoadMoreView() && position == itemCount - 1
    }

    internal fun isRefreshHeader(position: Int): Boolean {
        return mRecycle.isExistRefreshView() && position == 0
    }

    /**
     * 判断是否是PullToRefreshRecyclerView保留的itemViewType
     */
    private fun isReservedItemViewType(itemViewType: Int): Boolean {
        return itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_LOAD_MORE_FOOTER
    }

    override fun getItemCount(): Int {
        var count = 0

        if (mRecycle.isExistRefreshView()) {
            count++
        }

        if (mRecycle.isExistLoadMoreView()) {
            count++
        }

        count += adapter.itemCount
        return count
    }


    override fun getItemViewType(position: Int): Int {
        if (isRefreshHeader(position)) {
            return TYPE_REFRESH_HEADER
        }

        if (isLoadMoreFooter(position)) {
            return TYPE_LOAD_MORE_FOOTER
        }
        val adjPosition = position - 1
        val adapterCount: Int = adapter.itemCount
        if (adjPosition < adapterCount) {
            val type = adapter.getItemViewType(adjPosition)
            if (isReservedItemViewType(type)) {
                throw IllegalStateException("PullToRefreshRecyclerView require itemViewType in adapter should be less than 10000 ")
            }
            return type
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_REFRESH_HEADER) {

            return SimpleViewHolder(mRecycle.kotRecycleHeadView.apply {
                parent.removeView(this)
            } as View)
        }
        else if (viewType == TYPE_LOAD_MORE_FOOTER) {
            return SimpleViewHolder(mRecycle.kotRecycleFootView.apply {
                parent.removeView(this)
            } as View)
        }
        return adapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isRefreshHeader(position) || isLoadMoreFooter(position)) {
            return
        }
        val adjPosition = position - if (mRecycle.isExistRefreshView()) 1 else 0
        adapter.onBindViewHolder(holder, adjPosition)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (isRefreshHeader(position) || isLoadMoreFooter(position)) {
            return
        }
        val adjPosition = position - if (mRecycle.isExistRefreshView()) 1 else 0
        if (payloads.isEmpty()) {
            adapter.onBindViewHolder(holder, adjPosition)
        } else {
            adapter.onBindViewHolder(holder, adjPosition, payloads)
        }
    }


    override fun getItemId(position: Int): Long {
        if (isRefreshHeader(position) || isLoadMoreFooter(position)) {
            return -1
        }
        val adjPosition = position - if (mRecycle.isExistRefreshView()) 1 else 0
        return adapter.getItemId(adjPosition)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isLoadMoreFooter(position) || isRefreshHeader(position))
                        manager.spanCount
                    else
                        1
                }
            }
        }
        adapter.onAttachedToRecyclerView(recyclerView)
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        adapter.onDetachedFromRecyclerView(recyclerView)

    }


    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        val lp = holder.itemView.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams
            && (isRefreshHeader(holder.layoutPosition) || isLoadMoreFooter(holder.layoutPosition))
        ) {
            lp.isFullSpan = true
        }
        adapter.onViewAttachedToWindow(holder)

    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        adapter.onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return adapter.onFailedToRecycleView(holder)
    }


    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        adapter.unregisterAdapterDataObserver(observer)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        adapter.registerAdapterDataObserver(observer)
    }

    private inner class SimpleViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
}