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
                    indelingList.add(new ToernooiTafelIndeling(inschrijving.getToernooiID(), inschrijving.getSpelerID(), 0,0));
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
        if(!hasIndeling){
            buttonOK.setText("Opslaan");
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
                int spinnerValue = (Integer) tafelSpinner.getValue();
                double spelerAantal = indelingList.size();
                double printedValue = spelerAantal / spinnerValue;
                if(printedValue%1 == 0){
                    aantalSpelerPerTafel.setForeground(Color.black);
                }else{
                    aantalSpelerPerTafel.setForeground(Color.red);
                }
                aantalSpelerPerTafel.setText("" + printedValue);
                if(indelingList.size()%2 == 0){
                    fillList();
                }
            }
        });

        fillList();
    }



    private void fillList() {
        listModel.clear();
        int aantalSpelersPerTafel = (indelingList.size() / (Integer) tafelSpinner.getValue());
        if (!hasIndeling)
            Collections.shuffle(indelingList);

        for (int i = 0; i < indelingList.size(); i++) {
            ToernooiTafelIndeling s = indelingList.get(i);
            s.setStoelNr(i%aantalSpelersPerTafel+1);

            try {
                listModel.addElement(String.format("Stoel %s: [%s]", i%aantalSpelersPerTafel+1, s.getSpeler().toString()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
            int spelerCounter = 0;
            int tafelNummer = 0;
            int loopCounter = 0;
            for(ToernooiTafelIndeling toernooiTafelIndeling : indelingList){
                if(spelerCounter == (int) aantalSpelersPerTafel){
                    spelerCounter = 0;
                    tafelNummer++;
                }
                listModel.setElementAt("Tafel " + (tafelNummer+1) + ", " + listModel.getElementAt(loopCounter),loopCounter);
                loopCounter++;
                spelerCounter++;
            }
    }

    private void onOK() {
        if (!hasIndeling) {
            boolean goedOpgeslagen = true;
            int value = (Integer) tafelSpinner.getValue();
            if (lTafelIndeling.getModel().getSize() / value > 10) {
                JOptionPane.showMessageDialog(contentPane, "Het aantal spelers per tafel mag niet meer dan 10 zijn.", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
            }else if(lTafelIndeling.getModel().getSize()%value!=0) {
                JOptionPane.showMessageDialog(contentPane, "Het aantal spelers per tafel moet een heel getal zijn.", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
            }else{
            try {
                DatabaseHelper.creeerTafels(value, this.ToernooiID);
            } catch (SQLException error) {
                error.printStackTrace();
            }
            int loopCounter = 0;
            int tafelPos = 0;
            double aantalSpelersPerTafel = Double.parseDouble(aantalSpelerPerTafel.getText());
                for(ToernooiTafelIndeling toernooiTafelIndeling : indelingList){
                    if(loopCounter == (int) aantalSpelersPerTafel){
                        tafelPos++;
                        loopCounter = 0;
                    }
                    try {
                        toernooiTafelIndeling.setTafelID(DatabaseHelper.verkrijgTafelsByToernooiID(ToernooiID).get(tafelPos).getID());
                    }catch(SQLException error){
                        JOptionPane.showMessageDialog(contentPane, "Database fout! Error stacktrace: \n" + error.getStackTrace(), "Fout", JOptionPane.ERROR_MESSAGE);
                    }
                    loopCounter++;
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
