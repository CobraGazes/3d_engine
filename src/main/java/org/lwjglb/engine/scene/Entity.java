package org.lwjglb.engine.scene;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import jinngine.math.Vector3;

public class Entity {

    private final String id;
    private final String modelId;
    private Matrix4f Modelmatrix;
    private Vector3f position;
    private Vector3 DoublePosition;
    private Quaternionf rotation;
    private float scale;
    
    public Entity(String id, String modelID){
        this.modelId = modelID;
        this.id = id;
        scale = 1;

        Modelmatrix = new Matrix4f();
        position = new Vector3f();
        rotation = new Quaternionf();
    }

    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(float x, float y, float z, float angle){
        this.rotation.fromAxisAngleRad(x, y, z, angle);
    }

    public void setRotationFromMatrix3(jinngine.math.Matrix3 m) {
        Matrix3f jomlMat = new Matrix3f(
                (float)m.a11, (float)m.a12, (float)m.a13,
                (float)m.a21, (float)m.a22, (float)m.a23,
                (float)m.a31, (float)m.a32, (float)m.a33
        );
        this.rotation.setFromNormalized(jomlMat);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String returnID(){
        return id;
    }

    public String returnModelID(){
        return modelId;
    }

    public Matrix4f returnMatrix(){
        return Modelmatrix;
    }

    public Vector3f returnPosition(){
        return position;
    }

    public Quaternionf returnRotation(){
        return rotation;
    }

    public float returnScale(){
        return scale;
    }
    
    public void updateModelMatrix(){
        Modelmatrix.translationRotateScale(position, rotation, scale);
    }
}
