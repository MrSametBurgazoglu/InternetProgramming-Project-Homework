package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = Firebase.firestore
        /*
        val data = hashMapOf(
            "ProductCategory" to "Süt ve Süt Ürünleri",
            "ProductCount" to "100",
            "ProductID" to 1000000000,
            "ProductName" to "Mis Uht Süt Yarım Yağlı 1 Lt",
            "ProductPrice" to 4.9,
        )
        val document = db.collection("Products").document("Süt")
        document.set(data)
            .addOnSuccessListener { Log.d("Info", "DocumentSnapshot added successfully") }
            .addOnFailureListener { e -> Log.w("Info", "Error adding document", e)
            }
*/

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
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
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