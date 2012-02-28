import java.awt.*;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 1/29/12
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MenuStateMachine {
    private MenuState currentState;
    private LinkedList<MenuState> states = new LinkedList<MenuState>();
    
    public void addState(MenuState s){
        states.add(s);
    }
    
    public void changeState(MenuState s){
        currentState = s;
    }
    
    public void renderCurrentState(Graphics2D g2d){
       currentState.render(g2d);
    }

    public void advanceState(){
        if(currentState.getStateTimeout() != -1){
            //Handle State timing. Probably only for intro splash
            int startTime = (int)(System.currentTimeMillis() / 1000.0);
            int currentTime = startTime;
            while((currentTime - startTime) < currentState.getStateTimeout()){
                currentTime = (int)(System.currentTimeMillis() / 1000.0);
            }
        }
    }
}
