package com.mhmd.alexon.ui.fragments.ProductDetailsFragment

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.mhmd.alexon.R
import com.mhmd.alexon.databinding.FragmentProductDetailsBinding
import com.mhmd.alexon.domain.models.AddToCartList
import com.mhmd.alexon.domain.models.AddToCartProduct
import com.mhmd.alexon.domain.models.Product
import com.mhmd.alexon.ui.adapters.ProductImagesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private val productMvvm: ProductDetailsViewModel by viewModels()
    private var productInformation: Product? = null
    private lateinit var productImagesAdapter: ProductImagesAdapter
    var count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productImagesAdapter = ProductImagesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusBar()
        getProductDataByBundle()
        setupProductImagesRecylerView()
        setProductInformationInViews()
        onMinusClick()
        onPlusClick()
        onArrowBackClick()
        onBtnAddToCartClick()

    }


    private fun statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.let { activity?.window?.setStatusBarColor(it.getColor(R.color.white)) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onBtnAddToCartClick() {
        binding.addToCart.setOnClickListener {
            if (productInformation?.id != null) {
                val myCartId = getEncryptedSharedPreference().getString("id", "")?.toLongOrNull()
                if (myCartId != null) {
                    val product =
                        AddToCartList(listOf(AddToCartProduct(productInformation!!.id, count)))
                    productMvvm.addProductToMyCart(myCartId.toInt(), product)
                }
            }
        }
    }

    private fun onArrowBackClick() {
        binding.arrowBack.arrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_productDetailsFragment_to_searchFragment)
        }
    }

    private fun onPlusClick() {
        binding.add.setOnClickListener {
            count++
            val price = productInformation?.price?.times(count)
            binding.apply {
                tvPrice.text = price.toString()
                tvCount.text = count.toString()
            }
        }
    }

    private fun onMinusClick() {
        binding.minus.setOnClickListener {
            if (count > 1) {
                count--
                val price = productInformation?.price?.times(count)
                binding.apply {
                    tvPrice.text = price.toString()
                    tvCount.text = count.toString()
                }
            }
        }
    }

    private fun getProductDataByBundle() {
        val argument = arguments
        if (argument != null) {
            val product = arguments?.getParcelable<Product>("product")
            if (product != null) {
                productInformation = product
            }
        }
    }

    private fun setupProductImagesRecylerView() {
        binding.imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = productImagesAdapter
        }
    }

    private fun setProductInformationInViews() {
        productImagesAdapter.setImages(imageList = productInformation?.images as ArrayList<String>)
        binding.apply {
            productName.text = productInformation?.title
            tvDescription.text = productInformation?.description
            tvRating.text = productInformation?.rating.toString()
            tvPrice.text = productInformation?.price.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getEncryptedSharedPreference(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "my_encrypted_shared_preferences",
            masterKeyAlias,
            requireContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}