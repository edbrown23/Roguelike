import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Eric
 * Date: 4/11/12
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChunkManager implements Runnable {
    private int x, y;
    private int wX, wY;
    private boolean gameRunning = true;
    private MapChunk nextXChunk; // maybe make this into a list, so we can load multiples?
    private MapChunk nextYChunk;
    private LinkedList<MapChunk> oldChunks = new LinkedList<MapChunk>();
    private int chunkWidth, chunkHeight;
    private int loadingFactor; // simply trying to generalize, one could adjust the loading factor based on performance. It determines when we load chunks

    public ChunkManager(int x, int y, int wX, int wY, int cW, int cH, int lf){
        this.x = x;
        this.y = y;
        this.wX = wX;
        this.wY = wY;
        chunkWidth = cW;
        chunkHeight = cH;
        loadingFactor = lf;
    }

    public void updatePlayerPos(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void updateWorldPos(int wX, int wY){
        this.wX = wX;
        this.wY = wY;
    }

    public void toggleGame(){
        gameRunning = !gameRunning;
    }
    /*
    The chunk manager simply watches the world and loads/saves the appropriate chunks depending on player position.
    The actual observation occurs in a separate thread
     */
    @Override
    public void run() {

        float minXDistFromChunkEdge = chunkWidth, minYDistFromChunkEdge = chunkHeight, temp1;
        // Loop forever while the game is running, checking the players position and world position and loading chunks appropriately
        while(gameRunning){
            // Chunk Loading process
            temp1 = chunkWidth - x;
            if(temp1 < minXDistFromChunkEdge){
                minXDistFromChunkEdge = temp1;
            }
            temp1 = x;
            if(temp1 < minXDistFromChunkEdge){
                minXDistFromChunkEdge = temp1;
            }
            if(minXDistFromChunkEdge < (chunkWidth / loadingFactor)){
                if(minXDistFromChunkEdge < (chunkWidth / 2)){
                    nextXChunk = new ChunkFiler(wX - 1, wY).loadChunk();
                }else{
                    nextXChunk = new ChunkFiler(wX + 1, wY).loadChunk();
                }
            }
            temp1 = chunkHeight - y;
            if(temp1 < minYDistFromChunkEdge){
                minYDistFromChunkEdge = temp1;
            }
            temp1 = y;
            if(temp1 < minXDistFromChunkEdge){
                minYDistFromChunkEdge = temp1;
            }
            if(minYDistFromChunkEdge < (chunkHeight / loadingFactor)){
                if(minYDistFromChunkEdge < (chunkHeight / 2)){
                    nextYChunk = new ChunkFiler(wX, wY - 1).loadChunk();
                }else{
                    nextYChunk = new ChunkFiler(wX,  wY + 1).loadChunk();
                }
            }
            // Chunk saving process
            for(MapChunk currentC : oldChunks){
                if(Math.abs(wX - currentC.getWorldX()) >= 2){
                    new ChunkFiler(currentC.getWorldX(), currentC.getWorldY()).saveChunk(currentC);
                }
            }
        }
    }

    public class ChunkFiler implements Runnable{
        private FileOutputStream fos;
        private FileInputStream fis;
        private ObjectOutputStream outStream;
        private ObjectInputStream inStream;
        private MapChunk chunk, chunkToSave;
        private int chunkToLoadX, chunkToLoadY;
        private boolean doneLoading = false, doneSaving = false;
        private boolean loadingChunk, savingChunk;

        public ChunkFiler(int cX, int cY){
            chunkToLoadX = cX;
            chunkToLoadY = cY;
        }

        /**
         * Chunk manager tells ChunkFiler to load a new chunk, so it creates a thread and runs the chunk loader, then waits for it to finish.
         * Waiting in this fashion seems kinda dumb, as it makes the thread useless, but it's open to reorganizing soon
         * @return Function returns the loaded chunk
         */
        public MapChunk loadChunk(){
            loadingChunk = true;
            savingChunk = false;
            Executor threadManager = Executors.newFixedThreadPool(1);
            threadManager.execute(this);
            while(!doneLoading){}
            doneLoading = false;
            return chunk;
        }

        /**
         * Saves the desired old chunk, silly for the same reasons as the load method above
         * @param saveChunk The chunk to be saved
         */
        public void saveChunk(MapChunk saveChunk){
            loadingChunk = false;
            savingChunk = true;
            Executor threadManager = Executors.newFixedThreadPool(1);
            threadManager.execute(this);
            chunkToSave = saveChunk;
            while(!doneSaving){}
            doneSaving = false;
        }

        /*
        As it's planned now, the chunk loader has a runnable which will actually do the saving and loading of the chunks
         */
        @Override
        public void run() {
            if(loadingChunk && !savingChunk){
                try {
                    fis = new FileInputStream("World/Chunks/chunk" + chunkToLoadX + chunkToLoadY);
                    inStream = new ObjectInputStream(fis);
                    chunk = (MapChunk)inStream.readObject();
                    doneLoading = true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    if(!doneLoading){
                        // probably do something here, I forgot
                        System.out.println("Failed to load chunk at " + chunkToLoadX + " " + chunkToLoadY + ". Sorry!");
                    }else{
                        System.out.println("Loaded chunk at " + chunkToLoadX + " " + chunkToLoadY);
                    }
                }
            }else if(!loadingChunk && savingChunk){
                try {
                    fos = new FileOutputStream("World/Chunks/chunk" + chunkToLoadX + chunkToLoadY);
                    outStream = new ObjectOutputStream(fos);
                    outStream.writeObject(chunkToSave);
                    doneSaving = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                    if(!doneSaving){
                        System.out.println("Failed to save chunk at " + chunkToLoadX + " " + chunkToLoadY + ". Sorry!");
                    }else{
                        System.out.println("Saved chunk at " + chunkToLoadX + " " + chunkToLoadY);
                    }
                }
            }
        }
    }
}
