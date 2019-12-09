package com.goodjia.multimedia

import android.net.Uri
import androidx.annotation.IntRange
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.util.regex.Pattern

interface MediaController {
    fun setVolume(@IntRange(from = 0, to = 100) volumePercent: Int)
    fun start()
    fun pause()
    fun stop()
}

fun SimpleDraweeView.setResizeImage(uri: Uri?) {
    setController(uri)
}

fun SimpleDraweeView.setResizeImage(url: String) {
    setController(Uri.parse(url))
}

private fun SimpleDraweeView.setController(uri: Uri?) {
    post {
        if (width == 0 || height == 0) {
            return@post
        }
        val request = ImageRequestBuilder.newBuilderWithSource(uri)
            .setResizeOptions(ResizeOptions(width, height))
            .build()

        val controller = Fresco.newDraweeControllerBuilder()
            .setOldController(controller)
            .setImageRequest(request)
            .build()
        setController(controller)
    }
}

fun String.extractVideoIdFromUrl(): String {
    val VIDEO_ID_REGEX =
        arrayOf("\\?vi?=([^&]*)", "watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)")
    val youTubeLinkWithoutProtocolAndDomain = youTubeLinkWithoutProtocolAndDomain(this)
    for (regex in VIDEO_ID_REGEX) {
        val compiledPattern = Pattern.compile(regex)
        val matcher = compiledPattern.matcher(youTubeLinkWithoutProtocolAndDomain)
        if (matcher.find()) {
            return matcher.group(1)
        }
    }
    return ""
}

fun youTubeLinkWithoutProtocolAndDomain(url: String): String {
    val YOU_TUBE_URL_REG_EX = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/"
    val compiledPattern = Pattern.compile(YOU_TUBE_URL_REG_EX)
    val matcher = compiledPattern.matcher(url)
    return if (matcher.find()) {
        url.replace(matcher.group(), "")
    } else url

}