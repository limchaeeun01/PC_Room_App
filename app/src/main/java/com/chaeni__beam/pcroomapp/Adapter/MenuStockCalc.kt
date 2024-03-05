package com.chaeni__beam.pcroomapp.Adapter

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.StockData

interface MenuStockCalc {

    fun menuStockCalc(context: Context){
        val dbHelper = DataBaseHelper(context)
        var database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        var cursor1 = database.rawQuery("SELECT menu_code FROM Menu", null)

        for(i in 0..cursor1.count-1){
            var result = 10000
            cursor1.moveToNext()

            val cursur2 = database.rawQuery("SELECT stock_code, stock_need From Ingredient " +
                    "WHERE menu_code=${cursor1.getInt(0)}", null)

            if(cursur2.count == 0){ //재료가 없음
                database = dbHelper.writableDatabase

                val sql = "UPDATE Menu SET menu_number=? WHERE menu_code=?"
                val params = arrayOf(null, cursor1.getInt(0))

                database.execSQL(sql, params)
            }else{ //재료가 있음
                for(i in 0..cursur2.count-1){
                    cursur2.moveToNext()

                    val cursor3 = database.rawQuery("SELECT stock_number FROM Stock " +
                            "WHERE stock_code=${cursur2.getInt(0)}", null)

                    cursor3.moveToNext()

                    val temp = cursor3.getInt(0) / cursur2.getInt(1)

                    if(result > temp){
                        result = temp
                    }

                }

                database = dbHelper.writableDatabase

                val sql = "UPDATE Menu SET menu_number=? WHERE menu_code=?"
                val params = arrayOf(result, cursor1.getInt(0))

                database.execSQL(sql, params)
            }

        }

        val sql = "UPDATE Menu SET menu_soldOut='true' WHERE menu_number=0"
        database.execSQL(sql)


    }
}