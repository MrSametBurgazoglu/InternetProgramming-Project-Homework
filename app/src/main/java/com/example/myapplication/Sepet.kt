package com.example.myapplication

object Sepet {
    val liste = mutableListOf<ProductModel>()
    var total_price = 0

    fun add_to_list(productModel: ProductModel){
        liste.add(productModel)
        total_price += productModel.product_price
    }

    fun clear_list(){
        liste.clear()
        total_price = 0
    }
}