/*
 * This js file is used to create the sceneGraph for a3. This includes terrain, skybox, avatar and axis.
 */
importClass(Packages.java.io.File);
importClass(Packages.java.awt.Color);
importClass(Packages.graphicslib3D.Point3D);
importClass(Packages.graphicslib3D.Vector3D);
importClass(Packages.sage.camera.ICamera);
importClass(Packages.sage.scene.Group);
importClass(Packages.sage.scene.shape.Pyramid);
importClass(Packages.sage.scene.shape.Line);
importClass(Packages.sage.camera.JOGLCamera);
importClass(Packages.sage.scene.HUDString);
importClass(Packages.sage.texture.Texture);
importClass(Packages.sage.texture.TextureManager);
importClass(Packages.sage.scene.SkyBox);
importClass(Packages.sage.terrain.AbstractHeightMap);
importClass(Packages.sage.terrain.ImageBasedHeightMap);
importClass(Packages.sage.terrain.TerrainBlock);
importClass(Packages.sage.scene.state.RenderState);
importClass(Packages.sage.scene.state.TextureState);
importClass(Packages.a3.Avatar);

//create rootnode and axis
var rootNode = new Group("rootNode");
var axisGroup = new Group("Axis");
//--------------------------------------------------------create skybox----------------------------------------------------------------
var theSkyBox = new SkyBox("world", 30, 30, 30);
var texNorth = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbFront.bmp");
var texEast = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbRight.bmp");
var texSouth = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbBack.bmp");
var texWest = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbLeft.bmp");
var texUp = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbTop.bmp");
var texDown = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbBot.bmp");
        
theSkyBox.setTexture(faceNorth, texNorth);
theSkyBox.setTexture(faceEast, texEast);
theSkyBox.setTexture(faceSouth, texSouth);
theSkyBox.setTexture(faceWest, texWest);
theSkyBox.setTexture(faceUp, texUp);
theSkyBox.setTexture(faceDown, texDown);
rootNode.addChild(theSkyBox);
//---------------------------------------------------------create world axis----------------------------------------------------------
var origin = new Point3D(0,0,0);
var xEnd = new Point3D(1000,0,0);
var yEnd = new Point3D(0,1000,0);
 var zEnd = new Point3D(0,0,1000);
 var xAxis = new Line (origin, xEnd, Color.red, 2);
 var yAxis = new Line (origin, yEnd, Color.green, 2);
 var zAxis = new Line (origin, zEnd, Color.blue, 2);
 axisGroup.addChild(xAxis);
 axisGroup.addChild(yAxis);
 axisGroup.addChild(zAxis);
 rootNode.addChild(axisGroup);
        
//----------------------------------------------------------creat terrain----------------------------------------------------------------  
var heightMap = new ImageBasedHeightMap("." + File.separator + "terrain" +  File.separator + "HeightMap1.jpg");
var terrainScale = new Vector3D(10, 1, 10);
var terrainSize = heightMap.getSize();
var cornerHeight = heightMap.getTrueHeightAtPoint(0,0);
var terrainOrigin = new Point3D(0, -cornerHeight, 0);
var name = "Terrain: " + heightMap.getClass().getSimpleName();
imageTerrain =  new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
rootNode.addChild(imageTerrain);
var texState = (TextureState)(renderer.createRenderState(RenderState.RenderStateType.Texture));
var terrainTex = TextureManager.loadTexture2D("." + File.separator + "terrain" + File.separator + "terraintex1.png");
 texState.setTexture(terrainTex);
imageTerrain.setRenderState(texState);
//---------------------------------------------------player start location and orientation----------------------------------------
 var avatar = new Avatar(userName);
 avatar.translate(376, 1, 803);
 avatar.rotate(-90, new Vector3D(0, 1, 0));
 rootNode.addChild(avatar);
 
 //setup camera frustum and viewport
 var camera1 = new JOGLCamera(display.getRenderer());
 camera1.setPerspectiveFrustum(60, 1, 1, 1200);
 camera1.setViewport(0.0, 1.0, 0.0, 1.0);
 
 //------------------------------------------------------players HUD------------------------------------------------------------------
 var playerID = new HUDString(userName);
playerID.setName(userName);
playerID.setLocation(0.01, 0.98);
playerID.setColor(Color.ORANGE );

var health = 1000;
playerHealth = new HUDString("Health: " + health);
playerHealth.setName("Player1Health");
playerHealth.setLocation(0.01, 0.96);
playerHealth.setColor(Color.ORANGE );
        
var playerLoc = new HUDString("Player Location: ");
playerLoc.setName("Player1Loc");
playerLoc.setLocation(0, 0.01);
playerLoc.setColor(Color.ORANGE );
      
var ammo = 9900;
var playerAmmo = new HUDString("Ammo: " + ammo);
playerAmmo.setName("Player1Ammo");
playerAmmo.setLocation(0.01, 0.94);
playerAmmo.setColor(Color.ORANGE );
      
timeString = new HUDString("Time: ");
timeString.setLocation(0, 0.03);
timeString.setColor(Color.white);      

//------------------------------------------------------steup third person controller----------------------------------------------
//var camController = new ThirdPersonCameraController(camera, player, im, controller.getName(), 1.57, 20, 0.3, 15, 2);