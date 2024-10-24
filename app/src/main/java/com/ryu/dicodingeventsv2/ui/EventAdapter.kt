package com.ryu.dicodingeventsv2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ryu.dicodingeventsv2.R
import com.ryu.dicodingeventsv2.data.ListEventsItem
import com.ryu.dicodingeventsv2.databinding.ItemEventsBinding

class EventAdapter(private val onItemClick: (ListEventsItem) -> Unit) : ListAdapter<ListEventsItem, EventAdapter.EventViewHolder>(EventDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener { onItemClick(event) }
    }

    class EventViewHolder(private val binding: ItemEventsBinding, private val onItemClick: (ListEventsItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.apply {
                tvEventName.text = event.name ?: "No Title"
                tvEventDate.text = event.beginTime ?: "No Category"
                Glide.with(ivEventLogo.context)
                    .load(event.mediaCover)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivEventLogo)
            }

            binding.root.setOnClickListener { onItemClick(event) }
        }
    }

    companion object {
        private val EventDiffCallback = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}