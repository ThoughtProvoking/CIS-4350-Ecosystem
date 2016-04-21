package Main;

import Terrain.Terrain;
import Setup.InitJME;
import Setup.RainControl;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        Main app = new Main();
        // configures game screen
        InitJME.configScreen(app);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // initialize materials, lights, etc.
        InitJME.initGraphics(this);
        // create the terrain
        Terrain t = new Terrain(this);
        // create the water
//        Water w = new Water(this);

        // Manully activate/deactivate the rain
        inputManager.addMapping("Rain", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(new RainControl(this), "Rain");
    }

    @Override
    public void simpleUpdate(float tpf) {
        // Does nothing
    }
}