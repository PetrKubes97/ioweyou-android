package cz.petrkubes.ioweyou.Pojos;

import org.json.JSONObject;

import java.util.Date;

import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * @author Petr Kubes
 */
public class Action {

    public Integer id;
    public String type;
    public Integer debtId;
    public Integer user1Id;
    public String user1Name;
    public Integer user2Id;
    public String user2Name;
    public String note;
    public Date date;

    public Action(Integer id, String type, Integer debtId, Integer user1Id, String user1Name, Integer user2Id, String user2Name, String note, Date date) {
        this.id = id;
        this.type = type;
        this.debtId = debtId;
        this.user1Id = user1Id;
        this.user1Name = user1Name;
        this.user2Id = user2Id;
        this.user2Name = user2Name;
        this.note = note;
        this.date = date;
    }

    /**
     * Generates action object from a JSONObject
     *
     * @param response JSONObject
     * @return Action
     * @throws Exception
     */
    public static Action fromJson(JSONObject response) throws Exception {

        Date date = null;
        Integer user2Id = null; // can be null when the user2 is customFriendName

        if (!response.getString("date").isEmpty()) {
            date = Tools.parseDate(response.getString("date"));
        }

        if (!response.getString("user2Id").isEmpty()) {
            user2Id = response.getInt("user2Id");
        }

        return new Action(
                response.getInt("id"),
                response.getString("type"),
                response.getInt("debtId"),
                response.getInt("user1Id"),
                response.getString("user1Name"),
                user2Id,
                response.getString("user2Name"),
                response.getString("note"),
                date
        );
    }
}
