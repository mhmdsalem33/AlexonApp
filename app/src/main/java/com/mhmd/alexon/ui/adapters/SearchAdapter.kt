package com.mhmd.alexon.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mhmd.alexon.databinding.ItemSizeBinding
import com.mhmd.alexon.databinding.ItemStaggeredGridBinding
import com.mhmd.alexon.domain.models.Product


class SearchAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var onItemClick : ( (Product)-> Unit )

    private val diffUtil = object :DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
           return  oldItem == newItem
        }
    }
    var differ = AsyncListDiffer(this , diffUtil)

    companion object {
        private const val VIEW_TYPE_FIRST_ITEM = 0
        private const val VIEW_TYPE_STAGGERED_GRID_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FIRST_ITEM -> {
                val binding = ItemSizeBinding.inflate(LayoutInflater.from(parent.context))
                FullSizeViewHolder(binding)
            }
            else -> {
                val binding = ItemStaggeredGridBinding.inflate(LayoutInflater.from(parent.context))
                StaggeredGridViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = differ.currentList[position]

        when (holder) {
            is FullSizeViewHolder -> {
                holder.bind(differ.currentList.size -1 )
            }
            is StaggeredGridViewHolder -> {
                holder.bind(data)
                holder.itemView.setOnClickListener {
                    onItemClick.invoke(data)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_FIRST_ITEM
        } else {
            VIEW_TYPE_STAGGERED_GRID_ITEM
        }
    }
    class FullSizeViewHolder(val binding: ItemSizeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(countOfProducts : Int ) {
            binding.itemsSize.text =  StringBuffer().append(countOfProducts.toString()).append(" results")
        }
    }
    class StaggeredGridViewHolder(val binding : ItemStaggeredGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Product) {
            binding.apply {
                tvPrice.text       = data.price.toString()
                tvTitle.text       = data.title
                tvDescription.text = data.description
                tvRating.text      = data.rating.toString()
                Glide.with(binding.root).load(data.thumbnail).into(binding.productImage)
            }
        }
    }
}
