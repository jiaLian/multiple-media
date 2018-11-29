package com.goodjia.multimedia.fragment.component

import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.UserVisibleChangedBroadcastReceiver
import com.sprylab.android.widget.TextureVideoView

class VideoFragment : MediaFragment(), MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    companion object {
        fun newInstance(id: String, uri: Uri): VideoFragment {
            val args = Bundle()
            args.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
            args.putParcelable(MediaFragment.KEY_URI, uri)
            val fragment = VideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val videoView: TextureVideoView by lazy {
        TextureVideoView(context)
    }

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
        } else {
            uri = arguments!!.getParcelable(MediaFragment.KEY_URI)
            id = savedInstanceState.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
        outState.putParcelable(MediaFragment.KEY_URI, uri)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return videoView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = videoView.layoutParams as FrameLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.CENTER
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

    fun play() {
        uri.apply {
            videoView.setVideoURI(this)
            videoView.start()
        }
    }
}
