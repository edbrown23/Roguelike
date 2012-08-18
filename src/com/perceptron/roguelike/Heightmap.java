import java.io.*;

public class Heightmap implements Serializable{
    private Cell[][] values;

    /**
     * Copy constructor
     * @param map The heightmap to copy
     */
    public Heightmap(Heightmap map){
        for(int x = 0; x < map.values.length; x++){
            System.arraycopy(map.values[x], 0, this.values[x], 0, this.values[x].length);
        }
    }

    public Heightmap(float[][] input){
        values = new Cell[input.length][];
        for(int i = 0; i < input.length; i++){
            values[i] = new Cell[input[i].length];
        }
        for(int x = 0; x < input.length; x++){
            for(int y = 0; y < input[x].length; y++){
                int height = (int)(input[x][y] * 255);
                CellType temp;
                if(height > 170 && height < 210){
                    temp = CellType.Mountain;
                }else if(height >= 210){
                    temp = CellType.Snow;
                }else if(height > 100 && height <= 170){
                    temp = CellType.Grass;
                }else{
                    temp = CellType.Dirt;
                }
                values[x][y] = new Cell(height, temp, 0.0f, x, y);
            }
        }
    }

    public Cell[][] getCells(){
        return values;
    }
}
