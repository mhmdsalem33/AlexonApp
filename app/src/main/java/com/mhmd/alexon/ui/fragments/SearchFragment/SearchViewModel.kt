package com.mhmd.alexon.ui.fragments.SearchFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meeting.letsmeeting.base.util.Resource
import com.mhmd.alexon.domain.models.Product
import com.mhmd.alexon.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) :
    ViewModel() {
    private var _getSearchProductStateFlow =
        MutableStateFlow<Resource<List<Product>>>(Resource.Idle())
    var getSearchProductStateFlow = _getSearchProductStateFlow.asStateFlow()

    fun searchProduct(searchProduct: String) = viewModelScope.launch(Dispatchers.IO)
    {
         _getSearchProductStateFlow.emit(Resource.Loading())
        try {
            val response = searchRepository.searchToGetProducts(searchProduct)
            if (response.isSuccessful) {
                Log.d("testApp", response.body().toString())
                response.body()?.products?.let {
                      _getSearchProductStateFlow.emit(Resource.Success(it))
                } ?:  _getSearchProductStateFlow.emit(Resource.EmptyData())
            } else {
                Log.d("testApp", response.message().toString())
                Log.d("testApp", response.code().toString())
                _getSearchProductStateFlow.emit(Resource.Error(message =  response.message().toString()))
            }
        } catch (e: Exception) {
            Log.d("testApp", e.message.toString())
            _getSearchProductStateFlow.emit(Resource.Error(message =  e.message.toString()))
        }
    }
}