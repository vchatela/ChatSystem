package services.GUI;

import javafx.stage.FileChooser;
import services.Controller;
import services.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

/**
 * Created by Lucas on 31/10/2015.
 */
public class ConnectedPanel extends Panel implements ActionListener{

    Model model;
    Model.User selectedRemoteUser;

    //Panels
    Choice userListChoice;
    private Button selectUserButton;
    private Label selectedUser;
    private TextArea conversationTextField;
    private TextArea toSendTextField;
    private Button sendButton;
    private Button disconnectButton;
    private Button sendFileButton;

    //Other tools
    private Timer refreshTimer;
    private JFileChooser fc;


    public ConnectedPanel(Model model)
    {
        this.model = model;
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());

        //Setting up interface
        userListChoice = new Choice();
        selectUserButton = new Button("Select");
        selectedUser = new Label("Selected : none");
        conversationTextField = new TextArea("");
        conversationTextField.setEditable(false);
        toSendTextField = new TextArea("");
        sendButton = new Button("Send");
        sendFileButton = new Button("Send a file ...");

        selectUserButton.addActionListener(this);
        sendButton.addActionListener(this);
        sendFileButton.addActionListener(this);

        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;

        constraints.gridx=0;
        constraints.gridy=0;
        add(userListChoice, constraints);
        constraints.gridx=1;
        constraints.gridy=0;
        add(selectUserButton, constraints);
        constraints.gridx=2;
        constraints.gridy=0;
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
        refreshTimer = new Timer(100, this);
        refreshTimer.setActionCommand("Refresh");
        refreshTimer.start();

        //Setting up file chooser
        fc = new JFileChooser();

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand()=="Send") {

            if (selectedRemoteUser==null)
                System.out.println("Aucun utilisateur selectionne.");
            else if (!selectedRemoteUser.isConnected())
                System.out.println("L'utilisateur selectionne est deconnecte.");

            else
            {
                Controller.getInstance().sendText(toSendTextField.getText(), selectedRemoteUser);
                /*Model.TextMsg t = new Model.TextMsg(toSendTextField.getText(), selectedRemoteUser);
                Controller.getInstance().sendMessage(t);*/
                toSendTextField.setText("");
            }
        }
        else if (e.getActionCommand()=="Select")
        {
            if (userListChoice.getSelectedIndex()==-1)
            {
                System.out.println("Aucun utilisateur selectionne.");
                return;
            }

            selectedRemoteUser = model.getUserList().elementAt(userListChoice.getSelectedIndex());
            selectedUser.setText("Selected : " + selectedRemoteUser.getNickname());
            refreshEntireConversation();
        }
        else if (e.getActionCommand()=="Refresh")
        {
            if (model.isConversationNeedUpdate()){
                model.setConversationNeedUpdate(false);
                refreshEntireConversation();
            }
            if (model.isUserListNeedUpdate()){
                refreshUserList();
                model.setUserListNeedUpdate(false);
            }
        }
        else if (e.getActionCommand()=="Send a file ...")
        {
            if (selectedRemoteUser==null)
                System.out.println("Aucun utilisateur selectionne.");
            else if (!selectedRemoteUser.isConnected())
                System.out.println("L'utilisateur selectionne est deconnecte.");
            else {
                if (fc.showOpenDialog(this)== JFileChooser.APPROVE_OPTION) {
                    Controller.getInstance().sendFile(fc.getSelectedFile(), selectedRemoteUser);
                }
            }

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
            s = s + m + System.lineSeparator();
        }
        conversationTextField.setText(s);
    }
}
