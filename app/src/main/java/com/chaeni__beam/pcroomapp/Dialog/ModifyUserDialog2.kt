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
import com.chaeni__beam.pcroomapp.databinding.ActivityModifyUserDialog2Binding
import com.chaeni__beam.pcroomapp.databinding.ActivityModifyUserInfoDialogBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper

class ModifyUserDialog2 (
    context: Context,
    code : Int,
    private val okCallback: () -> Unit
) : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private lateinit var binding: ActivityModifyUserDialog2Binding

    lateinit var database : SQLiteDatabase

    val code : Int = code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 만들어놓은 dialog_profile.xml 뷰를 띄운다.
        binding = ActivityModifyUserDialog2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        // 뒤로가기 버튼, 빈 화면 터치를 통해 dialog가 사라지지 않도록

        // background를 투명하게 만듦
        // (중요) Dialog는 내부적으로 뒤에 흰 사각형 배경이 존재하므로, 배경을 투명하게 만들지 않으면
        // corner radius의 적용이 보이지 않는다.
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // OK Button 클릭에 대한 Callback 처리. 이 부분은 상황에 따라 자유롭게!

        deleteBtn.setOnClickListener{
            if(accessCodeArea.text.isNullOrBlank()){
                Toast.makeText(context, "액세스 코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                deleteManager()
                Toast.makeText(context, "매니저를 삭제하였습니다.", Toast.LENGTH_SHORT).show()
                okCallback()
                dismiss()
            }
        }

        closeBtn.setOnClickListener{
            dismiss()
        }

    }


    fun openDatabase(){
        val dbHelper = DataBaseHelper(context)
        database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득
    }


    fun deleteManager(){
        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor = database.rawQuery("SELECT manager_accessCode FROM Manager " +
                "WHERE manager_code=$code", null)

        cursor.moveToNext()

        if(binding.accessCodeArea.text!!.equals(cursor.getString(0))){
            Toast.makeText(context, "액세스 코드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        }else{
            openDatabase()
            if(database != null){
                val sql2 = "DELETE FROM Manager WHERE manager_code=$code"
                database.execSQL(sql2)
            }
        }
    }

}