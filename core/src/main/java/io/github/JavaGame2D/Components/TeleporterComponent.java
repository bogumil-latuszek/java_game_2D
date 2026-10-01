package io.github.JavaGame2D.Components;

public class TeleporterComponent implements Component{
    public int targetLevelID = 0;

    public static final long SIGNATURE = ComponentSignatures.register(TeleporterComponent.class);

    @Override
    public Component makeCopy() {
        TeleporterComponent temp = new TeleporterComponent();
        temp.targetLevelID = this.targetLevelID;
        return temp;
    }
}
