package com.mhmd.alexon.ui.fragments.IntroFragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.mhmd.alexon.R
import com.mhmd.alexon.databinding.FragmentIntroBinding
import com.mhmd.alexon.helper.ZoomOutPageTransformer
import com.mhmd.alexon.ui.adapters.AppIntroViewPager2Adapter


class IntroFragment : Fragment() {

    private lateinit var binding: FragmentIntroBinding
    var isButtonClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusBar()
        initViewPager()
        initIndicator()
        onButtonNextClick()
        onBackPressed()


    }


    private fun statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.let { activity?.window?.setStatusBarColor(it.getColor(R.color.white)) }
        }
    }

    private fun initViewPager() {
        binding.viewPager2.adapter = AppIntroViewPager2Adapter()
        binding.viewPager2.setPageTransformer(ZoomOutPageTransformer())
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.pageIndicatorView.selection = position
            }
        })
    }

    private fun initIndicator() {
        binding.pageIndicatorView.count = AppIntroViewPager2Adapter().itemCount
        binding.pageIndicatorView.selection = binding.viewPager2.currentItem
    }

    private fun onButtonNextClick() {
        binding.btnNext.setOnClickListener {
            val current = (binding.viewPager2.currentItem) + 1
            binding.viewPager2.currentItem = current

            if (current == 3) {
                if (!isButtonClicked) {
                    isButtonClicked = true
                    findNavController().navigate(R.id.action_introFragment_to_loginFragment)
                } else {
                    findNavController().navigate(R.id.action_introFragment_to_loginFragment)

                }
            }
        }
    }

    private fun onBackPressed() {

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}