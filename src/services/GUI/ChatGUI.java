package services.GUI;

import java.awt.event.*;
import java.io.IOException;

import services.Controller;
import services.Model;

import javax.swing.*;

/**
 * Created by Lucas on 31/10/2015.
 */
public class ChatGUI {

    private DisconnectedFrame disconnectedFrame;
    private ConnectedFrame connectedFrame;

    public ChatGUI(Model model,String username)
    {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        connectedFrame = new ConnectedFrame(model);
        connectedFrame.setTitle("ChatSystem - Connected");
        connectedFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


        if(username==null) {
            disconnectedFrame = new DisconnectedFrame();
            disconnectedFrame.setVisible(true);
            disconnectedFrame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand() == "Connect") {
                        try {
                            Controller.getInstance().Connect(disconnectedFrame.getUserName());
                            disconnectedFrame.setVisible(false);
                            connectedFrame.refreshUserList();
                            connectedFrame.setVisible(true);
                            connectedFrame.setSize(800, 600);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            disconnectedFrame.setTitle("Connexion");
            disconnectedFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            connectedFrame.setVisible(false);
        }
        else {
            try {
                Controller.getInstance().Connect(username);
                connectedFrame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                //TODO !!
            }
        }




    }
}
