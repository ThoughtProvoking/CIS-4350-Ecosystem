package Terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.ArrayList;
import java.util.Random;

public class Flora {

    private Random rand = new Random();
    private SimpleApplication sa;

    public Flora(SimpleApplication app, ArrayList<TerrainQuad> terrain) {
        sa = app;
    }
}
