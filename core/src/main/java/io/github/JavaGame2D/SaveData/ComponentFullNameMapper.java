package io.github.JavaGame2D.SaveData;

import java.io.IOException;

public class ComponentFullNameMapper {

    // path from root to package with components
    // something like "io.github.JavaGame2D.Components"
    private String basePackage;

    public ComponentFullNameMapper(String basePackage) {
        this.basePackage = basePackage;
    }


    public Class<?> getComponentClass(String shortName) throws IOException {
        try {
            return Class.forName(basePackage + "." + shortName);
        } catch (ClassNotFoundException e) {
            throw new IOException("Unknown component class: " + shortName, e);
        }
    }
}
