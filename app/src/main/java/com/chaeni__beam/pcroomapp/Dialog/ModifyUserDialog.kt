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
import com.chaeni__beam.pcroomapp.databinding.ActivityModifyUserDialogBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityModifyUserInfoDialogBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper

class ModifyUserDialog ( //매니저로만들기
    context: Context,
    code : Int,
    private val okCallback: () -> Unit
) : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private lateinit var binding: ActivityModifyUserDialogBinding

    lateinit var database : SQLiteDatabase

    val code : Int = code

    var password : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 만들어놓은 dialog_profile.xml 뷰를 띄운다.
        binding = ActivityModifyUserDialogBinding.inflate(layoutInflater)
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
        okBtn.setOnClickListener {
            if(accessCodeArea.text.isNullOrBlank()){
                Toast.makeText(context, "액세스 코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                insertManager()
                Toast.makeText(context, "매니저가 추가되었습니다.", Toast.LENGTH_SHORT).show()
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

    fun insertManager(){
        openDatabase()
        if(database != null){
            val sql = "INSERT INTO Manager (manager_code, manager_accessCode) " +
                    "VALUES (?, ?);"
            val params = arrayOf(code, binding.accessCodeArea.text)
            database.execSQL(sql, params)
        }
    }

}