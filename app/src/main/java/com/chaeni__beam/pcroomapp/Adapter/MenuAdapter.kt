package com.chaeni__beam.pcroomapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.MenuData

class MenuAdapter(val items : MutableList<MenuData>):RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    private var itemClickListener: onItemClickListener?= null

    fun setItemClickListener(itemClickListener: onItemClickListener) {
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
            number.text = items.number
            sale.text = items.sale
        }
    }

}