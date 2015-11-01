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
public class ConnectedPanel extends Panel implements ActionListener{

    Model model;
    Model.User selectedRemoteUser;

    Choice userListChoice;
    private Button selectUserButton;
    private Label selectedUser;
    private TextArea conversationTextField;
    private TextArea toSendTextField;
    private Button sendButton;
    private Button disconnectButton;

    private Timer refreshTimer;

    public ConnectedPanel(Model model)
    {
        this.model = model;
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());


        userListChoice = new Choice();
        selectUserButton = new Button("Select");
        selectedUser = new Label("Selected : none");
        conversationTextField = new TextArea("");
        conversationTextField.setEditable(false);
        toSendTextField = new TextArea("");
        sendButton = new Button("Send");

        selectUserButton.addActionListener(this);
        sendButton.addActionListener(this);

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
        add(sendButton, constraints);
        constraints.gridx=0;
        constraints.gridy=1;
        constraints.ipady=20;
        constraints.ipadx=40;
        add(conversationTextField,constraints);
        constraints.gridx=0;
        constraints.gridy=2;
        add(toSendTextField, constraints);

        setSize(800, 600);
        this.getLayout().minimumLayoutSize(this);

        refreshTimer = new Timer(100, this);
        refreshTimer.setActionCommand("Refresh");
        refreshTimer.start();

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
                System.out.println("Aucun utilisateur selectionne !");
            else if (!selectedRemoteUser.isConnected())
                System.out.println("L'utilisateur selectionne est deconnecte!");

            else
            {
                Model.Text t = new Model.Text(toSendTextField.getText(), selectedRemoteUser);
                Controller.getInstance().sendMessage(t);
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
