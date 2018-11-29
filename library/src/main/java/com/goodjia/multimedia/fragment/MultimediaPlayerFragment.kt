package com.goodjia.multimedia.fragment

import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.UserVisibleChangedBroadcastReceiver
import com.goodjia.multimedia.fragment.component.ImageFragment
import com.goodjia.multimedia.fragment.component.MediaFragment
import com.goodjia.multimedia.fragment.component.VideoFragment
import com.goodjia.multimedia.fragment.component.YoutubeFragment
import me.yokeyword.fragmentation.SupportFragment
import java.util.*


class MultimediaPlayerFragment : BaseFragment(), MediaFragment.MediaCallback, MediaFragment.AnimationCallback {

    companion object {
        private val TAG = MultimediaPlayerFragment::class.java.simpleName

        const val KEY_TASKS = "tasks"
        const val DEFAULT_ID = "only_one"

        @JvmStatic
        @JvmOverloads
        fun newInstance(tasks: List<Task>?, id: String = DEFAULT_ID): MultimediaPlayerFragment {
            val args = Bundle()
            args.putString(UserVisibleChangedBroadcastReceiver.KEY_ID, id)
            if (tasks != null) {
                args.putParcelableArrayList(KEY_TASKS, ArrayList(tasks))
            }
            val fragment = MultimediaPlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    protected lateinit var id: String

    var tasks: ArrayList<Task>? = null
    protected var mediaIndex = 0


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
        } else {
            tasks = savedInstanceState.getParcelableArrayList(KEY_TASKS)
            id = savedInstanceState.getString(UserVisibleChangedBroadcastReceiver.KEY_ID)
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

    override fun onCompletion(action: Int, message: String) {
        Log.d(TAG, "onCompletion: action $action, $message")
        if (isVisible) {
            startTask()
        }
    }

    override fun onError(action: Int, message: String) {
        Log.d(TAG, "onError: action $action, $message")
        val position = if (mediaIndex == 0) tasks!!.size - 1 else mediaIndex - 1
        playerListener?.onError(position, tasks!![position], action, message)
        if (isVisible) {
            startTask()
        }
    }

    override fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return animationCallback?.animation(transit, enter, nextAnim)
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
        var fragment: SupportFragment? = null
        val task = tasks!![mediaIndex]
        @Task.Companion.Action val action = task.action

        when (action) {
            Task.ACTION_VIDEO -> {
                Log.d(TAG, "ACTION_VIDEO: " + task.toString())
                Log.d(TAG, "ACTION_VIDEO: " + task.getFileUri())
                fragment = VideoFragment.newInstance(id, task.getFileUri())
            }
            Task.ACTION_IMAGE -> fragment = ImageFragment.newInstance(task.getFileUri(), task.playtime)
            Task.ACTION_YOUTUBE -> fragment = YoutubeFragment.newInstance(task.url, false, id)
        }

        if (fragment != null) {
            try {
                childFragmentManager.beginTransaction().replace(R.id.media_container, fragment).commit()
                playerListener?.onChange(mediaIndex, task)
                mediaIndex++
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    interface PlayerListener {
        fun onChange(position: Int, task: Task)

        fun onError(position: Int, task: Task, action: Int, message: String)
    }
}
