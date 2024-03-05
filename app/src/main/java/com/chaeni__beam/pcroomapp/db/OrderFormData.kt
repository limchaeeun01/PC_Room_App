package com.chaeni__beam.pcroomapp.db

data class OrderFormData(
    var seatNumber : Int,
    var userName : String,
    var orderId : Int,
    var menu_code : Int,
    var menu_name : String,
    var order_price : Int,
    var order_number : Int,
    var order_timeStamp : String,
    var order_status : String
)
