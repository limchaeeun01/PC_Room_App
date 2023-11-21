package com.chaeni__beam.pcroomapp.Activity

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaeni__beam.pcroomapp.Adapter.MenuAdapter
import com.chaeni__beam.pcroomapp.Adapter.StockAdapter
import com.chaeni__beam.pcroomapp.Dialog.InsertMenuActivity
import com.chaeni__beam.pcroomapp.Dialog.InsertStockDialog
import com.chaeni__beam.pcroomapp.Dialog.ModifyMenuActivity
import com.chaeni__beam.pcroomapp.Dialog.ModifyStockDialog
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.FragmentStockBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.MenuData
import com.chaeni__beam.pcroomapp.db.StockData


class MenuStockFragment : Fragment(), View.OnClickListener {

    private var _binding : FragmentStockBinding ?= null
    private val binding get() = _binding!!

    lateinit var database : SQLiteDatabase

    var stockList = mutableListOf<StockData>()
    lateinit var stockAdapter : StockAdapter

    var menuList = mutableListOf<MenuData>()
    lateinit var menuAdapter: MenuAdapter

    var mode = "Menu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.insertBtn.setOnClickListener(this)
        binding.menuBtn.setOnClickListener(this)
        binding.stockBtn.setOnClickListener(this)

        menuMode()

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun stockMode(){
        binding.stockColumn.visibility = View.VISIBLE
        binding.menuColumn.visibility = View.GONE
        binding.categorySpinner.visibility = View.GONE
        readStock()
        modifyStock()
    }

    fun menuMode(){
        binding.stockColumn.visibility = View.GONE
        binding.menuColumn.visibility = View.VISIBLE
        binding.categorySpinner.visibility = View.VISIBLE
        categorySpinner()
        readMenu()
        modifyMenu()
    }


    fun readStock(){
        stockList.clear()

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득
        //val stockList = mutableListOf<StockData>()
        val cursor = database.rawQuery("SELECT stock_name, stock_number, stock_code FROM Stock", null)

        for(i in 0..cursor.count-1){
            cursor.moveToNext()
            val date = StockData(cursor.getInt(2),
                cursor.getString(0), cursor.getInt(1))
            stockList.add(date)
        }

        stockAdapter = StockAdapter(stockList)
        binding.RV.layoutManager = GridLayoutManager(activity, 2)
        binding.RV.adapter = stockAdapter


        cursor.close()
        dbHelper.close()
    }


    fun modifyStock(){
        stockAdapter.setItemClickListener(object : StockAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                ModifyStockDialog(requireContext(), stockList[position].name,
                stockList[position].number.toString(), stockList[position].code, ){
                    readStock()
                }.show()
            }
        })
    }

    fun readMenu(){
        menuList.clear()

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득
        //val stockList = mutableListOf<StockData>()
        val cursor = database.rawQuery("SELECT menu_category, menu_name," +
                "menu_number, menu_sale, menu_code  FROM Menu", null)

        for(i in 0..cursor.count-1){
            cursor.moveToNext()
            val date = MenuData(cursor.getString(0),
                cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getInt(4))
            menuList.add(date)
        }

        menuAdapter = MenuAdapter(menuList)
        binding.RV.layoutManager = LinearLayoutManager(activity)
        binding.RV.adapter = menuAdapter

        cursor.close()
        dbHelper.close()
    }

    fun modifyMenu(){
        menuAdapter.setItemClickListener(object  : MenuAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(context, ModifyMenuActivity::class.java)
                intent.putExtra("data", menuList[position].code)
                startActivity(intent)
            }
        })
    }


    fun categorySpinner() {
        binding.categorySpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.food_category, android.R.layout.simple_list_item_1)

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    // 전체
                    0 -> {

                    }
                    // 밥류
                    1 -> {

                    }
                    // 면류
                    2 -> {

                    }
                    // 핫도그
                    3 -> {

                    }
                    // 사이드
                    4 -> {

                    }
                    // 토핑
                    5 -> {

                    }
                    // 기타
                    6 -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때 수행할 동작 (필요하면 구현)
            }
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.insertBtn -> {
                if(mode == "Stock"){
                    InsertStockDialog(requireContext()){
                        readStock()
                    }.show()
                }else{
                    val intent = Intent(context, InsertMenuActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    readMenu()
                }

            }

            R.id.menuBtn ->{
                binding.menuBtn.setBackgroundColor(Color.parseColor("#EDEDED"))
                binding.stockBtn.setBackgroundColor(Color.parseColor("#C3C3C3"))
                menuMode()
                mode = "Menu"
            }
            R.id.stockBtn ->{
                binding.menuBtn.setBackgroundColor(Color.parseColor("#C3C3C3"))
                binding.stockBtn.setBackgroundColor(Color.parseColor("#EDEDED"))
                stockMode()
                mode = "Stock"
            }
        }
    }

}