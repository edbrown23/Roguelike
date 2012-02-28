/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/1/12
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuadTreeNode {
    private QuadTreeNode UL;
    private QuadTreeNode UR;
    private QuadTreeNode BL;
    private QuadTreeNode BR;
    private TileTypes tile;
    private int width, height;
    private int x, y;

    public QuadTreeNode(QuadTreeNode upL, QuadTreeNode upR, QuadTreeNode botL, QuadTreeNode botR, TileTypes inputTile, int w, int h, int x, int y){
        UL = upL;
        UR = upR;
        BL = botL;
        BR = botR;
        tile = inputTile;
        width = w;
        height = h;
        this.x = x;
        this.y = y;
    }

    public QuadTreeNode(QuadTreeNode upL, QuadTreeNode upR, QuadTreeNode botL, QuadTreeNode botR, int w, int h, int x, int y){
        UL = upL;
        UR = upR;
        BL = botL;
        BR = botR;
        width = w;
        height = h;
        this.x = x;
        this.y = y;
    }

    public QuadTreeNode(){
        tile = TileTypes.NULL;
    }

    public boolean checkIdenticalBranches(){
        TileTypes ulType = UL.getTile();
        TileTypes urType = UR.getTile();
        TileTypes blType = BL.getTile();
        TileTypes brType = BR.getTile();
        if(ulType == TileTypes.NULL){
            return false;
        }else if(ulType == urType){
            if(urType == blType){
                if(blType == brType){
                    return true;
                }
            }
        }
        return false;
    }

    public TileTypes getTile(){
        return tile;
    }

    public TileTypes getBranchType(){
        return UL.getTile();
    }

    public void setTile(TileTypes type){
        tile = type;
    }

    public QuadTreeNode[] getBranches(){
        QuadTreeNode[] branchArray = new QuadTreeNode[4];
        if(UL == null){
            throw new NullPointerException("Leaf has no branches!");
        }else{
            branchArray[0] = UL;
            branchArray[1] = UR;
            branchArray[2] = BL;
            branchArray[3] = BR;

            return branchArray;
        }
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
}
