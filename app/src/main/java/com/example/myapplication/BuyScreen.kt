package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import com.example.myapplication.databinding.ActivityBuyScreenBinding

private lateinit var binding: ActivityBuyScreenBinding

class BuyScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val productImage:Int = intent.getIntExtra("product_image", 0)
        val productName:String = intent.getStringExtra("product_name").toString()
        val productPrice:String = intent.getStringExtra("product_price").toString()
        val productCode:String = intent.getStringExtra("product_code").toString()
        binding.productImage.setImageResource(productImage)
        binding.textView3.text = getString(R.string.product_code_string, productCode)
        binding.textView4.text = getString(R.string.product_name_string, productName)
        binding.textView5.text = getString(R.string.product_price_string, productPrice)

        binding.buyButton.setOnClickListener {
            val intent = Intent(this, SepetActivity::class.java)
            Sepet.clear_list()
            val count = binding.textView2.text.toString().toInt()
            val productModel = ProductModel(productImage, productName, productPrice.toInt(), productCode, count)
            Sepet.add_to_list(productModel)
            startActivity(intent)
        }

        binding.increaseButton.setOnClickListener{
            binding.textView2.text = (binding.textView2.text.toString().toInt() + 1).toString()
        }

        binding.decreaseButton.setOnClickListener{
            binding.textView2.text = (binding.textView2.text.toString().toInt() - 1).toString()
        }

        binding.stokButton.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
            startActivity(intent)
        }
    }
}