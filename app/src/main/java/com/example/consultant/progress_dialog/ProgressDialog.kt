package com.example.consultant.progress_dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.consultant.R

class ProgressDialog(val mActivity: Activity) {

    private var dialog: Dialog? = null



    fun showDialog()
    {
        val inflater=mActivity.layoutInflater
        val dialogView=inflater.inflate(R.layout.activity_progress_dialog,null)
        dialog=Dialog(mActivity)
        val builder= AlertDialog.Builder(mActivity, R.style.DialogCustomTheme)
        builder.setView(dialogView)
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog?.window?.setDimAmount(0.2f)
        dialog=builder.create()


        dialog?.show()

    }

    fun dialogDismiss()
    {
        dialog?.dismiss()
    }

}