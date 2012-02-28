import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 1/28/12
 * Time: 10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseUnit {
    protected int health;
    protected int maxHealth;
    protected int attackPower;
    protected Facing facingDirection;
    protected int visionMax;
    protected BufferedImage sprite;
    protected int xPosition;
    protected int yPosition;
    protected LinkedList<AStarNode> movementPath;
    protected String name;
    protected LinkedList<TileTypes> restrictedTiles = new LinkedList<TileTypes>();
    protected Hashtable<TileTypes, Integer> movementCosts = new Hashtable<TileTypes, Integer>();

    protected BaseUnit(int health, int attackPower, Facing facingDirection, int xPosition, String spritePath, int yPosition, int visionMax, String name) {
        try {
            BufferedImage temp = ImageIO.read(new File(spritePath));
            sprite = SpriteTools.setTransparent(temp);
            this.health = health;
            maxHealth = health;
            this.attackPower = attackPower;
            this.facingDirection = facingDirection;
            this.xPosition = xPosition;
            this.yPosition = yPosition;
            this.visionMax = visionMax;
            this.name = name;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void render(Graphics2D g2d, int topLeftX, int topLeftY){
        float rotationAngle = 0.0f;
        switch(facingDirection){
            case East:
                rotationAngle = 0.0f;
                break;
            case West:
                rotationAngle = (float)Math.PI;
                break;
            case North:
                rotationAngle = (float)((3 * Math.PI) / 2);
                break;
            case South:
                rotationAngle = (float)((Math.PI) / 2);
                break;
        }
        AffineTransform oldTransform = g2d.getTransform();
        AffineTransform newTransform = sprite.createGraphics().getTransform();
        newTransform.translate(xPosition + 10, yPosition + 10);
        newTransform.rotate(rotationAngle);
        newTransform.translate(-1 * (xPosition + 10), -1 * (yPosition + 10));
        g2d.transform(newTransform);
        g2d.drawImage(sprite, xPosition - topLeftX, yPosition - topLeftY, 20, 20, null);

        g2d.setTransform(oldTransform);
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public String getName(){
        return name;
    }
    
    public int getCurrentHealth() {
        return health;
    }
    
    public int getMaxHealth(){
        return maxHealth;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    public Facing getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(Facing facingDirection) {
        this.facingDirection = facingDirection;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getVisionMax() {
        return visionMax;
    }

    public void setVisionMax(int visionMax) {
        this.visionMax = visionMax;
    }

    public void setMovementPath(LinkedList<AStarNode> path){
        this.movementPath = path;
    }

    public LinkedList<AStarNode> getMovementPath(){
        return movementPath;
    }
}
