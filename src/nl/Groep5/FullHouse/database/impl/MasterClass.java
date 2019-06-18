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
    private String beginTijd, eindTijd;
    private double kosten, minRating;
    private int maxAantalInschrijvingen, leraar;

    public MasterClass(int locatieId, Date datum, String beginTijd, String eindTijd, double kosten, double minRating, int maxAantalInschrijvingen, int leraar) {
        this.locatieId = locatieId;
        this.datum = datum;
        this.beginTijd = beginTijd;
        this.eindTijd = eindTijd;
        this.kosten = kosten;
        this.minRating = minRating;
        this.maxAantalInschrijvingen = maxAantalInschrijvingen;
        this.leraar = leraar;
    }

    public MasterClass(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.datum = resultSet.getDate("datum");
        this.beginTijd = resultSet.getString("beginTijd");
        this.eindTijd = resultSet.getString("eindTijd");
        this.kosten = resultSet.getDouble("kosten");
        this.minRating = resultSet.getDouble("minRating");
        this.maxAantalInschrijvingen = resultSet.getInt("maxInschrijvingen");
        this.locatieId = resultSet.getInt("locatieID");
        this.leraar = resultSet.getInt("leraar");
    }

    public int getID() {
        return ID;
    }

    public int getLocatieId() {
        return locatieId;
    }

    public int getLeraar(){
        return leraar;
    }

    public void setLeraar(int leraar) throws Exception{
        if(String.valueOf(leraar).matches("\\d{1,4}")){
            this.leraar = leraar;
        }else{
            throw new Exception("Leraar moet tussen 1 en 4 cijfers bevatten.");
        }
    }

    public void setLocatieId(int locatieId) throws Exception{
        if(String.valueOf(locatieId).matches("\\d{1,2}")){
            this.locatieId = locatieId;
        }else{
            throw new Exception("LocatieID moet 1 of 2 cijfers bevatten.");
        }

    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getBeginTijd() {
        return beginTijd;
    }

    public void setBeginTijd(String beginTijd){
        this.beginTijd = beginTijd;
    }

    public String getEindTijd() {
        return eindTijd;
    }

    public void setEindTijd(String eindTijd) {
        this.eindTijd = eindTijd;
    }

    public double getKosten() {
        return kosten;
    }

    public void setKosten(double kosten) throws Exception{
        String regexDecimal = "^-?\\d*\\.\\d+$";
        String regexInteger = "^-?\\d+$";
        String regexDouble = regexDecimal + "|" + regexInteger;
        if(String.valueOf(kosten).matches(regexDouble)) {
            this.kosten = kosten;
        }else{
            throw new Exception("De kosten zijn incorrect ingevoerd.");
        }
    }

    public double getMinRating() {
        return minRating;
    }

    public void setMinRating(double minRating) throws Exception{
        String regexDecimal = "^-?\\d*\\.\\d+$";
        String regexInteger = "^-?\\d+$";
        String regexDouble = regexDecimal + "|" + regexInteger;
        if(String.valueOf(minRating).matches(regexDouble)) {
            this.minRating = minRating;
        }else{
            throw new Exception("De minimale rating is incorrect ingevoerd.");
        }
    }

    public int getMaxAantalInschrijvingen() {
        return maxAantalInschrijvingen;
    }

    public void setMaxAantalInschrijvingen(int maxAantalInschrijvingen) throws Exception{
        if(String.valueOf(maxAantalInschrijvingen).matches("\\d{1,999999999}")) {
            this.maxAantalInschrijvingen = maxAantalInschrijvingen;
        }else{
            throw new Exception("Het maximaal aantal inschrijvingen moet 1 tot 999999999 zijn.");
        }
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
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `masterclass` (`datum`, `beginTijd`, `eindTijd`, `kosten`, `minRating`, `maxInschrijvingen`, `locatieID`, leraar) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
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
        PreparedStatement ps = mysql.prepareStatement("UPDATE `masterclass` SET `datum`=?, `beginTijd`=?, `eindTijd`=?, `kosten`=?, `minRating`=?, `maxInschrijvingen`=?, `locatieID`=?, `leraar`=? WHERE `ID`=?;");
        FillPrepareStatement(ps);
        ps.setInt(9, this.ID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    private void FillPrepareStatement(PreparedStatement ps) throws SQLException {
        ps.setDate(1, this.datum);
        ps.setString(2, this.beginTijd);
        ps.setString(3, this.eindTijd);
        ps.setDouble(4, this.kosten);
        ps.setDouble(5, this.minRating);
        ps.setInt(6, this.maxAantalInschrijvingen);
        ps.setInt(7, this.locatieId);
        ps.setInt(8, this.leraar);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s", this.ID, this.datum.toString(), this.beginTijd.toString());
    }
}
