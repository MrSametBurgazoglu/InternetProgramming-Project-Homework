package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityBuyScreenBinding
import android.graphics.BitmapFactory
import java.io.File
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityBuyScreenBinding
private lateinit var auth: FirebaseAuth


class BuyScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        val productImage:String = intent.getStringExtra("product_image").toString()
        val productName:String = intent.getStringExtra("product_name").toString()
        val productCategory:String = intent.getStringExtra("product_category").toString()
        val productPrice:String = intent.getStringExtra("product_price").toString()
        val productCount:Int = intent.getIntExtra("product_count", 0)
        val productDocumentID:String = intent.getStringExtra("document_id").toString()
        val file = File(filesDir.absolutePath , productImage)
        if (file.exists()){
            val bmImg = BitmapFactory.decodeFile(file.absolutePath)
            binding.productImage.setImageBitmap(bmImg)
        }
        binding.textView4.text = getString(R.string.product_name_string, productName)
        binding.textView5.text = getString(R.string.product_price_string, productPrice)

        binding.buyButton.setOnClickListener {
            val count = binding.textView2.text.toString().toInt()
            if( count > productCount){
                Toast.makeText(this, "Üzgünüz, istediğiniz üründen kalmadı..",Toast.LENGTH_LONG).show()
            }
            else{
                val productModel = ProductModel(productDocumentID, productImage, productName, productCategory, productPrice.toDouble(), count)
                if (!Sepet.is_in_list(productModel)){
                    Sepet.addToList(productModel)
                }
                else{
                    Toast.makeText(this, "Bu ürünü zaten sepete eklediniz", Toast.LENGTH_LONG).show()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.increaseButton.setOnClickListener{
            binding.textView2.text = (binding.textView2.text.toString().toInt() + 1).toString()
        }

        binding.decreaseButton.setOnClickListener{
            val value = binding.textView2.text.toString().toInt()
            if (value == 0){
                Toast.makeText(this, "henüz ürün eklemediniz", Toast.LENGTH_SHORT).show()
            }
            else{
                binding.textView2.text = (value - 1).toString()
            }
        }

        binding.stokButton.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
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
}