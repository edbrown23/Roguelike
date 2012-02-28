import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/18/12
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpriteTools {
    public static BufferedImage setTransparent(BufferedImage inputSprite){
        BufferedImage sprite = new BufferedImage(inputSprite.getWidth(), inputSprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = sprite.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        g2d.drawImage(inputSprite, null, 0, 0);
        g2d.dispose();
        for(int y = 0; y < inputSprite.getHeight(); y++){
            for(int x = 0; x < inputSprite.getWidth(); x++){
                Color tempC = new Color(inputSprite.getRGB(x, y));
                int r = tempC.getRed();
                int g = tempC.getGreen();
                int b = tempC.getBlue();
                if(r == 255 && b == 250){
                    sprite.setRGB(x, y, 0x00000000);
                }
            }
        }
        return sprite;
    }
}
