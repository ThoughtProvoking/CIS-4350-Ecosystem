package Setup;

import Main.Main;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import java.util.Random;

/*
 * Implements ActionListener to allow manual rain generation
 * Extends AbstractControl to randomly generate rain (only if not already raining)
 */
public class RainControl extends AbstractControl implements ActionListener {

    private Main sa;
    private Random rand = new Random();
    private ParticleEmitter rain;
    private boolean raining = false;
    private Node rainNode = new Node();

    /*
     * Constructor
     * Attaches rainNode to root node and preloads rain scene to render manager
     */
    public RainControl(Main app) {
        sa = app;
        sa.getRootNode().attachChild(rainNode);
        sa.getRenderManager().preloadScene(rain);
    }

    /*
     * Randomly generate or stop rain if random number < 4300
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (rand.nextInt() < 430000) {
            if (!raining) {
                raining = true;
                makeItRain();
            } else {
                raining = false;
                rain.removeControl(this);
                rainNode.detachChild(rain);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Does nothing
    }

    /*
     * Initializes and creates the rain particle emitter
     * EmitterSphereShape - creates a sphere where all positions inside the sphere
     * are valid spawn points for particles
     * Links: https://wiki.jmonkeyengine.org/doku.php/jme3:beginner:hello_effects
     *     or
     * https://wiki.jmonkeyengine.org/doku.php/jme3:advanced:particle_emitters
     */
    private void makeItRain() {
        rain = new ParticleEmitter("Rain", ParticleMesh.Type.Triangle, 1200);
        rain.setStartColor(ColorRGBA.LightGray);
        rain.setStartSize(.5f);
        rain.setEndSize(.5f);
        rain.setFacingVelocity(true);
        rain.setParticlesPerSec(600);
        rain.setGravity(0, 160f, 0);
        rain.setLowLife(.3f);
        rain.setHighLife(.5f);
        rain.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -8, 0));
        rain.getParticleInfluencer().setVelocityVariation(-.3f);
        rain.setShape(new EmitterSphereShape(Vector3f.ZERO, 20));
        rain.setImagesX(1);
        rain.setImagesY(1);
        rain.setLocalTranslation(sa.getCamera().getLocation().add(0, 15, 0));
        rain.setMaterial(InitJME.rain);
        rainNode.attachChild(rain);
    }

    /*
     * If user pressed 'r' key, makeItRain()
     * Will deactivate the randomly generated rain if pressed while raining
     */
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if (!raining) {
                raining = true;
                makeItRain();
                System.out.println(rain.getLocalTranslation());
            } else {
                raining = false;
                rain.removeControl(this);
                rainNode.detachChild(rain);
            }
        }
    }
}