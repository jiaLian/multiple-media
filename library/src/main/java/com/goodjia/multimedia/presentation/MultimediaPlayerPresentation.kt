package com.goodjia.multimedia.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.commonsware.cwac.preso.PresentationFragment
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.fragment.MultimediaPlayerFragment
import com.goodjia.multimedia.fragment.MultimediaPlayerFragment.Companion.KEY_TASKS
import com.goodjia.multimedia.fragment.component.MediaFragment
import com.goodjia.multimedia.fragment.component.VideoFragment.Companion.KEY_LAYOUT_CONTENT
import java.util.*


class MultimediaPlayerPresentation : PresentationFragment() {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun newInstance(
            context: Context, display: Display,
            tasks: List<Task>?,
            layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT
        ) = MultimediaPlayerPresentation().apply {
            setDisplay(context, display)
            val args = Bundle()
            if (tasks != null) {
                args.putParcelableArrayList(KEY_TASKS, ArrayList(tasks))
            }
            args.putInt(KEY_LAYOUT_CONTENT, layoutContent)
            arguments = args
        }
    }

    private var playerFragment: MultimediaPlayerFragment? = null

    var playerListener: MultimediaPlayerFragment.PlayerListener? = null
    var animationCallback: MediaFragment.AnimationCallback? = null

    private var tasks: ArrayList<Task>? = null
    private var layoutContent: Int = ViewGroup.LayoutParams.MATCH_PARENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            tasks = arguments!!.getParcelableArrayList(KEY_TASKS)
            layoutContent = arguments!!.getInt(
                KEY_LAYOUT_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        } else {
            tasks = savedInstanceState.getParcelableArrayList(KEY_TASKS)
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
        val view = inflater.inflate(R.layout.fragment_multimedia_player, container, false)
        view.setBackgroundColor(Color.BLACK)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        playerFragment = MultimediaPlayerFragment.newInstance(tasks, layoutContent)
        playerFragment?.playerListener = playerListener
        playerFragment?.animationCallback = animationCallback
        childFragmentManager.beginTransaction().replace(R.id.media_container, playerFragment!!)
            .commit()
    }
}
