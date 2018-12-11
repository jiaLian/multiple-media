package com.goodjia.multimedia.fragment.component

import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.UserVisibleChangedBroadcastReceiver
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
        fun newInstance(url: String, showUI: Boolean = true, id: String = "only"): YoutubeFragment {
            val args = Bundle()
            args.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
            args.putString(MediaFragment.KEY_URI, url)
            args.putBoolean(KEY_UI, showUI)
            val fragment = YoutubeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var url: String

    private var showUI: Boolean = false

    private val youTubePlayerView: YouTubePlayerView  by lazy {
        YouTubePlayerView(context)
    }
    private var youTubePlayer: YouTubePlayer? = null

    private var id: String = "only"

    private val visibleChangedBroadcastReceiver = object : UserVisibleChangedBroadcastReceiver() {
        override fun onVisibleChanged(isVisible: Boolean, id: String) {
            if (this@YoutubeFragment.id == id && !isVisible) {
                youTubePlayer?.pause()
            }
        }
    }

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
            id = arguments!!.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
            showUI = arguments!!.getBoolean(KEY_UI)
        } else {
            url = savedInstanceState.getString(MediaFragment.KEY_URI)
            id = savedInstanceState.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
            showUI = savedInstanceState.getBoolean(KEY_UI)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
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
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            visibleChangedBroadcastReceiver,
            IntentFilter(UserVisibleChangedBroadcastReceiver.INTENT_ACTION_VISIBLE)
        )
        youTubePlayer?.play() ?: youTubePlayerView.initialize({ initializedYouTubePlayer ->
            youTubePlayer = initializedYouTubePlayer
            initializedYouTubePlayer.addListener(youTubePlayerListener)
        }, true)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(visibleChangedBroadcastReceiver)
    }

    override fun setVolume(volumePercent: Int) {
        youTubePlayer?.setVolume(volumePercent)
    }

    fun release() {
        youTubePlayerView.release()
    }

    fun load(url: String) {
        val videoId = url.extractVideoIdFromUrl()
        youTubePlayer?.loadVideo(videoId, 0f)
    }
}
