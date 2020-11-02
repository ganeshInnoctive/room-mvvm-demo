package com.example.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.ListItemBinding
import com.example.roomdemo.db.Subscriber

class SubscriberAdapter(
    private val subscribersList: List<Subscriber>,
    private val clickListener: (Subscriber) -> Unit
) :
    RecyclerView.Adapter<SubscriberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return SubscriberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriberViewHolder, position: Int) {
        holder.bind(subscribersList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return subscribersList.size
    }
}

class SubscriberViewHolder(private val binding: ListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        subscriber: Subscriber,
        clickListener: (Subscriber) -> Unit
    ) {
        binding.textViewName.text = subscriber.subscriberName
        binding.textViewEmail.text = subscriber.subscriberEmail

        binding.cardViewSubscriber.setOnClickListener {
            clickListener(subscriber)
        }
    }
}