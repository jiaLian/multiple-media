package com.goodjia.multiplemedia.fragment.component

import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.goodjia.multiplemedia.R
import com.goodjia.multiplemedia.Task
import com.goodjia.multiplemedia.setResizeImage

class ImageFragment : PlayTimeMediaFragment() {

    companion object {
        @JvmOverloads
        @JvmStatic
        fun newInstance(
            uri: Uri,
            playtime: Int = Task.DEFAULT_PLAYTIME,
            showLoadingIcon: Boolean = true,
            showFailureIcon: Boolean = true
        ) = ImageFragment().apply {
            arguments = bundle(uri, playtime, showLoadingIcon, showFailureIcon)
        }

        @JvmOverloads
        @JvmStatic
        fun bundle(
            uri: Uri,
            playtime: Int = Task.DEFAULT_PLAYTIME,
            showLoadingIcon: Boolean = true,
            showFailureIcon: Boolean = true
        ) = Bundle().apply {
            putParcelable(KEY_URI, uri)
            putInt(KEY_PLAY_TIME, playtime)
            putBoolean(KEY_SHOW_LOADING_ICON, showLoadingIcon)
            putBoolean(KEY_SHOW_FAILURE_ICON, showFailureIcon)
        }
    }

    private val draweeView: SimpleDraweeView by lazy {
        val hierarchy = GenericDraweeHierarchyBuilder(resources).apply {
            actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
            progressBarImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
            failureImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
            if (showLoadingIcon) setProgressBarImage(R.drawable.ic_loading)
            if (showFailureIcon) setFailureImage(R.drawable.ic_failure)
        }.build()
        SimpleDraweeView(context, hierarchy)
    }
    private var showLoadingIcon = true
    private var showFailureIcon = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            showLoadingIcon = it.getBoolean(KEY_SHOW_LOADING_ICON)
            showFailureIcon = it.getBoolean(KEY_SHOW_FAILURE_ICON)
        } ?: arguments?.let {
            showLoadingIcon = it.getBoolean(KEY_SHOW_LOADING_ICON)
            showFailureIcon = it.getBoolean(KEY_SHOW_FAILURE_ICON)
        }
        if (!Fresco.hasBeenInitialized()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) Fresco.initialize(
                context, ImagePipelineConfig.newBuilder(context).setDownsampleEnabled(true).build()
            )
            else Fresco.initialize(context)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_SHOW_FAILURE_ICON, showFailureIcon)
        outState.putBoolean(KEY_SHOW_LOADING_ICON, showLoadingIcon)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        draweeView.setBackgroundColor(Color.TRANSPARENT)
        return draweeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val params = draweeView.layoutParams as FrameLayout.LayoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        draweeView.setResizeImage(uri)
    }
}
