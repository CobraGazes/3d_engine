package org.lwjglb.engine;

import jinngine.geometry.Box;
import jinngine.geometry.Material;
import jinngine.geometry.Sphere;
import jinngine.math.Vector3;
import jinngine.physics.Body;
import jinngine.physics.DefaultScene;
import jinngine.physics.Scene;
import jinngine.physics.force.GravityForce;
import jinngine.physics.force.ImpulseForce;
import java.util.HashMap;
import java.util.Map;



public class Physics {

    private static Map<String, Body> cubes = new HashMap<>();
    private static Map<String, Body> spheres = new HashMap<>();

    static Scene PhysicsScene = new DefaultScene();

    public static void NewScene(){
        PhysicsScene.setTimestep(0.1);
    }

    public static void setFrictionCoefficient(Body body, double frictionCoefficient){
//        body.setFrictionCoefficient();
    }
    public static Body newCube(String name, double sizeX, double sizeY, double sizeZ, double positionX, double positionY, double positionZ, boolean isFixed){
        Body cube = new Body(name, new Box(sizeX, sizeY, sizeZ));
        cube.setPosition(positionX, positionY, positionZ);
        if (isFixed) {
            cube.setFixed(true);
        } else {
            PhysicsScene.addForce(new GravityForce(cube));
        }
        PhysicsScene.addBody(cube);
        cubes.put(name, cube);
        return cube;
    }

    public static Body newSphere(String name, double radius, double positionX, double positionY, double positionZ, boolean isFixed) {
        Body sphere = new Body(name, new Sphere(radius));
        sphere.setPosition(positionX, positionY, positionZ);
        if (isFixed) {
            sphere.setFixed(true);
        } else {
            PhysicsScene.addForce(new GravityForce(sphere));
        }
        PhysicsScene.addBody(sphere);
        spheres.put(name, sphere);
        return sphere;
    }

    public static Body getCube(String name){
        return cubes.get(name);
    }
    public static Body getSphere(String name){
        return spheres.get(name);
    }

    public static void applyImpulseForce(Body body, Vector3 point, Vector3 direction, double magnitude){
        PhysicsScene.addForce(new ImpulseForce(body, point, direction, magnitude));
    }

    public static void tickScene(){
        PhysicsScene.tick();
    }
}
