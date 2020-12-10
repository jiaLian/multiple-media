package com.goodjia.multiplemedia

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.io.Serializable

@Parcelize
data class Task @JvmOverloads constructor(
    @Action var action: Int = ACTION_UNKNOWN,
    var url: String? = null,
    var filePath: String? = null,
    var description: String? = null,
    @IntRange(from = 0) var playtime: Int = DEFAULT_PLAYTIME,
    @IntRange(from = 1) var repeatTimes: Int = 1,
    val className: String? = null,
    val bundle: Bundle? = null,
    val errorSet: LinkedHashSet<Long> = linkedSetOf()
) : Parcelable, Serializable {

    companion object {
        const val ACTION_UNKNOWN = -1
        const val ACTION_VIDEO = 0
        const val ACTION_YOUTUBE = 1
        const val ACTION_IMAGE = 2
        const val ACTION_CUSTOM = 3

        @IntDef(ACTION_UNKNOWN, ACTION_VIDEO, ACTION_YOUTUBE, ACTION_IMAGE, ACTION_CUSTOM)
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
        return Uri.parse(if (url?.isNotEmpty() == true) url else "")
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is Task) {
            return false
        }
        return if (url != null) url == obj.url else className != null && className == obj.className && bundle == obj.bundle
    }

    override fun hashCode(): Int {
        return if (url != null) url.hashCode() else className.hashCode() + bundle.hashCode()
    }
}