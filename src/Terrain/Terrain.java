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
    public static final float WATER_LEVEL = NORMALIZER / 25.0f;
    private static final int N = 2; // total number of TerrainQuads is N^2
    private SimpleApplication sa;
    private ArrayList<AbstractHeightMap> heightmap = new ArrayList<AbstractHeightMap>();
    private ArrayList<TerrainQuad> terrain = new ArrayList<TerrainQuad>();
    private Node terrainNode = new Node();

    /*
     * Beginning of the terrain generation
     * Looked over TerrainTestTile.java and searched Google for help
     */
    public Terrain(SimpleApplication app) {
        sa = app;
        sa.getRootNode().attachChild(terrainNode);
        initHeightMap();
    }

    /*
     * Generates the heightmaps
     */
    private void initHeightMap() {
        try {
            for (int i = 0; i < Math.pow(N, 2); i++) {
                AbstractHeightMap h = new MidpointDisplacementHeightMap(SIZE, RANGE, PERSISTENCE);
                heightmap.add(h);
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
        float[] currentHeightMap, rightHeightMap = null, bottomHeightMap = null, diagonalHeightMap = null;
        int index;

        for (AbstractHeightMap h : heightmap) {
            index = heightmap.indexOf(h);
            // Used same logic as Maze assignment: just check right and bottom edges
            currentHeightMap = h.getHeightMap();
            if (index % N != N - 1) {
                // No right neighbor if on rightmost side
                rightHeightMap = getRightMap(index).getHeightMap();
            }
            if (index < Math.pow(N, 2) - N) {
                // No bottom neighbor if on bottom row
                bottomHeightMap = getDownMap(index).getHeightMap();
            }
            if (index % N != N - 1 && index < Math.pow(N, 2) - N) {
                // No diagonal neighbor if on bottom row or on right most side
                diagonalHeightMap = getDiagMap(index).getHeightMap();
            }

            for (int i = 0; i < SIZE; i++) {
                if (i < SIZE - 1) {
                    // Not the bottom right corner
                    if (currentHeightMap[(i + 1) * SIZE - 1] != rightHeightMap[i * SIZE]) {
                        // Fix right edge if not equal
                        currentHeightMap[(i + 1) * SIZE - 1] =
                                (currentHeightMap[(i + 1) * SIZE - 1] + rightHeightMap[i * SIZE]) / 2;
                        rightHeightMap[i * SIZE] = currentHeightMap[(i + 1) * SIZE - 1];
                    }

                    if (currentHeightMap[SIZE * (SIZE - 1) + i] != bottomHeightMap[i]) {
                        // Fix bottom edge if not equal
                        currentHeightMap[SIZE * (SIZE - 1) + i] =
                                (currentHeightMap[SIZE * (SIZE - 1) + i] + bottomHeightMap[i]) / 2;
                        bottomHeightMap[i] = currentHeightMap[SIZE * (SIZE - 1) + i];
                    }
                } else {
                    // reached the corner
                    currentHeightMap[SIZE * (SIZE - 1) + i] =
                            (currentHeightMap[SIZE * (SIZE - 1) + i] + rightHeightMap[i * SIZE]
                            + bottomHeightMap[i] + diagonalHeightMap[0]) / 4;
                    rightHeightMap[i * SIZE] = currentHeightMap[SIZE * (SIZE - 1) + i];
                    bottomHeightMap[i] = currentHeightMap[SIZE * (SIZE - 1) + i];
                    diagonalHeightMap[0] = currentHeightMap[SIZE * (SIZE - 1) + i];
                }
            }
            initTerrain();
        }
    }

    /*
     * Gets the heightmap of bottom, right, and diagonal neighbors to fix the edges
     */
    private AbstractHeightMap getDiagMap(int i) {
        return heightmap.get(i + N + 1);
    }

    private AbstractHeightMap getRightMap(int i) {
        return heightmap.get(i + 1);
    }

    private AbstractHeightMap getDownMap(int i) {
        return heightmap.get(i + N);
    }

    /*
     * Creates the TerrainQuads
     */
    private void initTerrain() {
        // Supposedly helps with LOD of multiple TerrainQuads
        MultiTerrainLodControl mlod = new MultiTerrainLodControl(sa.getCamera());
        int k = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                TerrainQuad t = new TerrainQuad("Terrain" + k, PATCH_SIZE, SIZE, heightmap.get(k).getHeightMap());
                t.setMaterial(InitJME.wireframe);
                t.setLocalTranslation(i * SIZE, 0, j * SIZE);
                t.setNeighbourFinder(this);
                mlod.addTerrain(t);
                terrainNode.attachChild(t);
                terrain.add(t);

                // Terrain physics
                CollisionShape cShape = CollisionShapeFactory.createMeshShape((Node) t);
                RigidBodyControl terrainPhys = new RigidBodyControl(cShape, 0.0f);
                t.addControl(terrainPhys);
                InitJME.bullet.getPhysicsSpace().add(terrainPhys);
                k++;
            }
        }
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