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
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    private void makeItRain() {
        rain = new ParticleEmitter("Rain", ParticleMesh.Type.Point, 300);
        rain.setMaterial(InitJME.rain);
        rain.setStartColor(ColorRGBA.Red);
        rain.setStartSize(1f);
        rain.setGravity(0, -1f, 0);
        rain.getParticleInfluencer().setVelocityVariation(.5f);
        rain.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
        rain.setParticlesPerSec(30);
        rain.setLocalTranslation(0, 270, 0);
        rain.setShape(new EmitterSphereShape(new Vector3f(0, 255, 0), 5));
        rain.addControl(this);
        rainNode.attachChild(rain);

        RigidBodyControl rainPhys = new RigidBodyControl(1f);
        rain.addControl(rainPhys);
        InitJME.bullet.getPhysicsSpace().add(rainPhys);
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed && !raining) {
            raining = true;
            makeItRain();
        } else {
            raining = false;
            rain.removeControl(this);
            rainNode.detachChild(rain);
        }
    }
}

