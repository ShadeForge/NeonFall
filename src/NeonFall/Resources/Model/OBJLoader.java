// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Model;

import java.util.List;
import java.util.Iterator;
import org.joml.Vector2f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;

public class OBJLoader
{
    public static RawModel loadObjModel(final String fileName, final Loader loader) {
        FileReader fr;
        try {
            fr = new FileReader(new File(fileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("*OBJ-Datei " + fileName + " konnte nicht gefunden werden.");
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
        final BufferedReader reader = new BufferedReader(fr);
        final ArrayList<Vector3f> positionList = new ArrayList<Vector3f>();
        final ArrayList<Vector3f> normalList = new ArrayList<Vector3f>();
        final ArrayList<Vector2f> texList = new ArrayList<Vector2f>();
        final ArrayList<Triangle> indexList = new ArrayList<Triangle>();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                final String[] split = line.split(" ");
                if (line.startsWith("v ")) {
                    positionList.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                }
                else if (line.startsWith("vt ")) {
                    texList.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
                }
                else if (line.startsWith("vn ")) {
                    normalList.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                }
                else {
                    if (!line.startsWith("f ")) {
                        continue;
                    }
                    indexList.add(new Triangle(split[1]));
                    indexList.add(new Triangle(split[2]));
                    indexList.add(new Triangle(split[3]));
                }
            }
            reader.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        final float[] position = new float[indexList.size() * 3];
        final float[] texCoords = new float[indexList.size() * 2];
        final float[] normals = new float[indexList.size() * 3];
        final int[] indices = new int[indexList.size()];
        int currentIndex = 0;
        for (final Triangle triangle : indexList) {
            final Vector3f pos = positionList.get(triangle.v);
            position[currentIndex * 3] = pos.x;
            position[currentIndex * 3 + 1] = pos.y;
            position[currentIndex * 3 + 2] = pos.z;
            final Vector2f tex = texList.get(triangle.vt);
            texCoords[currentIndex * 2] = tex.x;
            texCoords[currentIndex * 2 + 1] = tex.y;
            final Vector3f normal = normalList.get(triangle.vn);
            normals[currentIndex * 3] = normal.x;
            normals[currentIndex * 3 + 1] = normal.y;
            normals[currentIndex * 3 + 2] = normal.z;
            indices[currentIndex] = currentIndex++;
        }
        return loader.loadToVAO(position, texCoords, normals, indices);
    }
    
    public static RawModel loadObjModelOld(final String fileName, final Loader loader) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File(fileName));
        }
        catch (FileNotFoundException e) {
            System.err.println("*OBJ-Datei " + fileName + ".obj konnte nicht gefunden werden.");
            e.printStackTrace();
        }
        final BufferedReader reader = new BufferedReader(fr);
        final List<Vector3f> vertices = new ArrayList<Vector3f>();
        final List<Vector3f> normals = new ArrayList<Vector3f>();
        final List<Vector2f> textures = new ArrayList<Vector2f>();
        final List<Integer> indices = new ArrayList<Integer>();
        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] texturesArray = null;
        int[] indicesArray = null;
        try {
            String line;
            while (true) {
                line = reader.readLine();
                final String[] currentLine = line.split(" ");
                if (line.startsWith("# ")) {
                    continue;
                }
                if (line.startsWith("v ")) {
                    final Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                }
                else if (line.startsWith("vt ")) {
                    final Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                }
                else {
                    if (!line.startsWith("vn ")) {
                        break;
                    }
                    final Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                }
            }
            if (line.startsWith("f ")) {
                texturesArray = new float[vertices.size() * 2];
                normalsArray = new float[vertices.size() * 3];
            }
            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                }
                else {
                    final String[] currentLine = line.split(" ");
                    final String[] vertex2 = currentLine[1].split("/");
                    final String[] vertex3 = currentLine[2].split("/");
                    final String[] vertex4 = currentLine[3].split("/");
                    processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
                    processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);
                    processVertex(vertex4, indices, textures, normals, texturesArray, normalsArray);
                    line = reader.readLine();
                }
            }
            reader.close();
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];
        int vertexPointer = 0;
        for (final Vector3f vertex5 : vertices) {
            verticesArray[vertexPointer++] = vertex5.x;
            verticesArray[vertexPointer++] = vertex5.y;
            verticesArray[vertexPointer++] = vertex5.z;
        }
        for (int i = 0; i < indices.size(); ++i) {
            indicesArray[i] = indices.get(i);
        }
        return loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
    }
    
    private static void processVertex(final String[] vertexData, final List<Integer> indices, final List<Vector2f> textures, final List<Vector3f> normals, final float[] texturesArray, final float[] normalsArray) {
        final int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        final Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        texturesArray[currentVertexPointer * 2] = currentTex.x;
        texturesArray[currentVertexPointer * 2 + 1] = 1.0f - currentTex.y;
        final Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
    }
    
    private static class Triangle
    {
        public int v;
        public int vt;
        public int vn;
        
        public Triangle(final String input) {
            final String[] split = input.split("/");
            this.v = Integer.parseInt(split[0]) - 1;
            this.vt = Integer.parseInt(split[1]) - 1;
            this.vn = Integer.parseInt(split[2]) - 1;
        }
    }
}
