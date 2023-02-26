package com.mhmd.alexon.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mhmd.alexon.databinding.MenuDrawerItemBinding
import com.mhmd.alexon.domain.models.DrawerMenuModel

class MenuDrawerAdapter() : RecyclerView.Adapter<MenuDrawerAdapter.ViewHolder>() {


    lateinit var onMenuItemClick : ((DrawerMenuModel) -> Unit )

    private val diffUtil = object  : DiffUtil.ItemCallback<DrawerMenuModel>()
    {
        override fun areItemsTheSame(oldItem: DrawerMenuModel, newItem: DrawerMenuModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DrawerMenuModel,
            newItem: DrawerMenuModel
        ): Boolean {
            return  oldItem == newItem
        }
    }
    var differ = AsyncListDiffer(this , diffUtil)

    class ViewHolder (val binding : MenuDrawerItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuDrawerAdapter.ViewHolder {

    return ViewHolder(MenuDrawerItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false ))
    }

    override fun onBindViewHolder(holder: MenuDrawerAdapter.ViewHolder, position: Int) {
        val data = differ.currentList[position]
        Glide.with(holder.itemView).load(data.icon).into(holder.binding.icon)
        holder.binding.name.text = data.name
        holder.itemView.setOnClickListener {
            onMenuItemClick.invoke(data)
        }
    }

    override fun getItemCount() = differ.currentList.size
}