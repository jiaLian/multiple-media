package com.goodjia.multimedia.fragment.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.extractVideoIdFromUrl
import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener

class YoutubeFragment : MediaFragment() {
    companion object {
        const val KEY_UI = "showUI"

        @JvmStatic
        @JvmOverloads
        fun newInstance(url: String?, showUI: Boolean = true): YoutubeFragment {
            val args = Bundle()
            args.putString(MediaFragment.KEY_URI, url)
            args.putBoolean(KEY_UI, showUI)
            val fragment = YoutubeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var url: String? = null

    private var showUI: Boolean = false

    private val youTubePlayerView: YouTubePlayerView  by lazy {
        YouTubePlayerView(context)
    }
    private var youTubePlayer: YouTubePlayer? = null

    private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onReady() {
            load(url)
            mediaCallback?.onPrepared()
        }

        override fun onError(error: PlayerConstants.PlayerError) {
            super.onError(error)
            mediaCallback?.onError(Task.ACTION_YOUTUBE, url)
        }

        override fun onStateChange(state: PlayerConstants.PlayerState) {
            super.onStateChange(state)
            if (PlayerConstants.PlayerState.ENDED == state) {
                mediaCallback?.onCompletion(Task.ACTION_YOUTUBE, url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            url = arguments!!.getString(MediaFragment.KEY_URI)
            showUI = arguments!!.getBoolean(KEY_UI)
        } else {
            url = savedInstanceState.getString(MediaFragment.KEY_URI)
            showUI = savedInstanceState.getBoolean(KEY_UI)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(MediaFragment.KEY_URI, url)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.playerUIController.showUI(showUI)
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
        youTubePlayer?.play() ?: youTubePlayerView.initialize({ initializedYouTubePlayer ->
            youTubePlayer = initializedYouTubePlayer
            initializedYouTubePlayer.addListener(youTubePlayerListener)
        }, true)
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
