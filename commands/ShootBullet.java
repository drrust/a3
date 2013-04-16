package a3.commands;

import a3.DuelArena;
import a3.MyBulletController;
import a3.ThirdPersonCameraController;
import a3.MyBullet;
import graphicslib3D.Matrix3D;
import net.java.games.input.Event;
import sage.scene.bounding.BoundingSphere;

/**
 *ShootBullet is an extension of an AbstractInputAction that will make use of the 
 * InputManager provided by the SAGE game engine. This class will allow a player to fire a bullet in the
 * game.
 * 
 * Finished on 3/19/2013
 * @author Daniel Swartz
 */
public class ShootBullet extends GameAbstractInputAction
{
    private ThirdPersonCameraController myController;
    private DuelArena theGame;
    /*
     * @controller is the controller owned by the player who will activate this class
     * @theGame is a reference to the game that bullets will be shot in
     */
    public ShootBullet(ThirdPersonCameraController controller, DuelArena theGame)
    {
        myController = controller;
        this.theGame = theGame;
    }

    /*
     * @param event is the object that describes what user input invoked this method
     * @param f 
     * 
     * This method will spawn a bullet, set its location, size, and rotation relative to the controllers view
     * direction. Also attaches a bulelt controller to the bullet and adds bullet to game world.
     * 
     */
    @Override
    public void performAction(float f, Event event) 
    {
        super.performAction(f, event);        
        if(theGame.getPlayer1Ammo() <= 0)
        {
            return;
        }
       if(isOutsideDeadZone())
        {
            MyBullet bullet = new  MyBullet(myController);
            bullet.translate((float)myController.getTargetLocation().getX(), (float)myController.getTargetLocation().getY(), (float)myController.getTargetLocation().getZ());
            bullet.scale(0.1f, 0.3f, 0.1f);
            bullet.rotate(-90, myController.getTargetsCamRightAxis());
            ((BoundingSphere)bullet.getLocalBound()).setRadius(0.6f); //change bounding sphere on bullet
            MyBulletController bc = new MyBulletController();
            bc.addControlledNode(bullet);
            bullet.addController(bc);
            theGame.addObjectToWorld(bullet);
            theGame.decrementAmmo(1);
        }       
    }
    
}
