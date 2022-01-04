package com.example.myapplication

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivitySepetBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

private lateinit var binding: ActivitySepetBinding
private lateinit var auth: FirebaseAuth
private var payed_count = 0
private lateinit var dialog: Dialog

class SepetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySepetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = SepetContentAdapter(Sepet.liste, context = this, binding)
        Toast.makeText(this, Sepet.total_price.toString(), Toast.LENGTH_SHORT).show()
        binding.totalPrice.text = "Toplam Tutar %.2f".format(Sepet.total_price)

        binding.stokButton.setOnClickListener {
            val intent = Intent(this, StokActivity::class.java)
            startActivity(intent)
        }

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.PayButton.setOnClickListener {
            if(auth.currentUser != null){
                payProducts()
            }
            else{
                showLoginDialog()
            }
        }

    }


    private fun payProducts(){
        val db = Firebase.firestore
        payed_count = Sepet.liste.size
        for (productModel in Sepet.liste){
            val productCategory = productModel.product_category
            val productDocumentId = productModel.document_id
            val document = db.collection("Products").document(productCategory!!).collection("Ürünler").document(
                productDocumentId!!)

            document.get()
                .addOnSuccessListener { result->
                    val model = result.toObject<ProductModel>()
                    if (model != null) {
                        document.update("product_count",
                            model.product_count?.minus(Sepet.liste[0].product_count!!)
                        )
                        payed_count -= 1
                        if (payed_count == 0){
                            Sepet.clearList()
                            Sepet.total_price = 0.0
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    else{
                        Toast.makeText(this, "Ürün bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Info", "Error getting documents.", exception)
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun showLoginDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_login)

        val signinButton = dialog.findViewById(R.id.SigninButton) as Button
        val signupButton = dialog.findViewById(R.id.SignupButton) as Button
        val editTextEmail = dialog.findViewById(R.id.editTextEmail) as EditText
        val editTextPassword = dialog.findViewById(R.id.editTextPassword) as EditText

        signinButton.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            if(email.isNotBlank() && password.isNotBlank()){
                signIn(email, password)
            }
            else{
                Toast.makeText(this, "Hatalı bilgi girdiniz", Toast.LENGTH_LONG).show()
            }
        }

        signupButton.setOnClickListener {
            dialog.dismiss()
            showSignupDialog()
        }
        dialog.show()

    }

    private fun signIn(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("hi", "signInWithEmail:success")
                    //val user = auth.currentUser
                    dialog.dismiss()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("hi", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showSignupDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.setCancelable(false)
        dialog.setContentView(R.layout.activity_signup)


        val signupButton = dialog.findViewById(R.id.SignupButton) as Button
        val editTextEmail = dialog.findViewById(R.id.editTextEmail) as EditText
        val editTextPassword = dialog.findViewById(R.id.editTextPassword) as EditText
        val editTextName = dialog.findViewById(R.id.editTextName) as EditText
        val checkBox = dialog.findViewById(R.id.checkBox) as CheckBox
        val checkBox2 = dialog.findViewById(R.id.checkBox2) as CheckBox


        signupButton.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val name = editTextName.text.toString()
            val authority = checkBox2.isChecked
            val authority2 = checkBox.isChecked

            if(email.isNotBlank() && password.isNotBlank() && name.isNotBlank() && ((authority && !authority2) || (!authority && authority2))){
                checkEmailExistsOrNot(email, password, name, authority)
            }
            else{
                Toast.makeText(this, (authority || !authority2).toString(), Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()

        }

        checkBox.setOnClickListener {
            checkBox2.isChecked = false
        }

        checkBox2.setOnClickListener{
            checkBox.isChecked = false
        }

        dialog.show()

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

                    auth.signOut()
                    showLoginDialog()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Hatalı bilgi girdiniz",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


}