package com.example.sapmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sapmobile.R
import com.example.sapmobile.helpers.LoadMore
import com.example.sapmobile.models.BusinessPartners
import kotlinx.android.synthetic.main.list_item.view.*

class BusinessPartnersRecyclerAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isFirstPage = true

    fun loadMoreBP(loadedItems: MutableList<Any>) {
        if (list[list.size - 1] is LoadMore) list.removeAt(list.size - 1)
        list.addAll(loadedItems)
        notifyDataSetChanged()
    }

    fun removeLastLoadMore() {
        if (list.isNotEmpty()) {
            if (list[list.size - 1] is LoadMore) list.removeAt(list.size - 1)
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is BusinessPartners -> 1
            is LoadMore -> 2
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> BPViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
            )
            2 -> LoaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.bottom_loader_recycler, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is BPViewHolder && item is BusinessPartners) {
            holder.itemView.tvListCode.text = item.CardCode
            holder.itemView.tvListName.text = item.CardName
        } else if (holder is LoaderViewHolder && item is LoadMore) {
            listener?.loadMore(position)
        }
    }


    inner class BPViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        init {
            itemView.setOnClickListener {
                val item = list[adapterPosition] as BusinessPartners
                listener?.onClick(item)
            }
        }
    }

    inner class LoaderViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
    }

    var listener: OnAdapterItemClickListener? = null

    interface OnAdapterItemClickListener {
        fun onClick(bp: BusinessPartners)
        fun loadMore(lastItemIndex: Int)
        fun onLongClick()
    }
}