package com.example.mvvm_order_management

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.model.LoadingState
import com.example.mvvm_order_management.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val adapter = OrderAdapter(arrayListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        initializeUI()
        initializeObservers()

        viewModel.onViewReady()
    }

    private fun initializeObservers() {
        viewModel.ordersLiveData.observe(this, Observer {
            adapter.updateOrders(it)
        })

        viewModel.loadingState.observe(this, Observer {
            onLoadingStateChanged(it)
        })

    }


    private fun onLoadingStateChanged(state: LoadingState) {
        binding.searchET.visibility = if (state == LoadingState.LOADED) View.VISIBLE else View.GONE
        binding.recyclerOrders.visibility = if (state == LoadingState.LOADED) View.VISIBLE else View.GONE
        binding.errorET.visibility = if (state == LoadingState.ERROR) View.VISIBLE else View.GONE
        binding.loading.visibility = if (state == LoadingState.LOADING) View.VISIBLE else View.GONE
    }

    private fun initializeUI() {
        binding.recyclerOrders.adapter = adapter
        binding.recyclerOrders.layoutManager = LinearLayoutManager(this)

        binding.searchET.doOnTextChanged { text, start, before, count ->
            viewModel.onSearchQuery(text.toString())
        }
    }
}