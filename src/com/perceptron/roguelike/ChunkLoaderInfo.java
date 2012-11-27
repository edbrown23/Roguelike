package com.perceptron.roguelike;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 6/24/12
 * Time: 8:47 PM
 */
public class ChunkLoaderInfo {
    public float newPlayerX;
    public float newPlayerY;
    public int newPlayerWorldX;
    public int newPlayerWorldY;

    public ChunkLoaderInfo(float x, float y, int wX, int wY){
        newPlayerX = x;
        newPlayerY = y;
        newPlayerWorldX = wX;
        newPlayerWorldY = wY;
    }
}
