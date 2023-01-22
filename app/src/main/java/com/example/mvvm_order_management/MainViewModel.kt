package com.example.mvvm_order_management

import androidx.lifecycle.*
import com.example.model.LoadingState
import com.example.model.Order
import com.example.model.OrderDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel() {

    val loadingState = MutableLiveData<LoadingState>()
    val ordersLiveData = MediatorLiveData<List<Order>>()
    private val _queryLiveData = MutableLiveData<String>()
    private val _allOrdersLiveData = MutableLiveData<List<Order>>()
    private var _searchLiveData : LiveData<List<Order>>

    private var searchJob: Job? = null
    private val debouncePeriod = 500L

    init {
        _searchLiveData = Transformations.switchMap(_queryLiveData) {
            fetchOrdersByQuery(it)
        }

        ordersLiveData.addSource(_allOrdersLiveData) {
            ordersLiveData.value = it
        }
        ordersLiveData.addSource(_searchLiveData) {
            ordersLiveData.value = it
        }
    }

    fun onViewReady(){
        if (_allOrdersLiveData.value.isNullOrEmpty()){
            getAllOrders()
        }
    }

    private fun getAllOrders() {
        loadingState.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
          try {
                val order = OrderDataGenerator.getAllOrders()
              _allOrdersLiveData.postValue(order)
              loadingState.postValue(LoadingState.LOADED)
          }catch (e: Exception){
              loadingState.postValue(LoadingState.ERROR)
          }
        }
    }

     fun onSearchQuery(query: String){
        searchJob?.cancel()
        searchJob = viewModelScope.launch{
            delay(debouncePeriod)
            if (query.isEmpty()){
                getAllOrders()
            }else{
                _queryLiveData.postValue(query)
            }
        }
    }

    private fun fetchOrdersByQuery(query: String): LiveData<List<Order>> {
        val liveData = MutableLiveData<List<Order>>()
        loadingState.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val orders = OrderDataGenerator.searchOrders(query)
                liveData.postValue(orders)
                loadingState.postValue(LoadingState.LOADED)
            } catch (e: Exception) {
                loadingState.postValue(LoadingState.ERROR)
            }
        }
        return liveData
    }
}
