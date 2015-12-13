package services.GUI;

import services.Model;
import services.network.tcp.ReceiverTCP;
import services.network.tcp.SenderTCP;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Admin on 13/12/2015.
 */
public class UserListCellRenderer extends DefaultListCellRenderer{
    private ImageIcon connectedIcon;
    private ImageIcon disconnectedIcon;

    public UserListCellRenderer() {
        super();
        connectedIcon =  new ImageIcon("res/connected.png");
        disconnectedIcon =  new ImageIcon("res/disconnected.png");
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel renderer;
        Model.User user;

        renderer = (JLabel) super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);


        user = (Model.User) value;

        if (user.isConnected()) {
            renderer.setText(user.getNickname());
            renderer.setIcon(connectedIcon);
        }
        else
        {
            renderer.setText(user.getNickname());
            renderer.setIcon(disconnectedIcon);
        }

        return renderer;
    }
}
