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
    private static final float NORMALIZER = (float) SIZE * 1.0f;
    public static final float WATER_LEVEL = NORMALIZER / 25.0f;
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
        for (TerrainQuad t : terrain) {
            sa.getRenderManager().preloadScene(t);
        }
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
     * DOES NOT WORK, for some reason the sides and values are wrong
     */
    private void fixEdges() {
        float[] right = null, bottom = null;
        int index = 0, i = 0, j = 0;   // i,j: Help translate the terrains properly
        boolean haveRight = false, haveBottom = false;

        for (float[] h : heightmap) {
            index = heightmap.indexOf(h);
            if (index % N < N - 1) {
                // Not in last column
                right = heightmap.get(index + 1);
                haveRight = true;
            }
            if (index < N * (N - 1)) {
                // Not in last row
                bottom = heightmap.get(index + N);
                haveBottom = true;
            }

            for (int k = 0; k < SIZE; k++) {
                if (k == SIZE - 1 && haveBottom && haveRight) {
                    // At the corner and have all neighbors
                    // Could also check if values are equal but very low possibility
                    // Should never reach other two if-statements because this corner
                    // is the last point
                    // heightmap.get(index + N + 1): diagonal neighbor
                    h[SIZE * SIZE - 1] = (h[SIZE * SIZE - 1] + right[SIZE * (SIZE - 1)]
                            + bottom[SIZE - 1] + heightmap.get(index + N + 1)[0]) / 4;
                    right[SIZE * (SIZE - 1)] = h[SIZE * SIZE - 1];
                    bottom[SIZE - 1] = h[SIZE * SIZE - 1];
                    heightmap.get(index + N + 1)[0] = h[SIZE * SIZE - 1];
                    break;
                }
                if (haveRight) {    // right edge
                    h[(k + 1) * SIZE - 1] = (h[(k + 1) * SIZE - 1] + right[k * SIZE]) / 2;
                    right[k * SIZE] = h[(k + 1) * SIZE - 1];
                }
                if (haveBottom) {   // bottom edge
                    h[SIZE * (SIZE - 1) + k] = (h[SIZE * (SIZE - 1) + k] + bottom[k]) / 2;
                    bottom[k] = h[SIZE * (SIZE - 1) + k];
                }
            }
            haveRight = false;
            haveBottom = false;
            if (haveRight) {
                heightmap.set(index + 1, right);
            }
            if (haveBottom) {
                heightmap.set(index + N, bottom);
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
     * Creates the TerrainQuad
     */
    private void initTerrain(int i, int j, int k) {
        TerrainQuad t = new TerrainQuad("Terrain" + i, PATCH_SIZE, SIZE, heightmap.get(i));
        t.setMaterial(InitJME.forest);
        t.setLocalTranslation(k * (SIZE - 1), 0, j * (SIZE - 1));
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