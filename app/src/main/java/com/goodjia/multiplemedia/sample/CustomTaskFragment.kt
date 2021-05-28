package com.goodjia.multiplemedia.sample

import android.os.Bundle
import android.view.View
import com.goodjia.multiplemedia.fragment.component.PlayTimeMediaFragment
import kotlinx.android.synthetic.main.fragment_custom.*

class CustomTaskFragment : PlayTimeMediaFragment(R.layout.fragment_custom) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.text = name
        if (isPreload) {
            parentFragmentManager.beginTransaction().hide(this).commit()
        }
    }
}