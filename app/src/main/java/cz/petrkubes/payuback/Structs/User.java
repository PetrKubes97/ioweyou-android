package cz.petrkubes.payuback.Structs;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by petr on 21.10.16.
 */

public class User {

    public int id;
    public String apiKey;
    public String email;
    public String name;
    public Date registredAt;

    public User(int id, String apiKey, String email, String name, Date registredAt) {
        this.id = id;
        this.apiKey = apiKey;
        this.email = email;
        this.name = name;
        this.registredAt = registredAt;
    }


}
