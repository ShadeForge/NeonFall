package NeonFall.Manager;

import NeonFall.MainGame;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.libffi.Closure;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public class DisplayManager {

    private static int width = 1280;
    private static int height = 720;
    private static float aspect = 16.0f/9.0f;

    // The window handle
    private static long window;
    @SuppressWarnings("unused")
    private static Closure debug;
    @SuppressWarnings("unused")
    private static GLFWErrorCallback errorCallback;
    @SuppressWarnings("unused")
    private static GLFWWindowSizeCallback windowSizeCallback;

    public static void init() {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint((System.err)));

        initOpenGL();

        initWindow();

        glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                resize(width, height);
            }
        });

        debug = GLUtil.setupDebugMessageCallback();
        System.out.println("Your OpenGL version is " + GL11.glGetString(GL11.GL_VERSION));
    }

    private static void initWindow() {
        // Das Fenster erzeugen.
        window = GLFW.glfwCreateWindow(width, height, "NeonFall", MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Auflösung des primären Displays holen.
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        // Fenster zentrieren
        glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - width) / 2, (GLFWvidmode.height(vidmode) - height) / 2);

        // Den GLFW Kontext aktuell machen.
        GLFW.glfwMakeContextCurrent(window);

        // GL Kontext unter Berücksichtigung des Betriebssystems erzeugen.
        GL.createCapabilities();

        // Synchronize to refresh rate.
        GLFW.glfwSwapInterval(0);

        //glfwSetInputMode(window,  GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // Das Fenster sichtbar machen.
        GLFW.glfwShowWindow(window);
    }

    private static void initOpenGL() {
        // GLFW Initialisieren. Die meisten GLFW-Funktionen funktionieren vorher nicht.
        if (GLFW.glfwInit() != GL11.GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // Konfigurieren des Fensters
        glfwDefaultWindowHints(); // optional, die aktuellen Window-Hints sind bereits Standardwerte
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // Das Fenster bleibt nach dem Erzeugen versteckt.
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // Die Fenstergröße lässt sich verändern.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GL_TRUE); // Windowhint für den Debug Kontext
    }

    public static void update() {
        GL11.glViewport(0, 0, width, height);
        GLFW.glfwSwapBuffers(window);
    }

    private static void resize(int w, int h) {
        width = w;
        height = (int)((float)w/aspect);
        GLFW.glfwSetWindowSize(window, width, height);
        MainGame.resize(width, height);
    }

    public static void closeDisplay() {
        GLFW.glfwDestroyWindow(window);
    }

    public static long getWindow() {
        return window;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static float getAspectRatio() {
        return (float)width / (float)height;
    }
}
