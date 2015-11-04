package services.GUI;

import services.Controller;
import services.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

/**
 * Created by ValentinC on 04/11/2015.
 */
public class ConversationComponent extends JComponent {

    private Model model;
    private Model.User selectedRemoteUser;
    private JTextArea conversation;
    private JTextField message;
    private JButton jSend;
    private JButton jSendFile;


    //Other tools
    private JFileChooser fc;

    public ConversationComponent(Model model, Model.User selectedRemoteUser) {
        this.model = model;
        this.selectedRemoteUser = selectedRemoteUser;

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        conversation = new JTextArea();
        conversation.setEditable(false);
        constraints.gridx=GridBagConstraints.REMAINDER;
        constraints.gridy=0;
        constraints.ipady=20;
        constraints.ipadx=40;
        constraints.insets = new Insets(15, 15, 15, 15);
        add(conversation,constraints);


        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRemoteUser==null)
                    System.out.println("Aucun utilisateur selectionne.");
                else if (!selectedRemoteUser.isConnected())
                    System.out.println("L'utilisateur selectionne est deconnecte.");

                else
                {
                    Controller.getInstance().sendText(message.getText(), selectedRemoteUser);
                    message.setText("");
                }
            }
        };


        message = new JTextField();
        message.addActionListener(actionListener);
        message.setSize(400,100);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.ipady=20;
        constraints.ipadx=40;
        constraints.insets = new Insets(15, 15, 15, 15);
        add(message,constraints);

        jSend = new JButton("Send");
        jSend.addActionListener(actionListener);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.ipady=20;
        constraints.ipadx=40;
        constraints.insets = new Insets(15, 15, 15, 15);
        add(jSend,constraints);



        //Setting up file chooser
        fc = new JFileChooser();

        jSendFile = new JButton("Send a File");
        jSendFile.addActionListener(new ActionListener() {
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

        constraints.gridx = 2;
        constraints.gridy =1;
        constraints.ipady=20;
        constraints.ipadx=40;
        constraints.insets = new Insets(15, 15, 15, 15);
        add(jSendFile,constraints);

        Timer refreshTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isConversationNeedUpdate()){
                    model.setConversationNeedUpdate(false);
                    refreshEntireConversation();
                }
            }
        });
        refreshTimer.setActionCommand("Refresh");
        refreshTimer.start();

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
                s= s+"Getting a file ! Will be implemented soon..\n";

            }
        }
        this.conversation.setText(s);
    }

}
