package services.GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Lucas on 31/10/2015.
 *
 */
public class DisconnectedFrame extends JFrame{
    private JTextField usernameTextFiel;
    private JLabel userNameLabel;
    private JButton connectButton;

    public DisconnectedFrame()
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        setSize(width/2, height/2);
        this.setLocationRelativeTo(null);

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        ImageIcon imageLogo = new ImageIcon("res/logo.png");

        ImageIcon imageINSA = new ImageIcon("res/bandeau.png");
        JLabel jLabelImageLogo = new JLabel();
        JLabel jLabelImageINSA = new JLabel();
        jLabelImageLogo.setIcon(imageLogo);
        jLabelImageINSA.setIcon(imageINSA);

        constraints.gridx=0;
        constraints.gridy=0;
        constraints.gridwidth=3;
        add(jLabelImageINSA, constraints);

        constraints.gridx=1;
        constraints.gridy=1;
        constraints.weighty = 1.0;
        add(jLabelImageLogo,constraints);


        userNameLabel = new JLabel("Username");

        constraints.gridx=1;
        constraints.gridy=2;
        constraints.weighty = 1.0;
        add(userNameLabel,constraints);

        usernameTextFiel = new JTextField("");
        userNameLabel.setSize(100,100);
        constraints.gridx=1;
        constraints.gridy=3;
        constraints.gridwidth=2;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(usernameTextFiel,constraints);


        connectButton = new JButton("Connect");
        constraints.gridx=1;
        constraints.gridy=4;
        add(connectButton,constraints);

        setSize(500, 700);
        this.setIconImage(new ImageIcon("res/logo.png").getImage());

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int answer = JOptionPane.showConfirmDialog(null, "You want to quit?", "Quit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    public String getUserName()
    {
        return usernameTextFiel.getText();
    }

    public void addActionListener(ActionListener actionListener) {
        connectButton.addActionListener(actionListener);
    }
    public void addActionUsername(ActionListener actionListener){
        usernameTextFiel.addActionListener(actionListener);

    }
}