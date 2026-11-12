package io.github.JavaGame2D.Dialogue;

import java.util.List;
import java.util.Map;

public class DialogueNode {
    public String id;
    public List<String> conditions;
    public String text;
    public List<String> effects;

    // next node routing
    public List<DialogueChoice> choices;     // null/empty = auto pathing
    public List<DialogueBranch> branches;     // potential next nodes
    public String defaultNextNodeID;         // null for terminal node
}
