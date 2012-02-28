import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;

/**
 * Probably going to be reimplemented. This class should remember if a unit can access a certain tile
 * User: Eric
 * Date: 1/28/12
 * Time: 11:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tile {

    private boolean unitAccessible;
    private boolean blocksLineOfSight;
    private BufferedImage terrainSprite;
    private TileTypes tileType;

    public Tile(boolean unitAccessible, boolean blocksLineOfSight, String path, TileTypes type) {
        this.unitAccessible = unitAccessible;
        this.blocksLineOfSight = blocksLineOfSight;
        tileType = type;
        try {
            terrainSprite = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Tile(){
        tileType = TileTypes.NULL;
    }

    public TileTypes getTileType() {
        return tileType;
    }

    public void setTileType(TileTypes tileType) {
        this.tileType = tileType;
    }

    public boolean isUnitAccessible() {
        return unitAccessible;
    }

    public void setUnitAccessible(boolean unitAccessible) {
        this.unitAccessible = unitAccessible;
    }

    public boolean isBlocksLineOfSight() {
        return blocksLineOfSight;
    }

    public void setBlocksLineOfSight(boolean blocksLineOfSight) {
        this.blocksLineOfSight = blocksLineOfSight;
    }

    public BufferedImage getTerrainSprite() {
        return terrainSprite;
    }

    public void setTerrainSprite(BufferedImage terrainSprite) {
        this.terrainSprite = terrainSprite;
    }
    
    public void render(Graphics2D g2d, int x, int y){
        AffineTransform transform = g2d.getTransform();
        AffineTransform scaleTransform = new AffineTransform();
        scaleTransform.scale(2, 2);
        g2d.transform(scaleTransform);
        g2d.drawImage(terrainSprite, null, x, y);
        g2d.setTransform(transform);
    }
    
    public String toString(){
        return unitAccessible + " unitAccessible, " + blocksLineOfSight + " blocksLineOfSight" + terrainSprite.toString();
    }
}
