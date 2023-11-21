package com.chaeni__beam.pcroomapp.Adapter

import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.MainData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainAdapter(val userCode : Int, val items : MutableList<MainData>):RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    lateinit var database : SQLiteDatabase

    interface onItemClickListener {
        fun onItemClick(v : View, position: Int){
            val introArea = v.findViewById<CardView>(R.id.introArea)

            if(introArea.visibility == 8){
                introArea.visibility = View.VISIBLE
            }else{
                introArea.visibility = View.GONE
            }
        }
    }

    private var itemClickListener: onItemClickListener?=null

    fun setItemClickListener(itemClickListener: onItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.main_recyclerview, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(it, position)
        }

        holder.bind(items[position])

        holder.orderBtn.setOnClickListener{
            val dbHelper = DataBaseHelper(holder.itemView?.context)
            database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val currentDate = current.format(formatter)

            val price = items[position].price * holder.number.text.toString().toInt()

            if(database != null){
                val sql = "INSERT INTO OrderForm (user_code, menu_code, order_number," +
                        "order_price, order_timeStamp, order_status)" +
                        "VALUES (?, ?, ?, ?, ?, ?)"

                val params = arrayOf(userCode, items[position].code, holder.number.text.toString().toInt(),
                price, currentDate, "주문완료")
                database.execSQL(sql, params)
            }

            Toast.makeText(holder.itemView?.context, "주문 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.menuImage)
        val name : TextView = itemView.findViewById(R.id.menuName)
        val price : TextView = itemView.findViewById(R.id.menuPrice)
        val intro : TextView = itemView.findViewById(R.id.introText)
        val soldOut = itemView.findViewById<CardView>(R.id.soldOutArea)

        val subBtn = itemView.findViewById<Button>(R.id.numberpicker_subBtn)
        val addBtn = itemView.findViewById<Button>(R.id.numberpicker_addBtn)
        val number = itemView.findViewById<TextView>(R.id.orderNumber)

        val orderBtn = itemView.findViewById<Button>(R.id.orderBtn)

        fun bind(items : MainData){
            if(items.image!=null){
                val bitmap = BitmapFactory.decodeByteArray(items.image, 0, items.image!!.size)
                image.setImageBitmap(bitmap)
            }else{
                Glide.with(image.context)
                    .load(R.drawable.menudefaultimage)
                    .thumbnail(0.1f)
                    .error(R.drawable.menudefaultimage)
                    .placeholder(R.drawable.menudefaultimage)
                    .into(image)
            }

            name.text = items.name
            price.text = items.price.toString() + "원"
            intro.text = items.intro

            if(items.soldOut == "true"){
                soldOut.visibility = View.VISIBLE
            }

            subBtn.setOnClickListener{
                if(number.text.toString().toInt() != 1 ){
                    number.text = (number.text.toString().toInt() - 1).toString()
                }
            }

            addBtn.setOnClickListener{
                if(number.text.toString().toInt() != 10){
                    number.text = (number.text.toString().toInt() + 1).toString()
                }
            }


        }

    }


}