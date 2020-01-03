// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Model;

public class StaticLoader
{
    public static RawModel loadCube(final Loader loader) {
        final float[] verticesArray = { 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f };
        final float[] normalsArray = { 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f };
        final int[] indicesArray = { 1, 2, 3, 7, 6, 5, 4, 5, 1, 5, 6, 2, 2, 6, 7, 0, 3, 7, 0, 1, 3, 4, 7, 5, 0, 4, 1, 1, 5, 2, 3, 2, 7, 4, 0, 7 };
        return new RawModel(loader.loadToVAO(verticesArray, normalsArray, indicesArray));
    }
    
    public static RawModel loadPlane(final Loader loader) {
        final float[] verticesArray = { -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f };
        final float[] textureCoords = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f };
        final float[] normalsArray = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f };
        final int[] indicesArray = { 0, 1, 2, 0, 2, 3 };
        return loader.loadToVAO(verticesArray, textureCoords, normalsArray, indicesArray);
    }
    
    public static RawModel loadSurface(final Loader loader) {
        final float[] verticesArray = { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f };
        final int[] indices = { 0, 1, 2, 3, 2, 1 };
        return loader.loadToVAO(verticesArray, indices, 2);
    }
}
