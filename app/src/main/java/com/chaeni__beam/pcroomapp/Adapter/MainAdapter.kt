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

    interface OnItemClickListener {
        fun onItemClick(v : View, position: Int){

        }
    }

    private var itemClickListener: OnItemClickListener?=null

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
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
            if (items[position].soldOut == "false") {
                itemClickListener?.onItemClick(it, position)
                holder.introArea.visibility = if (holder.introArea.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }

        holder.bind(items[position])

        holder.orderBtn.setOnClickListener{
            val dbHelper = DataBaseHelper(holder.itemView?.context)
            database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val currentDate = current.format(formatter)

            val price = items[position].price * holder.number.text.toString().toInt()

            val cursor = database.rawQuery("SELECT menu_number, menu_name FROM Menu " +
                    "WHERE menu_code = '${items[position].code}'", null)
            cursor.moveToNext()
            if(cursor.getInt(0) >= holder.number.text.toString().toInt()){ //재고량>=주문량
                if(database != null){
                    val sql = "INSERT INTO OrderForm (user_code, menu_code, order_number," +
                            "order_price, order_timeStamp, order_status)" +
                            "VALUES (?, ?, ?, ?, ?, ?)"

                    val params = arrayOf(userCode, items[position].code, holder.number.text.toString().toInt(),
                        price, currentDate, "주문완료")
                    database.execSQL(sql, params)
                }
                Toast.makeText(holder.itemView?.context, "주문 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(holder.itemView?.context, "${cursor.getString(1)}의 재고량이 주문량보다 적어 " +
                        "주문이 자동 취소되었습니다. (${cursor.getString(1)}의 재고량: ${cursor.getInt(0)})",
                    Toast.LENGTH_SHORT).show()
            }
            cursor.close()

            holder.introArea.visibility = View.GONE

        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.menuImage)
        val name : TextView = itemView.findViewById(R.id.menuName)
        val price : TextView = itemView.findViewById(R.id.menuPrice)
        val intro : TextView = itemView.findViewById(R.id.introText)
        val soldOutArea = itemView.findViewById<CardView>(R.id.soldOutArea)

        val subBtn = itemView.findViewById<Button>(R.id.numberpicker_subBtn)
        val addBtn = itemView.findViewById<Button>(R.id.numberpicker_addBtn)
        val number = itemView.findViewById<TextView>(R.id.orderNumber)

        val orderBtn = itemView.findViewById<Button>(R.id.orderBtn)

        val introArea = itemView.findViewById<CardView>(R.id.introArea)

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

            introArea.visibility = View.GONE

            if(items.soldOut == "true"){
                soldOutArea.visibility = View.VISIBLE
            }else{
                soldOutArea.visibility = View.GONE
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