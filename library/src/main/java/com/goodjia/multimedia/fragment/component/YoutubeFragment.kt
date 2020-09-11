package com.goodjia.multimedia.fragment.component

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.extractVideoIdFromUrl
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class YoutubeFragment : MediaFragment() {
    companion object {
        val TAG = YoutubeFragment::class.java.simpleName
        const val KEY_UI = "showUI"

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            url: String?, showUI: Boolean = true,
            repeatTimes: Int = 1
        ): YoutubeFragment {
            val args = Bundle()
            args.putString(KEY_URI, url)
            args.putBoolean(KEY_UI, showUI)
            args.putInt(KEY_REPEAT_TIMES, repeatTimes)
            val fragment = YoutubeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var url: String? = null

    private var showUI: Boolean = false

    private val youTubePlayerView: YouTubePlayerView by lazy {
        YouTubePlayerView(_mActivity).apply {
            enableAutomaticInitialization = false
        }
    }
    private var youTubePlayer: YouTubePlayer? = null

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
                repeatCount++
                if (repeatCount < repeatTimes) {
                    repeat()
                    Log.d(TAG, "repeat $repeatCount")
                } else {
                    mediaCallback?.onCompletion(Task.ACTION_YOUTUBE, url)
                }
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

    override fun setVolume(volumePercent: Int) {
        youTubePlayer?.setVolume(volumePercent)
    }

    fun release() {
        youTubePlayerView.release()
    }

    fun repeat() {
        load(url)
    }

    fun load(url: String?) {
        val videoId = url?.extractVideoIdFromUrl()
        if (videoId?.isNotEmpty() == true) {
            youTubePlayer?.loadVideo(videoId, 0f)
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
