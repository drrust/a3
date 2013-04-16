package a3.commands;

import net.java.games.input.Event;
import sage.app.AbstractGame;
import sage.input.action.AbstractInputAction;

/**
 *QuitGameAction is an extension of an AbstractInputAction that will make use of the 
 * InputManager provided by the SAGE game engine. This class will allow the game to end and close
 * when the performAction is invoked.
 * 
 * Finished on 3/19/2013
 * @author Daniel Swartz
 */
public class QuitGameAction extends AbstractInputAction
{
    private AbstractGame game;
    
    /*
     * @param game is the AbstractGame that is to be exited upon invoking this classe's performAction
     * method.
     */
    public QuitGameAction(AbstractGame game)
    {
        this.game = game;
    }

     /*
     * @param event is the object that describes what user input invoked this method
     * @param f 
     * 
     * This method will quit the game when invoked
     */
    @Override
    public void performAction(float f, Event event)
    {
        game.setGameOver(true);
    }
    
}
