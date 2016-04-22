package Terrain;

import Setup.InitJME;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import forester.Forester;
import forester.grass.GrassLayer;
import forester.grass.GrassLayer.MeshType;
import forester.grass.GrassLoader;
import forester.grass.algorithms.GPAUniform;
import forester.grass.datagrids.MapGrid;
import forester.image.FormatReader;
import java.util.ArrayList;
import java.util.Random;

public class Flora extends AbstractControl {

    /*
     * Example: (Not up-to-date; see below for differences)
     * http://jcarpet.googlecode.com/svn/trunk/eclipseProject/src/com/jcarpet/test/terrain/SimpleGrassTest.java
     * 
     * Tutorial: (Not very detailed)
     * https://wiki.jmonkeyengine.org/doku.php/jme3:contributions:vegetationsy1stem
     */
    private final int VIEWING_RANGE;
    private Random rand = new Random();
    private SimpleApplication sa;
    private Node floraNode = new Node();
    private Forester forester = Forester.getInstance();
    private GrassLoader loader;
    private MapGrid grid;
    private Texture density;
    private GrassLayer layer;

    /*
     * Constructor
     * Adds control and attaches floraNode
     */
    public Flora(SimpleApplication app, ArrayList<TerrainQuad> terrain) {
        sa = app;
        floraNode.addControl(this);
        sa.getRootNode().attachChild(floraNode);
        VIEWING_RANGE = (int) sa.getCamera().getFrustumFar();
        createFlora(terrain);
    }

    /*
     * Equivalent to setupForester method of example code
     * 
     * getForesterNode() does not exist
     * initialize() only takes four parameters, not five
     * Grass.j3m does not exist; equivalent may be grassBase.j3md
     * setMaxTerrainSlope() also does not exist
     */
    private void createFlora(ArrayList<TerrainQuad> terrain) {
        for (TerrainQuad t : terrain) {
            forester.initialize(sa.getRootNode(), sa.getCamera(), t, sa);
            loader = forester.createGrassLoader(t.getTotalSize(), 4, VIEWING_RANGE, VIEWING_RANGE / 2);
            grid = loader.createMapGrid();
            density = t.getMaterial().getTextureParam("AlphaMap").getTextureValue();
            grid.addDensityMap(density, (int) t.getLocalTranslation().x, (int) t.getLocalTranslation().z, 0);
            layer = loader.addLayer(InitJME.vegGrass, MeshType.CROSSQUADS);
            layer.setDensityTextureData(0, FormatReader.Channel.Red);
            layer.setDensityMultiplier(0.8f);
            layer.setMaxHeight(2.4f);
            layer.setMinHeight(2.f);
            layer.setMaxWidth(2.4f);
            layer.setMinWidth(2.f);
            ((GPAUniform) layer.getPlantingAlgorithm()).setThreshold(0.6f);
            loader.setWind(Vector2f.UNIT_XY);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        forester.update(tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
