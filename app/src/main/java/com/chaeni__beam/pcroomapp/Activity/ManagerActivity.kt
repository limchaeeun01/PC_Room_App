package com.chaeni__beam.pcroomapp.Activity

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowCompat
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.ActivityManagerBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class ManagerActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding : ActivityManagerBinding

    lateinit var database : SQLiteDatabase

    private val fragmentMain by lazy { MainFragment() }
    private val fragmentOrder by lazy { OrderStatusFragment() }
    private val fragmentStock by lazy { MenuStockFragment() }

    var user_code : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        binding = ActivityManagerBinding.inflate(layoutInflater)

        user_code = intent.getIntExtra("user_code", 500)

        val bundle = Bundle()
        bundle.putInt("user_code", user_code!!)
        fragmentMain.arguments = bundle

        setStatusBarTransparent()

        openDatabase()

        supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragmentMain).commit()

        replaceFragment()

    }

    fun openDatabase(){
        val dbHelper = DataBaseHelper(this)
        database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득
    }


    fun replaceFragment(){
        var navMainBottom = findViewById<BottomNavigationView>(R.id.nav_main_bottom)


        navMainBottom.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_bottom_main -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragmentMain).commit()
                }
                R.id.nav_bottom_menu -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragmentOrder).commit()
                }
                R.id.nav_bottom_stock -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragmentStock).commit()
                }

            }

            true
        }
    }



    fun Activity.setStatusBarTransparent() {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null && ev != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onClick(v: View?) {

    }


}