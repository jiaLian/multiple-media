package com.goodjia.multimedia.fragment

import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.goodjia.multimedia.MediaController
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.UserVisibleChangedBroadcastReceiver
import com.goodjia.multimedia.fragment.component.ImageFragment
import com.goodjia.multimedia.fragment.component.MediaFragment
import com.goodjia.multimedia.fragment.component.VideoFragment
import com.goodjia.multimedia.fragment.component.YoutubeFragment
import java.util.*


open class MultimediaPlayerFragment : BaseFragment(), MediaFragment.MediaCallback, MediaFragment.AnimationCallback,
    MediaController {

    companion object {
        private val TAG = MultimediaPlayerFragment::class.java.simpleName

        const val KEY_TASKS = "tasks"
        const val DEFAULT_ID = "only_one"

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            tasks: List<Task>?,
            id: String = DEFAULT_ID,
            layoutContent: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        ): MultimediaPlayerFragment {
            val args = Bundle()
            args.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
            if (tasks != null) {
                args.putParcelableArrayList(KEY_TASKS, ArrayList(tasks))
            }
            args.putInt(VideoFragment.KEY_LAYOUT_CONTENT, layoutContent)
            val fragment = MultimediaPlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var layoutContent: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    protected lateinit var id: String

    var tasks: ArrayList<Task>? = null
    protected var mediaIndex = 0
    private var mediaFragment: MediaFragment? = null

    var playerListener: PlayerListener? = null
    var animationCallback: MediaFragment.AnimationCallback? = null
    private val visibleChangedBroadcastReceiver = object : UserVisibleChangedBroadcastReceiver() {
        override fun onVisibleChanged(isVisible: Boolean, id: String) {
            if (isVisible) {
                startTask()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            tasks = arguments!!.getParcelableArrayList(KEY_TASKS)
            id = arguments!!.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
            layoutContent = arguments!!.getInt(VideoFragment.KEY_LAYOUT_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        } else {
            tasks = savedInstanceState.getParcelableArrayList(KEY_TASKS)
            id = savedInstanceState.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
            layoutContent =
                    savedInstanceState.getInt(VideoFragment.KEY_LAYOUT_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        UserVisibleChangedBroadcastReceiver.sendUserVisibleBroadcast(context!!, !hidden, id)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_TASKS, tasks)
        outState.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_multimedia_player, container, false)
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(
            visibleChangedBroadcastReceiver,
            IntentFilter(UserVisibleChangedBroadcastReceiver.INTENT_ACTION_VISIBLE)
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isVisible) {
            startTask()
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(visibleChangedBroadcastReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerListener = null
    }

    override fun onCompletion(action: Int, message: String?) {
        Log.d(TAG, "onCompletion: action $action, $message")
        if (mediaIndex == tasks!!.size) {
            playerListener?.onLoopCompletion()
        }
        if (isVisible) {
            startTask()
        }
    }

    override fun onError(action: Int, message: String?) {
        Log.d(TAG, "onError: action $action, $message")
        val position = if (mediaIndex == 0) tasks!!.size - 1 else mediaIndex - 1
        playerListener?.onError(position, tasks!![position], action, message)
        if (isVisible) {
            startTask()
        }
    }

    override fun onPrepared() {
        playerListener?.onPrepared(this)
    }

    override fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return animationCallback?.animation(transit, enter, nextAnim)
    }

    override fun setVolume(volumePercent: Int) {
        mediaFragment?.setVolume(volumePercent)
    }

    protected fun startTask() {
        if (tasks == null || tasks!!.size == 0) {
            return
        }

        if (mediaIndex > tasks!!.size - 1) {
            mediaIndex = 0
        }
        openMediaFragment()
    }

    private fun openMediaFragment() {
        val task = tasks!![mediaIndex]
        @Task.Companion.Action val action = task.action

        try {
            when (action) {
                Task.ACTION_VIDEO -> mediaFragment = VideoFragment.newInstance(id, task.getFileUri(), layoutContent)

                Task.ACTION_IMAGE -> mediaFragment = ImageFragment.newInstance(task.getFileUri(), task.playtime)

                Task.ACTION_YOUTUBE -> mediaFragment = YoutubeFragment.newInstance(task.url, false, id)

                Task.ACTION_CUSTOM -> {
                    val clz = Class.forName(task.className)
                    val bundle = task.bundle ?: Bundle()
                    bundle.putInt(MediaFragment.KEY_PLAY_TIME, task.playtime)
                    mediaFragment = clz.newInstance() as MediaFragment?
                    mediaFragment?.arguments = bundle
                }
            }
            if (mediaFragment != null) {
                childFragmentManager.beginTransaction().replace(R.id.media_container, mediaFragment!!).commit()
                playerListener?.onChange(mediaIndex, task)
                mediaIndex++
            } else {
                mediaIndex++
                onError(action, "Media Fragment is null $task")
            }
        } catch (e: Exception) {
            mediaIndex++
            onError(action, "open Media Fragment failed $task")
        }
    }


    interface PlayerListener {
        fun onChange(position: Int, task: Task)

        fun onError(position: Int, task: Task, action: Int, message: String?)

        fun onPrepared(playerFragment: MultimediaPlayerFragment)

        fun onLoopCompletion()
    }
}
