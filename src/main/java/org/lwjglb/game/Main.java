package org.lwjglb.game;

import jinngine.math.Matrix3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglb.engine.Engine;
import org.lwjglb.engine.IAppLogic;
import org.lwjglb.engine.IGuiInstance;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.Physics;
import org.lwjglb.engine.Window;
import org.lwjglb.engine.graph.Model;
import org.lwjglb.engine.graph.Render;
import org.lwjglb.engine.scene.Camera;
import org.lwjglb.engine.scene.Entity;
import org.lwjglb.engine.scene.ModelLoader;
import org.lwjglb.engine.scene.Scene;
import org.lwjglb.engine.scene.lights.PointLight;
import org.lwjglb.engine.scene.lights.SceneLights;
import org.lwjglb.engine.scene.lights.SpotLight;
import jinngine.physics.Body;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import jinngine.math.Vector3;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;


public class Main implements IAppLogic, IGuiInstance {

    private Body cube1;
    private Body sphere;

    private Entity cubeEntity;
    private Entity platformEntity;
    private Entity sphereEntity;

    private Vector3 PhysicsCubePos;
    private Matrix3 PhysicsCubeRotation;
    private Vector3 PhysicsSpherePos = new Vector3();
    private Matrix3 PhysicsSphereRotation = new Matrix3();

    private LightControls lightControls;
    private Vector4f displInc = new Vector4f();
    private float rotation;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final float forceMag = 2.0f;
    Matrix3 jomlMat = new Matrix3(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);


    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEng = new Engine("3d Engine Test", new Window.WindowOptions(), main);
        gameEng.run();
    }

    @Override
    public boolean handleGuiInput(Scene scene, Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPos();
        imGuiIO.addMousePosEvent(mousePos.x, mousePos.y);
        imGuiIO.addMouseButtonEvent(0, mouseInput.isLeftButtonPressed());
        imGuiIO.addMouseButtonEvent(1, mouseInput.isRightButtonPressed());

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }

    @Override
    public void drawGui(){
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();    
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        Camera camera = scene.getCamera();
        Physics.NewScene();

        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/cube/cube.obj", scene.getTextureCache()); scene.addModel(cubeModel);
        Model platformModel = ModelLoader.loadModel("platform-model", "resources/models/platform/platform.obj", scene.getTextureCache()); scene.addModel(platformModel);
        Model sphereModel = ModelLoader.loadModel("sphere-model", "resources/models/sphere/sphere.obj", scene.getTextureCache()); scene.addModel(sphereModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0f, -2);
        cubeEntity.updateModelMatrix();
        scene.addEntity(cubeEntity);
        cube1 = Physics.newCube("Cube", 1, 1, 1, 0, 0, -2, false);

        platformEntity = new Entity("platform-model", platformModel.getId());
        platformEntity.setPosition(0, -10f, -2);
        platformEntity.updateModelMatrix();
        scene.addEntity(platformEntity);    
        Body platform = Physics.newCube("Floor", 30000, 1, 30000, 0, -10, 0, true);

        sphereEntity = new Entity("sphere-model", sphereModel.getId());
        sphereEntity.setPosition(0, 0f, -2);
        sphereEntity.updateModelMatrix();
        scene.addEntity(sphereEntity);
        sphere = Physics.newSphere("sphere", 1, 0, 0, -2, false);


        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setSceneLights(sceneLights);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, -1.4f), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));

        //lightControls = new LightControls(scene);
        //scene.setGuiInstance(lightControls);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();

        // calculate forward and right vectors relative to camera orientation
        Vector3f forward = new Vector3f();
        camera.getViewMatrix().positiveZ(forward).negate();
        forward.y = 0; // keep movement on the horizontal plane
        if (forward.length() > 0) {
            forward.normalize();
        }

        Vector3f right = new Vector3f();
        camera.getViewMatrix().positiveX(right);
        right.y = 0; // keep movement on the horizontal plane
        if (right.length() > 0) {
            right.normalize();
        }

        if (window.isKeyPressed(GLFW_KEY_W)) {
            Physics.applyImpulseForce(sphere, new Vector3(0, 0, 0), new Vector3(forward.x * forceMag, 0, forward.z * forceMag), 1);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            Physics.applyImpulseForce(sphere, new Vector3(0, 0, 0), new Vector3(-forward.x * forceMag, 0, -forward.z * forceMag), 1);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            Physics.applyImpulseForce(sphere, new Vector3(0, 0, 0), new Vector3(-right.x * forceMag, 0, -right.z * forceMag), 1);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            Physics.applyImpulseForce(sphere, new Vector3(0, 0, 0), new Vector3(right.x * forceMag, 0, right.z * forceMag), 1);
        }
        if (window.isKeyPressed(GLFW_KEY_R)) {
            sphere.setPosition(0,-2,0);
            sphere.setVelocity(0,0,0);
            sphere.setOrientation(jomlMat);
        }
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        Physics.tickScene();

        PhysicsSpherePos = sphere.getPosition();
        PhysicsSphereRotation = sphere.getOrientation();

        float XSpherePos = (float) PhysicsSpherePos.x;
        float YSpherePos = (float) PhysicsSpherePos.y;
        float ZSpherePos = (float) PhysicsSpherePos.z;


        sphereEntity.setPosition(XSpherePos, YSpherePos,ZSpherePos);
        sphereEntity.setRotationFromMatrix3(PhysicsSphereRotation);
        sphereEntity.updateModelMatrix();

        Camera.update(window, sphereEntity, MOUSE_SENSITIVITY);
    }

    public void input(Window window, Scene scene, long diffTimeMillis) {
        throw new UnsupportedOperationException("Unimplemented method 'input'");
    }

}
