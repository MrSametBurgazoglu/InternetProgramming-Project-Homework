package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import android.graphics.BitmapFactory
import java.io.File


class ContentAdapter(private val courseModelArrayList: MutableList<ProductModel>, private val context: Context) :
    RecyclerView.Adapter<ContentAdapter.Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        // to inflate the layout for each item of recycler view.
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        // to set data to textview and imageview of each card layout
        val model = courseModelArrayList[position]
        holder.productName.text = model.product_name
        val price = "%s TL".format(model.product_price.toString())
        holder.productPrice.text = price
        val file = File(context.filesDir.absolutePath ,model.product_image.toString())
        if (file.exists()){
            val bmImg = BitmapFactory.decodeFile(file.absolutePath)
            holder.productImage.setImageBitmap(bmImg)
        }
        holder.cardview.setOnClickListener{
            val intent = Intent(context, BuyScreen::class.java).apply {
                putExtra("product_name", model.product_name)
                putExtra("product_price", model.product_price.toString())
                putExtra("product_image", model.product_image)
                putExtra("product_count", model.product_count)
                putExtra("document_id", model.document_id)
                putExtra("product_category", model.product_category)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        // this method is used for showing number
        // of card items in recycler view.
        return courseModelArrayList.size
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.ProductImage)
        val productName: TextView = itemView.findViewById(R.id.ProductName)
        val productPrice: TextView = itemView.findViewById(R.id.ProductPrice)
        val cardview: CardView = itemView.findViewById(R.id.CardView)
    }

}