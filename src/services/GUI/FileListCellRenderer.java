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
    private JLabel renderer;
    private Model.FileMsg fileMsg;
    private final Color green = new Color(50, 200, 50);
    private final Color red = new Color (200, 50, 50);
    private final Color yellow = new Color(150, 150, 10);
    private final Color blue = new Color (50, 50, 200);

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
                float received = receiverTCP.getBytesReceived();
                float total = receiverTCP.getFileLength();
                if (total==received)
                {
                    renderer.setText("Fichier reçu : " + receiverTCP.getFileName());
                    renderer.setForeground(green);
                }
                else
                {
                    float percentage = (float)receiverTCP.getBytesReceived() / (float)receiverTCP.getFileLength() * 100;
                    renderer.setText("Réception de \"" + receiverTCP.getFileName() + "\" ... " + percentage + "%");
                    renderer.setForeground(blue);
                }
            }

            else
            {
                renderer.setText("Réception de \"" + receiverTCP.getFileName() + "\" ... En attente");
                renderer.setForeground(yellow);
            }

        }
        else
        {
            SenderTCP senderTCP = SenderTCP.getInstance(fileMsg.getHashcodeTCP());


            switch (senderTCP.getFileState())
            {
                case file_transferred:
                    renderer.setText("Envoyé : " + senderTCP.getFileName());
                    renderer.setForeground(green);
                    break;

                case error:
                    renderer.setText("Error : " + senderTCP.getFileName());
                    renderer.setForeground(red);
                    break;

                case sending_file:
                    float percentage = (float)senderTCP.getBytesSent()/(float)senderTCP.getFileLength() * 100;
                    renderer.setText("Envoi de \"" + senderTCP.getFileName() + "\" ... " + percentage + "%");
                    renderer.setForeground(blue);
                    break;

                case file_refused:
                    renderer.setText("Fichier refusé :" + senderTCP.getFileName());
                    renderer.setForeground(red);
                    break;

                case waiting_for_accept:
                    renderer.setText("Envoie en attente :" + senderTCP.getFileName());
                    renderer.setForeground(yellow);
                    break;
            }
        }
        return renderer;
    }
}