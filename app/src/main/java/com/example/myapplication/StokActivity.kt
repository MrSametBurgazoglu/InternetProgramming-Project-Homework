package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityStokBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.io.File

private lateinit var binding: ActivityStokBinding
private var stok_list = mutableListOf<ProductModel>()
private lateinit var dialog: Dialog
private lateinit var photo_file: File

class StokActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStokBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        stok_list.clear()
        get_categories()
        binding.recyclerView.adapter = StokContentAdapter(stok_list, context = this)
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.saveButton.setOnClickListener {
            show_authority_Dialog()
        }

        binding.AddProductButton.setOnClickListener {
            show_add_Dialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 13 && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(photo_file.absolutePath)
            val product_image_button = dialog.findViewById(R.id.ProductImageButton) as ImageButton
            product_image_button.setImageBitmap(takenPhoto)
        }else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun save_stok(){
        val db = Firebase.firestore
        for(child_count in 0..stok_list.size){
            val view = binding.recyclerView.findViewHolderForAdapterPosition(child_count)
            val productCountEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductCount)
            val productPriceEdittext = view?.itemView?.findViewById<EditText>(R.id.ProductPrice)
            if (productCountEdittext != null && productPriceEdittext != null) {
                if (productCountEdittext.text.toString().toInt() != stok_list[child_count].product_count || productPriceEdittext.text.toString().toInt() != stok_list[child_count].product_price){
                    Toast.makeText(this, "heyyo", Toast.LENGTH_SHORT).show()
                    val document = db.collection("Products").document(stok_list[child_count].product_category!!).collection("Ürünler").document(
                        stok_list[child_count].document_id!!)
                    document.update("product_price", productPriceEdittext.text.toString().toInt())
                    document.update("product_count", productCountEdittext.text.toString().toInt())
                    Toast.makeText(this, productPriceEdittext.text.toString() + "==" + stok_list[child_count].product_price.toString(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(this, productCountEdittext.text.toString() + "==" + stok_list[child_count].product_count.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun get_categories(){
        val db = Firebase.firestore
        val categories = arrayListOf<String>()
        db.collection("Products")
            .get()
            .addOnSuccessListener { documents ->
                Toast.makeText(this, "Success on getting categories", Toast.LENGTH_LONG).show()
                for (document in documents) {
                    categories.add(document.id)
                }
                get_all_products(categories)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun get_all_products(categories: ArrayList<String>){

        val db = Firebase.firestore
        val collection = db.collection("Products")
        for (category in categories) {
            collection.document(category).collection("Ürünler")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("Info", "${document.id} => ${document.data}")
                        val model = document.toObject<ProductModel>()
                        model.document_id = document.id
                        stok_list.add(model)
                    }
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("Info", "Error getting documents.", exception)
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun show_authority_Dialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.setCancelable(false)
        dialog.setContentView(R.layout.authority_dialog_layout)
        val yesBtn = dialog.findViewById(R.id.yes_button) as Button
        val noBtn = dialog.findViewById(R.id.no_button) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            save_stok()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    private fun show_add_Dialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        //dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_product_dialog_layout)
        dialog.findViewById<Spinner>(R.id.spinner)
        val product_name_edit_text = dialog.findViewById(R.id.ProductNameEditText) as EditText
        val product_count_edit_text = dialog.findViewById(R.id.ProductCountEditText) as EditText
        val product_price_edit_text = dialog.findViewById(R.id.ProductPriceEditText) as EditText
        val product_category_spinner = dialog.findViewById(R.id.spinner) as Spinner
        val product_image_button = dialog.findViewById(R.id.ProductImageButton) as ImageButton
        val add_button = dialog.findViewById(R.id.AddButton) as Button
        val no_button = dialog.findViewById(R.id.no_button) as Button

        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            product_category_spinner.adapter = adapter
        }

        add_button.setOnClickListener {
            dialog.dismiss()
            val model = ProductModel(
                product_name_edit_text.text.toString(),
                0,//product_image_int
                product_name_edit_text.text.toString(),
                product_category_spinner.selectedItem.toString(),
                product_price_edit_text.text.toString().toInt(),
                product_count_edit_text.text.toString().toInt(),
            )
            val db = Firebase.firestore
            val document = db.collection("Products").document(model.product_category!!).collection("Ürünler").document(
                model.document_id!!).set(model)
            //save_stok()
        }
        no_button.setOnClickListener { dialog.dismiss() }
        product_image_button.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photo_file = getPhotoFile("photo.jpg")
            val providerFile = FileProvider.getUriForFile(this,"com.example.androidcamera.fileprovider", photo_file)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
        }
        dialog.show()

    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    private fun add_product_to_database(product:ProductModel){

    }
}