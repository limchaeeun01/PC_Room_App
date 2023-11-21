package com.chaeni__beam.pcroomapp.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.MainData
import com.chaeni__beam.pcroomapp.db.OrderFormData
import com.chaeni__beam.pcroomapp.db.StockData
import java.util.concurrent.TimeoutException

class OrderFormUserAdapter(val items : MutableList<OrderFormData>):RecyclerView.Adapter<OrderFormUserAdapter.ViewHolder>() {

    lateinit var database : SQLiteDatabase

    interface onItemClickListener {
        fun onItemClick(v : View, position: Int){

        }
    }

    private var itemClickListener: onItemClickListener?=null

    fun setItemClickListener(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderFormUserAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.orderform_user_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: OrderFormUserAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(it, position)
        }

        holder.bind(items[position])

    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val seatNumber = itemView.findViewById<TextView>(R.id.seatNumberArea)
        val userName = itemView.findViewById<TextView>(R.id.userNameArea)
        val orderId = itemView.findViewById<TextView>(R.id.orderIdArea)
        val orderTimeStamp = itemView.findViewById<TextView>(R.id.orderTiemStampArea)
        val orderInfo = itemView.findViewById<TextView>(R.id.orderInfoArea)
        val orderPrice = itemView.findViewById<TextView>(R.id.orderPriceArea)

        val statusText = itemView.findViewById<TextView>(R.id.statusText)
        val statusImg = itemView.findViewById<ImageView>(R.id.statusImg)
        val statusArea = itemView.findViewById<LinearLayout>(R.id.statusArea)

        fun bind(items : OrderFormData){
            orderId.text = "주문번호 : " + items.orderId
            orderTimeStamp.text = "주문일시 : " + items.order_timeStamp
            orderInfo.text = items.menu_name+ " x " + items.order_number
            orderPrice.text= items.order_price.toString() + "원"

            if(items.order_status == "판매완료"){
                statusText.text = "수령완료"
                statusArea.setBackgroundColor(Color.parseColor("#DADADA"))
            }else if(items.order_status == "주문취소"){
                statusText.text = "주문취소"
                statusImg.setBackgroundColor(Color.parseColor("#938C95"))
                statusArea.setBackgroundColor(Color.parseColor("#DADADA"))
            }else{
                statusText.text = "주문완료"
            }
        }

    }

}