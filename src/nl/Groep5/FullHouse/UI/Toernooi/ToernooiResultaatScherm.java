package nl.Groep5.FullHouse.UI.Toernooi;

import nl.Groep5.FullHouse.database.impl.InschrijvingToernooi;
import nl.Groep5.FullHouse.database.impl.Speler;
import nl.Groep5.FullHouse.database.impl.Toernooi;
import nl.Groep5.FullHouse.database.impl.ToernooiUitkomst;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class ToernooiResultaatScherm extends JDialog implements ListSelectionListener {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList listSpelers;
    private JLabel lGeselecteerdeSpeler;
    private JRadioButton rW1;
    private JRadioButton rW2;
    private JLabel lWinnaar1;
    private JLabel lWinnaar2;
    private JSpinner sW1;
    private JSpinner sW2;
    private JLabel lToernooiGeld;


    public ToernooiResultaatScherm(Toernooi toernooi) {
        DefaultListModel<SpelerInschrijving> spelerListModel = new DefaultListModel<>();

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Fullhouse - Toernooi winnaars verwerken");

        buttonOK.addActionListener(e -> onOK(spelerListModel, toernooi));

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

        try {
            DecimalFormat df = new DecimalFormat("#.##");
            double totaalGeld = toernooi.getInleg() * toernooi.getInschrijvingen().size();
            lToernooiGeld.setText("Totale inleggeld: €" + df.format(totaalGeld));


            for (InschrijvingToernooi inschrijving : toernooi.getInschrijvingen()) {
                spelerListModel.addElement(new SpelerInschrijving(inschrijving.getSpeler()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Er is een fout opgetreden met het laden van de spelers, probeer het later opnieuw.", "Laad fout", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }


        listSpelers.setModel(spelerListModel);
        listSpelers.addListSelectionListener(this);
        rW1.addActionListener(e -> {
            try{
                SpelerInschrijving si = (SpelerInschrijving) listSpelers.getModel().getElementAt(listSpelers.getSelectedIndex());

                for (int i = 0; i < listSpelers.getModel().getSize(); i++) {
                    SpelerInschrijving sii = (SpelerInschrijving) listSpelers.getModel().getElementAt(i);
                    sii.setWinnaar1(false);
                }

                si.setWinnaar1(true);
                si.setWinnaar2(false);
                rW2.setSelected(false);
            }catch (Exception ex){
                rW1.setSelected(false);
            }
        });

        rW2.addActionListener(e -> {
            try{
                SpelerInschrijving si = (SpelerInschrijving) listSpelers.getModel().getElementAt(listSpelers.getSelectedIndex());

                for (int i = 0; i < listSpelers.getModel().getSize(); i++) {
                    SpelerInschrijving sii = (SpelerInschrijving) listSpelers.getModel().getElementAt(i);
                    sii.setWinnaar2(false);
                }

                si.setWinnaar2(true);
                si.setWinnaar1(false);
                rW1.setSelected(false);
            }catch (Exception ex){
                rW2.setSelected(false);
            }
        });

        sW1.setValue(40);
        sW1.addChangeListener(e -> {
            try{
                Integer.valueOf(sW1.getValue().toString());
            }catch (Exception ex){
                sW1.setValue(40);
            }
            if((int)sW1.getValue() > 100) sW1.setValue(100);

            if((int)sW1.getValue() <= (int)sW2.getValue()){
                sW1.setValue((int)sW2.getValue() + 1);
            }

        });

        sW2.setValue(25);
        sW2.addChangeListener(e -> {
            try{
                Integer.valueOf(sW1.getValue().toString());
            }catch (Exception ex){
                sW1.setValue(25);
            }
            if((int)sW1.getValue() < 0) sW1.setValue(0);

            if((int)sW2.getValue() >= (int)sW1.getValue()){
                sW2.setValue((int)sW1.getValue() - 1);
            }
        });


    }

    private void onOK(ListModel<SpelerInschrijving> list, Toernooi toernooi) {

        Speler spelerWinnaar1 = null, spelerWinnaar2 = null;
        for (int i = 0; i < list.getSize(); i++) {
            SpelerInschrijving si = list.getElementAt(i);

            if(si.isWinnaar1()) spelerWinnaar1 = si.getSpeler();
            if(si.isWinnaar2()) spelerWinnaar2 = si.getSpeler();
        }

        if(spelerWinnaar1 == null || spelerWinnaar2 == null){
            JOptionPane.showMessageDialog(this, "Er is geen Winnaar 1 en/of Winnaar 2", "Woeps", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double totaalGeld = toernooi.getInleg() * toernooi.getInschrijvingen().size();

            // 40 / 100 = 0.4
            double prijs1 = totaalGeld * (Double.parseDouble(sW1.getValue().toString())/ 100);
            double prijs2 = totaalGeld * (Double.parseDouble(sW2.getValue().toString()) / 100);

            ToernooiUitkomst tuw1 = new ToernooiUitkomst(spelerWinnaar1.getID(), toernooi.getID(), 1, prijs1);
            ToernooiUitkomst tuw2 = new ToernooiUitkomst(spelerWinnaar2.getID(), toernooi.getID(), 2, prijs2);

            tuw1.Save();
            tuw2.Save();

            DecimalFormat df = new DecimalFormat("#.##");
            JOptionPane.showMessageDialog(this,
                    String.format("Speler %s heeft %s en speler %s heeft %s ontvangen!",
                            spelerWinnaar1.getVoornaam(),
                            df.format(prijs1),
                            spelerWinnaar2.getVoornaam(),
                            df.format(prijs2)),
                    "Gelukt", JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ow nee, er is wat fout gegaan met het opslaan in het database", "Ow nee", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void show(Toernooi toernooi){
        ToernooiResultaatScherm dialog = new ToernooiResultaatScherm(toernooi);
//        dialog.pack();
        dialog.setMinimumSize(new Dimension(420, 270));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        SpelerInschrijving si = (SpelerInschrijving) listSpelers.getModel().getElementAt(listSpelers.getSelectedIndex());
        lGeselecteerdeSpeler.setText(String.format("Geselecteerde speler: %s", si.getSpeler().getVoornaam()));
        rW1.setSelected(si.isWinnaar1());
        rW2.setSelected(si.isWinnaar2());
    }
}

class SpelerInschrijving{
    private Speler speler;
    private boolean winnaar1;
    private boolean winnaar2;

    public SpelerInschrijving(Speler speler) {
        this.speler = speler;
    }

    public Speler getSpeler() {
        return speler;
    }

    public boolean isWinnaar1() {
        return winnaar1;
    }

    public void setWinnaar1(boolean winnaar1) {
        this.winnaar1 = winnaar1;
    }

    public boolean isWinnaar2() {
        return winnaar2;
    }

    public void setWinnaar2(boolean winnaar2) {
        this.winnaar2 = winnaar2;
    }

    @Override
    public String toString() {
       return speler.toString();
    }
}

