package com.goodjia.multiplemedia.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.fragment.MultimediaPlayerFragment
import com.goodjia.utility.Logger
import kotlinx.android.synthetic.main.fragment_player_list.*
import kotlin.random.Random

class PlayerListFragment : Fragment(R.layout.fragment_player_list),
    TaskAdapter.OnItemClickListener {
    companion object {
        val TAG = PlayerListFragment::class.java.simpleName
    }

    private var multimediaPlayerFragment: MultimediaPlayerFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.d(TAG, "onViewCreated")
        recyclerView.layoutManager =
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        recyclerView.adapter = TaskAdapter(TASKS, this)
        btnCustom.setOnClickListener {
            val random = Random.Default
            multimediaPlayerFragment?.play(TASKS[random.nextInt(TASKS.size)])
        }
        multimediaPlayerFragment =
            childFragmentManager.getPrimaryMultiMediaPlayerFragment(R.id.navHostFragment)
    }

    override fun onClicked(position: Int, task: Task?) {
        multimediaPlayerFragment?.play(position)
    }
}