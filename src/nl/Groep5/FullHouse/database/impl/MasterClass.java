package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.*;
import java.util.List;

/**
 * Created by DeStilleGast 5-6-2019
 */
public class MasterClass {

    private int ID, locatieId;
    private Date datum;
    private Timestamp beginTijd, eindTijd;
    private double kosten, minRating;
    private int maxAantalInschrijvingen;

    public MasterClass(int locatieId, Date datum, Timestamp beginTijd, Timestamp eindTijd, double kosten, double minRating, int maxAantalInschrijvingen) {
        this.locatieId = locatieId;
        this.datum = datum;
        this.beginTijd = beginTijd;
        this.eindTijd = eindTijd;
        this.kosten = kosten;
        this.minRating = minRating;
        this.maxAantalInschrijvingen = maxAantalInschrijvingen;
    }

    public MasterClass(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.datum = resultSet.getDate("datum");
        this.beginTijd = resultSet.getTimestamp("beginTijd");
        this.eindTijd = resultSet.getTimestamp("eindTijd");
        this.kosten = resultSet.getDouble("kosten");
        this.minRating = resultSet.getDouble("minRating");
        this.maxAantalInschrijvingen = resultSet.getInt("maxInschrijvingen");
        this.locatieId = resultSet.getInt("locatieID");
    }

    public int getID() {
        return ID;
    }

    public int getLocatieId() {
        return locatieId;
    }

    public void setLocatieId(int locatieId) {
        this.locatieId = locatieId;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Timestamp getBeginTijd() {
        return beginTijd;
    }

    public void setBeginTijd(Timestamp beginTijd) {
        this.beginTijd = beginTijd;
    }

    public Timestamp getEindTijd() {
        return eindTijd;
    }

    public void setEindTijd(Timestamp eindTijd) {
        this.eindTijd = eindTijd;
    }

    public double getKosten() {
        return kosten;
    }

    public void setKosten(double kosten) {
        this.kosten = kosten;
    }

    public double getMinRating() {
        return minRating;
    }

    public void setMinRating(double minRating) {
        this.minRating = minRating;
    }

    public int getMaxAantalInschrijvingen() {
        return maxAantalInschrijvingen;
    }

    public void setMaxAantalInschrijvingen(int maxAantalInschrijvingen) {
        this.maxAantalInschrijvingen = maxAantalInschrijvingen;
    }

    public List<InschrijvingMasterclass> getInschrijvingen() throws SQLException {
        return DatabaseHelper.VerkrijgInschrijvingenVanMasterClass(this);
    }

    public Locatie getLocatie() throws SQLException {
        return DatabaseHelper.verkrijgLocatieById(locatieId);
    }

    /**
     * Probeer speler in te schrijven voor deze MasterClass
     *
     * @param speler om te registreren
     * @return true als registratie gelukt is, false als het niet gelukt is (bijvoorbeeld omdat het vol is)
     */
    public boolean voegSpelerToe(Speler speler, Boolean heeftBetaald) throws SQLException {
        return DatabaseHelper.registreerSpelerVoorMasterclass(this, speler, heeftBetaald);
    }

    /**
     * Kijk of de masterclass vol zit kwa inschrijvingen
     *
     * @return true als de inschrijven de maximaleAantal overschrijft
     * <br>
     * <br>
     * het returned ook true als er een SQL fout opgetreden is !!
     */
    public boolean isVol() {
        try {
            return this.getInschrijvingen().size() >= this.getMaxAantalInschrijvingen();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * Nieuwe masterclass opslaan
     *
     * @return True als masterclass opgeslagen is
     * @throws SQLException
     */
    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `masterclass` (`datum`, `beginTijd`, `eindTijd`, `kosten`, `minRating`, `maxInschrijvingen`, `locatieID`) VALUES (?, ?, ?, ?, ?, ?, ?);");
        FillPrepareStatement(ps);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Bestaande masterclass updaten
     *
     * @return True als masterclass geupdate is
     * @throws SQLException
     */
    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `masterclass` SET `datum`=?, `beginTijd`=?, `eindTijd`=?, `kosten`=?, `minRating`=?, `maxInschrijvingen`=?, `locatieID`=? WHERE `ID`=?;");
        FillPrepareStatement(ps);
        ps.setInt(8, this.ID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    private void FillPrepareStatement(PreparedStatement ps) throws SQLException {
        ps.setDate(1, this.datum);
        ps.setTimestamp(2, this.beginTijd);
        ps.setTimestamp(3, this.eindTijd);
        ps.setDouble(4, this.kosten);
        ps.setDouble(5, this.minRating);
        ps.setInt(6, this.maxAantalInschrijvingen);
        ps.setInt(7, this.locatieId);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s", this.ID, this.datum.toString(), this.beginTijd.toString());
    }
}
