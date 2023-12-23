package com.example.consultant

interface OnItemClick {
    fun onClick(position: Int, type: String? = "", data: Any? = null) {}
}