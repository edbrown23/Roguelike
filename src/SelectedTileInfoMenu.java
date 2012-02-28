import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/26/12
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectedTileInfoMenu extends JPanel {
    private QuadTreeNode selectedTile;
    private GameState currentState;
    private BaseUnit selectedUnit;

    public SelectedTileInfoMenu(){
        this.setSize(400, 100);
    }

    // This method exemplifies the problem with my current rendering. I have to have all states rendering practices in this method, and it ends up looking really ugly
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.gray);
        g2d.fillRect(0, 0, 400, 100);

        if(currentState.getClass() == UnitMovementState.class){
            if(selectedUnit != null){
                g2d.drawImage(selectedUnit.getSprite(), 15, 15, 70, 70, null);
                g2d.setColor(Color.black);
                g2d.drawString(selectedUnit.getName(), 100, 15);
                g2d.drawString("Health: " + selectedUnit.getCurrentHealth() + "/" + selectedUnit.getMaxHealth(), 100, 30);
                g2d.fillRect(100, 35, selectedUnit.getMaxHealth() * 10, 10);
                g2d.setColor(Color.red);
                g2d.fillRect(100, 35, selectedUnit.getCurrentHealth() * 10, 10);
                g2d.setColor(Color.black);
                
                g2d.drawString("Attack Power: " + selectedUnit.getAttackPower(), 100, 60);
                g2d.drawString("X: " + selectedUnit.getxPosition() + " Y: " + selectedUnit.getyPosition(), 100, 75);
            }
        }else{
            if(selectedTile != null){
                switch(selectedTile.getTile()){
                    case Water:
                        g2d.drawImage(QuadTree.waterSprite[0], 15, 15, 70, 70, null);
                        g2d.setColor(Color.black);
                        g2d.drawString("Water", 100, 50);
                        break;
                    case Grass:
                        g2d.drawImage(QuadTree.grassSprite, 15, 15, 70, 70, null);
                        g2d.setColor(Color.black);
                        g2d.drawString("Grass", 100, 50);
                        break;
                    case Mountains:
                        g2d.drawImage(QuadTree.mountainSprite, 15, 15, 70, 70, null);
                        g2d.setColor(Color.black);
                        g2d.drawString("Mountains", 100, 50);
                        break;
                    case Trees:
                        g2d.drawImage(QuadTree.treeSpriteTable.get(20), 15, 15, 70, 70, null);
                        g2d.setColor(Color.black);
                        g2d.drawString("Trees", 100, 50);
                        break;
                }
            }else{
                g2d.setColor(Color.black);
                g2d.fillRect(15, 15, 70, 70);
            }
        }
    }

    public void setSelectedTile(QuadTreeNode selectedTile) {
        this.selectedTile = selectedTile;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
    }

    public void setSelectedUnit(BaseUnit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }
}
