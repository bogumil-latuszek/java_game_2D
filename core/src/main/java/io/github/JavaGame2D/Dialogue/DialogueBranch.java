package io.github.JavaGame2D.Dialogue;

import java.util.List;

public class DialogueBranch {
    public List<Condition> conditions;          // all must be true to enter next node
    public String nextNodeID;
}
