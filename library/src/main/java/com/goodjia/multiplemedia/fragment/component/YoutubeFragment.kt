package com.goodjia.multiplemedia.fragment.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.extractVideoIdFromUrl
import com.goodjia.utility.Logger
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

open class YoutubeFragment : MediaFragment() {
    companion object {
        val TAG = YoutubeFragment::class.java.simpleName
        const val KEY_UI = "showUI"

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            url: String?, showUI: Boolean = true,
            repeatTimes: Int = 1
        ) = YoutubeFragment().apply {
            arguments = bundle(url, showUI, repeatTimes)
        }

        @JvmStatic
        @JvmOverloads
        fun bundle(
            url: String?, showUI: Boolean = true,
            repeatTimes: Int = 1
        ) = Bundle().apply {
            putString(KEY_URI, url)
            putBoolean(KEY_UI, showUI)
            putInt(KEY_REPEAT_TIMES, repeatTimes)
        }
    }

    private var url: String? = null

    private var showUI: Boolean = false

    private val youTubePlayerView: YouTubePlayerView by lazy {
        YouTubePlayerView(requireContext()).apply {
            enableAutomaticInitialization = false
        }
    }
    private var youTubePlayer: YouTubePlayer? = null
    private var videoDuration: Float? = null
    private var lastCurrentTime = 0L
    private var currentSecond: Float? = null
    private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            this@YoutubeFragment.youTubePlayer = youTubePlayer
            load(url)
            mediaCallback?.onPrepared()
        }

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            mediaCallback?.onError(Task.ACTION_YOUTUBE, url)
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            if (PlayerConstants.PlayerState.ENDED == state) {
                currentSecond = null
                repeatCount++
                if (repeatCount < repeatTimes) {
                    repeat()
                    Logger.d(TAG, "repeat $repeatCount")
                } else {
                    mediaCallback?.onCompletion(Task.ACTION_YOUTUBE, url)
                }
            }
        }

        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
            super.onVideoDuration(youTubePlayer, duration)
            videoDuration = duration
        }

        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            if (System.currentTimeMillis() - lastCurrentTime > 1_000) {
                currentSecond = second
                lastCurrentTime = System.currentTimeMillis()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            url = arguments?.getString(KEY_URI)
            showUI = arguments?.getBoolean(KEY_UI) ?: false
        } else {
            url = savedInstanceState.getString(KEY_URI)
            showUI = savedInstanceState.getBoolean(KEY_UI)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_URI, url)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.getPlayerUiController().showUi(showUI)
        return youTubePlayerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = youTubePlayerView.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onStart() {
        super.onStart()
        youTubePlayer?.play() ?: youTubePlayerView.initialize(youTubePlayerListener, true)
    }

    override fun onPause() {
        super.onPause()
        youTubePlayer

    }

    override fun setVolume(volumePercent: Int) {
        youTubePlayer?.setVolume(volumePercent)
    }

    fun release() {
        youTubePlayerView.release()
    }

    override fun repeat() {
        super.repeat()
        load(url)
    }

    fun reload() {
        load(url)
    }

    fun load(url: String?) {
        val videoId = url?.extractVideoIdFromUrl()
        if (videoId?.isNotEmpty() == true) {
            youTubePlayer?.loadVideo(videoId, currentSecond ?: 0f)
        }
    }

    override fun start() {
        youTubePlayer?.play()
    }

    override fun pause() {
        youTubePlayer?.pause()
    }

    override fun stop() {
        pause()
    }
}
