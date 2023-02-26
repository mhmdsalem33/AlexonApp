package com.mhmd.alexon.ui.fragments.ProductDetailsFragment

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mhmd.alexon.domain.models.AddToCartList
import com.mhmd.alexon.domain.repository.ProductDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productDetailsRepository: ProductDetailsRepository,
    private val application: Application
    ) :
    ViewModel() {

    fun addProductToMyCart(myCartNumber: Int, product: AddToCartList) =
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val response = productDetailsRepository.addProductToMyCart(myCartNumber, product)
                if (response.isSuccessful) {
                    Log.d("testApp", response.body().toString())
                    Log.d("testApp" , "Success to add product to cart")
                    withContext(Dispatchers.Main)
                    {
                        Toast.makeText(application, "Success to add product to cart", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("testApp", response.message().toString())
                    Log.d("testApp", response.code().toString())
                }

            } catch (e: Exception) {
                Log.d("testApp", e.message.toString())
            }
        }

}