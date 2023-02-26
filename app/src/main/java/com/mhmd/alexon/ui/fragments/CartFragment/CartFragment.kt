package com.mhmd.alexon.ui.fragments.CartFragment

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.auth.FirebaseAuth
import com.meeting.letsmeeting.base.util.Resource
import com.mhmd.alexon.R
import com.mhmd.alexon.databinding.FragmentCartBinding
import com.mhmd.alexon.ui.adapters.CartAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private val cartMvvm: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartAdapter = CartAdapter()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRecyclerView()
        getCartProducts()
        onArrowBackClick()

    }

    private fun onArrowBackClick() {
        binding.arrowBack.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }
    }

    private fun setupCartRecyclerView() {
        binding.cartRec.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getCartProducts() {

        val myCartId = getEncryptedSharedPreference().getString("id", "")?.toLongOrNull()
        if (myCartId != null) {
            cartMvvm.getCartProducts(myCartId.toInt())
            cartMvvm.cartLiveData.observe(viewLifecycleOwner) { cartInformation ->
                cartInformation?.let { response ->
                    when (response) {
                        is Resource.Loading -> {
                            Log.d("testApp", "cart information is loading ")
                        }
                        is Resource.Success -> {
                            response.data?.let {
                                cartAdapter.differ.submitList(it.products)
                                binding.totalCartPrice.text = it.total.toString()
                                binding.tvDiscount.text = it.discountedTotal.toString()
                            }
                        }
                        is Resource.Error -> {
                            response.message?.let {
                                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                            }
                        }
                        is Resource.EmptyData -> {
                            Toast.makeText(requireContext(), "cart is empty", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> Unit
                    }
                }
            }
        } else {
            Log.e("testApp", "Unable to parse myCartId as a Long")
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