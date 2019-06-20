package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by DeStilleGast 18-6-2019
 */
public class ToernooiTafelIndeling {

    private int ID, toernooiID, spelerID, stoelNr;

    private Speler speler;

    public ToernooiTafelIndeling(int toernooiID, int spelerID, int stoelNr) {
        this.toernooiID = toernooiID;
        this.spelerID = spelerID;
        this.stoelNr = stoelNr;
    }

    public ToernooiTafelIndeling(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.toernooiID = resultSet.getInt("toernooiID");
        this.spelerID = resultSet.getInt("spelerID");
        this.stoelNr = resultSet.getInt("stoelnr");
    }

    public int getID() {
        return ID;
    }

    public int getToernooiID() {
        return toernooiID;
    }

    public void setToernooiID(int toernooiID) {
        this.toernooiID = toernooiID;
    }

    public int getSpelerID() {
        return spelerID;
    }

    public void setSpelerID(int spelerID) {
        this.spelerID = spelerID;
    }

    public int getStoelNr() {
        return stoelNr;
    }

    public void setStoelNr(int stoelNr) {
        this.stoelNr = stoelNr;
    }

    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `toernooi_tafelindeling` (`toernooiID`, `spelerID`, `stoelnr`) VALUES (?, ?, ?);");
        fillPrepareStatement(ps);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `toernooi_tafelindeling` SET `toernooiID`=?, `spelerID`=?, `stoelnr`=? WHERE `ID`=?;");
        fillPrepareStatement(ps);
        ps.setInt(4, this.ID);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    public boolean UpdateAnderSave() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("select * from toernooi_tafelindeling where ID = ?");
        ps.setInt(1, this.ID);

        ResultSet rs = mysql.query(ps);

        if (rs.next()) {
            return Update();
        } else {
            return Save();
        }
    }

    private void fillPrepareStatement(PreparedStatement ps) throws SQLException {
        ps.setInt(1, toernooiID);
        ps.setInt(2, spelerID);
        ps.setInt(3, stoelNr);
    }

    public Speler getSpeler() throws SQLException {
        if (speler == null) speler = DatabaseHelper.verkrijgSpelerBijId(this.spelerID);

        return speler;
    }

}
