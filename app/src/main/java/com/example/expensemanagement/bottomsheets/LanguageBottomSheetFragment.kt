package com.example.expensemanagement.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.expensemanagement.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class LanguageBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvents(view)
    }

    private fun initEvents(view: View) {
        val btnVietnamese = view.findViewById<ConstraintLayout>(R.id.btnVietnameseChangeLanguageFragment)
        val btnEnglish = view.findViewById<ConstraintLayout>(R.id.btnEnglishChangeLanguageFragment)
        val btnClose = view.findViewById<ImageButton>(R.id.btnCloseChangeLanguageFragment)

        //Close fragment
        btnClose.setOnClickListener{
            dismiss()
        }

        //Change color when click on button
        btnVietnamese.setOnClickListener{
            btnVietnamese.setBackgroundResource(R.drawable.btn_light_green)
            btnEnglish.setBackgroundResource(R.drawable.btn_transparent_green)
        }
        btnEnglish.setOnClickListener{
            btnVietnamese.setBackgroundResource(R.drawable.btn_transparent_green)
            btnEnglish.setBackgroundResource(R.drawable.btn_light_green)
        }
    }
}