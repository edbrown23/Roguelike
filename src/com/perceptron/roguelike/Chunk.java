package com.perceptron.roguelike;

import com.sun.deploy.panel.ITreeNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 6/24/12
 * Time: 10:16 PM
 */
public class Chunk implements Serializable {
    /** World x coordinate */
    private int x;
    /** World y coordinate */
    private int y;
    /** Useful stuff */
    private int width;
    private int height;
    /** Heightmap */
    private Heightmap heightmap;
    /** A quick reference to the cells with water in this chunk */
    private ArrayList<Cell> waterCells;
    /** A quick reference to the cells with trees */
    private ArrayList<Tree> trees;
    /** A reference to the chunk loader, to request other chunks when things in this chunk leave it */
    private ChunkLoader chunkManager;

    /**
     * Creates a new chunk based on the input heightmap
     * @param map The heightmap
     * @param x The world x coordinate
     * @param y The world y coordinate
     */
    public Chunk(Heightmap map, int x, int y, ChunkLoader chunkManager, int width, int height){
        this.x = x;
        this.y = y;
        this.heightmap = map;
        waterCells = new ArrayList<Cell>();
        trees = new ArrayList<Tree>();
        this.chunkManager = chunkManager;
        this.width = width;
        this.height = height;
    }

    /**
     * Basic copy constructor
     * @param toCopy The Chunk to be copied
     */
    public Chunk(Chunk toCopy){
        this.x = toCopy.x;
        this.y = toCopy.y;
        // Everything else we're copying
        this.heightmap = new Heightmap(toCopy.heightmap);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public Heightmap getHeightmap(){
        return heightmap;
    }

    public void placeWater(int x, int y, float waterAmount){
        heightmap.getCells()[x][y].adjustWaterLevel(waterAmount);
        addWaterCell(heightmap.getCells()[x][y]);
    }

    public void placeTree(int x, int y){
        Tree newTree = new Tree(heightmap.getCells()[x][y], 100.0f);
        trees.add(newTree);
        heightmap.getCells()[x][y].setTree(newTree);
        heightmap.getCells()[x][y].setHasTree(true);
    }

    public void addWaterCell(Cell water){
        this.waterCells.add(water);
    }

    public void updateTrees(double dT){
        for(int i = 0; i < trees.size(); i++){
            trees.get(i).getCell().adjustSaturation(-0.005f); // Trees absorb some of the water in the ground just by existing
            int closeNeighborsCount = 0;
            for(int j = 0; j < trees.size(); j++){
                if(i != j){
                    if(Utilities.cellDistance(trees.get(i).getCell(), trees.get(j).getCell()) < 3){
                        closeNeighborsCount++;
                    }
                }
            }
            // If the tree is surrounded, it will die from overpopulation
            if(closeNeighborsCount > 5){
                trees.get(i).setMarkedForDeath(true);
            }else if(closeNeighborsCount < 2){ // If the tree has few neighbors, it's seeds can travel, so a new tree will be created
                Random numGen = new Random();
                int xOffset = numGen.nextInt(6) - 3;
                int yOffset = numGen.nextInt(6) - 3;
                Cell newTreeCell = heightmap.getCells()[trees.get(i).getCell().getX() + xOffset][trees.get(i).getCell().getY() + yOffset];
                Tree newTree = new Tree(newTreeCell, 100.0f);
                trees.add(newTree);
                newTreeCell.setHasTree(true);
                newTreeCell.setTree(newTree);
            }
        }

        // Process the trees which must be killed off
        for(int i = 0; i < trees.size(); i++){
            // Get the cell of this tree
            Cell currentCell = heightmap.getCells()[trees.get(i).getCell().getX()][trees.get(i).getCell().getY()];
            // Apply time interval to each tree
            trees.get(i).addTime((float)dT);
            // If the tree is set to die, get rid of it
            if(trees.get(i).isMarkedForDeath()){
                trees.remove(i);
                currentCell.setHasTree(false);
                // Perhaps unnecessary, I'm hoping by setting the tree to null the GC will collect it
                currentCell.setTree(null);
            }
        }
    }

    public void updateWater(){
        for(int i = 0; i < waterCells.size(); i++){
            Cell waterCell = waterCells.get(i);
            // More random constants. The land absorbs a bit of water based on the land's current saturation
            waterCell.adjustSaturation((1.0f - waterCell.getSaturation()) / 5.0f);
            // Check for a lower elevation neighbor
            // Get the appropriate cell, while handling the case of a cell being in a neighboring chunk
            Chunk leftChunk;
            Chunk chosenChunk;
            int leftIndex = waterCell.getX() - 1;
            Cell left;
            if(leftIndex < 0){
                leftChunk = chunkManager.getChunk(new Point(x - 1, y));
                left = leftChunk.getHeightmap().getCells()[width - 1][waterCell.getY()];
            }else{
                left = heightmap.getCells()[leftIndex][waterCell.getY()];
                leftChunk = this;
            }
            int rightIndex = waterCell.getX() + 1;
            Chunk rightChunk;
            Cell right;
            if(rightIndex >= width){
                rightChunk = chunkManager.getChunk(new Point(x + 1, y));
                right = rightChunk.getHeightmap().getCells()[0][waterCell.getY()];
            }else{
                right = heightmap.getCells()[rightIndex][waterCell.getY()];
                rightChunk = this;
            }
            int upIndex = waterCell.getY() - 1;
            Chunk upChunk;
            Cell up;
            if(upIndex < 0){
                upChunk = chunkManager.getChunk(new Point(x, y - 1));
                up = upChunk.getHeightmap().getCells()[waterCell.getX()][height - 1];
            }else{
                up = heightmap.getCells()[waterCell.getX()][upIndex];
                upChunk = this;
            }
            int downIndex = waterCell.getY() + 1;
            Chunk downChunk;
            Cell down;
            if(downIndex >= height){
                downChunk = chunkManager.getChunk(new Point(x, y + 1));
                down = downChunk.getHeightmap().getCells()[waterCell.getX()][0];
            }else{
                down = heightmap.getCells()[waterCell.getX()][downIndex];
                downChunk = this;
            }
            // Determine the lowest neighbor
            Cell chosenCell;
            if(left.getTotalHeight() < right.getTotalHeight() && left.getTotalHeight() < up.getTotalHeight() && left.getTotalHeight() < down.getTotalHeight()){
                chosenCell = left;
                if(leftChunk != this){
                    chosenChunk = leftChunk;
                }else{
                    chosenChunk = this;
                }
            }else if(right.getTotalHeight() < left.getTotalHeight() && right.getTotalHeight() < up.getTotalHeight() && right.getTotalHeight() < down.getTotalHeight()){
                chosenCell = right;
                if(rightChunk != this){
                    chosenChunk = rightChunk;
                }else{
                    chosenChunk = this;
                }
            }else if(up.getTotalHeight() < down.getTotalHeight() && up.getTotalHeight() < left.getTotalHeight() && up.getTotalHeight() < right.getTotalHeight()){
                chosenCell = up;
                if(upChunk != this){
                    chosenChunk = upChunk;
                }else{
                    chosenChunk = this;
                }
            }else{
                chosenCell = down;
                if(downChunk != this){
                    chosenChunk = downChunk;
                }else{
                    chosenChunk = this;
                }
            }
            // If the lowest neighbor is lower than the current cell
            if((chosenCell.getTotalHeight() + chosenCell.getWaterLevel()) < (waterCell.getTotalHeight() + waterCell.getWaterLevel())){
                // Send some of the current cells water to the cell
                float waterDelta = waterCell.getTotalHeight() - chosenCell.getTotalHeight();
                waterDelta /= 5;
                chosenCell.adjustWaterLevel(waterDelta);
                // If the cell is not already in the list of water cells, add it
                if(!chosenChunk.getWaterCells().contains(chosenCell)){
                    chosenChunk.getWaterCells().add(chosenCell);
                }
                // Adjust the water cell itself
                waterCell.adjustWaterLevel(-1 * waterDelta);
                // Since we're splitting in half, we can't actually reach zero, so we're cheating
                if(waterCell.getWaterLevel() < 0.001){
                    waterCell.setWaterLevel(0.0f);
                    chosenChunk.getWaterCells().remove(waterCell);
                }
                // Erosion occurs after the water has moved
                float erosionFactor = waterDelta / 4;
                waterCell.adjustHeight((int)(-1 * erosionFactor));
                chosenCell.adjustHeight((int)erosionFactor);
                if(erosionFactor > 1){
                    waterCell.setType(CellType.Sand);
                }
            }
        }
    }

    public Cell getHNeighbor(Cell center, int direction){
        if(center.getX() == 0){
            return chunkManager.getChunk(new Point(x + direction, y)).getHeightmap().getCells()[width - 1][center.getY()];
        }
        return heightmap.getCells()[center.getX() + direction][center.getY()];
    }

    public ArrayList<Cell> getWaterCells(){
        return waterCells;
    }

    public ArrayList<Tree> getTrees(){
        return trees;
    }
}
