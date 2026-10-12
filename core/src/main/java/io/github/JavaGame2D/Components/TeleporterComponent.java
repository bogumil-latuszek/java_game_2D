package io.github.JavaGame2D.Components;

public class TeleporterComponent implements Component{
    public int targetLevelID = -1;   // -1 = same level
    public int targetEntityID = -1;  // -1 = use level's default spawn point

    @Override
    public Component makeCopy() {
        TeleporterComponent temp = new TeleporterComponent();
        temp.targetLevelID = this.targetLevelID;
        temp.targetEntityID = this.targetEntityID;
        return temp;
    }
}
