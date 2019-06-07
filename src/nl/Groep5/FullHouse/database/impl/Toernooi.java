package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.*;
import java.util.List;

/**
 * Created by DeStilleGast 5-6-2019
 */
public class Toernooi {

    private int ID, locatieID;
    private String naam, beschrijving, beginTijd, eindTijd;
    private Date datum, uitersteInschrijfDatum;
    private int maxInschrijvingen;
    private double inleg;

    public Toernooi(int locatieID, String naam, String beschrijving, Date datum, Date uitersteInschrijfDatum, String beginTijd, String eindTijd, int maxInschrijvingen, double inleg) {
        this.locatieID = locatieID;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.datum = datum;
        this.uitersteInschrijfDatum = uitersteInschrijfDatum;
        this.beginTijd = beginTijd;
        this.eindTijd = eindTijd;
        this.maxInschrijvingen = maxInschrijvingen;
        this.inleg = inleg;
    }

    public Toernooi(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.naam = resultSet.getString("naam");
        this.beschrijving = resultSet.getString("beschrijving");
        this.datum = resultSet.getDate("datum");
        this.beginTijd = resultSet.getString("beginTijd");
        this.eindTijd = resultSet.getString("eindTijd");
        this.maxInschrijvingen = resultSet.getInt("maxInschrijvingen");
        this.inleg = resultSet.getDouble("inleg");
        this.uitersteInschrijfDatum = resultSet.getDate("uitersteInschrijfDatum");
        this.locatieID = resultSet.getInt("locatieID");
    }

    public int getID() {
        return ID;
    }

    public int getLocatieID() {
        return locatieID;
    }


    public void setLocatieID(int locatieID) throws Exception {
        if(String.valueOf(locatieID).matches("\\d{1,2}")) {
            this.locatieID = locatieID;
        }else{
            throw new Exception("Locatie ID moet 2 cijfers bevatten.");
        }
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) throws Exception{
        if(naam.matches(".{1,45}")) {
            this.naam = naam;
        }else{
            throw new Exception("Naam moet minimaal 1 tot 45 karakters bevatten.");
        }
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) throws Exception{
        if(beschrijving.matches(".{1,100}")) {
            this.beschrijving = beschrijving;
        }else{
            throw new Exception("Beschrijving moet minimaal 1 tot 100 karakters bevatten.");
        }
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Date getUitersteInschrijfDatum() {
        return uitersteInschrijfDatum;
    }

    public void setUitersteInschrijfDatum(Date uitersteInschrijfDatum) {
        this.uitersteInschrijfDatum = uitersteInschrijfDatum;
    }

    public String getBeginTijd() {
        return beginTijd;
    }

    public void setBeginTijd(String beginTijd) {
        this.beginTijd = beginTijd;
    }

    public String getEindTijd() {
        return eindTijd;
    }

    public void setEindTijd(String eindTijd) {
        this.eindTijd = eindTijd;
    }

    public int getMaxAantalInschrijvingen() {
        return maxInschrijvingen;
    }

    public void setMaxInschrijvingen(int maxInschrijvingen) throws Exception{
        if(String.valueOf(maxInschrijvingen).matches("\\d{1,999999999}")) {
            this.maxInschrijvingen = maxInschrijvingen;
        }else{
            throw new Exception("Het maximaal aantal inschrijvingen moet 1 tot 999999999 zijn.");
        }
    }

    public double getInleg() {
        return inleg;
    }

    public void setInleg(double inleg) throws Exception{
        String regexDecimal = "^-?\\d*\\.\\d+$";
        String regexInteger = "^-?\\d+$";
        String regexDouble = regexDecimal + "|" + regexInteger;
        if(String.valueOf(inleg).matches(regexDouble)) {
            this.inleg = inleg;
        }else{
            throw new Exception("De inleg is incorrect ingevoerd.");
        }
    }

    public List<InschrijvingToernooi> getInschrijvingen() throws SQLException {
        return DatabaseHelper.VerkrijgInschrijvingenVanToernooi(this);
    }

    public Locatie getLocatie() throws SQLException {
        return DatabaseHelper.verkrijgLocatieById(locatieID);
    }

    /**
     * Probeer speler in te schrijven voor dit toernooi
     *
     * @param speler om te registreren
     * @return true als registratie gelukt is, false als het niet gelukt is (bijvoorbeeld omdat het vol is)
     */
    public boolean voegSpelerToe(Speler speler, Boolean heeftBetaald) throws SQLException {
        return DatabaseHelper.registreerSpelerVoorToernooi(this, speler, heeftBetaald);
    }

    /**
     * Kijk of de toernooi vol zit kwa inschrijvingen
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
     * Nieuw toernooi opslaan
     *
     * @return true als toernooi is opgeslagen in database
     * @throws SQLException
     */
    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `toernooi` (`naam`, `datum`, `beginTijd`, `eindTijd`, `beschrijving`, `maxInschrijvingen`, `inleg`, `uitersteInschrijfDatum`, `locatieID`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
        FillPrepareStatement(ps);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Update bestaande Toernooi
     *
     * @return True als het geupdate is
     * @throws SQLException
     */
    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `toernooi` SET `naam`=?, `datum`=?, `beginTijd`=?, `eindTijd`=?, `beschrijving`=?, `maxInschrijvingen`=?, `inleg`=?, `uitersteInschrijfDatum`=?, `locatieID`=?  WHERE `ID`=?;");
        FillPrepareStatement(ps);
        ps.setInt(10, this.ID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    private void FillPrepareStatement(PreparedStatement ps) throws SQLException {
        ps.setString(1, this.naam);
        ps.setDate(2, this.datum);
        ps.setString(3, this.beginTijd);
        ps.setString(4, this.eindTijd);
        ps.setString(5, this.beschrijving);
        ps.setInt(6, this.maxInschrijvingen);
        ps.setDouble(7, this.inleg);
        ps.setDate(8, this.uitersteInschrijfDatum);
        ps.setInt(9, this.locatieID);
    }
}
