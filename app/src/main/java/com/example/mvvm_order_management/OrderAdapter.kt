package com.example.mvvm_order_management

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.model.Order
import com.example.mvvm_order_management.databinding.OrderItemBinding


class OrderAdapter(private val orders: ArrayList<Order>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount() = orders.size

    inner class OrderViewHolder(val binding: OrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.orderId.text = order.id.toString()
            binding.product.text = order.product.toString()
            binding.user.text = order.user.toString()
        }
    }
}