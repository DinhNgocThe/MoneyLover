package com.example.moneylover.ui

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.example.moneylover.databinding.DialogCustomBinding

class CustomAlertDialog(
    context: Context
) {
    private val dialog = Dialog(context)
    private val binding = DialogCustomBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
    }

    fun setTitle(title: String): CustomAlertDialog {
        binding.txtTitleDialog.text = title
        return this
    }

    fun setMessage(message: String): CustomAlertDialog {
        binding.txtMessageDialog.text = message
        return this
    }

    fun setOnAgreeClickListener(onAgree: () -> Unit): CustomAlertDialog {
        binding.btnAgreeDialog.setOnClickListener {
            onAgree()
            dialog.dismiss()
        }
        return this
    }

    fun setOnDegreeClickListener(onDegree: () -> Unit): CustomAlertDialog {
        binding.btnDegreeDialog.setOnClickListener {
            onDegree()
            dialog.dismiss()
        }
        return this
    }

    fun hideDegreeButton(): CustomAlertDialog {
        binding.btnDegreeDialog.visibility = View.GONE
        return this
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}
