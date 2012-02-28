import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/14/12
 * Time: 12:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class WorldEditState extends GameState {
    private GameCanvas game;
    private int topLeftX = 0, topLeftY = 0;
    private int width, height;

    public WorldEditState(int t, int width, int height) {
        super(t);
        this.width = width;
        this.height = height;
        game = new GameCanvas(width, height);
        this.setGuiComp(game); // You have to do this!
    }

    public void setKeyboardState(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_RIGHT){
            if(topLeftX < (width * 20) - 1000){
                topLeftX += 20;
            }
        }else if(e.getKeyCode() == KeyEvent.VK_LEFT){
            if(topLeftX > 0){
                topLeftX -= 20;
            }
        }else if(e.getKeyCode() == KeyEvent.VK_UP){
            if(topLeftY > 0){
                topLeftY -= 20;
            }
        }else if(e.getKeyCode() == KeyEvent.VK_DOWN){
            if(topLeftY < (height * 20) - 500){
                topLeftY += 20;
            }
        }else if(e.getKeyCode() == KeyEvent.VK_G){
            game.changeCell(TileTypes.Grass);
        }else if(e.getKeyCode() == KeyEvent.VK_M){
            game.changeCell(TileTypes.Mountains);
        }else if(e.getKeyCode() == KeyEvent.VK_W){
            game.changeCell(TileTypes.Water);
        }else if(e.getKeyCode() == KeyEvent.VK_T){
            game.changeCell(TileTypes.Trees);
        }else if(e.getKeyCode() == KeyEvent.VK_B){
            game.addUnit(new Infantry(10, 10, Facing.East, game.getSelectedNode().getX(), "Sprites/Infantry.png", game.getSelectedNode().getY(), 5, "Infantry"));
        }else if(e.getKeyCode() == KeyEvent.VK_F){
            game.addUnit(new JetFighter(10, 20, Facing.East, game.getSelectedNode().getX(), "Sprites/JetFighter.png", game.getSelectedNode().getY(), 8, "Jet Fighter"));
        }
    }

    public void setMouseState(MouseEvent e){
        game.mouseSelect(e.getX(), e.getY());
    }

    public void update(double elapsedTime){
        game.setElapsedTime(elapsedTime);
    }

    public void render(){
        //System.out.println("Edit State!");
        game.setCurrentState(this);
        game.setTopCoords(topLeftX, topLeftY);
        game.repaint();
    }

    public Component getGUIComponent(){
        return game;
    }

    public GameCanvas getGame(){
        return game;
    }

    public int getTopLeftX(){
        return topLeftX;
    }

    public int getTopLeftY(){
        return topLeftY;
    }

    public int getWidth(){
        return  width;
    }

    public int getHeight(){
        return height;
    }
}
