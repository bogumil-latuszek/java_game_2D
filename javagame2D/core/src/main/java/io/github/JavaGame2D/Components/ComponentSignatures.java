package io.github.JavaGame2D.Components;

public final class ComponentSignatures {
    public static final long TRANSFORM      = 1L << 0;
    public static final long DRAWABLE       = 1L << 1;
    public static final long PHYSICAL_BODY  = 1L << 2;
    public static final long COLLIDER       = 1L << 3;
    public static final long ANIMATION      = 1L << 4;
    public static final long TELEPORTER     = 1L << 5;
    public static final long HEALTH         = 1L << 6;
}
