/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package a3;

import a3.commands.GameAbstractInputAction;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import sage.camera.ICamera;
import sage.input.IInputManager;
import sage.input.action.IAction;
import sage.scene.SceneNode;

/**
 *
 * @author DAN
 */
public class ThirdPersonCameraController 
{
    private ICamera cam;
    private SceneNode target;
    private float cameraAzimuth;
    private float cameraElevation;
    private float cameraDistanceFromTarget;
    private Point3D targetPos;
    private float targetRotation, speedMultiplier;
    private Matrix3D initialTargetRotation;
    private Matrix3D targetTransform;
    private Vector3D worldUp, targetOffset;
    private String controllerName;    
    private boolean hasYAxisAction = false;
    private boolean hasXAxisAction = false;
    private boolean hasForwardAction = false;
    private boolean hasBackwardAction = false;
    private boolean hasLeftAction = false;
    private boolean hasRightAction = false;
    
    public ThirdPersonCameraController(ICamera cam, SceneNode target, IInputManager inputMgr, String controllerName, float azimuth, float targetRot, float camElev, float camDist, float speedMult)
    {
        this.cam = cam;
        this.target = target;
        initialTargetRotation = target.getLocalRotation();
        speedMultiplier = speedMult;
        cameraAzimuth = azimuth;
        targetRotation = targetRot;
        cameraElevation = camElev;
        cameraDistanceFromTarget = camDist;
        worldUp = new Vector3D(0, 1, 0);
        targetOffset = new Vector3D(0, 0, 0);
        updateTarget();
        updateCameraPosition();
        cam.lookAt(targetPos, worldUp);
        this.controllerName = controllerName;
        setupInput(inputMgr, controllerName);
    }
    
    public void update(float time)
    {
        updateTarget();
        updateCameraPosition();
        cam.lookAt(targetPos, worldUp);
        hasXAxisAction = false;
        hasYAxisAction = false;
        hasForwardAction = false;
        hasBackwardAction = false;
        hasLeftAction = false;
        hasRightAction = false;
    }
    
    private void updateTarget()
    {
        //System.out.println("targetOffset is : " + targetOffset);
        targetPos = new Point3D(target.getWorldTranslation().getCol(3));
        targetPos = new Point3D(new Vector3D(targetPos).add(targetOffset));        
        
        //target.setLocalRotation((Matrix3D)initialTargetRotation.clone());
        //target.rotate(targetRotation, worldUp);
        //target.translate(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        //target.setLocalTranslation(new Vector3D(targetPos.getX(),  targetPos.getY(), targetPos.getZ()));
    }
    
    private void updateCameraPosition()
    {
        double theta = cameraAzimuth;
        
        double phi = cameraElevation;
        
        double r = cameraDistanceFromTarget;
        
        Point3D desiredCameraLoc = convertSphericalToCartesian(theta, phi, r);
        //Point3D targetLoc = new Point3D(target.getWorldTranslation().getCol(3));
       // desiredCameraLoc.add(targetLoc);
//         Point3D targetLoc = new Point3D(target.getWorldTranslation().getCol(3));
//        desiredCameraLoc.setX(target.getLocalTranslation().getCol(3).getX());
//        desiredCameraLoc.setY(target.getLocalTranslation().getCol(3).getY());
//        desiredCameraLoc.setZ(target.getLocalTranslation().getCol(3).getZ());
        cam.setLocation(desiredCameraLoc);
    }
    
    /*
     * 
     */
    private Point3D convertSphericalToCartesian(double theta,double  phi,double r)
    {
        double x, y, z;
        double cosPhi = Math.cos(phi);
        x = r * cosPhi * Math.sin(theta) + target.getWorldTranslation().getCol(3).getX();
        y = r * Math.sin(phi) + target.getWorldTranslation().getCol(3).getY();
        z = r * cosPhi * Math.cos(theta) + target.getWorldTranslation().getCol(3).getZ();
        //System.out.println("in convert spherical to cartesian\nx = " + x + "\ny = " + y + "\nz = " + z);
        return new Point3D(x, y, z);
    }

    /*
     * 
     */
    private void setupInput(IInputManager inputMgr, String controllerName) 
    {
        IAction moveForward = new MoveTargetForward();
        IAction moveBackward = new MoveTargetBackward();
        IAction moveLeft = new MoveTargetLeft();
        IAction moveRight = new MoveTargetRight();
        IAction rotateRight = new RotateTargetRight();
        IAction rotateLeft = new RotateTargetLeft();
        IAction moveXAxis = new MoveTargetXAxis();
        IAction moveYAxis = new MoveTargetYAxis();
        IAction moveRXAxis = new RotateTargetRXAxis();
        inputMgr.associateAction(controllerName, Component.Identifier.Key.W, moveForward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Axis.Y, moveYAxis, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Axis.X, moveXAxis, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Axis.RX, moveRXAxis, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Key.S, moveBackward, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Key.Q, moveLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Key.E, moveRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Key.D, rotateRight, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
        inputMgr.associateAction(controllerName, Component.Identifier.Key.A, rotateLeft, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN );
    }
    
    /*
     * 
     */
    public Point3D getTargetLocation()
    {
        return new Point3D(targetPos.getX(), targetPos.getY(), targetPos.getZ());
    }
    
    /*
     * 
     */
    public Vector3D getTargetsCamViewDir()
    {
        Vector3D viewDir = cam.getViewDirection().normalize();
        return new Vector3D(viewDir.getX(), 0, viewDir.getZ());
    }
    
    /*
     * 
     */
    public Vector3D getTargetsCamRightAxis()
    {
        Vector3D viewDir = cam.getRightAxis().normalize();
        return new Vector3D(viewDir.getX(), 0, viewDir.getZ());
    }
    /*
     * 
     */
    public String getControllerName()
    {
        return controllerName;
    }
    /*
     * 
     */
    public void addOffset()
    {
        //targetOffset = targetOffset.add(offsetVec);
        System.out.println("targetLoc is : " + target.getWorldTranslation().getCol(3));
        System.out.println("camLoc is : " + cam.getLocation());
    }
    
    public SceneNode getTarget()
    {
        return target;
    }
    
    
    //==================================Abstract Actions===============================
    
    /*
     * move camera forward
     */
    private class MoveTargetForward extends GameAbstractInputAction
    {
        /*
         * instantiates the move camera forward input action
         */
        public MoveTargetForward() {}

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will move the camera 0.05 units forward when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {            
            super.performAction(f, event);
            float moveAmount;
            if(hasLeftAction || hasRightAction)
            {
                moveAmount = (f/100) * speedMultiplier;
            }
            else
            {
                moveAmount = (f/50) * speedMultiplier;
            }            
           if(isOutsideDeadZone())
            {
                Vector3D viewDir = cam.getViewDirection().normalize();
                Vector3D curLocAsVector = new Vector3D(cam.getLocation());
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }
           hasForwardAction = true;
        } 
    }
    /*
     * move camera backward
     */
    private class MoveTargetBackward extends GameAbstractInputAction
    {
        /*
         * instantiates the move camera backward input action
         */
        public MoveTargetBackward() {}

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will move the camera 0.05 units backward when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {            
            super.performAction(f, event);
           if(isOutsideDeadZone())
            {
                float moveAmount;
                if(hasLeftAction || hasRightAction)
                {
                     moveAmount = (-f/100) * speedMultiplier;
                 }
                   else
                 {
                      moveAmount = (-f/50) * speedMultiplier;
                 }      
                Vector3D viewDir = cam.getViewDirection().normalize();
                Vector3D curLocAsVector = new Vector3D(cam.getLocation());
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }       
           hasBackwardAction = true;
        } 
    }
    /*
     * move camera left
     */
    private class MoveTargetLeft extends GameAbstractInputAction
    {
        /*
         * instantiates the move camera left input action
         */
        public MoveTargetLeft() {}

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will move the camera 0.05 units left when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {            
            super.performAction(f, event);
           if(isOutsideDeadZone())
            {
                float moveAmount;
                if(hasForwardAction || hasBackwardAction)
                {
                    moveAmount = (-f/100) * speedMultiplier;
                }
                else
                {
                    moveAmount = (-f/50) * speedMultiplier;
                }      
                Vector3D viewDir = cam.getRightAxis().normalize();
                Vector3D curLocAsVector = new Vector3D(cam.getLocation());
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }       
           hasLeftAction = true;
        } 
    }
    /*
     * move camera right
     */
    private class MoveTargetRight extends GameAbstractInputAction
    {
        /*
         * instantiates the move camera right input action
         */
        public MoveTargetRight() {}

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will move the camera 0.05 units right when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {            
            super.performAction(f, event);
            float moveAmount;
            if(hasForwardAction || hasBackwardAction)
            {                
                moveAmount = (f/100) * speedMultiplier;
            }
            else
            {
                moveAmount = (f/50f) * speedMultiplier;
            }            
            //hasMultipleActions = true;
           if(isOutsideDeadZone())
            {
                //moveAmount = 0.25f;
                Vector3D viewDir = cam.getRightAxis().normalize();
                Vector3D curLocAsVector = new Vector3D(cam.getLocation());
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }
           hasRightAction = true;
        } 
    }
    /*
     * rotate camera right
     */
    private class RotateTargetRight extends GameAbstractInputAction
    {
        /*
         * instantiates the rotate camera right input action
         */
        public RotateTargetRight() {}

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will rotate the camera 1 unit to the right when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {            
            super.performAction(f, event);
            float rotateAmount = f/15;
           if(isOutsideDeadZone())
            {
                target.rotate(-rotateAmount, new Vector3D(0,1,0));
                cameraAzimuth -= rotateAmount * 0.01745;                     
            }       
        } 
    }
    /*
     * rotate camera left
     */
    private class RotateTargetLeft extends GameAbstractInputAction
    {
        /*
         * instantiates the rotate camera left input action
         */
        public RotateTargetLeft() {}

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will rotate the camera 1 unit to the right when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {            
            super.performAction(f, event);
            float rotateAmount = f/15;
           if(isOutsideDeadZone())
            {
                target.rotate(rotateAmount, new Vector3D(0,1,0));
                cameraAzimuth += rotateAmount * 0.01745;                     
            }       
        } 
    }
    
    /*
     * 
     */
    public class MoveTargetXAxis extends GameAbstractInputAction
    {
        /*
         * instantiates input action for moving camera left and right with an X axis
         */
        public MoveTargetXAxis(){ }

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will move the camera 0.05 units left or right when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {
            super.performAction(f, event);
            if(!isOutsideDeadZone())
            {
                return;
            }
            float moveAmount;
            if(hasYAxisAction)
            {
                moveAmount = (f/100f) * speedMultiplier;
            }
            else
            {
                moveAmount = (f/50f) * speedMultiplier;
            }
            Vector3D viewDir = cam.getRightAxis().normalize();
            Vector3D curLocAsVector = new Vector3D(cam.getLocation());
            if(event.getValue() < 0)
            {
                moveAmount *= -1;
                //moveAmount = -0.15f;
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }
            else
            {
                //moveAmount = 0.15f;
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }
            hasXAxisAction = true;
        }        
    }
    
    /*
     * 
     */
    public class MoveTargetYAxis extends GameAbstractInputAction
    {
        /*
         * instantiates input action for moving camera forward and backward with an Y axis
         */
        public MoveTargetYAxis(){ }

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will move the camera 0.05 units forward or backward when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {
            super.performAction(f, event);
            if(!isOutsideDeadZone())
            {
                return;
            }

            float moveAmount;
            if(hasXAxisAction)
            {
                moveAmount = (f/100) * speedMultiplier;
            }
            else
            {
                moveAmount = (f/50) * speedMultiplier;
            }
            Vector3D viewDir = cam.getViewDirection().normalize();
            Vector3D curLocAsVector = new Vector3D(cam.getLocation());
            if(event.getValue() < 0)
            {                
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }
            else
            {
                moveAmount *= -1f;
                Vector3D newLocAsVector = curLocAsVector.add(viewDir.mult(moveAmount));
                target.translate((float)(newLocAsVector.getX()-cam.getLocation().getX()), 0, (float)(newLocAsVector.getZ()-cam.getLocation().getZ()));
            }
            hasYAxisAction = true;
        }
    }
    
    private class RotateTargetRXAxis extends GameAbstractInputAction
    {
        /*
         * instantiates the rotate camera right input action
         */
        public RotateTargetRXAxis() {}

        /*
         * @param event is the object that describes what user input invoked this method
         * @param f 
         * 
         * This method will rotate the camera 1 unit to the right when invoked
         */
        @Override
        public void performAction(float f, Event event) 
        {            
            super.performAction(f, event);
            float rotateAmount = f/15;
           if(isOutsideDeadZone())
            {
                if(event.getValue() < 0)
                {
                    target.rotate(rotateAmount, new Vector3D(0,1,0));
                    cameraAzimuth += rotateAmount * 0.01745;     
                }
                else
                {
                    target.rotate(-rotateAmount, new Vector3D(0,1,0));
                    cameraAzimuth -= rotateAmount * 0.01745;              
                }            
            }       
        } 
    }
    
}
