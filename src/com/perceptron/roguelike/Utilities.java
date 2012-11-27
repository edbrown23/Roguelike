package com.perceptron.roguelike;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 6/19/12
 * Time: 12:11 AM
 */
public class Utilities {
    public static final long OPTIMAL_TIME = 1000000000 / 60; // 60 fps;

    public static float[][] combineEdgeArrays(float[] left, float[] right, float[] top, float[] bottom){
        float[][] output = new float[left.length][left.length];
        // For now I'm just going to average it
        float factor = 0.6f;
        for(int i = 0; i < left.length; i++){
            for(int j = 0; j < left.length; j++){
                output[i][j] = (left[j] * ((((float)left.length - (float)i) / (float)left.length) * factor)); // Eventually here we might be decreasing the affect of an edge farther from it's origin
                while(output[i][j] > 1.0f){
                    output[i][j] /= 1.1f;
                }
            }
        }

        for(int j = right.length - 1; j >= 0; j--){
            for(int i = 0; i < right.length; i++){
                output[j][i] = ((output[j][i] + (right[i] * ((((float)j) / (float)right.length) * factor))));
                while(output[j][i] > 1.0f){
                    output[j][i] /= 1.1f;
                }
            }
        }

        for(int i = 0; i < top.length; i++){
            for(int j = 0; j < top.length; j++){
                output[j][i] = ((output[j][i] + (top[j] * (((float)(top.length - i) / (float)top.length) * factor))));
                while(output[j][i] > 1.0f){
                    output[j][i] /= 1.1f;
                }
            }
        }

        for(int i = bottom.length - 1; i >= 0; i--){
            for(int j = 0; j < bottom.length; j++){
                output[j][i] = ((output[j][i] + (bottom[j] * ((((float)i / (float)bottom.length)) * factor))));
                while(output[j][i] > 1.0f){
                    output[j][i] /= 1.1f;
                }
            }
        }

        float[][] noise = PerlinNoise.GeneratePerlinNoise(top.length, top.length, 7);
        for(int i = 0; i < output.length; i++){
            for(int j = 0; j < output.length; j++){
                output[i][j] = (output[i][j] + noise[i][j]) / 2.0f;
            }
        }


        return output;
    }

    /**
     * Calculate the distance between two chunks
     * @param c1 chunk 1
     * @param c2 chunk 2
     * @return The euclidean distance between chunks
     */
    public static float chunkDistance(Chunk c1, Chunk c2){
        float xS = (c1.getX() - c2.getX()) * (c1.getX() - c2.getX());
        float yS = (c1.getY() - c2.getY()) * (c1.getY() - c2.getY());
        return (float)Math.sqrt(xS + yS);
    }

    /**
     * Converts a world coordinate, ie an absolute position in the world
     * to the proper chunk coordinate
     * @param p The absolute world coordinate
     * @param chunkWidth The width of a chunk
     * @param chunkHeight The height of a chunk
     * @return The chunk coordinate
     */
    public static Point convertWorldPoint(Point p, int chunkWidth, int chunkHeight){
        int x = (int)Math.floor((double)(p.x / chunkWidth));
        int y = (int)Math.floor((double)(p.y / chunkHeight));
        if(p.x < 0){
            x--;
        }
        if(p.y < 0){
            y--;
        }
        return new Point(x, y);
    }

    /**
     * Calculates euclidean distance between cells
     * @param c1 cell 1
     * @param c2 cell 2
     * @return The distance between cells
     */
    public static float cellDistance(Cell c1, Cell c2){
        float xS = (c1.getX() - c2.getX()) * (c1.getX() - c2.getX());
        float yS = (c1.getY() - c2.getY()) * (c1.getY() - c2.getY());
        return (float)Math.sqrt(xS + yS);
    }
}
