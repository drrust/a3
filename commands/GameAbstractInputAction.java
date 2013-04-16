package a3.commands;

import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;

/**
 *GameAbstractInputAction is an extension of an AbstractInputAction that will make use of the 
 * InputManager provided by the SAGE game engine. This class will allow the event passed to the 
 * performAction method delegate whether in game commands should be invoked or not based off a
 * dead zone on a joystick world to move backward when the performAction is invoked.
 * 
 * Finished on 3/19/2013
 * @author Daniel Swartz
 */
public class GameAbstractInputAction extends AbstractInputAction
{
    final private float DEAD_ZONE_MIN = 0.45f;
    final private float DEAD_ZONE_MAX = 1;
    private boolean isOutsideDeadZone = false;
    private boolean isPositive = false;
    
    /*
     * empty constructor
     */
    public GameAbstractInputAction()
    {
        
    }

    /*
     * @param event is the object that describes what user input invoked this method
     * @param f 
     * 
     * This method when any kind of input is invoked from the InputManager of a game.
     */
    @Override
    public void performAction(float f, Event event) 
    {
        if(Math.abs(event.getValue()) >= DEAD_ZONE_MIN && Math.abs(event.getValue()) <= DEAD_ZONE_MAX)
        {
            isOutsideDeadZone = true;
        }
        else
        {
            isOutsideDeadZone = false;
        }        
    }
    
    /*
     * @return true if the event that activated this action is outside of the dead zone (used for jotsticks)
     */
    public boolean isOutsideDeadZone()
    {
        return isOutsideDeadZone;
    }
    
}