package cz.petrkubes.payuback.Structs;

/**
 * Created by petr on 21.10.16.
 */

public class Friend {

    public int id;
    public String name;
    public String email;
    public String facebookId;

    public Friend(int id, String name, String email, String facebookId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.facebookId = facebookId;
    }

    @Override
    public String toString() {
        return name;
    }
}
