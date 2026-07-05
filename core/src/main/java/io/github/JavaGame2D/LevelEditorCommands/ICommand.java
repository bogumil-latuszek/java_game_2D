package io.github.JavaGame2D.LevelEditorCommands;

public interface ICommand {
    public void execute();
    public void undo();

    public String getDescription();
}
