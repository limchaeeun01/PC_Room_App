package com.chaeni__beam.pcroomapp.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaeni__beam.pcroomapp.Activity.MenuStockFragment
import com.chaeni__beam.pcroomapp.Dialog.ModifyMenuActivity
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.MenuData
import com.chaeni__beam.pcroomapp.db.StockData

class MenuAdapter(val list : MutableList<MenuData>):RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private var items : MutableList<MenuData> = list
    interface OnItemClickListener {
        fun onItemClick(position: Int){

        }
    }

    private var itemClickListener: OnItemClickListener?= null

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.menu_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: MenuAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            Log.d("tttt", "Item clicked at position: $position")
            itemClickListener?.onItemClick(position)
        }
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val category : TextView = itemView.findViewById(R.id.menuCategory)
        val name : TextView = itemView.findViewById(R.id.menuName)
        val number : TextView = itemView.findViewById(R.id.menuNumber)
        val sale : TextView = itemView.findViewById(R.id.menuSale)

        fun bind(items: MenuData){
            category.text = items.category
            name.text = items.name
            number.text = items.number.toString()
            sale.text = items.sale.toString()
        }
    }

    fun setItems(list: MutableList<MenuData>) {
        items = list
        notifyDataSetChanged()
    }


}