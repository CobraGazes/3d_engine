package org.lwjglb.engine;

import jinngine.geometry.Box;
import jinngine.physics.Body;
import jinngine.physics.DefaultScene;
import jinngine.physics.Scene;
import jinngine.physics.force.GravityForce;


public class Physics {

    static Scene PhysicsScene = new DefaultScene();
    public static Body cube = new Body("Box", new Box(1, 1, 1));
    static Body floor = new Body("Floor", new Box(1500,1,1500));

    public static void NewScene(){
        PhysicsScene.setTimestep(0.1);

        floor.setPosition(0, -10, 0);
        floor.setFixed(true);

        cube.setPosition(0, 0, -2);

        PhysicsScene.addBody(floor);
        PhysicsScene.addBody(cube);

        PhysicsScene.addForce(new GravityForce(cube));
    }

    public static void tickScene(){
        PhysicsScene.tick();
        System.out.println(cube.getPosition());
    }
}
