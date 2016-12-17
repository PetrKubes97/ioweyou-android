package cz.petrkubes.ioweyou.Pojos;

import org.json.JSONObject;

import java.util.Date;

import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * Created by petr on 30.11.16.
 */

public class Action {
    public Integer id;
    public String type;
    public Integer debtId;
    public Integer userId;
    public String note;
    public Date date;

    // Variables used for displaying other data in views
    public String userName;

    public Action(Integer id, String type, Integer debtId, Integer userId, String note, Date date) {
        this.id = id;
        this.type = type;
        this.debtId = debtId;
        this.userId = userId;
        this.note = note;
        this.date = date;
    }

    public static Action fromJson(JSONObject response) throws Exception {

        Date date = null;

        if (!response.getString("date").isEmpty()) {
            date = Tools.parseDate(response.getString("date"));
        }

        return new Action(
                response.getInt("id"),
                response.getString("type"),
                response.getInt("debtId"),
                response.getInt("userId"),
                response.getString("note"),
                date
        );
    }
}
