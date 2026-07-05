package io.github.JavaGame2D.LevelEditorCommands;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class CommandHistory {
    private Deque<ICommand> undoStack;
    private Deque<ICommand> redoStack;

    private final int STACK_LIMIT = 100;

    public CommandHistory() {
        this.undoStack = new ArrayDeque<>(STACK_LIMIT);
        this.redoStack = new ArrayDeque<>(STACK_LIMIT);
    }

    private boolean canUndo(){ return !undoStack.isEmpty(); }
    private boolean canRedo(){ return !undoStack.isEmpty(); }

    public void executeCommand(ICommand command)
    {
        // 1. Execute the command
        command.execute();

        // 2. Push to undo stack
        undoStack.addFirst(command);

        // 3. Clear redo stack (new action invalidates redo history)
        redoStack.clear();

        // 4. Limit history size
        if (undoStack.size() == STACK_LIMIT)
        {
            // Remove oldest command
            undoStack.removeLast();
        }
    }

    public void Undo()
    {
        if (!canUndo()) return;

        ICommand command = undoStack.removeFirst();
        command.undo();

        // Push to redo stack
        redoStack.addFirst(command);
    }

    public void Redo()
    {
        if (!canRedo()) return;

        ICommand command = redoStack.removeFirst();
        command.execute();

        // Push to undo stack
        undoStack.addFirst(command);
    }

    public void Clear()
    {
        undoStack.clear();
        redoStack.clear();
    }

    public String GetUndoDescription()
    {
        return !undoStack.isEmpty() ? undoStack.getFirst().getDescription() : "";
    }

    public String GetRedoDescription()
    {
        return !redoStack.isEmpty() ? redoStack.getFirst().getDescription() : "";
    }
}
