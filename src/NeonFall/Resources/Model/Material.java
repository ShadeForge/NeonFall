package NeonFall.Resources.Model;

import org.joml.Vector3f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public class Material {
    private Vector3f emission = new Vector3f(0, 0, 0);
    private Vector3f ambient  = new Vector3f(1, 1, 1);
    private Vector3f diffuse  = new Vector3f(1, 1, 1);
    private Vector3f specular = new Vector3f(1, 1, 1);    

    private float shininess = 1.0f;

    public Material(Material material) {
        this.ambient = new Vector3f(material.ambient);
        this.emission = new Vector3f(material.emission);
        this.diffuse = new Vector3f(material.diffuse);
        this.specular = new Vector3f(material.specular);
        this.shininess = material.shininess;
    }

    public Material(Vector3f emission, Vector3f ambient,  Vector3f diffuse, Vector3f specular, float shininess) {
        this.emission  = emission;
        this.ambient   = ambient;
        this.diffuse	   = diffuse;
        this.specular  = specular;
        this.shininess = shininess;
    }

    public void setEmission(Vector3f emission) {
        this.emission = emission;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public void setSpecular(Vector3f specular) {
        this.specular = specular;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public Vector3f getEmission() {
        return emission;
    }

    public Vector3f getAmbient() {
        return ambient;
    }
    
    public Vector3f getDiffuse() {
        return diffuse;
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public float getShininess() {
        return shininess;
    }

}
