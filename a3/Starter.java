package a3;

import a3.DuelArena;
import a3.commands.ChooseController;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Label;
import javax.swing.JButton;
import javax.swing.JFrame;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
/**
 * This class is the main class used to start the Duel Arena Game for assignment 2 in
 * Csc 165. 
 * 
 * Finished on 3/19/2013
 * @author Daniel Swartz
 */
public class Starter {

    /**
     * @param args the command line arguments
     */
    static private Controller [ ] cs;
    static private DuelArena myGame;
    public static void main(String[] args) 
    {
        //check for full screen arguments
        boolean fsemOn = false;
        if(args.length != 0)
        {            
            String param = args[0].substring(13);
            
            if(param.equalsIgnoreCase("true"))
            {
                fsemOn = true;
            }
        }
        //DuelArena myGame = new DuelArena(false);
        DuelArena myGame = new DuelArena(fsemOn);
        setupControllers();
        //myGame.start();
    }
    
    /*
     *  assigns the first found either keyboard or gamepad to player 1 and then to player 2
     */
    static private void setupControllers()
    {
        ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
        cs = ce.getControllers();
        boolean player1NeedsController = true;
        JFrame pickControllerFrame = new JFrame();
        Label label = new Label("Please choose a controller: ");
        Choice choice = new Choice();
        ChooseController myChoice = new ChooseController(myGame);
        
        pickControllerFrame.setLayout(new FlowLayout());
        pickControllerFrame.setSize(300, 150);
        pickControllerFrame.setLocation(500, 500);
        pickControllerFrame.add(label);
        System.out.println("Controller setup:");
        for(int i = 0; i < cs.length; i++)
        {
            if((cs[i].getType().toString().equalsIgnoreCase("Keyboard")) || (cs[i].getType().toString().equalsIgnoreCase("Gamepad")) && player1NeedsController)
            {
                JButton button = new JButton(cs[i].getType().toString());
                pickControllerFrame.add(button);
                button.setAction(myChoice);
                choice.add(cs[i].getType().toString());
            }
        }
        pickControllerFrame.add(choice);
        //pickControllerFrame
        //pickControllerFrame.setUndecorated(true);
        pickControllerFrame.setVisible(true);
    }
}
