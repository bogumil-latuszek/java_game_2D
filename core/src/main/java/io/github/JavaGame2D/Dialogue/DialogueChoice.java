package io.github.JavaGame2D.Dialogue;

import java.util.List;

public class DialogueChoice {
    public String text;
    public String nextNodeID;
    public List<Condition> conditions;          // optional, controls visibility
    public List<DialogueEffect> effects;     // optional, run on selection
}
