package com.chaeni__beam.pcroomapp.Dialog

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Toast
import com.chaeni__beam.pcroomapp.databinding.ActivityInsertStockDialogBinding
import com.chaeni__beam.pcroomapp.databinding.ActivityManagerLoginDialogBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper

class ManagerLoginDialog (
    context: Context,
    code : Int,
    private val okCallback: (String) -> Unit,
) : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private lateinit var binding: ActivityManagerLoginDialogBinding

    lateinit var database : SQLiteDatabase

    val code : Int = code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 만들어놓은 dialog_profile.xml 뷰를 띄운다.
        binding = ActivityManagerLoginDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        // background를 투명하게 만듦
        // (중요) Dialog는 내부적으로 뒤에 흰 사각형 배경이 존재하므로, 배경을 투명하게 만들지 않으면
        // corner radius의 적용이 보이지 않는다.
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // OK Button 클릭에 대한 Callback 처리. 이 부분은 상황에 따라 자유롭게!
        okBtn.setOnClickListener {
            if (accessCodeArea.text.isNullOrBlank()){
                Toast.makeText(context, "액세스 코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                searchManager(accessCodeArea.text.toString().toInt())
            }
        }

        closeBtn.setOnClickListener{
            dismiss()
        }


    }

    fun searchManager(accessCode : Int){
        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor = database.rawQuery("SELECT manager_accessCode FROM Manager " +
                "WHERE manager_code='$code'", null)
        cursor.moveToNext()
        if(cursor.getInt(0) == accessCode){
            Toast.makeText(context, "매니저 모드 로그인 완료", Toast.LENGTH_SHORT).show()
            okCallback("Manager")
        }else{
            Toast.makeText(context, "액세스 코드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
        }

    }



}