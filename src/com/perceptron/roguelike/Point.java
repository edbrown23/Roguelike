/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 6/24/12
 * Time: 9:20 PM
 */
public class Point {
    public int x;
    public int y;
    public float info;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Blasphemous hash
     * @return The hash code
     */
    public int hashCode(){
        String temp = Integer.toString(x) + Integer.toString(y);
        return temp.hashCode();
    }

    public boolean equals(Object o){
        if(o.getClass() != this.getClass()){
            throw new IllegalArgumentException();
        }else{
            Point p = (Point)o;
            return p.x == x && p.y == y;
        }
    }
}
