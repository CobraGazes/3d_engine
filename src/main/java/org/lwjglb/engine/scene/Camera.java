package org.lwjglb.engine.scene;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Window;

import javax.swing.text.Position;

public class Camera {

    private static Vector3f direction;
    private static Vector3f position;
    private static Vector3f right;
    private static Vector2f rotation;
    private static Vector3f up;
    private static Matrix4f viewMatrix;
    private static Vector2f NewMousepos;
    private static double oldMouseX = 0, oldMouseY = 0, newMouseX, newMouseY;
    private static float horizontalAngle = 0, verticalAngle = 0;
    private static float distance = 4.0f, angle = 0.0f;
    private static float pitch;
    private static float yaw;

    public Camera() {
        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();
        position = new Vector3f();
        viewMatrix = new Matrix4f();
        rotation = new Vector2f();
    }
    
    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public void CamUp(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.add(up);
        recalculate();
    }

    public void CamDown(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.sub(up);
        recalculate();
    }

    public void CamForward(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.add(direction);
        recalculate();
    }

    public void CamBackwards(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.sub(direction);
        recalculate();
    }

    public void CamLeft(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.sub(right);
        recalculate();
    }

    public void CamRight(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.add(right);
        recalculate();
    }

    private static void recalculate() {
        viewMatrix.identity()
            .rotateX(rotation.x)
            .rotateY(rotation.y)
            .translate(-position.x, -position.y, -position.z);
    }

    public static void addRotation(float x, float y) {
        rotation.add(x, y);
        recalculate();
    }

    public static float returnYaw(){
        return yaw;
    }
    public static void setRotation(float x, float y) {
        rotation.set(x, y);
        recalculate();
    }

    public static void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
    }

    public static void update(Window window, Entity entity, float MOUSE_SENSITIVITY){
        MouseInput mouseInput = window.getMouseInput();
        NewMousepos = mouseInput.getCurrentPos();
        newMouseX = NewMousepos.x;
        newMouseY = NewMousepos.y;

        float dx = (float) ((float) newMouseX - oldMouseX);
        float dy = (float) ((float) newMouseY - oldMouseY);

        verticalAngle -= dy * MOUSE_SENSITIVITY;
        horizontalAngle += dx * MOUSE_SENSITIVITY;

        float horDist = (float) (distance * Math.cos(Math.toRadians(verticalAngle)));
        float virDist = (float) (distance * Math.sin(Math.toRadians(verticalAngle)));

        float x = (float) (horDist * Math.sin(Math.toRadians(-horizontalAngle)));
        float z = (float) (horDist * Math.cos(Math.toRadians(-horizontalAngle)));

        // set camera position
        setPosition(entity.returnPosition().x + x, entity.returnPosition().y - virDist, entity.returnPosition().z + z);

        // set camera rotation to look at the entity
        pitch = (float) Math.toRadians(-verticalAngle);
        yaw = (float) Math.toRadians(horizontalAngle);
        setRotation(pitch, yaw);

        oldMouseX = newMouseX;
        oldMouseY = newMouseY;
    }
}
