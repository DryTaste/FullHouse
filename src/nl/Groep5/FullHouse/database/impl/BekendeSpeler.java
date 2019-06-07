package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by DeStilleGast 5-6-2019
 */
public class BekendeSpeler {

    private int id;
    private String pseudonaam;

    public BekendeSpeler(String pseudonaam) {
        this.pseudonaam = pseudonaam;
    }

    public BekendeSpeler(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.pseudonaam = resultSet.getString("pseudonaam");
    }

    public int getId() {
        return id;
    }

    public String getPseudonaam() {
        return pseudonaam;
    }

    public void setPseudonaam(String pseudonaam) throws Exception {
        if(pseudonaam.matches(".{1,45}")) {
            this.pseudonaam = pseudonaam;
        }else{
            throw new Exception("De naam moet 1 tot 45 karakters bevatten.");
        }
    }

    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `bekende_speler` SET `pseudonaam`=? WHERE `ID`=?;");
        FillPrepareStatement(ps);
        ps.setInt(2, this.id);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `bekende_speler` (`pseudonaam`) VALUES (?);");
        ps.setString(1, this.pseudonaam);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    public boolean Delete() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("DELETE FROM `bekende_speler` WHERE `ID`=?;");
        ps.setInt(1, this.id);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }
    private void FillPrepareStatement(PreparedStatement ps) throws SQLException {
        ps.setString(1, this.pseudonaam);
        ps.setString(2, String.valueOf(this.id));

    }
}
