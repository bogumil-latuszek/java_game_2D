package io.github.JavaGame2D.Components;
import java.util.HashMap;
import java.util.Map;

public final class ComponentSignatures {
    private static final Map<Class<? extends Component>, Long> compClassToSignature = new HashMap<>();
    private static long nextBit = 1L;

    // synchronized: can only be accessed by one thread at a time
    public static synchronized long get(Class<? extends Component> type) {
        if (compClassToSignature.containsKey(type)){
            return compClassToSignature.get(type);
        }
        else{
            // generate new signature, save and return it
            long bit = nextBit;
            compClassToSignature.put(type, bit);
            nextBit <<= 1; // shift the bit by 1 position
            return bit;
        }
    }
}
