# JavaGame2D

## About 

This is a 2d platformer, built on top of LibGDX. It has extensive, modular features, such as:
- ECS for game object management, 
- custom rendering pipeline, 
- custom animation state machines
- custom entity serialization/deserialization
- Level editor (WIP)

Above all, it's an all-purpose base for creating 2d platformers. It also comes with a playable demo

![gameplay](docs/gameplay.gif)

## How to run

### Running the game from source (Unix systems only)
1. Gradle wrapper handles dependencies needed for the game such as Gradle and JDK. All you have to do is make sure you have Java installed in version 8 or higher, so that gradle wrapper can start
2. clone this repository, navigate to project root, then run:
```shell
./gradlew run
```
Gradle wrapper should take care of the rest
