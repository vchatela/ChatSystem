package services.GUI;


import services.Model;
import services.network.ChatNetwork;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created by Lucas on 31/10/2015.
 *
 */
public class ConnectedFrame extends JFrame{

    private Model model;

    //J
    private JList<Model.User> listUser;
    private JTabbedPane tabbedPane;
    private JButton disconnectButton;
    private ImageIcon iconOnline;
    private ImageIcon iconNotification;

    public ConnectedFrame(Model model)
    {
        this.model = model;

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        iconOnline = createImageIcon("/res/onlineIcon.png","Online icon");
        iconNotification = createImageIcon("/res/notificationIcon.png","Notification icon");

        //TabbedPane
        tabbedPane = new JTabbedPane();

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        add(tabbedPane,c);

        //Disconnect Button
        disconnectButton = new JButton("Disconnection");
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO : manage sending file etc
                closeProgram();
            }
        });
        c.gridx = 1;
        c.gridy = 2;
        add(disconnectButton,c);


        // Jlist
        listUser = new JList(model.getUserList());
        listUser.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (listUser.getSelectedIndex() != -1) {
                        // TODO : check if user tab already exist and open this one !
                        // else create it
                        JComponent panel1;
                        panel1 = new ConversationComponent(model, listUser.getSelectedValue());
                        tabbedPane.addTab(listUser.getSelectedValue().getNickname(), iconOnline, panel1);
                    }
                }
            }
        });


        listUser.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listUser.setLayoutOrientation(JList.VERTICAL);
        listUser.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(listUser);
        listScroller.setPreferredSize(new Dimension(250, 80));

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(listScroller,c);

        //setSize(300,300);

        //Setting up refresh timer
        Timer refreshTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (model.isUserListNeedUpdate()){
                    refreshUserList();
                    model.setUserListNeedUpdate(false);
                }
            }
        });
        refreshTimer.setActionCommand("Refresh");
        refreshTimer.start();

        setSize(800, 600);
        this.getLayout().minimumLayoutSize(this);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "You want to quit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    public void refreshUserList()
    {
        listUser.removeAll();
        listUser.setListData(model.getUserList());
    }

    protected ImageIcon createImageIcon(String path,
                                        String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    private void closeProgram(){
        if(JOptionPane.showConfirmDialog(null,
                "Do you wanna Disconnect?",
                "Choose",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try {
                ChatNetwork.getInstance().sendBye();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            dispose();
            System.exit(0);
        }
    }
}
