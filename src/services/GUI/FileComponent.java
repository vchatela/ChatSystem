package services.GUI;

import services.Controller;
import services.Model;
import services.network.tcp.ReceiverTCP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Admin on 03/12/2015.
 */
public class FileComponent extends JComponent implements ActionListener{

    public FileComponent(Model.FileMsg fileMsg)
    {
        this.setLayout(new GridLayout(2, 2));

        if (fileMsg.getTransferType()== Model.FileMsg.TransferType.FromRemoteUser)
        {
            ReceiverTCP receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());

            JLabel fileName = new JLabel(receiverTCP.getFileName());
            add(fileName);

            JLabel fileLength = new JLabel(String.valueOf(receiverTCP.getFileLength()) + " bytes");
            add(fileLength);

            JButton accept = new JButton("Accept");
            add(accept);
            accept.addActionListener(this);

            JButton refuse = new JButton("Refuse");
            add(refuse);
            refuse.addActionListener(this);
        }
        else
        {
            JLabel state2 = new JLabel(fileMsg.getTransferType().toString() +" todo");
            add(state2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Action performed");

        switch (e.getActionCommand()) {
            case "Accept":
                JFileChooser jc = new JFileChooser("test");
                jc.getSelectedFile();
                break;

            default:
                break;
        }
    }
}
