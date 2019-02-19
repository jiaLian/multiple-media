[![Download](https://api.bintray.com/packages/jialian/goodJia/multimedia/images/download.svg) ](https://bintray.com/jialian/goodJia/multimedia/_latestVersion)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)

# Multiple Media Component
一個可以使用圖片、影片、Youtube來源及Custom fragment的播放器。
將圖片、影片、Youtube打包成一個fragment的方式進行播放器的輪播。

# 如何使用

**1. 项目下app的build.gradle中依赖：**
````gradle
implementation 'com.goodjia:multimedia:0.0.8'
````

**2. Task（工作任務），建立任務清單**
````kotlin
tasks = arrayListOf(
        Task(
            Task.ACTION_CUSTOM, playtime = 3,
            className = CustomTaskFragment::class.java.name,
            bundle = CustomTaskFragment.bundle("Custom 1")
        ),
        Task(
            Task.ACTION_IMAGE,
            "https://media.wired.com/photos/598e35994ab8482c0d6946e0/master/w_1164,c_limit/phonepicutres-TA.jpg",
            playtime = 3
        ),
        Task(
            Task.ACTION_CUSTOM,
            playtime = 10,
            className = CustomTaskFragment::class.java.name,
            bundle = CustomTaskFragment.bundle("Custom 2")
        ),
        Task(
            Task.ACTION_VIDEO,
            "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4"
        ),
        Task(Task.ACTION_YOUTUBE, "https://www.youtube.com/watch?v=kQ0WqJmqkLA")
        )
````

**3. Start MultimediaPlayerFragment**
````kotlin
//影片來源Match parent 
/*val multimediaPlayerFragment = 
               MultimediaPlayerFragment.newInstance(tasks, layoutContent = ViewGroup.LayoutParams.MATCH_PARENT)*/
//影片來源依據來源size置中
val multimediaPlayerFragment = 
               MultimediaPlayerFragment.newInstance(tasks)
                //animationCallback 切換動畫
                multimediaPlayerFragment?.animationCallback = object : MediaFragment.AnimationCallback {
                    override fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
                        return if (enter) CubeAnimation.create(CubeAnimation.RIGHT, enter, DURATION).fading(0.3f, 1.0f)
                        else MoveAnimation.create(MoveAnimation.RIGHT, enter, DURATION).fading(1.0f, 0.3f)
                    }
                }
                // Player listener
                multimediaPlayerFragment?.playerListener = object : MultimediaPlayerFragment.PlayerListener {
                    override fun onLoopCompletion() {
                        Log.d(TAG, "onLoopCompletion")
                    }

                    override fun onPrepared(playerFragment: MultimediaPlayerFragment) {
                        val volume = Random().nextInt(100)
                        Log.d(TAG, "onPrepared $volume")
                        playerFragment.setVolume(volume)
                    }

                    override fun onChange(position: Int, task: Task) {
                        Log.d(TAG, "onChange $position, task $task")
                    }

                    override fun onError(position: Int, task: Task, action: Int, message: String?) {
                        Log.d(TAG, "onError $position, task $task, error $message")
                    }
                }
                start(multimediaPlayerFragment)
````

**.. Start Youtube Fragment**
````kotlin
start(YoutubeFragment.newInstance("https://www.youtube.com/watch?v=IduYAx4ptNU"))
````

## LICENSE
````
Copyright 2018 Jia

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
````
