package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityChooseScreenBinding
import com.example.myapplication.ContentAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


private lateinit var binding: ActivityChooseScreenBinding
private var model_list = mutableListOf<ProductModel>()

class ChooseScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_choose_screen)
        binding = ActivityChooseScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val category = intent.getStringExtra("category")
        model_list.clear()

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (category != null){
            getModels(category)
            binding.recyclerView.adapter = ContentAdapter(model_list, context = this)
        }

        binding.stokButton.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
            startActivity(intent)
        }

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getModels(category:String){

        val db = Firebase.firestore
        val collection = db.collection("Products")
        collection.document(category).collection("Ürünler")
            .get()
            .addOnSuccessListener { result ->
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                for (document in result) {
                    Log.d("Info", "${document.id} => ${document.data}")
                    val model = document.toObject<ProductModel>()
                    model_list.add(model)
                }
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("Info", "Error getting documents.", exception)
            }


        val models = mutableListOf(
            ProductModel(R.drawable.ic_launcher_foreground, "Süt", "Süt ve Süt Ürünleri", 3, 1, 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Ayran","Süt ve Süt Ürünleri", 3, 2, 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Yoğurt", "Süt ve Süt Ürünleri",3, 3, 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Peynir", "Süt ve Süt Ürünleri",3, 4, 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Kaşar", "Süt ve Süt Ürünleri",3, 5, 0)
        )
    }
}