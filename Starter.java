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
    static private NetworkSetup nw;
    static String ip, port, userName;
    //hello
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        nw = new NetworkSetup();
        nw.setVisible(true);
        setupNetwork();
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
        DuelArena myGame = new DuelArena(fsemOn, userName, ip, port);
        myGame.start();
    }   
    /*
      * use a "sleep thread"  to wait for the network setup to be picked from 
     * the network window
      */
    static private void setupNetwork()
    {
        while(!nw.buttonPressed())
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
        userName = nw.getUserName();
        port = nw.getIP();
        ip = nw.getPort();
        nw.setVisible(false);
    }
}
