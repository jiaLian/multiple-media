package com.goodjia.multiplemedia.sample

import android.os.Bundle
import android.view.animation.Animation
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.fragment.MultimediaPlayerFragment
import com.goodjia.multiplemedia.fragment.component.MediaFragment
import com.goodjia.utility.Logger
import kotlin.random.Random

class MyPlayerFragment : MultimediaPlayerFragment() {
    companion object {
        val TAG = MyPlayerFragment::class.simpleName
    }

    init {
        animationCallback =
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
                    }.getAnimation(enter)
                }
            }
        playerListener =
            object : MultimediaPlayerFragment.PlayerListener {
                override fun onPrepared(player: MultimediaPlayerFragment) {
                    //                            val volume = Random().nextInt(100)
                    //                            Log.d(TAG, "onPrepared $volume")
                    //                        playerFragment.setVolume(volume)
                }

                override fun onLoopCompletion(
                    player: MultimediaPlayerFragment,
                    repeatCount: Int
                ) {
                    Logger.d(
                        TAG,
                        "onLoopCompletion $repeatCount, finished ${player?.isFinished}"
                    )
                }

                override fun onFinished(player: MultimediaPlayerFragment) {
                    Logger.d(TAG, "onFinished ${player?.isFinished}")
                }

                override fun onChange(
                    player: MultimediaPlayerFragment,
                    position: Int,
                    task: Task
                ) {
                    Logger.d(
                        TAG,
                        "onChange $position, task $task, finished ${player?.isFinished}"
                    )
                }

                override fun onError(
                    player: MultimediaPlayerFragment,
                    position: Int,
                    task: Task?,
                    action: Int,
                    message: String?
                ) {
                    Logger.d(TAG, "onError $position, task $task, error $message")
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (tasks.isEmpty()) { tasks = ArrayList(TASKS.shuffled()) }
    }

    override fun onError(action: Int, message: String?) {
        super.onError(action, message)
        next()
    }
}