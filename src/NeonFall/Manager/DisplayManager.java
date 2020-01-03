// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Manager;

import NeonFall.MainGame;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.libffi.Closure;

public class DisplayManager
{
    private static int width = 1280;
    private static int height = 720;
    private static float aspect = 1.7777778f;
    private static long window;
    private static Closure debug;
    private static GLFWErrorCallback errorCallback;
    private static GLFWWindowSizeCallback windowSizeCallback;
    
    public static void init() {
        GLFW.glfwSetErrorCallback(DisplayManager.errorCallback = GLFWErrorCallback.createPrint(System.err));
        initOpenGL();
        initWindow();
        GLFW.glfwSetWindowSizeCallback(DisplayManager.window, DisplayManager.windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(final long window, final int width, final int height) {
                resize(width, height);
            }
        });
        DisplayManager.debug = GLUtil.setupDebugMessageCallback();
        System.out.println("Your OpenGL version is " + GL11.glGetString(7938));
    }
    
    private static void initWindow() {
        DisplayManager.window = GLFW.glfwCreateWindow(DisplayManager.width, DisplayManager.height, "NeonFall", 0L, 0L);
        if (DisplayManager.window == 0L) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        final ByteBuffer vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(DisplayManager.window, (GLFWvidmode.width(vidmode) - DisplayManager.width) / 2, (GLFWvidmode.height(vidmode) - DisplayManager.height) / 2);
        GLFW.glfwMakeContextCurrent(DisplayManager.window);
        GL.createCapabilities();
        GLFW.glfwSwapInterval(0);
        GLFW.glfwShowWindow(DisplayManager.window);
    }
    
    private static void initOpenGL() {
        if (GLFW.glfwInit() != 1) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(131076, 0);
        GLFW.glfwWindowHint(131075, 1);
        GLFW.glfwWindowHint(139266, 3);
        GLFW.glfwWindowHint(139267, 2);
        GLFW.glfwWindowHint(139270, 0);
        GLFW.glfwWindowHint(139272, 204802);
        GLFW.glfwWindowHint(139271, 1);
    }
    
    public static void update() {
        GL11.glViewport(0, 0, DisplayManager.width, DisplayManager.height);
        GLFW.glfwSwapBuffers(DisplayManager.window);
    }
    
    private static void resize(final int w, final int h) {
        DisplayManager.width = w;
        DisplayManager.height = (int)(w / DisplayManager.aspect);
        GLFW.glfwSetWindowSize(DisplayManager.window, DisplayManager.width, DisplayManager.height);
        MainGame.resize(DisplayManager.width, DisplayManager.height);
    }
    
    public static void closeDisplay() {
        GLFW.glfwDestroyWindow(DisplayManager.window);
    }
    
    public static long getWindow() {
        return DisplayManager.window;
    }
    
    public static int getWidth() {
        return DisplayManager.width;
    }
    
    public static int getHeight() {
        return DisplayManager.height;
    }
    
    public static float getAspectRatio() {
        return DisplayManager.aspect;
    }
}
