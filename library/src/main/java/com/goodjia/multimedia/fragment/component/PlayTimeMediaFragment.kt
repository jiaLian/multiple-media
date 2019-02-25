package com.goodjia.multimedia.fragment.component

import android.os.Bundle
import android.view.View
import com.goodjia.multimedia.Task
import kotlin.random.Random

abstract class PlayTimeMediaFragment : MediaFragment() {
    private val completionRunnable: Runnable = Runnable {
        mediaCallback?.onCompletion(
            if (javaClass.simpleName == ImageFragment.javaClass.simpleName) Task.ACTION_IMAGE else Task.ACTION_CUSTOM,
            javaClass.simpleName
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            playtime = arguments!!.getInt(KEY_PLAY_TIME)
        } else {
            playtime = savedInstanceState.getInt(KEY_PLAY_TIME)
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
        this.view?.postDelayed(completionRunnable, playtime * 1000L)
    }

    override fun stop() {
        view?.removeCallbacks(completionRunnable)
    }

    override fun pause() {
        stop()
    }
}
