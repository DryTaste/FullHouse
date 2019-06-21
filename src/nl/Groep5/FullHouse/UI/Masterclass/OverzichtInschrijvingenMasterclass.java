package nl.Groep5.FullHouse.UI.Masterclass;

import nl.Groep5.FullHouse.database.impl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

public class OverzichtInschrijvingenMasterclass extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JList lSpelersNietBetaald;
    private JList lSpelersBetaald;
    private JCheckBox cbBetaald;
    private JLabel lblSpeler;
    private JButton btnSpelerUitschrijven;

    private List<InschrijvingMasterclass> inschrijvingenMasterclass;

    private DefaultListModel<SpelerInschrijvingOverzicht> betaald = new DefaultListModel<>();
    private DefaultListModel<SpelerInschrijvingOverzicht> nietbetaald = new DefaultListModel<>();

    private SpelerInschrijvingOverzicht geselecteerdeInschrijving = null;
    private boolean isUpdating = false;

    public OverzichtInschrijvingenMasterclass(MasterClass masterClass) throws SQLException {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle("Overzicht inschrijvingen masterclass (" + masterClass.getID() + ")");

        buttonOK.addActionListener(e -> onOK());


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        inschrijvingenMasterclass = masterClass.getInschrijvingen();

        lSpelersBetaald.setModel(betaald);
        lSpelersNietBetaald.setModel(nietbetaald);

        repopulateListboxes();

        lSpelersBetaald.addListSelectionListener(e -> {
            if(!isUpdating && lSpelersBetaald.getSelectedValue() != null) {
                geselecteerdeInschrijving = betaald.elementAt(lSpelersBetaald.getSelectedIndex());
                updateGeselecteerdeInschrijving();
                lSpelersNietBetaald.clearSelection();
            }
        });

        lSpelersNietBetaald.addListSelectionListener(e -> {
            if(!isUpdating && lSpelersNietBetaald.getSelectedValue() != null) {
                geselecteerdeInschrijving = nietbetaald.elementAt(lSpelersNietBetaald.getSelectedIndex());
                updateGeselecteerdeInschrijving();
                lSpelersBetaald.clearSelection();
            }
        });

        this.cbBetaald.addActionListener(l -> {
            if(geselecteerdeInschrijving != null){
                geselecteerdeInschrijving.getInschrijving().setBetaald(this.cbBetaald.isSelected());
                try {
                    if(!geselecteerdeInschrijving.getInschrijving().Update()){
                        this.cbBetaald.setSelected(!this.cbBetaald.isSelected());
                        JOptionPane.showMessageDialog(contentPane, "Er is een fout opgetreden tijdens het updaten van inschrijving", "Fout", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(contentPane, "Er is een fout opgetreden tijdens het updaten van inschrijving", "Fout", JOptionPane.ERROR_MESSAGE);
                }
                repopulateListboxes();
            }
        });

        this.btnSpelerUitschrijven.addActionListener(e -> {
            if(geselecteerdeInschrijving != null){
                try {
                    if(geselecteerdeInschrijving.getInschrijving().Delete()) {
                        inschrijvingenMasterclass.remove(geselecteerdeInschrijving.getInschrijving());
                        geselecteerdeInschrijving = null;
                        repopulateListboxes();
                        JOptionPane.showMessageDialog(contentPane, "Speler succesvol uitgeschreven", "succes", JOptionPane.INFORMATION_MESSAGE);
                    }else{
                        throw new SQLException("Fout met uitschrijven, al verwijderd (placeholdertext)");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(contentPane, "Er is een fout opgetreden tijdens het verwijderen van de inschrijving", "Fout", JOptionPane.ERROR_MESSAGE);
                }
            }else{
                JOptionPane.showMessageDialog(contentPane, "Er is geen speler geselecteerd", "Fout", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private void repopulateListboxes(){
        isUpdating = true;

        betaald.clear();
        nietbetaald.clear();

        inschrijvingenMasterclass.forEach(it -> {
            try {
                if(it.isBetaald())
                    betaald.addElement(new SpelerInschrijvingOverzicht(it, it.getSpeler()));
                else
                    nietbetaald.addElement(new SpelerInschrijvingOverzicht(it, it.getSpeler()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        isUpdating = false;
    }

    private void updateGeselecteerdeInschrijving(){
        if(geselecteerdeInschrijving != null) {
            this.cbBetaald.setSelected(geselecteerdeInschrijving.getInschrijving().isBetaald());
            lblSpeler.setText(geselecteerdeInschrijving.getSpeler().toString());
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void show(MasterClass masterClass) throws SQLException {
        OverzichtInschrijvingenMasterclass dialog = new OverzichtInschrijvingenMasterclass(masterClass);
        dialog.setMinimumSize(new Dimension(500, 400));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
}

class SpelerInschrijvingOverzicht{
    private InschrijvingMasterclass inschrijving;
    private Speler speler;

    public SpelerInschrijvingOverzicht(InschrijvingMasterclass inschrijving, Speler speler) {
        this.inschrijving = inschrijving;
        this.speler = speler;
    }

    public InschrijvingMasterclass getInschrijving() {
        return inschrijving;
    }

    public Speler getSpeler() {
        return speler;
    }

    @Override
    public String toString() {
        return speler.toString();
    }
}
