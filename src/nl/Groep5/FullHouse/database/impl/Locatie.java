package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by DeStilleGast 5-6-2019
 */
public class Locatie {

    private int ID;
    private String naam;

    public Locatie(String naam) {
        this.naam = naam;
    }

    public Locatie(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.naam = resultSet.getString("naam");
    }

    public int getID() {
        return ID;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }


    /**
     * Nieuwe locatie opslaan
     *
     * @return True als locatie is opgeslagen
     * @throws SQLException
     */
    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `locatie` (`naam`) VALUES (?);");
        ps.setString(1, this.naam);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    /**
     * Bestaande locatie updaten
     *
     * @return True als locatie is opgeslagen
     * @throws SQLException
     */
    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `locatie` SET `naam`=? WHERE `ID`=?;");
        ps.setString(1, this.naam);
        ps.setInt(2, this.ID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }
}
