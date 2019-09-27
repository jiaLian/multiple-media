package com.goodjia.multimedia.sample

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.fragment.BaseFragment
import com.goodjia.multimedia.fragment.MultimediaPlayerFragment
import com.goodjia.multimedia.fragment.component.MediaFragment
import com.labo.kaji.fragmentanimations.CubeAnimation
import kotlinx.android.synthetic.main.fragment_player_list.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.toast
import kotlin.random.Random

class PlayerListFragment : BaseFragment(), TaskAdapter.OnItemClickListener,
    MultimediaPlayerFragment.PlayerListener {
    companion object {
        val TAG = PlayerListFragment::class.java.simpleName!!
        const val KEY_TASKS = "tasks"
        fun newInstance(tasks: ArrayList<Task>): PlayerListFragment {
            val args = Bundle()
            args.putParcelableArrayList(KEY_TASKS, tasks)
            val fragment = PlayerListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var tasks: ArrayList<Task>? = null
    private var multimediaPlayerFragment: MultimediaPlayerFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasks = if (savedInstanceState == null) arguments!!.getParcelableArrayList(KEY_TASKS)
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
        return inflater.inflate(R.layout.fragment_player_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        multimediaPlayerFragment = MultimediaPlayerFragment.newInstance(tasks?.toList())
        multimediaPlayerFragment?.animationCallback = object : MediaFragment.AnimationCallback {
            override fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
                return CubeAnimation.create(
                    CubeAnimation.RIGHT,
                    enter,
                    300
                ).fading(0.3f, 1.0f)
            }
        }
        multimediaPlayerFragment?.playerListener = this
        loadRootFragment(R.id.playerListContainer, multimediaPlayerFragment)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = TaskAdapter(tasks, this)
        btnCustom.onClick {
            val random = Random.Default
            multimediaPlayerFragment?.play(tasks?.get(random.nextInt(tasks?.size ?: 0)))
        }
    }

    override fun onClicked(position: Int, task: Task?) {
        multimediaPlayerFragment?.play(position)
    }

    override fun onLoopCompletion() {
        Log.d(TAG, "onLoopCompletion")
    }

    override fun onPrepared(playerFragment: MultimediaPlayerFragment) {
        Log.d(TAG, "onPrepared")
    }

    override fun onChange(position: Int, task: Task) {
        Log.d(TAG, "onChange $position, task $task")
        recyclerView.scrollToPosition(position)
    }

    override fun onError(position: Int, task: Task?, action: Int, message: String?) {
        Log.d(TAG, "onError $position, task $task, error $message")
        toast("onError $position")
    }
}