package com.chaeni__beam.pcroomapp.db

data class MenuData(
    var category : String,
    var name : String,
    var number : Int? = null,
    var sale : Int,
    var code : Int
)
