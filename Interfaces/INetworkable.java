/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package a3.Interfaces;

import java.util.ArrayList;
import sage.scene.Group;
import sage.scene.SceneNode;

public interface INetworkable 
{
    //Creates a SceneNode of the specific type, Example: Square, Bullet
    SceneNode createSceneNode(String type);
    
    //Overrides the games root with a new root. The levelName specifies a specific algorithm or file to be
    //used to generate the new root. For example: Small Level, Big Level. The Items that this
    //Method should add to the new root should be the terrain and skybox. This method is used by the client
    void setUpGameWorld(String levelName);
	
    //This sets the controllable SceneNode of the game to the avitar parameter
    void setAvitar(SceneNode avitar);
    
    //Returns the level name. This relates to the earlier method "setUpGameWorld". 
    //This method is used by the server.
    String getGameWorldLevelName();
    
    //This method returns the games controllable SceneNode (The avitar)
    SceneNode getAvitar();
    
    //This method returns the games root node (Game World)
    Group getRootNode();
	
    //This method returns the type of a SceneNode. Examples: Bullet, Triangle, Square
    String getType(SceneNode node);

    //This method creates a new avitar. This method is used by the server to send the client
    //An avitar it will use.
    SceneNode generateNewAvitar(String name);
	
    //This method is used to retrieve SceneNodes that were added to the GameWorld after the initial init.
    //This method excludes other players. This method is used by the server to send over GameObjects that were
    //Added during the game before the client connect. It is only called once, during the connecting period. Its
    //Purpose is to keep the game synced.
    ArrayList<SceneNode> getObjectsAddedAfterInit();
}