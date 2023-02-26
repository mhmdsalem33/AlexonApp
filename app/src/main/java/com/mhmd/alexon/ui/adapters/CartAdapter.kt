package com.mhmd.alexon.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mhmd.alexon.databinding.CartRowBinding
import com.mhmd.alexon.domain.models.CartProducts

class CartAdapter() : RecyclerView.Adapter<CartAdapter.ViewHolder>() {


    private val diffUtil = object : DiffUtil.ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }

    var differ = AsyncListDiffer(this, diffUtil)

    class ViewHolder(val binding: CartRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CartRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cart = differ.currentList[position]

        //Attention i don't found any image for products on cart
        //https://dummyjson.com/carts/1 every product don't have product image
       holder.binding.apply {
                nameCart.text = cart.title
                countCart.text = StringBuffer().append(cart.quantity.toString()).append(" quantity")
                totalPrice.text = cart.price.toString()

       }

    }

    override fun getItemCount() = differ.currentList.size

}