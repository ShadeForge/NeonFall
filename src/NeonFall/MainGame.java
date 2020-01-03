// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall;

import NeonFall.Scene.GameScene;
import org.lwjgl.opengl.GL11;
import java.util.Iterator;
import java.io.FileWriter;
import org.lwjgl.glfw.GLFW;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import NeonFall.Manager.InputManager;
import NeonFall.Manager.SoundManager;
import NeonFall.Manager.ResourceManager;
import NeonFall.Manager.DisplayManager;
import java.util.ArrayList;
import NeonFall.Scene.MainMenuScene;
import NeonFall.Scene.Scene;

public class MainGame
{
    private static Scene currentScene;
    public static ArrayList<Integer> highscore;
    
    public static void main(final String[] args) {
        long frameTime = 0L;
        int frames = 0;
        try {
            DisplayManager.init();
            ResourceManager.init();
            SoundManager.init();
            InputManager.init(DisplayManager.getWindow());
            long lastUpdate = System.currentTimeMillis();
            MainGame.highscore = new ArrayList<Integer>();
            try {
                final FileReader fr = new FileReader("highscore.txt");
                final BufferedReader reader = new BufferedReader(fr);
                String line;
                while ((line = reader.readLine()) != null) {
                    MainGame.highscore.add(Integer.parseInt(line));
                }
            }
            catch (Exception err) {
                err.printStackTrace();
            }
            MainGame.highscore.sort((o1, o2) -> o2.compareTo(o1));
            MainGame.currentScene = new MainMenuScene();
            while (GLFW.glfwWindowShouldClose(DisplayManager.getWindow()) == 0) {
                final long delta = System.currentTimeMillis() - lastUpdate;
                lastUpdate = System.currentTimeMillis();
                update(delta / 1000.0f);
                render();
                frameTime += delta;
                if (frameTime > 1000L) {
                    frameTime %= 1000L;
                    System.out.println("FPS: " + frames);
                    System.out.println("MousePos: " + InputManager.getMouseX() + " | " + InputManager.getMouseY());
                    frames = 0;
                }
                ++frames;
                InputManager.postUpdate();
            }
            InputManager.destroy();
            SoundManager.destroy();
            ResourceManager.destroy();
            DisplayManager.closeDisplay();
            currentScene.destroy();
            try {
                final FileWriter writer = new FileWriter("highscore.txt");
                for (final Integer highscore : MainGame.highscore) {
                    writer.write(highscore + "\n");
                }
                writer.close();
            }
            catch (IOException err) {
                err.printStackTrace();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void update(final float delta) {
        GLFW.glfwPollEvents();
        DisplayManager.update();
        MainGame.currentScene.update(delta);
    }
    
    private static void render() {
        if (DisplayManager.getWidth() != 0 && DisplayManager.getHeight() != 0) {
            GL11.glClear(16640);
            MainGame.currentScene.render();
        }
    }
    
    public static void setScene(final Scene.SCENE_TYPE scene) {
        switch (scene) {
            case MAINMENU: {
                MainGame.currentScene = new MainMenuScene();
                break;
            }
            case GAME: {
                MainGame.currentScene = new GameScene();
                break;
            }
            case CREDITS: {
                //MainGame.currentScene = new MainMenuScene();
                break;
            }
            case HIGHSCORE: {
                //MainGame.currentScene = new MainMenuScene();
                break;
            }
            case INTRO: {
                //MainGame.currentScene = null;
                break;
            }
        }
    }
    
    public static void resize(final int width, final int height) {
        MainGame.currentScene.resize(width, height);
    }
}
