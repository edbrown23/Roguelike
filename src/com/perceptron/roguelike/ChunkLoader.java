import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 6/24/12
 * Time: 8:44 PM
 */
public class ChunkLoader implements Observer {
    /** The X chunk position of the player */
    private int playerWorldX;
    /** The Y chunk position of the player */
    private int playerWorldY;
    /** The players "velocity", which indicates their direction of travel relative to previous positions */
    private float playerVelocityX;
    /** The players "velocity", which indicates their direction of travel relative to previous positions */
    private float playerVelocityY;
    /** The players position within a chunk */
    private float playerX;
    /** The players position within a chunk */
    private float playerY;
    /** Whether the Chunk Loader should be running */
    private boolean running;
    /** The width of a chunk, to determine chunk loading */
    private int chunkWidth;
    /** The height of a chunk, to determine chunk loading */
    private int chunkHeight;
    /** The ratio of the world view, to determine if a chunk should be displayed */
    private float viewPortRatio;
    /** A map of world coordinates to chunks */
    private Hashtable<Point, Chunk> chunkTable;

    /**
     * Constructor sets up the Chunk Loader and starts the thread
     * @param chunkWidth The width of a chunk
     * @param chunkHeight The height of a chunk
     * @param viewPortRatio The ratio of a chunk's size to determine when it should be displayed
     */
    public ChunkLoader(int chunkWidth, int chunkHeight, float viewPortRatio){
        chunkTable = new Hashtable<Point, Chunk>();
        playerVelocityX = 0f;
        playerVelocityY = 0f;
        this.chunkHeight = chunkHeight;
        this.chunkWidth = chunkWidth;
        this.viewPortRatio = viewPortRatio;
    }

    /**
     * Creates thread and begins the chunk loaders managing faculties
     */
    public void startManager(){
        Executor threadExecutor = Executors.newSingleThreadExecutor();
        running = true;
        threadExecutor.execute(new ChunkLoaderThread());
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg.getClass() != ChunkLoaderInfo.class){
            throw new IllegalArgumentException("Must pass the ChunkLoaderInfo Object");
        }else{
            ChunkLoaderInfo update = (ChunkLoaderInfo)arg;
            // Assuming we're update at a relatively constant time, this is enough to determine the velocity
            playerVelocityX = update.newPlayerX - playerX;
            playerVelocityY = update.newPlayerY - playerY;
            playerX = update.newPlayerX;
            playerY = update.newPlayerY;

            playerWorldX = update.newPlayerWorldX;
            playerWorldY = update.newPlayerWorldY;
        }
    }

    /**
     * Returns a collection of the chunks in view of the player,
     * based on the view port ratio and the players position
     * @return A table of the chunks in view of the player
     */
    public HashMap<Point, Chunk> getChunksInView(){
        HashMap<Point, Chunk> output = new HashMap<Point, Chunk>();

        // The chunk the player is currently standing on must be included
        Point currentPlayer = new Point(playerWorldX, playerWorldY);
        output.put(currentPlayer, chunkTable.get(currentPlayer));

        // Flags to indicate which extra chunks are being included
        boolean left = false;
        boolean right = false;
        boolean up = false;
        boolean down = false;
        // If the right chunk must be included
        if((playerX - (chunkWidth * playerWorldX)) / chunkWidth > viewPortRatio){
            right = true;
            Point p = new Point(playerWorldX + 1, playerWorldY);
            output.put(p, chunkTable.get(p));
        }else if(((playerX - (chunkWidth * playerWorldX)) / chunkWidth) < (1 - viewPortRatio)){ // If the left chunk must be included
            left = true;
            Point p = new Point(playerWorldX - 1, playerWorldY);
            output.put(p, chunkTable.get(p));
        }
        // If the lower chunk must be included
        if(((playerY - (chunkHeight * playerWorldY)) / chunkHeight) > viewPortRatio){
            down = true;
            Point p = new Point(playerWorldX, playerWorldY + 1);
            output.put(p, chunkTable.get(p));
        }else if(((playerY - (chunkHeight * playerWorldY)) / chunkHeight) < (1 - viewPortRatio)){ // If the upper chunk must be included
            up = true;
            Point p = new Point(playerWorldX, playerWorldY - 1);
            output.put(p, chunkTable.get(p));
        }
        // Use the flags to include the diagonal chunks if necessary
        if(up && left){
            Point p = new Point(playerWorldX - 1, playerWorldY - 1);
            output.put(p, chunkTable.get(p));
        }
        if(up && right){
            Point p = new Point(playerWorldX + 1, playerWorldY - 1);
            output.put(p, chunkTable.get(p));
        }
        if(down && left){
            Point p = new Point(playerWorldX - 1, playerWorldY + 1);
            output.put(p, chunkTable.get(p));
        }
        if(down && right){
            Point p = new Point(playerWorldX + 1, playerWorldY + 1);
            output.put(p, chunkTable.get(p));
        }

        return output;
    }

    public float getViewPortRatio(){
        return viewPortRatio;
    }

    public void setViewPortRatio(float view){
        viewPortRatio = view;
    }

    public void shutdown(){
        running = false;
    }

    private class ChunkLoaderThread implements Runnable{
        @Override
        public void run() {
            while(running){
                // If the players chunk hasn't been loaded, load it immediately
                if(chunkTable.get(new Point(playerWorldX, playerWorldY)) == null){
                    attemptChunkLoad(new Point(playerWorldX,  playerWorldY));
                }
                // Determine if a chunk should be loaded
                float predictedPlayerX = playerX + (playerVelocityX * 300f); // I'm pulling that constant out of my ass
                float predictedPlayerY = playerY + (playerVelocityY * 300f);
                int predictedChunkX = (int)Math.floor((double)(predictedPlayerX / chunkWidth));
                int predictedChunkY = (int)Math.floor((double)(predictedPlayerY / chunkHeight));

                //System.out.println(playerWorldX + " " + playerWorldY + " " + predictedChunkX + " " + predictedChunkY);
                // Diagonal movement
                Point p1 = new Point(predictedChunkX, predictedChunkY);
                if(!chunkTable.containsKey(p1)){
                    attemptChunkLoad(p1);
                }
                // Vertical movement
                Point p2 = new Point(playerWorldX, predictedChunkY);
                if(!chunkTable.containsKey(p2)){
                    attemptChunkLoad(p2);
                }
                // Horizontal movement
                Point p3 = new Point(predictedChunkX, playerWorldY);
                if(!chunkTable.containsKey(p3)){
                    attemptChunkLoad(p3);
                }
                // diagonals. Temporary solution
                Point p4 = new Point(playerWorldX - 1, playerWorldY - 1);
                if(!chunkTable.contains(p4)){
                    attemptChunkLoad(p4);
                }
                Point p5 = new Point(playerWorldX + 1, playerWorldY - 1);
                if(!chunkTable.contains(p5)){
                    attemptChunkLoad(p5);
                }
                Point p6 = new Point(playerWorldX - 1, playerWorldY + 1);
                if(!chunkTable.contains(p6)){
                    attemptChunkLoad(p6);
                }
                Point p7 = new Point(playerWorldX + 1, playerWorldY + 1);
                if(!chunkTable.contains(p7)){
                    attemptChunkLoad(p7);
                }
                // Hypothetically, every 10 seconds or so we should clear out old chunks to conserve memory
                if(chunkTable.size() > 20){
                    System.out.println("SAVING SHIT!!");
                    saveOldChunks();
                }
            }

        }
    }

    private void attemptChunkLoad(Point p){
        if(chunkTable.get(p) == null){
            ObjectInputStream input;
            try {
                String fileName = "World/Chunk" + Integer.toString(p.x) + Integer.toString(p.y) + ".chunk";
                input = new ObjectInputStream(new FileInputStream(fileName));
                Chunk inputChunk = (Chunk)input.readObject();
                chunkTable.put(p, inputChunk);
                System.out.println("Loaded Chunk");
                input.close();
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                createChunk(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Use the yet to be determined chunk creation algorithm to create a new chunk
     * at the appropriate position
     * @param p The world coordinate for the new chunk
     */
    private void createChunk(Point p){
        float[][] noise = PerlinNoise.GeneratePerlinNoise(chunkWidth, chunkHeight, 7);
        chunkTable.put(p, new Chunk(new Heightmap(noise), p.x, p.y, this, chunkWidth, chunkHeight));
        System.out.println("Created Chunk");
    }

    /**
     * Iterate over the chunk table, and save and remove chunks which are far
     * away from the player
     */
    private synchronized void saveOldChunks(){
        ArrayList<Chunk> removedChunks = new ArrayList<Chunk>();
        Chunk playerChunk = chunkTable.get(new Point(playerWorldX, playerWorldY));
        for(Chunk current : chunkTable.values()){
            // If the chunk is too far from the player for it to matter, we save it and
            // remove it from the table, leaving it for garbage collection
            if(Utilities.chunkDistance(playerChunk, current) > 5){
                removedChunks.add(current);
                saveChunk(current);
            }
        }
        for(Chunk rChunk : removedChunks){
            chunkTable.remove(new Point(rChunk.getX(), rChunk.getY()));
        }
    }

    private void saveChunk(Chunk c){
        ObjectOutputStream output;
        try{
            String fileName = "World/Chunk" + Integer.toString(c.getX()) + Integer.toString(c.getY()) + ".chunk";
            FileOutputStream fileOutStream = new FileOutputStream(fileName);
            output = new ObjectOutputStream(fileOutStream);
            output.writeObject(c);
            System.out.println("Saved Chunk");
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            // Clear everything?
        }
    }

    public Chunk getChunk(Point p) throws NullPointerException{
        return chunkTable.get(p);
    }
}
