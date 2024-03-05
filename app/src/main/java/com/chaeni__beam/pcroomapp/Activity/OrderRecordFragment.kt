package com.chaeni__beam.pcroomapp.Activity

import android.content.res.ColorStateList
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaeni__beam.pcroomapp.Adapter.OrderFormManagerAdapter
import com.chaeni__beam.pcroomapp.Adapter.OrderFormUserAdapter
import com.chaeni__beam.pcroomapp.Adapter.StockAdapter
import com.chaeni__beam.pcroomapp.Dialog.ModifyStockDialog
import com.chaeni__beam.pcroomapp.Dialog.ModifyUserInfoDialog
import com.chaeni__beam.pcroomapp.R
import com.chaeni__beam.pcroomapp.databinding.FragmentOrderRecordBinding
import com.chaeni__beam.pcroomapp.databinding.FragmentOrderStatusBinding
import com.chaeni__beam.pcroomapp.databinding.FragmentStockBinding
import com.chaeni__beam.pcroomapp.db.DataBaseHelper
import com.chaeni__beam.pcroomapp.db.OrderFormData
import com.chaeni__beam.pcroomapp.db.StockData


class OrderRecordFragment : Fragment(), View.OnClickListener {

    private var _binding : FragmentOrderRecordBinding?= null
    private val binding get() = _binding!!

    lateinit var database : SQLiteDatabase

    var orderList = mutableListOf<OrderFormData>()
    lateinit var orderAdapter : OrderFormUserAdapter

    var user_code : Int? = null

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
        _binding = FragmentOrderRecordBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.userInfoBtn.setOnClickListener(this)

        readOrder()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun readOrder(){
        orderList.clear()

        val dbHelper = DataBaseHelper(context)
        database = dbHelper.readableDatabase  //데이터베이스 읽기 권한 획득

        val cursor1 = database.rawQuery("SELECT * FROM OrderForm WHERE user_code=$user_code " +
                "ORDER BY order_id DESC", null)

        for(i in 0..cursor1.count-1) {
            cursor1.moveToNext()
            val cursor2 = database.rawQuery(
                "SELECT user_seatNumber, user_name FROM User " +
                        "WHERE user_code=${cursor1.getInt(1)}", null
            )
            val cursor3 = database.rawQuery(
                "SELECT menu_code, menu_name, menu_price FROM Menu " +
                        "WHERE menu_code=${cursor1.getInt(2)}", null
            )
            cursor2.moveToNext()
            cursor3.moveToNext()
            val data = OrderFormData(
                cursor2.getInt(0), cursor2.getString(1),
                cursor1.getInt(0), cursor3.getInt(0), cursor3.getString(1), cursor1.getInt(4),
                cursor1.getInt(3), cursor1.getString(5), cursor1.getString(6)
            )

            orderList.add(data)

            cursor2.close()
            cursor3.close()
        }

        cursor1.close()

        orderAdapter = OrderFormUserAdapter(orderList)
        binding.orderRV.layoutManager = LinearLayoutManager(activity)
        binding.orderRV.adapter = orderAdapter

        dbHelper.close()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.userInfoBtn ->{
                ModifyUserInfoDialog(requireContext(), user_code!!, requireActivity() as UserActivity){

                }.show()
            }
        }
    }
}