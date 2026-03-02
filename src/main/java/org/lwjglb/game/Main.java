package org.lwjglb.game;

import jinngine.math.Matrix3;
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
    private Body platform;
    private LightControls lightControls;
    private Entity cubeEntity;
    private Entity platformEntity;
    private Vector3 PhysicsCubePos;
    private Matrix3 PhysicsCubeRotation;
    private Vector4f displInc = new Vector4f();
    private float rotation;
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;

    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEng = new Engine("This isnt gonna work", new Window.WindowOptions(), main);
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

        Physics.NewScene();

        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/cube/cube.obj", scene.getTextureCache()); scene.addModel(cubeModel);
        Model platformModel = ModelLoader.loadModel("platform-model", "resources/models/platform/platform.obj", scene.getTextureCache()); scene.addModel(platformModel);
        //Model rbModel = ModelLoader.loadModel("rb-model", "resources/models/room/room.obj", scene.getTextureCache()); scene.addModel(rbModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0f, -2);
        cubeEntity.updateModelMatrix(); //move cube
        scene.addEntity(cubeEntity);
        cube1 = Physics.newCube("Cube", 1, 1, 1, 0, 0, -2, false);

        platformEntity = new Entity("platform-model", platformModel.getId());
        platformEntity.setPosition(0, -10f, -2);
        platformEntity.updateModelMatrix(); //move cube
        scene.addEntity(platformEntity);    
        platform = Physics.newCube("Floor", 1500, 1, 1500, 0, -10, 0, true);
        //FUNCTION POSITIONS MATTER BRUH that mght be the other issue and i dont even know how to begin with fixing sum like that
        //if ts dont have colour still its probably because inside the mtl file we are definind the Ka, Kd and Ks values as an image and value and in the tutorial they use set values


        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.3f);
        scene.setSceneLights(sceneLights);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, -1.4f), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));

        lightControls = new LightControls(scene);
        scene.setGuiInstance(lightControls);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.CamForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.CamBackwards(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A )|| window.isKeyPressed(GLFW_KEY_LEFT)) {
            camera.CamLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D) || window.isKeyPressed(GLFW_KEY_RIGHT)) {
            camera.CamRight(move);
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            camera.CamUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            camera.CamDown(move);
        }
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            cube1.setVelocity(new Vector3(0,10,0));
            //Physics.applyImpulseForce(cube1, new Vector3(0, 3, 0), new Vector3(0, 4, 0),1);
        }
        //new function to determine if I'm actually scrolling

        //clean up keybinds

        MouseInput mouseInput = window.getMouseInput();
        //System.out.println(mouseInput.getCurrentScroll());
        //Vector2f scrollVec = mouseInput.getCurrentScroll();
        Vector2f displVec = mouseInput.getDisplVec();
        camera.addRotation((float) Math.toRadians(displVec.x * MOUSE_SENSITIVITY), (float) Math.toRadians(displVec.y * MOUSE_SENSITIVITY));
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        Physics.tickScene();
        PhysicsCubePos = cube1.getPosition();
        PhysicsCubeRotation = cube1.getOrientation();
        cubeEntity.setPosition((float) PhysicsCubePos.x, (float) PhysicsCubePos.y,(float) PhysicsCubePos.z);
        cubeEntity.setRotationFromMatrix3(PhysicsCubeRotation);
        cubeEntity.updateModelMatrix();
    }

    public void input(Window window, Scene scene, long diffTimeMillis) {
        throw new UnsupportedOperationException("Unimplemented method 'input'");
    }

}