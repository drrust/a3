package a3.commands;
import a3.DuelArena;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author Daniel Swartz
 */
public class ChooseController extends AbstractAction 
{
    private DuelArena myGame;
    /*
     * @param DuelArena instance that is to be started.
     */
    public ChooseController(DuelArena theGame)
    {
        myGame = theGame;
    }
    
    /*
     * actionPerformed will call the game worlds instance about method.
     * 
     * @param e is the event that drives this command 
     */
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getActionCommand().equals("Keyboard"))
        {
            System.out.println("KEYBOARD PRESSED!");
        }
        //myGame.start();
    }
}