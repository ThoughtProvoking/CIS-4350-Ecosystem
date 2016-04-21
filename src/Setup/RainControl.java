package Setup;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.RigidBodyControl;
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

public class RainControl extends AbstractControl implements ActionListener {
    
    private SimpleApplication sa;
    private Random rand = new Random();
    private ParticleEmitter rain;
    private Node rainNode = new Node();
    private boolean raining = false;
    
    public RainControl(SimpleApplication app) {
        sa = app;
        sa.getRootNode().attachChild(rainNode);
        sa.getRenderManager().preloadScene(rain);
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    private void makeItRain() {
        rain = new ParticleEmitter("Rain", ParticleMesh.Type.Triangle, 30);
        rain.setStartColor(ColorRGBA.LightGray);
        rain.setStartSize(.5f);
        rain.setEndSize(.5f);
        rain.setFacingVelocity(true);
        rain.setParticlesPerSec(10000000);
        rain.setGravity(0, 80f, 0);
        rain.setLowLife(.5f);
        rain.setHighLife(.75f);
        rain.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -8, 0));
        rain.getParticleInfluencer().setVelocityVariation(-.3f);
        rain.setShape(new EmitterSphereShape(Vector3f.ZERO, 10));
        rain.setImagesX(1);
        rain.setImagesY(1);
        rain.setLocalTranslation(0, 265, 0);
        rain.setMaterial(InitJME.rain);
        rainNode.attachChild(rain);
//        RigidBodyControl rainPhys = new RigidBodyControl(1f);
//        rain.addControl(rainPhys);
//        InitJME.bullet.getPhysicsSpace().add(rainPhys);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed && !raining) {
            raining = true;
            makeItRain();
        } else if (isPressed && raining) {
            raining = false;
            rain.removeControl(this);
            rainNode.detachChild(rain);
        }
    }
}