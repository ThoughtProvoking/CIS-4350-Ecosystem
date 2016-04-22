package Setup;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.awt.Dimension;
import java.awt.Toolkit;

public class InitJME {

    private static SimpleApplication sa;
    private static Texture grass, dirt, rock, vegColor, vegNoise;
    public static Material wireframe, forest, rain, vegGrass;
    public static BulletAppState bullet;
    public static DirectionalLight sun;

    /*
     * Initializes the settings for the game screen
     */
    public static void configScreen(SimpleApplication app) {
        AppSettings aps = new AppSettings(true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        screen.width *= 0.9;
        screen.height *= 0.9;
        aps.setResolution(screen.width, screen.height);
        app.setSettings(aps);
        app.setShowSettings(false);
    }

    /*
     * Calls the intialization methods
     */
    public static void initGraphics(SimpleApplication app) {
        sa = app;
        initMaterials();
        initLights();
        initCamera();
        initPhysics();
        initSky();
    }

    /*
     * Create materials
     */
    private static void initMaterials() {
        // Terrain wireframe
        wireframe = new Material(sa.getAssetManager(), "Common/MatDefs/Terrain/TerrainLighting.j3md");
        wireframe.getAdditionalRenderState().setWireframe(true);

        // Texture splatting
        forest = new Material(sa.getAssetManager(), "Common/MatDefs/Terrain/TerrainLighting.j3md");
        forest.setTexture("AlphaMap", sa.getAssetManager().loadTexture("Textures/Terrain/splat/alphamap.png"));
        grass = sa.getAssetManager().loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        forest.setTexture("DiffuseMap", grass);
        forest.setFloat("DiffuseMap_0_scale", 64f);
        dirt = sa.getAssetManager().loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        forest.setTexture("DiffuseMap_1", dirt);
        forest.setFloat("DiffuseMap_1_scale", 32f);
        rock = sa.getAssetManager().loadTexture("Textures/Terrain/splat/road.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        forest.setTexture("DiffuseMap_2", rock);
        forest.setFloat("DiffuseMap_2_scale", 128f);

        // Material for vegetation system
        vegGrass = new Material(sa.getAssetManager(), "Resources/MatDefs/Grass/grassBase.j3md");
        vegColor = sa.getAssetManager().loadTexture("Resources/Textures/Grass/grass.png");
        vegGrass.setTexture("ColorMap", vegColor);
        vegNoise = sa.getAssetManager().loadTexture("Resources/Textures/Grass/noise.png");
        vegGrass.setTexture("AlphaNoiseMap", vegNoise);

        // Rain material
        rain = new Material(sa.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        rain.setTexture("Texture", sa.getAssetManager().loadTexture("Effects/Explosion/spark.png"));
    }

    /*
     * Initialize light
     */
    private static void initLights() {
        sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        sa.getRootNode().addLight(sun);
    }

    /*
     * Initialize the camera
     */
    private static void initCamera() {
        sa.getFlyByCamera().setEnabled(true);
        sa.getFlyByCamera().setMoveSpeed(100.0f);
        sa.getFlyByCamera().setZoomSpeed(100.0f);
        sa.getCamera().setFrustumFar(4000.0f);
        sa.getCamera().setLocation(new Vector3f(0, 255, 0));
    }

    /*
     * Initialize the physics engine
     */
    private static void initPhysics() {
        bullet = new BulletAppState();
        sa.getStateManager().attach(bullet);
    }

    /*
     * Initialize the sky background
     */
    private static void initSky() {
        Spatial sky = SkyFactory.createSky(sa.getAssetManager(), "Scenes/Beach/FullskiesSunset0068.dds", false);
        sa.getRootNode().attachChild(sky);
    }
}