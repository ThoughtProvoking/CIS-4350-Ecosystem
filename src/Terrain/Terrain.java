package Terrain;

import Setup.InitJME;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.MultiTerrainLodControl;
import com.jme3.terrain.geomipmap.NeighbourFinder;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.MidpointDisplacementHeightMap;
import java.util.ArrayList;

public class Terrain implements NeighbourFinder {

    private static final int SIZE = 1025;
    private static final int PATCH_SIZE = 33;
    private static final float RANGE = 5.0f;
    private static final float PERSISTENCE = 0.5f;  // roughness
    private static final float NORMALIZER = (float) SIZE * 2.0f;
    public static final float WATER_LEVEL = NORMALIZER / 50.0f;
    private static final int N = 2; // total number of TerrainQuads is N^2
    private SimpleApplication sa;
    private ArrayList<float[]> heightmap = new ArrayList<float[]>();
    private ArrayList<TerrainQuad> terrain = new ArrayList<TerrainQuad>();
    private Node terrainNode = new Node();
    // Supposedly helps with LOD of multiple TerrainQuads
    private MultiTerrainLodControl mlod;

    /*
     * Beginning of the terrain generation
     * Looked over TerrainTestTile.java and searched Google for help
     */
    public Terrain(SimpleApplication app) {
        sa = app;
        sa.getRootNode().attachChild(terrainNode);
        mlod = new MultiTerrainLodControl(sa.getCamera());
        initHeightMap();
    }

    /*
     * Generates the heightmaps and places the float[] data into ArrayList
     */
    private void initHeightMap() {
        try {
            for (int i = 0; i < Math.pow(N, 2); i++) {
                AbstractHeightMap h = new MidpointDisplacementHeightMap(SIZE, RANGE, PERSISTENCE);
                heightmap.add(h.getHeightMap());
            }
        } catch (Exception e) {
        }
        fixEdges();
    }

    /*
     * Fixes the heights of the edges
     * Sets the height to be the average of all of the heights
     */
    private void fixEdges() {
        float[] right = null, bottom = null, diagonal = null;
        int index, i = 0, j = 0;   // i,j: Help translate the terrains properly
        boolean lastCol = true, lastRow = true;

        for (float[] h : heightmap) {
            index = heightmap.indexOf(h);
            if (index % N != N - 1) {
                // Not in last column
                right = getRightMap(index);
                lastCol = false;
            }
            if (index < N * (N - 1)) {
                // Not in last row
                bottom = getDownMap(index);
                lastRow = false;
            }
            if (!lastCol && !lastRow) {
                // Have diagonal neighbor iff have right and bottom neighbors
                diagonal = getDiagMap(index);
            }

            initTerrain(index, i, j);
            j = (j + 1) % N;
            if (j == 0) {
                // If j restarts counting from zero, new row -> increment i
                // Should ignore the first time j = 0;
                i++;
            }
        }
    }

    /*
     * Gets the heightmap data of bottom, right, and diagonal neighbors to fix the edges
     */
    private float[] getDiagMap(int i) {
        return heightmap.get(i + N + 1);
    }

    private float[] getRightMap(int i) {
        return heightmap.get(i + 1);
    }

    private float[] getDownMap(int i) {
        return heightmap.get(i + N);
    }

    /*
     * Creates the TerrainQuad
     */
    private void initTerrain(int i, int j, int k) {
        TerrainQuad t = new TerrainQuad("Terrain" + i, PATCH_SIZE, SIZE, heightmap.get(i));
        t.setMaterial(InitJME.mat);
        t.setLocalTranslation(j * SIZE, 0, k * SIZE);
        t.setNeighbourFinder(this);
        mlod.addTerrain(t);
        terrainNode.attachChild(t);
        terrain.add(t);

        // Terrain physics
        CollisionShape cShape = CollisionShapeFactory.createMeshShape((Node) t);
        RigidBodyControl terrainPhys = new RigidBodyControl(cShape, 0.0f);
        t.addControl(terrainPhys);
        InitJME.bullet.getPhysicsSpace().add(terrainPhys);
    }

    /*
     * Methods from NeighbourFinder interface
     * 
     * Gets the respective neighboring TerrainQuad (that's not in the same quad 
     * tree) and seams the edges, only if heights are the same
     * https://javadoc.jmonkeyengine.org/com/jme3/terrain/geomipmap/NeighbourFinder.html
     * 
     * @return null if TerrainQuad does not have specified neighbor
     */
    public TerrainQuad getRightQuad(TerrainQuad center) {
        if (terrain.indexOf(center) % N == N - 1) {
            return null;    // TerrainQuad is on rightmost side
        }
        return terrain.get(terrain.indexOf(center) + 1);
    }

    public TerrainQuad getLeftQuad(TerrainQuad center) {
        if (terrain.indexOf(center) % N == 0) {
            return null;    // TerrainQuad is on leftmost side
        }
        return terrain.get(terrain.indexOf(center) - 1);
    }

    public TerrainQuad getTopQuad(TerrainQuad center) {
        if (terrain.indexOf(center) < N) {
            return null;    // TerrainQuad is in top row
        }
        return terrain.get(terrain.indexOf(center) - N);
    }

    public TerrainQuad getDownQuad(TerrainQuad center) {
        if (terrain.indexOf(center) >= Math.pow(N, 2) - N) {
            return null;    // TerrainQuad is in bottom row
        }
        return terrain.get(terrain.indexOf(center) + N);
    }
}