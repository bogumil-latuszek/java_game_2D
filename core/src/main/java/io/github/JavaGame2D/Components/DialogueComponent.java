package io.github.JavaGame2D.Components;

public class DialogueComponent implements Component{
    public int dialogueID = -1;

    @Override
    public Component makeCopy() {
        DialogueComponent copy = new DialogueComponent();
        copy.dialogueID = this.dialogueID;
        return copy;
    }
}
