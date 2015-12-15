package services.GUI;

import services.model.FileMessage;
import services.model.Model;
import services.network.tcp.ReceiverTCP;
import services.network.tcp.SenderTCP;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


public class FileTransferComponent extends JComponent implements ActionListener{
    private FileMessage fileMsg;
    private JProgressBar progressBar;
    private JLabel stateLabel;
    private JButton acceptButton;
    private JButton refuseButton;
    private JButton openButton;

    public FileTransferComponent(FileMessage fileMsg)
    {
        this.fileMsg = fileMsg;
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        stateLabel = new JLabel();
        acceptButton = new JButton("Accept");
        acceptButton.addActionListener(this);
        refuseButton = new JButton("Refuse");
        refuseButton.addActionListener(this);
        openButton = new JButton("Open");
        openButton.addActionListener(this);

        setLayout(new BorderLayout());
        Border border = BorderFactory.createEtchedBorder();

        if (fileMsg.getTransferType()== FileMessage.TransferType.FromRemoteUser)
        {
            ReceiverTCP receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());
            progressBar.setMaximum((int)receiverTCP.getFileLength());
            setBorder(BorderFactory.createTitledBorder(border, receiverTCP.getFileName()));
        }
        else
        {
            SenderTCP senderTCP = SenderTCP.getInstance(fileMsg.getHashcodeTCP());
            progressBar.setMaximum((int)senderTCP.getFileLength());
            setBorder(BorderFactory.createTitledBorder(border, senderTCP.getFileName()));
        }

        update();
    }

    public void update()
    {
        removeAll();
        setMaximumSize(new Dimension(245, 55));

        if (fileMsg.getTransferType()== FileMessage.TransferType.FromRemoteUser)
        {
            ReceiverTCP receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());
            long bytesReceived = receiverTCP.getBytesReceived();
            long total = receiverTCP.getFileLength();

            switch (receiverTCP.getFileState())
            {
                case waiting_for_accept:
                    add(acceptButton, BorderLayout.LINE_START);
                    add(refuseButton, BorderLayout.LINE_END);

                    stateLabel.setText("New request : file length=" + receiverTCP.getFileLength() + " bytes");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case receiving_file:
                    progressBar.setValue((int) bytesReceived);
                    progressBar.setString(((Long) bytesReceived).toString() + "/" + ((Long) total).toString() + " bytes");
                    add(progressBar, BorderLayout.PAGE_END);

                    stateLabel.setText("State : receiving file ...");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case file_transferred:
                    add(openButton, BorderLayout.LINE_START);
                    stateLabel.setText("State : file received");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case file_refused:
                    stateLabel.setText("State : file refused");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case error:
                    stateLabel.setText("State : an unknown error as occurred");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

            }
        }
        else
        {
            SenderTCP senderTCP = SenderTCP.getInstance(fileMsg.getHashcodeTCP());
            long bytesReceived = senderTCP.getBytesSent();
            long total = senderTCP.getFileLength();

            switch (senderTCP.getFileState())
            {
                case waiting_for_accept:
                    stateLabel.setText("State : pending ...");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case sending_file:
                    progressBar.setValue((int) bytesReceived);
                    progressBar.setString(((Long) bytesReceived).toString() + "/" + ((Long) total).toString() + " bytes");
                    add(progressBar, BorderLayout.PAGE_END);

                    stateLabel.setText("State : sending file ...");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case file_transferred:
                    stateLabel.setText("State : file sent");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case file_refused:
                    stateLabel.setText("State : file refused by the remote user");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

                case error:
                    stateLabel.setText("State : an unknown error as occurred");
                    add(stateLabel, BorderLayout.PAGE_START);
                    break;

            }
        }

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ReceiverTCP receiverTCP;

        switch (e.getActionCommand()) {
            case "Accept":
                receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());
                if (receiverTCP.getFileState()==ReceiverTCP.State.waiting_for_accept)
                {
                    JFileChooser fc = new JFileChooser();
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    fc.setAcceptAllFileFilterUsed(false);

                    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        try {
                            receiverTCP.acceptFile(fc.getSelectedFile().getCanonicalPath());
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
                break;

            case "Refuse":
                receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());
                receiverTCP.refuseFile();
                break;

            case "Open":
                receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());
                try {
                    Desktop.getDesktop().open(new File(receiverTCP.getFilePath() +File.separator+ receiverTCP.getFileName()));
                } catch (IOException e2) {
                    System.out.println("Can't open file : " + receiverTCP.getFileName());
                    e2.printStackTrace();
                }
                break;
        }
    }
}
