/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
importClass(Packages.java.awt.Color);
importClass(Packages.sage.scene.Group);
importClass(Packages.sage.scene.shape.Pyramid);
importClass(Packages.sage.scene.shape.Line);
importClass(Packages.graphicslib3D.Point3D);
importClass(Packages.sage.camera.ICamera);
importClass(Packages.graphicslib3D.Vector3D);
importClass(Packages.sage.camera.JOGLCamera);
importClass(Packages.sage.scene.HUDString);
//importClass(Packages.a3.ThirdPersonCameraConrtoller);

//create rootnode and axis
var rootNode = new Group("rootNode");
var axisGroup = new Group("Axis");

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
 rootNode.addChild(xAxis);
 rootNode.addChild(yAxis);
 rootNode.addChild(zAxis);
 rootNode.addChild(axisGroup);
        
//---------------------------------------------------player start location and orientation----------------------------------------
 var avatar = new Pyramid(userName);
 avatar.translate(376, 1, 803);
 avatar.rotate(-90, new Vector3D(0, 1, 0));
 
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
      
ammo = 100;
var playerAmmo = new HUDString("Ammo: " + ammo);
playerAmmo.setName("Player1Ammo");
playerAmmo.setLocation(0.01, 0.94);
playerAmmo.setColor(Color.ORANGE );
      
timeString = new HUDString("Time: ");
timeString.setLocation(0, 0.03);
timeString.setColor(Color.white);      

var camController = new ThirdPersonCameraController(camera, player, im, controller.getName(), 1.57f, 20f, 0.3f, 15f, 2f);