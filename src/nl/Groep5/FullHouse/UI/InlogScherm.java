package nl.Groep5.FullHouse.UI;
import nl.Groep5.FullHouse.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InlogScherm implements ActionListener {
    private JFrame frame;

    private JPanel mainPanel;
    private JTextField txtUser;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public InlogScherm() {
        frame = new JFrame("FullHouse login");
        frame.add(mainPanel);
        frame.setSize(410, 130);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        txtPassword.addActionListener(this);
        btnLogin.addActionListener(this);


        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            PreparedStatement ps = Main.getMySQLConnection().prepareStatement("select gebruikersnaam, wachtwoord, rechten from medewerker where gebruikersnaam = ? and wachtwoord = ?");
            ps.setString(1, txtUser.getText());
            ps.setString(2, new String(txtPassword.getPassword()));

            ResultSet rs = Main.getMySQLConnection().query(ps);
            if(rs.next()) {
                switch (rs.getInt("rechten")) {
                    case 0:
                        JOptionPane.showMessageDialog(frame, "U bezit onvoldoende rechten!", "Toegang geweigerd",JOptionPane.WARNING_MESSAGE);
                        break;
                    case 1:
                        new MainScherm();
                        frame.dispose();
                        break;
                    case 2:
                        new MainScherm();
                        frame.dispose();
                        break;
                }
            }else{
                JOptionPane.showMessageDialog(frame, "Gebruikersnaam of wachtwoord is incorrect!","Onbekende gebruiker", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }
}
