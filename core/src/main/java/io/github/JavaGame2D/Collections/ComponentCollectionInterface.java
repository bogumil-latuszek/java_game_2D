package io.github.JavaGame2D.Collections;

import java.lang.reflect.Type;
import java.util.HashMap;

public interface ComponentCollectionInterface<ComponentType> {
    HashMap<Integer, Long> updateEntitySignature(HashMap<Integer,Long> entityIDtoSignature);
    void removeComponentFromEntity(int entityID);
    boolean hasEntityID(int entityID);
    ComponentType getComponent(int entityID);
    Class<ComponentType> getComponentType();
}
