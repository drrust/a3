/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package a3;

import graphicslib3D.Vector3D;
import sage.scene.shape.Pyramid;

/**
 *
 * @author DAN
 */
public class Avatar extends Pyramid
{
    private Vector3D viewDirection;
    public Avatar(String name)
    {
        super.setName(name);
    }
    public void setViewDirection(Vector3D vd)
    {
        viewDirection = vd;
    }
    public Vector3D getViewDirection()
    {
        return viewDirection;
    }
}
