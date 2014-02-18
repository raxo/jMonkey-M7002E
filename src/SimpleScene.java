

import jme3tools.optimize.TextureAtlas;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
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
        
        new Ship(pivot).build();
        
        // Attach
        // Rotate the pivot node: Note that both boxes have rotated! 
        //pivot.rotate(.4f,.4f,0f);
    }
    
    class Ship {
    	private Node parentNode, shipNode;
    	private Material mat1;
    	
    	private float hullHeight = 2.0f;
    	private float hullWidth = 3.0f;
    	private float hullLength = 10.0f;
    	private float waterDepth = 1.0f;
    	
    	public Ship(Node parentNode) {
			this.parentNode = parentNode;
			shipNode = new Node();
		} 
    	private void build() {
    		//addFloor();
    		addHull();
    		this.parentNode.attachChild(shipNode);
    	}
    	private void addFloor() {
    		//http://code.google.com/p/jmonkeyengine/source/browse/trunk/engine/src/test/jme3test/bullet/TestBrickWall.java
    		
    		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    		Texture t = assetManager.loadTexture("Textures/wood.jpg");
    		t.setWrap(WrapMode.Repeat);
            mat.setTexture("ColorMap", t);
    		
    		Box floorBox = new Box(Vector3f.ZERO, hullLength, 0.1f, hullWidth);
            //floorBox.scaleTextureCoordinates(new Vector2f(3, 6));

            Geometry floor = new Geometry("floor", floorBox);
            floor.setMaterial(mat);
            floor.setLocalTranslation(new Vector3f(0,0,hullHeight-waterDepth));
            //floor.setShadowMode(ShadowMode.Receive);
            floor.setLocalTranslation(0, -0.1f, 0);
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
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0,
        		0,1,0
            };

            // Setting buffers
            m.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
            m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
            m.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexesLeft));
            m.updateBound();
            
            // Creating a geometry, and apply a single color material to it
            Geometry hull = new Geometry("OurMesh", m);
            hull.setLocalTranslation(new Vector3f(0,0,-waterDepth));
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            //mat.setColor("Color", ColorRGBA.Brown);
            
            
            Texture t = assetManager.loadTexture("Textures/wood.jpg");
    		//t.setWrap(WrapMode.Repeat);
    		t.setWrap(WrapAxis.T, WrapMode.Repeat);
            mat.setTexture("ColorMap", t);
            
            hull.setMaterial(mat);

            // Attaching our geometry to the root node.
            shipNode.attachChild(hull);
    	}
    }
}
