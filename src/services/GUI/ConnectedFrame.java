package services.GUI;

import services.Model;
import services.network.ChatNetwork;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by Lucas on 31/10/2015.
 *
 */
public class ConnectedFrame extends JFrame implements ActionListener, WindowListener,ListSelectionListener{

    private Model model;

    //J
    private JList<Model.User> listUser;
    private JList<String> listFilesWaiting; // TODO !!!
    private JTabbedPane tabbedPane;
    private JButton disconnectButton;
    private Vector<Model.User> openedTab;

    public ConnectedFrame(Model model)
    {
        this.model = model;

        setLayout(new BorderLayout());

        //TabbedPane
        openedTab = new Vector<>();

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


        listUser.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listUser.setLayoutOrientation(JList.VERTICAL);
        listUser.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(listUser);
        listScroller.setPreferredSize(new Dimension(250, 80));

        j.add(listScroller,BorderLayout.CENTER);

        //JList Files
        listFilesWaiting = new JList<>();
        listFilesWaiting.addListSelectionListener(this); // TODO
        listFilesWaiting.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listFilesWaiting.setLayoutOrientation(JList.VERTICAL);
        listFilesWaiting.setVisibleRowCount(-1);
        JScrollPane listScrollerFiles = new JScrollPane(listFilesWaiting);
        listScrollerFiles.setPreferredSize(new Dimension(250, 80));
        j.add(listScrollerFiles,BorderLayout.PAGE_END);

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

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand())
		{
			case "Disconnection":
                //TODO : manage sending file etc
                closeProgram();
				break;
				
			case "Refresh" :
                if (model.isUserListNeedUpdate()){
                    refreshUserList();
                    model.setUserListNeedUpdate(false);
                }
                break;
		}
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
        int answer = JOptionPane.showConfirmDialog(null, "Do you want to quit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
            }
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (listUser.getSelectedIndex() != -1) {
                // TODO : check if user tab already exist and open this one !
                if(openedTab.indexOf(listUser.getSelectedValue()) != -1){
                    // open the tab at indexOf
                }
                else {
                    // else create it
                    JComponent panel1;
                    panel1 = new ConversationComponent(model, listUser.getSelectedValue());
                    panel1.setSize(500, 500);
                    openedTab.add(listUser.getSelectedValue());
                    tabbedPane.addTab(listUser.getSelectedValue().getNickname(), null, panel1); //TODO : manage null
                }
            }
        }		
	}
}
