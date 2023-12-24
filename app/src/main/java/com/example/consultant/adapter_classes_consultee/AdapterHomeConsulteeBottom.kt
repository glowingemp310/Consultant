package com.example.consultant.adapter_classes_consultee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.OnItemClick
import com.example.consultant.databinding.ItemConsulteeBottomBinding
import com.example.consultant.databinding.ItemConsulteeTopBinding
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeBottom
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop

class AdapterHomeConsulteeBottom(var showData: MutableList<ModelHomeConsulteeBottom>,var listener: OnItemClick):RecyclerView.Adapter<AdapterHomeConsulteeBottom.ViewHolder>() {

    lateinit var binding: ItemConsulteeBottomBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHomeConsulteeBottom.ViewHolder {
        binding = ItemConsulteeBottomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterHomeConsulteeBottom.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelHomeConsulteeBottom>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterHomeConsulteeBottom.ViewHolder, position: Int) {
        val categories = showData[position]
        holder.binding.model = categories
        holder.binding.ivIcon.setImageResource(showData[position].image)
        holder.binding.tvOccupation.text=categories.title
        holder.itemView.setOnClickListener {
            listener.onClick(position, "Occupation", categories.title)
        }


    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemConsulteeBottomBinding) : RecyclerView.ViewHolder(binding.root) {}

}