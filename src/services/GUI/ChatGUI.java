package services.GUI;

import services.ChatController;
import services.Model;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Lucas on 31/10/2015.
 *
 */
public class ChatGUI {

    private DisconnectedFrame disconnectedFrame;
    private ConnectedFrame connectedFrame;

    public ChatGUI(Model model,String username)
    {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        connectedFrame = new ConnectedFrame(model);
        connectedFrame.setTitle("ChatSystem - Connected");
        connectedFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


        if(username==null) {
            disconnectedFrame = new DisconnectedFrame();
            disconnectedFrame.addActionUsername(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    changeToConnectedFrame();
                }
            });
            disconnectedFrame.setVisible(true);
            disconnectedFrame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals("Connect")) {
                        changeToConnectedFrame();
                    }
                }
            });
            disconnectedFrame.setTitle("Connexion");
            disconnectedFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            connectedFrame.setVisible(false);
        }
        else {
            try {
                ChatController.getInstance().Connect(username);
                connectedFrame.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR : Can't connect with username : " + username);
            }
        }
    }
    private void changeToConnectedFrame(){
        if(disconnectedFrame.getUserName().equals("")){
            ToastMessage toastMessage = new ToastMessage("Invalid username ",3000);
            toastMessage.setVisible(true);
        }
        else{
            try {
                ChatController.getInstance().Connect(disconnectedFrame.getUserName());
                disconnectedFrame.setVisible(false);
                connectedFrame.refreshUserList();
                connectedFrame.setVisible(true);
                connectedFrame.setSize(800, 600);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public class ToastMessage extends JDialog {
        int miliseconds;
        public ToastMessage(String toastString, int time) {
            this.miliseconds = time;
            setUndecorated(true);
            getContentPane().setLayout(new BorderLayout(0, 0));

            JPanel panel = new JPanel();
            panel.setBackground(Color.GRAY);
            panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
            getContentPane().add(panel, BorderLayout.CENTER);

            JLabel toastLabel = new JLabel("");
            toastLabel.setText(toastString);
            toastLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            toastLabel.setForeground(Color.WHITE);

            setBounds(100, 100, toastLabel.getPreferredSize().width+20, 31);


            setAlwaysOnTop(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int y = dim.height/2-getSize().height/2;
            int half = y/2;
            setLocation(dim.width/2-getSize().width/2, y+half);
            panel.add(toastLabel);
            setVisible(false);

            new Thread(){
                public void run() {
                    try {
                        Thread.sleep(miliseconds);
                        dispose();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
