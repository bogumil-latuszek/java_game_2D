package io.github.JavaGame2D.Components;

public class PrefabComponent implements Component {

    public String prefabName = "default"; // default value for serializer

    public static final long SIGNATURE = ComponentSignatures.register(PrefabComponent.class);

    // empty constructor for serializer
    public PrefabComponent() {
    }

    public PrefabComponent(String prefabName) {
        this.prefabName = prefabName;
    }

    @Override
    public Component makeCopy() {
        PrefabComponent temp = new PrefabComponent();
        temp.prefabName = this.prefabName;
        return temp;
    }
}
