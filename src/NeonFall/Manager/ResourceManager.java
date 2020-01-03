package NeonFall.Manager;

import NeonFall.Resources.BitmapFont;
import NeonFall.Resources.Model.Loader;
import NeonFall.Resources.Model.Material;
import NeonFall.Resources.Model.RawModel;
import NeonFall.Resources.Model.StaticLoader;
import NeonFall.Resources.Sounds.Sound;
import NeonFall.Resources.Texture.ModelTexture;
import org.joml.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public class ResourceManager {

    //Directories
    public static String RES_DIR = "res/";
    public static String SHADER_DIR = RES_DIR + "shaders/";
    public static String MESHES_DIR = RES_DIR + "meshes/";
    public static String SOUNDS_DIR = RES_DIR + "sounds/";
    public static String MUSIC_DIR = RES_DIR + "music/";
    public static String FONTS_DIR = RES_DIR + "fonts/";

    //Shaders
    public static final String MAIN_VERTEX_FILE = SHADER_DIR + "main_vert.glsl";
    public static final String IMAGE_VERTEX_FILE = SHADER_DIR + "image_vert.glsl";
    public static final String MAIN_FRAGMENT_FILE = SHADER_DIR + "main_frag.glsl";
    public static final String GLOW_FRAGMENT_FILE = SHADER_DIR + "glow_frag.glsl";
    public static final String BLUR_FRAGMENT_FILE = SHADER_DIR + "blur_frag.glsl";

    //Models
    public static final String PLAYER_MODEL_FILE = MESHES_DIR + "ship.obj";
    public static final String CUBE_MODEL_FILE = MESHES_DIR + "cube.obj";

    //Static Models
    public static final String PLANE_MODEL_NAME = "Plane_Model";
    public static final String SURFACE_MODEL_NAME = "Surface_Model";

    //Textures
    public static final String TEX_CUBE_FILE = MESHES_DIR + "cube.png";
    public static final String TEX_WHITE_FILE = MESHES_DIR + "white.png";
    public static final String TEX_SHIP_FILE = MESHES_DIR + "ship.png";
    public static final String TEX_START_FILE = FONTS_DIR + "start.png";
    public static final String TEX_EXIT_FILE = FONTS_DIR + "exit.png";
    public static final String TEX_CREDITS_FILE = FONTS_DIR + "credits.png";
    public static final String TEX_HIGHSCORE_FILE = FONTS_DIR + "highscore.png";

    //Sounds

    //Fonts

    //Debug
    public static final String TEST_MODEL_FILE = MESHES_DIR + "cube.obj";
    public static final Material TEST_MATERIAL = new Material(new Vector3f(0,0,0), new Vector3f(0,0,0), new Vector3f(0,1,0), new Vector3f(0,0,0), 0);
    public static final String TEST_TEXTURE_FILE = MESHES_DIR + "cube_texture.png";
    public static final String DEBUG_FONT_FILE = FONTS_DIR + "font.png";
    public static final String DEBUG_FONT_NAME = "Times New Roman";
    public static BitmapFont DEBUG_FONT;

    private static Loader loader;
    private static HashMap<String, RawModel> modelMap;
    private static HashMap<String, Sound> soundMap;
    private static HashMap<String, ModelTexture> textureMap;
    private static HashMap<String, Material> materialMap;
    private static ArrayList<String> musicFiles = new ArrayList<>();

    public static void init() {
        modelMap = new HashMap<>();
        soundMap = new HashMap<>();
        textureMap = new HashMap<>();
        materialMap = new HashMap<>();
        loader = new Loader();

        //Load models
        loadModel(TEST_MODEL_FILE);
        loadModel(PLAYER_MODEL_FILE);

        //Load static models
        modelMap.put(PLANE_MODEL_NAME, StaticLoader.loadPlane(loader));
        modelMap.put(SURFACE_MODEL_NAME, StaticLoader.loadSurface(loader));

        //Load textures
        loadTexture(TEX_CUBE_FILE);
        loadTexture(TEX_SHIP_FILE);
        loadTexture(TEX_EXIT_FILE);
        loadTexture(TEX_START_FILE);
        loadTexture(TEX_CREDITS_FILE);
        loadTexture(TEX_HIGHSCORE_FILE);
        loadTexture(TEX_WHITE_FILE);

        //Load sounds

        //Load music
        loadMusicFiles();

        //Load fonts
        DEBUG_FONT = loadFont(DEBUG_FONT_FILE);

        for(int i = 0; i < 10; i++) {
            textureMap.put(FONTS_DIR + String.valueOf(i) + ".png", new ModelTexture(loader.loadTexture(FONTS_DIR + String.valueOf(i) + ".png")));
        }
    }

    public static String getNumberTexture(int i) {
        return FONTS_DIR + String.valueOf(i) + ".png";
    }

    private static void loadModel(String file) {
        modelMap.put(file, loader.loadModel(file));
    }

    private static void loadSound(String file) {
        soundMap.put(file, new Sound(file));
    }

    private static void loadTexture(String file) {
        textureMap.put(file, new ModelTexture(loader.loadTexture(file)));
    }

    private static void loadMaterial(String file) {
        materialMap.put(file, loader.loadMaterial(file));
    }

    private static void loadMusicFiles() {
        musicFiles = new ArrayList<>();

        try {
            Files.walk(Paths.get(MUSIC_DIR)).forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    musicFiles.add(MUSIC_DIR + filePath.getFileName().toString());
                    loadSound(MUSIC_DIR + filePath.getFileName().toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BitmapFont loadFont(String file) {
        Font awtFont = new Font("Times New Roman", Font.BOLD, 24);

        return new BitmapFont(file);
    }

    public static ArrayList<String> getMusicFiles() {
        return musicFiles;
    }

    public static RawModel getModel(String file) {
        return modelMap.get(file);
    }

    public static Sound getSound(String file) { return soundMap.get(file); }

    public static ModelTexture getTexture(String file) {
        return textureMap.get(file);
    }

    public static void destroy() {
        for (ModelTexture modelTexture : textureMap.values()) {
            glDeleteTextures(modelTexture.getTextureID());
        }
        loader.cleanUp();
    }
}
