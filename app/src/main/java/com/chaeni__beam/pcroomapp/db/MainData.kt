package com.chaeni__beam.pcroomapp.db

data class MainData(
    var image : ByteArray?,
    var name : String,
    var price : Int,
    var intro : String?,
    var soldOut : String,
    var code : Int
)
