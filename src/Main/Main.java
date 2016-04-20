package Main;

import Terrain.Terrain;
import Setup.InitJME;
import Water.Water;
import com.jme3.app.SimpleApplication;

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
    }

    @Override
    public void simpleUpdate(float tpf) {
    }
}