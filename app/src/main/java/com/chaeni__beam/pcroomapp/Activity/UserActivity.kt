package com.chaeni__beam.pcroomapp.Activity

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowCompat
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.ActivityManagerBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class UserActivity : AppCompatActivity() {

    lateinit var binding : ActivityUserBinding

    lateinit var database : SQLiteDatabase

    private val fragmentMain by lazy { MainFragment() }
    private val fragmentOrderRecord by lazy { OrderRecordFragment() }

    var user_code : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        binding = ActivityUserBinding.inflate(layoutInflater)

        user_code = intent.getIntExtra("user_code", 500)

        val bundle = Bundle()
        bundle.putInt("user_code", user_code!!)
        fragmentMain.arguments = bundle

        supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragmentMain).commit()

        setStatusBarTransparent()

        replaceFragment()
    }

    fun replaceFragment(){
        var navMainBottom = findViewById<BottomNavigationView>(R.id.nav_main_bottom)

        navMainBottom.setOnItemSelectedListener {
            val bundle = Bundle()
            bundle.putInt("user_code", user_code!!)

            when(it.itemId) {
                R.id.nav_bottom_main -> {
                    fragmentMain.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragmentMain).commit()
                }
                R.id.nav_bottom_order -> {
                    fragmentOrderRecord.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.fragmentArea, fragmentOrderRecord).commit()
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
}