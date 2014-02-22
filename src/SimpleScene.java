import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Matrix3f;

import jme3tools.optimize.TextureAtlas;

import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.LowPassFilter;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Surface;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

/**
 * In order to compile in eclipce: Add assets folder as Class folder. Add jMonkey jars.  
 * @author oskkla-9
 * 
 * 
1. Write a program that creates a 3D view of some enclosed environment: This could be a 
bedroom, a classroom, a church hall, an outdoor courtyard, or even an outdoor terrain in a 
cubic (room) universe. You can model any kind of room, real or imaginary. Start by creating 
objects and enter their 3D coordinates in the “environment”. You do not have to model it in 
complete detail, just the walls, door, a few pieces of furniture, or outdoor terrain, trees, 
bench. You can use simple, block-like shapes to represent the furniture, trees (even as 
simple as a sphere on top of a vertical cylinder). 
DONE skybox, water

2. Model Creation: You can create the objects programmatically using basic primitives, or use a 
modeling program such as Blender (available at www.blender.org for you to download and 
install) and then load the resulting models into your scene. 
DONE Ship

3. Instancing: Your room should contain some similar objects (like trees or matching chess 
pieces), you should model a generic object (like the pawn chess piece) just once, then use it 
over and over again in your hierarchy, with a different local transformation matrix each time, 
so each object shows up in a different location (and possible different size, remember 
“instancing” from the lecture notes). Instancing is an important part of the power of 
a scenegraph (and this assignment). JMonkey implements instancing automatically for 
shared mesh objects between the geometries using it (more discussion on instancing from 
the JME3 forums below) 
DONE Ship's mast's

4. Smooth motion: In response to user input (keystroke, mouse click) you should move 
something in the room (for example, one of the chairs could move along floor, a door could 
open, objects could move on a desk, people move in the room, or a person's head or arm 
could move). Objects should move smoothly following an animation path. Allow at least 3 
things to be moved by the user this way.
island, 

 *
 */
public class SimpleScene extends SimpleApplication {
 
	private FilterPostProcessor fpp;
	private WaterFilter water;
	private Vector3f lightDir = new Vector3f(-4.9f, -1.3f, 5.9f); // same as light source
	private Node floatingNode, solidNode;
	private boolean enableWaterBobbing = true, enableEarthquake = false;
	MotionEvent Earthquaker;
	
    public static void main(String[] args){
    	SimpleScene app = new SimpleScene();
        app.start();
    }
 
    @Override
    public void simpleInitApp() {
    	
    	
    	
    	
    	

        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
    	
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
    	water.setUseFoam(true);
    	water.setWaterTransparency(0.1f);
    	water.setFoamTexture( (Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
    	/*
    	AudioNode waves = new AudioNode(assetManager, "Sounds/RowingBoat.wav", false);
    	waves.setLooping(true);
    	audioRenderer.playSource(waves);
    	*/
    	viewPort.addProcessor(fpp);
    	
    	flyCam.setMoveSpeed(100);
    	cam.setLocation(new Vector3f(0,10,0));
    	
    	// sky!
        rootNode.attachChild(SkyFactory.createSky( assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false));
    	
        // solid
        solidNode = new Node("pivot");
        rootNode.attachChild(solidNode); // put this node in the scene
        Island i = new Island(solidNode, "mainLand");
        i.generate();
        solidNode.getChild("mainLand").setLocalTranslation(new Vector3f(150f, 0, 150f));
        
        MotionPath path = new MotionPath();
        path.addWayPoint(new Vector3f(1, 0, 0));
        path.addWayPoint(new Vector3f(0, 1, 0));
        path.addWayPoint(new Vector3f(0, 0, 1));
        path.setCycle(true);
        Earthquaker = new MotionEvent(solidNode,path);
        Earthquaker.setLoopMode(LoopMode.Cycle);
        Earthquaker.setSpeed(20f);
        
        
        // float
        floatingNode = new Node("floating");
        rootNode.attachChild(floatingNode);
        Ship s = new Ship(floatingNode, "flagship");
        s.setDimentions(10, 8, 25, 7);
        s.build();
        ((Ship) floatingNode.getChild("flagship").getUserData("class")).addFloor();
        
        // input
        inputManager.addMapping("SetSail",  new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("Left",   new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right",  new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("SetAnchor", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("FIRE", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Earthquake", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("WaterBobbing", new KeyTrigger(KeyInput.KEY_B));
        // Add the names to the action listener.
        inputManager.addListener(actionListener,"Earthquake", "WaterBobbing");
        inputManager.addListener(analogListener,"Left", "Right", "Rotate", "SetSail", "FIRE");
    }
    
    private ActionListener actionListener = new ActionListener() {
    	public void onAction(String name, boolean keyPressed, float tpf) {
        	if (name.equals("Earthquake") && keyPressed) {
        		enableEarthquake = !enableEarthquake;
        		if(enableEarthquake) {
        			Earthquaker.play();
	            } else {
	            	Earthquaker.stop();
	        	}
        	}
        	if (name.equals("WaterBobbing") && keyPressed) {
        		enableWaterBobbing = !enableWaterBobbing;
        	}
		}
    };
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
    		if (name.equals("FIRE")) {

    		}
    		if (name.equals("SetSail")) {
    			// http://hub.jmonkeyengine.org/wiki/doku.php/jme3:advanced:motionpath
    		}
    		if (name.equals("SetAnchor")) {

    		}
    		if (name.equals("Right")) {

    		}
        	if (name.equals("Left")) {

        	}
        }
    };
    
    private float time = 0.0f;
    private float waterHeight = 0.0f;
    private float initialWaterHeight = 0f;
    private boolean uw = false;
    //AudioNode waves;
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        if(enableWaterBobbing) {
            time += tpf;
	        waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
	        water.setWaterHeight(initialWaterHeight + waterHeight);
	        
	        // move floating node up/down
	        Vector3f v = floatingNode.getLocalTranslation();
	        v.y = waterHeight;
	        floatingNode.setLocalTranslation(v);
	        
        }
    }
    
    abstract class MyNode implements Savable {
    	protected Node node, parentNode;
    	
    	public MyNode(Node parentNode, String id) {
    		node = new Node(id);
    		this.parentNode = parentNode;
    		node.setUserData("class", this);
		}
    	
    	protected void attatch(Node parentNode) {
    		parentNode.attachChild(node);
    	}
    	
    	public Node getNode() {
    		return node;
    	}
    }
    
    class Island extends MyNode {
    	
    	private static final float defaultWith = 50f;
    	private float width = defaultWith;

    	public Island(Node parentNode, String id) {
    		super(parentNode, id);
		}
    	
    	public void generate() {
    		Random random = new Random();
    		/*
    		List<List<Vector4f>> controlPoints = null;
    		List<Float>[] nurbKnots = null;
    		int uSegments;
    		int vSegments;
    		int basisUFunctionDegree;
    		int basisVFunctionDegree;
    		Surface s = Surface.createNurbsSurface(controlPoints, nurbKnots, 0, 0, 0, 0); // wtf is this shit? docs pls
    		*/

    		Material mat = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
    		Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
    	    dirt.setWrap(WrapMode.Repeat);
    	    mat.setTexture("Tex2", dirt);
    	    mat.setFloat("Tex2Scale", 32f);
    		
    		Sphere s = new Sphere(20, 20, width/2);
    		Geometry island = new Geometry("flagPole", s);
    		island.setMaterial(mat);
    		
    		Spatial palm = assetManager.loadModel("Models/palm.obj"); // http://www.the3dmodelstore.com/free.php
    		palm.setLocalScale(new Vector3f(0.2f, 0.2f, 0.2f));
    		node.attachChild(palm);
    		
    		node.setLocalTranslation(new Vector3f(0, 10f, 0f));
    		island.setLocalScale(5, 0.2f, 3);
    		
    		node.attachChild(island);
    		parentNode.attachChild(node);
    	}
    	
    	@Override
    	public void read(JmeImporter ex) throws IOException {
    		OutputCapsule capsule = (OutputCapsule) ex.getCapsule(this);
            capsule.write(width,  "hullHeight", defaultWith);
    	}

    	@Override
    	public void write(JmeExporter im) throws IOException {
            InputCapsule capsule = (InputCapsule) im.getCapsule(this);
            width = capsule.readFloat("hullHeight", defaultWith);
    	}
    	
    }
    
    class Ship extends MyNode {
    	
    	private float hullHeight = 7.0f;
    	private float hullWidth = 5.0f;
    	private float hullLength = 20.0f;
    	private float waterDepth = 5.0f;
    	

    	//private AssetManager assetManager;
    	
    	public Ship(Node parentNode, String id) {
    		super(parentNode, id);
    	}
    	
    	public void setDimentions(float hullHeight, float hullWidth, float hullLength, float waterDepth) {
    		this.hullHeight = hullHeight;
    		this.hullWidth = hullWidth;
    		this.hullLength = hullLength;
    		this.waterDepth = waterDepth;
    	}
    	
    	public void build() {
    		//addFloor();
    		addHull();
    		addMast(hullLength/4f);
    		addMast(hullLength/1.5f);
    		addAftercastle();
    		addBowsprit();

    		parentNode.attachChild(node);
    	}
    	
    	private void addMast(float lengthFromFront) {
    		Spatial mastNode = node.getChild("mast");
    		if(mastNode != null && mastNode instanceof Node) {
    			System.out.println("Cloning mast Node!");
    			mastNode = (Node) mastNode.clone(true);
    			System.out.println(lengthFromFront);
	    		mastNode.setLocalTranslation(lengthFromFront, 0, 0);
    		} else {
    			System.out.println("Creating mast Node at x="+lengthFromFront);
        		float mastHeight = 2*hullHeight;
        		
	    		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    		mat.setColor("Color", ColorRGBA.Brown);
        		
	    		Cylinder c = new Cylinder(16, 16, 0.5f, mastHeight, true);
	    		Geometry mast = new Geometry("mast", c);
	    		mast.setMaterial(mat);
	    		mast.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) ));
	    		mast.setLocalTranslation(new Vector3f(0, hullHeight, 0));
	    		
	    		Cylinder c2 = new Cylinder(16, 16, 0.1f, hullWidth, true);
	    		Geometry bottom = new Geometry("bottom", c2);
	    		bottom.setMaterial(mat);
	    		bottom.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,1,0) ));
	    		bottom.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,0,1) ));
	    		bottom.setLocalTranslation(new Vector3f(-0.5f, hullHeight+hullHeight*1/6, 0));
	    		
	    		Geometry top = new Geometry("top", c2);
	    		top.setMaterial(mat);
	    		top.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,1,0) ));
	    		top.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,0,1) ));
	    		top.setLocalTranslation(new Vector3f(-0.5f, hullHeight+hullHeight-hullHeight*1/6, 0));

	    		Cylinder c3 = new Cylinder(16, 16, 1f, hullHeight/10, true);
	    		Geometry crowsNest = new Geometry("crowsNest", c3);
	    		crowsNest.setMaterial(mat);
	    		crowsNest.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) ));
	    		crowsNest.setLocalTranslation(new Vector3f(0, mastHeight, 0));

	    		Cylinder c4 = new Cylinder(16, 16, 0.05f, 4*hullHeight/10, true);
	    		Geometry flagPole = new Geometry("flagPole", c4);
	    		flagPole.setMaterial(mat);
	    		flagPole.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) ));
	    		flagPole.setLocalTranslation(new Vector3f(0, mastHeight+hullHeight/10, 0));
	    		
	    		Box b = new Box(0.1f, hullWidth/2, hullHeight*2/6);
	    		Geometry sail = new Geometry("sail", b);
	    		Material mat2 = mat.clone();
	    		mat2.setColor("Color", ColorRGBA.White);
	    		sail.setMaterial(mat2);
	    		sail.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) ));
	    		sail.setLocalTranslation(new Vector3f(-0.7f, hullHeight+hullHeight-hullHeight*3/6, 0));
	    		
	    		Box b2 = new Box(0.1f, hullHeight/12, hullHeight/12);
	    		Geometry flag = new Geometry("flag", b2);
	    		Material mat3 = mat.clone();
	    		mat3.setColor("Color", ColorRGBA.Black);
	    		flag.setMaterial(mat3);
	    		flag.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,1,0) ));
	    		flag.setLocalTranslation(new Vector3f(hullHeight/12, mastHeight+hullHeight/10+hullHeight/12, 0));
	    		
	    		mastNode = new Node("mast");
	    		((Node) mastNode).attachChild(mast);
	    		((Node) mastNode).attachChild(bottom);
	    		((Node) mastNode).attachChild(top);
	    		((Node) mastNode).attachChild(crowsNest);
	    		((Node) mastNode).attachChild(flagPole);
	    		((Node) mastNode).attachChild(sail);
	    		((Node) mastNode).attachChild(flag);
	    		mastNode.setLocalTranslation(lengthFromFront, 0, 0);
    		}
    		this.node.attachChild(mastNode);
    	}
    	
    	private void addFloor() {
    		//http://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/bullet/TestBrickWall.java
    		
    		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    		Texture t = assetManager.loadTexture("Textures/wood2.jpg");
    		t.setWrap(WrapMode.Repeat);
            mat.setTexture("ColorMap", t);
    		
    		Box floorBox = new Box(hullLength/2-hullLength/6, 0.1f, hullWidth/2);
            //floorBox.scaleTextureCoordinates(new Vector2f(3, 6));

            Geometry floor = new Geometry("floor", floorBox);
            floor.setMaterial(mat);
            floor.setLocalTranslation(new Vector3f(hullLength/2,hullHeight-waterDepth+0.1f,0));
            //floor.setShadowMode(ShadowMode.Receive);
            //floor.addControl(new RigidBodyControl(new BoxCollisionShape(new Vector3f(10f, 0.1f, 5f)), 0));
            this.node.attachChild(floor);
            //this.getPhysicsSpace().add(floor);

    	}
    	
    	private void addAftercastle() {
    		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    		Texture t = assetManager.loadTexture("Textures/wood2.jpg");
    		t.setWrap(WrapMode.Repeat);
            mat.setTexture("ColorMap", t);
            
    		Box b = new Box(hullLength/12, hullHeight/4, hullWidth/2);
            Geometry castle = new Geometry("aftercastle", b);
    		castle.setMaterial(mat);
    		
    		castle.setLocalTranslation(new Vector3f(hullLength-hullLength/12,hullHeight-waterDepth+hullHeight/4,0));
    		this.node.attachChild(castle);
    	}
    	
    	private void addBowsprit() {
			Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    		mat.setColor("Color", ColorRGBA.Brown);
    		
    		Cylinder c = new Cylinder(16, 16, 0.2f, hullHeight, true);
			Geometry bowsprit = new Geometry("flagPole", c);
			bowsprit.setMaterial(mat);
			double sinCosMathShitt = Math.tan((hullHeight/6)/(hullLength/6));
			bowsprit.getLocalRotation().multLocal(new Quaternion().fromAngleAxis( (float) (FastMath.PI/2) , new Vector3f(0,1,0) ));
			bowsprit.getLocalRotation().multLocal(new Quaternion().fromAngleAxis( (float) (sinCosMathShitt) , new Vector3f(1,0,0) ));
			//bowsprit.setLocalRotation(q);
			bowsprit.setLocalTranslation(new Vector3f(-hullHeight*1/3, hullHeight-waterDepth+2*hullHeight/6, 0));
			node.attachChild(bowsprit);
    	}
    	
    	private static final float derp = 1;
    	
    	private void addHull() {
    		// http://jmonkeyengine.googlecode.com/svn/branches/stable-alpha4/engine/src/test/jme3test/model/shape/TestCustomMesh.java
    		
    		
    		
    		Mesh m = new Mesh();
    		
    		// the front hull 2 sides z posetive out. 
    		
    		int verticesNum = 11;
    		// Vertex positions in space
            Vector3f [] vertices = new Vector3f[verticesNum];
            vertices[0] = new Vector3f(0,hullHeight/6,0); // front
            vertices[1] = new Vector3f(hullLength/6,-hullHeight/3,hullWidth/2); // left down
            vertices[2] = new Vector3f(hullLength/6,-hullHeight/3,-hullWidth/2); // right down
            vertices[3] = new Vector3f(hullLength/6,0,hullWidth/2); // left up
            vertices[4] = new Vector3f(hullLength/6,0,-hullWidth/2); // right up
            vertices[5] = new Vector3f(hullLength/6,-hullHeight,0); // buttom
            
        	// back
            vertices[6] = new Vector3f(hullLength,0,hullWidth/2); // left up
            vertices[7] = new Vector3f(hullLength,0,-hullWidth/2); // right up
            vertices[8] = new Vector3f(hullLength,-hullHeight/3,hullWidth/2); // left down
            vertices[9] = new Vector3f(hullLength,-hullHeight/3,-hullWidth/2); // right down
            vertices[10] = new Vector3f(hullLength,-hullHeight,0); // buttom
            
            // Texture coordinates
            Vector2f [] texCoord = new Vector2f[verticesNum];
            texCoord[0] = new Vector2f(0.0f,0.0f);
            texCoord[1] = new Vector2f(1/6f,1/6f);
            texCoord[2] = new Vector2f(1/6f,1/6f);
            texCoord[3] = new Vector2f(1/6f,1/6f);
            texCoord[4] = new Vector2f(1/6f,1/6f);
            texCoord[5] = new Vector2f(1/6f,1/6f);
            texCoord[6] = new Vector2f(5/6f,5/6f);
            texCoord[7] = new Vector2f(5/6f,5/6f);
            texCoord[8] = new Vector2f(5/6f,5/6f);
            texCoord[9] = new Vector2f(5/6f,5/6f);
            texCoord[10] = new Vector2f(1,1);
            
            int [] indexesLeft = {
        		1,3,0, // front up left
        		0,5,1, // front down left
        		
        		1,8,3, // side up left
        		3,8,6, // side up left
        		1,5,8, // side down left
        		8,5,10, // side down left
        		
        		10,9,8, // back down
        		8,9,7, // back up
        		7,6,8, // back up
        		
        		4,7,2, // side up right
        		2,7,9, // side up right
        		9,10,2, // side down right
        		2,10,5, // side down right
        		
        		0,4,2, // front up right
        		2,5,0, // front down right
        		
        		0,3,4,
        		4,6,7,
        		6,4,3
    		};
            float[] normals = new float[] {
        		0,1,0,
        		0,0,1,
        		0,0,-1,
        		0,1,0,
        		0,1,0,
        		0,-1,0,
        		0,1,0,
        		0,1,0,
        		0,0,1,
        		0,0,-1,
        		0,1,0
            };

            // Setting buffers
            m.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
            m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
            m.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexesLeft));
            m.updateBound();
            
            
            Geometry hull = new Geometry("OurMesh", m);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            //mat.setColor("Color", ColorRGBA.Brown);
            
            
            Texture t = assetManager.loadTexture("Textures/wood2.jpg");
    		t.setWrap(WrapMode.Repeat);
            mat.setTexture("ColorMap", t);

            hull.setMaterial(mat);

            hull.setLocalTranslation(new Vector3f(0,hullHeight-waterDepth,0));
            
            
            node.attachChild(hull);
    	}

    	@Override
    	public void read(JmeImporter ex) throws IOException {
    		OutputCapsule capsule = (OutputCapsule) ex.getCapsule(this);
            capsule.write(hullHeight,  "hullHeight", 7.0f);
            capsule.write(hullWidth,"hullWidth", 5.0f);
            capsule.write(hullLength, "hullLength",20.0f);
            capsule.write(waterDepth, "waterDepth",5.0f);
    	}

    	@Override
    	public void write(JmeExporter im) throws IOException {
            InputCapsule capsule = (InputCapsule) im.getCapsule(this);
            hullHeight = capsule.readFloat("hullHeight", 7);
            hullWidth = capsule.readFloat("hullWidth", 5f);
            hullLength = capsule.readFloat("hullLength", 20f);
            waterDepth  = capsule.readFloat("waterDepth", 5.0f);
    	}
    }

    
}
