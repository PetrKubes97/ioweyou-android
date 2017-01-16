package cz.petrkubes.ioweyou.Tools;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class including useful functions used on many places throughout the project
 *
 * @author Petr Kubes
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

    /**
     * Removes diacritics from a string
     *
     * @param text input text
     * @return text without diacritics
     */
    public static String removeDiacritics(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return text;
    }

}
