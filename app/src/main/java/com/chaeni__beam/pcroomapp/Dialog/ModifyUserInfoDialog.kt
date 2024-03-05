package com.chaeni__beam.pcroomapp.Dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import com.chaeni__beam.pcroomapp.Activity.UserActivity
import com.chaeni__beam.pcroomapp.databinding.ActivityInsertStockDialogBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityManagerLoginDialogBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityModifyStockDialogBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityModifyUserInfoDialogBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper

class ModifyUserInfoDialog (
    context: Context,
    code : Int,
    private val userActivity: UserActivity,
    private val okCallback: (String) -> Unit
) : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private lateinit var binding: ActivityModifyUserInfoDialogBinding

    lateinit var database : SQLiteDatabase

    val code : Int = code

    var password : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 만들어놓은 dialog_profile.xml 뷰를 띄운다.
        binding = ActivityModifyUserInfoDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUser()
        initViews()
    }

    private fun initViews() = with(binding) {
        // 뒤로가기 버튼, 빈 화면 터치를 통해 dialog가 사라지지 않도록

        // background를 투명하게 만듦
        // (중요) Dialog는 내부적으로 뒤에 흰 사각형 배경이 존재하므로, 배경을 투명하게 만들지 않으면
        // corner radius의 적용이 보이지 않는다.
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // OK Button 클릭에 대한 Callback 처리. 이 부분은 상황에 따라 자유롭게!
        okBtn.setOnClickListener {
            if(currentPasswordArea.text.isNullOrBlank()) {
                Toast.makeText(context,"현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(currentPasswordArea.text.toString() != password){
                Toast.makeText(context,"비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(!newPasswordArea.text.toString().equals(checkNewPasswordArea.text.toString())){
                Toast.makeText(context,"새 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,"회원 정보 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                updateUser()
                dismiss()
            }
        }

        deleteBtn.setOnClickListener{
            if(currentPasswordArea.text.isNullOrBlank()) {
                Toast.makeText(context,"현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else if(currentPasswordArea.text.toString() != password) {
                Toast.makeText(context, "비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                val dlg: AlertDialog.Builder = AlertDialog.Builder(context,  android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
                dlg.setMessage("정말 회원 탈퇴하시겠습니까?.") // 메시지
                dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(context,"회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    deleteUser()
                    dismiss()
                    userActivity.finish()
                })
                dlg.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->

                })
                dlg.show()
            }
        }

        closeBtn.setOnClickListener{
            dismiss()
        }

    }

    fun setUser(){
        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor = database.rawQuery("SELECT user_id, user_name, user_password, user_signUp_timeStamp FROM User " +
                "WHERE user_code=$code", null)
        cursor.moveToNext()

        binding.idArea.setText(cursor.getString(0))
        binding.nameArea.setText(cursor.getString(1))
        password = cursor.getString(2)
        binding.dateArea.setText("가입일: " + cursor.getString(3))

        cursor.close()
    }

    fun openDatabase(){
        val dbHelper = DataBaseHelper(context)
        database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득
    }

    fun updateUser(){
        openDatabase()
        if(database != null){
            if(binding.newPasswordArea.text.isNullOrBlank()){
                val sql = "UPDATE User SET user_name=? WHERE user_code=$code"
                val params = arrayOf(binding.nameArea.text.toString())
                database.execSQL(sql, params)
            }else{
                val sql = "UPDATE User SET user_name=?, user_password=? WHERE user_code=$code"
                val params = arrayOf(binding.nameArea.text.toString(), binding.newPasswordArea.text.toString())
                database.execSQL(sql, params)
            }
        }
    }

    fun deleteUser(){
        openDatabase()
        if(database != null){
            val sql2 = "DELETE FROM OrderForm WHERE user_code=$code"
            database.execSQL(sql2)
            val sql = "DELETE FROM User WHERE user_code=$code"
            database.execSQL(sql)
        }
    }

}