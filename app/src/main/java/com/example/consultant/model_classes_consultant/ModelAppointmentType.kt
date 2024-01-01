package com.example.consultant.model_classes_consultant

import com.example.consultant.R

class ModelAppointmentType {

    constructor(title: String, colorSelected: Int?, color: Int?) {

        this.title = title
        this.colorSelected = colorSelected
        this.color = color

    }

    constructor(title: String, colorSelected: String, color: Int)

    var title = "ALL"
    var colorSelected: Int? = R.color.yellow
    var color: Int? = android.R.color.black
}