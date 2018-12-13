package com.goodjia.multimedia.sample

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.fragment.BaseFragment
import com.goodjia.multimedia.fragment.MultimediaPlayerFragment
import com.goodjia.multimedia.fragment.component.MediaFragment
import com.goodjia.multimedia.fragment.component.YoutubeFragment
import com.labo.kaji.fragmentanimations.CubeAnimation
import com.labo.kaji.fragmentanimations.MoveAnimation
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadLargeFileListener
import com.liulishuo.filedownloader.FileDownloadQueueSet
import com.liulishuo.filedownloader.FileDownloader
import com.maning.mndialoglibrary.MProgressBarDialog
import kotlinx.android.synthetic.main.fragment_menu.*
import permissions.dispatcher.*
import java.io.File
import java.util.*

@RuntimePermissions
class MenuFragment : BaseFragment(), View.OnClickListener {
    companion object {
        val TAG = MenuFragment::class.java.simpleName!!
        fun newInstance() = MenuFragment()
        const val DURATION: Long = 500
    }

    private val tasks = arrayListOf(
        Task(
            Task.ACTION_IMAGE,
            "https://media.wired.com/photos/598e35994ab8482c0d6946e0/master/w_1164,c_limit/phonepicutres-TA.jpg"
        ),
        Task(
            Task.ACTION_VIDEO,
            "http://cowork.coretronic.com/virtualshelf/r/admin/v1/videofiles/1/20181108/1541648105968ZSL6BFM.mp4"
        ),
        Task(Task.ACTION_YOUTUBE, "https://www.youtube.com/watch?v=kQ0WqJmqkLA"),
        Task(
            Task.ACTION_VIDEO,
            "http://cowork.coretronic.com/virtualshelf/r/admin/v1/videofiles/1/20181108/1541648064414Z0JKTAN.mp4"
        ),
        Task(Task.ACTION_VIDEO, "http://techslides.com/demos/sample-videos/small.mp4"),
        Task(Task.ACTION_VIDEO, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"),
        Task(
            Task.ACTION_YOUTUBE,
            "https://www.youtube.com/watch?v=7gXB9VdYG7c"
        ),
        Task(Task.ACTION_IMAGE, "https://amazingpict.com/wp-content/uploads/2015/04/lake-view-sunset.jpg"),
        Task(
            Task.ACTION_IMAGE,
            "https://www.telegraph.co.uk/content/dam/Travel/2017/April/view-snowdon.jpg?imwidth=1400"
        ),
        Task(Task.ACTION_YOUTUBE, "https://www.youtube.com/watch?v=1igDc5jK6iY"),
        Task(Task.ACTION_IMAGE, "http://kb4images.com/images/picture/37509081-picture.jpg")
    )

    private val progressBarDialog: MProgressBarDialog by lazy {
        MProgressBarDialog.Builder(activity).build()
    }

    private var downloadTaskSet: MutableSet<Task> = HashSet()

    private var totalSource: Int = 0
    private var progress: Int = 0
    private var multimediaPlayerFragment: MultimediaPlayerFragment? = null
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnMultimediaPlayer -> {
                multimediaPlayerFragment =
                        MultimediaPlayerFragment.newInstance(tasks/*, layoutContent = ViewGroup.LayoutParams.WRAP_CONTENT*/)

                multimediaPlayerFragment?.animationCallback = object : MediaFragment.AnimationCallback {
                    override fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
                        return if (enter) CubeAnimation.create(CubeAnimation.RIGHT, enter, DURATION).fading(0.3f, 1.0f)
                        else MoveAnimation.create(MoveAnimation.RIGHT, enter, DURATION).fading(1.0f, 0.3f)
                    }
                }

                multimediaPlayerFragment?.playerListener = object : MultimediaPlayerFragment.PlayerListener {
                    override fun onLoopCompletion() {
                        Log.d(TAG, "onLoopCompletion")
                    }

                    override fun onPrepared(playerFragment: MultimediaPlayerFragment) {
                        val volume = Random().nextInt(100)
                        Log.d(TAG, "onPrepared $volume")
                        playerFragment.setVolume(volume)
                    }

                    override fun onChange(position: Int, task: Task) {
                        Log.d(TAG, "onChange $position, task $task")
                    }

                    override fun onError(position: Int, task: Task, action: Int, message: String) {
                        Log.d(TAG, "onError $position, task $task, error $message")
                    }
                }

                start(multimediaPlayerFragment)
            }
            R.id.btnYoutube -> start(YoutubeFragment.newInstance("https://www.youtube.com/watch?v=IduYAx4ptNU"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTasks()
        canDownloadWithPermissionCheck()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnMultimediaPlayer.setOnClickListener(this)
        btnYoutube.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FileDownloader.getImpl().pause(fileListener)
    }

    private fun initTasks() {
        var task: Task
        val size = tasks.size
        for (i in 0 until size) {
            task = tasks[i]
            if (task.action == Task.ACTION_VIDEO) {
                task.filePath = Environment.getExternalStorageDirectory().absolutePath + File.separatorChar +
                        activity!!.packageName + File.separatorChar + i
                downloadTaskSet.add(task)
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
        val finished = totalSource - downloadTaskSet.size
        progressBarDialog.showProgress(progress, getString(R.string.message_download, finished, totalSource))
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

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun canDownload() {
        Log.d(TAG, "canDownload: ")
        download()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onShowRationale(request: PermissionRequest) {
        request.proceed()
        Log.d(TAG, "onShowRationale: ")
    }

    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onPermissionDenied() {
        Toast.makeText(activity, "請先開啟讀寫權限", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onPermissionDenied: ")
    }

    @OnNeverAskAgain(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onNeverAskAgain() {
        Toast.makeText(activity, "請先開啟讀寫權限", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "onNeverAskAgain: ")
    }
}