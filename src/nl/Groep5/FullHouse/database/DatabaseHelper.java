package nl.Groep5.FullHouse.database;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.impl.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DeStilleGast 5-6-2019
 */
public class DatabaseHelper {

    /**
     * Verkrijg lijst met spelers
     *
     * @return List met Speler objecten
     * @throws SQLException
     */
    public static List<Speler> verkrijgAlleSpelers() throws SQLException {
        return verkrijgAlleSpelers(null);
    }

    /**
     * Verkrijg lijst met spelers waarvan de voornaam, tussenvoegsel, achternaam gefilterd zijn
     *
     * @param filter waarom de filter moet werken
     * @return List met Speler objecten die gefilted zijn
     * @throws SQLException
     */
    public static List<Speler> verkrijgAlleSpelers(String filter) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps;
        if (filter == null || filter.isEmpty()) {
            ps = mysql.prepareStatement("select * from speler");
        } else {
            ps = mysql.prepareStatement("select * from speler where voornaam like ? or tussenvoegsel like ? or achternaam like ? or id = ?");
            ps.setString(1, "%" + filter + "%");
            ps.setString(2, filter);
            ps.setString(3, "%" + filter + "%");
            ps.setString(4, filter);
        }
        ResultSet rs = mysql.query(ps);

        List<Speler> spelers = new ArrayList<>();

        while (rs.next()) {
            spelers.add(new Speler(rs));
        }

        return spelers;
    }

    public static List<BekendeSpeler> verkrijgBekendeSpelers() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps;

        ps = mysql.prepareStatement("select * from bekende_speler");

        ResultSet rs = mysql.query(ps);

        List<BekendeSpeler> bekendeSpelers = new ArrayList<>();

        while (rs.next()) {
            bekendeSpelers.add(new BekendeSpeler(rs));
        }

        return bekendeSpelers;
    }

    public static List<BekendeSpeler> verkrijgBekendeSpelers(String filter) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps;
        if (filter == null || filter.isEmpty()) {
            ps = mysql.prepareStatement("select * from bekende_speler");
        }else{
            ps = mysql.prepareStatement("select * from bekende_speler where pseudonaam like ? or id = ?");
            ps.setString(1, "%" + filter + "%");
            ps.setString(2, filter);
        }
        ResultSet rs = mysql.query(ps);

        List<BekendeSpeler> bekendeSpelers = new ArrayList<>();

        while (rs.next()) {
            bekendeSpelers.add(new BekendeSpeler(rs));
        }

        return bekendeSpelers;
    }

    public static BekendeSpeler verkrijgBekendeSpelerBijId(int id) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from bekende_speler where ID = ?");
        ps.setInt(1, id);
        ResultSet rs = mysql.query(ps);

        if (rs.next()) return new BekendeSpeler(rs);

        return null;
    }

    /**
     * Verkrijg een {@link Speler} dat bij de ID hoort
     *
     * @param id Speler ID dat gekoppeld is in de database
     * @return Als id is gevonden geeft het een {@link Speler} anders null
     * @throws SQLException
     */
    public static Speler verkrijgSpelerBijId(int id) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from speler where ID = ?");
        ps.setInt(1, id);
        ResultSet rs = mysql.query(ps);

        if (rs.next()) return new Speler(rs);

        return null;
    }

    /**
     * Verkrijg lijst met locaties
     *
     * @return Lijst met locatie objecten
     * @throws SQLException
     */
    public static List<Locatie> verkrijgLocatieLijst() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from locatie");
        ResultSet rs = mysql.query(ps);

        List<Locatie> locaties = new ArrayList<>();

        while (rs.next()) {
            locaties.add(new Locatie(rs));
        }

        return locaties;
    }

    /**
     * Verkrijg lijst met MasterClassen
     *
     * @param filter Filter bij het verkrijgen van masterclasses
     * @return Lijst met MasterClass objecten
     * @throws SQLException
     */
    public static List<MasterClass> verkrijgMasterClasses(String filter) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps;
        if (filter == null || filter.isEmpty()) {
            ps = mysql.prepareStatement("select * from masterclass");
        }else{
            ps = mysql.prepareStatement("select * from masterclass where datum like ? or beginTijd like ? or eindTijd like ? or kosten like ? or minRating like ? or maxInschrijvingen like ? or locatieID = ? or leraar like ? or ID = ?");
            ps.setString(1, "%" + filter + "%");
            ps.setString(2, filter);
            ps.setString(3, filter);
            ps.setString(4, filter);
            ps.setString(5, filter);
            ps.setString(6, filter);
            ps.setString(7, filter);
            ps.setString(8, filter);
            ps.setString(9, filter);
        }
        ResultSet rs = mysql.query(ps);

        List<MasterClass> masterClasses = new ArrayList<>();

        while (rs.next()) {
            masterClasses.add(new MasterClass(rs));
        }

        return masterClasses;
    }

    /**
     * Verkrijg lijst met MasterClassen
     *
     * @return Lijst met MasterClass objecten
     * @throws SQLException
     */
    public static List<MasterClass> verkrijgMasterClasses() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from masterclass");
        ResultSet rs = mysql.query(ps);

        List<MasterClass> masterClasses = new ArrayList<>();

        while (rs.next()) {
            masterClasses.add(new MasterClass(rs));
        }

        return masterClasses;
    }

    /**
     * Verkrijg MasterClass object met opgegeven ID, returned null als er niks gevonden is
     *
     * @param id MasterClass ID
     * @return Gevonden MasterClass, null als er niks gevonden is
     * @throws SQLException
     */
    public static MasterClass verkrijgMasterClassById(int id) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from masterclass where ID = ?");
        ps.setInt(1, id);
        ResultSet rs = mysql.query(ps);

        if (rs.next()) return new MasterClass(rs);

        return null;
    }

    /**
     * Verkrijg een lijst met inschrijvingen van het opgegeven toernooi
     *
     * @param toernooi Toernooi object
     * @return Lijst met inschrijvingen van toernooi
     * @throws SQLException
     */
    public static List<InschrijvingToernooi> VerkrijgInschrijvingenVanToernooi(Toernooi toernooi) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from inschrijving_toernooi where toernooiID = ?");
        ps.setInt(1, toernooi.getID());

        ResultSet rs = mysql.query(ps);

        List<InschrijvingToernooi> inschrijvingen = new ArrayList<>();

        while (rs.next()) {
            inschrijvingen.add(new InschrijvingToernooi(rs));
        }

        return inschrijvingen;
    }

    /**
     * Verkrijg een lijst met inschrijvingen van opgegeven masterclass
     *
     * @param masterClass Masterclass object
     * @return Lijst met Inschrijvingen van masterclass
     * @throws SQLException
     */
    public static List<InschrijvingMasterclass> VerkrijgInschrijvingenVanMasterClass(MasterClass masterClass) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from inschrijving_masterclass where masterclassID = ?");
        ps.setInt(1, masterClass.getID());

        ResultSet rs = mysql.query(ps);

        List<InschrijvingMasterclass> inschrijvingen = new ArrayList<>();

        while (rs.next()) {
            inschrijvingen.add(new InschrijvingMasterclass(rs));
        }

        return inschrijvingen;
    }

    /**
     * Verkrijg een lijst met toernooien
     *
     * @return Lijst met toernooien
     * @throws SQLException
     */
    public static List<Toernooi> verkrijgToernooien() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from toernooi");
        ResultSet rs = mysql.query(ps);

        List<Toernooi> toernooien = new ArrayList<>();

        while (rs.next()) {
            toernooien.add(new Toernooi(rs));
        }

        return toernooien;
    }

    /**
     * Verkrijg een lijst met toernooien
     *
     * @param filter waarom de filter moet werken
     * @return List met toernooi objecten die gefilted zijn
     * @throws SQLException
     */
    public static List<Toernooi> verkrijgToernooien(String filter) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps;
        if (filter == null || filter.isEmpty()) {
            ps = mysql.prepareStatement("select * from toernooi");
        } else {
            ps = mysql.prepareStatement("select * from toernooi where naam like ? or datum like ? or beginTijd like ? or eindTijd like ? or ID = ?");
            ps.setString(1, "%" + filter + "%");
            ps.setString(2, "%" + filter + "%");
            ps.setString(3, filter);
            ps.setString(4, filter);
            ps.setString(5, filter);
        }
        ResultSet rs = mysql.query(ps);

        List<Toernooi> toernooien = new ArrayList<>();

        while (rs.next()) {
            toernooien.add(new Toernooi(rs));
        }

        return toernooien;
    }

    /**
     * Verkrijg {@link Toernooi} dat bij het opgegeven ID hoort
     *
     * @param id Toernooi ID
     * @return {@link Toernooi}
     * @throws SQLException
     */
    public static Toernooi verkrijgToernooiById(int id) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from toernooi where ID = ?");
        ps.setInt(1, id);

        ResultSet rs = mysql.query(ps);

        if (rs.next()) return new Toernooi(rs);
        return null;
    }

    /**
     * Verkrijg {@link Locatie} van de opgegeven ID
     *
     * @param locatieId
     * @return {@link Locatie}
     * @throws SQLException
     */
    public static Locatie verkrijgLocatieById(int locatieId) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from locatie where ID = ?");
        ps.setInt(1, locatieId);
        ResultSet rs = mysql.query(ps);

        if (rs.next()) return new Locatie(rs);
        return null;
    }

    /**
     * Probeer een speler te registreren voor een {@link Toernooi}
     *
     * @param toernooi     Toernooi voor de speler om te registeren
     * @param speler       Speler dat zich wilt registeren voor Toernooi
     * @param heeftBetaald Of de speler al betaald heeft voor het toernooi
     * @return true als de registratie voltooid is, false registratie niet gelukt is (bijvoorbeeld vol)
     * @throws SQLException
     */
    public static boolean registreerSpelerVoorToernooi(Toernooi toernooi, Speler speler, boolean heeftBetaald) throws SQLException {
        if (toernooi.isVol()) {
            // De inschrijvingen zijn vol, geen vrije plekken beschikbaar
            return false;
        }

        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `inschrijving_toernooi` (`spelerID`, `toernooiID`, `betaald`) VALUES (?, ?, ?)");
        ps.setInt(1, speler.getID());
        ps.setInt(2, toernooi.getID());
        ps.setBoolean(3, heeftBetaald);

        mysql.update(ps);

        return true;
    }

    /**
     * Probeer een speler te registreren voor een {@link MasterClass}
     *
     * @param masterClass  Masterclass voor de speler om te registeren
     * @param speler       Speler dat zich wilt registeren voor Masterclass
     * @param heeftBetaald Of de speler al betaald heeft voor de Masterclass
     * @return true als de registratie voltooid is, false registratie niet gelukt is (bijvoorbeeld vol of te lage rating)
     * @throws SQLException
     */
    public static boolean registreerSpelerVoorMasterclass(MasterClass masterClass, Speler speler, boolean heeftBetaald) throws SQLException {
        if (masterClass.isVol() || speler.getRating() < masterClass.getMinRating()) {
            // De inschrijvingen zijn vol, geen vrije plekken beschikbaar of de rating van speler is te laag
            return false;
        }

        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `inschrijving_masterclass` (`spelerID`, `masterclassID`, `betaald`) VALUES (?, ?, ?)");
        ps.setInt(1, speler.getID());
        ps.setInt(2, masterClass.getID());
        ps.setBoolean(3, heeftBetaald);

        mysql.update(ps);

        return true;
    }

    /**
     * Verkrijg een lijst met uitkomsten van toernooi
     *
     * @param toernooi
     * @return Lijst met uitkomsten
     * @throws SQLException
     */
    public static List<ToernooiUitkomst> verkrijgToernooiUikomsten(Toernooi toernooi) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from toernooi_uitkomsten where toernooiID = ?");
        ps.setInt(1, toernooi.getID());

        ResultSet rs = mysql.query(ps);


        List<ToernooiUitkomst> toernooiUitkomsts = new ArrayList<>();

        while (rs.next()) {
            toernooiUitkomsts.add(new ToernooiUitkomst(rs));
        }

        return toernooiUitkomsts;
    }

    /**
     * Delete uitkomsten uit de database per ID
     *
     * @param ID
     * @throws SQLException
     */
    public static void verwijderToernooiUitkomstByToernooiID(int ID) throws SQLException{
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("delete from toernooi_uitkomsten where toernooiID = ?");
        ps.setInt(1, ID);
        mysql.update(ps);
    }


    /**
     * Verkrijg een lijst met uitkomsten van Speler
     *
     * @param speler
     * @return Lijst met uitkomsten
     * @throws SQLException
     */
    public static List<ToernooiUitkomst> verkrijgToernooiUikomsten(Speler speler) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from toernooi_uitkomsten where spelerID = ?");
        ps.setInt(1, speler.getID());

        ResultSet rs = mysql.query(ps);


        List<ToernooiUitkomst> toernooiUitkomsts = new ArrayList<>();

        while (rs.next()) {
            toernooiUitkomsts.add(new ToernooiUitkomst(rs));
        }

        return toernooiUitkomsts;
    }

    public static List<InschrijvingToernooi> verkrijgLijstMetToernooiInschrijvingen(Toernooi toernooi) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from inschrijving_toernooi where toernooiID = ?");
        ps.setInt(1, toernooi.getID());

        ResultSet rs = mysql.query(ps);

        List<InschrijvingToernooi> inschrijvingen = new ArrayList<>();

        while (rs.next()) {
            inschrijvingen.add(new InschrijvingToernooi(rs));
        }

        return inschrijvingen;
    }

    public static List<InschrijvingToernooi> verkrijgLijstMetToernooiInschrijvingen(Speler speler) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from inschrijving_toernooi where spelerID = ?");
        ps.setInt(1, speler.getID());

        ResultSet rs = mysql.query(ps);

        List<InschrijvingToernooi> inschrijvingen = new ArrayList<>();

        while (rs.next()) {
            inschrijvingen.add(new InschrijvingToernooi(rs));
        }

        return inschrijvingen;
    }


    public static List<InschrijvingMasterclass> verkrijgLijstMetMasterclassInschrijvingen(MasterClass masterClass) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from inschrijving_masterclass where masterclassID = ?");
        ps.setInt(1, masterClass.getID());

        ResultSet rs = mysql.query(ps);

        List<InschrijvingMasterclass> inschrijvingen = new ArrayList<>();

        while (rs.next()) {
            inschrijvingen.add(new InschrijvingMasterclass(rs));
        }

        return inschrijvingen;
    }

    public static List<InschrijvingMasterclass> verkrijgLijstMetMasterclassInschrijvingen(Speler speler) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from inschrijving_masterclass where spelerID = ?");
        ps.setInt(1, speler.getID());

        ResultSet rs = mysql.query(ps);

        List<InschrijvingMasterclass> inschrijvingen = new ArrayList<>();

        while (rs.next()) {
            inschrijvingen.add(new InschrijvingMasterclass(rs));
        }

        return inschrijvingen;
    }

    public static List<ToernooiTafelIndeling> verkrijgTafelIndelingVanToernooi(Toernooi toernooi) throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from toernooi_tafelindeling where toernooiID = ?");
        ps.setInt(1, toernooi.getID());

        ResultSet rs = mysql.query(ps);

        List<ToernooiTafelIndeling> indelingList = new ArrayList<>();

        while (rs.next()) {
            indelingList.add(new ToernooiTafelIndeling(rs));
        }
        return indelingList;
    }
}
