package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView


class SepetContentAdapter(private val courseModelArrayList: MutableList<ProductModel>, private val context: Context) :
    RecyclerView.Adapter<SepetContentAdapter.Viewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        // to inflate the layout for each item of recycler view.
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.card_sepet_layout, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        // to set data to textview and imageview of each card layout
        val model = courseModelArrayList[position]
        holder.productName.text = model.product_name
        //val pricetext = Resources.getSystem().getString(R.string.product_price_string, model.product_price.toString())
        holder.productPrice.text = context.getString(R.string.product_price_string, model.product_price.toString())
        //holder.productImage.setImageResource(Stok.product_images[model.product_image!!])
        holder.productCount.text = context.getString(R.string.product_count_string, model.product_count.toString())
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
        val productCount: TextView = itemView.findViewById(R.id.ProductCount)
        val cardview: CardView = itemView.findViewById(R.id.CardView)
    }

}