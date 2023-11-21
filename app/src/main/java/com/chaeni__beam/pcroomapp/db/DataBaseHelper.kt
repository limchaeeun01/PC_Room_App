package com.chaeni__beam.pcroomapp.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DataBaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, 1) {
    private val mDataBase: SQLiteDatabase? = null
    private val mContext: Context


    init {
        DB_PATH = "/data/data/" + context?.packageName + "/databases/"
        mContext = context!!
        dataBaseCheck()
    }

    private fun dataBaseCheck() {
        val dbFile = File(DB_PATH + DB_NAME)
        if (!dbFile.exists()) {
            dbCopy()
            Log.d(TAG, "Database is copied.")
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }


    @Synchronized
    override fun close() {
        mDataBase?.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE Stock (" +
                "stock_code INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "stock_name TEXT NOT NULL, " +
                "stock_number INTEGER NOT NULL)")

        db.execSQL("CREATE TABLE Menu" +
                "(menu_code INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "menu_name TEXT NOT NULL, " +
                "menu_category TEXT NOT NULL, " +
                "menu_price INTEGER NOT NULL, " +
                "menu_number INTEGER, " +
                "menu_intro TEXT, " +
                "menu_show TEXT NOT NULL, " +
                "menu_image	BLOB, " +
                "menu_sale INTEGER, " +
                "menu_soldOut TEXT NOT NULL)")

        db.execSQL("CREATE TABLE Ingredient" +
                "(menu_code INTEGER NOT NULL, " +
                "stock_code INTEGER NOT NULL, " +
                "stock_need INTEGER NOT NULL, " +
                "FOREIGN KEY (menu_code) REFERENCES Menu(menu_code), " +
                "FOREIGN KEY (stock_code) REFERENCES Stock(stock_code))")

        db.execSQL("CREATE TABLE User" +
                "(user_code INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT NOT NULL, " +
                "user_password TEXT NOT NULL, " +
                "user_name TEXT NOT NULL, " +
                "user_seatNumber INTEGER, " +
                "user_signUp_timeStamp TEXT)")

        db.execSQL("CREATE TABLE OrderForm" +
            "(order_id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "user_code INTEGER NOT NULL, " +
            "menu_code INTEGER NOT NULL, " +
            "order_number INTEGER NOT NULL, " +
                "order_price INTEGER NOT NULL, " +
            "order_timeStamp INTEGER NOT NULL, " +
            "order_status TEXT NOT NULL, " +
                "FOREIGN KEY (user_code) REFERENCES User(user_code), " +
                "FOREIGN KEY (menu_code) REFERENCES Menu(menu_code))")

        db.execSQL("CREATE TABLE Manager" +
                "(manager_code INTEGER NOT NULL, " +
                "manager_accessCode INTEGER NOT NULL, " +
                "FOREIGN KEY (manager_code) REFERENCES User(user_code))")

        db.execSQL("INSERT INTO User (user_id, user_password, user_name)" +
                "VALUES(?, ?, ?)", arrayOf("abcd", "abcd", "임채은"))

        db.execSQL("INSERT INTO Manager (manager_code, manager_accessCode)" +
                "VALUES(?, ?)", arrayOf("abcd", "3333"))

    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("CREATE TABLE IF NOT EXISTS Stock (" +
                "stock_code INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "stock_name TEXT NOT NULL, " +
                "stock_number INTEGER NOT NULL)")

        db.execSQL("CREATE TABLE IF NOT EXISTS Menu" +
                "(menu_code INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "menu_name TEXT NOT NULL, " +
                "menu_category TEXT NOT NULL, " +
                "menu_price INTEGER NOT NULL, " +
                "menu_number INTEGER, " +
                "menu_intro TEXT, " +
                "menu_show TEXT NOT NULL, " +
                "menu_image	BLOB, " +
                "menu_sale INTEGER, " +
                "menu_soldOut TEXT NOT NULL)")

        db.execSQL("CREATE TABLE IF NOT EXISTS Ingredient" +
                "(menu_code INTEGER NOT NULL, " +
                "stock_code INTEGER NOT NULL, " +
                "stock_need INTEGER NOT NULL, " +
                "FOREIGN KEY (menu_code) REFERENCES Menu(menu_code), " +
                "FOREIGN KEY (stock_code) REFERENCES Stock(stock_code))")

        db.execSQL("CREATE TABLE IF NOT EXISTS User" +
                "(user_code INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT NOT NULL, " +
                "user_password TEXT NOT NULL, " +
                "user_name TEXT NOT NULL, " +
                "user_seatNumber INTEGER, " +
                "user_signUp_timeStamp TEXT)")

        db.execSQL("CREATE TABLE IF NOT EXISTS OrderForm" +
                "(order_id	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "user_code INTEGER NOT NULL, " +
                "menu_code INTEGER NOT NULL, " +
                "order_number INTEGER NOT NULL, " +
                "order_price INTEGER NOT NULL, " +
                "order_timeStamp INTEGER NOT NULL, " +
                "order_status TEXT NOT NULL, " +
                "FOREIGN KEY (user_code) REFERENCES User(user_code), " +
                "FOREIGN KEY (menu_code) REFERENCES Menu(menu_code))")

        db.execSQL("CREATE TABLE IF NOT EXISTS Manager" +
                "(manager_code INTEGER NOT NULL, " +
                "manager_accessCode INTEGER NOT NULL, " +
                "FOREIGN KEY (manager_code) REFERENCES User(user_code))")


        Log.d(TAG, "onOpen() : DB Opening!")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    // db를 assets에서 복사해온다.
    private fun dbCopy() {
        try {
            val folder = File(DB_PATH)
            if (!folder.exists()) {
                folder.mkdir()
            }
            val inputStream = mContext.assets.open(DB_NAME)
            val out_filename = DB_PATH + DB_NAME
            val outputStream: OutputStream = FileOutputStream(out_filename)
            val mBuffer = ByteArray(1024)
            var mLength: Int
            while (inputStream.read(mBuffer).also { mLength = it } > 0) {
                outputStream.write(mBuffer, 0, mLength)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("dbCopy", "IOException 발생함")
        }
    }

    companion object {
        private const val TAG = "DatabaseHelper" // Logcat에 출력할 태그이름

        // database 의 파일 경로
        private var DB_PATH = ""
        private const val DB_NAME = "PCRoomData.db"
    }
}