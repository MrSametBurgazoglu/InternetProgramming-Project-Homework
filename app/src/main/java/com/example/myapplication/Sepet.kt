package com.example.myapplication

import android.content.Context
import android.util.Log
import android.widget.Toast

object Sepet {
    val liste = mutableListOf<ProductModel>()
    var total_price = 0.0

    fun addToList(productModel: ProductModel){
        liste.add(productModel)
        total_price += productModel.product_count?.times(productModel.product_price!!) ?: 0.0
    }

    fun clearList(){
        liste.clear()
        total_price = 0.0
    }

    fun removeFromList(productModel: ProductModel){
        var product_removed = productModel
        for (product in liste ){
            Log.i("something", product.product_name.toString() + "--" + productModel.product_name.toString())
            if (product.product_name.toString() == productModel.product_name.toString()){
                product_removed = product
            }
        }
        liste.remove(product_removed)
        total_price -= product_removed.product_count?.times(product_removed.product_price!!) ?: 0.0

    }

    fun is_in_list(productModel: ProductModel): Boolean {
        for (product in liste ){
            if (product.product_name.toString() == productModel.product_name.toString()){
                return true
            }
        }
        return false
    }

    fun changeCountForProduct(productModel: ProductModel, count:Int){
        for (product in liste ){
            if (product.product_name.toString() == productModel.product_name.toString()){
                val change = count - product.product_count!!
                product.product_count = count
                total_price += change * product.product_price!!
            }
        }
    }
}