package services.GUI;


import services.Controller;
import services.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Created by Lucas on 31/10/2015.
 */
public class ConnectedFrame extends JFrame{

    Model model;
    Model.User selectedRemoteUser;

    //Panels
    Choice userListChoice;
    private JButton selectUserButton;
    private JLabel selectedUser;
    private JTextArea conversationTextField;
    private JTextArea toSendTextField;
    private JButton sendButton;
    private JButton disconnectButton;
    private JButton sendFileButton;

    //Other tools
    private Timer refreshTimer;
    private JFileChooser fc;


    public ConnectedFrame(Model model)
    {
        this.model = model;
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());

        //Setting up interface
        userListChoice = new Choice();
        selectUserButton = new JButton("Select");
        selectedUser = new JLabel("Selected : none");
        conversationTextField = new JTextArea("");
        conversationTextField.setEditable(false);
        toSendTextField = new JTextArea("");
        sendButton = new JButton("Send");
        sendFileButton = new JButton("Send a file ...");

        selectUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userListChoice.getSelectedIndex()==-1)
                {
                    System.out.println("Aucun utilisateur selectionne.");
                    return;
                }

                selectedRemoteUser = model.getUserList().elementAt(userListChoice.getSelectedIndex());
                selectedUser.setText("Selected : " + selectedRemoteUser.getNickname());
                refreshEntireConversation();
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRemoteUser==null)
                    System.out.println("Aucun utilisateur selectionne.");
                else if (!selectedRemoteUser.isConnected())
                    System.out.println("L'utilisateur selectionne est deconnecte.");

                else
                {
                    Controller.getInstance().sendText(toSendTextField.getText(), selectedRemoteUser);
                    toSendTextField.setText("");
                }
            }
        });
        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRemoteUser==null)
                    System.out.println("Aucun utilisateur selectionne.");
                else if (!selectedRemoteUser.isConnected())
                    System.out.println("L'utilisateur selectionne est deconnecte.");
                else {
                    if (fc.showOpenDialog(null)== JFileChooser.APPROVE_OPTION) {
                        Controller.getInstance().sendFile(fc.getSelectedFile(), selectedRemoteUser);
                    }
                }
            }
        });

        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;

        constraints.gridx=0;
        constraints.gridy=0;
        constraints.ipady=20;
        constraints.ipadx=400;
        add(userListChoice, constraints);
        constraints.gridx=1;
        constraints.gridy=0;
        constraints.ipady=0;
        constraints.ipadx=0;
        add(selectUserButton, constraints);
        constraints.gridx=1;
        constraints.gridy=1;
        constraints.ipady=20;
        constraints.ipadx=200;
        add(selectedUser, constraints);
        constraints.gridx=1;
        constraints.gridy=2;
        constraints.ipady=10;
        constraints.ipadx=20;
        add(sendButton, constraints);
        constraints.gridx=0;
        constraints.gridy=1;
        constraints.ipady=20;
        constraints.ipadx=40;
        add(conversationTextField,constraints);
        constraints.gridx=0;
        constraints.gridy=2;
        add(toSendTextField, constraints);
        constraints.gridx=0;
        constraints.gridy=3;
        constraints.ipady=0;
        constraints.ipadx=0;
        add(sendFileButton,constraints);

        setSize(800, 600);
        this.getLayout().minimumLayoutSize(this);


        //Setting up refresh timer
        refreshTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isConversationNeedUpdate()){
                    model.setConversationNeedUpdate(false);
                    refreshEntireConversation();
                }
                if (model.isUserListNeedUpdate()){
                    refreshUserList();
                    model.setUserListNeedUpdate(false);
                }
            }
        });
        refreshTimer.setActionCommand("Refresh");
        refreshTimer.start();

        //Setting up file chooser
        fc = new JFileChooser();
        pack();

    }

    public void refreshUserList()
    {
        Vector<Model.User> userList = model.getUserList();
        userListChoice.removeAll();
        for (Model.User u : userList)
        {
            userListChoice.add(u.getNickname());
        }
    }


    private void refreshEntireConversation()
    {
        int index = model.getUserList().indexOf(selectedRemoteUser);
        if (index==-1)
            return; //No user selected, nothing to be done
        Vector<Model.Msg> conversation = model.getConversations().elementAt(index);
        String s = "";
        for (Model.Msg m : conversation)
        {
            if (m.getClass()==Model.TextMsg.class) {
                s = s + m + System.lineSeparator();
            }
            else if(m.getClass()==Model.FileMsg.class) {
                s= s+"Oui a file :D\n";

            }
        }
        conversationTextField.setText(s);
    }
}
