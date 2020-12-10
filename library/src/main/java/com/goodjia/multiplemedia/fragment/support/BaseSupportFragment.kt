package com.goodjia.multiplemedia.fragment.support

import me.yokeyword.fragmentation.SupportFragment
import me.yokeyword.fragmentation.anim.DefaultNoAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator

abstract class BaseSupportFragment : SupportFragment() {
    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return DefaultNoAnimator()
    }
}
