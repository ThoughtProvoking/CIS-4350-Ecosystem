package Water;

import Terrain.Terrain;
import Setup.InitJME;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

public class Water {

    private SimpleApplication sa;
    private WaterFilter water;
    private Vector3f lightDir = InitJME.sun.getDirection();

    /*
     * Calls the initialization methods and initializes the water
     */
    public Water(SimpleApplication app) {
        sa = app;
        water = new WaterFilter(app.getRootNode(), lightDir);
        configure();
        initFilter();
        initSound();
    }

    /*
     * Sets some settings for the water
     * All settings and their explanations: https://wiki.jmonkeyengine.org/doku.php/jme3:advanced:post-processor_water
     * Original source code: TestPostWater.java
     */
    private void configure() {
        water.setWaterColor(new ColorRGBA().set(0.0078f, 0.3176f, 0.5f, 1.0f));
        water.setDeepWaterColor(new ColorRGBA().set(0.0039f, 0.00196f, 0.145f, 1.0f));
        water.setUnderWaterFogDistance(200);
        water.setWaterTransparency(0.1f);
        water.setFoamIntensity(0.4f);
        water.setFoamHardness(0.3f);
        water.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
        water.setReflectionDisplace(50);
        water.setRefractionConstant(0.25f);
        water.setColorExtinction(new Vector3f(30, 50, 70));
        water.setCausticsIntensity(0.4f);
        water.setWaveScale(0.001f);
        water.setMaxAmplitude(3f);
        water.setFoamTexture((Texture2D) sa.getAssetManager().loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        water.setRefractionStrength(0.2f);
        water.setWaterHeight(Terrain.WATER_LEVEL);
    }

    /*
     * Initializes the filters to make the water look nice
     */
    private void initFilter() {
        //Bloom Filter
        BloomFilter bloom = new BloomFilter();
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);

        //Light Scattering Filter
        LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(-300));
        lsf.setLightDensity(0.5f);

        //Depth of field Filter
        DepthOfFieldFilter dof = new DepthOfFieldFilter();
        dof.setFocusDistance(200);
        dof.setFocusRange(200);

        FilterPostProcessor fpp = new FilterPostProcessor(sa.getAssetManager());

        fpp.addFilter(water);
        fpp.addFilter(bloom);
        fpp.addFilter(dof);
        fpp.addFilter(lsf);
        fpp.addFilter(new FXAAFilter());

        sa.getViewPort().addProcessor(fpp);
    }

    /*
     * Initializes the sound of the ocean
     */
    private void initSound() {
        AudioNode waves = new AudioNode(sa.getAssetManager(), "Sound/Environment/Ocean Waves.ogg", false);
        waves.setLooping(true);
        sa.getAudioRenderer().playSource(waves);
    }
}