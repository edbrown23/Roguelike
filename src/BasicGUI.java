import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 1/29/12
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicGUI extends JFrame {
    private GameStateMachine gameMachine = new GameStateMachine();
    private boolean isRunning = true;

    public BasicGUI(){
        KeyHandler kHandler = new KeyHandler();
        this.addKeyListener(kHandler);
        this.setLayout(new BorderLayout());

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1004, 628);

        WorldEditState worldEditState = new WorldEditState(-1, 768, 256);
        gameMachine.addState(worldEditState);
        UnitMovementState unitMovementState = new UnitMovementState(-1, worldEditState);
        gameMachine.addState(unitMovementState);
        gameMachine.rotateState();
        this.add(gameMachine.getCurrentState().getGUIComponent(), BorderLayout.CENTER);
        this.setResizable(false);
        this.setLocation(100, 100);
        this.setVisible(true);

        MouseHandler mHandler = new MouseHandler();
        gameMachine.getCurrentState().getGUIComponent().addMouseListener(mHandler);

        double MAXFPS = 1000000000 / 30.0f; // should stand for 30 frames per second

        double currentTime, elapsedTime = 0.0;

        while(isRunning){

            currentTime = System.nanoTime();
            gameMachine.updateCurrentState(elapsedTime / 1000000); // change nanoseconds to milliseconds
            gameMachine.renderCurrentState();
            elapsedTime = System.nanoTime() - currentTime;
            //System.out.println(elapsedTime / 1000000);
            // If it took less than the fps to render and update, sleep for the rest of the time
            if(elapsedTime < MAXFPS){
                int milliElapsedTime = (int)((MAXFPS - elapsedTime) / 1000000);
                try{
                    Thread.sleep(milliElapsedTime);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            this.requestFocus();
        }
    }

    private class KeyHandler implements KeyListener{

        @Override
        public void keyTyped(KeyEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                isRunning = false;
            }else if(e.getKeyCode() == KeyEvent.VK_U){
                gameMachine.rotateState();
            }else{
                gameMachine.informStateofInput(e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private class MouseHandler implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            gameMachine.getCurrentState().setMouseState(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
