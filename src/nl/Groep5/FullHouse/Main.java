package nl.Groep5.FullHouse;

import nl.Groep5.FullHouse.UI.InlogScherm;
import nl.Groep5.FullHouse.database.MySQLConnector;

import javax.swing.*;
import java.io.*;
import java.util.Properties;

public class Main {

    private static MySQLConnector mysqlConnection;

    public static void main(String[] args) {
        if (!createDbConnection()) {
            return; // sluit applicatie
        }

        try {
            // zet de style naar de operatie systeem style (voor meeste van ons, windows style)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.out.println("Unsupported Look and feel, defaulting to normal");
        }

        new InlogScherm();

    }

    public static boolean createDbConnection() {
        if (mysqlConnection == null) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream("config.config"));
            } catch (IOException e) {
//            e.printStackTrace();

                try {
                    PrintWriter writer = new PrintWriter("config.config", "UTF-8");

                    writer.println("host=");
                    writer.println("database=");
                    writer.println("username=");
                    writer.println("password=");
                    writer.close();
                    System.out.println("config bestand gemaakt, bewerk deze voor gebruik");
                    return false;
                } catch (FileNotFoundException | UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
            }

            if (props.containsKey("host") && props.containsKey("database") && props.containsKey("username") && props.containsKey("password")) {
                mysqlConnection = new MySQLConnector(props.getProperty("host"), props.getProperty("database"), props.getProperty("username"), props.getProperty("password"));

                // Als de applicatie sluit, sluit dan ook de connectie met DB op een nette manier
                Runtime.getRuntime().addShutdownHook(new Thread(Main.mysqlConnection::close));

            } else {
                System.out.println("Missing mysql info, exiting");
                return false;
            }
        }
        // Geen fouten of db connectie bestaat al
        return true;
    }

    public static MySQLConnector getMySQLConnection() {
        return mysqlConnection;
    }
}
