import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/26/12
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlMenu extends JPanel {
    private int topLeftX, topLeftY;
    private Minimap minimap = new Minimap();
    private SelectedTileInfoMenu selected = new SelectedTileInfoMenu();
    private GameState currentState;

    public ControlMenu(){
        this.setLayout(null);
        this.add(minimap);
        minimap.setLocation(0, 0);
        this.add(selected);
        selected.setLocation(300, 0);
        this.setSize(1000, 100);
    }
    
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.darkGray);
        g2d.fillRect(0, 0, 1000, 100);
    }

    public void setTopLeft(int topLeftX, int topLeftY){
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        minimap.setTopLeftX(topLeftX);
        minimap.setTopLeftY(topLeftY);
    }

    public void setSelectedTile(QuadTreeNode selectedTile){
        selected.setSelectedTile(selectedTile);
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
        selected.setCurrentState(currentState);
    }
    
    public void setSelectedUnit(BaseUnit selectedUnit){
        selected.setSelectedUnit(selectedUnit);
    }
}
