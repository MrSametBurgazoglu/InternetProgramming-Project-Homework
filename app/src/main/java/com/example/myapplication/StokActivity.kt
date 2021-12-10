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
private var image_to_download = 0

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
            //show_authority_Dialog()
            save_stok()
        }

        binding.AddProductButton.setOnClickListener {
            show_add_Dialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 13 && resultCode == Activity.RESULT_OK){
            val takenPhoto = BitmapFactory.decodeFile(photo_file.absolutePath)
            //val b = BitmapFactory.decodeByteArray(takenPhoto, 0, takenPhoto.length)
            val resized_image = Bitmap.createScaledBitmap(takenPhoto, 128, 128, false)
            val product_image_button = dialog.findViewById(R.id.ProductImageButton) as ImageButton
            product_image_button.setImageBitmap(resized_image)
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
                    getImages()
                }
                .addOnFailureListener { exception ->
                    Log.w("Info", "Error getting documents.", exception)
                    Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                }
        }
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
                product_name_edit_text.text.toString() + ".jpg",//product_image
                product_name_edit_text.text.toString(),
                product_category_spinner.selectedItem.toString(),
                product_price_edit_text.text.toString().toInt(),
                product_count_edit_text.text.toString().toInt(),
            )
            addProductToDatabase(model)
            product_image_button.invalidate()
            val drawable = product_image_button.drawable
            val bitmap = drawable.toBitmap()
            val resized_image = Bitmap.createScaledBitmap(bitmap, 64, 64, false)
            val baos = ByteArrayOutputStream()
            resized_image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val storageRef = Firebase.storage.reference
            val fileRef = storageRef.child(model.product_category.toString()+"/"+model.product_image.toString())
            val uploadTask = fileRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Ürün resmini veritabanına göndermede sorun yaşandı", Toast.LENGTH_SHORT).show()
            }
        }
        no_button.setOnClickListener { dialog.dismiss() }
        product_image_button.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photo_file = getPhotoFile("photo.jpg")
            val providerFile = FileProvider.getUriForFile(
                Objects.requireNonNull(this),
                BuildConfig.APPLICATION_ID + ".fileprovider", photo_file);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
            startActivityForResult(takePhotoIntent, 13)
        }
        dialog.show()

    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    private fun addProductToDatabase(model:ProductModel){
        val db = Firebase.firestore
        db.collection("Products").document(model.product_category!!).collection("Ürünler").document(
            model.document_id!!).set(model)

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages(){

        val storage = Firebase.storage
        val storage_ref = storage.reference
        image_to_download = stok_list.size

        for (model in stok_list){
            val file = File(filesDir.absolutePath, model.product_image.toString())
            if (!file.exists()){
                val pathReference = storage_ref.child(model.product_category.toString() + "/" + model.product_image.toString())
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
            }
        }

    }
}