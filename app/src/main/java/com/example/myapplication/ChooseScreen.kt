package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityChooseScreenBinding
import com.example.myapplication.ContentAdapter


private lateinit var binding: ActivityChooseScreenBinding

class ChooseScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_choose_screen)
        binding = ActivityChooseScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = ContentAdapter(getModels(), context = this)

        binding.stokButton.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getModels(): MutableList<ProductModel> {

        val models = mutableListOf(
            ProductModel(R.drawable.ic_launcher_foreground, "Süt", 3, "0000000001", 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Ayran", 3, "0000000002", 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Yoğurt", 3, "0000000003", 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Peynir", 3, "0000000004", 0),
            ProductModel(R.drawable.ic_launcher_foreground, "Kaşar", 3, "0000000005", 0)
        )
        return models
    }
}