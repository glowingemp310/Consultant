package com.example.consultant.adapter_classes_consultant

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.R
import com.example.consultant.model_classes_consultant.ModelAppointmentType

class AdapterAppointmentTypes(var mData:ArrayList<ModelAppointmentType>, var mContext: Context,
                              var callBackSelected:(prePosition: Int, selectedPost: Int, item: ModelAppointmentType)-> Unit):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selected: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_appointment_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = mData[position]
        val tvTopItem = holder.itemView.findViewById<TextView>(R.id.tvItemTop)
        val viewSelection = holder.itemView.findViewById<View>(R.id.viewSelected)

        tvTopItem.text = item.title
        if (selected == position) {
            tvTopItem.setTextColor(Color.parseColor("#ffffbb33"))
            viewSelection.visibility = View.VISIBLE
        }

        else
        {
            tvTopItem.setTextColor(Color.parseColor("#FFFFFF"))
            viewSelection.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val prePos = selected
            selected = position
            notifyItemChanged(position)
            notifyItemChanged(prePos)
            callBackSelected.invoke(prePos, selected, item)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
    class ViewHolder(item: View): RecyclerView.ViewHolder(item){

    }

}