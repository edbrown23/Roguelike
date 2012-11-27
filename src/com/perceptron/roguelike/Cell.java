package com.perceptron.roguelike;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 7/8/12
 * Time: 9:51 PM
 */
public class Cell implements Serializable {
    /** The height of this cell */
    private int height;
    /** The type of this cell */
    private CellType type;
    /** The height of water above this cell */
    private float waterLevel;
    /** useful coordinates */
    private int x;
    private int y;
    /** the percent water saturation of this cell, to indicate drynee */
    private float saturation = 1.0f;
    /** flag indicating whether there is a tree on this cell */
    private boolean hasTree;
    /** The tree located on this cell, if appropriate */
    private Tree tree;

    public Cell(int height, CellType type, float waterLevel, int x, int y) {
        this.height = height;
        this.type = type;
        this.waterLevel = waterLevel;
        this.x = x;
        this.y = y;
    }

    public float getLandHeight(){
        return height;
    }

    public float getTotalHeight() {
        return height + waterLevel;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public float getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
    }

    public boolean hasTree() {
        return hasTree;
    }

    public void setHasTree(boolean hasTree) {
        this.hasTree = hasTree;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    /**
     * Utility water level adjustment method, to avoid excessive method calls
     * @param delta The amount of water to change by
     */
    public void adjustWaterLevel(float delta){
        this.waterLevel += delta;
    }

    /**
     * Just like the water level adjuster
     * @param delta The amound to adjust the height of the cell by
     */
    public void adjustHeight(int delta){
        this.height += delta;
        if(height < 10){
            height = 10;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void adjustSaturation(float percent){
        saturation += percent;
        if(saturation <= 0.0f){
            saturation = 0.0f;
        }
    }

    public float getSaturation(){
        return saturation;
    }
}
