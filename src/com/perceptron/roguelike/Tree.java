package com.perceptron.roguelike;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 7/19/12
 * Time: 10:48 PM
 */
public class Tree {
    /** How long the tree will live on it's own */
    private float lifeSpan;
    /** The current age of the tree. When lifeSpan - age == 0, the tree will die */
    private float age;
    /** Trees may have different colored leaves, specified here */
    private Color leafColor;
    /** The cell in which this tree resides */
    private Cell cell;
    /** Flag indicating this tree will die on the next update, usually from overcrowding */
    private boolean markedForDeath;

    public Tree(Cell cell, float lifeSpan){
        this.cell = cell;
        this.lifeSpan = lifeSpan;
        this.age = 1.0f;
        this.leafColor = new Color(68, 128, 68);
    }

    public Cell getCell() {
        return cell;
    }

    public boolean isMarkedForDeath() {
        return markedForDeath;
    }

    public void setMarkedForDeath(boolean markedForDeath) {
        this.markedForDeath = markedForDeath;
    }

    public void addTime(float dT){
        age += (dT / 1000);
        if(lifeSpan - age <= 0.0f){
            markedForDeath = true;
            System.out.println("TREE WILL DIE");
        }
        this.leafColor = new Color(68 + (int)(age / 2), leafColor.getGreen(), leafColor.getBlue(), 50);
    }

    public Color getLeafColor() {
        return leafColor;
    }

    public float getLifeSpan() {
        return lifeSpan;
    }

    public float getAge() {
        return age;
    }
}
