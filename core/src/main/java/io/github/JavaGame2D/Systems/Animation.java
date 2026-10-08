package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.AnimationFrame;

public class Animation {
    public boolean looping = true;
    public int framesPerSecond = 30;
    private final AnimationFrame[] animationFrames;
    int pixelsToUnit = 900;

    public Animation(AnimationFrame[] animationFrames) {
        this.animationFrames = animationFrames;
    }

    public AnimationFrame getFrameByNumber(int frameNumber){
        //if number out of bounds, then:
        if(frameNumber >= animationFrames.length){
            if (this.looping){
                frameNumber = frameNumber%animationFrames.length;
            }
            else {
                frameNumber = animationFrames.length-1;
            }
        }
        return animationFrames[frameNumber];
    }

    public AnimationFrame getFrameByDuration(float durationInMilliseconds){
        //if number out of bounds, then:
        float durationInSeconds = durationInMilliseconds/1000;
        int frameNumber = (int)(framesPerSecond*durationInSeconds);
        AnimationFrame animationFrame = getFrameByNumber(frameNumber);
        animationFrame.pixelsPerUnit = this.pixelsToUnit;
        return animationFrame;
    }
}
