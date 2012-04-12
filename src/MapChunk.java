import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 4/11/12
 * Time: 10:36 PM
 * Each MapChunk should function as a world map cell, with coordinates and a map. This class is
 * really just a wrapper for now for the as yet unproven chunk manager.
 */
public class MapChunk implements Serializable {
    private SimpleMap chunk;
    private int worldX, worldY;

    public MapChunk(SimpleMap c, int wX, int wY){
        chunk = c;
        worldX = wX;
        worldY = wY;
    }

    public int getWorldX() {
        return worldX;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }

    public int getWorldY() {
        return worldY;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }
}
