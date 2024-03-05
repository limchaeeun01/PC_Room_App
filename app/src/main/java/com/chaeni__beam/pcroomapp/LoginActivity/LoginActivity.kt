package com.chaeni__beam.pcroomapp.LoginActivity

import android.app.Activity
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowCompat
import com.chaeni__beam.pcroomapp.Activity.ManagerActivity
import com.chaeni__beam.pcroomapp.Activity.UserActivity
import com.chaeni__beam.pcroomapp.Dialog.ManagerLoginDialog
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.ActivityLoginBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityModifyMenuBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import java.util.Random

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    lateinit var database : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.signUpBtn.setOnClickListener(this)
        binding.signInBtn.setOnClickListener(this)

        val dbHelper = DataBaseHelper(this)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        setStatusBarTransparent()

    }

    fun searchUser(id : String, password : String){
        val dbHelper = DataBaseHelper(this)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor1 = database.rawQuery("SELECT user_password, user_name, user_code FROM User " +
                "WHERE user_id='$id'", null)
        if(cursor1.count == 0){ //해당 id가 존재하지 않음
            Toast.makeText(this, "가입되지 않은 아이디입니다.", Toast.LENGTH_SHORT).show()
        }else{
            cursor1.moveToNext()
            if(cursor1.getString(0) == password){
                val cursor2 = database.rawQuery("SELECT * FROM Manager " +
                        "WHERE manager_code=${cursor1.getInt(2)}", null)
                if(cursor2.count == 0) { //일반 손님
                    Toast.makeText(this, "${cursor1.getString(1)}님, 환영합니다!", Toast.LENGTH_SHORT)
                        .show()
                    val sql = "UPDATE User SET user_seatNumber=? WHERE user_code =? "
                    val params = arrayOf(randomSeatNumber(), cursor1.getInt(2))
                    database.execSQL(sql, params)

                    val intent = Intent(this, UserActivity::class.java)
                    intent.putExtra("user_code", cursor1.getInt(2))
                    startActivity(intent)
                }else{ //매니저
                    cursor2.moveToNext()
                    ManagerLoginDialog(this, cursor1.getInt(2)){
                        if(it == "Manager"){
                            val sql = "UPDATE User SET user_seatNumber=? WHERE user_code =? "
                            val params = arrayOf(0, cursor1.getInt(2))
                            database.execSQL(sql, params)

                            val intent = Intent(this, ManagerActivity::class.java)
                            intent.putExtra("user_code", cursor1.getInt(2))
                            startActivity(intent)
                        }else{
                            val sql = "UPDATE User SET user_seatNumber=? WHERE user_code =? "
                            val params = arrayOf(randomSeatNumber(), cursor1.getInt(2))
                            database.execSQL(sql, params)

                            val intent = Intent(this, UserActivity::class.java)
                            intent.putExtra("user_code", cursor1.getInt(2))
                            startActivity(intent)
                        }
                    }.show()
                }
            }else{
                Toast.makeText(this, "비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun randomSeatNumber() : Int{
        val random = Random()
        val result = random.nextInt(120) + 1

        val dbHelper = DataBaseHelper(this)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor = database.rawQuery("SELECT * FROM User WHERE user_seatNumber=$result",null)

        if(cursor.count == 0){
            return result
        }else{
            return randomSeatNumber()
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
            R.id.signInBtn ->{
                if(binding.idArea.text.isNullOrBlank()){
                    Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }else if(binding.passwordArea.text.isNullOrBlank()){
                    Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }else{
                    searchUser(binding.idArea.text.toString(), binding.passwordArea.text.toString())
                }
            }
            R.id.signUpBtn ->{
                startActivity(Intent(this, JoinActivity::class.java))
            }

        }
    }
}