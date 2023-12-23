package com.example.consultant.adapter_classes_consultee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consultant.OnItemClick
import com.example.consultant.R
import com.example.consultant.databinding.ItemConsulteeTopBinding
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop

class AdapterHomeConsulteeTop(var showData: MutableList<ModelHomeConsulteeTop>, var listener: OnItemClick):RecyclerView
    .Adapter<AdapterHomeConsulteeTop.ViewHolder>() {

    lateinit var binding: ItemConsulteeTopBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterHomeConsulteeTop.ViewHolder {
        binding = ItemConsulteeTopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterHomeConsulteeTop.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelHomeConsulteeTop>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterHomeConsulteeTop.ViewHolder, position: Int) {
        val topConsultant = showData[position]
        holder.binding.model = topConsultant
        Glide.with(holder.binding.ivConsultantImage).load(topConsultant.consultantImage).into(holder.binding.ivConsultantImage)
        holder.binding.tvConsultantName.text=topConsultant.consultantName

        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
       return showData.size
    }

    class ViewHolder(val binding: ItemConsulteeTopBinding) : RecyclerView.ViewHolder(binding.root) {}
}