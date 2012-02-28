import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/17/12
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnitMovementState extends GameState {
    private GameCanvas game;
    private int topLeftX = 0, topLeftY = 0;
    private int width, height;
    private BaseUnit selectedUnit;
    private boolean unitMoving = false;

    public UnitMovementState(int t, GameState previousState) {
        super(t);
        //The following is a hack, it can certainly be improved
        // Essentially transferring everything from the edit state to this state
        WorldEditState prev = (WorldEditState)previousState;
        game = prev.getGame();
        width = prev.getWidth();
        height = prev.getHeight();
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
        }else if(e.getKeyCode() == KeyEvent.VK_M){
            //System.out.println(selectedUnit.getName());
            if(selectedUnit != null){
                System.out.println("Please click on a destination!");
                unitMoving = true;
            }else{
                System.out.println("No unit selected!");
            }
        }
    }

    public void update(double elapsedTime){
        game.setElapsedTime(elapsedTime);
    }

    public void render(){
        //System.out.println("Unit State");
        game.setCurrentState(this);
        game.setTopCoords(topLeftX, topLeftY);
        game.repaint();
    }

    public Component getGUIComponent(){
        return game;
    }

    public void setMouseState(MouseEvent e){
        game.mouseSelect(e.getX(), e.getY());
        // This is a terribly bad no fun way of doing this, which will crap on everything in large groups of units. FIX!
        if(!unitMoving){
            for(BaseUnit currentUnit : game.getMap().getUnits()){
                if(currentUnit.getxPosition() == game.getSelectedNode().getX() && currentUnit.getyPosition() == game.getSelectedNode().getY()){
                    selectedUnit = currentUnit;
                    game.setSelectedUnit(selectedUnit);
                    break;
                }else{
                    selectedUnit = null;
                }
            }
        }

        // The below is probably terrible too. I should basically rethink all user input handling
        if(unitMoving){
            AStarNode desiredPoint = new AStarNode(game.getSelectedNode().getX() / 20, game.getSelectedNode().getY() / 20);
            game.setUnitPath(selectedUnit, desiredPoint);
            unitMoving = false;
            System.out.println("You've selected a destination at " + desiredPoint.x * 20 + " " + desiredPoint.y * 20);
        }else{
            game.setUnitPath(selectedUnit, null);
        }
    }
}
