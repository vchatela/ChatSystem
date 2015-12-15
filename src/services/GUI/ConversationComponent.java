package services.GUI;

import services.ChatController;
import services.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 * Created by ValentinC on 04/11/2015.
 */
public class ConversationComponent extends JComponent implements ActionListener, Observer{

    private Model model;
    private Model.User selectedRemoteUser;
    private JTextArea conversation;
    private JTextField message;
    private JButton jSend;
    private JButton jSendFile;
    private final static int sizeCutMessage = 70;


    //Other tools
    private JFileChooser fc;

    public ConversationComponent(Model model, Model.User selectedRemoteUser) {
        this.model = model;
        this.selectedRemoteUser = selectedRemoteUser;
        model.addObserver(this);

        setLayout(new BorderLayout());

        conversation = new JTextArea();
        conversation.setEditable(false);
        // TODO
        //Font font = new Font("Verdana", Font.BOLD, 12);
         //conversation.setFont(font);
        JScrollPane j= new JScrollPane(conversation);
        add(j,BorderLayout.CENTER);

        JPanel b1 = new JPanel();
        b1.setLayout(new BorderLayout());

        message = new JTextField();
        message.addActionListener(this);
        message.setActionCommand("Send");
        b1.add(message,BorderLayout.CENTER);

        JPanel b2 = new JPanel();
        b2.setLayout(new BorderLayout());

        jSend = new JButton("Send");
        jSend.addActionListener(this);
        b2.add(jSend,BorderLayout.PAGE_START);

        //Setting up file chooser
        fc = new JFileChooser();

        jSendFile = new JButton("Send a File");
        jSendFile.addActionListener(this);
        b2.add(jSendFile,BorderLayout.PAGE_END);

        JPanel b3 = new JPanel();
        b3.setLayout(new BorderLayout());
        b3.add(b1,BorderLayout.CENTER);
        b3.add(b2,BorderLayout.LINE_END);


        add(b3,BorderLayout.PAGE_END);
    }
    
    private void refreshEntireConversation()
    {
        int index = model.getUserList().indexOf(selectedRemoteUser);
        if (index==-1)
            return; //No user selected, nothing to be done

        //Vector is shared and must be synchronized
        Vector<Model.Msg> conversation = model.getConversations().elementAt(index);
        String s = "";

        for (Model.Msg m : conversation)
        {
            if (m.getClass()==Model.TextMsg.class) {
                s = s + formatMessage(m.toString()) + System.lineSeparator() + System.lineSeparator();
            }
            else if(m.getClass()==Model.FileMsg.class) {
                Model.FileMsg fileMsg = (Model.FileMsg)m;
                if (fileMsg.getTransferType()== Model.FileMsg.TransferType.FromRemoteUser)
                {
                    s= s+"Remote user asked for a file transfer." + System.lineSeparator() + System.lineSeparator();
                }
                else
                {
                    s= s+"You asked for a file transfer." + System.lineSeparator() + System.lineSeparator();
                }
            }
        }
        this.conversation.setText(s);

    }
    public String formatMessage(String value){
        String res ="";
        int i = 0;
        while(i+sizeCutMessage<value.length()) {
            res += value.substring(i,i+sizeCutMessage) + System.lineSeparator();
            i+=sizeCutMessage;
        }
        res += value.substring(i,value.length());
        return res;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Send":
                actionSend();
                break;
            case "Send a File":
                if (selectedRemoteUser == null)
                    System.out.println("Aucun utilisateur selectionne.");
                else if (!selectedRemoteUser.isConnected())
                    System.out.println("L'utilisateur selectionne est deconnecte.");
                else {
                    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        ChatController.getInstance().sendFile(fc.getSelectedFile(), selectedRemoteUser);
                    }
                }
                break;
        }
	}

    private void actionSend() {
        if (selectedRemoteUser == null)
            System.out.println("Aucun utilisateur selectionne.");
        else if (!selectedRemoteUser.isConnected())
            System.out.println("L'utilisateur selectionne est deconnecte.");
        else if (message.getText().equals("")) {
            //do nothing
        } else {
            ChatController.getInstance().sendText(message.getText(), selectedRemoteUser);
            message.setText("");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (model.isConversationNeedUpdate()) {
            model.setConversationNeedUpdate(false);
            refreshEntireConversation();
        }
        if(!selectedRemoteUser.isConnected()) {
            jSend.setEnabled(false);
            jSendFile.setEnabled(false);
        }
        else {
            jSend.setEnabled(true);
            jSendFile.setEnabled(true);
        }
    }
}
