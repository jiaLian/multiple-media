package com.goodjia.multiplemedia.fragment.component

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.goodjia.multiplemedia.R
import com.goodjia.multiplemedia.Task
import com.goodjia.utility.Logger
import kotlinx.android.synthetic.main.fragment_video.*


open class VideoFragment : MediaFragment(R.layout.fragment_video), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener {
    companion object {
        val TAG = VideoFragment::class.simpleName
        const val KEY_LAYOUT_CONTENT = "layout_content"
        const val KEY_PRELOAD = "preload"

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            uri: Uri,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            repeatTimes: Int = 1,
            preload: Boolean = false
        ) = VideoFragment().apply {
            arguments = bundle(uri, layoutContent, repeatTimes, preload)
        }

        @JvmStatic
        @JvmOverloads
        fun bundle(
            uri: Uri,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            repeatTimes: Int = 1,
            preload: Boolean = false
        ) = Bundle().apply {
            putParcelable(KEY_URI, uri)
            putInt(KEY_LAYOUT_CONTENT, layoutContent)
            putInt(KEY_REPEAT_TIMES, repeatTimes)
            putBoolean(KEY_PRELOAD, preload)
        }
    }

    private var layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT
    private var isPreload: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private var videoPosition: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutContent =
            savedInstanceState?.getInt(KEY_LAYOUT_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                ?: (arguments?.getInt(KEY_LAYOUT_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    ?: ViewGroup.LayoutParams.MATCH_PARENT)
        isPreload =
            savedInstanceState?.getBoolean(KEY_PRELOAD, false)
                ?: arguments?.getBoolean(KEY_PRELOAD, false) ?: false

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_LAYOUT_CONTENT, layoutContent)
        outState.putBoolean(KEY_PRELOAD, isPreload)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutParams = videoView.layoutParams
        layoutParams.width = layoutContent
        layoutParams.height = layoutContent
        videoView.setOnPreparedListener(this)
        videoView.setOnCompletionListener(this)
        videoView.setOnErrorListener(this)
        play()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Logger.d(TAG, "onHiddenChanged: $hidden $this")
        if (!hidden) start() else pause()
    }

    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoPosition = mediaPlayer?.currentPosition
        }
    }

    override fun onDestroyView() {
        videoView?.stopPlayback()
        super.onDestroyView()
    }

    override fun onCompletion(mp: MediaPlayer) {
        videoPosition = null
        repeatCount++
        if (repeatCount < repeatTimes) {
            play()
            Logger.d(TAG, "repeat $repeatCount $this")
        } else {
            Logger.d(TAG, "onCompletion $this")
            mediaCallback?.onCompletion(Task.ACTION_VIDEO, uri?.toString() ?: "")
        }
    }

    override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
        mediaCallback?.onError(Task.ACTION_VIDEO, uri?.toString() ?: "")
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Logger.d(TAG, "onPrepared $this")
        videoPosition?.let { videoView?.seekTo(it) }
        mediaPlayer = mp
        if (!isPreload) {
            mediaCallback?.onPrepared()
        } else {
            Logger.d(TAG, "onPrepared hide $this")
            parentFragmentManager.beginTransaction().hide(this).commit()
        }
    }

    override fun setVolume(volumePercent: Int) {
        val value = volumePercent / 100f
        mediaPlayer?.setVolume(value, value)
    }

    fun playPreload() {
        Logger.d(TAG, "disablePrepare $this")
        isPreload = false
        mediaPlayer?.let {
            Logger.d(TAG, "disablePrepare mediaPlayer $this")
            mediaCallback?.onPrepared()
            it.start()
        } ?: play()
    }

    fun play() {
        Logger.d(TAG, "play $this")
        uri.apply {
            videoView?.setVideoURI(this)
            videoView?.seekTo(1)
            if (!isPreload) videoView?.start()
        }
    }

    override fun start() {
        Logger.d(TAG, "start $this")
        if (!isPreload) videoView?.start()
    }

    override fun pause() {
        Logger.d(TAG, "pause $this")
        videoView?.pause()
    }

    override fun stop() {
        Logger.d(TAG, "stop $this")
        videoView?.stopPlayback()
    }

    override fun repeat() {
        super.repeat()
        Logger.d(TAG, "repeat $this")
        videoView?.seekTo(1)
        videoView?.start()
    }
}
