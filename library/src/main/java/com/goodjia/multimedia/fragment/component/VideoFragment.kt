package com.goodjia.multimedia.fragment.component

import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import kotlinx.android.synthetic.main.fragment_video.*


open class VideoFragment : MediaFragment(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener {
    companion object {
        val TAG = VideoFragment::class.simpleName
        const val KEY_LAYOUT_CONTENT = "layout_content"

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            uri: Uri,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            repeatTimes: Int = 1
        ): VideoFragment {
            val args = Bundle()
            args.putParcelable(KEY_URI, uri)
            args.putInt(KEY_LAYOUT_CONTENT, layoutContent)
            args.putInt(KEY_REPEAT_TIMES, repeatTimes)
            val fragment = VideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT
    private var mediaPlayer: MediaPlayer? = null
    private var videoPosition: Int? = null
        set(value) {
            field = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) null else value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutContent =
            savedInstanceState?.getInt(KEY_LAYOUT_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                ?: (arguments?.getInt(KEY_LAYOUT_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    ?: ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_LAYOUT_CONTENT, layoutContent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutParams = videoView.layoutParams
        layoutParams.width = layoutContent
        layoutParams.height = layoutContent
        videoView.setOnPreparedListener(this)
        videoView.setOnCompletionListener(this)
        videoView.setOnErrorListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        play()
    }

    override fun onPause() {
        super.onPause()
        if (videoView?.isPlaying == true) {
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
            Log.d(TAG, "repeat $repeatCount")
        } else {
            mediaCallback?.onCompletion(Task.ACTION_VIDEO, uri?.toString() ?: "")
        }
    }

    override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
        mediaCallback?.onError(Task.ACTION_VIDEO, uri?.toString() ?: "")
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        videoPosition?.let { mp?.seekTo(it) }
        mediaPlayer = mp
        mediaCallback?.onPrepared()
    }

    override fun setVolume(volumePercent: Int) {
        val value = volumePercent / 100f
        mediaPlayer?.setVolume(value, value)
    }

    fun play() {
        uri.apply {
            videoPosition = null
            videoView?.setVideoURI(this)
            videoView?.start()
        }
    }

    override fun start() {
        videoView?.start()
    }

    override fun pause() {
        videoView?.pause()
    }

    override fun stop() {
        videoView?.stopPlayback()
    }

    override fun repeat() {
        super.repeat()
        play()
    }
}
