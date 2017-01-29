package cz.petrkubes.ioweyou.Pojos;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Petr Kubes
 */
@Parcel
public class Friend {

    public Integer id;
    public String name;
    public String email;

    // Variables used for displaying friends
    public String debtsString;
    public ArrayList<Debt> debts;
    public HashMap<String, Double> totals;

    // Empty constructor for Parceler
    public Friend() {
    }

    public Friend(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return name;
    }
}
