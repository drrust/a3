/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package a3;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author Owner
 */
public class NetworkSetup extends JFrame implements ActionListener 
{
    private ArrayList button = new ArrayList();
    private ArrayList controllers = new ArrayList();
    private JRadioButton client, server, single;
    private JTextField portField, ipField, nameField;
    private boolean startGame = false;
    private JButton start;
    private String inputIP, inputPort, inputName;
    
    public NetworkSetup()
    {        
        JPanel namePanel = new JPanel();
        JPanel portPanel = new JPanel();
        JPanel ipPanel = new JPanel();
        ButtonGroup radioButtons = new ButtonGroup();
        JPanel rbPanel = new JPanel();
        JLabel description = new JLabel("User name: ", JLabel.CENTER);
        JLabel ip = new JLabel("IP address: ");
        JLabel port = new JLabel("Port number: ");
        start = new JButton("Start Game");
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,2));
        
        client = new JRadioButton("Client");
        client.addActionListener(this);
        single = new JRadioButton("Single Player");
        single.addActionListener(this);
        server = new JRadioButton("Server");
        server.addActionListener(this);
        start.addActionListener(this);
        portField = new JTextField(6);
        ipField = new JTextField(16);
        nameField = new JTextField(16);
        
        rbPanel.add(single);
        rbPanel.add(server);
        rbPanel.add(client);                
        radioButtons.add(single);
        radioButtons.add(client);
        radioButtons.add(server);                
        namePanel.add(description);
        namePanel.add(nameField);
        ipPanel.add(ip);
        ipPanel.add(ipField);
        portPanel.add(port);
        portPanel.add(portField);        
        
        setLayout(new GridLayout(5,1));
        
        add(namePanel);
        add(rbPanel);
        add(portPanel);
        add(ipPanel);
        add(start);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 250);
        setLocation(500, 300);
        setTitle("Network Setup");                  
    }
    
    public boolean buttonPressed()
    {
        return startGame;
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if(e.getSource().equals(start))
        {            
            inputName = nameField.getText();
            inputIP = ipField.getText();
            inputPort = portField.getText();           
            startGame = true;
        }
        else if(e.getSource().equals(client))
        {
            ipField.setEnabled(true);
            portField.setEnabled(true);
        }
        else if(e.getSource().equals(server))
        {
            ipField.setEnabled(false);
            portField.setEnabled(true);
        }
        else if(e.getSource().equals(single))
        {
            ipField.setEnabled(false);
            portField.setEnabled(false);
        }
    }
    
    /*
     * @return inputName is the user name that was input to the GUI
     */
    public String getUserName() 
    {
        return inputName;
    }
    /*
     * @return inputIP is the IP address that was input to the GUI
     */
    public String getIP() 
    {
        return inputIP;
    }
    /*
     * @return inputport is the port number that was input to the GUI
     */
    public String getPort() 
    {
        return inputPort;
    }
}
