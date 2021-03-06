package com.goodjia.multiplemedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import com.goodjia.multiplemedia.MediaController
import com.goodjia.multiplemedia.R
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.fragment.component.ImageFragment
import com.goodjia.multiplemedia.fragment.component.MediaFragment
import com.goodjia.multiplemedia.fragment.component.MediaFragment.Companion.KEY_PLAY_TIME
import com.goodjia.multiplemedia.fragment.component.MediaFragment.Companion.KEY_REPEAT_TIMES
import com.goodjia.multiplemedia.fragment.component.VideoFragment
import com.goodjia.multiplemedia.fragment.component.VideoFragment.Companion.KEY_LAYOUT_CONTENT
import com.goodjia.multiplemedia.fragment.component.YoutubeFragment
import com.goodjia.utility.Logger


open class MultimediaPlayerFragment : Fragment(), MediaFragment.MediaCallback,
    MediaFragment.AnimationCallback,
    MediaController {

    companion object {
        private val TAG = MultimediaPlayerFragment::class.java.simpleName
        const val KEY_TASKS = "tasks"
        const val KEY_VOLUME = "volume"

        @JvmStatic
        @JvmOverloads
        fun newInstance(
            tasks: List<Task>?,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            repeatTimes: Int? = null,
            playTime: Int? = null,
            volumePercent: Int? = null
        ) = MultimediaPlayerFragment().apply {
            arguments = bundle(tasks, layoutContent, repeatTimes, playTime, volumePercent)
        }

        @JvmStatic
        @JvmOverloads
        fun bundle(
            tasks: List<Task>?,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            repeatTimes: Int? = null,
            playTime: Int? = null,
            volumePercent: Int? = null
        ) = Bundle().apply {
            tasks?.let { putParcelableArrayList(KEY_TASKS, ArrayList(it)) }
            putInt(KEY_LAYOUT_CONTENT, layoutContent)
            putInt(KEY_PLAY_TIME, playTime ?: Int.MIN_VALUE)
            putInt(KEY_REPEAT_TIMES, repeatTimes ?: Int.MIN_VALUE)
            putInt(KEY_VOLUME, volumePercent ?: Int.MIN_VALUE)
        }
    }

    private var layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT

    lateinit var tasks: ArrayList<Task>
    private var customTask: Task? = null
    protected var mediaIndex = 0
    private var mediaFragment: MediaFragment? = null
    private var startTime: Long = 0
    private var repeatTimes: Int = Int.MIN_VALUE
    private var repeatCount: Int = 0
    private var resetPlayTime: Int = Int.MIN_VALUE
    private var playTime: Int = Int.MIN_VALUE
    private var volumePercent: Int = Int.MIN_VALUE
    var playerListener: PlayerListener? = null
    var animationCallback: MediaFragment.AnimationCallback? = null
    var isFinished = false
        private set(value) {
            field = value
            if (value) {
                playerListener?.onFinished(this)
            }
        }
    private val completionRunnable by lazy {
        Runnable {
            isFinished = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate: ")
        if (savedInstanceState == null) {
            tasks = arguments?.getParcelableArrayList(KEY_TASKS) ?: arrayListOf()
            layoutContent = arguments?.getInt(
                KEY_LAYOUT_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ) ?: ViewGroup.LayoutParams.MATCH_PARENT
            playTime = arguments?.getInt(KEY_PLAY_TIME) ?: Int.MIN_VALUE
            resetPlayTime = arguments?.getInt(KEY_PLAY_TIME) ?: Int.MIN_VALUE
            repeatTimes = arguments?.getInt(KEY_REPEAT_TIMES) ?: Int.MIN_VALUE
            volumePercent = arguments?.getInt(KEY_VOLUME) ?: Int.MIN_VALUE
        } else {
            tasks = savedInstanceState.getParcelableArrayList(KEY_TASKS) ?: arrayListOf()
            layoutContent =
                savedInstanceState.getInt(
                    KEY_LAYOUT_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            playTime = savedInstanceState.getInt(KEY_PLAY_TIME)
            resetPlayTime = savedInstanceState.getInt(KEY_PLAY_TIME)
            repeatTimes = savedInstanceState.getInt(KEY_REPEAT_TIMES)
            volumePercent = savedInstanceState.getInt(KEY_VOLUME)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_TASKS, tasks)
        outState.putInt(KEY_LAYOUT_CONTENT, layoutContent)
        outState.putInt(KEY_PLAY_TIME, playTime)
        outState.putInt(KEY_REPEAT_TIMES, repeatTimes)
        outState.putInt(KEY_VOLUME, volumePercent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Logger.d(TAG, "onCreateView: ")
        return inflater.inflate(R.layout.fragment_multimedia_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaFragment ?: startTask()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Logger.d(TAG, "onHiddenChanged: ")
        if (hidden) {
            mediaFragment?.pause()
            removePlaytime()
        } else {
            mediaFragment?.start()
            postPlaytime()
        }
    }

    override fun onResume() {
        super.onResume()
        Logger.d(TAG, "onResume: ")
        start()
    }

    override fun onPause() {
        super.onPause()
        Logger.d(TAG, "onPause: ")
        pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Logger.d(TAG, "onDestroyView: ")
        playerListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy: ")
    }

    override fun onCompletion(action: Int, message: String?) {
        Logger.d(TAG, "onCompletion: action $action, $message")
        checkLoopCompletion()
        if (tasks.size == 1) {
            when (mediaFragment) {
                is VideoFragment -> (mediaFragment as VideoFragment).play()
                is YoutubeFragment -> (mediaFragment as YoutubeFragment).reload()
                else -> mediaFragment?.repeat()
            }
        } else {
            startTask()
        }
    }

    override fun onError(action: Int, message: String?) {
        Logger.d(TAG, "onError: action $action, $message")
        val position = if (mediaIndex == 0) tasks.lastIndex else mediaIndex - 1
        val task = if (customTask == null) tasks[position] else customTask
        task?.errorSet?.add(System.currentTimeMillis())
        playerListener?.onError(
            this,
            if (customTask == null) position else -1,
            task,
            action,
            message
        )
        checkLoopCompletion()
    }

    override fun onPrepared() {
        playerListener?.onPrepared(this)
        if (volumePercent >= 0) {
            setVolume(volumePercent)
        }
    }

    override fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return animationCallback?.animation(transit, enter, nextAnim)
    }

    override fun setVolume(volumePercent: Int) {
        mediaFragment?.setVolume(volumePercent)
    }

    override fun start() {
        mediaFragment?.start()
        postPlaytime()
    }

    override fun pause() {
        mediaFragment?.pause()
        removePlaytime()
    }

    override fun stop() {
        mediaFragment?.stop()
        removePlaytime()
    }

    override fun repeat() {
        if (tasks.isNotEmpty()) {
            if (resetPlayTime > 0) {
                play(0)
            }
        } else {
            mediaFragment?.repeat()
        }
        isFinished = false
        repeatCount = 0
        playTime = resetPlayTime
        postPlaytime()
    }

    @JvmOverloads
    fun reset(tasks: List<Task>, repeatTimes: Int? = null, playTime: Int? = null) {
        this.tasks = ArrayList(tasks)
        this.repeatTimes = repeatTimes ?: Int.MIN_VALUE
        this.resetPlayTime = playTime ?: Int.MIN_VALUE
        repeat()
        if (resetPlayTime == Int.MIN_VALUE && (this.repeatTimes == Int.MIN_VALUE || this.repeatTimes > 0)) {
            play(0)
        }
    }

    protected fun startTask() {
        if (isHidden || tasks.isNullOrEmpty()) return
        if (mediaIndex > tasks.lastIndex) {
            mediaIndex = 0
        }
        openMediaFragment()
    }

    open fun next() {
        startTask()
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

    private fun checkLoopCompletion() {
        if (mediaIndex == tasks.size) {
            playerListener?.onLoopCompletion(this, ++repeatCount)
            if (playTime == Int.MIN_VALUE && !isFinished && repeatCount == if (repeatTimes > 0) repeatTimes else 1) {
                isFinished = true
            }
        }
    }

    private fun openMediaFragment(task: Task? = null) {
        customTask = task
        val playTask = task ?: tasks[mediaIndex]
        @Task.Companion.Action val action = playTask.action
        try {
            val oldMediaFragment = mediaFragment
            when (action) {
                Task.ACTION_VIDEO -> mediaFragment =
                    VideoFragment.newInstance(
                        playTask.getFileUri(),
                        layoutContent,
                        repeatTimes = playTask.repeatTimes
                    )

                Task.ACTION_IMAGE -> mediaFragment =
                    ImageFragment.newInstance(playTask.getFileUri(), playTask.playtime)

                Task.ACTION_YOUTUBE -> mediaFragment =
                    YoutubeFragment.newInstance(
                        playTask.url,
                        false,
                        repeatTimes = playTask.repeatTimes
                    )

                Task.ACTION_CUSTOM -> {
                    playTask.className ?: return
                    val clz = Class.forName(playTask.className)
                    val bundle = playTask.bundle ?: Bundle()
                    bundle.putInt(MediaFragment.KEY_PLAY_TIME, playTask.playtime)
                    mediaFragment = clz.newInstance() as MediaFragment?
                    mediaFragment?.arguments = bundle
                }
                Task.ACTION_UNKNOWN -> {
                    mediaFragment?.let {
                        childFragmentManager.beginTransaction().remove(it)
                        mediaFragment = null
                    }
                    playerListener?.onChange(this, if (task == null) mediaIndex else -1, playTask)
                    return
                }
            }
            if (mediaFragment != null) {
                oldMediaFragment?.pause()
                childFragmentManager.beginTransaction()
                    .replace(R.id.media_container, mediaFragment!!).commit()
                playerListener?.onChange(this, if (task == null) mediaIndex else -1, playTask)
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

    private fun postPlaytime() {
        if (playTime == Int.MIN_VALUE || isFinished) return
        startTime = System.currentTimeMillis()
        view?.removeCallbacks(completionRunnable)
        view?.postDelayed(completionRunnable, playTime * 1000L)
    }

    private fun removePlaytime() {
        if (playTime == Int.MIN_VALUE || isFinished) return
        val processTime = (System.currentTimeMillis() - startTime) / 1000
        playTime = if (processTime < playTime) playTime - processTime.toInt() else 0
        view?.removeCallbacks(completionRunnable)
    }

    interface PlayerListener {
        fun onChange(player: MultimediaPlayerFragment, position: Int, task: Task)

        fun onError(
            player: MultimediaPlayerFragment,
            position: Int,
            task: Task?,
            action: Int,
            message: String?
        )

        fun onPrepared(player: MultimediaPlayerFragment)

        fun onLoopCompletion(player: MultimediaPlayerFragment, repeatCount: Int)

        fun onFinished(player: MultimediaPlayerFragment)
    }
}
