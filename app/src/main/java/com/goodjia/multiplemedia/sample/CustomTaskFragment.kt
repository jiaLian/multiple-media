package com.goodjia.multiplemedia.sample

import android.os.Bundle
import android.view.View
import com.goodjia.multiplemedia.fragment.component.PlayTimeMediaFragment
import kotlinx.android.synthetic.main.fragment_custom.*

class CustomTaskFragment : PlayTimeMediaFragment(R.layout.fragment_custom) {
    companion object {
        const val KEY_ID = "id"
        fun bundle(id: String) = Bundle().apply {
            putString(KEY_ID, id)
        }
    }

    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = if (savedInstanceState == null) {
            arguments?.getString(KEY_ID)
        } else {
            savedInstanceState.getString(KEY_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.text = id
        if (isPreload) {
            parentFragmentManager.beginTransaction().hide(this).commit()
        }
    }
}