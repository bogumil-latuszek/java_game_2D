package io.github.JavaGame2D.Systems;

import io.github.JavaGame2D.Dialogue.Dialogue;

import java.util.Optional;

public class DialogueSystem {

    Optional<Dialogue> currentDialogue; // empty when no dialogue happening currently
    FileSystem fileSystem;

    public DialogueSystem() {
        this.currentDialogue = Optional.empty();
    }

    public void update(float deltaTimeInSeconds){
        // if dialogue is present: render node, check if we need to proceed to the next one

    }

    private void startDialogue(String dialogueID){
        this.currentDialogue = fileSystem.loadDialogue(dialogueID);
    }

    private void renderNextNode(){
        //
    }

}
