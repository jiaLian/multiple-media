package com.goodjia.multimedia.sample

import android.Manifest
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.TransitionAnimation
import com.goodjia.multimedia.fragment.BaseFragment
import com.goodjia.multimedia.fragment.MultimediaPlayerFragment
import com.goodjia.multimedia.fragment.component.MediaFragment
import com.goodjia.multimedia.fragment.component.YoutubeFragment
import com.goodjia.multimedia.presentation.MultimediaPlayerPresentation
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadLargeFileListener
import com.liulishuo.filedownloader.FileDownloadQueueSet
import com.liulishuo.filedownloader.FileDownloader
import com.maning.mndialoglibrary.MProgressBarDialog
import kotlinx.android.synthetic.main.fragment_menu.*
import org.jetbrains.anko.displayManager
import permissions.dispatcher.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@RuntimePermissions
class MenuFragment : BaseFragment(), View.OnClickListener {
    companion object {
        val TAG = MenuFragment::class.java.simpleName
        fun newInstance() = MenuFragment()
        const val DURATION: Long = 500
        val ANIMATIONS = listOf(
            TransitionAnimation(
                TransitionAnimation.AnimationType.CUBE.name,
                TransitionAnimation.Direction.DOWN.name,
                500
            ),
            TransitionAnimation(
                TransitionAnimation.AnimationType.MOVE.name,
                TransitionAnimation.Direction.UP.name,
                800
            ),
            TransitionAnimation(
                TransitionAnimation.AnimationType.FLIP.name,
                TransitionAnimation.Direction.LEFT.name,
                300
            ),
            TransitionAnimation(
                TransitionAnimation.AnimationType.SIDES.name,
                TransitionAnimation.Direction.RIGHT.name,
                1200
            ),
            TransitionAnimation(
                TransitionAnimation.AnimationType.PUSH_PULL.name,
                TransitionAnimation.Direction.RIGHT.name,
                600
            ),
            TransitionAnimation(TransitionAnimation.AnimationType.NONE.name)
        )
    }

    private val tasks = arrayListOf(
        Task(
            Task.ACTION_CUSTOM, playtime = 3,
            className = CustomTaskFragment::class.java.name,
            bundle = CustomTaskFragment.bundle("Custom 1")
        ),
        Task(
            Task.ACTION_IMAGE,
            "http://cowork.coretronic.com/virtualshelf/r/admin/v1/blob/virtualshelf/org-1/image/20200707/1594085333703ZAMI.JPG",
            playtime = 10
        ),
        Task(
            Task.ACTION_IMAGE,
            "https://images.freeimages.com/images/large-previews/adf/sun-burst-1478549.jpg",
            playtime = 20
        ),
        Task(Task.ACTION_VIDEO, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"),
        Task(
            Task.ACTION_YOUTUBE,
            "https://youtu.be/033JQZV8cJU"
        ),
        Task(
            Task.ACTION_CUSTOM, playtime = 15,
            //            Error custom class sample
            className = MenuFragment::class.java.name,
            bundle = CustomTaskFragment.bundle("Custom Error")
        )
    )
    private val presentationTasks = arrayListOf(
        Task(
            Task.ACTION_IMAGE,
            "https://www.sripanwa.com/wp-content/uploads/view-2/7-View-Gallery-Sri-Panwa-Luxury-Hotel-Phuket-Resort-3000x1688.jpg",
            playtime = 5
        ),
        Task(
            Task.ACTION_IMAGE,
            "http://cowork.coretronic.com/virtualshelf/r/admin/v1/blob/virtualshelf/org-1/image/20200707/1594085333703ZAMI.JPG",
            playtime = 3
        ),
        Task(
            Task.ACTION_CUSTOM, playtime = 10,
            className = CustomTaskFragment::class.java.name,
            bundle = CustomTaskFragment.bundle("Presentation")
        ),
        Task(Task.ACTION_VIDEO, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
    )

    private val progressBarDialog: MProgressBarDialog by lazy {
        MProgressBarDialog.Builder(activity).build()
    }

    private var downloadTaskSet: MutableSet<Task> = HashSet()

    private var totalSource: Int = 0
    private var progress: Int = 0
    private var multimediaPlayerFragment: MultimediaPlayerFragment? = null
    private var multimediaPlayerPresentation: MultimediaPlayerPresentation? = null

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMultimediaPlayer -> {
                openPlayerFragment()
                showPresentation()
            }
            R.id.btnYoutube -> start(YoutubeFragment.newInstance("https://youtu.be/033JQZV8cJU"))
            R.id.btnPlayerList -> start(PlayerListFragment.newInstance(tasks))
        }
    }

    private fun openPlayerFragment() {
        multimediaPlayerFragment =
            MultimediaPlayerFragment.newInstance(tasks/*, ViewGroup.LayoutParams.WRAP_CONTENT*/)

        multimediaPlayerFragment?.animationCallback =
            object : MediaFragment.AnimationCallback {
                override fun animation(
                    transit: Int,
                    enter: Boolean,
                    nextAnim: Int
                ): Animation? {
                    //                        return if (enter) CubeAnimation.create(CubeAnimation.RIGHT, enter, DURATION).fading(0.3f, 1.0f)
                    //                        else MoveAnimation.create(MoveAnimation.RIGHT, enter, DURATION).fading(1.0f, 0.3f)

                    //use transaction animation object
                    return ANIMATIONS[Random.nextInt(ANIMATIONS.size)].apply {
                        Log.d(TAG, "animation $this")
                    }.getAnimation(enter)
                }
            }

        multimediaPlayerFragment?.playerListener =
            object : MultimediaPlayerFragment.PlayerListener {
                override fun onLoopCompletion() {
                    Log.d(TAG, "onLoopCompletion")
                }

                override fun onPrepared(playerFragment: MultimediaPlayerFragment) {
                    //                            val volume = Random().nextInt(100)
                    //                            Log.d(TAG, "onPrepared $volume")
                    //                        playerFragment.setVolume(volume)
                }

                override fun onChange(position: Int, task: Task) {
                    Log.d(TAG, "onChange $position, task $task")
                }

                override fun onError(
                    position: Int,
                    task: Task?,
                    action: Int,
                    message: String?
                ) {
                    Log.d(TAG, "onError $position, task $task, error $message")
                }
            }
        start(multimediaPlayerFragment)
    }

    private fun showPresentation() {
        try {
            context?.displayManager?.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
                ?.get(0)?.let {
                    multimediaPlayerPresentation = MultimediaPlayerPresentation.newInstance(
                        context!!,
                        it, presentationTasks
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        multimediaPlayerPresentation?.animationCallback =
            object : MediaFragment.AnimationCallback {
                override fun animation(
                    transit: Int,
                    enter: Boolean,
                    nextAnim: Int
                ): Animation? {
                    return ANIMATIONS[Random.nextInt(ANIMATIONS.size)].apply {
                        Log.d(TAG, "presentation animation $this")
                    }.getAnimation(enter)
                }
            }

        multimediaPlayerPresentation?.playerListener =
            object : MultimediaPlayerFragment.PlayerListener {
                override fun onLoopCompletion() {
                    Log.d(TAG, "presentation onLoopCompletion")
                }

                override fun onPrepared(playerFragment: MultimediaPlayerFragment) {
                    playerFragment.setVolume(0)
                }

                override fun onChange(position: Int, task: Task) {
                    Log.d(TAG, "presentation onChange $position, task $task")
                }

                override fun onError(
                    position: Int,
                    task: Task?,
                    action: Int,
                    message: String?
                ) {
                    Log.d(TAG, "presentation onError $position, task $task, error $message")
                }
            }
        fragmentManager?.let { multimediaPlayerPresentation?.show(it, "secondary") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTasks(tasks, presentationTasks)
        canDownloadWithPermissionCheck()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnMultimediaPlayer.setOnClickListener(this)
        btnYoutube.setOnClickListener(this)
        btnPlayerList.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FileDownloader.getImpl().pause(fileListener)
    }

    private fun initTasks(vararg taskList: ArrayList<Task>) {
        taskList.forEachIndexed { index, arrayList ->
            var task: Task
            val size = arrayList.size
            for (i in 0 until size) {
                task = arrayList[i]
                if (task.action == Task.ACTION_VIDEO) {
                    task.filePath =
                        Environment.getExternalStorageDirectory().absolutePath + File.separatorChar +
                                activity?.packageName + File.separatorChar + index + i
                    downloadTaskSet.add(task)
                }
            }
        }
    }

    private fun download() {
        FileDownloader.setup(activity)
        totalSource = downloadTaskSet.size
        setProgress()
        val queueSet = FileDownloadQueueSet(fileListener)
        val baseDownloadTasks = ArrayList<BaseDownloadTask>()
        var baseDownloadTask: BaseDownloadTask
        for (task in downloadTaskSet) {
            baseDownloadTask = FileDownloader.getImpl().create(task.url).setPath(task.filePath)
            baseDownloadTasks.add(baseDownloadTask)
        }
        queueSet.setAutoRetryTimes(3)
        queueSet.downloadTogether(baseDownloadTasks).start()
    }

    private fun checkDownloadStatus() {
        if (downloadTaskSet.size == 0) {
            progressBarDialog.dismiss()
        }
    }

    private fun setProgress() {
        if (downloadTaskSet.size == 0) return
        val finished = totalSource - downloadTaskSet.size
        progressBarDialog.showProgress(
            progress,
            getString(R.string.message_download, finished, totalSource)
        )
    }

    private val fileListener = object : FileDownloadLargeFileListener() {

        override fun pending(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
            Log.d(TAG, "fileListener pending: " + task.path)
            Log.d(TAG, "fileListener pending: " + task.url)
        }

        override fun progress(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
            progress = (soFarBytes * 100 / totalBytes).toInt()
            setProgress()
            Log.d(TAG, "fileListener progress: " + task.path + " " + progress)
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
            Log.d(TAG, "fileListener paused: " + task.path)
        }

        override fun completed(task: BaseDownloadTask) {
            Log.d(TAG, "fileListener completed: " + task.path)
            downloadTaskSet.remove(Task(url = task.url))
            setProgress()
            checkDownloadStatus()
        }

        override fun error(task: BaseDownloadTask, e: Throwable) {
            Log.d(TAG, "fileListener error: " + task.path)
            downloadTaskSet.remove(Task(url = task.url))
            setProgress()
            checkDownloadStatus()
        }

        override fun warn(task: BaseDownloadTask) {
            Log.d(TAG, "fileListener warn: " + task.path)
        }
    }

    @NeedsPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun canDownload() {
        Log.d(TAG, "canDownload: ")
        download()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onShowRationale(request: PermissionRequest) {
        request.proceed()
        Log.d(TAG, "onShowRationale: ")
    }

    @OnPermissionDenied(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionDenied() {
        Toast.makeText(activity, "請先開啟讀寫權限", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onPermissionDenied: ")
    }

    @OnNeverAskAgain(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onNeverAskAgain() {
        Toast.makeText(activity, "請先開啟讀寫權限", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onNeverAskAgain: ")
    }
}