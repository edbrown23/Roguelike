import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/12/12
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Infantry extends BaseUnit {

    protected Infantry(int health, int attackPower, Facing facingDirection, int xPosition, String spritePath, int yPosition, int visionMax, String name) {
        super(health, attackPower, facingDirection, xPosition, spritePath, yPosition, visionMax, name);
        this.restrictedTiles.add(TileTypes.Water);
        this.movementCosts.put(TileTypes.Mountains, 80);
        this.movementCosts.put(TileTypes.Trees, 50);
        this.movementCosts.put(TileTypes.Grass, 20);
    }
}
