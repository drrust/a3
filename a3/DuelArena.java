package a3;

import a3.commands.QuitGameAction;
import a3.commands.ShootBullet;
import sage.camera.ICamera;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import java.awt.Choice;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Label;
import sage.app.BaseGame;
import sage.scene.SceneNode;
import sage.scene.HUDString;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
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
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;

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
    private final int X_BULLET_BOUNDS = 150;
    private final int Z_BULLET_BOUNDS = 150;
    
    private float time = 0;
    private float pointTimer;
    private HUDString timeString;
    private HUDString damageDealtString;
    
    private HUDString player1Health,  player1Ammo;
    private int healthP1,  ammoP1;
    
    private ArrayList<SceneNode> removableObjects;
    private IDisplaySystem display;
    private GraphicsDevice device;
    private Controller [ ] cs;
    
    private Controller controller1;    
    private  ICamera camera1;
    private SceneNode player1;    
    private ThirdPersonCameraController cam1Controller;
    
    private double formatedTime;
    private boolean fsemOn;
    
    public DuelArena(boolean fsemOn)
    {
        this.fsemOn = fsemOn;
    }
   /*
    * Override of BaseGame's method for initializing a display system which will be used to set FSEM,
    * an input manager for controlling the controllers, and a list of gameworld objects so that initGame() 
    * can set gameworld objects into the game.
    */
    @Override
    protected void initSystem()
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        
        //output display information
        outputSystemInfo();
        
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
        //timeString = new HUDString("Time = " + time);
        damageDealtString = new HUDString("");
        damageDealtString.setColor(Color.yellow);
        damageDealtString.setLocation(0.40, 0.10);              
        //addGameWorldObject(timeString);
        addGameWorldObject(damageDealtString);        
        
        //set initial transforms of game world objects using scenegraph hierarchy
        //rootNode.updateGeometricState(0, true);
    }
    
    /*
     * @param elapsedTimeMS is used as a measurement of time that the gameloop has elapsed for
     * 
     * Update method is called every time around on the game loop, updates all game world objects.
     */
    @Override
    public void update(float elapsedTimeMS)
    {
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
        
        //update elapsed time on HUD
        time += elapsedTimeMS;
        DecimalFormat df = new DecimalFormat("0.0");
        formatedTime = Double.parseDouble(df.format(time/1000));
        timeString.setText("Time = " + formatedTime);        
        
        //call update to BaseGame
        super.update(elapsedTimeMS);
    }
    
    /*
     * 
     */
    @Override
    protected void render()
    {
        IRenderer renderer = getRenderer();

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
                            //if bullet is colliding with the avatar of a player who didnt shoot the bullet, then set to collided, otherwise, bullet collided with the same avatar that shot the bullet so ignore collision.
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
                 if(xCheck > X_BULLET_BOUNDS || xCheck < -X_BULLET_BOUNDS ||  zCheck > Z_BULLET_BOUNDS || zCheck < -Z_BULLET_BOUNDS)
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
        //create world axis
        Point3D origin = new Point3D(0,0,0);
        Point3D xEnd = new Point3D(100,0,0);
        Point3D yEnd = new Point3D(0,100,0);
        Point3D zEnd = new Point3D(0,0,100);
        Line xAxis = new Line (origin, xEnd, Color.red, 2);
        Line yAxis = new Line (origin, yEnd, Color.green, 2);
        Line zAxis = new Line (origin, zEnd, Color.blue, 2);
        //create floor
        Rectangle floor = new Rectangle(150, 150);
        floor.rotate(90, new Vector3D(1,0,0));
        floor.setColor(Color.gray);
        //add axis and floor to game world       
        addGameWorldObject(floor);
        addGameWorldObject(xAxis);
        addGameWorldObject(yAxis);
        addGameWorldObject(zAxis);
        
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
        cam1Controller = new ThirdPersonCameraController(camera1, player1, im, controller1.getName(), 1.57f);
        
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
    public void outputSystemInfo()
    {
        System.out.println("\nMonitor information:");
        System.out.println("\twidth: " + device.getDisplayMode().getWidth() + "\n\theight " + device.getDisplayMode().getHeight()
                + "\n\tbit depth: " + device.getDisplayMode().getBitDepth() + "\n\trefresh rate: " + device.getDisplayMode().getRefreshRate());
    }

    /*
     *  assigns the first found either keyboard or gamepad to player 1 and then to player 2
     */
    private void setupControllers()
    {
        boolean player1NeedsController = true;
        JFrame pickControllerFrame = new JFrame();
        pickControllerFrame.setLayout(new FlowLayout());
        pickControllerFrame.setSize(300, 150);
        pickControllerFrame.setLocation(500, 500);
        Label label = new Label("Please choose a controller: ");
        pickControllerFrame.add(label);
        Choice choice = new Choice();
        System.out.println("Controller setup:");
        for(int i = 0; i < cs.length; i++)
        {
            if((cs[i].getType().toString().equalsIgnoreCase("Keyboard")) || (cs[i].getType().toString().equalsIgnoreCase("Gamepad")) && player1NeedsController)
            {
                choice.add(cs[i].getType().toString());
                pickControllerFrame.add(choice);
                controller1 = cs[i];
                //player1NeedsController = false;
            }
        }
        pickControllerFrame.add(choice);
        pickControllerFrame.setVisible(true);
        System.out.println("\tPlayer1 has controller: " + controller1.getType());
    }
    
    /*
     * This method creates the avatar and camera for each player
     */
    private void createPlayers()
    {
        //------------------------------------------setup player 1's avatar and camera-------------------------------------------
        player1 = new Pyramid("PyramidP1");
        player1.translate(60, 1, 30);
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
        player1ID.setLocation(0.01, 0.90);
        player1ID.setRenderMode(SceneNode.RENDER_MODE.ORTHO);
        player1ID.setColor(Color.ORANGE );
        player1ID.setCullMode(SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(player1ID);

        healthP1 = 100;
        player1Health = new HUDString("Health: " + healthP1);
        player1Health.setName("Player1Health");
        player1Health.setLocation(0.01, 0.86);
        player1Health.setRenderMode(SceneNode.RENDER_MODE.ORTHO);
        player1Health.setColor(Color.ORANGE );
        player1Health.setCullMode(SceneNode.CULL_MODE.NEVER);
        camera1.addToHUD(player1Health);
        
        ammoP1 = 40;
        player1Ammo = new HUDString("Ammo: " + ammoP1);
        player1Ammo.setName("Player1Ammo");
        player1Ammo.setLocation(0.01, 0.82);
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
}