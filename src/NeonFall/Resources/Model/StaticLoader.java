package NeonFall.Resources.Model;

/**
 * Usage:
 * Author: lbald
 * Last Update: 01.01.2016
 */
public class StaticLoader {

    public static RawModel loadCube(Loader loader) {
        float[] verticesArray = {
                1, -1, -1,
                1, -1, 1,
                -1, -1, 1,
                -1, -1, -1,
                1, 1, -1,
                1, 1, 1,
                -1, 1, 1,
                -1, 1, -1,
        };
        float[] normalsArray = {
                0, 0, -1,
                0, 0, 1,
                -1, 0, 0,
                -1, 0, 0,
                0, 0, -1,
                0, 0, 1,
                -1, 0, 0,
                0, 0, -1,
        };
        int[] indicesArray = {
                1, 2, 3,
                7, 6, 5,
                4, 5, 1,
                5, 6, 2,
                2, 6, 7,
                0, 3, 7,
                0, 1, 3,
                4, 7, 5,
                0, 4, 1,
                1, 5, 2,
                3, 2, 7,
                4, 0, 7
        };

        return new RawModel(loader.loadToVAO(verticesArray, normalsArray, indicesArray));
    }

    public static RawModel loadPlane(Loader loader) {
        float[] verticesArray = {
                -1, -1, 0,
                -1, 1, 0,
                1, 1, 0,
                1, -1, 0,
        };
        float[] textureCoords = {
                0, 1,
                0, 0,
                1, 0,
                1, 1,
        };
        float[] normalsArray = {
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
                0, 1, 0,
        };
        int[] indicesArray = {
                0, 1, 2,
                0, 2, 3
        };
        return loader.loadToVAO(verticesArray, textureCoords, normalsArray, indicesArray);
    }

    public static RawModel loadSurface(Loader loader) {
        float[] verticesArray  = {
                0, 0,
                1, 0,
                0, 1,
                1, 1,
        };
        int[] indices = {
                0, 1, 2,
                3, 2, 1,
        };
        return loader.loadToVAO(verticesArray, indices, 2);
    }
}
