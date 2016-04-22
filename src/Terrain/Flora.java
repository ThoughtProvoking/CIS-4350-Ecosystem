package Terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.ArrayList;
import java.util.Random;

public class Flora {

    private SimpleApplication sa;
    private Random rand = new Random();
    private Node floraNode = new Node();
    private Quad q = new Quad(1, 1);

    public Flora(SimpleApplication app, ArrayList<TerrainQuad> terrain) {
        sa = app;
        sa.getRootNode().attachChild(floraNode);
        createFlora(terrain);
    }

    private void createFlora(ArrayList<TerrainQuad> terrain) {
        float x = 0, y, z = 0;
        float size = terrain.get(0).getTotalSize();

        for (TerrainQuad t : terrain) {
            float[] h = t.getHeightMap();

            for (int i = 0; i < h.length; i++) {
                y = h[i];
//            
                if (y > Terrain.WATER_LEVEL && y < Terrain.MOUNTAIN_BASE) {
                    if (rand.nextDouble() < .333) {
                        Spatial tree = sa.getAssetManager().loadModel("Models/Tree/Tree.mesh.xml");
                        tree.setLocalTranslation(x, y, z);
                        tree.setLocalScale(20);
                        floraNode.attachChild(tree);
                    } else {
                    }
                }
                z = (z + 1) % size;
                if (z == 0) {
                    x++;
                }
            }
            break;
        }
    }
}