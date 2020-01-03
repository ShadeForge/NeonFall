package NeonFall;

import NeonFall.Manager.DisplayManager;
import NeonFall.Manager.InputManager;
import NeonFall.Manager.ResourceManager;
import NeonFall.Manager.SoundManager;
import NeonFall.Scene.GameScene;
import NeonFall.Scene.MainMenuScene;
import NeonFall.Scene.Scene;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * Usage:
 * Author: lbald
 * Last Update: 28.12.2015
 */
public class MainGame {

    private static Scene currentScene;
    private static MainMenuScene mainMenuScene;
    public static ArrayList<Integer> highscore;

    public static void main(String[] args) {
        long lastUpdate;
        long delta;
        long frameTime = 0;
        int frames = 0;
        try{
            // Setup window
            DisplayManager.init();
            ResourceManager.init();
            SoundManager.init();
            InputManager.init(DisplayManager.getWindow());
            lastUpdate = System.currentTimeMillis();
            highscore = new ArrayList<>();

            try {
                FileReader fr = new FileReader("highscore.txt");
                BufferedReader reader = new BufferedReader(fr);

                String line;

                while((line = reader.readLine()) != null) {
                    highscore.add(Integer.parseInt(line));
                }

            } catch (IOException | NumberFormatException err){
                err.printStackTrace();
            }
            highscore.sort((o1, o2) -> o2.compareTo(o1));

            mainMenuScene = new MainMenuScene();

            currentScene = mainMenuScene;

            while(glfwWindowShouldClose(DisplayManager.getWindow()) == GL_FALSE) {

                //Calculate delta-time
                delta = System.currentTimeMillis() - lastUpdate;
                lastUpdate = System.currentTimeMillis();

                update(delta / 1000f);
                render();

                frameTime += delta;

                if(frameTime > 1000) {
                    frameTime %= 1000;
                    System.out.println("FPS: " + frames);
                    frames = 0;
                }

                frames++;

                InputManager.postUpdate();
            }

            InputManager.destroy();
            SoundManager.destroy();
            ResourceManager.destroy();
            DisplayManager.closeDisplay();
            mainMenuScene.destroy();

            try {
                FileWriter writer = new FileWriter("highscore.txt");

                for (Integer highscore : MainGame.highscore) {
                    writer.write(highscore + "\n");
                }

                writer.close();
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void update(float delta) {
        GLFW.glfwPollEvents();
        DisplayManager.update();
        currentScene.update(delta);
    }

    private static void render() {
        // render the scene //
        if(DisplayManager.getWidth() != 0 && DisplayManager.getHeight() != 0) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            currentScene.render();
        }
    }

    public static void setScene(Scene.SCENE_TYPE scene) {
        switch(scene) {
            case MAINMENU:
                currentScene = mainMenuScene;
                break;
            case GAME:
                currentScene = new GameScene();
                break;
            case CREDITS:
                currentScene = mainMenuScene;
                break;
            case HIGHSCORE:
                currentScene = mainMenuScene;
                break;
            case INTRO:
                currentScene = null;
        }
    }

    public static void resize(int width, int height) {
        currentScene.resize(width, height);
        mainMenuScene.resize(width, height);
    }
}
