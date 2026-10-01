package io.github.JavaGame2D.Components;
import java.util.HashMap;
import java.util.Map;

public final class ComponentSignatures {
    private static final Map<Class<? extends Component>, Long> compClassToSignature = new HashMap<>();
    private static long nextBit = 1L;

    // synchronized: can only be accessed by one thread at a time
    public static synchronized long register(Class<? extends Component> type) {
        // computeIfAbsent: if it's already registered, don't execute the lambda expression
        return compClassToSignature.computeIfAbsent(type, t -> {
            long bit = nextBit;
            nextBit <<= 1; // shift the bit by 1 position
            return bit;
        });
    }

    public static long get(Class<? extends Component> type) {
        Long sig = compClassToSignature.get(type);
        if (sig == null){
            throw new IllegalArgumentException("Unregistered: " + type);
        }
        return sig;
    }
}
