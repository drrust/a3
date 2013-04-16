package a3;

import a3.commands.QuitGameAction;
import a3.commands.ShootBullet;
import graphicslib3D.Matrix3D;
import sage.camera.ICamera;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import sage.app.BaseGame;
import sage.scene.SceneNode;
import sage.scene.HUDString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.input.IInputManager;
import sage.input.IInputManager.INPUT_ACTION_TYPE;
import sage.input.InputManager;
import sage.input.action.IAction;
import sage.renderer.IRenderer;
import sage.renderer.jogl.JOGLContextCapabilities;
import sage.scene.Group;
import sage.scene.SkyBox;
import sage.scene.SkyBox.Face;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.scene.state.jogl.JOGLTextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.ImageBasedHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.TextureManager;

/**
 * Duel Arena is a game made for experimental purposes for a Game Architecture and
 * Implementation course, helps give experience working with a pre-made game engine, 
 * implement a third person camera, set display settings, provide two player split screen
 * and use a controller class for a collection of game objects.
 * 
 * Finished on 3/19/2013
 * @author Daniel Swartz
 */
public class DuelArena extends BaseGame
{
    protected Group rootNode;
    
    private final int X_BULLET_BOUNDS = 1250;
    private final int Z_BULLET_BOUNDS = 1200;
    
    private float time = 0;
    private float pointTimer;
    private HUDString timeString;
    private HUDString damageDealtString;
    
    private TerrainBlock imageTerrain;
    
    private HUDString player1Health,  player1Ammo, player1Loc;
    private int healthP1,  ammoP1;
    
    private ArrayList<SceneNode> removableObjects;
    private IDisplaySystem display;
    private GraphicsDevice device;
    private Controller [ ] cs;
    
    private Controller controller1;    
    private  ICamera camera1;
    private SceneNode player1;    
    private ThirdPersonCameraController cam1Controller;
    private InitDialog myInitDiag;
    private SkyBox theSkyBox;
    
    private double formatedTime;
    private boolean fsemOn, controllerNotPicked = true;
    
    public DuelArena(boolean fsemOn)
    {
        this.fsemOn = fsemOn;
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        myInitDiag = new InitDialog(getSystemInfo());        
    }
   /*
    * Override of BaseGame's method for initializing a display system which will be used to set FSEM,
    * an input manager for controlling the controllers, and a list of gameworld objects so that initGame() 
    * can set gameworld objects into the game.
    */
    @Override
    protected void initSystem()
    {
        
        //output display information
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
        cs = ce.getControllers();
        myInitDiag.setVisible(true);
        setupController();         
        
        //create IDisplaySystem
        display = createDisplaySystem();
        setDisplaySystem(display);                
        
        IInputManager inputManager = new InputManager();
        setInputManager(inputManager);
        
        ArrayList<SceneNode> gameWorld = new ArrayList();
        setGameWorld(gameWorld);
    }
    
    /*
     * This initGame method is an override of the BaseGame class used in the SAGE game engine,
     * used to initiate all the game world objects, setup the camera and setup all ingame commands to
     * function.
     */
    @Override
    public void initGame()
    {
        //instantiate the display system and set title to game name
        display = getDisplaySystem();
        display.setTitle("Duel Arena");
        
        //initialize objects in world
        createScene();
               
        //create player avatars, HUDS, and cameras
        createPlayers();
        
        //initialize the game controls for keyboard and gamepad
        initInput();
        
        //add game state stings to HUD      
        damageDealtString = new HUDString("");
        damageDealtString.setColor(Color.yellow);
        damageDealtString.setLocation(0.40, 0.10);              
        addGameWorldObject(damageDealtString);        
        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderStates();
        
    }
    
    /*
     * @param elapsedTimeMS is used as a measurement of time that the gameloop has elapsed for
     * 
     * Update method is called every time around on the game loop, updates all game world objects.
     */
    @Override
    public void update(float elapsedTimeMS)
    {
        //update skybox location
        Point3D camLoc = camera1.getLocation();
        Matrix3D camTranslation = new Matrix3D();
        camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
        theSkyBox.setLocalTranslation(camTranslation);
        
        //update avatars location relative to terrain
        updateVerticalPosition();
        
        checkIfGameOver();
        //update camare controller for the player
        cam1Controller.update(elapsedTimeMS);
        //check for collisions for bullets and extra ammo and apply appropriate updates when event occurs
        checkForCollisions();
        checkForExpiredBullets();
        removeObjects();
        pointDisplayTimer();
        //update the current health and ammo on the HUD
        player1Health.setText("Health: " + healthP1);
        player1Ammo.setText("Ammo: " + ammoP1);        
         DecimalFormat df = new DecimalFormat("0");
        player1Loc.setText("Player Location: " + df.format(getPlayer1Location().getX()) + ", " + 
                                                                df.format(getPlayer1Location().getY()) +  ", " + df.format(getPlayer1Location().getZ()));
        
        //update elapsed time on HUD
        time += elapsedTimeMS;
        DecimalFormat dfTime = new DecimalFormat("0.0");
        formatedTime = Double.parseDouble(dfTime.format(time/1000));
        timeString.setText("Time = " + formatedTime);        
        
        //call update to BaseGame
        super.update(elapsedTimeMS);
    }
    
     /*
     * renders the scenegraph from the perspective of the given camera
     */
    @Override
    protected void render()
    {
        IRenderer renderer = getRenderer();
        renderer.clearRenderQueue();
        renderer.addToRenderQueue(rootNode);
        renderer.processRenderQueue();
        renderer.setCamera(camera1);
        super.render();
    }
    
    /*
     * This method is used to remove collected treasure objects from the game world collection.
     */
    public void removeObjects()
    {
        //adds collected objects from the game world into the removable objects array list
        for ( SceneNode s : getGameWorld())
        {
            if(s instanceof MyBullet)
            {
                if(((MyBullet)s).isCollided())
                {                    
                    removableObjects.add(s);
                }
                if(((MyBullet)s).isExpired())
                {                    
                    removableObjects.add(s);
                }
            }
        }        
        //removes removable game world objects from the game and the removable object array list
        for(int i = 0; i < removableObjects.size(); i++)
        {
            removeGameWorldObject(removableObjects.get(i));
            removableObjects.remove(i);
        }
    }
    
    /*
     * This method checks to see if the camera has collided with any treasure objects and sets the 
     * appropriate flags on those objects.
     */
    public void checkForCollisions()
    {        
        for ( SceneNode s : getGameWorld())
        {            
            if(s instanceof MyBullet)
            {
                if(s.getWorldBound() != null)
                {
                    if(s.getWorldBound().contains( ((ThirdPersonCameraController)cam1Controller).getTargetLocation()))
                    {
                        if(!( ((MyBullet)s).getBirthedControllerName().equals(cam1Controller.getControllerName()) ) )
                        {
                            //if bullet is colliding with the avatar of a player who didnt shoot the bullet, then set to 
                            //collided, otherwise, bullet collided with the same avatar that shot the bullet so ignore collision.
                            ((MyBullet)s).setCollided();
                            healthP1 -= 5;
                        }
                    }             
                }
            }
        }
    }
    
    /*
     * This method checks to see if any bullets have reached the edge of the world bounds and if a bullet 
     * has reached the edge, sets the bullets flag of expire so that it can be removed from the game world
     */
    public void checkForExpiredBullets()
    {        
        for ( SceneNode s : getGameWorld())
        {            
            if(s instanceof MyBullet)
            {
                double xCheck = ((MyBullet)s).getLocation().getX();
                double zCheck = ((MyBullet)s).getLocation().getZ();
                 if(xCheck > X_BULLET_BOUNDS || xCheck < 0 ||  zCheck > Z_BULLET_BOUNDS || zCheck < 0)
                 {
                     ((MyBullet)s).setExpired();
                 }
             }
         }
    }
    
    /*
     * This method creates all the game objects when the game is initially started
     */
    public void createScene()
    {
        rootNode = new Group("root node");
        //add skybox
        createSkyBox();
        //add terrain
        createTerrain();
        //create world axis        
        Point3D origin = new Point3D(0,0,0);
        Point3D xEnd = new Point3D(100,0,0);
        Point3D yEnd = new Point3D(0,100,0);
        Point3D zEnd = new Point3D(0,0,100);
        Line xAxis = new Line (origin, xEnd, Color.red, 2);
        rootNode.addChild(xAxis);
        Line yAxis = new Line (origin, yEnd, Color.green, 2);
        rootNode.addChild(yAxis);
        Line zAxis = new Line (origin, zEnd, Color.blue, 2);
        rootNode.addChild(zAxis);
        //add axis and floor to game world       
        addGameWorldObject(rootNode);
        //        addGameWorldObject(floor);
//        addGameWorldObject(xAxis);
//        addGameWorldObject(yAxis);
//        addGameWorldObject(zAxis);
        
        //instantiate an array list used to collected removable objects
        removableObjects = new ArrayList();
    }
    
    /*
     * 
     */
    private IDisplaySystem createDisplaySystem()
    {
        //uses the default screen devices width, height, bit depth, refresh rate to set up a display system
        display = new MyDisplaySystem(device.getDisplayMode().getWidth(), 
                device.getDisplayMode().getHeight(), device.getDisplayMode().getBitDepth(), 
                device.getDisplayMode().getRefreshRate(), fsemOn, "sage.renderer.jogl.JOGLRenderer");
        System.out.println("\nWaiting for display creation...");
        int count = 0;
        
        while(!display.isCreated())
        {
            try
            {
                Thread.sleep(10);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException("Display creation interrupted");
            }
            count++;
            System.out.println("+");
            if(count % 80 == 0)
            {
                System.out.println();
            }
            if(count > 2000)
            {
                throw new RuntimeException("Unable to create display");               
            }            
        }
        System.out.println();
         return display;
    }
    
     /*
     * This method will close the display when the program is shutdown
     */
    @Override
    protected void shutdown()
    {
        display.close();
    }
    
    /*
     * This method will turn off the dispaly for the points earned in half a second
     */
    public void pointDisplayTimer()
    {
        if(500 < time - pointTimer)
        {
            damageDealtString.setText("");
        }
    }
    /*
     * This method initiallizes the input mechanisms to moving the camera, toggling the axis and setting the camera
     */
    public void initInput()
    {
        //add input manager
        IInputManager im = getInputManager();
        cam1Controller = new ThirdPersonCameraController(camera1, player1, im, controller1.getName(), 1.57f, 20f, 0.3f, 15f, 2f);
        
        IAction shootBullet1 = new ShootBullet(cam1Controller, this);
        im.associateAction(controller1.getName(), Component.Identifier.Key.C, shootBullet1, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY );
        im.associateAction(controller1.getName(), Component.Identifier.Button._1, shootBullet1, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY );

        IAction quitGame = new QuitGameAction(this);            
        im.associateAction(controller1.getName(), Component.Identifier.Key.ESCAPE, quitGame, INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE );
        im.associateAction(controller1.getName(), Component.Identifier.Button._7, quitGame, INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE );        
    }
    
    /*
     * This method outputs information specific to th computer that this application is running on such as,
     * resolution/bid-depth/refresh-rate of monitor and the available controllers connected to the computer.
     */
    public String [] getSystemInfo()
    {
        String [] out = {"Monitor information:", "Width: " + device.getDisplayMode().getWidth(), 
            "Height: " + device.getDisplayMode().getHeight(), "  Bit Depth:  " + device.getDisplayMode().getBitDepth(),
            "Refresh Rate: " + device.getDisplayMode().getRefreshRate() };
        return out;
    }
    
    /*
     * This method creates the avatar and camera for each player
     */
    private void createPlayers()
    {
        //------------------------------------------setup player 1's avatar and camera-------------------------------------------
        player1 = new Pyramid("PyramidP1");
        player1.translate(376, 1, 803);
        player1.rotate(-90, new Vector3D(0, 1, 0));
        addGameWorldObject(player1);
        camera1 = new JOGLCamera(display.getRenderer());
        camera1.setPerspectiveFrustum(60, 1, 1, 1000);
        camera1.setViewport(0.0, 1.0, 0.0, 1.0);
        createPlayerHUDs();
    }
    
    /*
     * 
     */
    private void createPlayerHUDs()
    {
        //---------------------------------------------------player's HUD strings---------------------------------------------------
        HUDString player1ID = new HUDString("Player1");
        player1ID.setName("Player1ID");
        player1ID.setLocation(0.01, 0.98);
        player1ID.setRenderMode(SceneNode.RENDER_MODE.ORTHO);
        player1ID.setColor(Color.ORANGE );
        player1ID.setCullMode(SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(player1ID);

        healthP1 = 100;
        player1Health = new HUDString("Health: " + healthP1);
        player1Health.setName("Player1Health");
        player1Health.setLocation(0.01, 0.96);
        player1Health.setRenderMode(SceneNode.RENDER_MODE.ORTHO);
        player1Health.setColor(Color.ORANGE );
        player1Health.setCullMode(SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(player1Health);
        
        player1Loc = new HUDString("Player Location: ");
        player1Loc.setName("Player1Loc");
        player1Loc.setLocation(0.01, 0.04);
        player1Loc.setRenderMode(SceneNode.RENDER_MODE.ORTHO);
        player1Loc.setColor(Color.ORANGE );
        player1Loc.setCullMode(SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(player1Loc);
        
        ammoP1 = 100;
        player1Ammo = new HUDString("Ammo: " + ammoP1);
        player1Ammo.setName("Player1Ammo");
        player1Ammo.setLocation(0.01, 0.94);
        player1Ammo.setRenderMode(SceneNode.RENDER_MODE.ORTHO);
        player1Ammo.setColor(Color.ORANGE );
        player1Ammo.setCullMode(SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(player1Ammo);
        
        timeString = new HUDString("Time: " + formatedTime);
        timeString.setLocation(0.75, 1.1); //default location is (0,0), goes from (0,0) lower left to (1,1) top right.
        timeString.setColor(Color.white);        
        camera1.addToHUD(timeString);
    }
    
    /*
     * This method is intended to be invoked by input actions that are invoked by user input.
     */
    public void addObjectToWorld(SceneNode s)
    {
        addGameWorldObject(s);
    }
    
    /*     
     * @param controller is the name of the players controller that the ammo decrement corresponds to
     * @param ammount is the amount of ammo to decrement to the player
     */
    public void decrementAmmo(int amount)
    {
        ammoP1 -= amount;
    }
    
    /*
     * @param controller is the name of the players controller that the ammo increment corresponds to
     * @param ammount is the amount of ammo to increment to the player
     */
    public void incrementAmmo(int amount)
    {
        ammoP1 += amount;
    }
    
    /*
     * returns the string for player 1's controller name
     */
    public String getPlayer1ControllerName()
    {
        return cam1Controller.getControllerName();
    }
    
    /*
     * @return returns the integer value representing how much ammo player 1 currently has
     */
    public int getPlayer1Ammo()
    {
        return ammoP1;
    }

    public Point3D getPlayer1Location()
    {
        return new Point3D(cam1Controller.getTarget().getLocalTranslation().getCol(3));
    }
    /*
     * checks if the game is over by seeing if the player has lost all health
     */
    private void checkIfGameOver() 
    {
        if(healthP1 <= 0)
        {
            System.out.println("\n\tGame Over\tPlayer 2 wins!");
            //setGameOver(true);
        }
    }

     /*
      * use a "sleep thread"  to wait for the controller to be picked from the startup window
      */
    private void setupController()
    {
        while(controllerNotPicked)
        {
            try
            {
                Thread.sleep(100);
            }
            catch(InterruptedException e)
            {
                throw new RuntimeException("Display creation interrupted");
            }    
        }
        setDisplaySystem(display);
    }
    /*
     * this method creates a skybox using the sage library. Imports six images to create a cube with the 
     * six images on each face of the cube.
     */
    private void createSkyBox()
    {
        theSkyBox = new SkyBox("world", 30.0f, 30.0f, 30.0f);
        Texture texNorth = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbFront.bmp");
        Texture texEast = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbRight.bmp");
        Texture texSouth = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbBack.bmp");
        Texture texWest = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbLeft.bmp");
        Texture texUp = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbTop.bmp");
        Texture texDown = TextureManager.loadTexture2D("." + File.separator + "textures" + File.separator + "sbBot.bmp");
        
        theSkyBox.setTexture(Face.North, texNorth);
        theSkyBox.setTexture(Face.East, texEast);
        theSkyBox.setTexture(Face.South, texSouth);
        theSkyBox.setTexture(Face.West, texWest);
        theSkyBox.setTexture(Face.Up, texUp);
        theSkyBox.setTexture(Face.Down, texDown);
        rootNode.addChild(theSkyBox);
    }

    /*
     * call this method to create the terrain, currently implements an image based height map that will be used
     * for creating the terrain block.
     */
    private void createTerrain()
    {
        ImageBasedHeightMap heightMap = new ImageBasedHeightMap("." + File.separator + "terrain" +  File.separator + "HeightMap1.jpg");
        imageTerrain = createTerrainBlock(heightMap);
        rootNode.addChild(imageTerrain);
        TextureState terrainTexState = createTextureState("terraintex1.png ");
        imageTerrain.setRenderState(terrainTexState);
    }
    
    /*
     * @return returns a TerrainBlock given an abstract height map
     * @param pass an already instantiated height map to be used for building a terrain block
     */
    private TerrainBlock createTerrainBlock(AbstractHeightMap heightMap)
    {
        Vector3D terrainScale = new Vector3D(10, 1, 10);
        int terrainSize = heightMap.getSize();
        System.out.println("height map size: " + terrainSize);
        float cornerHeight = heightMap.getTrueHeightAtPoint(0,0);
        Point3D terrainOrigin = new Point3D(0, -cornerHeight, 0);
        String name = "Terrain: " + heightMap.getClass().getSimpleName();
        return new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
    }
    
    /*
     * updates the avatar's y location for the player based off of its terrain location
     */
    private void updateVerticalPosition()
    {
        Point3D curTargetLoc = getPlayer1Location();
        if(targetLiesInsideTerrain(imageTerrain, curTargetLoc))
        {
            float x = (float) curTargetLoc.getX();
            float z = (float) curTargetLoc.getZ();
            float heightRelativeToTerrainOrigin = imageTerrain.getHeight(x,z);
            double desiredHeight = heightRelativeToTerrainOrigin + imageTerrain.getOrigin().getY()+1;
            cam1Controller.getTarget().getLocalTranslation().setElementAt(1, 3, desiredHeight+1);
        }
        else{//target is outside of terrain, so dont change y value
        }
    }
    
    /*
     * @return returns true if the avatar is within the terrain bounds or false if outside.
     * @param tb is the terrain block that is to be checked whether the avatar is inside the terrain
     * @param loc is the location of the avatar in the game world
     */
    private boolean targetLiesInsideTerrain(TerrainBlock tb, Point3D loc)
    {
        float limit = tb.getSize()-1;
        Vector3D scaleFactor = tb.getScaleFactor();
        double xLimit = limit * scaleFactor.getX();
        double zLimit = limit * scaleFactor.getZ();
        double xLoc = loc.getX();
        double yLoc = loc.getY();
        double zLoc = loc.getZ();
        return ((xLoc >= 0 ) && (xLoc <= xLimit) && (zLoc >= 0) && (zLoc <= zLimit));
            
    }

    /*
     * creates a new texture state using the texture name passed to the method
     * @return TextureState, a texture state that will be used for the terrain
     * @param is the name of the file to use as a texture for this tecture state
     */
    private TextureState createTextureState(String fileName)
    {                
        TextureState texState = (TextureState)this.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
        Texture terrainTex = TextureManager.loadTexture2D("." + File.separator + "terrain" + File.separator + fileName);
        texState.setTexture(terrainTex);
        return texState;
    }
    
     /*
     * This Jframe will display when the game is started and will ask the user what controller they would 
     * like to use. Supports only keyboards and gamepads.
     */
    public class InitDialog extends JFrame implements ActionListener
    {
        private ArrayList button = new ArrayList();
        private ArrayList controllers = new ArrayList();
        public InitDialog(String [] info)
        {
            ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();            
            Controller [ ] cs = ce.getControllers();
            JPanel buttonPan = new JPanel();
            JLabel sysInfo = new JLabel(info[0], JLabel.CENTER);
            add(sysInfo);
            sysInfo = new JLabel("<html><p>" + info[1] + "<br>" + info[2] +  "</p></html>", JLabel.CENTER);
            add(sysInfo);
            sysInfo = new JLabel("<html>&nbsp;&nbsp;&nbsp;" + info[3]+ "<br>" + info[4] + "</html>", JLabel.CENTER);
            add(sysInfo);
            sysInfo = new JLabel("===============================", JLabel.CENTER);
            add(sysInfo);
            sysInfo = new JLabel("Please choose a controller:", JLabel.CENTER);
            add(sysInfo);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(8,1));
            setSize(275, 250);
            setLocation(500, 300);
            setTitle("GAME SETUP");
            int numOfControllers = 0;
            for(int i = 0; i < cs.length; i++)
            {
                if((cs[i].getType().toString().equalsIgnoreCase("Keyboard")) || (cs[i].getType().toString().equalsIgnoreCase("Gamepad")))
                {
                    numOfControllers += 1;
                    
                    controllers.add(cs[i]);
                    button.add(new JButton(cs[i].getType().toString()));
                    buttonPan.add((JButton)button.get(numOfControllers - 1));
                    ((JButton)button.get(numOfControllers - 1)).setActionCommand(cs[i].getType().toString());
                    ((JButton)button.get(numOfControllers - 1)).addActionListener(this);
                }
            }
            setSize(numOfControllers * 125, 300);
            add(buttonPan);            
        }
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            for(int i = 0; i < button.size(); i++)
            {
                if(e.getSource().equals(((JButton)button.get(i))))
                {
                    controllerNotPicked = false;
                    controller1 = (((Controller)controllers.get(i)));
                    setVisible(false);
                }
            }
        }        
    }
}