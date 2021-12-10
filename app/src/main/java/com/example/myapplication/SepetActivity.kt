package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivitySepetBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivitySepetBinding

class SepetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySepetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = SepetContentAdapter(Sepet.liste, context = this)
        binding.totalPrice.text = getString(R.string.total_price_string, Sepet.total_price.toString())

        binding.stokButton.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
            startActivity(intent)
        }

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.PayButton.setOnClickListener {
            val db = Firebase.firestore
            val productCategory = Sepet.liste[0].product_category
            val productDocumentId = Sepet.liste[0].document_id
            val document = db.collection("Products").document(productCategory!!).collection("Ürünler").document(
                productDocumentId!!)

            document.get()
                .addOnSuccessListener { result->
                    //stokta bulunduğundan fazla ürün almayı önceden kontrol ettiğimiz içi burda kontrol etmeye gerek yok
                    val model = result.toObject<ProductModel>()
                    if (model != null) {
                        document.update("product_count",
                            model.product_count?.minus(Sepet.liste[0].product_count!!)
                        )
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Ürün bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Info", "Error getting documents.", exception)
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
        }

    }
}