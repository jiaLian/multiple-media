package com.goodjia.multimedia.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.fragment.component.PlayTimeMediaFragment
import kotlinx.android.synthetic.main.fragment_custom.*

class CustomTaskFragment : PlayTimeMediaFragment() {
    companion object {
        const val KEY_ID = "id"
        fun newInstance(id: String): CustomTaskFragment {
            val args = bundle(id)
            val fragment = CustomTaskFragment()
            fragment.arguments = args
            return fragment
        }

        fun bundle(id: String): Bundle {
            val args = Bundle()
            args.putString(KEY_ID, id)
            return args
        }
    }

    private var id: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            id = arguments?.getString(KEY_ID)
        } else {
            id = savedInstanceState.getString(KEY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_custom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.text = id
    }
}