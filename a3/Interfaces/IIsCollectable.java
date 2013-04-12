package a3.Interfaces;

/**
 * This interface should be used for all collectable objects in the game
 * @author DAN
 */
public interface IIsCollectable 
{
    boolean isCollected = false;
    
    public boolean isCollected();
    public void setCollected();
}
