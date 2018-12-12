package com.goodjia.multimedia.fragment.component

import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.UserVisibleChangedBroadcastReceiver
import kotlinx.android.synthetic.main.fragment_video.*


open class VideoFragment : MediaFragment(), MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnPreparedListener {

    companion object {
        const val KEY_LAYOUT_CONTENT = "layout_content"
        fun newInstance(id: String, uri: Uri, layoutContent: Int = ViewGroup.LayoutParams.WRAP_CONTENT): VideoFragment {
            val args = Bundle()
            args.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
            args.putParcelable(MediaFragment.KEY_URI, uri)
            args.putInt(KEY_LAYOUT_CONTENT, layoutContent)
            val fragment = VideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var layoutContent: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mediaPlayer: MediaPlayer? = null
    private var id: String? = null
    private val visibleChangedBroadcastReceiver = object : UserVisibleChangedBroadcastReceiver() {
        override fun onVisibleChanged(isVisible: Boolean, id: String) {
            if (this@VideoFragment.id == id && !isVisible && videoView.isPlaying) {
                videoView.pause()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            uri = arguments!!.getParcelable(MediaFragment.KEY_URI)
            id = arguments!!.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
            layoutContent = arguments!!.getInt(KEY_LAYOUT_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
            uri = savedInstanceState.getParcelable(MediaFragment.KEY_URI)
            id = savedInstanceState.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
            layoutContent = savedInstanceState.getInt(KEY_LAYOUT_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
        outState.putParcelable(MediaFragment.KEY_URI, uri)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            visibleChangedBroadcastReceiver,
            IntentFilter(UserVisibleChangedBroadcastReceiver.INTENT_ACTION_VISIBLE)
        )
        play()
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(visibleChangedBroadcastReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        videoView.stopPlayback()
    }

    override fun onCompletion(mp: MediaPlayer) {
        mediaCallback?.onCompletion(Task.ACTION_VIDEO, uri?.toString() ?: "")
    }

    override fun onError(mediaPlayer: MediaPlayer, i: Int, i1: Int): Boolean {
        mediaCallback?.onError(Task.ACTION_VIDEO, uri?.toString() ?: "")
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer = mp
        mediaCallback?.onPrepared()
    }

    override fun setVolume(volumePercent: Int) {
        val value = volumePercent / 100f
        mediaPlayer?.setVolume(value, value)
    }

    fun play() {
        uri.apply {
            videoView.setVideoURI(this)
            videoView.start()
        }
    }
}
