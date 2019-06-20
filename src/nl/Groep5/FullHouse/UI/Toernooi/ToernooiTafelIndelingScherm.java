package nl.Groep5.FullHouse.UI.Toernooi;

import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.impl.InschrijvingToernooi;
import nl.Groep5.FullHouse.database.impl.Toernooi;
import nl.Groep5.FullHouse.database.impl.ToernooiTafelIndeling;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private JLabel indelingLabel;
    private JSpinner tafelSpinner;
    private JLabel aantalSpelerPerTafel;

    private List<ToernooiTafelIndeling> indelingList = new ArrayList<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private boolean hasIndeling = false;
    private int ToernooiID;

    public ToernooiTafelIndelingScherm(Toernooi toernooi) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Toernooi tafelindeling: " + toernooi.getNaam());
        pack();
        this.ToernooiID = toernooi.getID();

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

        try {
            for(int i = 1;;i++){
                if(DatabaseHelper.verkrijgLijstMetToernooiInschrijvingen(toernooi).size() / i == 2 && DatabaseHelper.verkrijgLijstMetToernooiInschrijvingen(toernooi).size()%2 == 0){
                    SpinnerNumberModel tafelSpinnerModel = new SpinnerNumberModel(1,1,i+1,1);
                    tafelSpinner.setModel(tafelSpinnerModel);
                    int value = (Integer) tafelSpinner.getValue();
                    aantalSpelerPerTafel.setText("" + (indelingList.size() / value));
                    break;
                }else if(DatabaseHelper.verkrijgLijstMetToernooiInschrijvingen(toernooi).size() / i == 2 && DatabaseHelper.verkrijgLijstMetToernooiInschrijvingen(toernooi).size()%2 != 0){
                    SpinnerNumberModel tafelSpinnerModel = new SpinnerNumberModel(1,1,i-1,1);
                    tafelSpinner.setModel(tafelSpinnerModel);
                    int value = (Integer) tafelSpinner.getValue();
                    aantalSpelerPerTafel.setText("" + (indelingList.size() / value));
                    break;
                }
            }
        }catch(Exception error){

        }
        try {
            if (hasIndeling) {
                tafelSpinner.setEnabled(false);
                tafelSpinner.setValue(DatabaseHelper.verkrijgTafelsByToernooiID(toernooi.getID()).size());
                aantalSpelerPerTafel.setText("" + (indelingList.size() / DatabaseHelper.verkrijgTafelsByToernooiID(toernooi.getID()).size()));
            }
        }catch(SQLException error){
            error.printStackTrace();
        }
        tafelSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (Integer) tafelSpinner.getValue();
                aantalSpelerPerTafel.setText("" + (indelingList.size() / value));
            }
        });

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
            int value = (Integer) tafelSpinner.getValue();
            if (lTafelIndeling.getModel().getSize() / value >= 10) {
                JOptionPane.showMessageDialog(contentPane, "Het aantal spelers per tafel mag niet meer dan 10 zijn.", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
            }else{
            try {
                DatabaseHelper.creeerTafels(value, this.ToernooiID);
            } catch (SQLException error) {
                error.printStackTrace();
            }
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
                dispose();
        }
        }else{
            dispose();
        }



    }

    public static void show(Toernooi toernooi) {
        ToernooiTafelIndelingScherm dialog = new ToernooiTafelIndelingScherm(toernooi);
        dialog.setMinimumSize(new Dimension(400, 300));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
