/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: 2/20/12
 * Time: 11:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AStarNode {
    public int x;
    public int y;
    public float G;
    public float H;
    public float F;
    public AStarNode parent;
    
    public AStarNode(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object other){
        if(other.getClass() != AStarNode.class) throw new IllegalArgumentException();
        AStarNode a = (AStarNode)other;
        if(a.x == this.x && a.y == this.y){
            return true;
        }else{
            return false;
        }
    }
}
