import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 1/29/12
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class GameCanvas extends JPanel {
    private SimpleMap map;
    private ControlMenu menu;
    private int topLeftX, topLeftY;
    private LinkedList<AStarNode> unitPath;
    private GameState currentState;

    public GameCanvas(int width, int height){
        map = new SimpleMap(width, height);
        this.setSize(500, 250);
        map.createMapFromPerlinNoise(100, 150, 200, 255);
        QuadTree.setupSprites();
        map.getMapTreeRoots();
        menu = new ControlMenu();
        this.setLayout(null);
        this.add(menu);
        menu.setLocation(0,500);
    }

    public void setTopCoords(int topX, int topY){
        topLeftX = topX;
        topLeftY = topY;
        menu.setTopLeft(topLeftX / 20, topLeftY / 20);
    }

    public void setUnitPath(BaseUnit startingUnit, AStarNode goal){
        if(goal != null){
            unitPath = map.calculatePath(startingUnit, goal);
            if(unitPath != null){
                map.informUnitOfNewPath(startingUnit, unitPath);
            }else{
                System.out.println("No path to destination!");
            }
        }else{
            unitPath = null;
        }
    }

    public int getTopLeftX(){
        return topLeftX;
    }

    public int getTopLeftY(){
        return topLeftY;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D)g;

        map.render(g2d, topLeftX, topLeftY);
        g2d.setColor(Color.black);
        for(int x = 0; x <= 1000; x += 20){
            g2d.drawLine(x, 0, x, 500);
        }
        for(int y = 0; y <= 500; y += 20){
            g2d.drawLine(0, y, 1000, y);
        }

        map.renderSelection(g2d, topLeftX, topLeftY);

        if(unitPath != null){
            g2d.setColor(Color.blue);
            for(AStarNode currentPoint : unitPath){
                g2d.fillRect((currentPoint.x * 20) + 5 - topLeftX, (currentPoint.y * 20) + 5 - topLeftY, 10, 10);
            }
        }

        menu.setSelectedTile(QuadTree.getSelectedNode());
        // is this evil? I don't know, stack overflow says it is
        menu.repaint();
    }

    public void addUnit(BaseUnit unit){
        map.addUnit(unit);
    }

    public void mouseSelect(int x, int y){
        int selectedX = x + topLeftX;
        int selectedY = y + topLeftY;

        QuadTree.selectTile(map.getMapTreeRoots()[0][0], selectedX, selectedY);
        QuadTree.selectTile(map.getMapTreeRoots()[1][0], selectedX, selectedY);
        QuadTree.selectTile(map.getMapTreeRoots()[2][0], selectedX, selectedY);
    }

    public QuadTreeNode getSelectedNode(){
        return QuadTree.getSelectedNode();
    }

    public void changeCell(TileTypes newType){
        map.changeCell(newType);
    }

    public void setElapsedTime(double elapsedTime){
        map.update(elapsedTime);
        map.updateUnitFrames(elapsedTime);
        map.setTimeOfDay((map.getTimeOfDay() + elapsedTime / 100) % (2 * Math.PI));
    }

    public SimpleMap getMap(){
        return map;
    }

    public void setCurrentState(GameState currentState) {
        this.currentState = currentState;
        menu.setCurrentState(currentState);
    }

    public void setSelectedUnit(BaseUnit selectedUnit){
        menu.setSelectedUnit(selectedUnit);
    }
}
