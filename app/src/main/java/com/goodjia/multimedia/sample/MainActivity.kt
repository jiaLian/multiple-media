package com.goodjia.multimedia.sample

import android.os.Bundle
import android.view.WindowManager
import me.yokeyword.fragmentation.SupportActivity


class MainActivity : SupportActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        loadRootFragment(R.id.container, MenuFragment.newInstance())
    }
}
