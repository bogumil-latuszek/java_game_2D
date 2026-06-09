package io.github.JavaGame2D.Collections;

import java.util.HashMap;

public interface ComponentCollectionInterface {
    HashMap<Integer, Long> updateEntitySignature(HashMap<Integer,Long> entityIDtoSignature);
    void removeComponentFromEntity(int entityID);
}
