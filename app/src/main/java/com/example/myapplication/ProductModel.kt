package com.example.myapplication

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ProductModel (
    @get:Exclude var document_id: String? = "",
    var product_image: String? = "",
    var product_name: String? = "",
    var product_category: String? = "",
    var product_price: Double? = 0.0,
    var product_count: Int? = 0)
