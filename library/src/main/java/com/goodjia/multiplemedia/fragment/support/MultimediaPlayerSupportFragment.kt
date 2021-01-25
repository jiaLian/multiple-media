package com.goodjia.multiplemedia.fragment.support

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.fragment.MultimediaPlayerFragment
import me.yokeyword.fragmentation.*
import me.yokeyword.fragmentation.ISupportFragment.LaunchMode
import me.yokeyword.fragmentation.anim.DefaultNoAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator


open class MultimediaPlayerSupportFragment : MultimediaPlayerFragment(), ISupportFragment {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun newInstance(
            tasks: List<Task>?,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            repeatTimes: Int? = null,
            playTime: Int? = null,
            volumePercent: Int? = null, preload: Boolean = false,
            showLoadingIcon: Boolean = true,
            showFailureIcon: Boolean = true
        ) = MultimediaPlayerSupportFragment().apply {
            arguments = bundle(
                tasks,
                layoutContent,
                repeatTimes,
                playTime,
                volumePercent,
                preload,
                showLoadingIcon,
                showFailureIcon
            )
        }
    }

    private val mDelegate = SupportFragmentDelegate(this)
    protected var _mActivity: SupportActivity? = null

    override fun getSupportDelegate(): SupportFragmentDelegate? = mDelegate
    override fun extraTransaction(): ExtraTransaction? = mDelegate.extraTransaction()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mDelegate.onAttach()
        _mActivity = mDelegate.activity as SupportActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return mDelegate.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mDelegate.onActivityCreated(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mDelegate.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mDelegate.onPause()
    }

    override fun onDestroyView() {
        mDelegate.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mDelegate.onDestroy()
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mDelegate.onHiddenChanged(hidden)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mDelegate.setUserVisibleHint(isVisibleToUser)
    }

    override fun post(runnable: Runnable?) = mDelegate.post(runnable)

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) =
        mDelegate.onEnterAnimationEnd(savedInstanceState)


    override fun onLazyInitView(savedInstanceState: Bundle?) =
        mDelegate.onLazyInitView(savedInstanceState)


    override fun onSupportVisible() = mDelegate.onSupportVisible()

    override fun onSupportInvisible() = mDelegate.onSupportInvisible()

    override fun isSupportVisible() = mDelegate.isSupportVisible

    override fun onCreateFragmentAnimator(): FragmentAnimator? = DefaultNoAnimator()

    override fun getFragmentAnimator(): FragmentAnimator? = mDelegate.fragmentAnimator


    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator?) {
        mDelegate.fragmentAnimator = fragmentAnimator
    }

    override fun onBackPressedSupport() = mDelegate.onBackPressedSupport()


    override fun setFragmentResult(resultCode: Int, bundle: Bundle?) {
        mDelegate.setFragmentResult(resultCode, bundle)
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) =
        mDelegate.onFragmentResult(requestCode, resultCode, data)


    override fun onNewBundle(args: Bundle?) = mDelegate.onNewBundle(args)


    override fun putNewBundle(newBundle: Bundle?) = mDelegate.putNewBundle(newBundle)


    protected open fun hideSoftInput() = mDelegate.hideSoftInput()


    protected open fun showSoftInput(view: View?) = mDelegate.showSoftInput(view)

    open fun loadRootFragment(containerId: Int, toFragment: ISupportFragment?) {
        mDelegate.loadRootFragment(containerId, toFragment)
    }

    open fun loadRootFragment(
        containerId: Int,
        toFragment: ISupportFragment?,
        addToBackStack: Boolean,
        allowAnim: Boolean
    ) = mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnim)


    open fun loadMultipleRootFragment(
        containerId: Int,
        showPosition: Int,
        vararg toFragments: ISupportFragment?
    ) = mDelegate.loadMultipleRootFragment(containerId, showPosition, *toFragments)

    open fun showHideFragment(showFragment: ISupportFragment?) =
        mDelegate.showHideFragment(showFragment)

    open fun showHideFragment(showFragment: ISupportFragment?, hideFragment: ISupportFragment?) =
        mDelegate.showHideFragment(showFragment, hideFragment)

    open fun start(toFragment: ISupportFragment?) = mDelegate.start(toFragment)

    open fun start(toFragment: ISupportFragment?, @LaunchMode launchMode: Int) =
        mDelegate.start(toFragment, launchMode)

    open fun startForResult(toFragment: ISupportFragment?, requestCode: Int) =
        mDelegate.startForResult(toFragment, requestCode)

    open fun startWithPop(toFragment: ISupportFragment?) = mDelegate.startWithPop(toFragment)

    open fun startWithPopTo(
        toFragment: ISupportFragment?,
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean
    ) = mDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment)


    open fun replaceFragment(toFragment: ISupportFragment?, addToBackStack: Boolean) =
        mDelegate.replaceFragment(toFragment, addToBackStack)

    open fun pop() = mDelegate.pop()

    open fun popChild() = mDelegate.popChild()

    open fun popTo(targetFragmentClass: Class<*>?, includeTargetFragment: Boolean) =
        mDelegate.popTo(targetFragmentClass, includeTargetFragment)

    open fun popTo(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?
    ) = mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable)

    open fun popTo(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?,
        popAnim: Int
    ) = mDelegate.popTo(
        targetFragmentClass,
        includeTargetFragment,
        afterPopTransactionRunnable,
        popAnim
    )

    open fun popToChild(targetFragmentClass: Class<*>?, includeTargetFragment: Boolean) =
        mDelegate.popToChild(targetFragmentClass, includeTargetFragment)

    open fun popToChild(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?
    ) = mDelegate.popToChild(
        targetFragmentClass,
        includeTargetFragment,
        afterPopTransactionRunnable
    )

    open fun popToChild(
        targetFragmentClass: Class<*>?,
        includeTargetFragment: Boolean,
        afterPopTransactionRunnable: Runnable?,
        popAnim: Int
    ) = mDelegate.popToChild(
        targetFragmentClass,
        includeTargetFragment,
        afterPopTransactionRunnable,
        popAnim
    )

    open fun getTopFragment(): ISupportFragment? = SupportHelper.getTopFragment(fragmentManager)

    open fun getTopChildFragment(): ISupportFragment? =
        SupportHelper.getTopFragment(childFragmentManager)

    open fun getPreFragment(): ISupportFragment? = SupportHelper.getPreFragment(this)

    open fun <T : ISupportFragment?> findFragment(fragmentClass: Class<T>?): T =
        SupportHelper.findFragment(fragmentManager, fragmentClass)

    open fun <T : ISupportFragment?> findChildFragment(fragmentClass: Class<T>?): T =
        SupportHelper.findFragment(childFragmentManager, fragmentClass)
}
