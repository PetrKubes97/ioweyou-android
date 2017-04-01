package cz.petrkubes.ioweyou.Pojos;

import java.util.Date;

/**
 * @author Petr Kubes
 */
public class User {

    public Integer id;
    public String apiKey;
    public String email;
    public String name;
    public Date registeredAt;

    public User(Integer id, String apiKey, String email, String name, Date registeredAt) {
        this.id = id;
        this.apiKey = apiKey;
        this.email = email;
        this.name = name;
        this.registeredAt = registeredAt;
    }


}
