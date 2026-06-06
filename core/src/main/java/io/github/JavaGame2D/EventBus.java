package io.github.JavaGame2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class EventBus {
    // Singleton (global object)
    private static EventBus singleInstance;

    // Key: Event type, Value: List of Consumer functions
    private final HashMap< Class<?>, ArrayList<Consumer> > subscribers = new HashMap<>();

    private EventBus(){
    }

    // returns Singleton
    synchronized public static EventBus getInstance(){
        if(singleInstance == null){
            singleInstance = new EventBus();
        }
        return singleInstance;
    }

    // Subscribe: pass the event class and your function
    // <T> is a generic type parameter - it means the method can work with any type: String, Integer, Double, etc.
    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        // if Event not registered as key, register it with new empty array as value
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>());
        // then add handler to that list
        subscribers.get(eventType).add(handler);
    }

    // Publish: call all subscribed functions
    public <T> void publish(T event) {
        ArrayList<Consumer> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            for (Consumer handler : handlers) {
                handler.accept(event);  // Call the function!
            }
        }
    }
}
