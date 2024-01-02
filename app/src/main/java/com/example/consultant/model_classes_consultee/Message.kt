package com.example.consultant.model_classes_consultee

class Message {

    var message:String?=null
    var senderUid:String?=null


    constructor(){}
    constructor(message:String, senderUid:String)
    {
        this.message=message
        this.senderUid=senderUid

    }
}