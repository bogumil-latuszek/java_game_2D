package io.github.JavaGame2D.Components;

public class TeleporterComponent implements Component{
    public int targetLevelID = 0;
    @Override
    public long getSignature() {
        return ComponentSignatures.TELEPORTER;
    }

    @Override
    public Component makeCopy() {
        TeleporterComponent temp = new TeleporterComponent();
        temp.targetLevelID = this.targetLevelID;
        return temp;
    }
}
