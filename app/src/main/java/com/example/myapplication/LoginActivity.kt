package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth

private lateinit var auth: FirebaseAuth
private lateinit var binding: ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.SigninButton.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            signIn(email, password)
        }

        binding.SignupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("hi", "signInWithEmail:success")
                    //val user = auth.currentUser
                    val intent = Intent(this, StokActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("hi", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}