package com.mhmd.alexon.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mhmd.alexon.databinding.ProductImagesItemBinding

class ProductImagesAdapter() : RecyclerView.Adapter<ProductImagesAdapter.ViewHolder>() {

    private var imageList = ArrayList<String>()
    fun setImages(imageList: ArrayList<String>) {
        this.imageList = imageList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ProductImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ProductImagesItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val images = imageList[position]
        Glide.with(holder.itemView).load(images).into(holder.binding.productImage)
    }

    override fun getItemCount() = imageList.size

}