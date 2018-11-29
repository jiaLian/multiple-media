package com.goodjia.multimedia

import android.net.Uri
import android.os.Parcelable
import android.support.annotation.IntDef
import android.support.annotation.IntRange
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.Serializable

@Parcelize
data class Task @JvmOverloads constructor(
    @Action var action: Int = ACTION_UNKNOWN,
    var url: String,
    var filePath: String? = null,
    var description: String? = null,
    @IntRange(from = 0) var playtime: Int = DEFAULT_PLAYTIME
) : Parcelable, Serializable {

    companion object {
        const val ACTION_UNKNOWN = -1
        const val ACTION_VIDEO = 0
        const val ACTION_YOUTUBE = 1
        const val ACTION_IMAGE = 2

        @IntDef(ACTION_UNKNOWN, ACTION_VIDEO, ACTION_YOUTUBE, ACTION_IMAGE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Action

        const val DEFAULT_PLAYTIME = 5     //second
    }

    fun getFileUri(): Uri {
        if (filePath != null && !filePath.isNullOrEmpty()) {
            val file = File(filePath)
            if (file.isFile) {
                return Uri.fromFile(file)
            }

        }
        return Uri.parse(if (url.isNotEmpty()) url else "")
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is Task) {
            return false
        }
        return url == obj.url
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }
}