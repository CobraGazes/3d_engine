package org.lwjglb.engine.scene;

import org.joml.Matrix4f;



public class Projection {
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_FAR = 1000.f;
    private static final float Z_NEAR = 0.01f;

    private Matrix4f matrix;

    public void updateMatrix(int width, int height){
        matrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
    }

    public Matrix4f getProjMatrix() {
        return matrix;
    }

    public Projection(int width, int height){
        matrix = new Matrix4f();
        updateMatrix(width, height);
    }



}  