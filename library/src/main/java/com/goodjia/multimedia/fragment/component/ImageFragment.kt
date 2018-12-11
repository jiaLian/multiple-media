package com.goodjia.multimedia.fragment.component

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.goodjia.multimedia.R
import com.goodjia.multimedia.Task
import com.goodjia.multimedia.setResizeImage

class ImageFragment : MediaFragment() {

    companion object {
        fun newInstance(uri: Uri, delaySecond: Int = Task.DEFAULT_PLAYTIME): ImageFragment {
            val args = Bundle()
            args.putParcelable(KEY_URI, uri)
            args.putInt(KEY_DELAY_SECOND, delaySecond)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val completionRunnable: Runnable = Runnable {
        mediaCallback?.onCompletion(Task.ACTION_IMAGE, uri?.toString() ?: "")
    }

    private val draweeView: SimpleDraweeView by lazy {
        val hierarchy = GenericDraweeHierarchyBuilder(resources)
            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
            .setProgressBarImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
            .setFailureImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE)
            .setProgressBarImage(R.drawable.ic_loading)
            .setFailureImage(R.drawable.ic_failure)
            .build()

        SimpleDraweeView(context, hierarchy)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(context!!)
        }
        if (savedInstanceState == null) {
            uri = arguments!!.getParcelable(KEY_URI)
            delaySecond = arguments!!.getInt(KEY_DELAY_SECOND)
        } else {
            uri = savedInstanceState.getParcelable(KEY_URI)
            delaySecond = savedInstanceState.getInt(KEY_DELAY_SECOND)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_URI, uri)
        outState.putInt(KEY_DELAY_SECOND, delaySecond)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        draweeView.setBackgroundColor(Color.TRANSPARENT)
        return draweeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = draweeView.layoutParams as FrameLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        draweeView.setResizeImage(uri)
        getView()!!.postDelayed(completionRunnable, delaySecond * 1000L)
    }

    override fun onDestroyView() {
        view?.removeCallbacks(completionRunnable)
        super.onDestroyView()
    }
}
