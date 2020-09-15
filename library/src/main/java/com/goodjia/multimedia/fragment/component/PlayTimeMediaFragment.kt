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
        view?.postDelayed(completionRunnable, playTime * 1000L)
    }

    override fun repeat() {
        super.repeat()
        start()
    }

    override fun stop() {
        val processTime = (System.currentTimeMillis() - startTime) / 1000
        playTime = if (processTime < playTime) playTime - processTime.toInt() else 0
        view?.removeCallbacks(completionRunnable)
    }

    override fun pause() {
        stop()
    }
}
