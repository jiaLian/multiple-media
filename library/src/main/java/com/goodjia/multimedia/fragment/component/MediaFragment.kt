package com.goodjia.multimedia.fragment.component

import android.net.Uri
import android.os.Bundle
import android.view.animation.Animation
import com.goodjia.multimedia.MediaController
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.fragment.BaseFragment

abstract class MediaFragment : BaseFragment(), MediaController {
    companion object {
        const val KEY_REPEAT_TIMES = "repeat_times"
        const val KEY_PLAY_TIME = "play_time"
        const val KEY_URI = "uri"
    }

    protected val mediaCallback: MediaCallback? by lazy {
        try {
            parentFragment as MediaCallback?
        } catch (e: Exception) {
            null
        }
    }
    protected val animationCallback: AnimationCallback? by lazy {
        try {
            parentFragment as AnimationCallback?
        } catch (e: Exception) {
            null
        }
    }
    protected var uri: Uri? = null

    protected var playtime: Int = Task.DEFAULT_PLAYTIME
    protected var repeatTimes: Int = 1
    protected var repeatCount: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repeatTimes = savedInstanceState?.getInt(KEY_REPEAT_TIMES)
            ?: (arguments?.getInt(KEY_REPEAT_TIMES) ?: Int.MIN_VALUE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REPEAT_TIMES, repeatTimes)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (!isDetached) animationCallback?.animation(transit, enter, nextAnim)
            ?: super.onCreateAnimation(
                transit,
                enter,
                nextAnim
            ) else null
    }

    override fun setVolume(volumePercent: Int) {
    }

    interface AnimationCallback {
        fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation?
    }

    interface MediaCallback {
        fun onCompletion(action: Int, message: String?)

        fun onError(action: Int, message: String?)

        fun onPrepared()
    }
}
