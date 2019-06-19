package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.regex.Pattern;

/**
 * Created by DeStilleGast 5-6-2019
 */
public class Speler {

    private int ID;
    private String voornaam, tussenvoegsel, achternaam, adres, postcode, woonplaats, telefoonnummer, email;
    private char geslacht;
    private Date geboortedatum;
    private double rating;

    public Speler(String voornaam, String tussenvoegsel, String achternaam, String adres, String postcode, String woonplaats, String telefoonnummer, String email, char geslacht, Date geboortedatum, double rating) {
        this.voornaam = voornaam;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.adres = adres;
        this.postcode = postcode;
        this.woonplaats = woonplaats;
        this.telefoonnummer = telefoonnummer;
        this.email = email;
        this.geslacht = geslacht;
        this.geboortedatum = geboortedatum;
        this.rating = rating;
    }

    // Zonder tussenvoegsel
    public Speler(String voornaam, String achternaam, String adres, String postcode, String woonplaats, String telefoonnummer, String email, char geslacht, Date geboortedatum, double rating) {
        this.voornaam = voornaam;
        this.achternaam = achternaam;
        this.adres = adres;
        this.postcode = postcode;
        this.woonplaats = woonplaats;
        this.telefoonnummer = telefoonnummer;
        this.email = email;
        this.geslacht = geslacht;
        this.geboortedatum = geboortedatum;
        this.rating = rating;
    }

    public Speler(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.voornaam = resultSet.getString("voornaam");
        this.tussenvoegsel = resultSet.getString("tussenvoegsel");
        this.achternaam = resultSet.getString("achternaam");
        this.adres = resultSet.getString("adres");
        this.postcode = resultSet.getString("postcode");
        this.woonplaats = resultSet.getString("woonplaats");
        this.telefoonnummer = resultSet.getString("telefoonnummer");
        this.email = resultSet.getString("email");
        this.geslacht = resultSet.getString("geslacht").charAt(0);
        this.geboortedatum = resultSet.getDate("geboortedatum");
        this.rating = resultSet.getDouble("rating");
    }

    public int getID() {
        return ID;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public String getAdres() {
        return adres;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getWoonplaats() {
        return woonplaats;
    }

    public String getTelefoonnummer() {
        return telefoonnummer;
    }

    public String getEmail() {
        return email;
    }

    public char getGeslacht() {
        return geslacht;
    }

    public Date getGeboortedatum() {
        return geboortedatum;
    }

    public double getRating() {
        return rating;
    }

    // https://howtodoinjava.com/java/calculate-age-from-date-of-birth/
    public int getLeeftijd() {
        Period p = Period.between(this.getGeboortedatum().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now());
        return p.getYears();
    }

    public void setVoornaam(String voornaam) throws Exception {
        if(voornaam.matches(".{1,20}")) {
            this.voornaam = voornaam;
        }else{
            throw new Exception("Voornaam mag niet leeg zijn en mag maximaal 20 karakters bevatten.");
        }
    }

    public void setTussenvoegsel(String tussenvoegsel) throws Exception{
        if(tussenvoegsel.matches("\\D{0,10}")) {
            this.tussenvoegsel = tussenvoegsel;
        }else{
            throw new Exception("Tussenvoegsel mag alleen 0 tot 10 letters bevatten");
        }
    }

    public void setAchternaam(String achternaam) throws Exception{
        if(achternaam.matches("^(?=\\s*\\S).*$")) {
            this.achternaam = achternaam;
        }else{
            throw new Exception("Achternaam mag niet leeg zijn en mag maximaal 45 karakters bevatten.");
        }
    }

    public void setAdres(String adres) throws Exception {
        if(adres.matches(".{3,30}")) {
            this.adres = adres;
        }else{
            throw new Exception("Adres mag alleen 3 tot 30 karakters bevatten.");
        }
    }

    public void setPostcode(String postcode) throws Exception {
        if(postcode.matches("\\d{4}[A-Z]{2}")) {
            this.postcode = postcode;
        }else{
            throw new Exception("Postcode moet beginnen met 4 cijfers en eindigen met 2 hoofdletters.");
        }
    }

    public void setWoonplaats(String woonplaats) throws Exception {
        if(woonplaats.matches(".{1,20}")) {
            this.woonplaats = woonplaats;
        }else{
            throw new Exception("Plaatsnaam moet 1 tot 20 letters bevatten.");
        }
    }

    public void setTelefoonnummer(String telefoonnummer) throws Exception {
        if(telefoonnummer.matches("\\d{1,10}")){
            this.telefoonnummer = telefoonnummer;
        }else{
            throw new Exception("Telefoonnummers mogen 1 tot 10 cijfers bevatten.");
        }

    }

    public void setEmail(String email) throws Exception {
        if(email.matches(".{0,30}")) {
            this.email = email;
        }else{
            throw new Exception("Een email mag niet langer zijn dan 30 karakters.");
        }
    }

    public void setGeslacht(char geslacht) {
        this.geslacht = geslacht;
    }

    public void setGeboortedatum(Date geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public void setRating(double rating) throws Exception{
        String regexDecimal = "^-?\\d*\\.\\d+$";
        String regexInteger = "^-?\\d+$";
        String regexDouble = regexDecimal + "|" + regexInteger;
        if(String.valueOf(rating).matches(regexDouble)) {
            this.rating = rating;
        }else{
            throw new Exception("De rating is incorrect ingevoerd.");
        }
    }

    public boolean registreerVoorToernooi(Toernooi toernooi, Boolean heeftBetaald) throws SQLException {
        return DatabaseHelper.registreerSpelerVoorToernooi(toernooi, this, heeftBetaald);
    }

    public boolean registreerVoorMasterClass(MasterClass masterClass, Boolean heeftBetaald) throws SQLException {
        return DatabaseHelper.registreerSpelerVoorMasterclass(masterClass, this, heeftBetaald);
    }

    /**
     * Nieuwe speler opslaan
     *
     * @return true als nieuwe speler is opgeslagen
     * @throws SQLException
     */
    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `speler` (`voornaam`, `tussenvoegsel`, `achternaam`, `adres`, `postcode`, `woonplaats`, `telefoonnummer`, `email`, `geslacht`, `geboortedatum`, `rating`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        FillPrepareStatement(ps);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Speler bewerken
     *
     * @return True als speler bewerkt is
     * @throws SQLException
     */
    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `speler` SET `voornaam`=?, `tussenvoegsel`=?, `achternaam`=?, `adres`=?, `postcode`=?, `woonplaats`=?, `telefoonnummer`=?, `email`=?, `geslacht`=?, `geboortedatum`=?, `rating`=? WHERE `ID`=?;");
        FillPrepareStatement(ps);
        ps.setInt(12, this.ID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    private void FillPrepareStatement(PreparedStatement ps) throws SQLException {
        ps.setString(1, this.voornaam);
        ps.setString(2, this.tussenvoegsel);
        ps.setString(3, this.achternaam);
        ps.setString(4, this.adres);
        ps.setString(5, this.postcode);
        ps.setString(6, this.woonplaats);
        ps.setString(7, this.telefoonnummer);
        ps.setString(8, this.email);
        ps.setString(9, String.valueOf(this.geslacht)); // convert char back to string
        ps.setDate(10, this.geboortedatum);
        ps.setDouble(11, this.rating);
    }

    /**
     * Probeer alle persoonsgevoelige informatie te verwijderen uit database
     *
     * @return true als het gelukt is
     * @throws SQLException
     */
    public boolean AVGClear() throws SQLException {
        this.voornaam = "[verwijderd]";
        this.tussenvoegsel = "";
        this.achternaam = "[verwijderd]";
        this.postcode = "0000AA";
        this.adres = "[verwijderd]";
        this.email = "[verwijderd]";
        this.woonplaats = "[verwijderd]";
        this.telefoonnummer = "0000000000";
        try {
            this.geboortedatum = (new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse("0001-01-01").getTime()));
        }catch (ParseException parseError){
            parseError.printStackTrace();
        }
        this.setGeslacht('?');
        this.rating = 0;

        return Update();
    }

    @Override
    public String toString() {
        if(this.tussenvoegsel == null || this.tussenvoegsel.isEmpty())
            return String.format("%s %s", this.voornaam, this.achternaam);

        return String.format("%s %s %s", this.voornaam, this.tussenvoegsel, this.achternaam);
    }
}
