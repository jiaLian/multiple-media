package com.goodjia.multiplemedia.sample

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.TransitionAnimation
import com.goodjia.multiplemedia.fragment.MultimediaPlayerFragment

val ANIMATIONS = listOf(
    TransitionAnimation(
        TransitionAnimation.AnimationType.CUBE.name,
        TransitionAnimation.Direction.DOWN.name,
        500
    ),
    TransitionAnimation(
        TransitionAnimation.AnimationType.MOVE.name,
        TransitionAnimation.Direction.UP.name,
        800
    ),
    TransitionAnimation(
        TransitionAnimation.AnimationType.FLIP.name,
        TransitionAnimation.Direction.LEFT.name,
        300
    ),
    TransitionAnimation(
        TransitionAnimation.AnimationType.SIDES.name,
        TransitionAnimation.Direction.RIGHT.name,
        1200
    ),
    TransitionAnimation(
        TransitionAnimation.AnimationType.PUSH_PULL.name,
        TransitionAnimation.Direction.RIGHT.name,
        600
    ),
    TransitionAnimation(TransitionAnimation.AnimationType.NONE.name)
)
val TASKS = arrayListOf(
    Task(
        Task.ACTION_CUSTOM, playtime = 3,
        className = CustomTaskFragment::class.java.name,
        bundle = CustomTaskFragment.bundle("Custom 1")
    ),
    Task(
        Task.ACTION_IMAGE,
        "http://cowork.coretronic.com/pcloudplus-api/r/admin/v1/blob/adplayer2/org-1/0/20201120/1605859970149ZC7B.jpg",
        playtime = 10
    ),
    Task(
        Task.ACTION_IMAGE,
        "https://images.freeimages.com/images/large-previews/adf/sun-burst-1478549.jpg",
        playtime = 10
    ),
    Task(
        Task.ACTION_VIDEO,
        "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
        repeatTimes = 2
    ),
    Task(
        Task.ACTION_YOUTUBE,
        "https://youtu.be/033JQZV8cJU", repeatTimes = 1
    ),
    Task(
        Task.ACTION_CUSTOM, playtime = 5,
//                    Error custom class sample
        className = MenuFragment::class.java.name,
        bundle = CustomTaskFragment.bundle("Custom Error")
    )
)
val PRESENTATION_TASKS = arrayListOf(
    Task(
        Task.ACTION_IMAGE,
        "https://www.sripanwa.com/wp-content/uploads/view-2/7-View-Gallery-Sri-Panwa-Luxury-Hotel-Phuket-Resort-3000x1688.jpg",
        playtime = 5
    ),
    Task(
        Task.ACTION_CUSTOM, playtime = 10,
        className = CustomTaskFragment::class.java.name,
        bundle = CustomTaskFragment.bundle("Presentation")
    ),
    Task(Task.ACTION_VIDEO, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
)

fun FragmentManager.getPrimaryMultiMediaPlayerFragment(@IdRes navHostId: Int) =
    getNavHostFragment(navHostId)?.childFragmentManager?.primaryNavigationFragment as MultimediaPlayerFragment?


fun FragmentManager.getNavHostFragment(@IdRes navHostId: Int) =
    findFragmentById(navHostId) as NavHostFragment?