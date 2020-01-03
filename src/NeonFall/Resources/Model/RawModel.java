// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Model;

public class RawModel
{
    private int vaoID;
    private int vertexCount;
    
    public RawModel(final int vaoID, final int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }
    
    public RawModel(final RawModel model) {
        this.vaoID = model.getVaoID();
        this.vertexCount = model.getVertexCount();
    }
    
    public int getVaoID() {
        return this.vaoID;
    }
    
    public int getVertexCount() {
        return this.vertexCount;
    }
}
