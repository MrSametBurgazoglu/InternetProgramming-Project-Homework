package com.example.myapplication

object Sepet {
    val liste = mutableListOf<ProductModel>()
    var total_price = 0

    fun addToList(productModel: ProductModel){
        liste.add(productModel)
        total_price += productModel.product_count?.times(productModel.product_price!!) ?: 0
    }

    fun clearList(){
        liste.clear()
        total_price = 0
    }
}