package a3;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

/**
 *
 * @author DAN
 */
public class MyBulletController extends Controller 
{
    
    @Override
    public void update(double d) 
    {
        //translate bullets
        for(SceneNode node : controlledNodes)
        {
            if(node instanceof MyBullet)
            {                
                Vector3D nodeVD = ((MyBullet)node).getViewDir();
                double nodeVelocity = ((MyBullet)node).getVelocity();                
                Vector3D curLocAsVector = new Vector3D(((MyBullet)node).getLocation());
                Vector3D newLocAsVector = curLocAsVector.add(nodeVD.mult(nodeVelocity));
                node.translate((float)(newLocAsVector.getX()-((MyBullet)node).getLocation().getX()), 0, (float)(newLocAsVector.getZ()-((MyBullet)node).getLocation().getZ()));                
            }            
        }
    }
    
}
