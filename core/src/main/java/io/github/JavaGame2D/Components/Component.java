package io.github.JavaGame2D.Components;

public interface Component {
    public long getSignature();
    public Component makeCopy();
}
