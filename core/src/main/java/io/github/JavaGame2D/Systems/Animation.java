package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.SpriteData;

public class Animation {
    public boolean looping = true;
    public int framesPerSecond = 30;
    private final SpriteData[] animationFrames;
    int pixelsToUnit = 900;

    public Animation(SpriteData[] animationFrames) {
        this.animationFrames = animationFrames;
    }

    public SpriteData getFrameByNumber(int frameNumber){
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

    public SpriteData getFrameByDuration(float durationInMilliseconds){
        //if number out of bounds, then:
        float durationInSeconds = durationInMilliseconds/1000;
        int frameNumber = (int)(framesPerSecond*durationInSeconds);
        SpriteData animationFrame =  getFrameByNumber(frameNumber);
        animationFrame.pixelsPerUnit = this.pixelsToUnit;
        return animationFrame;
    }
}
