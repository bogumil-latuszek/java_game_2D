package io.github.JavaGame2D.Systems;

import com.badlogic.gdx.graphics.Texture;

public class Animation {
    private boolean looping = true;
    private final Texture[] animationFrames;

    public Animation(Texture[] animationFrames) {
        this.animationFrames = animationFrames;
    }

    public Texture getFrame(int frameNumber){
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
}
