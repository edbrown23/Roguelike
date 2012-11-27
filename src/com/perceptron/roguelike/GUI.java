package com.perceptron.roguelike;

import com.sun.deploy.panel.ITreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 6/25/12
 * Time: 8:37 PM
 */
public class GUI extends JFrame{
    private ChunkLoader chunkManager;
    private JPanel drawingArea;
    private Player player;
    private boolean quitting = false;
    private int chunkWidth = 1000;
    private int chunkHeight = 1000;
    private float zoomFactor = 1.0f;
    private float maxZoom = 50f;
    private MotherNature motherNature;

    public GUI(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(942, 614);
        this.setVisible(true);

        drawingArea = new DrawingArea();
        this.add(drawingArea, BorderLayout.CENTER);

        this.addKeyListener(new CustomKeyboardListener());

        player = new Player(350, 350, 5, 0, 0, chunkWidth, chunkHeight);
        chunkManager = new ChunkLoader(chunkWidth, chunkHeight, 0.6f);
        motherNature = new MotherNature(chunkManager);

        player.addObserver(chunkManager);
        player.setXYZ(350, 350, 5);
        chunkManager.startManager();
    }

    public void gameLoop(){
        while(!quitting){
            long preUpdateRender = System.nanoTime(); // Time before doing anything

            // update the player
            player.update(Utilities.OPTIMAL_TIME / 1000000);
            // update the world
            motherNature.update(Utilities.OPTIMAL_TIME / 1000000);

            // render
            drawingArea.repaint();

            long updateRenderTime = System.nanoTime() - preUpdateRender; // Time it took to update and render
            long sleepTime = (Utilities.OPTIMAL_TIME - updateRenderTime) / 1000000; // The time left assuming 60 fps optimum. Sleep for that time
            if(sleepTime > 0){
                try{
                    Thread.sleep(sleepTime);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
            this.requestFocus();
            //System.out.println(1.0f / ((System.nanoTime() - preUpdateRender) / 1000000000f)); // Prints framerate
        }
        chunkManager.shutdown();
        System.exit(0);
    }

    private class DrawingArea extends JPanel{
        public HashMap<Point, Chunk> chunks;
        private Point treePoints[] = new Point[chunkWidth * chunkHeight];
        int treePointer = 0;

        public DrawingArea(){
            for(int i = 0; i < treePoints.length; i++){
                treePoints[i] = new Point(0, 0);
            }
        }

        public void paintComponent(Graphics g){
            this.chunks = motherNature.getVisibleRegions();

            Graphics2D g2d = (Graphics2D)g;
            // Clear the background
            g2d.setColor(Color.white);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

            // Draw the cells around the player
            int pX = (int)player.getxPosition();
            int pY = (int)player.getyPosition();

            int xRange = Math.round(17 * zoomFactor);
            int yRange = Math.round(11 * zoomFactor);
            int cellSizeX = Math.round((float) this.getWidth() / (float) xRange);
            int cellSizeY = Math.round((float)this.getHeight() / (float)yRange);

            // Iterate over the area around the player to determine which cells to load
            //System.out.println(zoomFactor + " " + xRange + " " + yRange);
            for(int dX = (-1 * xRange / 2); dX <= (xRange / 2) + 1; dX++){
                for(int dY = (-1 * yRange / 2); dY <= (yRange / 2) + 1; dY++){

                    // Get the coordinate of the cell to draw in absolute coordinates
                    int cellX = (pX + dX);
                    int cellY = (pY + dY);
                    // convert the coordinate to chunk coordinates and get the chunk from which the cell will be drawn
                    Point cellP = Utilities.convertWorldPoint(new Point(cellX, cellY), chunkWidth, chunkHeight);
                    Chunk chunkToDraw = chunks.get(cellP);

                    // Get the coordinate of the cell within its chunk
                    int inChunkX = cellX - (chunkWidth * cellP.x);
                    int inChunkY = cellY - (chunkHeight * cellP.y);
                    if(inChunkX == chunkWidth){
                        inChunkX = chunkWidth - 1;
                    }
                    if(inChunkY == chunkHeight){
                        inChunkY = chunkHeight - 1;
                    }
                    // get the heightmap point at the above coordinates
                    Cell theCell;
                    CellType cell;
                    theCell = chunkToDraw.getHeightmap().getCells()[inChunkX][inChunkY];
                    cell = theCell.getType();
                    // convert dX and dY into drawing coordinates
                    int drawX = dX + Math.round((float)xRange / 2.0f) - 1;
                    int drawY = dY + Math.round((float)yRange / 2.0f) - 1;
                    // Get the color of the cell itself, based on it's type
                    Color cellColor = new Color(255, 255, 255);

                    switch(cell){
                        case Mountain:
                            cellColor = new Color(51, 27, 0);
                            break;
                        case Dirt:
                            cellColor = new Color(112, 81, 31);
                            break;
                        case Grass:
                            cellColor = new Color(0, 150, 2);
                            break;
                        case Snow:
                            cellColor = new Color(232, 232, 232);
                            break;
                        case Sand:
                            cellColor = new Color(255, 203, 95);
                            break;
                    }
                    g2d.setColor(cellColor);
                    g2d.fillRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);
                    Color saturation = new Color(165, 119, 47, 255 * (int)(1.0 - theCell.getSaturation()));
                    g2d.setColor(saturation);
                    g2d.fillRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);

                    if(theCell.getWaterLevel() > 0){
                        Color waterColor;
                        try{
                            if(theCell.getWaterLevel() > 30){
                                waterColor = new Color(0, 10, 130, 200);
                            }else{
                                waterColor = new Color(0, 10, 220 - ((int)theCell.getWaterLevel() * 3), 110 + ((int)theCell.getWaterLevel() * 3));
                            }
                            g2d.setColor(waterColor);
                        }catch(IllegalArgumentException e){
                            System.out.println(theCell.getWaterLevel());
                        }
                        g2d.fillRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);
                    }
                    if(theCell.hasTree()){
                        treePoints[treePointer].x = drawX;
                        treePoints[treePointer].y = drawY;
                        treePoints[treePointer].info = theCell.getTree().getAge();
                        treePointer++;
                        if(treePointer >= treePoints.length){
                            treePointer = 0;
                        }
                    }
                    if(dX == 0 && dY == 0){
                        //player.setZPosition(theCell.getTotalHeight());
                    }
                    g2d.setColor(new Color(50, 50, 50));
                    g2d.drawRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);

//                    String coords = drawX + " " + drawY;
//                    g2d.drawString(coords, drawX * cellSizeX, drawY * cellSizeY + cellSizeY);


                }
            }

            // This is still a crappy way of doing things, as the tree's aren't factored into the depth stuff
            for(Point p : treePoints){
                if(p.info > 0.0f){
                    int drawX = p.x;
                    int drawY = p.y;
                    if(drawX < xRange && drawX > 0 && drawY < yRange && drawY > 0){
                        g2d.setColor(new Color(99, 71, 50));
                        g2d.fillRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);
                        g2d.setColor(new Color(68 + (int)(p.info / 2), 128, 68, 50));
                        float radius = p.info / 100.0f; // for now, all trees have a lifespan of 100
                        radius = radius * 10;
                        for(float degree = 0; degree < 2.0f * Math.PI; degree += Math.PI / 32.0f){
                            for(int dist = 0; dist < radius; dist++){
                                int xOffset = (int)Math.round((Math.cos(degree) * dist));
                                int yOffset = (int)Math.round((Math.sin(degree) * dist));
                                //float xOffset = (float)(r * Math.cos(angle));
                                //float yOffset = (float)(r * Math.sin(angle));
                                int adjX = drawX + xOffset;
                                int adjY = drawY + yOffset;
                                g2d.fillRect(adjX * cellSizeX, adjY * cellSizeY, cellSizeX, cellSizeY);
                                //System.out.println((xOffset) + " " + (yOffset));
                            }
                        }
                    }
                }
            }

            treePointer = 0;

            // Draw the player
            g2d.setColor(Color.blue);
            // Determine the player's proper rendering position
            int playerX = Math.round((((float)xRange / 2.0f) * cellSizeX));
            int playerY = Math.round((((float)yRange / 2.0f) * cellSizeY));
            // Since the grid might be even on one or both axes depending on zoom level, this must be factored into the player's position
            if(xRange % 2 == 0){
                playerX -= Math.round(cellSizeX / 2.0f);
            }
            if(yRange % 2 == 0){
                playerY -= Math.round(cellSizeY / 2.0f);
            }
            g2d.fillOval(playerX, playerY, 10, 10);

            // Attempt to light the level
            for(int dX = (-1 * xRange / 2); dX <= (xRange / 2) + 1; dX++){
                for(int dY = (-1 * yRange / 2); dY <= (yRange / 2) + 1; dY++){

                    // Get the coordinate of the cell to draw in absolute coordinates
                    int cellX = (pX + dX);
                    int cellY = (pY + dY);
                    // convert the coordinate to chunk coordinates and get the chunk from which the cell will be drawn
                    Point cellP = Utilities.convertWorldPoint(new Point(cellX, cellY), chunkWidth, chunkHeight);
                    Chunk chunkToDraw = chunks.get(cellP);

                    // Get the coordinate of the cell within its chunk
                    int inChunkX = cellX - (chunkWidth * cellP.x);
                    int inChunkY = cellY - (chunkHeight * cellP.y);
                    if(inChunkX == chunkWidth){
                        inChunkX = chunkWidth - 1;
                    }
                    if(inChunkY == chunkHeight){
                        inChunkY = chunkHeight - 1;
                    }
                    int drawX = dX + Math.round((float)xRange / 2.0f) - 1;
                    int drawY = dY + Math.round((float)yRange / 2.0f) - 1;
                    // get the heightmap point at the above coordinates
                    Cell theCell;
                    CellType cell;
                    Color cellColor;
                    theCell = chunkToDraw.getHeightmap().getCells()[inChunkX][inChunkY];
                    // get the gray composite that indicates player height
                    if(theCell.getLandHeight() < player.getzPosition()){
                        int hDiff = (int)(player.getzPosition() - theCell.getLandHeight());
                        if(hDiff >= 200){
                            cellColor = new Color(15, 19, 65, 200);
                        }else{
                            cellColor = new Color(15,22, 105, hDiff);
                        }
                        g2d.setColor(cellColor);
                        g2d.fillRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);
                    }else{
                        int hDiff = (int)(theCell.getLandHeight() - player.getzPosition());
                        if(hDiff >= 200){
                            cellColor = new Color(188, 139, 98, 200);
                        }else{
                            cellColor = new Color(223, 94, 10, hDiff);
                        }

                        g2d.setColor(cellColor);
                        g2d.fillRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);
                    }
                    // In my little world, the sun rises from the west, and sets in the east
                    float sun = motherNature.getSunAngle();
                    // If the sun angle is > 0, the sun is above the horizon
                    //if(sun > 0){
                        int dir = 0;
                        // If the sun < Math.PI, the sun is to the left, otherwise it's to the right
                        if(motherNature.getDateManager().getHour() < 12){
                            dir = 1;
                        }else{
                            dir = -1;
                        }
                        int walkDistance = (int)(Math.abs((1.57f - sun)) * 10);
                        boolean litCell = true;
                        for(int offset = 1; offset < walkDistance; offset++){
                            float angle = (float)Math.atan((theCell.getLandHeight() - chunkToDraw.getHNeighbor(theCell, (offset * dir)).getLandHeight()) / Math.abs(offset));
                            angle = angle * 0.3f;
                            //float angleToBeat = -1.0f * dir * (1.57f - sun);
                            //System.out.println(angleToBeat);
                            if(angle > (sun)){
                                litCell = false;
                                break;
                            }
                        }
                        if(!litCell || sun < 0){
                            int alpha = Math.round(((1.57f - sun) / 1.57f) * 220);
                            if(alpha > 220){
                                alpha = 220;
                            }
                            g2d.setColor(new Color(35, 33, 34, alpha));
                            g2d.fillRect(drawX * cellSizeX, drawY * cellSizeY, cellSizeX, cellSizeY);
                        }
                    //}
                }
            }
            int dateX = this.getWidth() - 200;
            int dateY = this.getHeight() - 25;
            g2d.setColor(Color.black);
            g2d.drawString(motherNature.getDateManager().toString() + " " + motherNature.getSunAngle(), dateX, dateY);
        }
    }

    public void processKeys(){
        float sprintMultiplier;
        if(Keyboard.leftShift){
            sprintMultiplier = 4.0f;
        }else{
            sprintMultiplier = 1.0f;
        }

        if(Keyboard.UP){
            player.setyVelocity(-0.05f * sprintMultiplier);
            //player.adjustVelocity(0.0f, -0.05f * sprintMultiplier, 0.0f);
            if(Keyboard.DOWN){
                player.setyVelocity(0.0f);
                //player.adjustVelocity(0.0f, 0.05f * sprintMultiplier, 0.0f);
            }
        }
        if(Keyboard.DOWN){
            player.setyVelocity(0.05f * sprintMultiplier);
            //player.adjustVelocity(0.0f, 0.05f * sprintMultiplier, 0.0f);
            if(Keyboard.UP){
                player.setyVelocity(0.0f);
                //player.adjustVelocity(0.0f, -0.05f * sprintMultiplier, 0.0f);
            }
        }
        if(!Keyboard.UP && !Keyboard.DOWN){
            player.setyVelocity(0.0f);
        }

        if(Keyboard.LEFT){
            player.setxVelocity(-0.05f * sprintMultiplier);
            //player.adjustVelocity(-0.05f * sprintMultiplier, 0.0f, 0.0f);
            if(Keyboard.RIGHT){
                player.setxVelocity(0.0f);
                //player.adjustVelocity(0.05f * sprintMultiplier, 0.0f, 0.0f);
            }
        }
        if(Keyboard.RIGHT){
            player.setxVelocity(0.05f * sprintMultiplier);
            //player.adjustVelocity(0.05f * sprintMultiplier, 0.0f, 0.0f);
            if(Keyboard.LEFT){
                player.setxVelocity(0.0f);
                //player.adjustVelocity(-0.05f * sprintMultiplier, 0.0f, 0.0f);
            }
        }
        if(!Keyboard.RIGHT && !Keyboard.LEFT){
            player.setxVelocity(0.0f);
        }

        if(Keyboard.SPACE){
            player.adjustVelocity(0.0f, 0.0f, 0.1f);
            if(Keyboard.CONTROL){
                player.adjustVelocity(0.0f, 0.0f, -0.1f);
            }
        }
        if(Keyboard.CONTROL){
            player.adjustVelocity(0.0f, 0.0f, -0.1f);
            if(Keyboard.SPACE){
                player.adjustVelocity(0.0f, 0.0f, 0.1f);
            }
        }
        if(!Keyboard.SPACE && !Keyboard.CONTROL){
            player.setzVelocity(0.0f);
        }

        if(Keyboard.U){
            zoomFactor -= 0.5f;
            if(zoomFactor <= 0.5f){
                zoomFactor = 0.5f;
            }
        }

        if(Keyboard.I){
            zoomFactor += 0.5f;
            if(zoomFactor >= maxZoom){
                zoomFactor = maxZoom;
            }
        }

        if(Keyboard.W){
            Point wP = Utilities.convertWorldPoint(new Point((int)player.getxPosition(), (int)player.getyPosition()), chunkWidth, chunkHeight);
            Point pP = new Point((int)player.getInChunkX(), (int)player.getInChunkY());
            motherNature.addWaterCell(wP, pP);
        }

        if(Keyboard.T){
            Point wP = Utilities.convertWorldPoint(new Point((int)player.getxPosition(), (int)player.getyPosition()), chunkWidth, chunkHeight);
            Point pP = new Point((int)player.getInChunkX(), (int)player.getInChunkY());
            motherNature.addTree(wP, pP);
        }

        if(Keyboard.ESCAPE){
            quitting = true;
        }
        if(Keyboard.TILDE){
            System.out.println("Console!");
        }

        if(Keyboard.B){
            motherNature.speedUp();
        }
        if(Keyboard.V){
            motherNature.slowDown();
        }
    }

    private class CustomKeyboardListener implements KeyListener{
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()){
                case KeyEvent.VK_UP:
                    Keyboard.UP = true;
                    break;
                case KeyEvent.VK_DOWN:
                    Keyboard.DOWN = true;
                    break;
                case KeyEvent.VK_LEFT:
                    Keyboard.LEFT = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    Keyboard.RIGHT = true;
                    break;
                case KeyEvent.VK_ESCAPE:
                    Keyboard.ESCAPE = true;
                    break;
                case KeyEvent.VK_DEAD_TILDE:
                    Keyboard.TILDE = true;
                    break;
                case KeyEvent.VK_SPACE:
                    Keyboard.SPACE = true;
                    break;
                case KeyEvent.VK_CONTROL:
                    Keyboard.CONTROL = true;
                    break;
                case KeyEvent.VK_W:
                    Keyboard.W = true;
                    break;
                case KeyEvent.VK_U :
                    Keyboard.U = true;
                    break;
                case KeyEvent.VK_I:
                    Keyboard.I = true;
                    break;
                case KeyEvent.VK_T:
                    Keyboard.T = true;
                    break;
                case KeyEvent.VK_V:
                    Keyboard.V = true;
                    break;
                case KeyEvent.VK_B:
                    Keyboard.B = true;
                    break;
                case KeyEvent.VK_SHIFT:
                    Keyboard.leftShift = true;
                    break;
            }
            processKeys();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            switch(e.getKeyCode()){
                case KeyEvent.VK_UP:
                    Keyboard.UP = false;
                    break;
                case KeyEvent.VK_DOWN:
                    Keyboard.DOWN = false;
                    break;
                case KeyEvent.VK_LEFT:
                    Keyboard.LEFT = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    Keyboard.RIGHT = false;
                    break;
                case KeyEvent.VK_ESCAPE:
                    Keyboard.ESCAPE = false;
                    break;
                case KeyEvent.VK_DEAD_TILDE:
                    Keyboard.TILDE = false;
                    break;
                case KeyEvent.VK_SPACE:
                    Keyboard.SPACE = false;
                    break;
                case KeyEvent.VK_CONTROL:
                    Keyboard.CONTROL = false;
                    break;
                case KeyEvent.VK_W:
                    Keyboard.W = false;
                    break;
                case KeyEvent.VK_U :
                    Keyboard.U = false;
                    break;
                case KeyEvent.VK_I:
                    Keyboard.I = false;
                    break;
                case KeyEvent.VK_T:
                    Keyboard.T = false;
                    break;
                case KeyEvent.VK_V:
                    Keyboard.V = false;
                    break;
                case KeyEvent.VK_B:
                    Keyboard.B = false;
                    break;
                case KeyEvent.VK_SHIFT:
                    Keyboard.leftShift = false;
                    break;
            }
            processKeys();
        }
    }
}
