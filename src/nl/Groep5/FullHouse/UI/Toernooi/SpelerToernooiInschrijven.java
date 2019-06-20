package nl.Groep5.FullHouse.UI.Toernooi;

import nl.Groep5.FullHouse.UI.TextFieldWithPlaceholder;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.impl.InschrijvingToernooi;
import nl.Groep5.FullHouse.database.impl.Speler;
import nl.Groep5.FullHouse.database.impl.Toernooi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class SpelerToernooiInschrijven extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList lSpelers;
    private JList lToernooien;
    private TextFieldWithPlaceholder txtFilterSpeler;
    private TextFieldWithPlaceholder txtFilterToernooi;
    private JPanel pToernooi;
    private JPanel pSpeler;
    private JCheckBox cbBetaald;


    private Speler speler;
    private Toernooi toernooi;

    private List<Speler> spelers;
    private List<Toernooi> toernooien;


    public SpelerToernooiInschrijven(List<Speler> spelers, List<Toernooi> toernooien, Speler speler, Toernooi toernooi) {
        this.speler = speler;
        this.toernooi = toernooi;


        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Speler inschrijven voor toernooi");

        buttonOK.addActionListener(e -> {
            onOK();
            this.speler = speler;
            this.toernooi = toernooi;
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
        DefaultListModel<Toernooi> toernooiDefaultListModel = new DefaultListModel<>();

        lSpelers.setModel(spelerDefaultListModel);
        lToernooien.setModel(toernooiDefaultListModel);


        spelers = spelers.stream().filter(s -> !s.getVoornaam().equals("[verwijderd]")).collect(Collectors.toList());
        toernooien = toernooien.stream().filter(t -> t.getUitersteInschrijfDatum().toLocalDate().isAfter(LocalDate.now())).collect(Collectors.toList());

        this.spelers = spelers;
        this.toernooien = toernooien;


        spelers.forEach(spelerDefaultListModel::addElement);
        toernooien.forEach(toernooiDefaultListModel::addElement);

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

        //Filter toernooi
        List<Toernooi> finalToeornooien = toernooien;
        txtFilterToernooi.addCaretListener(e -> {
            DefaultListModel<Toernooi> toernooiFilterModel = new DefaultListModel<>();

            finalToeornooien.forEach(t -> {
                if(t.getNaam().contains(txtFilterToernooi.getText()) || t.getDatum().toString().contains(txtFilterToernooi.getText())){
                    toernooiFilterModel.addElement(t);
                }
            });

            lToernooien.setModel(toernooiFilterModel);
        });


        if (speler == null && toernooi != null) {
            pToernooi.setVisible(false);
        } else if (speler != null && toernooi == null) {
            pSpeler.setVisible(false);
        }

        if (toernooi != null) {
            if (toernooi.isVol()) {
                JOptionPane.showMessageDialog(contentPane, "Dit toernooi zit vol !!", "Vol", JOptionPane.INFORMATION_MESSAGE);
                onCancel();
            }
        }
    }

    private void onOK() {
        if(toernooi == null){
            toernooi = (Toernooi) lToernooien.getModel().getElementAt(lToernooien.getSelectedIndex());
        }
        if(speler == null){
            speler = (Speler) lSpelers.getModel().getElementAt(lSpelers.getSelectedIndex());
        }

        if(toernooi == null || speler == null){
            JOptionPane.showMessageDialog(contentPane, "Er is geen speler of toernooi geselecteerd, probeer het opnieuw te selecteren.", "Woeps", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (toernooi.isVol()) {
            JOptionPane.showMessageDialog(contentPane, "Dit toernooi zit vol !!", "Vol", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try{
            List<InschrijvingToernooi> inschrijvingToernooi = DatabaseHelper.verkrijgLijstMetToernooiInschrijvingen(speler);

            List<Toernooi> toernooienWaarSpelerInZit = toernooien.stream().filter(
                    t -> inschrijvingToernooi.stream().anyMatch(in -> in.getToernooiID() == t.getID()) // filter alle toernooien waar de speler in zit
            ).collect(Collectors.toList());

            // Kijk of speler in toernooi zit
            if(toernooienWaarSpelerInZit.stream().map(Toernooi::getID).anyMatch(t -> t == toernooi.getID())){
                JOptionPane.showMessageDialog(contentPane, "Deze speler zit al in dit toernooi", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // kijk of speler in een ander toernooi zit van de zelfde datum
            if(toernooienWaarSpelerInZit.stream().anyMatch(t -> t.getDatum().equals(toernooi.getDatum()))){
                JOptionPane.showMessageDialog(contentPane, "Deze speler zit al in een ander toernooi dat op de zelfde dag plaats vind", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DatabaseHelper.registreerSpelerVoorToernooi(toernooi, speler, cbBetaald.isSelected());
            JOptionPane.showMessageDialog(contentPane, "Speler succesvol ingeschreven", "Bericht", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }catch (SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(contentPane, "Er is een fout opgetreden met het registeren van de speler op het toernooi.", "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        dispose();
    }

    public static void show(Toernooi toernooi) throws SQLException{
        SpelerToernooiInschrijven dialog = new SpelerToernooiInschrijven(DatabaseHelper.verkrijgAlleSpelers(), DatabaseHelper.verkrijgToernooien(), null, toernooi);
        dialog.setMinimumSize(new Dimension(500, 500));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void show(Speler speler) throws SQLException{
        SpelerToernooiInschrijven dialog = new SpelerToernooiInschrijven(DatabaseHelper.verkrijgAlleSpelers(), DatabaseHelper.verkrijgToernooien(), speler, null);
        dialog.setMinimumSize(new Dimension(500, 500));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}
