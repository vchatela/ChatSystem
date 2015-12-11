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
    JLabel renderer;
    Model.FileMsg fileMsg;
    public FileListCellRenderer() {
        super();
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        renderer = (JLabel) super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
        fileMsg = (Model.FileMsg)value;
        if (fileMsg.getTransferType()== Model.FileMsg.TransferType.FromRemoteUser)
        {
            ReceiverTCP receiverTCP = ReceiverTCP.getInstance(fileMsg.getHashcodeTCP());

            if (receiverTCP.ackGiven())
            {

                float percentage = (float)receiverTCP.getBytesReceived() / (float)receiverTCP.getFileLength() * 100;

                renderer.setText("Réception de \"" + receiverTCP.getFileName() + "\" ... " + percentage + "%");}
            else
            {
                renderer.setText("Réception de \"" + receiverTCP.getFileName() + "\" ... En attente");
            }

        }
        else
        {
            SenderTCP senderTCP = SenderTCP.getInstance(fileMsg.getHashcodeTCP());


            switch (senderTCP.getFileState())
            {
                case file_transferred:
                    renderer.setText(senderTCP.getFileName() + " envoyé.");
                    break;

                case error:
                    renderer.setText(senderTCP.getFileName() + " error.");
                    break;

                case sending_file:
                    float percentage = (float)senderTCP.getBytesSent()/(float)senderTCP.getFileLength() * 100;
                    renderer.setText("Envoi de \"" + senderTCP.getFileName() + "\" ... " + percentage + "%");
                    break;

                case file_refused:
                    renderer.setText(senderTCP.getFileName() + " refusé.");
                    break;
            }
        }
        return renderer;
    }
}