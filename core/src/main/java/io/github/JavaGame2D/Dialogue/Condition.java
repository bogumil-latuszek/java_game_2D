package io.github.JavaGame2D.Dialogue;

import java.util.Map;

public class Condition {
    public String type;              // "flagSet", "playerHasItem", etc.
    public Map<String, Object> params;  // type-specific
}
