// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Manager;

import org.joml.Vector3f;
import java.nio.file.Path;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Files;
import java.nio.file.FileVisitOption;
import java.nio.file.Paths;
import NeonFall.Resources.Model.StaticLoader;
import java.util.ArrayList;
import NeonFall.Resources.Texture.ModelTexture;
import NeonFall.Resources.Sounds.Sound;
import NeonFall.Resources.Model.RawModel;
import java.util.HashMap;
import NeonFall.Resources.Model.Loader;
import NeonFall.Resources.BitmapFont;
import NeonFall.Resources.Model.Material;

public class ResourceManager
{
    public static String RES_DIR = "res/";
    public static String SHADER_DIR = ResourceManager.RES_DIR + "shaders/";
    public static String MESHES_DIR = ResourceManager.RES_DIR + "meshes/";
    public static String SOUNDS_DIR = ResourceManager.RES_DIR + "sounds/";
    public static String MUSIC_DIR = ResourceManager.RES_DIR + "music/";
    public static String FONTS_DIR = ResourceManager.RES_DIR + "fonts/";
    public static final String MAIN_VERTEX_FILE = ResourceManager.SHADER_DIR + "main_vert.glsl";
    public static final String IMAGE_VERTEX_FILE = ResourceManager.SHADER_DIR + "image_vert.glsl";
    public static final String TEXT_VERTEX_FILE = ResourceManager.SHADER_DIR + "text_vert.glsl";
    public static final String TEXT_FRAGMENT_FILE = ResourceManager.SHADER_DIR + "text_frag.glsl";
    public static final String MAIN_FRAGMENT_FILE = ResourceManager.SHADER_DIR + "main_frag.glsl";
    public static final String GLOW_FRAGMENT_FILE = ResourceManager.SHADER_DIR + "glow_frag.glsl";
    public static final String BLUR_FRAGMENT_FILE = ResourceManager.SHADER_DIR + "blur_frag.glsl";
    public static final String PLAYER_MODEL_FILE = ResourceManager.MESHES_DIR + "ship.obj";
    public static final String CUBE_MODEL_FILE = ResourceManager.MESHES_DIR + "cube.obj";
    public static final String PLANE_MODEL_NAME = "Plane_Model";
    public static final String SURFACE_MODEL_NAME = "Surface_Model";
    public static final String TEX_CUBE_FILE = ResourceManager.MESHES_DIR + "cube.png";
    public static final String TEX_WHITE_FILE = ResourceManager.MESHES_DIR + "white.png";
    public static final String TEX_SHIP_FILE = ResourceManager.MESHES_DIR + "ship.png";
    public static final String TEST_MODEL_FILE = ResourceManager.MESHES_DIR + "cube.obj";
    public static final Material TEST_MATERIAL = new Material(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), 0.0f);
    public static final String TEST_TEXTURE_FILE = ResourceManager.MESHES_DIR + "cube_texture.png";
    public static final String DEBUG_FONT_FILE = ResourceManager.FONTS_DIR + "font.png";
    public static BitmapFont DEBUG_FONT;
    private static Loader loader;
    private static HashMap<String, RawModel> modelMap;
    private static HashMap<String, Sound> soundMap;
    private static HashMap<String, ModelTexture> textureMap;
    private static HashMap<String, Material> materialMap;
    private static ArrayList<String> musicFiles = new ArrayList<String>();
    
    public static void init() {
        ResourceManager.modelMap = new HashMap<String, RawModel>();
        ResourceManager.soundMap = new HashMap<String, Sound>();
        ResourceManager.textureMap = new HashMap<String, ModelTexture>();
        ResourceManager.materialMap = new HashMap<String, Material>();
        ResourceManager.loader = new Loader();
        loadModel(ResourceManager.TEST_MODEL_FILE);
        loadModel(ResourceManager.PLAYER_MODEL_FILE);
        ResourceManager.modelMap.put("Plane_Model", StaticLoader.loadPlane(ResourceManager.loader));
        ResourceManager.modelMap.put("Surface_Model", StaticLoader.loadSurface(ResourceManager.loader));
        loadTexture(ResourceManager.TEX_CUBE_FILE);
        loadTexture(ResourceManager.TEX_SHIP_FILE);
        loadTexture(ResourceManager.TEX_WHITE_FILE);
        loadMusicFiles();
        ResourceManager.DEBUG_FONT = loadFont(ResourceManager.DEBUG_FONT_FILE);
    }
    
    public static String getNumberTexture(final int i) {
        return ResourceManager.FONTS_DIR + String.valueOf(i) + ".png";
    }
    
    private static BitmapFont loadFont(final String path) {
        return new BitmapFont(path, 64);
    }
    
    private static void loadModel(final String file) {
        ResourceManager.modelMap.put(file, ResourceManager.loader.loadModel(file));
    }
    
    private static void loadSound(final String file) {
        ResourceManager.soundMap.put(file, new Sound(file));
    }
    
    private static void loadTexture(final String file) {
        ResourceManager.textureMap.put(file, new ModelTexture(ResourceManager.loader.loadTexture(file)));
    }
    
    private static void loadMaterial(final String file) {
        ResourceManager.materialMap.put(file, ResourceManager.loader.loadMaterial(file));
    }
    
    private static void loadMusicFiles() {
        ResourceManager.musicFiles = new ArrayList<String>();
        try {
            Files.walk(Paths.get(ResourceManager.MUSIC_DIR, new String[0]), new FileVisitOption[0]).forEach(filePath -> {
                if (Files.isRegularFile(filePath, new LinkOption[0])) {
                    ResourceManager.musicFiles.add(ResourceManager.MUSIC_DIR + filePath.getFileName().toString());
                    loadSound(ResourceManager.MUSIC_DIR + filePath.getFileName().toString());
                }
            });
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<String> getMusicFiles() {
        return ResourceManager.musicFiles;
    }
    
    public static RawModel getModel(final String file) {
        return ResourceManager.modelMap.get(file);
    }
    
    public static Sound getSound(final String file) {
        return ResourceManager.soundMap.get(file);
    }
    
    public static ModelTexture getTexture(final String file) {
        return ResourceManager.textureMap.get(file);
    }
    
    public static void destroy() {
        for (final ModelTexture modelTexture : ResourceManager.textureMap.values()) {
            GL11.glDeleteTextures(modelTexture.getTextureID());
        }
        ResourceManager.loader.cleanUp();
    }
}
