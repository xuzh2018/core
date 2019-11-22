package com.xzh.core.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xzh.core.base.KotAdapterItem

/**
 * Created by xzh on 2019/9/16.
 */
abstract class KotAdapter(
    host: Context?,
    sourceUiModelDiff: DiffUtil.ItemCallback<KotAdapterItem>
) : ListAdapter<KotAdapterItem, RecyclerView.ViewHolder>(sourceUiModelDiff) {
    private val inflater = LayoutInflater.from(host)




//    override fun getItemViewType(position: Int): Int {
//        if (position < currentList.size && currentList.isNotEmpty()) {
//            return getKotItemType(position)
//        }
//        return super.getItemViewType(position)
//    }
//
//    abstract fun getKotItemType(position: Int): Int

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        try {
//            return createKotViewHolder(inflater, viewType, parent)
//        } catch (e: IllegalAccessException) {
//            throw IllegalAccessException("unSupported view type")
//        }
//    }
//
//    abstract fun createKotViewHolder(
//        inflater: LayoutInflater,
//        type: Int,
//        parent: ViewGroup
//    ): RecyclerView.ViewHolder
//
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        try {
//            bindKotViewHolder(holder, position)
//        } catch (e: IllegalAccessException) {
//            throw IllegalAccessException("holder bind error")
//        }
//    }
//
//    abstract fun bindKotViewHolder(holder: RecyclerView.ViewHolder, position: Int)
}