// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Model;

import org.joml.Vector3f;

public class Material
{
    private Vector3f emission;
    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private float shininess;
    
    public Material(final Material material) {
        this.emission = new Vector3f(0.0f, 0.0f, 0.0f);
        this.ambient = new Vector3f(1.0f, 1.0f, 1.0f);
        this.diffuse = new Vector3f(1.0f, 1.0f, 1.0f);
        this.specular = new Vector3f(1.0f, 1.0f, 1.0f);
        this.shininess = 1.0f;
        this.ambient = new Vector3f(material.ambient);
        this.emission = new Vector3f(material.emission);
        this.diffuse = new Vector3f(material.diffuse);
        this.specular = new Vector3f(material.specular);
        this.shininess = material.shininess;
    }
    
    public Material(final Vector3f emission, final Vector3f ambient, final Vector3f diffuse, final Vector3f specular, final float shininess) {
        this.emission = new Vector3f(0.0f, 0.0f, 0.0f);
        this.ambient = new Vector3f(1.0f, 1.0f, 1.0f);
        this.diffuse = new Vector3f(1.0f, 1.0f, 1.0f);
        this.specular = new Vector3f(1.0f, 1.0f, 1.0f);
        this.shininess = 1.0f;
        this.emission = emission;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
    
    public void setEmission(final Vector3f emission) {
        this.emission = emission;
    }
    
    public void setAmbient(final Vector3f ambient) {
        this.ambient = ambient;
    }
    
    public void setDiffuse(final Vector3f diffuse) {
        this.diffuse = diffuse;
    }
    
    public void setSpecular(final Vector3f specular) {
        this.specular = specular;
    }
    
    public void setShininess(final float shininess) {
        this.shininess = shininess;
    }
    
    public Vector3f getEmission() {
        return this.emission;
    }
    
    public Vector3f getAmbient() {
        return this.ambient;
    }
    
    public Vector3f getDiffuse() {
        return this.diffuse;
    }
    
    public Vector3f getSpecular() {
        return this.specular;
    }
    
    public float getShininess() {
        return this.shininess;
    }
}
