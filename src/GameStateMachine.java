import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/14/12
 * Time: 12:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameStateMachine {
    private GameState currentState;
    private double currentStateElapsedTime = 0.0;
    private LinkedList<GameState> stateQueue = new LinkedList<GameState>();

    /**
     * Adds state to queue, and sets current state to the added state
     * @param newState the new state
     */
    public void addState(GameState newState){
        stateQueue.addLast(newState);
    }

    public void rotateState(){
        if(currentState != null){
            stateQueue.addLast(currentState);
        }
        currentState = stateQueue.remove();
    }

    public void changeState(GameState state){
        currentState = state;
    }

    public void updateCurrentState(double elapsedTime){
        currentStateElapsedTime += elapsedTime;
        if(currentStateElapsedTime > currentState.getTimeOut() && currentState.getTimeOut() != -1){
            currentStateElapsedTime = 0.0;
            currentState = stateQueue.remove();
        }
        currentState.update(elapsedTime);
    }

    public void renderCurrentState(){
        currentState.render();
    }

    public GameState getCurrentState(){
        return currentState;
    }

    public void informStateofInput(KeyEvent keyEvent){
        currentState.setKeyboardState(keyEvent);
    }
}
