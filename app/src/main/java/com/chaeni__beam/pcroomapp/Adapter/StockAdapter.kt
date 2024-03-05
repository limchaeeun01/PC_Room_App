package com.chaeni__beam.pcroomapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.MenuData
import com.chaeni__beam.pcroomapp.db.StockData


class StockAdapter(val list : MutableList<StockData>):RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    private var items : MutableList<StockData> = list
    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: onItemClickListener?=null


    fun setItemClickListener(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.stock_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: StockAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.stockName)
        val number : TextView = itemView.findViewById(R.id.stockNumber)

        fun bind(items : StockData){
            name.text = items.name
            number.text = items.number.toString()
        }

    }

    fun setItems(list: MutableList<StockData>) {
        items = list
        notifyDataSetChanged()
    }

}