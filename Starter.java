package a3;

import a3.DuelArena;
/**
 * This class is the main class used to start the Duel Arena Game for assignment 2 in
 * Csc 165. 
 * 
 * Finished on 3/19/2013
 * @author Daniel Swartz
 */
public class Starter {

    //hello
    /**
     * @param args the command line arguments
     */
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
        DuelArena myGame = new DuelArena(fsemOn);
        myGame.start();
    }   
}
