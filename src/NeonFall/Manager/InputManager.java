// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Manager;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class InputManager
{
    private static GLFWMouseButtonCallback mouseButtonCallback;
    private static GLFWKeyCallback keyCallback;
    private static GLFWCursorPosCallback cursorPosCallback;
    private static int[] kbState = new int[349];
    private static boolean[] kbPressed = new boolean[349];
    private static int[] mState = new int[8];
    private static boolean[] mPressed = new boolean[8];
    private static int mouseX = 0;
    private static int mouseY = 0;
    private static long window;
    
    public static void init(final long window) {
        GLFW.glfwSetKeyCallback(InputManager.window = window, InputManager.keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(final long window, final int key, final int scancode, final int action, final int mods) {
                if (key != -1) {
                    InputManager.kbState[key] = action;
                }
                if (action == 0) {
                    InputManager.kbPressed[key] = true;
                }
            }
        });
        GLFW.glfwSetCursorPosCallback(window, InputManager.cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(final long window, final double x, final double y) {
                InputManager.mouseX = (int)x;
                InputManager.mouseY = (int)y;
            }
        });
        GLFW.glfwSetMouseButtonCallback(window, InputManager.mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(final long window, final int button, final int action, final int mods) {
                InputManager.mState[button] = action;
                if (action == 0) {
                    InputManager.mPressed[button] = true;
                }
            }
        });
    }
    
    public static void postUpdate() {
        for (int i = 0; i < InputManager.kbPressed.length; ++i) {
            InputManager.kbPressed[i] = false;
        }
        for (int i = 0; i < InputManager.mPressed.length; ++i) {
            InputManager.mPressed[i] = false;
        }
    }
    
    public static void destroy() {
        GLFW.glfwSetKeyCallback(InputManager.window, null);
        GLFW.glfwSetMouseButtonCallback(InputManager.window, null);
        GLFW.glfwSetCursorPosCallback(InputManager.window, null);
    }
    
    public static boolean isKeyDown(final int key) {
        return InputManager.kbState[key] > 0;
    }
    
    public static boolean isKeyUp(final int key) {
        return InputManager.kbState[key] == 0;
    }
    
    public static boolean isKeyPressed(final int key) {
        return InputManager.kbPressed[key];
    }
    
    public static boolean isMouseButtonDown(final int button) {
        return InputManager.mState[button] > 0;
    }
    
    public static boolean isMouseButtonUp(final int button) {
        return InputManager.mState[button] == 0;
    }
    
    public static boolean isMouseButtonPressed(final int button) {
        return InputManager.mPressed[button];
    }
    
    public static int getMouseX() {
        return InputManager.mouseX;
    }
    
    public static int getMouseY() {
        return InputManager.mouseY;
    }
}
