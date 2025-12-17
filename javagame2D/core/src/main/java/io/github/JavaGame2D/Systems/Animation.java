package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;

public class Animation {
    public boolean looping = true;
    public int framesPerSecond = 30;
    private final Texture[] animationFrames;

    public Animation(Texture[] animationFrames) {
        this.animationFrames = animationFrames;
    }

    public Texture getFrameByNumber(int frameNumber){
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

    public Texture getFrameByDuration(float durationInMilliseconds){
        //if number out of bounds, then:
        float durationInSeconds = durationInMilliseconds/1000;
        int frameNumber = (int)(framesPerSecond*durationInSeconds);
        return  getFrameByNumber(frameNumber);
    }
}
