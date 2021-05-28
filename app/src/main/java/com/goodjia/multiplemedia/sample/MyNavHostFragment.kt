package com.goodjia.multiplemedia.sample

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.goodjia.multiplemedia.fragment.component.MediaFragment
import com.goodjia.utility.Logger

class MyNavHostFragment : NavHostFragment(), MediaFragment.MediaCallback {
    companion object {
        val TAG = MyNavHostFragment::class.simpleName
    }

    override fun onPlayLog(
        id: Long?,
        name: String?,
        className: String?,
        startTime: Long,
        endTime: Long,
        pauseTime: Int
    ) {
    }

    override fun onCompletion(action: Int, message: String?) {
        Logger.d(TAG, "onCompletion: ")
        if (findNavController().currentDestination?.id == R.id.youtubeFragment) {
            findNavController().navigateUp()
        }
    }

    override fun onError(action: Int, message: String?) {
        Logger.d(TAG, "onError: ")
        findNavController().popBackStack()
    }

    override fun onPrepared() {
        Logger.d(TAG, "onPrepared: ")
    }
}