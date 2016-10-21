package cz.petrkubes.payuback.Structs;

/**
 * Created by petr on 21.10.16.
 */

public class Friend {

    public int id;
    public int name;
    public int email;
    public int facebookId;

    public Friend(int id, int name, int email, int facebookId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.facebookId = facebookId;
    }
}
