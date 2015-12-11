package services.GUI;

import services.Model;
import services.network.ChatNetwork;
import services.network.tcp.ReceiverTCP;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by Lucas on 31/10/2015.
 *
 */
public class ConnectedFrame extends JFrame implements ActionListener, WindowListener,ListSelectionListener{

    private Model model;

    //J
    private JList<Model.User> listUser;
    private JList<Model.FileMsg> fileTransferJList;
    private Vector<Model.FileMsg> fileTransferVector;
    private JTabbedPane tabbedPane;
    private JButton disconnectButton;

    public ConnectedFrame(Model model)
    {
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


        listUser.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listUser.setLayoutOrientation(JList.VERTICAL);
        listUser.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(listUser);
        listScroller.setPreferredSize(new Dimension(250, 80));

        j.add(listScroller,BorderLayout.CENTER);

        //JList Files
        fileTransferVector = new Vector<Model.FileMsg>();
        fileTransferJList = new JList<Model.FileMsg>();
        fileTransferJList.addListSelectionListener(this); // TODO
        fileTransferJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        fileTransferJList.setLayoutOrientation(JList.VERTICAL);
        fileTransferJList.setVisibleRowCount(-1);

        FileListCellRenderer cellRenderer = new FileListCellRenderer();
        fileTransferJList.setCellRenderer(cellRenderer);
        JScrollPane listScrollerFiles = new JScrollPane(fileTransferJList);
        listScrollerFiles.setPreferredSize(new Dimension(250, 80));
        j.add(listScrollerFiles,BorderLayout.PAGE_END);

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JList theList = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = theList.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        Model.FileMsg fileMsg = (Model.FileMsg) theList.getModel().getElementAt(index);
                        if(fileMsg.getTransferType()== Model.FileMsg.TransferType.FromRemoteUser)
                        {
                            ReceiverTCP receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());
                            if (!receiverTCP.ackGiven())
                            {
                                JFileChooser fc = new JFileChooser();
                                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                fc.setAcceptAllFileFilterUsed(false);

                                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                    try {
                                        receiverTCP.acceptFile(fc.getSelectedFile().getCanonicalPath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else {
                                if(receiverTCP.getBytesReceived()==receiverTCP.getFileLength()){
                                    // open file
                                    try {
                                        Desktop.getDesktop().open(new File(receiverTCP.getFilePath() +File.separator+ receiverTCP.getFileName()));
                                    } catch (IOException e) {
                                        System.out.println("Can't open file : " + receiverTCP.getFileName());
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };

        fileTransferJList.addMouseListener(mouseListener);

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
                //TODO : manage sending file etc
                closeProgram();
				break;
				
			case "Refresh" :
                if (model.isUserListNeedUpdate()){
                    refreshUserList();
                    model.setUserListNeedUpdate(false);
                }

                if (model.isFileTransferNeedUpdate())
                {
                    LinkedList<Model.FileMsg> fileMsgs = model.getNewFileTransferRequests();


                    while (!fileMsgs.isEmpty())
                    {
                        fileTransferVector.addElement(fileMsgs.pollLast());
                    }
                    fileTransferJList.setListData(fileTransferVector);
                    //model.setFileTransferNeedUpdate(false);
                }
                if(model.isNeedToOpenATab()){
                    Model.User user = model.getUsertabToOpen();
                    createNewTab(user);
                    //on oublie pas de le reset
                    model.setUsertabToOpen(null);
                    model.setNeedToOpenATab(false);
                }
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
                // TODO : check if user tab already exist and open this one !
                if(model.getUserListOpenedTab().indexOf(listUser.getSelectedValue()) != -1){
                    // open the tab at indexOf
                }
                else {
                    // else create it
                    createNewTab(listUser.getSelectedValue());
                }
            }
        }
	}
    private void createNewTab(Model.User user){
        JComponent panel1;
        panel1 = new ConversationComponent(model, user);
        panel1.setSize(500, 500);
        model.getUserListOpenedTab().add(user);
        model.isConversationNeedUpdate();
        tabbedPane.addTab(user.getNickname(), null, panel1);
    }
}
