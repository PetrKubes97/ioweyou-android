package cz.petrkubes.payuback.Structs;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by petr on 21.10.16.
 */

public class User {

    public int id;
    public String email;
    public String name;
    public String facebookId;
    public String facebookToken;
    public Date registredAt;
    public ArrayList<Friend> friends;

    public User(int id, String email, String name, String facebookId, String facebookToken, Date registredAt, ArrayList<Friend> friends) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.facebookId = facebookId;
        this.facebookToken = facebookToken;
        this.registredAt = registredAt;
        this.friends = friends;
    }


}
