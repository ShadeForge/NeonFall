package NeonFall.Manager;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Usage:
 * Author: lbald
 * Last Update: 29.12.2015
 */
public class InputManager {

    @SuppressWarnings("unused")
    private static GLFWMouseButtonCallback mouseButtonCallback;
    @SuppressWarnings("unused")
    private static GLFWKeyCallback keyCallback;
    @SuppressWarnings("unused")
    private static GLFWCursorPosCallback cursorPosCallback;

    private static int[] kbState = new int[GLFW_KEY_LAST+1];
    private static boolean[] kbPressed = new boolean[GLFW_KEY_LAST+1];
    private static int[] mState = new int[GLFW_MOUSE_BUTTON_LAST+1];
    private static boolean[] mPressed = new boolean[GLFW_MOUSE_BUTTON_LAST+1];
    private static int mouseX = 0;
    private static int mouseY = 0;
    private static long window;

    public static void init(long window) {

        InputManager.window = window;

        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key != -1)
                    kbState[key] = action;
                if(action == GLFW_RELEASE) {
                    kbPressed[key] = true;
                }
            }
        });

        glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                mouseX = (int) x;
                mouseY = (int) y;
            }
        });

        glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mState[button] = action;
                if(action == GLFW_RELEASE) {
                    mPressed[button] = true;
                }
            }
        });
    }

    public static void postUpdate() {
        for(int i = 0; i < kbPressed.length; i++) {
            kbPressed[i] = false;
        }

        for(int i = 0; i < mPressed.length; i++) {
            mPressed[i] = false;
        }
    }

    public static void destroy() {
        glfwSetKeyCallback(window, null);
        glfwSetMouseButtonCallback(window, null);
        glfwSetCursorPosCallback(window, null);
    }

    public static boolean isKeyDown(int key) {
        return kbState[key] > 0;
    }

    public static boolean isKeyUp(int key) {
        return kbState[key] == GLFW_RELEASE;
    }

    public static boolean isKeyPressed(int key) { return kbPressed[key]; }

    public static boolean isMouseButtonDown(int button) {
        return mState[button] > 0;
    }

    public static boolean isMouseButtonUp(int button) {
        return mState[button] == GLFW_RELEASE;
    }

    public static boolean isMouseButtonPressed(int button) { return mPressed[button]; }

    public static int getMouseX() {
        return mouseX;
    }

    public static int getMouseY() {
        return mouseY;
    }
}
