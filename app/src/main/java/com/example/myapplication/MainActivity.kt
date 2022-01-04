package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityMainBinding
private lateinit var auth: FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission()

        binding.milkProductsButton.setOnClickListener {
            val intent = Intent(this, ChooseScreen::class.java).apply {
                putExtra("category", "Süt ve Süt Ürünleri")
            }
            startActivity(intent)
        }
        binding.breadButton.setOnClickListener {
            val intent = Intent(this, ChooseScreen::class.java).apply {
                    putExtra("category", "Ekmek ve Pasta")
                }
            startActivity(intent)
        }
        binding.chocolateButton.setOnClickListener {
            val intent = Intent(this, ChooseScreen::class.java).apply {
                putExtra("category", "Atıştırmalık")
            }
            startActivity(intent)
        }
        binding.cleanerButton.setOnClickListener {
            val intent = Intent(this, ChooseScreen::class.java).apply {
                putExtra("category", "Temizlik ve Bakım")
            }
            startActivity(intent)
        }
        binding.drinkButton.setOnClickListener {
            val intent = Intent(this, ChooseScreen::class.java).apply {
                putExtra("category", "İçecekler")
            }
            startActivity(intent)
        }
        binding.meatButton.setOnClickListener {
            val intent = Intent(this, ChooseScreen::class.java).apply {
                putExtra("category", "Et ve Et Ürünleri")
            }
            startActivity(intent)
        }
        binding.stokButton.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
            startActivity(intent)
        }

        binding.sepetButton.setOnClickListener {
            val intent = Intent(this, SepetActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Write Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Write Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}