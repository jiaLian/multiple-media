package com.goodjia.multimedia;

import android.view.animation.Animation;

import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.labo.kaji.fragmentanimations.MoveAnimation;
import com.labo.kaji.fragmentanimations.PushPullAnimation;
import com.labo.kaji.fragmentanimations.SidesAnimation;

public class TransitionAnimation {
    private String type;
    private String direction;
    private long duration;

    public TransitionAnimation() {
    }

    public TransitionAnimation(String type, String direction, int duration) {
        this.type = type;
        this.direction = direction;
        this.duration = duration;
    }

    public TransitionAnimation(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Animation getAnimation(boolean enter) {
        int direction = 0;
        try {
            direction = Direction.valueOf(this.direction.toUpperCase()).value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (AnimationType.valueOf(type.toUpperCase())) {
            case MOVE:
                return MoveAnimation.create(direction, enter, duration);
            case CUBE:
                return CubeAnimation.create(direction, enter, duration);
            case FLIP:
                return FlipAnimation.create(direction, enter, duration);
            case PUSHPULL:
                return PushPullAnimation.create(direction, enter, duration);
            case SIDES:
                return SidesAnimation.create(direction, enter, duration);
            case NONE:
                return null;
        }
        return null;
    }

    @Override
    public String toString() {
        return "TransitionAnimation{" +
                "type='" + type + '\'' +
                ", direction='" + direction + '\'' +
                ", duration=" + duration +
                '}';
    }

    public enum AnimationType {
        NONE,
        MOVE,
        CUBE,
        FLIP,
        PUSHPULL,
        SIDES
    }


    public enum Direction {
        UP(1), DOWN(2), LEFT(3), RIGHT(4);

        Direction(int value) {
            this.value = value;
        }

        private int value;

        public int getValue() {
            return value;
        }
    }
}
