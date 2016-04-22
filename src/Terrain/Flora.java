package Terrain;

import com.jme3.app.SimpleApplication;
import java.util.ArrayList;
import java.util.Random;

public class Flora {

    private Random rand = new Random();
    private SimpleApplication sa;

    public Flora(SimpleApplication app, ArrayList<float[]> heightmap) {
        sa = app;
        int size = (int) Math.sqrt(heightmap.get(0).length);
        for (float[] h : heightmap) {
            // No grass for the edge points because it might look weird
            for (int i = size + 1; i < h.length; i++) {
                if (i % size != 0 && i % size != size - 1 && i < h.length - size){
                    
                }
            }
        }
    }
}
