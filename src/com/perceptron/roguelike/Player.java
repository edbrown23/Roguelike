package com.perceptron.roguelike;

import javax.swing.text.AttributeSet;
import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 6/25/12
 * Time: 8:46 PM
 */
public class Player extends Observable {
    private float xPosition;
    private float yPosition;
    private float zPosition;
    private int worldXPosition;
    private int worldYPosition;
    private float xVelocity;
    private float yVelocity;
    private float zVelocity;
    private float maxVelocity;
    private int chunkWidth;
    private int chunkHeight;

    public Player(float xPosition, float yPosition, float zPosition, int worldXPosition, int worldYPosition, int chunkW, int chunkH) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.zPosition = zPosition;
        this.worldXPosition = worldXPosition;
        this.worldYPosition = worldYPosition;
        this.xVelocity = 0.0f;
        this.yVelocity = 0.0f;
        this.zVelocity = 0.0f;
        this.maxVelocity = 0.1f;
        this.chunkWidth = chunkW;
        this.chunkHeight = chunkH;
    }

    public void setXYZ(float x, float y, float z){
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        notifyChunkLoader();
    }

    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
        notifyChunkLoader();
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
        notifyChunkLoader();
    }

    public int getWorldXPosition() {
        return worldXPosition;
    }

    public void setWorldXPosition(int worldXPosition) {
        this.worldXPosition = worldXPosition;
    }

    public int getWorldYPosition() {
        return worldYPosition;
    }

    public void setWorldYPosition(int worldYPosition) {
        this.worldYPosition = worldYPosition;
    }

    public void adjustVelocity(float x, float y, float z){
        this.xVelocity += x;
        this.yVelocity += y;
        this.zVelocity += z;
        if(xVelocity >= maxVelocity){
            xVelocity = maxVelocity;
        }else if(xVelocity <= (-1 * maxVelocity)){
            xVelocity = (-1 * maxVelocity);
        }
        if(yVelocity >= maxVelocity){
            yVelocity = maxVelocity;
        }else if(yVelocity <= (-1 * maxVelocity)){
            yVelocity = (-1 * maxVelocity);
        }
        if(zVelocity >= maxVelocity){
            zVelocity = maxVelocity;
        }else if(zVelocity <= (-1 * maxVelocity)){
            zVelocity = (-1 * maxVelocity);
        }
    }

    public void setVelocity(float x, float y, float z){
        xVelocity = x;
        yVelocity = y;
        zVelocity = z;
    }

    public void setxVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setyVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    public void setzVelocity(float zVelocity) {
        this.zVelocity = zVelocity;
    }

    public float getzPosition() {
        return zPosition;
    }

    public void setZPosition(float z){
        this.zPosition = z;
    }

    public float getInChunkX(){
        return (xPosition - (chunkWidth * worldXPosition));
    }

    public float getInChunkY(){
        return (yPosition - (chunkHeight * worldYPosition));
    }

    public void update(float dT){
        xPosition += (xVelocity * dT);
        yPosition += (yVelocity * dT);
        zPosition += (zVelocity * dT);
        if(zPosition >= 255){
            zPosition = 255;
        }else if(zPosition <= 0){
            zPosition = 0;
        }
        worldXPosition = (int)Math.floor((double)(xPosition / chunkWidth));
        worldYPosition = (int)Math.floor((double)(yPosition / chunkHeight));

        notifyChunkLoader();
    }

    public void notifyChunkLoader(){
        ChunkLoaderInfo info = new ChunkLoaderInfo(xPosition, yPosition, worldXPosition, worldYPosition);
        setChanged();
        notifyObservers(info);
    }
}
