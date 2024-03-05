package com.chaeni__beam.pcroomapp.Activity

import android.content.Intent
import android.content.res.ColorStateList
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaeni__beam.pcroomapp.Adapter.IngAdapter
import com.chaeni__beam.pcroomapp.Adapter.MenuAdapter
import com.chaeni__beam.pcroomapp.Adapter.MenuStockCalc
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
import java.util.Locale


class MenuStockFragment : Fragment(), View.OnClickListener, MenuStockCalc {

    private var _binding : FragmentStockBinding ?= null
    private val binding get() = _binding!!

    lateinit var database : SQLiteDatabase

    var stockList = mutableListOf<StockData>()
    var searchStockList= mutableListOf<StockData>()
    lateinit var stockAdapter : StockAdapter

    var menuList = mutableListOf<MenuData>()
    var searchMenuList= mutableListOf<MenuData>()
    lateinit var menuAdapter: MenuAdapter

    var mode = "Menu"

    var sortMode = ""

    var menu_mode = "all"

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
        binding.stockNumberBtn.setOnClickListener(this)
        binding.menuNumberBtn.setOnClickListener(this)
        binding.menuSaleBtn.setOnClickListener(this)

        mode = "Menu"

        menu_mode = "all"

        menuStockCalc(requireContext())

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
        searchStock()
    }

    fun menuMode(){
        binding.stockColumn.visibility = View.GONE
        binding.menuColumn.visibility = View.VISIBLE
        binding.categorySpinner.visibility = View.VISIBLE
        categorySpinner()
        readMenu()
        modifyMenu()
        searchMenu()
    }


    fun readStock(){
        binding.RV.adapter = null
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

    fun searchStock(){
        binding.searchArea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) { //edittext가 바뀔 때마다 호출
                var searchText = binding.searchArea.text.toString()
                searchStockList.clear()

                if(searchText.equals("")){
                    stockAdapter!!.setItems(stockList)
                }else{
                    for (a in 0 until stockList.size) {
                        if (stockList.get(a).name.toLowerCase()
                                .contains(searchText.lowercase(Locale.getDefault()))
                        ) {
                            searchStockList.add(stockList.get(a))
                        }
                        stockAdapter!!.setItems(searchStockList)
                    }
                }
            }

        })
    }


    fun modifyStock(){
        stockAdapter.setItemClickListener(object : StockAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                if(binding.searchArea.text.isNullOrBlank()){
                    ModifyStockDialog(requireContext(), stockList[position].name,
                        stockList[position].number.toString(), stockList[position].code, ){
                        readStock()
                    }.show()
                }else{
                    ModifyStockDialog(requireContext(), searchStockList[position].name,
                        searchStockList[position].number.toString(), searchStockList[position].code, ){
                        readStock()
                    }.show()
                }
            }
        })
    }

    fun sortStock(){
        stockList.clear()

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor : Cursor

        if(sortMode == "DESC"){
            cursor = database.rawQuery("SELECT stock_name, stock_number, stock_code FROM Stock " +
                    "ORDER BY stock_number DESC", null)
        }else {
            cursor = database.rawQuery("SELECT stock_name, stock_number, stock_code FROM Stock " +
                        "ORDER BY stock_number ASC", null)
        }

        for(i in 0..cursor.count-1){
            cursor.moveToNext()
            val date = StockData(cursor.getInt(2),
                cursor.getString(0), cursor.getInt(1))
            stockList.add(date)
        }
        cursor.close()

        stockAdapter = StockAdapter(stockList)
        binding.RV.layoutManager = GridLayoutManager(activity, 2)
        binding.RV.adapter = stockAdapter

        dbHelper.close()
    }

    fun readMenu(){
        binding.RV.adapter = null

        menuList.clear()

        val menu_mode = when(menu_mode){
            "all" ->{"전체"}
            "rice" ->{"밥류"}
            "noodle" ->{"면류"}
            "snack" ->{"과자"}
            "side" ->{"사이드"}
            "topping" ->{"토핑"}
            "drink" ->{"음료"}
            else ->{}
        }

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        if(menu_mode == "전체"){
            val cursor = database.rawQuery("SELECT menu_category, menu_name," +
                    "menu_number, menu_sale, menu_code  FROM Menu", null)

            for(i in 0..cursor.count-1){
                cursor.moveToNext()
                val date = MenuData(cursor.getString(0),
                    cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4))
                menuList.add(date)
            }
            cursor.close()
        }else{
            val cursor = database.rawQuery("SELECT menu_category, menu_name," +
                    "menu_number, menu_sale, menu_code  FROM Menu WHERE menu_category='${menu_mode}'", null)

            for(i in 0..cursor.count-1){
                cursor.moveToNext()
                val date = MenuData(cursor.getString(0),
                    cursor.getString(1), cursor.getInt(2),
                    cursor.getInt(3), cursor.getInt(4))
                menuList.add(date)
            }
            cursor.close()
        }

        menuAdapter = MenuAdapter(menuList)
        binding.RV.layoutManager = LinearLayoutManager(activity)
        binding.RV.adapter = menuAdapter


        dbHelper.close()
    }

    fun searchMenu(){
        binding.searchArea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) { //edittext가 바뀔 때마다 호출
                var searchText = binding.searchArea.text.toString()
                searchMenuList.clear()

                if(searchText.equals("")){
                    menuAdapter!!.setItems(menuList)
                }else{
                    for (a in 0 until menuList.size) {
                        if (menuList.get(a).name.toLowerCase()
                                .contains(searchText.lowercase(Locale.getDefault()))
                        ) {
                            searchMenuList.add(menuList.get(a))
                        }
                        menuAdapter!!.setItems(searchMenuList)
                    }
                }
            }

        })
    }

    fun modifyMenu(){
        menuAdapter.setItemClickListener(object  : MenuAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                Log.d("tttt", "Item clicked at position///: $position")
                if(binding.searchArea.text.isNullOrBlank()){
                    val intent = Intent(context, ModifyMenuActivity::class.java)
                    intent.putExtra("data", menuList[position].code)
                    startActivity(intent)
                }else{
                    val intent = Intent(context, ModifyMenuActivity::class.java)
                    intent.putExtra("data", searchMenuList[position].code)
                    startActivity(intent)
                }
            }
        })
    }

    fun sortMenu(){
        menuList.clear()

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor : Cursor

        if(sortMode == "NumDESC"){
            cursor = database.rawQuery("SELECT menu_category, menu_name," +
                    "menu_number, menu_sale, menu_code  FROM Menu " +
                    "ORDER BY menu_number DESC", null)

        }else if(sortMode == "NumASC"){
            cursor = database.rawQuery("SELECT menu_category, menu_name," +
                    "menu_number, menu_sale, menu_code  FROM Menu " +
                    "ORDER BY menu_number ASC", null)

        }else if(sortMode == "SaleDESC"){
            cursor = database.rawQuery("SELECT menu_category, menu_name," +
                    "menu_number, menu_sale, menu_code  FROM Menu " +
                    "ORDER BY menu_sale DESC", null)

        }else{ //SaleASC
            cursor = database.rawQuery("SELECT menu_category, menu_name," +
                    "menu_number, menu_sale, menu_code  FROM Menu " +
                    "ORDER BY menu_sale ASC", null)

        }

        for(i in 0..cursor.count-1){
            cursor.moveToNext()
            val date = MenuData(cursor.getString(0),
                cursor.getString(1), cursor.getInt(2),
                cursor.getInt(3), cursor.getInt(4))
            menuList.add(date)
        }

        menuAdapter = MenuAdapter(menuList)
        binding.RV.layoutManager = LinearLayoutManager(activity)
        binding.RV.adapter = menuAdapter

        cursor.close()
        dbHelper.close()
    }

    fun categorySpinner() {
        binding.categorySpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.food_category, android.R.layout.simple_list_item_1)

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    // 전체
                    0 -> {
                        menu_mode = "all"
                        readMenu()
                        modifyMenu()
                    }
                    // 밥류
                    1 -> {
                        menu_mode = "rice"
                        readMenu()
                        modifyMenu()
                    }
                    // 면류
                    2 -> {
                        menu_mode = "noodle"
                        readMenu()
                        modifyMenu()
                    }
                    // 과자
                    3 -> {
                        menu_mode = "snack"
                        readMenu()
                        modifyMenu()
                    }
                    // 사이드
                    4 -> {
                        menu_mode = "side"
                        readMenu()
                        modifyMenu()
                    }
                    // 토핑
                    5 -> {
                        menu_mode = "topping"
                        readMenu()
                        modifyMenu()
                    }
                    // 음료
                    6 -> {
                        menu_mode = "drink"
                        readMenu()
                        modifyMenu()
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
                        modifyStock()
                    }.show()
                }else{
                    val intent = Intent(context, InsertMenuActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    readMenu()
                    modifyMenu()
                }

            }

            R.id.menuBtn ->{
                binding.menuBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EDEDED"))
                binding.stockBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3C3C3"))
                menuMode()
                mode = "Menu"
                sortMode = ""
                binding.searchArea.setText("")
                menuStockCalc(requireContext())
            }
            R.id.stockBtn ->{
                binding.stockBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EDEDED"))
                binding.menuBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3C3C3"))
                stockMode()
                mode = "Stock"
                sortMode = ""
                binding.searchArea.setText("")
                menuStockCalc(requireContext())
            }

            R.id.stockNumberBtn ->{
                binding.stockNumImg.visibility = View.VISIBLE
                if(sortMode == ""){
                    sortMode = "DESC"
                    binding.stockNumImg.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                }else if(sortMode == "DESC"){
                    sortMode = "ASC"
                    binding.stockNumImg.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                }else{
                    sortMode = "DESC"
                    binding.stockNumImg.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                }
                sortStock()
                modifyStock()
            }
            R.id.menuNumberBtn ->{
                binding.menuSaleImg.visibility = View.GONE
                binding.menuNumImg.visibility = View.VISIBLE
                if(sortMode == "" || sortMode == "SaleDESC" || sortMode == "SaleASC"){
                    sortMode = "NumDESC"
                    binding.menuNumImg.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                }else if(sortMode == "NumDESC"){
                    sortMode = "NumASC"
                    binding.menuNumImg.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                }else{
                    sortMode = "NumDESC"
                    binding.menuNumImg.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                }
                sortMenu()
                modifyMenu()
            }

            R.id.menuSaleBtn ->{
                binding.menuSaleImg.visibility = View.VISIBLE
                binding.menuNumImg.visibility = View.GONE
                if(sortMode == "" || sortMode == "NumDESC" || sortMode == "NumASC"){
                    sortMode = "SaleDESC"
                    binding.menuSaleImg.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                }else if(sortMode == "SaleDESC"){
                    sortMode = "SaleASC"
                    binding.menuSaleImg.setImageResource(R.drawable.baseline_arrow_drop_up_24)
                }else{
                    sortMode = "SaleDESC"
                    binding.menuSaleImg.setImageResource(R.drawable.baseline_arrow_drop_down_24)
                }
                sortMenu()
                modifyMenu()
            }
        }
    }

}