package com.example.sapmobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sapmobile.R
import com.example.sapmobile.models.ItemWarehouseInfo
import kotlinx.android.synthetic.main.item_onhand_whs_row.view.*

class ItemsWhsInfoAdapter : RecyclerView.Adapter<ItemsWhsInfoAdapter.ItemsWhsInfoViewHolder>() {

    var list: MutableList<ItemWarehouseInfo> = arrayListOf<ItemWarehouseInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ItemsWhsInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsWhsInfoViewHolder {
        return ItemsWhsInfoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_onhand_whs_row, parent, false)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemsWhsInfoViewHolder, position: Int) {
        val item = list[position]
        holder.itemView.tvWhsCode.text = item.WarehouseCode
        holder.itemView.tvWhsQty.text = item.InStock.toString()
    }
}