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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

private lateinit var binding: ActivitySepetBinding
private lateinit var auth: FirebaseAuth
private var payed_count = 0

class SepetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySepetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = SepetContentAdapter(Sepet.liste, context = this, binding)
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
            if(auth.currentUser != null){
                val db = Firebase.firestore
                payed_count = Sepet.liste.size
                for (productModel in Sepet.liste){
                    val productCategory = productModel.product_category
                    val productDocumentId = productModel.document_id
                    val document = db.collection("Products").document(productCategory!!).collection("Ürünler").document(
                        productDocumentId!!)

                    document.get()
                        .addOnSuccessListener { result->
                            val model = result.toObject<ProductModel>()
                            if (model != null) {
                                document.update("product_count",
                                    model.product_count?.minus(Sepet.liste[0].product_count!!)
                                )
                                payed_count -= 1
                                if (payed_count == 0){
                                    Sepet.clearList()
                                    Sepet.total_price = 0.0
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
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
            else{
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

    }
}