package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityStokBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

private lateinit var binding: ActivityStokBinding
private var stok_list = mutableListOf<ProductModel>()
private lateinit var dialog: Dialog
private lateinit var photo_file: File
private lateinit var userid:String
private var image_to_download = 0
private var categories_to_download = 0
private lateinit var auth: FirebaseAuth



class StokActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStokBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser == null){
            showLoginDialog()
        }
        Toast.makeText(this, auth.currentUser.toString(), Toast.LENGTH_SHORT).show()


        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        stok_list.clear()
        getCategories()
        binding.recyclerView.adapter = StokContentAdapter(stok_list, context = this)

        userid = intent.getStringExtra("userid").toString()

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.saveButton.setOnClickListener {
            //show_authority_Dialog()
            saveStok()
        }

        binding.AddProductButton.setOnClickListener {
            showAddDialog()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 13 && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(photo_file.absolutePath)
            val resizedImage = Bitmap.createScaledBitmap(takenPhoto, 128, 128, false)
            val productImageButton = dialog.findViewById(R.id.ProductImageButton) as ImageButton
            productImageButton.setImageBitmap(resizedImage)
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun saveStok(){
        val db = Firebase.firestore
        for(child_count in 0..stok_list.size){
            val view = binding.recyclerView.findViewHolderForAdapterPosition(child_count)
            val productCountEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductCount)
            val productPriceEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductPrice)
            if (productCountEdittext != null && productPriceEdittext != null) {
                if (productCountEdittext.text.toString().toInt() != stok_list[child_count].product_count || productPriceEdittext.text.toString().toDouble() != stok_list[child_count].product_price){
                    val document = db.collection("Products").document(stok_list[child_count].product_category!!).collection("Ürünler").document(
                        stok_list[child_count].document_id!!)
                    document.update("product_price", productPriceEdittext.text.toString().toDouble())
                    document.update("product_count", productCountEdittext.text.toString().toDouble())
                }
            }
        }

    }

    private fun getCategories(){
        val db = Firebase.firestore
        val categories = arrayListOf<String>()
        db.collection("Products")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    categories.add(document.id)
                }
                getAllProducts(categories)
            }
    }

    private fun getAllProducts(categories: ArrayList<String>){
        categories_to_download = categories.size
        for (category in categories) {
            getProductsByCategory(category)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getProductsByCategory(category:String){
        val db = Firebase.firestore
        val collection = db.collection("Products")
        collection.document(category).collection("Ürünler")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Info", "${document.id} => ${document.data}")
                    val model = document.toObject<ProductModel>()
                    model.document_id = document.id
                    stok_list.add(model)
                }
                categories_to_download -= 1
                if(categories_to_download == 0){
                    stok_list.sortBy { it.document_id }
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                    getImages()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Info", "Error getting documents.", exception)
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
            }
    }


    private fun setVisibility(){
        if(auth.currentUser == null){
            binding.saveButton.visibility = View.INVISIBLE
            binding.AddProductButton.visibility = View.INVISIBLE
            for(child_count in 0..stok_list.size){
                val view = binding.recyclerView.findViewHolderForAdapterPosition(child_count)
                val productCountEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductCount)
                val productPriceEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductPrice)
                if (productCountEdittext != null && productPriceEdittext != null) {
                    productCountEdittext.isEnabled = false
                    productPriceEdittext.isEnabled = false
                }
            }
        }
        else{
            val db = Firebase.firestore
            userid = auth.currentUser!!.uid
            val document = db.collection("Users").document(userid)

            document.get()
                .addOnSuccessListener { result->
                    val model = result.toObject<User>()
                    if (model != null) {
                        if (!model.stuff){
                            binding.saveButton.visibility = View.INVISIBLE
                            binding.AddProductButton.visibility = View.INVISIBLE
                            for(child_count in 0..stok_list.size){
                                val view = binding.recyclerView.findViewHolderForAdapterPosition(child_count)
                                val productCountEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductCount)
                                val productPriceEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductPrice)
                                if (productCountEdittext != null && productPriceEdittext != null) {
                                    productCountEdittext.isEnabled = false
                                    productPriceEdittext.isEnabled = false
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(this, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Info", "Error getting documents.", exception)
                    Toast.makeText(this, exception.toString(), Toast.LENGTH_LONG).show()
                }
        }

    }


    private fun showAddDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_product_dialog_layout)
        dialog.findViewById<Spinner>(R.id.spinner)
        val productNameEditText = dialog.findViewById(R.id.ProductNameEditText) as EditText
        val productCountEditText = dialog.findViewById(R.id.ProductCountEditText) as EditText
        val productPriceEditText = dialog.findViewById(R.id.ProductPriceEditText) as EditText
        val productCategorySpinner = dialog.findViewById(R.id.spinner) as Spinner
        val productImageButton = dialog.findViewById(R.id.ProductImageButton) as ImageButton
        val addButton = dialog.findViewById(R.id.AddButton) as Button
        val noButton = dialog.findViewById(R.id.no_button) as Button

        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            productCategorySpinner.adapter = adapter
        }

        addButton.setOnClickListener {
            dialog.dismiss()
            if(productNameEditText.text.toString().isBlank() || productPriceEditText.text.toString().isBlank() || productCountEditText.text.toString().isBlank()){
                Toast.makeText(this, "Girilen veriler hatalı", Toast.LENGTH_LONG).show()
            }
            else{
                val model = ProductModel(
                    productNameEditText.text.toString(),
                    productNameEditText.text.toString() + ".jpg",//product_image
                    productNameEditText.text.toString(),
                    productCategorySpinner.selectedItem.toString(),
                    productPriceEditText.text.toString().toDouble(),
                    productCountEditText.text.toString().toInt(),
                )
                addProductToDatabase(model)
                productImageButton.invalidate()
                val drawable = productImageButton.drawable
                val bitmap = drawable.toBitmap()
                val resizedImage = Bitmap.createScaledBitmap(bitmap, 64, 64, false)
                val baos = ByteArrayOutputStream()
                resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val storageRef = Firebase.storage.reference
                val fileRef = storageRef.child(model.product_category.toString()+"/"+model.product_image.toString())
                val uploadTask = fileRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    Toast.makeText(this, "Ürün resmini veritabanına göndermede sorun yaşandı", Toast.LENGTH_SHORT).show()
                }
            }
        }
        noButton.setOnClickListener { dialog.dismiss() }
        productImageButton.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            photo_file = File.createTempFile("photo.jpg", ".jpg", directoryStorage)
            val providerFile = FileProvider.getUriForFile(Objects.requireNonNull(this),  BuildConfig.APPLICATION_ID + ".fileprovider", photo_file)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
            startActivityForResult(takePhotoIntent, 13)
        }
        dialog.show()

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
                    setVisibility()
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



    private fun addProductToDatabase(model:ProductModel){
        val db = Firebase.firestore
        db.collection("Products").document(model.product_category!!).collection("Ürünler").document(
            model.document_id!!).set(model)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages(){

        val storage = Firebase.storage
        val storageRef = storage.reference
        image_to_download = stok_list.size

        for (model in stok_list){
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
                setVisibility()
            }
        }

    }
}