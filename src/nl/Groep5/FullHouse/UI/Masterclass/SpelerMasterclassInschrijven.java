package nl.Groep5.FullHouse.UI.Masterclass;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.UI.TextFieldWithPlaceholder;
import nl.Groep5.FullHouse.UI.Toernooi.SpelerToernooiInschrijven;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.impl.InschrijvingToernooi;
import nl.Groep5.FullHouse.database.impl.MasterClass;
import nl.Groep5.FullHouse.database.impl.Speler;
import nl.Groep5.FullHouse.database.impl.Toernooi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SpelerMasterclassInschrijven extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox cbBetaald;
    private JPanel pSpeler;
    private JList lSpelers;
    private TextFieldWithPlaceholder txtFilterSpeler;
    private JPanel pMasterclass;
    private JList lMasterclass;
    private TextFieldWithPlaceholder txtFilterMasterclass;


    private Speler speler;
    private MasterClass masterClass;

    private List<Speler> spelers;
    private List<MasterClass> masterClassen;

    public SpelerMasterclassInschrijven(List<Speler> spelers, List<MasterClass> masterClassen, Speler speler, MasterClass masterClass) {
        this.speler = speler;
        this.masterClass = masterClass;


        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> {
            onOK();
            this.speler = speler;
            this.masterClass = masterClass;
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        DefaultListModel<Speler> spelerDefaultListModel = new DefaultListModel<>();
        DefaultListModel<MasterClass> masterClassDefaultListModel = new DefaultListModel<>();

        lSpelers.setModel(spelerDefaultListModel);
        lMasterclass.setModel(masterClassDefaultListModel);


        spelers = spelers.stream().filter(s -> !s.getVoornaam().equals("[verwijderd]")).collect(Collectors.toList());
        masterClassen = masterClassen.stream().filter(t -> t.getDatum().toLocalDate().isAfter(LocalDate.now())).collect(Collectors.toList());

        this.spelers = spelers;
        this.masterClassen = masterClassen;


        spelers.forEach(spelerDefaultListModel::addElement);
        masterClassen.forEach(masterClassDefaultListModel::addElement);

        // Filter spelers
        List<Speler> finalSpelers = spelers;
        txtFilterSpeler.addCaretListener(e -> {
            DefaultListModel<Speler> spelerFilterModel = new DefaultListModel<>();

            finalSpelers.forEach(s -> {
                if(s.getVoornaam().contains(txtFilterSpeler.getText()) || s.getAchternaam().contains(txtFilterSpeler.getText())){
                    spelerFilterModel.addElement(s);
                }
            });

            lSpelers.setModel(spelerFilterModel);
        });

        //Filter masterClass
        List<MasterClass> finalToeornooien = masterClassen;
        txtFilterMasterclass.addCaretListener(e -> {
            DefaultListModel<MasterClass> masterclassFilterModel = new DefaultListModel<>();

            finalToeornooien.forEach(t -> {
                if(String.valueOf(t.getID()).contains(txtFilterMasterclass.getText()) || t.getDatum().toString().contains(txtFilterMasterclass.getText())){
                    masterclassFilterModel.addElement(t);
                }
            });

            lMasterclass.setModel(masterclassFilterModel);
        });


        if (speler == null && masterClass != null) {
            pMasterclass.setVisible(false);
        } else if (speler != null && masterClass == null) {
            pSpeler.setVisible(false);
        }

        if (masterClass != null) {
            if (masterClass.isVol()) {
                JOptionPane.showMessageDialog(contentPane, "Dit Masterclass zit vol !!", "Vol", JOptionPane.INFORMATION_MESSAGE);
                onCancel();
            }
        }
    }

    private void onOK() {
        if(masterClass == null){
            masterClass = (MasterClass) lMasterclass.getModel().getElementAt(lMasterclass.getSelectedIndex());
        }
        if(speler == null){
            speler = (Speler) lSpelers.getModel().getElementAt(lSpelers.getSelectedIndex());
        }

        if(masterClass == null || speler == null){
            JOptionPane.showMessageDialog(contentPane, "Er is geen speler of MasterClass geselecteerd, probeer het opnieuw te selecteren.", "Woeps", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (masterClass.isVol()) {
            JOptionPane.showMessageDialog(contentPane, "Dit masterClass zit vol !!", "Vol", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if(speler.getRating() < masterClass.getMinRating()){
            JOptionPane.showMessageDialog(contentPane, "De rating van de speler is te laag en mag niet meedoen !", "Te lage rating", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try{
            List<InschrijvingToernooi> inschrijvingToernooi = DatabaseHelper.verkrijgLijstMetToernooiInschrijvingen(speler);

            List<MasterClass> masterClassWaarSpelerInZit = masterClassen.stream().filter(
                    t -> inschrijvingToernooi.stream().anyMatch(in -> in.getToernooiID() == t.getID()) // filter alle masterClassen waar de speler in zit
            ).collect(Collectors.toList());

            // Kijk of speler in masterClass zit
            if(masterClassWaarSpelerInZit.stream().map(MasterClass::getID).anyMatch(t -> t == masterClass.getID())){
                JOptionPane.showMessageDialog(contentPane, "De speler zit al in deze masterClass !!!", "Doet al mee", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // kijk of speler in een ander masterClass zit van de zelfde datum
            if(masterClassWaarSpelerInZit.stream().anyMatch(t -> t.getDatum().equals(masterClass.getDatum()))){
                JOptionPane.showMessageDialog(contentPane, "De speler zit al in een ander masterClass dat op die dag plaats vind !!!", "Kan niet meer mee doen", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DatabaseHelper.registreerSpelerVoorMasterclass(masterClass, speler, cbBetaald.isSelected());
            JOptionPane.showMessageDialog(contentPane, "Speler ingeschreven", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Er is een fout opgetreden met het registeren van de speler op het masterClass.", "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void show(MasterClass masterClass) throws SQLException{
        SpelerMasterclassInschrijven dialog = new SpelerMasterclassInschrijven(DatabaseHelper.verkrijgAlleSpelers(), DatabaseHelper.verkrijgMasterClasses(), null, masterClass);
        dialog.setMinimumSize(new Dimension(500, 500));
        dialog.setVisible(true);
    }

    public static void show(Speler speler) throws SQLException{
        SpelerMasterclassInschrijven dialog = new SpelerMasterclassInschrijven(DatabaseHelper.verkrijgAlleSpelers(), DatabaseHelper.verkrijgMasterClasses(), speler, null);
        dialog.setMinimumSize(new Dimension(500, 500));
        dialog.setVisible(true);
    }
}
