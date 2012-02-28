/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/27/12
 * Time: 11:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class JetFighter extends BaseUnit{

    protected JetFighter(int health, int attackPower, Facing facingDirection, int xPosition, String spritePath, int yPosition, int visionMax, String name) {
        super(health, attackPower, facingDirection, xPosition, spritePath, yPosition, visionMax, name);
        this.movementCosts.put(TileTypes.Grass, 10);
        this.movementCosts.put(TileTypes.Mountains, 20);
        this.movementCosts.put(TileTypes.Trees, 15);
        this.movementCosts.put(TileTypes.Water, 15);
    }
}
