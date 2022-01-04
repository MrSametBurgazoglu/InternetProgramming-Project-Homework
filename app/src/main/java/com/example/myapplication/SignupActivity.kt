package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.myapplication.databinding.ActivityMainBinding

import com.example.myapplication.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject

private lateinit var binding: ActivitySignupBinding

private lateinit var auth: FirebaseAuth

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.SignupButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val name = binding.editTextName.text.toString()
            val authority = binding.checkBox2.isChecked

            checkEmailExistsOrNot(email, password, name, authority)
        }

        binding.checkBox.setOnClickListener {
            binding.checkBox2.isChecked = false
        }

        binding.checkBox2.setOnClickListener{
            binding.checkBox.isChecked = false
        }

    }


    fun checkEmailExistsOrNot(email: String, password: String, name: String, stuff: Boolean) {

        auth
            .fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->

                if (task.result?.signInMethods?.size ?: 0 == 0) {
                    // email not existed
                    signup(email, password, name, stuff)
                } else {
                    // email existed
                    Toast.makeText(this, "Zaten üyesiniz giriş yapınız", Toast.LENGTH_LONG).show()

                }
            }.addOnFailureListener {
                    e -> e.printStackTrace()
            }

    }

    fun signup(email:String, password:String, name:String, stuff:Boolean){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user != null){
                        val db = Firebase.firestore
                        val users = db.collection("Users")
                        val userObject = User(name, email, stuff)
                        users.document(user.uid).set(userObject)
                    }

                    val intent = Intent(this, StokActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Hatalı bilgi girdiniz",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}