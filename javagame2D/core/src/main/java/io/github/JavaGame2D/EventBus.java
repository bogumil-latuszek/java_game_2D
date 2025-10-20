package io.github.JavaGame2D;

public class EventBus {
    private static EventBus singleInstance;
    private EventBus(){
    }

    //Singleton (global object)
    synchronized public static EventBus getInstance(){
        if(singleInstance == null){
            singleInstance = new EventBus();
        }
        return singleInstance;
    }



}
