package com.goodjia.multimedia.fragment

import me.yokeyword.fragmentation.SupportFragment
import me.yokeyword.fragmentation.anim.DefaultNoAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

abstract class BaseFragment : SupportFragment() {
    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultNoAnimator()
    }
}
