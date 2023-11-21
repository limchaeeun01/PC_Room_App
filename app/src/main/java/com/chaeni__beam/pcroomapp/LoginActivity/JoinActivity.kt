package com.chaeni__beam.pcroomapp.LoginActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.ActivityInsertMenuBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityJoinBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityLoginBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JoinActivity : AppCompatActivity(), View.OnClickListener {

    val binding by lazy { ActivityJoinBinding.inflate(layoutInflater) }

    lateinit var database : SQLiteDatabase

    var duplicateCheck : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.joinBtn.setOnClickListener(this)
        binding.duplicateCheckBtn.setOnClickListener(this)

        setStatusBarTransparent()

        val dbHelper = DataBaseHelper(this)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득
    }

    fun duplicateCheck(id : String){
        val dbHelper = DataBaseHelper(this)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor = database.rawQuery("SELECT user_id FROM User WHERE user_id='$id'", null)

        if(cursor.count == 0){
            val dlg: AlertDialog.Builder = AlertDialog.Builder(this,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            dlg.setMessage("사용 가능한 아이디입니다.") // 메시지
            dlg.setPositiveButton("사용", DialogInterface.OnClickListener { dialog, which ->
                duplicateCheck = true
            })
            dlg.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->

            })
            dlg.show()
        }else{
            val dlg: AlertDialog.Builder = AlertDialog.Builder(this,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            dlg.setMessage("중복된 아이디입니다.") // 메시지
            dlg.setPositiveButton("확인", null)
            dlg.show()
        }


    }

    fun openDatabase(){
        val dbHelper = DataBaseHelper(this)
        database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득
    }

    fun insertUser(){
        openDatabase()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val currentDate = current.format(formatter)

        if(database != null){
            val sql = "INSERT INTO User (user_id, user_password, user_name, user_signUp_timeStamp)" +
                    "VALUES(?, ?, ?, ?)"
            val params = arrayOf(binding.idArea.text.toString(),
            binding.passwordArea.text.toString(), binding.nameArea.text.toString(), currentDate)

            database.execSQL(sql, params)
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
        when(v?.id){
            R.id.joinBtn ->{
                if(binding.nameArea.text.toString().isNullOrBlank()){
                    Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }else if(binding.passwordArea.text.toString().isNullOrBlank()){
                    Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }else if(!binding.passwordArea.text.toString().equals(binding.passwordCheckArea.text.toString())){
                    Toast.makeText(this, "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                }else if(!duplicateCheck){
                    Toast.makeText(this, "아이디 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }else if(binding.nameArea.text.toString().isNullOrBlank()){
                    Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    insertUser()
                    finish()
                }
            }
            R.id.duplicateCheckBtn ->{
                duplicateCheck(binding.idArea.text.toString())
            }
        }
    }
}