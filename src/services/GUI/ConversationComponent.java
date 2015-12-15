package services.GUI;

import services.ChatController;
import services.model.*;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class ConversationComponent extends JComponent implements ActionListener, Observer{

    //Attributes
    private User remoteUser;
    private JTextArea conversation;
    private JTextField message;
    private JButton jSend;
    private JButton jSendFile;
    private final static int sizeCutMessage = 70;


    //Other tools
    private JFileChooser fc;

    public ConversationComponent(User remoteUser) {
        this.remoteUser = remoteUser;
        remoteUser.addObserver(this);

        setLayout(new BorderLayout());

        conversation = new JTextArea();
        conversation.setEditable(false);
        DefaultCaret caret = (DefaultCaret) conversation.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        conversation.setLineWrap(true);
        //random font ...
        conversation.setFont(Font.getFont("Calibri"));

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

        refreshEntireConversation();
    }
    
    private void refreshEntireConversation()
    {
        Vector<Messages> conversation = remoteUser.getConversation();
        String s = "";

        for (Messages m : conversation)
        {
            if (m.getClass()==TextMessage.class) {
                s = s + m.toString() + System.lineSeparator() + System.lineSeparator();
            }
            else if(m.getClass()==FileMessage.class) {
                FileMessage fileMsg = (FileMessage)m;
                if (fileMsg.getTransferType()== FileMessage.TransferType.FromRemoteUser)
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

	@Override
	public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Send":
                actionSend();
                break;
            case "Send a File":
                if (remoteUser == null)
                    System.out.println("Aucun utilisateur selectionne.");
                else if (!remoteUser.isConnected())
                    System.out.println("L'utilisateur selectionne est deconnecte.");
                else {
                    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        ChatController.getInstance().sendFile(fc.getSelectedFile(), remoteUser);
                    }
                }
                break;
        }
	}

    private void actionSend() {
        if (!remoteUser.isConnected())
            System.out.println("L'utilisateur selectionne est deconnecte.");
        else if (message.getText().equals("")) {
            //do nothing
        } if(message.getText().equals("Ernesto")) {
            jSend.setText("Enviar");
            jSendFile.setText("Enviar archivo");
            message.setText("");
            return;
        } if(message.getText().equals("Gilles")){
            changeFontRandom();
            message.setText("");
            return;
        }
        else
        {
            ChatController.getInstance().sendText(message.getText(), remoteUser);
            message.setText("");
        }
    }

    private void changeFontRandom() {
        Font[] tabfont = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        conversation.setFont(tabfont[(int) (Math.random()*(tabfont.length))].deriveFont(14.0f));
    }

    @Override
    public void update(Observable o, Object arg) {
        if (remoteUser.newMessages())
            refreshEntireConversation();
        if(!remoteUser.isConnected()) {
            jSend.setEnabled(false);
            jSendFile.setEnabled(false);
        }
        else {
            jSend.setEnabled(true);
            jSendFile.setEnabled(true);
        }
    }
}
