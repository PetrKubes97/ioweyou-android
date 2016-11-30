package cz.petrkubes.payuback.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by petr on 19.11.16.
 */

public class Tools {

    public static String formatDate(Date date) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (date != null) {
            return df.format(date);
        } else {
            return null;
        }
    }

    public static Date parseDate(String date) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
