package com.example.consultant.adapter_classes_consultee

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.consultant.R
import com.example.consultant.model_classes_consultee.ModelSetBookTime

class AdapterSetBookTime(): RecyclerView.Adapter<AdapterSetBookTime.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSetBookTime.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_select_booking,parent,false)
        return AdapterSetBookTime.ViewHolder(view)
    }
    private val arrlist = ArrayList<ModelSetBookTime>()
    fun set_Data(timeStrings:List<ModelSetBookTime>) {
        arrlist.clear()
        arrlist.addAll(timeStrings)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterSetBookTime.ViewHolder, position: Int) {
        val service= arrlist[position]
        holder.tvSetTime.text=arrlist[position].tvSeTime
        holder.layoutService.setOnClickListener {
            for (i in arrlist.indices) {
                arrlist[i].isSelect = false
            }
            service.isSelect = true
            notifyDataSetChanged()
        }

        if (service.isSelect) {
            holder.layoutService.background.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.yellow),
                PorterDuff.Mode.SRC_ATOP
            )
            holder.tvSetTime.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))

        } else {
            holder.layoutService.background.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
            holder.tvSetTime.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }
    }

    fun getSelectedTime(): String? {
        for (item in arrlist) {
            if (item.isSelect) {
                return item.tvSeTime
            }
        }
        return null
    }


    override fun getItemCount(): Int {
        return arrlist.size
    }

    class ViewHolder(item: View): RecyclerView.ViewHolder(item)
    {
        val tvSetTime=item.findViewById<TextView>(R.id.tvSetTime)
        val layoutService: ConstraintLayout =item.findViewById(R.id.clSelectTime)
    }
}