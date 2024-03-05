package com.chaeni__beam.pcroomapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.MenuData
import com.chaeni__beam.pcroomapp.db.StockData
import com.chaeni__beam.pcroomapp.db.UserData


class UserAdapter(val list : MutableList<UserData>):RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private var items : MutableList<UserData> = list
    interface onItemClickListener {
        fun onItemClick(v:View, position: Int){

        }
    }

    private var itemClickListener: onItemClickListener?=null


    fun setItemClickListener(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.user_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(it, position)
        }
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val id : TextView = itemView.findViewById(R.id.idArea)
        val name : TextView = itemView.findViewById(R.id.nameArea)
        val division : TextView = itemView.findViewById(R.id.divisionArea)
        val orderNum : TextView = itemView.findViewById(R.id.orderNumberArea)

        fun bind(items : UserData){
            id.text = items.user_id
            name.text = items.user_name
            division.text = items.user_division
            orderNum.text = items.user_order_number.toString()
        }

    }

    fun setItems(list: MutableList<UserData>) {
        items = list
        notifyDataSetChanged()
    }

}