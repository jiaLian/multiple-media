package com.goodjia.multimedia.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.goodjia.multimedia.MediaController
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.fragment.component.ImageFragment
import com.goodjia.multimedia.fragment.component.MediaFragment
import com.goodjia.multimedia.fragment.component.VideoFragment
import com.goodjia.multimedia.fragment.component.VideoFragment.Companion.KEY_LAYOUT_CONTENT
import com.goodjia.multimedia.fragment.component.YoutubeFragment
import java.util.*


open class MultimediaPlayerFragment : BaseFragment(), MediaFragment.MediaCallback,
    MediaFragment.AnimationCallback,
    MediaController {

    companion object {
        private val TAG = MultimediaPlayerFragment::class.java.simpleName

        const val KEY_TASKS = "tasks"

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            tasks: List<Task>?,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT
        ) = MultimediaPlayerFragment().apply {
            val args = Bundle()
            if (tasks != null) {
                args.putParcelableArrayList(KEY_TASKS, ArrayList(tasks))
            }
            args.putInt(KEY_LAYOUT_CONTENT, layoutContent)
            arguments = args
        }
    }

    private var layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT

    lateinit var tasks: ArrayList<Task>
    private var customTask: Task? = null
    protected var mediaIndex = 0
    private var mediaFragment: MediaFragment? = null

    var playerListener: PlayerListener? = null
    var animationCallback: MediaFragment.AnimationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            tasks = arguments?.getParcelableArrayList(KEY_TASKS) ?: arrayListOf()
            layoutContent = arguments?.getInt(
                KEY_LAYOUT_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ) ?: ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            tasks = savedInstanceState.getParcelableArrayList(KEY_TASKS) ?: arrayListOf()
            layoutContent =
                savedInstanceState.getInt(
                    KEY_LAYOUT_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_TASKS, tasks)
        outState.putInt(KEY_LAYOUT_CONTENT, layoutContent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_multimedia_player, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mediaFragment ?: startTask()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            mediaFragment?.pause()
        } else {
            mediaFragment?.start()
        }
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playerListener = null
    }

    override fun onCompletion(action: Int, message: String?) {
        Log.d(TAG, "onCompletion: action $action, $message")

        val size = tasks?.size
        if (mediaIndex == size) {
            playerListener?.onLoopCompletion()
        }
        if (size == 1) {
            when (mediaFragment) {
                is VideoFragment -> (mediaFragment as VideoFragment).play()
                is YoutubeFragment -> (mediaFragment as YoutubeFragment).repeat()
            }
        } else {
            startTask()
        }
    }

    override fun onError(action: Int, message: String?) {
        Log.d(TAG, "onError: action $action, $message")
        val position = if (mediaIndex == 0) tasks.lastIndex else mediaIndex - 1
        val task = if (customTask == null) tasks[position] else customTask
        task?.errorSet?.add(System.currentTimeMillis())
        playerListener?.onError(if (customTask == null) position else -1, task, action, message)
        startTask()
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

    override fun start() {
        mediaFragment?.start()
    }

    override fun pause() {
        mediaFragment?.pause()
    }

    override fun stop() {
        mediaFragment?.stop()
    }

    protected fun startTask() {
        if (isHidden || tasks.isNullOrEmpty()) return


        if (mediaIndex > tasks.lastIndex) {
            mediaIndex = 0
        }
        openMediaFragment()
    }

    fun play(mediaIndex: Int) {
        stop()
        this.mediaIndex = mediaIndex
        openMediaFragment()
    }

    fun play(task: Task?) {
        task ?: return
        stop()
        openMediaFragment(task)
    }

    private fun openMediaFragment(task: Task? = null) {
        customTask = task
        val playTask = task ?: tasks[mediaIndex]
        @Task.Companion.Action val action = playTask.action
        try {
            val oldMediaFragment = mediaFragment
            when (action) {
                Task.ACTION_VIDEO -> mediaFragment =
                    VideoFragment.newInstance(playTask.getFileUri(), layoutContent)

                Task.ACTION_IMAGE -> mediaFragment =
                    ImageFragment.newInstance(playTask.getFileUri(), playTask.playtime)

                Task.ACTION_YOUTUBE -> mediaFragment =
                    YoutubeFragment.newInstance(playTask.url, false)

                Task.ACTION_CUSTOM -> {
                    playTask.className ?: return
                    val clz = Class.forName(playTask.className)
                    val bundle = playTask.bundle ?: Bundle()
                    bundle.putInt(MediaFragment.KEY_PLAY_TIME, playTask.playtime)
                    mediaFragment = clz.newInstance() as MediaFragment?
                    mediaFragment?.arguments = bundle
                }
            }
            if (mediaFragment != null) {
                oldMediaFragment?.pause()
                childFragmentManager.beginTransaction()
                    .replace(R.id.media_container, mediaFragment!!).commit()
                playerListener?.onChange(if (task == null) mediaIndex else -1, playTask)
                task ?: mediaIndex++
            } else {
                task ?: mediaIndex++
                onError(action, "Media Fragment is null $playTask")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            task ?: mediaIndex++
            onError(action, "open Media Fragment failed $playTask")
        }
    }


    interface PlayerListener {
        fun onChange(position: Int, task: Task)

        fun onError(position: Int, task: Task?, action: Int, message: String?)

        fun onPrepared(playerFragment: MultimediaPlayerFragment)

        fun onLoopCompletion()
    }
}
