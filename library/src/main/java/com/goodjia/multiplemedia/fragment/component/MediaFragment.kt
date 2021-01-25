package com.goodjia.multiplemedia.fragment.component

import android.net.Uri
import android.os.Bundle
import android.view.animation.Animation
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.goodjia.multiplemedia.MediaController
import com.goodjia.multiplemedia.Task
import com.goodjia.utility.Logger

abstract class MediaFragment : Fragment, MediaController {
    companion object {
        val TAG = MediaFragment::class.simpleName
        const val KEY_PRELOAD = "preload"
        const val KEY_REPEAT_TIMES = "repeat_times"
        const val KEY_PLAY_TIME = "play_time"
        const val KEY_URI = "uri"
        const val KEY_SHOW_LOADING_ICON = "loading_icon"
        const val KEY_SHOW_FAILURE_ICON = "failure_icon"
    }

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    var preloadTask: Task? = null
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
    protected var isPreload: Boolean = false
    protected var playTime: Int = Task.DEFAULT_PLAYTIME
    protected var resetPlayTime: Int = Task.DEFAULT_PLAYTIME
    protected var repeatTimes: Int = 1
    protected var repeatCount: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            playTime = arguments?.getInt(KEY_PLAY_TIME) ?: Task.DEFAULT_PLAYTIME
            resetPlayTime = arguments?.getInt(KEY_PLAY_TIME) ?: Task.DEFAULT_PLAYTIME
            repeatTimes = arguments?.getInt(KEY_REPEAT_TIMES) ?: Int.MIN_VALUE
            uri = arguments?.getParcelable(KEY_URI)
            isPreload = arguments?.getBoolean(KEY_PRELOAD, false) ?: false
        } else {
            playTime = savedInstanceState.getInt(KEY_PLAY_TIME, Task.DEFAULT_PLAYTIME)
            resetPlayTime = savedInstanceState.getInt(KEY_PLAY_TIME, Task.DEFAULT_PLAYTIME)
            repeatTimes = savedInstanceState.getInt(KEY_REPEAT_TIMES)
            uri = savedInstanceState.getParcelable(KEY_URI)
            isPreload = savedInstanceState.getBoolean(KEY_PRELOAD, false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_REPEAT_TIMES, repeatTimes)
        outState.putInt(KEY_PLAY_TIME, playTime)
        outState.putParcelable(KEY_URI, uri)
        outState.putBoolean(KEY_PRELOAD, isPreload)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (!isDetached) animationCallback?.animation(transit, enter, nextAnim)
            ?: super.onCreateAnimation(
                transit,
                enter,
                nextAnim
            ) else null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Logger.d(TAG, "onHiddenChanged: $hidden $this")
        if (!hidden) start() else pause()
    }

    override fun repeat() {
        repeatCount = 0
        playTime = resetPlayTime
    }

    override fun setVolume(volumePercent: Int) {
    }

    open fun playPreload() {
        isPreload = false
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
