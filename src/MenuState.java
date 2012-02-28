import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 1/29/12
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MenuState {
    private BufferedImage background; // Background splash
    private int stateTimeout; // How long the screen stays around. If -1, then infinite, until user input

    public MenuState(int stateTimeout, BufferedImage background) {
        this.stateTimeout = stateTimeout;
        this.background = background;
    }

    public void render(Graphics2D g2d){
         g2d.drawImage(background, 0, 0, background.getWidth(), background.getHeight(), null);
    }
    
    public int getStateTimeout(){
        return stateTimeout;
    }
}
