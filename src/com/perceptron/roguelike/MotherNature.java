package com.perceptron.roguelike;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 7/19/12
 * Time: 10:24 PM
 */
public class MotherNature {
    /**
     * Timescale indicates the world time which passes on each frame update.
     * ie a timeScale of 1 represents real time, < 1 is slower than real time,
     * and > 1 is faster than real time
     */
    private float timeScale = 1.0f;
    /** A reference to the chunkManager */
    private ChunkLoader chunkManager;
    /** The regions of the map potentially visible by the player. Also the portions
     * of the world which are updated every frame
     */
    private HashMap<Point, Chunk> visibleRegions;
    /** The date manager for the world, to keep track of time */
    private RogueDateManager dateManager;
    /** The angle of the sun above the horizon */
    private float sunAngle;

    public MotherNature(ChunkLoader chunkManager){
        this.chunkManager = chunkManager;
        this.dateManager = new RogueDateManager();
    }

    /**
     * Update the entities in the player's visible regions, or those immediately
     * surrounding the player
     * @param dT the time interval, in mS
     */
    public void update(double dT){
        dateManager.update((float)dT * timeScale);
        updateSunAngle();
        visibleRegions = chunkManager.getChunksInView();
        if(visibleRegions.values() != null && visibleRegions.size() > 0){
            for(Chunk c : visibleRegions.values()){
                if(c != null){
                    // My hackish time and date based "event" system
                    if(dateManager.isNewSecond()){
                        c.updateWater();
                    }
                    if(dateManager.isNewMinute()){
                        c.updateTrees(dT * timeScale);
                    }
                }
            }
        }
        dateManager.clearFlags();

    }

    private void updateSunAngle(){
        int numSeconds = dateManager.getSecond() + (dateManager.getMinute() * 60) + (dateManager.getHour() * 60 * 60);
        if(numSeconds < 43200){ // 43200 is the seconds in half a day
            float ratio = (numSeconds / 21600.0f) - 1; // 21600 is the seconds in a quarter day
            sunAngle = (float)Math.asin(ratio);
        }else{
            float ratio = ((numSeconds - 43200) / 21600.0f) - 1;
            sunAngle = -1.0f * (float)Math.asin(ratio);
        }

    }

    /**
     * Adds a bit of water to the cell the player is currently standing on
     * @param worldPoint The Chunk coordinate of the player's position
     * @param playerPoint The players position within the chunk
     */
    public void addWaterCell(Point worldPoint, Point playerPoint){
        visibleRegions.get(worldPoint).placeWater(playerPoint.x, playerPoint.y, 2.0f);
    }

    /**
     * Places a tree at the player's position
     * @param worldPoint The chunk coordinate of the player
     * @param playerPoint The player's position within a chunk
     */
    public void addTree(Point worldPoint, Point playerPoint){
        visibleRegions.get(worldPoint).placeTree(playerPoint.x, playerPoint.y);
    }

    public HashMap<Point, Chunk> getVisibleRegions(){
        return visibleRegions;
    }

    public RogueDateManager getDateManager(){
        return dateManager;
    }

    public void slowDown(){
        timeScale -= (timeScale * 0.1);
        if(timeScale <= 0){
            timeScale = 0.0f;
        }
    }

    public void speedUp(){
        timeScale += (0.1 * timeScale);
        if(timeScale >= 1000){
            timeScale = 1000.0f;
        }
        System.out.println(timeScale);
    }

    public float getSunAngle() {
        return sunAngle;
    }
}
