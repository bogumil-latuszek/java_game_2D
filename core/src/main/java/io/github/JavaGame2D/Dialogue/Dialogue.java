package io.github.JavaGame2D.Dialogue;

import java.util.List;
import java.util.Map;

public class Dialogue {
    public String id;
    public List<DialogueBranch> entryBranches;
    public String defaultStartNodeID;
    public Map<String, DialogueNode> nodes;
}
