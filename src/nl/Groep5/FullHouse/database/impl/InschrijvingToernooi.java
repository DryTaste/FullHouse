package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by DeStilleGast 5-6-2019
 */
public class InschrijvingToernooi {

    private int spelerID, toernooiID;
    private boolean isBetaald;

    public InschrijvingToernooi(int spelerID, int toernooiID, boolean isBetaald) {
        this.spelerID = spelerID;
        this.toernooiID = toernooiID;
        this.isBetaald = isBetaald;
    }

    public InschrijvingToernooi(ResultSet resultSet) throws SQLException {
        this.spelerID = resultSet.getInt("spelerID");
        this.toernooiID = resultSet.getInt("toernooiID");
        this.isBetaald = resultSet.getBoolean("betaald");
    }

    public int getSpelerID() {
        return spelerID;
    }

    public int getToernooiID() {
        return toernooiID;
    }

    public boolean isBetaald() {
        return isBetaald;
    }

    public void setBetaald(boolean betaald) {
        isBetaald = betaald;
    }

    /**
     * Verkrijg de {@link Speler} van deze inschrijving
     *
     * @return {@link Speler} van deze inschrijving
     * @throws SQLException
     */
    public Speler getSpeler() throws SQLException {
        return DatabaseHelper.verkrijgSpelerBijId(spelerID);
    }

    /**
     * Verkrijg de {@link Toernooi} van deze inschrijving
     *
     * @return {@link Toernooi} van deze inschrijving
     * @throws SQLException
     */
    public Toernooi getToernooi() throws SQLException {
        return DatabaseHelper.verkrijgToernooiById(toernooiID);
    }


    /**
     * Sla nieuwe inschrijving op
     *
     * @return True als inschrijving opgeslagen is
     * @throws SQLException
     */
    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `inschrijving_toernooi` (`spelerID`, `toernooiID`, `betaald`) VALUES (?, ?, ?);");

        ps.setInt(1, this.spelerID);
        ps.setInt(2, this.toernooiID);
        ps.setBoolean(3, this.isBetaald);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Bewerk nieuwe inschrijving
     *
     * @return True als inschrijving is bewerkt
     * @throws SQLException
     */
    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `18086632`.`inschrijving_toernooi` SET `betaald`=? WHERE `spelerID`=? and `toernooiID`=?;");

        ps.setBoolean(1, this.isBetaald);
        ps.setInt(2, this.spelerID);
        ps.setInt(3, this.toernooiID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Verwijder inschrijving voor als de speler niet meer wilt/kan of de toernooi is afgelast
     *
     * @return True als inschrijving verwijderd is
     * @throws SQLException
     */
    public boolean Delete() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("DELETE FROM `inschrijving_toernooi` WHERE `spelerID`=? and `toernooiID`=?;");

        ps.setInt(1, this.spelerID);
        ps.setInt(2, this.toernooiID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }
}
