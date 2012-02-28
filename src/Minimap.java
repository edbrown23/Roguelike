import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/26/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class Minimap extends JPanel {
    private BufferedImage map;
    private int topLeftX, topLeftY;


    public Minimap(){
        try{
            map = QuadTree.toCompatibleImage(ImageIO.read(new File("Sprites/perlinNoise.png")));
        }catch(IOException e){
            e.printStackTrace();
        }
        this.setSize(300, 100);
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(map, 0, 0, 300, 100, null);

        g2d.setColor(Color.black);
        g2d.drawRect(Math.round(topLeftX  / 2.56f), Math.round(topLeftY / 2.56f), 20, 10);
    }

    public void setTopLeftX(int topLeftX) {
        this.topLeftX = topLeftX;
    }

    public void setTopLeftY(int topLeftY) {
        this.topLeftY = topLeftY;
    }
}
