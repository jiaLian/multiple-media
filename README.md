[![com.goodjia:multiplemedia package in gj feed in Azure Artifacts](https://feeds.dev.azure.com/goodjialian/db3d441d-f9cb-441e-9edc-acab8f7bb4fa/_apis/public/Packaging/Feeds/911ee5c1-df21-4a56-a486-b41525ee9d42/Packages/044e8b05-3d7c-4abc-824f-647d5e8bcd6b/Badge)](https://dev.azure.com/goodjialian/lib/_packaging?_a=package&feed=911ee5c1-df21-4a56-a486-b41525ee9d42&package=044e8b05-3d7c-4abc-824f-647d5e8bcd6b&preferRelease=true)
# Multiple Media Component
The library can play photo, video, youtube and custom fragment.
It is fragment player.

## Demo

<img src="/demo/autoplay.gif" width="280px"/>       <img src="/demo/list.gif" width="280px"/>

# How to Use

**1 . root/build.gradle**
````gradle
allprojects {
    repositories {
        ...
        // Used to MultimediaPlayerPresentation class (optional)
        maven { url "https://s3.amazonaws.com/repo.commonsware.com" }
    }
}
````

**2. app/build.gradle**
````gradle
implementation 'com.goodjia:multiplemedia:{_latestVersion}'
or
// When you never use MultimediaPlayerPresentation class
implementation('com.goodjia:multiplemedia:{_latestVersion}') {
        exclude group: 'com.commonsware.cwac', module: 'presentation.x'
}
````

**3. Task, create task list**
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
            "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_10mb.mp4",
            repeatTimes = 2
        ),
        Task(Task.ACTION_YOUTUBE, "https://youtu.be/nSbCMxSaBaw",repeatTimes = 2)
        )
````
**4. MultimediaPlayerFragment**
````kotlin
//Video source size: Wrap content (origin center);Set repeatTimes or playtime
/*val multimediaPlayerFragment = 
               MultimediaPlayerFragment.newInstance(tasks, ViewGroup.LayoutParams.WRAP_CONTENT,repeatTimes = 3,playTime=10)*/
//Video source size: default fit center
val multimediaPlayerFragment = 
               MultimediaPlayerFragment.newInstance(tasks)
                //animationCallback: switch page animation
                multimediaPlayerFragment?.animationCallback = object : MediaFragment.AnimationCallback {
                    override fun animation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
//                        return if (enter) CubeAnimation.create(CubeAnimation.RIGHT, enter, DURATION).fading(0.3f, 1.0f)
//                        else MoveAnimation.create(MoveAnimation.RIGHT, enter, DURATION).fading(1.0f, 0.3f)

                            //use transaction animation object 
                            val ANIMATIONS = listOf(
                                       TransitionAnimation(
                                           TransitionAnimation.AnimationType.CUBE.name,
                                           TransitionAnimation.Direction.DOWN.name,
                                           500
                                       ),
                                       TransitionAnimation(
                                           TransitionAnimation.AnimationType.MOVE.name,
                                           TransitionAnimation.Direction.UP.name,
                                           800
                                       ),
                                       TransitionAnimation(
                                           TransitionAnimation.AnimationType.FLIP.name,
                                           TransitionAnimation.Direction.LEFT.name,
                                           300
                                       ),
                                       TransitionAnimation(
                                           TransitionAnimation.AnimationType.SIDES.name,
                                           TransitionAnimation.Direction.RIGHT.name,
                                           1200
                                       ),
                                       TransitionAnimation(
                                           TransitionAnimation.AnimationType.PUSH_PULL.name,
                                           TransitionAnimation.Direction.RIGHT.name,
                                           600
                                       ),
                                       TransitionAnimation(TransitionAnimation.AnimationType.NONE.name)
                                   )                           
                           
                            return ANIMATIONS[kotlin.random.Random.nextInt(ANIMATIONS.size)].getAnimation(
                                enter
                            ).apply {
                                Log.d(PlayerListFragment.TAG, "animation $this")
                            }
                    }
                }
                // Player listener
                multimediaPlayerFragment?.playerListener = object : MultimediaPlayerFragment.PlayerListener {
                    override fun onLoopCompletion(repeatCount: Int) {
                        Log.d(
                        TAG,
                        "onLoopCompletion $repeatCount, finished ${multimediaPlayerFragment?.isFinished}"
                        )
                    }

                    override fun onFinished() {
                        Log.d(TAG, "onFinished ${multimediaPlayerFragment?.isFinished}")
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
````
**5. Show MultimediaPlayerPresentation (Secondary Screen)**
````kotlin
(context?.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager?)?.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
            ?.get(0)?.let {
                multimediaPlayerPresentation = MultimediaPlayerPresentation.newInstance(
                    context!!,
                    it, tasks
                )
            }

        multimediaPlayerPresentation?.animationCallback =
            object : MediaFragment.AnimationCallback {
                override fun animation(
                    transit: Int,
                    enter: Boolean,
                    nextAnim: Int
                ): Animation? {
                    return ANIMATIONS[Random.nextInt(ANIMATIONS.size)].apply {
                        Log.d(TAG, "presentation animation $this")
                    }.getAnimation(enter)
                }
            }

        multimediaPlayerPresentation?.playerListener =
            object : MultimediaPlayerFragment.PlayerListener {
                override fun onLoopCompletion(repeatCount: Int) {
                    Log.d(TAG,
                    "presentation onLoopCompletion $repeatCount, finished ${multimediaPlayerFragment?.isFinished}"
                    )
                }

                override fun onFinished() {
                    Log.d(TAG, "presentation onFinished ${multimediaPlayerFragment?.isFinished}")
                }

                override fun onPrepared(playerFragment: MultimediaPlayerFragment) {
                    playerFragment.setVolume(0)
                }

                override fun onChange(position: Int, task: Task) {
                    Log.d(TAG, "presentation onChange $position, task $task")
                }

                override fun onError(
                    position: Int,
                    task: Task?,
                    action: Int,
                    message: String?
                ) {
                    Log.d(TAG, "presentation onError $position, task $task, error $message")
                }
            }
        multimediaPlayerPresentation?.show(fragmentManager!!, "secondary")
````

**6. Change Play other task**
````kotlin
val otherTask=Task()
...
multimediaPlayerFragment?.play(otherTask)
````

**7. Change play the position of task list**
````kotlin
multimediaPlayerFragment?.play(position)
````
**8. Check can support image/video local file with file name**
````kotlin
//return true is video format with file name (can support mp4)
isVideoFile(filename)
//return true is image format with file name (can support jpg and png)
isImageFile(filename)
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
