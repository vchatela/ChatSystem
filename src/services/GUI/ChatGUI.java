package services.GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import services.Controller;
import services.Model;

/**
 * Created by Lucas on 31/10/2015.
 */
public class ChatGUI extends Frame implements ActionListener, WindowListener {

    private DisconnectedPanel disconnectedPanel;
    private ConnectedPanel connectedPanel;

    public ChatGUI(Model model)
    {
        addWindowListener(this);
        setLayout(new CardLayout());

        disconnectedPanel = new DisconnectedPanel();
        disconnectedPanel.addActionListener(this);
        disconnectedPanel.setVisible(true);
        add(disconnectedPanel);

        connectedPanel = new ConnectedPanel(model);
        connectedPanel.setVisible(false);
        add(connectedPanel);

        setTitle("ChatSystem");
        setSize(disconnectedPanel.getSize());
        setVisible(true);
        setResizable(false);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand()=="Connect") {
            try {
                Controller.getInstance().Connect(disconnectedPanel.getUserName());
                disconnectedPanel.setVisible(false);
                connectedPanel.refreshUserList();
                //setSize(connectedPanel.getSize());
                setSize(800, 600);
                connectedPanel.setVisible(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
