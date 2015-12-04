package services.GUI;

import services.Model;
import services.network.tcp.ReceiverTCP;
import services.network.tcp.SenderTCP;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Admin on 03/12/2015.
 */
public class FileListCellRenderer extends DefaultListCellRenderer{
    public FileListCellRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        Model.FileMsg fileMsg = (Model.FileMsg)value;


        if (fileMsg.getTransferType()== Model.FileMsg.TransferType.FromRemoteUser)
        {
            ReceiverTCP receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());

            if (receiverTCP.ackGiven())
            {
                float percentage = receiverTCP.getBytesReceived() /receiverTCP.getFileLength() * 100;
                renderer.setText("Réception de \"" + receiverTCP.getFileName().substring(0,receiverTCP.getFileName().length()>10 ? 10:receiverTCP.getFileName().length()) + "\" ... " + percentage + "%");
            }
            else
            {
                renderer.setText("Réception de \"" + receiverTCP.getFileName().substring(0,receiverTCP.getFileName().length()>10 ? 10:receiverTCP.getFileName().length()) + "\" ... En attente");
            }

        }
        else
        {
            SenderTCP senderTCP = SenderTCP.getInstance(fileMsg.getHashcodeTCP());
            float percentage = senderTCP.getBytesSent()/senderTCP.getFileLength() * 100;
            renderer.setText("Envoi de \"" + senderTCP.getFileName().substring(0,senderTCP.getFileName().length()>10 ? 10:senderTCP.getFileName().length()) + "\" ... " + percentage + "%");
        }


        return renderer;
    }
}