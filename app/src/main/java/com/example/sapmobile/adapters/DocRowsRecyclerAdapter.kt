package com.example.sapmobile.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sapmobile.R
import com.example.sapmobile.models.DocumentLines
import kotlinx.android.synthetic.main.doc_rows.view.*
import java.math.BigDecimal

class DocRowsRecyclerAdapter(var isNewDoc: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var list: MutableList<Any> = arrayListOf<Any>()
        set(value) {
            field = value
            notifyDataSetChanged()
            Log.d("RESPONSE", value.toString())
        }

    fun addRow(item: Any) {
        list.add(item)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is DocumentLines -> 1
            else -> throw IllegalStateException()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> DocRowsViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.doc_rows, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is DocRowsViewHolder && item is DocumentLines) {
            holder.itemView.tvItemCode.text = item.ItemCode
            holder.itemView.tvItemName.text = item.ItemName
            holder.itemView.tvQuantity.setText(item.Quantity.toString())
            holder.itemView.tvPrice.setText(item.Price.toString())
        }
    }

    inner class DocRowsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            if (isNewDoc) {
                itemView.btnMinus.setOnClickListener {
                    val item = list[bindingAdapterPosition] as DocumentLines
                    if (item.Quantity!! > 1) {
                        val result: BigDecimal =
                            item.Quantity!!.toBigDecimal().subtract(BigDecimal(1))
                        (list[bindingAdapterPosition] as DocumentLines).Quantity = result.toDouble()
                        notifyDataSetChanged()
                        Log.d("RESPONSE", "Minus btn pressed")
                    }
                }

                itemView.btnPlus.setOnClickListener {
                    val item = list[bindingAdapterPosition] as DocumentLines
                    val result: BigDecimal = item.Quantity!!.toBigDecimal().add(BigDecimal(1))
                    (list[bindingAdapterPosition] as DocumentLines).Quantity = result.toDouble()
                    notifyDataSetChanged()
                    Log.d("RESPONSE", "Plus btn pressed")
                }

                itemView.setOnLongClickListener {
                    list.removeAt(bindingAdapterPosition)
                    notifyDataSetChanged()
                    return@setOnLongClickListener true
                }

                itemView.tvPrice.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(editable: Editable?) {
                        if (editable != null && editable.isNotEmpty())
                            (list[bindingAdapterPosition] as DocumentLines).Price =
                                editable.toString().toDouble()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }
                })

                itemView.tvQuantity.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(editable: Editable?) {
                        if (editable != null && editable.isNotEmpty())
                            (list[bindingAdapterPosition] as DocumentLines).Quantity =
                                editable.toString().toDouble()
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                })
            } else {
                itemView.btnMinus.visibility = View.GONE
                itemView.btnPlus.visibility = View.GONE
                itemView.tvQuantity.isEnabled = false
                itemView.tvPrice.isEnabled = false
            }
        }
    }

    val listener: OnAdapterItemClickListener? = null

    interface OnAdapterItemClickListener {
        fun onLongClick(item: DocumentLines)
    }
}