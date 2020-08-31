package com.goodjia.multimedia.fragment.component

import android.os.Bundle
import android.view.View
import com.goodjia.multimedia.Task

abstract class PlayTimeMediaFragment : MediaFragment() {
    private val completionRunnable = Runnable {
        mediaCallback?.onCompletion(
            if (javaClass.simpleName == ImageFragment.javaClass.simpleName) Task.ACTION_IMAGE else Task.ACTION_CUSTOM,
            javaClass.simpleName
        )
    }
    private var startTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            playtime = arguments?.getInt(KEY_PLAY_TIME) ?: Task.DEFAULT_PLAYTIME
        } else {
            playtime = savedInstanceState.getInt(KEY_PLAY_TIME, Task.DEFAULT_PLAYTIME)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_PLAY_TIME, playtime)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        start()
    }

    override fun onDestroyView() {
        stop()
        super.onDestroyView()
    }

    override fun start() {
        startTime = System.currentTimeMillis()
        view?.removeCallbacks(completionRunnable)
        view?.postDelayed(completionRunnable, playtime * 1000L)
    }

    override fun stop() {
        val processTime = (System.currentTimeMillis() - startTime) / 1000
        playtime = if (processTime < playtime) playtime - processTime.toInt() else 0
        view?.removeCallbacks(completionRunnable)
    }

    override fun pause() {
        stop()
    }
}
