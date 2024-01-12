package com.example.consultant.model_classes_consultee

import java.io.Serializable

class ModelHomeConsulteeTop() : Serializable {
    lateinit  var id: String
    var consultantImage: String? = null
    var consultantName: String? = null
    var clinicName: String? = null
    var address: String? = null
    var phoneNo: String? = null
    var cnic: String? = null
    var about: String? = null
    var occupation: String? = null
    var documentImage: String? = null
    var closeTime: String? = null
    var openTime: String? = null


    constructor(
        id: String,
        consultantImage: String,
        consultantName: String,
        clinicName: String,
        address: String,
        phoneNo: String,
        cnic: String,
        about: String,
        occupation: String,
        documentImage:String,
        closeTime: String,
        openTime: String
    ) : this() {
        this.id = id
        this.consultantImage = consultantImage
        this.consultantName = consultantName
        this.clinicName = clinicName
        this.address = address
        this.phoneNo = phoneNo
        this.cnic = cnic
        this.about = about
        this.occupation = occupation
        this.documentImage=documentImage
        this.closeTime = closeTime
        this.openTime = openTime
    }
}