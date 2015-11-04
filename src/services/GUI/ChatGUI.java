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

    public ChatGUI(Model model)
    {
        disconnectedFrame = new DisconnectedFrame();
        disconnectedFrame.setVisible(true);
        disconnectedFrame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand()=="Connect") {
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

        connectedFrame = new ConnectedFrame(model);
        connectedFrame.setVisible(false);

        connectedFrame.setTitle("ChatSystem - Connected");
        disconnectedFrame.setTitle("Connexion");
        disconnectedFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connectedFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
