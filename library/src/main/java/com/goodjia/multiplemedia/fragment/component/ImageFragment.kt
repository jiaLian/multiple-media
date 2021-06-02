package com.goodjia.multiplemedia.fragment.component

import android.graphics.Color
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.image.ImageInfo
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.goodjia.multiplemedia.R
import com.goodjia.multiplemedia.Task
import com.goodjia.utility.Logger

class ImageFragment : PlayTimeMediaFragment() {

    companion object {
        val TAG = ImageFragment::class.simpleName

        @JvmOverloads
        @JvmStatic
        fun newInstance(
            uri: Uri,
            playtime: Int = Task.DEFAULT_PLAYTIME,
            showLoadingIcon: Boolean = true,
            showFailureIcon: Boolean = true,
            id: Long? = null,
            name: String? = null
        ) = ImageFragment().apply {
            arguments = bundle(uri, playtime, showLoadingIcon, showFailureIcon, id, name)
        }

        @JvmOverloads
        @JvmStatic
        fun bundle(
            uri: Uri,
            playtime: Int = Task.DEFAULT_PLAYTIME,
            showLoadingIcon: Boolean = true,
            showFailureIcon: Boolean = true,
            id: Long? = null,
            name: String? = null
        ) = Bundle().apply {
            putParcelable(KEY_URI, uri)
            putInt(KEY_PLAY_TIME, playtime)
            putBoolean(KEY_SHOW_LOADING_ICON, showLoadingIcon)
            putBoolean(KEY_SHOW_FAILURE_ICON, showFailureIcon)
            id?.let {
                putLong(KEY_ID, it)
            }
            putString(KEY_NAME, name)
        }
    }

    private var isSet = false
    private var draweeView: SimpleDraweeView? = null
    private var showLoadingIcon = true
    private var showFailureIcon = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val hierarchy = GenericDraweeHierarchyBuilder(resources).apply {
            actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
            progressBarImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
            failureImageScaleType = ScalingUtils.ScaleType.CENTER_INSIDE
            if (showLoadingIcon) setProgressBarImage(R.drawable.ic_loading)
            if (showFailureIcon) setFailureImage(R.drawable.ic_failure)
        }.build()
        draweeView = SimpleDraweeView(context, hierarchy).apply {
            setBackgroundColor(Color.TRANSPARENT)
        }
        return draweeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Logger.d(TAG, "onViewCreated: ")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun start() {
        super.start()
        Logger.d(TAG, "start: ")
        if (!isSet) {
            Logger.d(TAG, "assign image $uri")
            (draweeView?.layoutParams as? FrameLayout.LayoutParams)?.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            draweeView?.resize()
        }
    }

    private fun SimpleDraweeView.resize() {
        post {
            if (width == 0 || height == 0) {
                return@post
            }
            val request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(ResizeOptions(width, height))
                .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(object : BaseControllerListener<ImageInfo>() {
                    override fun onFailure(id: String?, throwable: Throwable?) {
                        val retry = getTag(R.id.retry) as Boolean?
                        if (retry != true) {
                            setTag(R.id.retry, true)
                            setImageURI(uri, null)
                        }
                    }

                    override fun onFinalImageSet(
                        id: String?,
                        imageInfo: ImageInfo?,
                        animatable: Animatable?
                    ) {
                        Logger.d(TAG, "onFinalImageSet  $uri")
                        isSet = true
                    }

                    override fun onRelease(id: String?) {
                        Logger.d(TAG, "onRelease  $uri")
                        isSet = false
                    }
                })
                .setOldController(controller)
                .setImageRequest(request)
                .build()
            setController(controller)
        }
    }
}
