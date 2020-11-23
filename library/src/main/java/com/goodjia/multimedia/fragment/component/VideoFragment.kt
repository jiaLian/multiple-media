package com.goodjia.multimedia.fragment.component

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media.AudioAttributesCompat
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.common.UriMediaItem
import androidx.media2.player.MediaPlayer
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import kotlinx.android.synthetic.main.fragment_video.*
import java.util.concurrent.Executors


open class VideoFragment : MediaFragment() {
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

    private val executorService by lazy {
        Executors.newSingleThreadExecutor()
    }
    private val mediaMetaData by lazy {
        MediaMetadata.Builder().build()
    }
    private  var mediaPlayer: MediaPlayer?=null

    private var layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT

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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mediaPlayer = MediaPlayer(requireContext()).apply {
            videoView.setPlayer(this)
        }
        mediaPlayer?.run{
            registerPlayerCallback(executorService,
                object : MediaPlayer.PlayerCallback() {
                    override fun onError(mp: MediaPlayer, item: MediaItem, what: Int, extra: Int) {
                        mediaCallback?.onError(Task.ACTION_VIDEO, uri?.toString() ?: "")
                    }

                    override fun onPlaybackCompleted(player: SessionPlayer) {
                        repeatCount++
                        if (repeatCount < repeatTimes) {
                            this@VideoFragment.play()
                            Log.d(TAG, "onPlaybackCompleted repeat $repeatCount")
                        } else {
                            Log.d(TAG, "onPlaybackCompleted: onCompletion")
                            mediaCallback?.onCompletion(Task.ACTION_VIDEO, uri?.toString() ?: "")
                        }
                    }

                    override fun onPlayerStateChanged(player: SessionPlayer, playerState: Int) {
                        Log.d(TAG, "onPlayerStateChanged: $playerState")
                    }
                })
            setAudioAttributes(AudioAttributesCompat.Builder().build())
            uri?.let {
                val mediaItem = UriMediaItem.Builder(it).setMetadata(mediaMetaData).build()
                setMediaItem(mediaItem)
                prepare().addListener({
                    play()
                    mediaCallback?.onPrepared()
                }, executorService)
            }
        }
    }

    override fun onDestroyView() {
        mediaPlayer?.close()
        executorService.shutdownNow()
        super.onDestroyView()
    }

    override fun setVolume(volumePercent: Int) {
        val value = volumePercent / 100f
        mediaPlayer?.playerVolume = value
    }

    fun play() {
        mediaPlayer?.run {
            seekTo(0L)
            play()
        }
    }

    override fun start() {
        mediaPlayer?.play()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun stop() {
        mediaPlayer?.pause()
    }

    override fun repeat() {
        super.repeat()
        play()
    }
}
