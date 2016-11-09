package cz.petrkubes.payuback.Structs;

/**
 * Created by petr on 21.10.16.
 */

public class Friend {

    public Integer id;
    public String name;
    public String email;

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
