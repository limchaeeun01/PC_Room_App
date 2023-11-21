package com.chaeni__beam.pcroomapp.Activity

import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chaeni__beam.pcroomapp.Adapter.MainAdapter
import com.chaeni__beam.pcroomapp.Adapter.StockAdapter
import com.chaeni__beam.pcroomapp.Dialog.ModifyStockDialog
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.FragmentMainBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.MainData
import com.chaeni__beam.pcroomapp.db.StockData

class MainFragment : Fragment(), View.OnClickListener {

    private var _binding : FragmentMainBinding ?= null
    private val binding get() = _binding!!

    lateinit var database : SQLiteDatabase

    var menuList = mutableListOf<MainData>()
    lateinit var mainAdapter: MainAdapter

    var user_code : Int? = null

    var mode = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user_code = requireArguments().getInt("user_code")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.allBtn.setOnClickListener(this)
        binding.popularBtn.setOnClickListener(this)
        binding.riceBtn.setOnClickListener(this)
        binding.noodleBtn.setOnClickListener(this)
        binding.hotDogBtn.setOnClickListener(this)
        binding.sideBtn.setOnClickListener(this)
        binding.toppingBtn.setOnClickListener(this)
        binding.otherBtn.setOnClickListener(this)

        readData()

        clickMenu()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun openDatabase(){
        val dbHelper = DataBaseHelper(context)
        database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득
    }


    fun readData(){
        menuList.clear()

        val mode = when(mode){
            "all" ->{"전체"}
            "popular" -> {"인기"}
            "rice" ->{"밥류"}
            "noodle" ->{"면류"}
            "hotdog" ->{"핫도그"}
            "side" ->{"사이드"}
            "topping" ->{"토핑"}
            "other" ->{"기타"}
            else ->{}
        }

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        if(mode == "전체"){
            val cursor = database.rawQuery("SELECT menu_image, menu_name, menu_price, " +
                    "menu_intro, menu_soldOut, menu_code FROM Menu WHERE menu_show='true'", null)

            for(i in 0..cursor.count-1){
                cursor.moveToNext()
                val date = MainData(cursor.getBlob(0),
                    cursor.getString(1), cursor.getInt(2),
                    cursor.getString(3), cursor.getString(4), cursor.getInt(5))
                menuList.add(date)
            }
            cursor.close()
        }else if(mode == "인기"){

        }else{
            val cursor = database.rawQuery("SELECT menu_image, menu_name, menu_price, " +
                    "menu_intro, menu_soldOut, menu_code FROM Menu WHERE menu_show='true' AND menu_category='$mode'", null)

            for(i in 0..cursor.count-1){
                cursor.moveToNext()
                val date = MainData(cursor.getBlob(0),
                    cursor.getString(1), cursor.getInt(2),
                    cursor.getString(3), cursor.getString(4), cursor.getInt(5))
                menuList.add(date)
            }
            cursor.close()
        }

        mainAdapter = MainAdapter(user_code!!, menuList)
        binding.menuRecyclerview.layoutManager = GridLayoutManager(activity, 2)
        binding.menuRecyclerview.adapter = mainAdapter


        dbHelper.close()
    }

    fun clickMenu(){
        mainAdapter.setItemClickListener(object : MainAdapter.onItemClickListener{
            override fun onItemClick(v:View, position: Int) {
                if(menuList[position].soldOut == "false"){
                    super.onItemClick(v, position)

                }else{

                }
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.allBtn ->{
                mode = "all"
                changeBtnColor()
                binding.allBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }

            R.id.popularBtn ->{
                mode = "popular"
                changeBtnColor()
                binding.popularBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }

            R.id.riceBtn ->{
                mode = "rice"
                changeBtnColor()
                binding.riceBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }

            R.id.noodleBtn ->{
                mode = "noodle"
                changeBtnColor()
                binding.noodleBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }

            R.id.hotDogBtn ->{
                mode = "hotdog"
                changeBtnColor()
                binding.hotDogBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }

            R.id.sideBtn ->{
                mode = "side"
                changeBtnColor()
                binding.sideBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }

            R.id.toppingBtn ->{
                mode = "topping"
                changeBtnColor()
                binding.toppingBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }

            R.id.otherBtn ->{
                mode = "other"
                changeBtnColor()
                binding.otherBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#360455"))
                readData()
            }
        }
    }

    fun changeBtnColor(){
        binding.allBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
        binding.popularBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
        binding.riceBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
        binding.noodleBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
        binding.hotDogBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
        binding.sideBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
        binding.toppingBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
        binding.otherBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#8131B3"))
    }


}