package com.chaeni__beam.pcroomapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.StockData


class IngAdapter(list: MutableList<StockData>):RecyclerView.Adapter<IngAdapter.ViewHolder>() {

    private var items : MutableList<StockData> = list

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: onItemClickListener? = null


    fun setItemClickListener(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: IngAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
        holder.bind(items!![position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.ingName)

        fun bind(items : StockData){
            name.text = items.name
        }

    }

    fun setItems(list: MutableList<StockData>) {
        items = list
        notifyDataSetChanged()
    }

}