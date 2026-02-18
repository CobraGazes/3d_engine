package org.lwjglb.engine.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity {

    private final String id;
    private final String modelId;
    private Matrix4f Modelmatrix;
    private Vector3f position;
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
