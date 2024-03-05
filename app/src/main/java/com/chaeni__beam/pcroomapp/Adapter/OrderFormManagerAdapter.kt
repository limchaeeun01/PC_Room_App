package com.chaeni__beam.pcroomapp.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
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

class OrderFormManagerAdapter(val items : MutableList<OrderFormData>):RecyclerView.Adapter<OrderFormManagerAdapter.ViewHolder>(),
MenuStockCalc{

    lateinit var database : SQLiteDatabase

    interface onItemClickListener {
        fun onItemClick(v : View, position: Int){

        }
    }

    private var itemClickListener: onItemClickListener?=null

    fun setItemClickListener(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderFormManagerAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.orderform_manager_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: OrderFormManagerAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(it, position)
        }

        holder.bind(items[position])

        holder.soldBtn.setOnClickListener{
            if(items[position].order_status == "주문완료"){
                val dbHelper = DataBaseHelper(holder.itemView?.context)
                database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득

                if(database != null){
                    val sql = "UPDATE OrderForm SET order_status =? WHERE order_id =?"
                    val params = arrayOf("판매완료", items[position].orderId)
                    database.execSQL(sql, params)

                    val sql2 = "UPDATE Menu SET menu_sale = menu_sale+? WHERE menu_name =?"
                    val params2 = arrayOf(items[position].order_number, items[position].menu_name)
                    database.execSQL(sql2, params2)

                    val cursor = database.rawQuery("SELECT stock_code, stock_need FROM Ingredient " +
                            "WHERE menu_code=${items[position].menu_code}", null)

                    for(i in 0 .. cursor.count-1){
                        cursor.moveToNext()
                        val stock_need = cursor.getInt(1)
                        val sql = "UPDATE Stock SET stock_number=stock_number-${stock_need} WHERE stock_code =?"
                        val params = arrayOf(cursor.getInt(0))
                        database.execSQL(sql, params)
                    }

                }

                holder.soldBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A5A5A5"))
                holder.statusArea.setBackgroundColor(Color.parseColor("#DADADA"))
                items[position].order_status = "판매완료"

            }
        }

        holder.cancleBtn.setOnClickListener{
            if(items[position].order_status == "주문완료"){
                val dbHelper = DataBaseHelper(holder.itemView?.context)
                database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득

                if(database != null){
                    val sql = "UPDATE OrderForm SET order_status =? WHERE order_id =?"
                    val params = arrayOf("주문취소", items[position].orderId)
                    database.execSQL(sql, params)
                }

                holder.cancleBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A5A5A5"))
                holder.statusImg.setBackgroundColor(Color.parseColor("#938C95"))
                holder.statusArea.setBackgroundColor(Color.parseColor("#DADADA"))
                items[position].order_status = "주문취소"
            }
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val seatNumber = itemView.findViewById<TextView>(R.id.seatNumberArea)
        val userName = itemView.findViewById<TextView>(R.id.userNameArea)
        val orderId = itemView.findViewById<TextView>(R.id.orderIdArea)
        val orderTimeStamp = itemView.findViewById<TextView>(R.id.orderTiemStampArea)
        val orderInfo = itemView.findViewById<TextView>(R.id.orderInfoArea)
        val orderPrice = itemView.findViewById<TextView>(R.id.orderPriceArea)

        val soldBtn = itemView.findViewById<Button>(R.id.soldBtn)
        val cancleBtn = itemView.findViewById<Button>(R.id.cancleBtn)
        val statusImg = itemView.findViewById<ImageView>(R.id.statusImg)
        val statusArea = itemView.findViewById<LinearLayout>(R.id.statusArea)

        fun bind(items : OrderFormData){
            seatNumber.text = items.seatNumber.toString().padStart(3, '0')
            userName.text = items.userName
            orderId.text = "주문번호 : " + items.orderId
            orderTimeStamp.text = "주문일시 : " + items.order_timeStamp
            orderInfo.text = items.menu_name+ " x " + items.order_number
            orderPrice.text= items.order_price.toString() + "원"

            soldBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F1F1F1"))
            statusArea.setBackgroundColor(Color.parseColor("#FFFFFF"))

            if(items.order_status == "판매완료"){
                soldBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A5A5A5"))
                statusArea.setBackgroundColor(Color.parseColor("#DADADA"))
            }else if(items.order_status == "주문취소"){
                cancleBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#A5A5A5"))
                statusImg.setBackgroundColor(Color.parseColor("#938C95"))
                statusArea.setBackgroundColor(Color.parseColor("#DADADA"))
            }
        }

    }

}