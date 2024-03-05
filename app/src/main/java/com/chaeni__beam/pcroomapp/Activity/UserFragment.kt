package com.chaeni__beam.pcroomapp.Activity

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaeni__beam.pcroomapp.Adapter.MainAdapter
import com.chaeni__beam.pcroomapp.Adapter.StockAdapter
import com.chaeni__beam.pcroomapp.Adapter.UserAdapter
import com.chaeni__beam.pcroomapp.Dialog.InsertStockDialog
import com.chaeni__beam.pcroomapp.Dialog.ModifyUserDialog
import com.chaeni__beam.pcroomapp.Dialog.ModifyUserDialog2
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.FragmentStockBinding
import com.chaeni__beam.pcroomapp.databinding.FragmentUserBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.StockData
import com.chaeni__beam.pcroomapp.db.UserData
import java.util.Locale


class UserFragment : Fragment(), View.OnClickListener {

    private var _binding : FragmentUserBinding?= null
    private val binding get() = _binding!!

    lateinit var database : SQLiteDatabase

    var userList = mutableListOf<UserData>()
    var searchUserList= mutableListOf<UserData>()
    lateinit var userAdapter : UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root

        readUser()

        searchUser()

        changeDivision()

        return view
    }

    fun readUser(){
        userList.clear()

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득
        //val stockList = mutableListOf<StockData>()
        val cursor = database.rawQuery("SELECT user_code, user_id, user_name FROM User", null)

        for(i in 0..cursor.count-1){
            cursor.moveToNext()
            val cursor2 = database.rawQuery("SELECT * FROM Manager " +
                    "WHERE manager_code=${cursor.getInt(0)}", null)
            val cursor3 = database.rawQuery("SELECT * FROM OrderForm " +
                    "WHERE user_code=${cursor.getInt(0)}", null)
            if(cursor2.count != 0){
                val date = UserData(cursor.getInt(0), cursor.getString(1),
                cursor.getString(2), "매니저", cursor3.count)
                userList.add(date)
            }else{
                val date = UserData(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), "일반회원", cursor3.count)
                userList.add(date)
            }
        }
        userAdapter = UserAdapter(userList)
        binding.RV.layoutManager = LinearLayoutManager(activity)
        binding.RV.adapter = userAdapter

        cursor.close()
        dbHelper.close()
    }

    fun searchUser(){
        binding.searchArea.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) { //edittext가 바뀔 때마다 호출
                var searchText = binding.searchArea.text.toString()
                searchUserList.clear()

                if(searchText.equals("")){
                    userAdapter!!.setItems(userList)
                }else{
                    for (a in 0 until userList.size) {
                        if (userList.get(a).user_name.toLowerCase()
                                .contains(searchText.lowercase(Locale.getDefault()))
                        ) {
                            searchUserList.add(userList.get(a))
                        }
                        userAdapter!!.setItems(searchUserList)
                    }
                }
            }

        })
    }

    fun changeDivision(){
        userAdapter.setItemClickListener(object : UserAdapter.onItemClickListener{
            override fun onItemClick(v:View, position: Int) {
                if (binding.searchArea.text.isNullOrBlank()) {
                    if (userList[position].user_division == "매니저") {
                        ModifyUserDialog2(requireContext(), userList[position].user_code){
                            readUser()
                            searchUser()
                            changeDivision()
                        }.show()
                    } else{ //일반회원
                        ModifyUserDialog(requireContext(), userList[position].user_code){
                            readUser()
                            searchUser()
                            changeDivision()
                        }.show()
                    }

                } else {
                    if (searchUserList[position].user_division == "매니저") {
                        ModifyUserDialog2(requireContext(), searchUserList[position].user_code){
                            readUser()
                            searchUser()
                            changeDivision()
                        }.show()
                    } else{ //일반회원
                        ModifyUserDialog(requireContext(), searchUserList[position].user_code){
                            readUser()
                            searchUser()
                            changeDivision()
                        }.show()
                    }
                }
            }


        })
    }


    override fun onClick(v: View?) {
        when(v?.id){

        }
    }


}