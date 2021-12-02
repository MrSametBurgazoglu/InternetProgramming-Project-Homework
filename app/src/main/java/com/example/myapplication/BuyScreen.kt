package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import com.example.myapplication.databinding.ActivityBuyScreenBinding

private lateinit var binding: ActivityBuyScreenBinding

class BuyScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productImage:Int = intent.getIntExtra("product_image", 0)
        val productName:String = intent.getStringExtra("product_name").toString()
        val productCategory:String = intent.getStringExtra("product_category").toString()
        val productPrice:String = intent.getStringExtra("product_price").toString()
        val productCode:Long = intent.getLongExtra("product_code", 0)
        binding.productImage.setImageResource(Stok.product_images[productImage])
        binding.textView3.text = getString(R.string.product_code_string, productCode.toString())
        binding.textView4.text = getString(R.string.product_name_string, productName)
        binding.textView5.text = getString(R.string.product_price_string, productPrice)

        binding.buyButton.setOnClickListener {
            val intent = Intent(this, SepetActivity::class.java)
            Sepet.clear_list()
            val count = binding.textView2.text.toString().toInt()
            val productModel = ProductModel(productImage, productName, productCategory, productPrice.toInt(), productCode, count)
            Sepet.add_to_list(productModel)
            startActivity(intent)
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
            startActivity(intent)
        }

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}