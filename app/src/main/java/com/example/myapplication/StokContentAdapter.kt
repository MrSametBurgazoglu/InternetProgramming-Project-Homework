package com.example.myapplication

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class StokContentAdapter(private val courseModelArrayList: MutableList<ProductModel>, private val context: Context) :
    RecyclerView.Adapter<StokContentAdapter.Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_stok_layout, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val model = courseModelArrayList[position]
        holder.productName.text = model.product_name
        holder.productPrice.setText(model.product_price.toString())
        holder.productCount.setText(model.product_count.toString())
        val file = File(context.filesDir.absolutePath ,model.product_image.toString())
        if (file.exists()){
            val bmImg = BitmapFactory.decodeFile(file.absolutePath)
            holder.productImage.setImageBitmap(bmImg)
        }
    }

    override fun getItemCount(): Int {
        return courseModelArrayList.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.ProductImage)
        val productName: TextView = itemView.findViewById(R.id.ProductName)
        val productPrice: EditText = itemView.findViewById(R.id.ProductPrice)
        val productCount: EditText = itemView.findViewById(R.id.ProductCount)
    }

}