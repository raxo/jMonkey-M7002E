

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;


public class SimpleScene extends SimpleApplication {
 
	private FilterPostProcessor fpp;
	private WaterFilter water;
	private Vector3f lightDir = new Vector3f(-4.9f, -1.3f, 5.9f); // same as light source
	private float initialWaterHeight = -1.8f; // choose a value for your scene
	
    public static void main(String[] args){
    	SimpleScene app = new SimpleScene();
        app.start();
    }
 
    @Override
    public void simpleInitApp() {
    	
    	// water
    	fpp = new FilterPostProcessor(assetManager);
    	water = new WaterFilter(rootNode, lightDir);
    	water.setWaterHeight(initialWaterHeight);
    	//water.setMaxAmplitude(0.3f);
    	//water.setWaveScale(0.08f);
    	water.setSpeed(0.3f);
    	water.setUseRipples(true);
    	fpp.addFilter(water);
    	water.setWaterColor(new ColorRGBA(0.0f,0.5f,0.5f,1.0f));
    	water.setWaterTransparency(0.0f);
    	viewPort.addProcessor(fpp);
 
        // create a blue box at coordinates (1,-1,1) 
        Box box1 = new Box(1,1,1);
        Geometry blue = new Geometry("Box", box1);
        blue.setLocalTranslation(new Vector3f(1,-1,1));
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        blue.setMaterial(mat1);
 
        // create a red box straight above the blue one at (1,3,1) 
        Box box2 = new Box(1,1,1);
        Geometry red = new Geometry("Box", box2);
        red.setLocalTranslation(new Vector3f(1,3,1));
        Material mat2 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);
 
        // Create a pivot node at (0,0,0) and attach it to the root node 
        Node pivot = new Node("pivot");
        rootNode.attachChild(pivot); // put this node in the scene
 
        // sky!
        rootNode.attachChild(SkyFactory.createSky(
                assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        
        // floor
        //PlaneCollisionShape floor = new PlaneCollisionShape(new Plane(new Vector3f(5f,5f,0f), 1.0f));
        
        Mesh mesh = new Mesh();
        
        
        // Attach
        pivot.attachChild(blue);
        pivot.attachChild(red);
        pivot.attachChild(red);
        // Rotate the pivot node: Note that both boxes have rotated! 
        pivot.rotate(.4f,.4f,0f);
    }
    
    class ShipCreator {
    	private Node parent;
    	public ShipCreator(Node parent) {
			this.parent = parent;
		} 
    	private void addFloor() {
    		//http://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/bullet/TestBrickWall.java
    	}
    }
}
