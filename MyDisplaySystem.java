/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package a3;

import java.awt.Canvas;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.renderer.RendererFactory;

/**
 *
 * @author DAN
 */
public class MyDisplaySystem implements IDisplaySystem
{
    private JFrame myFrame;
    private GraphicsDevice device;
    private IRenderer myRenderer;
    private int width, height, bitDepth, refreshRate;
    private Canvas rendererCanvas;
    private boolean isCreated, isFullScreen;
    
    public MyDisplaySystem(int w, int h, int depth, int rate, boolean isFullScreen, String rendererClassName)    
    {
        width = w;
        height = h;
        bitDepth = depth;
        refreshRate = rate;
        this.isFullScreen = isFullScreen;
        myRenderer = RendererFactory.createRenderer(rendererClassName);
        if(myRenderer == null)
        {
            throw new RuntimeException("Unable to find renderer '" + rendererClassName + "'");
        }
        rendererCanvas = myRenderer.getCanvas();
        myFrame = new JFrame("Default Title");
        myFrame.add(rendererCanvas);
        //myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        DisplayMode displayMode = new DisplayMode(width, height, bitDepth, refreshRate);
        initScreen(displayMode, isFullScreen);
        
        DisplaySystem.setCurrentDisplaySystem(this);
        myFrame.setVisible(true);
        //myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        isCreated = true;        
    }
    
    private void initScreen(DisplayMode dispMode, boolean fullScreenRequested)
    {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();
        
        if(device.isFullScreenSupported() && fullScreenRequested)
        {
            //takes away title bar, close, minimize, maximize, and border
            myFrame.setUndecorated(true);
            //makes the canvas size unchangeable
            myFrame.setResizable(false);
            //ignore AWT repaint
            myFrame.setIgnoreRepaint(true);            
            
            device.setFullScreenWindow(myFrame);            
            if(dispMode != null && device.isDisplayChangeSupported())
            {
                try
                {
                    device.setDisplayMode(dispMode);
                    //myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
                    myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());                                        
                }
                catch(Exception e)
                {
                    System.err.println("Exception while setting device DisplayMode: " + e);
                }
            }
            else
            {
                System.err.println("Cannot set display mode");
            }
            //myFrame.setLocationRelativeTo(null);
        }
        else
        {
            //must use windowed mode
            myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
            //centers window to middle of screen
            myFrame.setLocationRelativeTo(null);
        }
    }

    @Override
    public int getWidth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getHeight() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getBitDepth() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRefreshRate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setWidth(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setHeight(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBitDepth(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRefreshRate(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTitle(String string) 
    {
        myFrame.setTitle(string);
    }

    @Override
    public IRenderer getRenderer() 
    {
        return myRenderer;
    }

    @Override
    /*
     * closes full screen window
     */
    public void close()
    {
        if(device != null)
        {
            Window window = device.getFullScreenWindow();
            if(window != null)
            {
                window.dispose();
            }
            device.setFullScreenWindow(null);
        }
    }

    @Override
    public boolean isCreated() 
    {
        return isCreated;
    }

    @Override
    public boolean isFullScreen() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addKeyListener(KeyListener kl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addMouseListener(MouseListener ml) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addMouseMotionListener(MouseMotionListener ml) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isShowing() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void convertPointToScreen(Point point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPredefinedCursor(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCustomCursor(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
