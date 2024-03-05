package com.chaeni__beam.pcroomapp.Dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaeni__beam.pcroomapp.Adapter.IngAdapter
import com.chaeni__beam.pcroomapp.databinding.SelectIngredientDialogBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.StockData
import java.util.Locale


class SelectIngDialog (
    context: Context,
    private val okCallback: (String) -> Unit,
) : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private lateinit var binding: SelectIngredientDialogBinding

    var ingList = mutableListOf<StockData>()
    var searchList = mutableListOf<StockData>()

    lateinit var database : SQLiteDatabase

    var adapter : IngAdapter? = null

    var select = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 만들어놓은 dialog_profile.xml 뷰를 띄운다.
        binding = SelectIngredientDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectedIng.visibility = View.GONE

        searchIng()

        selectIng()

        initViews()
    }


    fun searchIng(){
        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor = database.rawQuery("SELECT * FROM Stock", null)

        for(i in 0..cursor.count-1){
            cursor.moveToNext()
            val date = StockData(cursor.getInt(0),
                cursor.getString(1), cursor.getInt(2))
            ingList.add(date)
        }

        cursor.close()
        dbHelper.close()

        binding.searchArea.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) { //edittext가 바뀔 때마다 호출
                var searchText = binding.searchArea.text.toString()
                searchList.clear()

                if(searchText.equals("")){
                    adapter!!.setItems(ingList)
                }else{
                    for (a in 0 until ingList.size) {
                        if (ingList.get(a).name.toLowerCase()
                                .contains(searchText.lowercase(Locale.getDefault()))
                        ) {
                            searchList.add(ingList.get(a))
                        }
                        adapter!!.setItems(searchList)
                    }
                }
            }

        })

        adapter = IngAdapter(ingList)
        binding.ingRV.layoutManager = LinearLayoutManager(context)
        binding.ingRV.adapter = adapter

    }

    private fun initViews() = with(binding) {
        // 뒤로가기 버튼, 빈 화면 터치를 통해 dialog가 사라지지 않도록
        // background를 투명하게 만듦
        // (중요) Dialog는 내부적으로 뒤에 흰 사각형 배경이 존재하므로, 배경을 투명하게 만들지 않으면
        // corner radius의 적용이 보이지 않는다.
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // OK Button 클릭에 대한 Callback 처리. 이 부분은 상황에 따라 자유롭게!
        okBtn.setOnClickListener {
            if(!select){
                Toast.makeText(context, "재료를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }else if(binding.numberArea.text.isNullOrBlank()){
                Toast.makeText(context, "필요량을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }else{
                okCallback(binding.stockCode.text.toString() + ";" + binding.selectedIng.text.toString()
                        + ";" + binding.numberArea.text.toString())
                dismiss()
            }
        }

        closeBtn.setOnClickListener{
            dismiss()
        }

    }

    fun selectIng(){
        select = true

        adapter!!.setItemClickListener(object : IngAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                if(binding.searchArea.text.toString().isNullOrBlank()){
                    binding.selectedIng.text = ingList[position].name
                    binding.stockCode.text = ingList[position].code.toString()
                }else{
                    binding.selectedIng.text = searchList[position].name
                    binding.stockCode.text = searchList[position].code.toString()
                }
                binding.selectedIng.visibility = View.VISIBLE
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }

}