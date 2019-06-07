package nl.Groep5.FullHouse;

import nl.Groep5.FullHouse.database.impl.Speler;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by DeStilleGast 6-6-2019
 */
public class DebugingTest {


    public static void main(String[] args) {
        if (Main.createDbConnection()) {
            fillDbWithSpelers();
        }
    }

    // vul database met test (nep) spelers
    private static void fillDbWithSpelers() {
        List<String> woonplaatsen = Arrays.asList("Rijswijk", "Den haag", "Delft", "Amsterdam", "Rotterdam", "Groningen", "Brussel", "New york", "Wateringen", "Scheveningen", "Stompwijk", "Zoetermeer", "Vlaardingen", "Maasdam");
        List<String> emails = Arrays.asList("@gmail.com", "@hotmail.com", "@yahoo.com", "@live.com");

        Random rnd = new SecureRandom();
        for (int i = 0; i < 250; i++) {
            // https://stackoverflow.com/questions/3985392/generate-random-date-of-birth
            int minDay = (int) LocalDate.of(1900, 1, 1).toEpochDay(); // included
            int maxDay = (int) LocalDate.of(2015, 1, 1).toEpochDay(); // excluded
            long randomDay = minDay + rnd.nextInt(maxDay - minDay);

            LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);


            Speler s = new Speler("Speler" + i,                                                                //voornaam
                    (rnd.nextBoolean() ? "Van der" : ""),                                                    // tussenvoegsel
                    randomString(10, rnd),                                                               // achternaam
                    String.format("%s %s", randomString(10, rnd), i),                                    // address
                    randomString("1234567890", 4, rnd) + randomString(2, rnd),         // postcode
                    woonplaatsen.get(rnd.nextInt(woonplaatsen.size())),                                       // woonplaats
                    randomString("1234567890", 8, rnd),                                            // telefoonnummer
                    randomString(7, rnd) + emails.get(rnd.nextInt(emails.size())),                 // email
                    rnd.nextBoolean() ? 'M' : 'V',                                                           // geslacht
                    java.sql.Date.valueOf(randomBirthDate),                                                 // geboorte
                    rnd.nextDouble() * 10                                                             //rating
            );

            try {
                if (s.Save()) {
                    System.out.println(String.format("Speler %s opgeslagen !", i));
                } else {
                    System.out.println("IETS GAAT FOUT (" + i + ")");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static String randomString(int len, Random rnd) {
        return randomString(AB, len, rnd);
    }

    private static String randomString(String seed, int len, Random rnd) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(seed.charAt(rnd.nextInt(seed.length())));
        return sb.toString();
    }
}
