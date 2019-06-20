package nl.Groep5.FullHouse.UI.Toernooi;

import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.impl.InschrijvingToernooi;
import nl.Groep5.FullHouse.database.impl.Toernooi;
import nl.Groep5.FullHouse.database.impl.ToernooiTafelIndeling;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ToernooiTafelIndelingScherm extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList lTafelIndeling;
    private JButton btnOpnieuw;

    private List<ToernooiTafelIndeling> indelingList = new ArrayList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private boolean hasIndeling = false;

    public ToernooiTafelIndelingScherm(Toernooi toernooi) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Toernooi tafelindeling: " + toernooi.getNaam());
        pack();

        buttonOK.addActionListener(e -> onOK());
        btnOpnieuw.addActionListener(e -> fillList());

        lTafelIndeling.setModel(listModel);

        try {
            indelingList = DatabaseHelper.verkrijgTafelIndelingVanToernooi(toernooi);

            if (indelingList == null || indelingList.isEmpty()) {
                for (InschrijvingToernooi inschrijving : toernooi.getInschrijvingen()) {
                    indelingList.add(new ToernooiTafelIndeling(inschrijving.getToernooiID(), inschrijving.getSpelerID(), 0));
                }
            } else {
                hasIndeling = true;
                btnOpnieuw.setEnabled(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Er is een fout opgetreden met het laden van de inschrijvingen", "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        fillList();
    }

    private void fillList() {
        listModel.clear();

        if (!hasIndeling)
            Collections.shuffle(indelingList);

        for (int i = 0; i < indelingList.size(); i++) {
            ToernooiTafelIndeling s = indelingList.get(i);
            s.setStoelNr(i);

            try {
                listModel.addElement(String.format("Stoel %s: [%s]", i, s.getSpeler().toString()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void onOK() {
        if (!hasIndeling) {
            boolean goedOpgeslagen = true;
            for (ToernooiTafelIndeling toernooiTafelIndeling : indelingList) {
                try {
                    boolean isOpgeslagen = toernooiTafelIndeling.UpdateAnderSave();

                    if (!isOpgeslagen) goedOpgeslagen = false;

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (!goedOpgeslagen) {
                JOptionPane.showMessageDialog(this, "Er is wat fout gegaan tijdens het opslaan van de tafelindeling !", "", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        dispose();
    }

    public static void show(Toernooi toernooi) {
        ToernooiTafelIndelingScherm dialog = new ToernooiTafelIndelingScherm(toernooi);
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
