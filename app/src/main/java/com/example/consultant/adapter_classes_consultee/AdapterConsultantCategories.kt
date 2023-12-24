package com.example.consultant.adapter_classes_consultee

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.consultant.OnItemClick
import com.example.consultant.databinding.ItemConsultantCategoriesBinding
import com.example.consultant.databinding.ItemConsulteeTopBinding
import com.example.consultant.model_classes_consultee.ModelConsultantCategories
import com.example.consultant.model_classes_consultee.ModelHomeConsulteeTop

class AdapterConsultantCategories(var showData: MutableList<ModelHomeConsulteeTop>, var listener: OnItemClick):
    RecyclerView
.Adapter<AdapterConsultantCategories.ViewHolder>() {

    lateinit var binding: ItemConsultantCategoriesBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterConsultantCategories.ViewHolder {
        binding =
            ItemConsultantCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterConsultantCategories.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelHomeConsulteeTop>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterConsultantCategories.ViewHolder, position: Int) {
        val consultantCategories = showData[position]
        holder.binding.model = consultantCategories
        //holder.binding.ivConsultantImage.setImageResource(showData[position].icon)
        holder.binding.tvConsultantName.text = consultantCategories.consultantName
        holder.binding.tvConsultantOccupation.text = consultantCategories.occupation


        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemConsultantCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}