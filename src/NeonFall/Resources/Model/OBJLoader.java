package NeonFall.Resources.Model;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class OBJLoader {

	public static RawModel loadObjModel(String fileName, Loader loader) {
		FileReader fr;

		try {
			fr = new FileReader(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("*OBJ-Datei " + fileName + " konnte nicht gefunden werden.");
			e.printStackTrace();
			System.exit(-1);
			return null;
		}

		BufferedReader reader = new BufferedReader(fr);
        ArrayList<Vector3f> positionList = new ArrayList<>();
        ArrayList<Vector3f> normalList = new ArrayList<>();
        ArrayList<Vector2f> texList = new ArrayList<>();
        ArrayList<Triangle> indexList = new ArrayList<>();
		String line;

		try {
            while((line = reader.readLine()) != null) {

				String split[] = line.split(" ");

				if(line.startsWith("v ")) {
                    positionList.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
				} else if(line.startsWith("vt ")) {
                    texList.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
				} else if(line.startsWith("vn ")) {
                    normalList.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
				} else if(line.startsWith("f ")) {
                    indexList.add(new Triangle(split[1]));
                    indexList.add(new Triangle(split[2]));
                    indexList.add(new Triangle(split[3]));
                }
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

        float[] position = new float[indexList.size() * 3];
        float[] texCoords = new float[indexList.size() * 2];
        float[] normals = new float[indexList.size() * 3];
        int[] indices = new int[indexList.size()];
        int currentIndex = 0;

        for (Triangle triangle : indexList) {
            Vector3f pos = positionList.get(triangle.v);
            position[currentIndex * 3] = pos.x;
            position[currentIndex * 3 + 1] = pos.y;
            position[currentIndex * 3 + 2] = pos.z;

            Vector2f tex = texList.get(triangle.vt);
            texCoords[currentIndex * 2] = tex.x;
            texCoords[currentIndex * 2 + 1] = tex.y;

            Vector3f normal = normalList.get(triangle.vn);
            normals[currentIndex * 3] = normal.x;
            normals[currentIndex * 3 + 1] = normal.y;
            normals[currentIndex * 3 + 2] = normal.z;

            indices[currentIndex] = currentIndex++;
        }

		return loader.loadToVAO(position, texCoords, normals, indices);
	}

    private static class Triangle {
        public int v;
        public int vt;
        public int vn;

        public Triangle(String input) {
            String[] split = input.split("/");

            v = Integer.parseInt(split[0]) - 1;
            vt = Integer.parseInt(split[1]) - 1;
            vn = Integer.parseInt(split[2]) - 1;
        }
    }


    public static RawModel loadObjModelOld(String fileName, Loader loader) {

        FileReader fr = null;
        try {
            fr = new FileReader(new File(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("*OBJ-Datei " + fileName + ".obj konnte nicht gefunden werden.");
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        String line;
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Integer> indices = new ArrayList<Integer>();

        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] texturesArray = null;
        int[] indicesArray = null;

        try {
            while (true) {
                line = reader.readLine();
                String[] currentLine = line.split(" ");
                if (line.startsWith("# ")) {
                    continue;
                }
                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    vertices.add(vertex);
                } else if(line.startsWith("vt ")){
                    Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]),
                            Float.parseFloat(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    texturesArray = new float[vertices.size() *2];
                    normalsArray = new float[vertices.size() * 3];
                    break;
                } else
                    break;
            }

            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                processVertex(vertex1, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, texturesArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, texturesArray, normalsArray);

                line = reader.readLine();
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        return loader.loadToVAO(verticesArray, texturesArray, normalsArray, indicesArray);
    }

    private static void processVertex(String[] vertexData,  List<Integer> indices,
                                      List<Vector2f> textures, List<Vector3f> normals,
                                      float[] texturesArray, float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);

        Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        texturesArray[currentVertexPointer*2] = currentTex.x;
        texturesArray[currentVertexPointer*2+1] = 1 - currentTex.y;

        Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNorm.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
    }
}
