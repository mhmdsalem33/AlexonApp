package com.mhmd.alexon.ui.fragments.CartFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meeting.letsmeeting.base.util.Resource
import com.mhmd.alexon.domain.models.CartResponse
import com.mhmd.alexon.domain.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val cartRepository: CartRepository) : ViewModel() {

    private var _cartLiveData = MutableLiveData<Resource<CartResponse>>()
    var cartLiveData: LiveData<Resource<CartResponse>> = _cartLiveData

    fun getCartProducts(cartNumber: Int) = viewModelScope.launch(Dispatchers.IO) {
                  _cartLiveData.postValue(Resource.Loading())
        try {
            val response = cartRepository.getCartProducts(cartNumber)
            if (response.isSuccessful) {
                // Log.d("testApp" , response.body().toString())
                response.body()?.let {
                    _cartLiveData.postValue(Resource.Success(it))
                } ?: _cartLiveData.postValue(Resource.EmptyData())
            } else {
                Log.d("testApp", response.message().toString())
                Log.d("testApp", response.code().toString())
                _cartLiveData.postValue(Resource.Error(message = response.message().toString()))
            }
        } catch (e: Exception) {
            Log.d("testApp", e.message.toString())
            _cartLiveData.postValue(Resource.Error(message = e.message.toString()))
        }
    }
}