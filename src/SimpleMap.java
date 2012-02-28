import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 1/29/12
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleMap {
    private int mapWidth;
    private int mapHeight;
    private QuadTreeNode[][] mapTreeRoots = new QuadTreeNode[3][1];
    private LinkedList<BaseUnit> units = new LinkedList<BaseUnit>();
    private BaseUnit movingUnit;
    private boolean unitMoving = false;
    private boolean[][] fog;
    private TileTypes[][] referenceTiles;
    private double timeOfDay;
    private double elapsedFrameTime = 0.0;
    private int multiplier = 20;

    public SimpleMap(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        fog = new boolean[mapWidth][mapHeight];
        for(int y = 0; y < mapHeight; y++){
            for(int x = 0; x < mapWidth; x++){
                fog[x][y] = true;
            }
        }
        referenceTiles = new TileTypes[mapWidth][mapHeight];
    }

    /**
     * Takes various parameters and creates a random perlin noise map based on them
     * @param waterLevel the top of the water threshold in the perlin heightmap
     * @param grassLevel the top of the grass threshold in the perlin heightmap
     * @param treeLevel the top of the tree threshold in the perlin heightmap
     * @param mountainLevel the top of the mountain threshold in the perlin heightmap
     */
    public void createMapFromPerlinNoise(int waterLevel, int grassLevel, int treeLevel, int mountainLevel){
        QuadTreeNode[][] baseTreeTiles =  new QuadTreeNode[mapWidth][mapHeight];;
        float[][] finalNoiseArray = PerlinNoise.GeneratePerlinNoise(mapWidth, mapHeight, 8);
        BufferedImage debugImage = new BufferedImage(mapWidth, mapHeight, BufferedImage.TYPE_4BYTE_ABGR);
        for(int y = 0; y < mapHeight; y++){
            for(int x = 0; x < mapWidth; x++){
                int grey = (int)(255 * finalNoiseArray[x][y]);
                if(grey < waterLevel){
                    baseTreeTiles[x][y] = new QuadTreeNode(new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), TileTypes.Water, 20, 20, x * multiplier, y * multiplier);
                    debugImage.setRGB(x, y, new Color(0, 0, 255).getRGB());
                    referenceTiles[x][y] = TileTypes.Water;
                }else if(grey > waterLevel && grey < grassLevel){
                    baseTreeTiles[x][y] = new QuadTreeNode(new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), TileTypes.Grass, 20, 20, x * multiplier, y * multiplier);
                    debugImage.setRGB(x, y, new Color(0, 255, 0).getRGB());
                    referenceTiles[x][y] = TileTypes.Grass;
                }else if(grey > grassLevel && grey < treeLevel){
                    baseTreeTiles[x][y] = new QuadTreeNode(new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), TileTypes.Trees, 20, 20, x * multiplier, y * multiplier);
                    debugImage.setRGB(x, y, new Color(50, 150, 50).getRGB());
                    referenceTiles[x][y] = TileTypes.Trees;
                }else {
                    baseTreeTiles[x][y] = new QuadTreeNode(new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), new QuadTreeNode(), TileTypes.Mountains, 20, 20, x * multiplier, y * multiplier);
                    debugImage.setRGB(x, y, new Color(150, 100, 25).getRGB());
                    referenceTiles[x][y] = TileTypes.Mountains;
                }
            }
        }
        try{
            ImageIO.write(debugImage, "png", new File("Sprites/perlinNoise.png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        mapTreeRoots = QuadTree.generateQuadTree(baseTreeTiles, mapWidth, mapHeight);
    }

    public void render(Graphics2D g2d, int topLeftX, int topLeftY){
        if((mapTreeRoots[0][0].getX() - topLeftX) < (1000) && (mapTreeRoots[0][0].getX() + mapTreeRoots[0][0].getWidth() - topLeftX) >= 0){
            if((mapTreeRoots[0][0].getY() - topLeftY) < (500) && (mapTreeRoots[0][0].getY() + mapTreeRoots[0][0].getHeight() - topLeftY) >= 0){
                QuadTree.quadTreeRender(g2d, mapTreeRoots[0][0], topLeftX, topLeftY);
            }
        }
        if((mapTreeRoots[1][0].getX() - topLeftX) < (1000) && (mapTreeRoots[1][0].getX() + mapTreeRoots[1][0].getWidth() - topLeftX) >= 0){
            if((mapTreeRoots[1][0].getY() - topLeftY) < (500) && (mapTreeRoots[1][0].getY() + mapTreeRoots[1][0].getHeight() - topLeftY) >= 0){
                QuadTree.quadTreeRender(g2d, mapTreeRoots[1][0], topLeftX, topLeftY);
            }
        }
        if((mapTreeRoots[2][0].getX() - topLeftX) < (1000) && (mapTreeRoots[2][0].getX() + mapTreeRoots[2][0].getWidth() - topLeftX) >= 0){
            if((mapTreeRoots[2][0].getY() - topLeftY) < (500) && (mapTreeRoots[2][0].getY() + mapTreeRoots[2][0].getHeight() - topLeftY) >= 0){
                QuadTree.quadTreeRender(g2d, mapTreeRoots[2][0], topLeftX, topLeftY);
            }
        }

        for(BaseUnit currentUnit : units){
            currentUnit.render(g2d, topLeftX, topLeftY);
        }

        this.calculateFog(topLeftX, topLeftY);
        this.renderFog(g2d, topLeftX, topLeftY);
    }

    public void renderSelection(Graphics2D g2d, int topLeftX, int topLeftY){
        QuadTree.renderSelection(g2d, topLeftX, topLeftY);
    }

    public void renderFog(Graphics2D g2d, int topLeftX, int topLeftY){
        int fogScale = (int)((Math.sin(timeOfDay) + 1) * 100);
        for(int y = topLeftY; y < (topLeftY + 500); y += 20){
            for(int x = topLeftX; x < (topLeftX + 1000); x += 20){
                if(fog[Math.round(x / 20.0f)][Math.round(y / 20.0f)]){
                    g2d.setColor(new Color(0, 0, 0, fogScale));
                    g2d.fillRect(x - topLeftX, y - topLeftY, 20, 20);
                }else{
                    g2d.setColor(new Color(0, 255, 0, 0));
                    g2d.fillRect(x - topLeftX, y - topLeftY, 20, 20);
                }
            }
        }
    }

    public void addUnit(BaseUnit newUnit){
        units.add(newUnit);
    }

    public void calculateFog(int topLeftX, int topLeftY){
        for(int y = topLeftY; y < (topLeftY + 500); y += 20){
            for(int x = topLeftX; x < (topLeftX + 1000); x += 20){
                if(!fog[Math.round(x / 20.0f)][Math.round(y / 20.0f)]){
                    fog[Math.round(x / 20.0f)][Math.round(y / 20.0f)] = true;
                }
            }
        }
        for(BaseUnit currentUnit : units){
            int radius = currentUnit.getVisionMax();
            for(float degree = 0; degree < 2.0f * Math.PI; degree += Math.PI / 32.0f){
                for(int dist = 0; dist < radius; dist++){
                    float x = (float)(Math.cos(degree) * dist * 20.0f);
                    float y = (float)(Math.sin(degree) * dist * 20.0f);
                    int refX = Math.round((currentUnit.getxPosition() + x) / 20);
                    int refY = Math.round((currentUnit.getyPosition() + y) / 20);
                    if(refX >= 0 && refY >= 0 && refX < 768 && refY < 256){
                        TileTypes temp = referenceTiles[refX][refY];
                        if(temp == TileTypes.Mountains || temp == TileTypes.Trees){
                            break;
                        }else{
                            fog[refX][refY] = false;
                        }
                    }
                }
            }
        }

    }

    public void changeCell(TileTypes newType){
        referenceTiles[QuadTree.getSelectedNode().getX() / 20][QuadTree.getSelectedNode().getY() / 20] = newType;
        QuadTree.changeCell(mapTreeRoots[0][0], newType);
        QuadTree.changeCell(mapTreeRoots[1][0], newType);
        QuadTree.changeCell(mapTreeRoots[2][0], newType);

        QuadTree.compressCells(mapTreeRoots[0][0]);
        QuadTree.compressCells(mapTreeRoots[1][0]);
        QuadTree.compressCells(mapTreeRoots[2][0]);

        // The following three repeats shouldn't be necessary, but it's a test. This can definitely be optimized
        QuadTree.compressCells(mapTreeRoots[0][0]);
        QuadTree.compressCells(mapTreeRoots[1][0]);
        QuadTree.compressCells(mapTreeRoots[2][0]);

        QuadTree.compressCells(mapTreeRoots[0][0]);
        QuadTree.compressCells(mapTreeRoots[1][0]);
        QuadTree.compressCells(mapTreeRoots[2][0]);

        QuadTree.compressCells(mapTreeRoots[0][0]);
        QuadTree.compressCells(mapTreeRoots[1][0]);
        QuadTree.compressCells(mapTreeRoots[2][0]);
    }

    public LinkedList<BaseUnit> getUnits(){
        return units;
    }

    public QuadTreeNode[][] getMapTreeRoots(){
        return mapTreeRoots;
    }

    public double getTimeOfDay(){
        return timeOfDay;
    }

    public void update(double elapsedTime){
        QuadTree.update(elapsedTime);
    }

    public void updateUnitFrames(double elapsedTime){
        if(unitMoving){
            elapsedFrameTime += elapsedTime;
            if(elapsedFrameTime > 0.8){
                elapsedFrameTime = 0.0;
                if(movingUnit.getMovementPath().size() > 0){
                    AStarNode node = movingUnit.getMovementPath().removeLast();
                    if((node.y * 20) < movingUnit.getyPosition()){
                        movingUnit.setFacingDirection(Facing.North);
                    }else if((node.y * 20) > movingUnit.getyPosition()){
                        movingUnit.setFacingDirection(Facing.South);
                    }else if((node.x * 20) < movingUnit.getxPosition()){
                        movingUnit.setFacingDirection(Facing.West);
                    }else if((node.x * 20) > movingUnit.getxPosition()){
                        movingUnit.setFacingDirection(Facing.East);
                    }
                    movingUnit.setxPosition(node.x * 20);
                    movingUnit.setyPosition(node.y * 20);
                }else{
                    unitMoving = false;
                }
            }
        }
    }

    public void setTimeOfDay(double timeOfDay1){
        timeOfDay = timeOfDay1;
    }

    public void informUnitOfNewPath(BaseUnit unit, LinkedList<AStarNode> path){
        movingUnit = units.get(units.indexOf(unit));
        movingUnit.setMovementPath(path);
        unitMoving = true;
    }

    /**
     * Rework for different kinds of units! And to avoid collisions! This of course is not at all optimized for the quad tree,
     * which was half the point of creating it in the first place. Redoing it should be considered strongly
     * @param startUnit
     * @param Goal
     * @return
     */
    public LinkedList<AStarNode> calculatePath(BaseUnit startUnit, AStarNode Goal){
        AStarNode startingPoint = new AStarNode(startUnit.getxPosition() / 20, startUnit.getyPosition() / 20);
        startingPoint.G = 0;
        startingPoint.H = calculateEuclideanDistance(startingPoint, Goal);
        startingPoint.F = startingPoint.G + startingPoint.H;
        LinkedList<AStarNode> openList = new LinkedList<AStarNode>();
        LinkedList<AStarNode> closedList = new LinkedList<AStarNode>();
        AStarNode topNeighbor, bottomNeighbor, leftNeighbor, rightNeighbor;

        openList.add(startingPoint);
        while(true){
            AStarNode currentPoint = getListBest(openList, startingPoint, Goal);
            closedList.add(currentPoint);
            if(closedList.contains(Goal)){
                break;
            }else if(!closedList.contains(Goal) && openList.size() == 0){
                return null;
            }else{
                openList.remove(currentPoint);
                topNeighbor = new AStarNode(currentPoint.x, currentPoint.y - 1);
                if(!openList.contains(topNeighbor) && !closedList.contains(topNeighbor)){
                    if(topNeighbor.x >= 0 && topNeighbor.x < this.mapWidth && topNeighbor.y >= 0 && topNeighbor.y < this.mapHeight){
                        if(!startUnit.restrictedTiles.contains(referenceTiles[topNeighbor.x][topNeighbor.y])){
                            topNeighbor.G = currentPoint.G + startUnit.movementCosts.get(referenceTiles[topNeighbor.x][topNeighbor.y]);
                            topNeighbor.H = calculateEuclideanDistance(topNeighbor, Goal);
                            topNeighbor.F = topNeighbor.G + topNeighbor.H;
                            topNeighbor.parent = currentPoint;
                            openList.add(topNeighbor);
                        }
                    }
                }else if(openList.contains(topNeighbor)){
                    float tempG = startUnit.movementCosts.get(referenceTiles[topNeighbor.x][topNeighbor.y]);
                    if(tempG < topNeighbor.G){
                        topNeighbor.G = tempG;
                        topNeighbor.parent = currentPoint;
                    }
                }

                rightNeighbor = new AStarNode(currentPoint.x + 1, currentPoint.y);
                if(!openList.contains(rightNeighbor) && !closedList.contains(rightNeighbor)){
                    if(rightNeighbor.x >= 0 && rightNeighbor.x < this.mapWidth && rightNeighbor.y >= 0 && rightNeighbor.y < this.mapHeight){
                        if(!startUnit.restrictedTiles.contains(referenceTiles[rightNeighbor.x][rightNeighbor.y])){
                            rightNeighbor.G = currentPoint.G + startUnit.movementCosts.get(referenceTiles[rightNeighbor.x][rightNeighbor.y]);
                            rightNeighbor.H = calculateEuclideanDistance(rightNeighbor, Goal);
                            rightNeighbor.F = rightNeighbor.G + rightNeighbor.H;
                            rightNeighbor.parent = currentPoint;
                            openList.add(rightNeighbor);
                        }
                    }
                }else if(openList.contains(rightNeighbor)){
                    float tempG = currentPoint.G + startUnit.movementCosts.get(referenceTiles[rightNeighbor.x][rightNeighbor.y]);
                    if(tempG < rightNeighbor.G){
                        rightNeighbor.G = tempG;
                        rightNeighbor.parent = currentPoint;
                    }
                }

                leftNeighbor = new AStarNode(currentPoint.x - 1, currentPoint.y);
                if(!openList.contains(leftNeighbor) && !closedList.contains(leftNeighbor)){
                    if(leftNeighbor.x >= 0 && leftNeighbor.x < this.mapWidth && leftNeighbor.y >= 0 && leftNeighbor.y < this.mapHeight){
                        if(!startUnit.restrictedTiles.contains(referenceTiles[leftNeighbor.x][leftNeighbor.y])){
                            leftNeighbor.G = currentPoint.G + startUnit.movementCosts.get((referenceTiles[leftNeighbor.x][leftNeighbor.y]));
                            leftNeighbor.H = calculateEuclideanDistance(leftNeighbor, Goal);
                            leftNeighbor.F = leftNeighbor.G + leftNeighbor.H;
                            leftNeighbor.parent = currentPoint;
                            openList.add(leftNeighbor);
                        }
                    }
                }else if(openList.contains(leftNeighbor)){
                    float tempG = currentPoint.G + startUnit.movementCosts.get((referenceTiles[leftNeighbor.x][leftNeighbor.y]));
                    if(tempG < leftNeighbor.G){
                        leftNeighbor.G = tempG;
                        leftNeighbor.parent = currentPoint;
                    }
                }

                bottomNeighbor = new AStarNode(currentPoint.x, currentPoint.y + 1);
                if(!openList.contains(bottomNeighbor) && !closedList.contains(bottomNeighbor)){
                    if(bottomNeighbor.x >= 0 && bottomNeighbor.x < this.mapWidth && bottomNeighbor.y >= 0 && bottomNeighbor.y < this.mapHeight){
                        if(!startUnit.restrictedTiles.contains(referenceTiles[bottomNeighbor.x][bottomNeighbor.y])){
                            bottomNeighbor.G = currentPoint.G + startUnit.movementCosts.get(referenceTiles[bottomNeighbor.x][bottomNeighbor.y]);
                            bottomNeighbor.H = calculateEuclideanDistance(bottomNeighbor, Goal);
                            bottomNeighbor.F = bottomNeighbor.G + bottomNeighbor.H;
                            bottomNeighbor.parent = currentPoint;
                            openList.add(bottomNeighbor);
                        }
                    }
                }else if(openList.contains(bottomNeighbor)){
                    float tempG = currentPoint.G + startUnit.movementCosts.get(referenceTiles[bottomNeighbor.x][bottomNeighbor.y]);
                    if(tempG < bottomNeighbor.G){
                        bottomNeighbor.G = tempG;
                        bottomNeighbor.parent = currentPoint;
                    }
                }
            }
        }
        if(closedList.contains(Goal)){
            LinkedList<AStarNode> finalPath = new LinkedList<AStarNode>();
            AStarNode current = closedList.get(closedList.indexOf(Goal));
            while(!current.equals(startingPoint)){
                finalPath.addLast(current);
                if(current.parent == null){
                    break;
                }else{
                    current = current.parent;
                }
            }
            return finalPath;
        }else{
            return null;
        }
    }

    private AStarNode getListBest(LinkedList<AStarNode> list, AStarNode source, AStarNode dest){
        AStarNode bestNode = list.peek();
        for(AStarNode current : list){
            if(current.F < bestNode.F){
                bestNode = current;
            }
        }
        return bestNode;
    }

    /**
     * My heuristic function, which should produce more natural movement
     * @param current current node
     * @param dest destination node
     * @return Calculated Heuristic
     */
    private float calculateEuclideanDistance(AStarNode current, AStarNode dest){
        float xSq = (dest.x - current.x) * (dest.x - current.x);
        float ySq = (dest.y - current.y) * (dest.y - current.y);

        return (float)Math.sqrt(xSq + ySq);
    }
}
