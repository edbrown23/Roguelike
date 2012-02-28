import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/14/12
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GameState {
    /** The number of milliseconds to spend in the current state, or -1 to stay there forever or till forced to leave */
    private int timeOut;
    protected Component guiComp;
    
    public GameState(int t){
        timeOut = t;
    }

    public void setGuiComp(Component gui){
        guiComp = gui;
    }
    
    public int getTimeOut(){
        return timeOut;
    }

    public void update(double elapsedTime){}

    public void setKeyboardState(KeyEvent keyEvent){}

    public void setMouseState(MouseEvent e){}
    
    public void render(){}
    
    public Component getGUIComponent(){
        return guiComp;
    }
}
