package com.goodjia.multimedia.sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.fragment.BaseFragment
import com.goodjia.multimedia.fragment.MultimediaPlayerFragment

class DualPlayerFragment : BaseFragment() {
    companion object {
        val TAG = DualPlayerFragment::class.java.simpleName
        private const val KEY_TASKS = "tasks"
        fun newInstance(tasks: ArrayList<Task>): DualPlayerFragment {
            val args = Bundle()
            args.putParcelableArrayList(KEY_TASKS, tasks)
            val fragment = DualPlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var tasks: ArrayList<Task>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasks = if (savedInstanceState == null) arguments?.getParcelableArrayList(KEY_TASKS)
        else savedInstanceState.getParcelableArrayList(KEY_TASKS)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_TASKS, tasks)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dual_player, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadRootFragment(
            R.id.container1,
            MultimediaPlayerFragment.newInstance(tasks?.shuffled()).apply {
                playerListener =
                    object : MultimediaPlayerFragment.PlayerListener {
                        override fun onPrepared(player: MultimediaPlayerFragment) {}

                        override fun onLoopCompletion(
                            player: MultimediaPlayerFragment,
                            repeatCount: Int
                        ) {
                            Log.d(
                                TAG,
                                "onLoopCompletion container1 $repeatCount"
                            )
                        }

                        override fun onFinished(player: MultimediaPlayerFragment) {
                            Log.d(TAG, "onFinished container1 ")
                        }

                        override fun onChange(
                            player: MultimediaPlayerFragment,
                            position: Int,
                            task: Task
                        ) {
                            Log.d(
                                MenuFragment.TAG,
                                "onChange container1 $position, task $task"
                            )
                        }

                        override fun onError(
                            player: MultimediaPlayerFragment,
                            position: Int,
                            task: Task?,
                            action: Int,
                            message: String?
                        ) {
                            Log.d(
                                MenuFragment.TAG,
                                "onError container1 $position, task $task, error $message"
                            )
                        }
                    }
            })
        loadRootFragment(
            R.id.container2,
            MultimediaPlayerFragment.newInstance(tasks?.shuffled()).apply {
                playerListener =
                    object : MultimediaPlayerFragment.PlayerListener {
                        override fun onPrepared(player: MultimediaPlayerFragment) {}

                        override fun onLoopCompletion(
                            player: MultimediaPlayerFragment,
                            repeatCount: Int
                        ) {
                            Log.d(
                                TAG,
                                "onLoopCompletion container2 $repeatCount"
                            )
                        }

                        override fun onFinished(player: MultimediaPlayerFragment) {
                            Log.d(TAG, "onFinished container2 ")
                        }

                        override fun onChange(
                            player: MultimediaPlayerFragment,
                            position: Int,
                            task: Task
                        ) {
                            Log.d(
                                MenuFragment.TAG,
                                "onChange container2 $position, task $task"
                            )
                        }

                        override fun onError(
                            player: MultimediaPlayerFragment,
                            position: Int,
                            task: Task?,
                            action: Int,
                            message: String?
                        ) {
                            Log.d(
                                MenuFragment.TAG,
                                "onError container2 $position, task $task, error $message"
                            )
                        }
                    }
            })
    }
}