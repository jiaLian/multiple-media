package com.goodjia.multiplemedia.sample

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.fragment.MultimediaPlayerFragment
import com.goodjia.multiplemedia.fragment.component.MediaFragment
import com.goodjia.multiplemedia.fragment.component.YoutubeFragment
import com.goodjia.multiplemedia.presentation.MultimediaPlayerPresentation
import com.goodjia.utility.Logger
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadLargeFileListener
import com.liulishuo.filedownloader.FileDownloadQueueSet
import com.liulishuo.filedownloader.FileDownloader
import com.maning.mndialoglibrary.MProgressBarDialog
import kotlinx.android.synthetic.main.fragment_menu.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class MenuFragment : Fragment(R.layout.fragment_menu) {
    companion object {
        val TAG = MenuFragment::class.java.simpleName
    }

    private val progressDialog: MProgressBarDialog by lazy {
        MProgressBarDialog.Builder(activity)
            .setStyle(MProgressBarDialog.MProgressBarDialogStyle_Circle)
            .build()
    }

    private var downloadTaskSet: MutableSet<Task> = HashSet()

    private var totalSource: Int = 0
    private var progress: Int = 0

    private var multimediaPlayerPresentation: MultimediaPlayerPresentation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate")
        initTasks(TASKS, PRESENTATION_TASKS)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.d(TAG, "onViewCreated")
        btnMultimediaPlayer.setOnClickListener {
            openPlayerFragment()
        }
        btnDualMultimediaPlayer.setOnClickListener {
            findNavController().navigate(
                R.id.action_menuFragment_to_dualPlayerFragment
            )
        }
        btnYoutube.setOnClickListener {
            findNavController().navigate(
                R.id.action_menuFragment_to_youtubeFragment,
                YoutubeFragment.bundle("https://youtu.be/033JQZV8cJU")
            )
        }
        btnPlayerList.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_playerListFragment)
        }
        btnPresentation.isEnabled = !displays.isNullOrEmpty()
        btnPresentation.setOnClickListener {
            if (multimediaPlayerPresentation == null) {
                showPresentation()
            } else {
                multimediaPlayerPresentation?.dismiss()
                multimediaPlayerPresentation = null
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            multimediaPlayerPresentation?.dismiss()
            multimediaPlayerPresentation = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Logger.d(TAG, "onDestroyView")
        FileDownloader.getImpl().pause(fileListener)
    }

    private fun initTasks(vararg taskList: ArrayList<Task>) {
        taskList.forEach { arrayList ->
            arrayList.forEach { task ->
                if (task.action == Task.ACTION_VIDEO || task.action == Task.ACTION_IMAGE) {
                    task.filePath =
                        requireContext().getExternalFilesDir(null)?.absolutePath + File.separatorChar +
                                task.url?.substringAfterLast(
                                    "/"
                                )
                    downloadTaskSet.add(task)
                }
            }
        }
        download()
    }

    private fun openPlayerFragment() {
        findNavController().navigate(
            R.id.action_menuFragment_to_myPlayerFragment,
            MultimediaPlayerFragment.bundle(TASKS, repeatTimes = 2, preload = true)
        )
    }

    private val displays: List<Display>? by lazy {
        (context?.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager?)?.getDisplays(
            DisplayManager.DISPLAY_CATEGORY_PRESENTATION
        )?.toList()
    }

    private fun showPresentation() {
        if (displays.isNullOrEmpty()) return
        displays?.get(0)?.let {
            Logger.d(TAG, "Presentation")
            multimediaPlayerPresentation = MultimediaPlayerPresentation.newInstance(
                requireContext(),
                it, PRESENTATION_TASKS.shuffled()
            )
        }
        multimediaPlayerPresentation?.animationCallback =
            object : MediaFragment.AnimationCallback {
                override fun animation(
                    transit: Int,
                    enter: Boolean,
                    nextAnim: Int
                ): Animation? {
                    return ANIMATIONS[Random.nextInt(ANIMATIONS.size)].apply {
                        Logger.d(TAG, "presentation animation $this")
                    }.getAnimation(enter)
                }
            }

        multimediaPlayerPresentation?.playerListener =
            object : MultimediaPlayerFragment.PlayerListener {

                override fun onPrepared(player: MultimediaPlayerFragment) {
                    player.setVolume(0)
                }

                override fun onLoopCompletion(player: MultimediaPlayerFragment, repeatCount: Int) {
                    Logger.d(TAG, "presentation onLoopCompletion $repeatCount")
                }

                override fun onFinished(player: MultimediaPlayerFragment) {
                    Logger.d(TAG, "presentation onFinished")
                }

                override fun onChange(player: MultimediaPlayerFragment, position: Int, task: Task) {
                    Logger.d(TAG, "presentation onChange $position, task $task")
                }

                override fun onError(
                    player: MultimediaPlayerFragment,
                    position: Int,
                    task: Task?,
                    action: Int,
                    message: String?
                ) {
                    player.next()
                    Logger.d(TAG, "presentation onError $position, task $task, error $message")
                }
            }
        multimediaPlayerPresentation?.show(
            requireActivity().supportFragmentManager,
            MultimediaPlayerPresentation::class.simpleName
        )
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
            progressDialog.dismiss()
        }
    }

    private fun setProgress() {
        if (downloadTaskSet.size == 0) return
        val finished = totalSource - downloadTaskSet.size
        try {
            progressDialog.showProgress(
                progress,
                getString(R.string.message_download, finished, totalSource)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val fileListener = object : FileDownloadLargeFileListener() {
        override fun pending(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
            Logger.d(TAG, "fileListener pending: " + task.path)
            Logger.d(TAG, "fileListener pending: " + task.url)
        }

        override fun progress(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
            progress = (soFarBytes * 100 / totalBytes).toInt()
            setProgress()
            Logger.d(TAG, "fileListener progress: " + task.path + " " + progress)
        }

        override fun paused(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
            Logger.d(TAG, "fileListener paused: " + task.path)
        }

        override fun completed(task: BaseDownloadTask) {
            downloadTaskSet.remove(Task(filePath = task.targetFilePath))
            Logger.d(
                TAG,
                "fileListener completed: ${task.targetFilePath}, ${task.url}, ${downloadTaskSet.size}"
            )
            setProgress()
            checkDownloadStatus()
        }

        override fun error(task: BaseDownloadTask, e: Throwable) {
            Logger.d(TAG, "fileListener error: " + task.targetFilePath)
            downloadTaskSet.remove(Task(filePath = task.targetFilePath))
            setProgress()
            checkDownloadStatus()
        }

        override fun warn(task: BaseDownloadTask) {
            Logger.d(TAG, "fileListener warn: " + task.path)
        }
    }
}