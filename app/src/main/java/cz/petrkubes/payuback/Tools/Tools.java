package cz.petrkubes.payuback.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by petr on 19.11.16.
 */

public class Tools {

    public static String formatDateTime(Date date) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (date != null) {
            return df.format(date);
        } else {
            return null;
        }
    }

}
