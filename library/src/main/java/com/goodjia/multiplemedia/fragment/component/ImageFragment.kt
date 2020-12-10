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
        fun newInstance(uri: Uri, playtime: Int = Task.DEFAULT_PLAYTIME) = ImageFragment().apply {
            arguments = bundle(uri, playtime)
        }

        @JvmOverloads
        @JvmStatic
        fun bundle(uri: Uri, playtime: Int = Task.DEFAULT_PLAYTIME) = Bundle().apply {
            putParcelable(KEY_URI, uri)
            putInt(KEY_PLAY_TIME, playtime)
        }
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
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) Fresco.initialize(
                context, ImagePipelineConfig.newBuilder(context).setDownsampleEnabled(true).build()
            )
            else Fresco.initialize(context)
        }
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
