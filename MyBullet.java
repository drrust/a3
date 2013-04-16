package a3;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.scene.shape.Pyramid;
//new comment

/**
 *MyBullet is an extension of the Sage shape object Pyramid, allowing the object to have more
 * customization to it such as a velocity, direction to move in, a flag for whether
 * the object is considered collected or expired.
 * 
 * Finished on 3/19/2013
 * @author Daniel Swartz
 */
public class MyBullet extends Pyramid
{
    
    //attributes for the bullet
    private double velocity;
    private Vector3D moveDirection;
    private Point3D location;
    private String controllerName;
    private boolean isCollided = false;
    private boolean isExpired = false;
    
    /*
     * instantiates a MyBullet object setting a velocity, and controller that created the bullet
     */
    public MyBullet(ThirdPersonCameraController controller)
    {
        moveDirection = new Vector3D(controller.getTargetsCamViewDir().getX(), controller.getTargetsCamViewDir().getY(), controller.getTargetsCamViewDir().getZ());
        velocity = 0.6;
        controllerName = controller.getControllerName();
    }

    
    /*
     * This method should be invoked to set this object having collided with another game world object
     */
    public void setCollided()
    {
        isCollided = true;
    }
    
    /*
     * @return returns true if the object has collided with an object and false if it hasn't
     * 
     * This method should be called to find out whether this object should be discarded from the game world
     */
    public boolean isCollided()
    {
        return isCollided;
    }
    
    /*
     * This method should be invoked to set this object as an expired object
     */
    public void setExpired()
    {
        isExpired = true;
    }
    
    /*
     * @return returns true if the object has reached the end bounds of the map
     * 
     * This method should be called to find out whether this object should be discarded from the game world
     */
    public boolean isExpired()
    {
        return isExpired;
    }

    /*
     * @return the vector of the direction the bullet is to travel
     */
    public Vector3D getViewDir() 
    {
        return new Vector3D(moveDirection.getX(), 0, moveDirection.getZ());
    }
    
    /*
     * @return returns the velocity of the bullet
     */
    public double getVelocity()
    {
        return velocity;
    }
    
    /*
     * @return returns the location of the bullet
     */
    public Point3D getLocation()
    {
        location = new Point3D(getWorldTranslation().getCol(3));
        return new Point3D(location.getX(),location.getY(), location.getZ());
    }

    /*
     * @return returns the name of the controller that the bullet shot from
     */
    public String getBirthedControllerName() 
    {
        return controllerName;
    }

}