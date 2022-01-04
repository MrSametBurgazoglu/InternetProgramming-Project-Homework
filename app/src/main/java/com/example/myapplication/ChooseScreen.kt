package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityChooseScreenBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

private lateinit var binding: ActivityChooseScreenBinding
private lateinit var auth: FirebaseAuth
private var model_list = mutableListOf<ProductModel>()
private var image_to_download = 0

class ChooseScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityChooseScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val category = intent.getStringExtra("category")
        model_list.clear()
        binding.categoryTextView.text = category

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        if (category != null){
            getModels(category)
            binding.recyclerView.adapter = ContentAdapter(model_list, context = this)
        }

        binding.stokButton.setOnClickListener {
            if(auth.currentUser == null){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, StokActivity::class.java).apply {
                    putExtra("userid", auth.currentUser!!.uid)
                }
                startActivity(intent)
            }
        }

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.sepetButton.setOnClickListener {
            val intent = Intent(this, SepetActivity::class.java)
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
                for (document in result) {
                    Log.d("Info", "${document.id} => ${document.data}")
                    val model = document.toObject<ProductModel>()
                    model.document_id = document.id
                    model_list.add(model)
                }
                getImages()
            }
            .addOnFailureListener { exception ->
                Log.w("Info", "Error getting documents.", exception)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages(){

        val storage = Firebase.storage
        val storageRef = storage.reference
        image_to_download = model_list.size

        for (model in model_list){
            val file = File(filesDir.absolutePath, model.product_image.toString())
            if (!file.exists()){
                val pathReference = storageRef.child(model.product_category.toString() + "/" + model.product_image.toString())
                val b = file.createNewFile()
                if(b){
                    pathReference.getFile(file).addOnSuccessListener {
                        image_to_download -= 1
                        if (image_to_download == 0){
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                image_to_download -= 1
            }
            if (image_to_download == 0){
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        }

    }
}