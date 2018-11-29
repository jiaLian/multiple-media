package com.goodjia.multimedia.fragment.component

import android.net.Uri
import android.view.animation.Animation
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.fragment.BaseFragment

abstract class MediaFragment : BaseFragment() {
    companion object {
        const val KEY_DELAY_SECOND = "delay_second"
        const val KEY_URI = "uri"
    }

    protected val mediaCallback: MediaCallback? by lazy {
        parentFragment as MediaCallback?
    }
    protected val animationCallback: AnimationCallback? by lazy {
        parentFragment as AnimationCallback?
    }
    protected var uri: Uri? = null

    protected var delaySecond: Int = Task.DEFAULT_PLAYTIME

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return animationCallback?.animation(transit, enter, nextAnim) ?: super.onCreateAnimation(
            transit,
            enter,
            nextAnim
        )
    }

    interface AnimationCallback {
        fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation?
    }

    interface MediaCallback {
        fun onCompletion(action: Int, message: String)

        fun onError(action: Int, message: String)
    }
}
