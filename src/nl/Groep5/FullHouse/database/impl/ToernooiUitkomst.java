package nl.Groep5.FullHouse.database.impl;

import nl.Groep5.FullHouse.Main;
import nl.Groep5.FullHouse.database.DatabaseHelper;
import nl.Groep5.FullHouse.database.MySQLConnector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by DeStilleGast 6-6-2019
 */
public class ToernooiUitkomst {

    private int ID, SpelerID, ToernooiID, Plaats;
    private double prijs;

    public ToernooiUitkomst(int spelerID, int toernooiID, int plaats, double prijs) {
        SpelerID = spelerID;
        ToernooiID = toernooiID;
        Plaats = plaats;
        this.prijs = prijs;
    }

    public ToernooiUitkomst(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.SpelerID = resultSet.getInt("spelerID");
        this.ToernooiID = resultSet.getInt("toernooiID");
        this.Plaats = resultSet.getInt("plaats");
        this.prijs = resultSet.getDouble("prijs");
    }

    public int getID() {
        return ID;
    }

    public int getSpelerID() {
        return SpelerID;
    }

    public void setSpelerID(int spelerID) {
        SpelerID = spelerID;
    }

    public int getToernooiID() {
        return ToernooiID;
    }

    public void setToernooiID(int toernooiID) {
        ToernooiID = toernooiID;
    }

    public int getPlaats() {
        return Plaats;
    }

    public void setPlaats(int plaats) {
        Plaats = plaats;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public boolean Save() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("INSERT INTO `toernooi_uitkomsten` (`toernooiID`, `spelerID`, `plaats`, `prijs`) VALUES (?, ?, ?, ?);");
        ps.setInt(1, this.ToernooiID);
        ps.setInt(2, this.SpelerID);
        ps.setInt(3, this.Plaats);
        ps.setDouble(4, this.prijs);

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    public boolean Update() throws SQLException {
        MySQLConnector mysql = Main.getMySQLConnection();
        PreparedStatement ps = mysql.prepareStatement("UPDATE `toernooi_uitkomsten` SET `toernooiID`=?, `spelerID`=?, `plaats`=?, `prijs`=? WHERE `ID`=?;");
        ps.setInt(1, this.ToernooiID);
        ps.setInt(2, this.SpelerID);
        ps.setInt(3, this.Plaats);
        ps.setDouble(4, this.prijs);
        ps.setInt(5, this.getID());

        // check if the update is 1 (1 row updated/added)
        return mysql.update(ps) == 1;
    }

    public Speler getSpeler() throws SQLException {
        return DatabaseHelper.verkrijgSpelerBijId(this.SpelerID);
    }

    public Toernooi getToernooi() throws SQLException {
        return DatabaseHelper.verkrijgToernooiById(this.ToernooiID);
    }
}
