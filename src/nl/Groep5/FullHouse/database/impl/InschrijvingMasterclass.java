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
public class InschrijvingMasterclass {

    private int spelerID, masterclassID;
    private boolean isBetaald;

    public InschrijvingMasterclass(int spelerID, int masterclassID, boolean isBetaald) {
        this.spelerID = spelerID;
        this.masterclassID = masterclassID;
        this.isBetaald = isBetaald;
    }

    public InschrijvingMasterclass(ResultSet resultSet) throws SQLException {
        this.spelerID = resultSet.getInt("spelerID");
        this.masterclassID = resultSet.getInt("masterclassID");
        this.isBetaald = resultSet.getBoolean("betaald");
    }

    public int getSpelerID() {
        return spelerID;
    }

    public int getMasterclassID() {
        return masterclassID;
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
     * Verkrijg de {@link MasterClass} van deze inschrijving
     *
     * @return {@link MasterClass} van deze inschrijving
     * @throws SQLException
     */
    public MasterClass getMasterClass() throws SQLException {
        return DatabaseHelper.verkrijgMasterClassById(masterclassID);
    }


    /**
     * Nieuwe inschrijving opslaan
     *
     * @return True als inschrijving is opgeslagen
     * @throws SQLException
     */
    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `inschrijving_masterclass` (`spelerID`, `masterclassID`, `betaald`) VALUES (?, ?, ?);");

        ps.setInt(1, this.spelerID);
        ps.setInt(2, this.masterclassID);
        ps.setBoolean(3, this.isBetaald);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Bewerk inschrijving
     *
     * @return True als inschrijving is bewerkt
     * @throws SQLException
     */
    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `18086632`.`inschrijving_masterclass` SET `betaald`=? WHERE `spelerID`=? and `masterclassID`=?;");

        ps.setBoolean(1, this.isBetaald);
        ps.setInt(2, this.spelerID);
        ps.setInt(3, this.masterclassID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Verwijder inschrijving voor als de speler niet meer wilt/kan of de masterclass is afgelast
     *
     * @return True als inschrijving verwijderd is
     * @throws SQLException
     */
    public boolean Delete() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("DELETE FROM `inschrijving_masterclass` WHERE `spelerID`=? and `masterclassID`=?;");

        ps.setInt(1, this.spelerID);
        ps.setInt(2, this.masterclassID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }
}
