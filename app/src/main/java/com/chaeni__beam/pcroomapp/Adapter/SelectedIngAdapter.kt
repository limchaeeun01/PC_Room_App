package com.chaeni__beam.pcroomapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.IngData
import com.chaeni__beam.pcroomapp.db.StockData


class SelectedIngAdapter(list: MutableList<IngData>):RecyclerView.Adapter<SelectedIngAdapter.ViewHolder>() {

    private var items : MutableList<IngData> = list

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: onItemClickListener?=null


    fun setItemClickListener(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedIngAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.ingredients_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: SelectedIngAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
        holder.bind(items!![position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.ingName)
        val number : TextView = itemView.findViewById(R.id.ingNum)

        fun bind(items : IngData){
            name.text = items.stock_name
            number.text = items.stock_need.toString()
        }

    }

    fun setItems(list: MutableList<IngData>) {
        items = list
        notifyDataSetChanged()
    }

}