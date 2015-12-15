package services.GUI;

import services.ChatController;
import services.model.FileMessage;
import services.model.Messages;
import services.model.TextMessage;
import services.model.User;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

public class ConversationComponent extends JComponent implements ActionListener, Observer{

    //Attributes
    private User remoteUser;
    private JEditorPane conversation;
    private JTextField message;
    private JButton jSend;
    private JButton jSendFile;
    private final static int sizeCutMessage = 70;
    static ImageIcon SMILE_IMG=createImage();

    //Other tools
    private JFileChooser fc;

    public ConversationComponent(User remoteUser) {
        this.remoteUser = remoteUser;
        remoteUser.addObserver(this);

        setLayout(new BorderLayout());

        conversation = new JEditorPane();
        conversation.setEditable(false);
        DefaultCaret caret = (DefaultCaret) conversation.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        //conversation.setLineWrap(true);
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
        conversation.setEditorKit(new StyledEditorKit());
        initListenerJEditorPane(conversation);


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void initListenerJEditorPane(final JEditorPane j) {
        j.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent event) {
                final DocumentEvent e=event;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (e.getDocument() instanceof StyledDocument) try {
                            StyledDocument doc = (StyledDocument) e.getDocument();
                            int start = Utilities.getRowStart(j, Math.max(0, e.getOffset() - 1));
                            int end = Utilities.getWordStart(j, e.getOffset() + e.getLength());
                            String text = doc.getText(start, end - start);

                            int i = text.indexOf(":)");
                            while (i >= 0) {
                                final SimpleAttributeSet attrs = new SimpleAttributeSet(doc.getCharacterElement(start + i).getAttributes());
                                if (StyleConstants.getIcon(attrs) == null) {
                                    StyleConstants.setIcon(attrs, SMILE_IMG);
                                    doc.remove(start + i, 2);
                                    doc.insertString(start + i, ":)", attrs);
                                }
                                i = text.indexOf(":)", i + 2);
                            }
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }
            public void removeUpdate(DocumentEvent e) {
            }
            public void changedUpdate(DocumentEvent e) {
            }
        });
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
        } if (message.getText().equals("")){

        } if (message.getText().equals("Lucas")){
            jSend.setText("Envoyer");
            jSendFile.setText("Envoyer un fichier");
            message.setText("");
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
    static ImageIcon createImage() {
        BufferedImage res=new BufferedImage(17, 17, BufferedImage.TYPE_INT_ARGB);
        Graphics g=res.getGraphics();
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.yellow);
        g.fillOval(0,0,16,16);

        g.setColor(Color.black);
        g.drawOval(0,0,16,16);

        g.drawLine(4,5, 6,5);
        g.drawLine(4,6, 6,6);

        g.drawLine(11,5, 9,5);
        g.drawLine(11,6, 9,6);

        g.drawLine(4,10, 8,12);
        g.drawLine(8,12, 12,10);
        g.dispose();

        return new ImageIcon(res);
    }
}
