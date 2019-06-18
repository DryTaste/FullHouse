package nl.Groep5.FullHouse.UI.Toernooi;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.impl.InschrijvingToernooi;
import nl.Groep5.FullHouse.database.impl.Speler;
import nl.Groep5.FullHouse.database.impl.Toernooi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ToernooiTafelIndelingScherm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList lTafelIndeling;

    public ToernooiTafelIndelingScherm(Toernooi toernooi) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Toernooi tafelindeling: " + toernooi.getNaam());
        pack();

        buttonOK.addActionListener(e -> onOK());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        lTafelIndeling.setModel(listModel);

        try {
            List<InschrijvingToernooi> inschrijvingen = toernooi.getInschrijvingen();
            Collections.shuffle(inschrijvingen);
            for (int i = 0; i < inschrijvingen.size(); i++) {
                Speler s = inschrijvingen.get(i).getSpeler();

                listModel.addElement(String.format("Stoel %s: [%s]", i, s.toString()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void show(Toernooi toernooi) throws SQLException {
        ToernooiTafelIndelingScherm dialog = new ToernooiTafelIndelingScherm(toernooi);
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.setVisible(true);
    }
}
