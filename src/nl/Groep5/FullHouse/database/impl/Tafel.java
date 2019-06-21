package nl.Groep5.FullHouse.database.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tafel {
    private int ID, toernooiID;

    public Tafel(int toernooiID) {
        this.toernooiID = toernooiID;
    }

    public Tafel(ResultSet resultSet) throws SQLException {
        this.ID = resultSet.getInt("ID");
        this.toernooiID = resultSet.getInt("toernooiID");
    }

    public int getID(){
        return ID;
    }

    public int getToernooiID(){
        return toernooiID;
    }

    public void setToernooiID(int ID){
        this.toernooiID = ID;
    }
}
