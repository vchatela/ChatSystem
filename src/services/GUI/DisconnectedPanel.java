package services.GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Lucas on 31/10/2015.
 */
public class DisconnectedPanel extends Panel{
    private TextField usernameTextFiel;
    private Label userNameLabel;
    private Button connectButton;

    public DisconnectedPanel()
    {
        setLayout(new GridLayout(2, 2));
        userNameLabel = new Label("Username :");
        add(userNameLabel);
        usernameTextFiel = new TextField(""); // construct TextField
        usernameTextFiel.setSize(100, 20);
        usernameTextFiel.setMinimumSize(new Dimension(100, 20));
        usernameTextFiel.setMaximumSize(new Dimension(100, 20));
        add(usernameTextFiel);
        connectButton = new Button("Connect");
        add(connectButton);
        setSize(300, 100);
    }

    public void addActionListener(ActionListener a)
    {
        connectButton.addActionListener(a);
    }

    public String getUserName()
    {
        return usernameTextFiel.getText();
    }
}
