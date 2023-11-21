package com.chaeni__beam.pcroomapp.Dialog

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaeni__beam.pcroomapp.Adapter.SelectedIngAdapter
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.ActivityInsertMenuBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.IngData
import com.chaeni__beam.pcroomapp.db.StockData
import java.io.ByteArrayOutputStream

class InsertMenuActivity : AppCompatActivity(), View.OnClickListener {

    val binding by lazy { ActivityInsertMenuBinding.inflate(layoutInflater) }

    var ingList = mutableListOf<IngData>()
    lateinit var adapter : SelectedIngAdapter

    lateinit var database : SQLiteDatabase

    private val SELECT_IMAGE_REQUEST_CODE = 1

    val permission_request = 99
    var permissions = arrayOf(
        android.Manifest.permission.READ_MEDIA_IMAGES
    )


    var menuOn : String = "false"

    var soldOut : String = "false"

    var number : String = ""

    var category : String = ""

    var imageChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        binding.okBtn.setOnClickListener(this)
        binding.addIngredientBtn.setOnClickListener(this)
        binding.closeBtn.setOnClickListener(this)
        binding.menuImage.setOnClickListener(this)

        categorySpinner()

        ingRV()

        binding.onMenu.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                menuOn = "true"
            }else{
                menuOn = "false"
            }
        }
        binding.soldOut.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                soldOut = "true"
            }else{
                soldOut = "false"
            }
        }
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.okBtn ->{
                if (binding.nameArea.text.isNullOrBlank()) {
                    Toast.makeText(this, "메뉴명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if(binding.priceArea.text.isNullOrBlank()){
                    Toast.makeText(this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if(category == ""){
                    Toast.makeText(this, "카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()
                } else{
                    insertMenu(binding.nameArea.text.toString(), category, binding.priceArea.text.toString(),
                        binding.introArea.text.toString(), menuOn)
                    finish()
                }
            }

            R.id.addIngredientBtn -> {
                SelectIngDialog(this){
                    val data = it.split('/')
                    ingList.add(IngData(data[0].toInt(), data[1] ,data[2].toInt()))
                    adapter.notifyDataSetChanged()
                }.show()
            }

            R.id.closeBtn -> {
                finish()
            }

            R.id.menuImage ->{
                openGallery()
            }

        }
    }

    fun ingRV(){
        adapter = SelectedIngAdapter(ingList)
        binding.ingRV.layoutManager = LinearLayoutManager(this)
        binding.ingRV.adapter = adapter
    }

    fun openDatabase(){
        val dbHelper = DataBaseHelper(this)
        database = dbHelper.writableDatabase  //데이터베이스 쓰기 권한 획득
    }


    fun insertMenu(name : String, category : String, price : String, intro : String, show : String){
        openDatabase()

        val price = price.toInt()

        var image : ByteArray? =null

        if(imageChange){
            image = drawableToByteArray(binding.menuImage.drawable)
        }else{

        }

        if(database != null){
            val sql = "INSERT INTO Menu (menu_name, menu_category, menu_price," +
                    "menu_number, menu_intro, menu_show, menu_image, menu_sale," +
                    "menu_soldOut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"

            val params = arrayOf(name, category, price, 0, intro, show, image, 0, soldOut)
            database.execSQL(sql, params)
        }

        val cursor = database.rawQuery("SELECT menu_code FROM Menu WHERE menu_name='$name'", null)
        cursor.moveToNext()
        insertIng(cursor.getInt(0))
        cursor.close()

    }

    fun insertIng(code : Int){
        openDatabase()

        if(database != null){
            val sql = "INSERT INTO Ingredient (menu_code, stock_code, stock_need) " +
                    "VALUES ($code, ?, ?);"

            for(i in 0 .. ingList.size-1){
                val params = arrayOf(ingList[i].stock_code, ingList[i].stock_need)
                database.execSQL(sql, params)
            }

        }
    }

    fun categorySpinner() {
        binding.categorySpinner.adapter = ArrayAdapter.createFromResource(
            this, R.array.food_category2, android.R.layout.simple_list_item_1)

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    // 카테고리 선택
                    0 -> {
                        category = ""
                    }
                    // 밥류
                    1 -> {
                        category = "밥류"
                    }
                    // 면류
                    2 -> {
                        category = "면류"
                    }
                    // 핫도그
                    3 -> {
                        category = "핫도그"
                    }
                    // 사이드
                    4 -> {
                        category = "사이드"
                    }
                    // 토핑
                    5 -> {
                        category = "토핑"
                    }
                    // 기타
                    6 -> {
                        category = "기타"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때 수행할 동작 (필요하면 구현)
            }
        }
    }

    fun isPermitted() : Boolean{
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun openGallery(){
        if(isPermitted()){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE)
        }else{
            ActivityCompat.requestPermissions(this, permissions, permission_request)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val selectedImageUri: Uri? = data.data
                binding.menuImage.setImageURI(selectedImageUri)
                imageChange = true
            }
        }
    }

    fun drawableToByteArray(drawable : Drawable?) : ByteArray? {
        val bitmapDrawable = drawable as BitmapDrawable?
        val bitmap = bitmapDrawable?.bitmap
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        return byteArray
    }


}