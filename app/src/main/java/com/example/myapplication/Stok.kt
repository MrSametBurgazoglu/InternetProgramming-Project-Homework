package com.example.myapplication

object Stok {
    val liste = mutableListOf<ProductModel>()

    fun add_to_list(productModel: ProductModel){
        liste.add(productModel)
    }

    fun clear_list(){
        liste.clear()
    }
}