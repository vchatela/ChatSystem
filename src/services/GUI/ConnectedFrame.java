package services.GUI;

import services.model.FileMessage;
import services.model.Model;
import services.model.User;
import services.network.ChatNetwork;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Lucas on 31/10/2015.
 *
 */
public class ConnectedFrame extends JFrame implements ActionListener, WindowListener,ListSelectionListener, Observer {

    private Model model;

    //J
    private JList<User> listUser;
    private JPanel fileTransferPanel;
    private JTabbedPane tabbedPane;
    private JButton disconnectButton;


    public ConnectedFrame(Model model)
    {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        setSize(width/2, height/2);
        this.setLocationRelativeTo(null);

        this.model = model;
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        add(tabbedPane,BorderLayout.CENTER);


        JPanel j = new JPanel();
        j.setLayout(new BorderLayout());
        //Disconnect Button
        disconnectButton = new JButton("Disconnection");
        disconnectButton.addActionListener(this);
        j.add(disconnectButton,BorderLayout.PAGE_START);

        // Jlist User
        listUser = new JList(model.getUserList());
        listUser.addListSelectionListener(this);
        listUser.setCellRenderer(new UserListCellRenderer());

        listUser.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listUser.setLayoutOrientation(JList.VERTICAL);
        listUser.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(listUser);
        listScroller.setPreferredSize(new Dimension(250, 80));

        j.add(listScroller,BorderLayout.CENTER);

        //JList Files
        fileTransferPanel = new JPanel();
        fileTransferPanel.setLayout(new BoxLayout(fileTransferPanel, BoxLayout.PAGE_AXIS));

        JScrollPane fileTransferPanelScroller = new JScrollPane(fileTransferPanel);
        fileTransferPanelScroller.setPreferredSize(new Dimension(250, 300));
        j.add(fileTransferPanelScroller, BorderLayout.PAGE_END);

        add(j,BorderLayout.LINE_END);

        //Setting up refresh timer
        Timer refreshTimer = new Timer(100, this);
        refreshTimer.setActionCommand("Refresh");
        refreshTimer.start();

        setSize(1000, 800);
        this.getLayout().minimumLayoutSize(this);

        this.addWindowListener(this);
    }

    public void refreshUserList()
    {
        listUser.removeAll();
        listUser.setListData(model.getUserList());
    }

    private void closeProgram(){
        if(JOptionPane.showConfirmDialog(null,
                "Do you want to Disconnect?",
                "Choose",
                JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try {
                ChatNetwork.getInstance().sendBye();
            } catch (IOException e1) {
                System.out.println("Error during Sending bye !");
                e1.printStackTrace();
            }
            dispose();
            System.exit(0);
        }
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand())
		{
			case "Disconnection":
                closeProgram();
				break;
				
			case "Refresh" :
                //Refresh file transfer list
                    LinkedList<FileMessage> fileMsgs = model.getNewFileTransferRequests();

                    //FileMsgs shared and must be synchronized
                    while (!fileMsgs.isEmpty())
                    {
                        FileMessage fileMsg = fileMsgs.pollLast();
                        fileTransferPanel.add(new FileTransferComponent(fileMsg));
                    }

                //Refresh file transfer state
                for (Component c : fileTransferPanel.getComponents()) {
                    FileTransferComponent fc = (FileTransferComponent) c;
                    fc.update();
                }

                revalidate();
                repaint();
                break;
		}
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
            closeProgram();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	@Override
	public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (listUser.getSelectedIndex() != -1) {
                int index = model.getUserListOpenedTab().indexOf(listUser.getSelectedValue());
                if(index != -1){
                    // open the tab at indexOf
                    tabbedPane.setSelectedIndex(index);
                    model.notifyObservers();
                }
                else {
                    // else create it
                    createNewTab(listUser.getSelectedValue());
                    model.notifyObservers();
                }
            }
        }
	}
    private void createNewTab(User user){
        JComponent panel1;
        panel1 = new ConversationComponent(user);
        panel1.setSize(500, 500);
        model.getUserListOpenedTab().add(user);
        tabbedPane.addTab(user.getNickname(), null, panel1);
        int index = model.getUserListOpenedTab().indexOf(user);
        tabbedPane.setSelectedIndex(index);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (model.isNeedToOpenATab())
        {
            User user = model.getUsertabToOpen();
            createNewTab(user);
            //on oublie pas de le reset
            model.setNeedToOpenATab(false);
            model.setUsertabToOpen(null);
            Wizz.creerWizz(this,5,50);
        }

        if (model.isUserListNeedUpdate()){
            refreshUserList();
            model.setUserListNeedUpdate(false);
        }
    }
}
