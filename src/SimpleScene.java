import java.io.IOException;

import javax.vecmath.Matrix3f;

import jme3tools.optimize.TextureAtlas;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.LowPassFilter;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.BufferUtils;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

/**
 * In order to compile in eclipce: Add assets folder as Class folder. Add jMonkey jars.  
 * @author oskkla-9
 *
 */
public class SimpleScene extends SimpleApplication {
 
	private FilterPostProcessor fpp;
	private WaterFilter water;
	private Vector3f lightDir = new Vector3f(-4.9f, -1.3f, 5.9f); // same as light source
	private Node floatingNode;
	
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
    	water.setUseFoam(true);
    	water.setWaterTransparency(1f);
    	/*
    	AudioNode waves = new AudioNode(assetManager, "Sounds/RowingBoat.wav", false);
    	waves.setLooping(true);
    	audioRenderer.playSource(waves);
    	*/
    	viewPort.addProcessor(fpp);
    	
    	flyCam.setMoveSpeed(10);
    	cam.setLocation(new Vector3f(0,10,0));
 /*
        // create a blue box at coordinates (1,-1,1) 
        Box box1 = new Box(1,1,1);
        Geometry blue = new Geometry("Box", box1);
        blue.setLocalTranslation(new Vector3f(1,-1,1));
        Material mat1 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Brown);
        blue.setMaterial(mat1);
 
        // create a red box straight above the blue one at (1,3,1) 
        Box box2 = new Box(1,1,1);
        Geometry red = new Geometry("Box", box2);
        red.setLocalTranslation(new Vector3f(1,3,1));
        Material mat2 = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Red);
        red.setMaterial(mat2);
        
        
        pivot.attachChild(blue);
        pivot.attachChild(red);
 */
        // Create a pivot node at (0,0,0) and attach it to the root node 
        Node pivot = new Node("pivot");
        rootNode.attachChild(pivot); // put this node in the scene
 
        // sky!
        rootNode.attachChild(SkyFactory.createSky(
                assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
        
        // floor
        //PlaneCollisionShape floor = new PlaneCollisionShape(new Plane(new Vector3f(5f,5f,0f), 1.0f));
        
        //Mesh mesh = new Mesh();
        
        floatingNode = new Node("floating");
        rootNode.attachChild(floatingNode);
        
        Ship s = new Ship(floatingNode, "flagship");
        s.setDimentions(10, 8, 25, 7);
        s.build();
        ((Ship) floatingNode.getChild("flagship").getUserData("class")).addFloor();
        
        // Attach
        // Rotate the pivot node: Note that both boxes have rotated! 
        //pivot.rotate(.4f,.4f,0f);
    }
    
    private float time = 0.0f;
    private float waterHeight = 0.0f;
    private float initialWaterHeight = 0f;//0.8f;
    private boolean uw = false;
    //AudioNode waves;
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        time += tpf;
        waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(initialWaterHeight + waterHeight);
        
        // move floating node up/down
        Vector3f v = floatingNode.getLocalTranslation();
        v.y = waterHeight;
        floatingNode.setLocalTranslation(v);
        if (water.isUnderWater() && !uw) {

            //waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
            uw = true;
        }
        if (!water.isUnderWater() && uw) {
            uw = false;
            //waves.setReverbEnabled(false);
            //waves.setDryFilter(new LowPassFilter(1, 1f));
            //waves.setDryFilter(new LowPassFilter(1,1f));

        }
    }
    
    class Ship implements Savable {
    	private Node parentNode, shipNode;
    	
    	private float hullHeight = 7.0f;
    	private float hullWidth = 5.0f;
    	private float hullLength = 20.0f;
    	private float waterDepth = 5.0f;
    	

    	//private AssetManager assetManager;
    	
    	public Ship(Node parentNode, String id) {
    		this.parentNode = parentNode;
    		shipNode = new Node(id);
    		shipNode.setUserData("class", this);
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
    		addMast(hullLength/2f);
    		this.parentNode.attachChild(shipNode);
    	}
    	
    	private void addMast(float lengthFromFront) {
    		Spatial mastNode = shipNode.getChild("mast");
    		if(mastNode != null && mastNode instanceof Node) {
    			System.out.println("Instancing!");
    			mastNode = (Node) mastNode.clone(true);
    			System.out.println(lengthFromFront);
	    		mastNode.setLocalTranslation(lengthFromFront, 0, 0);
    		} else {
    			System.out.println("Creating mast at x="+lengthFromFront);
	    		Cylinder c = new Cylinder(16, 16, 0.5f, hullHeight*2, true);
	    		Geometry mast = new Geometry("mast", c);
	    		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
	    		mat.setColor("Color", ColorRGBA.Brown);
	    		mast.setMaterial(mat);
	    		mast.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(1,0,0) ));
	    		mast.setLocalTranslation(new Vector3f(0, hullHeight, 0));
	    		
	    		Cylinder c2 = new Cylinder(16, 16, 0.1f, hullWidth, true);
	    		Geometry bottom = new Geometry("bottom", c2);
	    		bottom.setMaterial(mat);
	    		bottom.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,1,0) ));
	    		bottom.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,0,1) ));
	    		bottom.setLocalTranslation(new Vector3f(0, hullHeight+hullHeight*1/6, 0));
	    		
	    		Geometry top = new Geometry("top", c2);
	    		top.setMaterial(mat);
	    		top.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,1,0) ));
	    		top.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,0,1) ));
	    		top.setLocalTranslation(new Vector3f(0, hullHeight+hullHeight-hullHeight*1/6, 0));
	    		
	    		Geometry crowsNest = new Geometry("crowsNest", c2);
	    		crowsNest.setMaterial(mat);
	    		crowsNest.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,1,0) ));
	    		crowsNest.setLocalRotation(new Quaternion().fromAngleAxis( FastMath.PI/2 , new Vector3f(0,0,1) ));
	    		crowsNest.setLocalTranslation(new Vector3f(0, hullHeight+hullHeight, 0));
	    		
	    		
	    		
	    		mastNode = new Node("mast");
	    		((Node) mastNode).attachChild(mast);
	    		((Node) mastNode).attachChild(bottom);
	    		((Node) mastNode).attachChild(top);
	    		mastNode.setLocalTranslation(lengthFromFront, 0, 0);
    		}
    		this.shipNode.attachChild(mastNode);
    	}
    	
    	private void addFloor() {
    		//http://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/bullet/TestBrickWall.java
    		
    		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    		Texture t = assetManager.loadTexture("Textures/wood2.jpg");
    		t.setWrap(WrapMode.Repeat);
            mat.setTexture("ColorMap", t);
    		
    		Box floorBox = new Box(Vector3f.ZERO, hullLength/2-hullLength/6, 0.1f, hullWidth/2);
            //floorBox.scaleTextureCoordinates(new Vector2f(3, 6));

            Geometry floor = new Geometry("floor", floorBox);
            floor.setMaterial(mat);
            floor.setLocalTranslation(new Vector3f(hullLength/2,hullHeight-waterDepth+0.1f,0));
            //floor.setShadowMode(ShadowMode.Receive);
            //floor.addControl(new RigidBodyControl(new BoxCollisionShape(new Vector3f(10f, 0.1f, 5f)), 0));
            this.shipNode.attachChild(floor);
            //this.getPhysicsSpace().add(floor);

    	}
    	private void addHull() {
    		// http://jmonkeyengine.googlecode.com/svn/branches/stable-alpha4/engine/src/test/jme3test/model/shape/TestCustomMesh.java
    		
    		
    		
    		Mesh m = new Mesh();
    		
    		// the front hull 2 sides z posetive out. 
    		
    		int verticesNum = 11;
    		// Vertex positions in space
            Vector3f [] vertices = new Vector3f[verticesNum];
            vertices[0] = new Vector3f(0,0,0); // front
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
            
            
            shipNode.attachChild(hull);
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
