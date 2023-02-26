package com.mhmd.alexon.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mhmd.alexon.R
import com.mhmd.alexon.databinding.IntroRowBinding

class AppIntroViewPager2Adapter : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            IntroRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.itemView.run {
        with(holder) {
            if (position == 0) {
                binding.browse.text = context.getString(R.string.browse)
                binding.textDiscount.text = context.getString(R.string.disscount)
                binding.introImage.setImageResource(R.drawable.intro_one)
            }
            if (position == 1) {
                binding.browse.text = context.getString(R.string.even)
                binding.textDiscount.text = context.getString(R.string.disscount)
                binding.introImage.setImageResource(R.drawable.intro_two)
            }
            if (position == 2) {
                binding.browse.text = context.getString(R.string.pickup)
                binding.textDiscount.text = context.getString(R.string.disscount)
                binding.introImage.setImageResource(R.drawable.intro_three)
            }
        }
    }
}
class ViewHolder(val binding: IntroRowBinding) : RecyclerView.ViewHolder(binding.root)

