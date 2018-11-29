package com.goodjia.multimedia

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager

/**
 * Created by Jia on 2017/8/15.
 */

abstract class UserVisibleChangedBroadcastReceiver : BroadcastReceiver() {
    companion object {
        const val INTENT_ACTION_VISIBLE = "com.coretronic.virtualstore.demo.uservisible"
        const val KEY_VISIBLE = "visible"
        const val KEY_ID = "id"
        @JvmStatic
        fun sendUserVisibleBroadcast(context: Context, isVisible: Boolean, id: String) {
            val intent = Intent(INTENT_ACTION_VISIBLE)
            intent.putExtra(KEY_VISIBLE, isVisible)
            intent.putExtra(KEY_ID, id)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }

    abstract fun onVisibleChanged(isVisible: Boolean, id: String)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == INTENT_ACTION_VISIBLE) {
            onVisibleChanged(intent.getBooleanExtra(KEY_VISIBLE, false), intent.getStringExtra(KEY_ID))
        }
    }
}
