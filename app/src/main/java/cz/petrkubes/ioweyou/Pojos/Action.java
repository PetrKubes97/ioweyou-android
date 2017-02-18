package cz.petrkubes.ioweyou.Pojos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
    public ArrayList<String> messages;

    // messages constants
    public static final String MESSAGE_DEBT_NEW = "debt_new";
    public static final String MESSAGE_DEBT_MARKED_AS_PAID = "debt_marked_as_paid";
    public static final String MESSAGE_DEBT_MARKED_AS_UNPAID = "debt_marked_as_unpaid";
    public static final String MESSAGE_DEBT_DELETED = "debt_deleted";
    public static final String MESSAGE_DEBT_RESTORED = "debt_restored";
    public static final String MESSAGE_DEBT_FRIEND_NAME_CHANGED = "debt_friend_name_changed";
    public static final String MESSAGE_DEBT_AMOUNT_CHANGED = "debt_amount_changed";
    public static final String MESSAGE_DEBT_CURRENCY_CHANGED = "debt_currency_changed";
    public static final String MESSAGE_DEBT_MONEY_TO_THING = "debt_money_to_thing";
    public static final String MESSAGE_DEBT_THING_TO_MONEY = "debt_thing_to_money";
    public static final String MESSAGE_DEBT_THING_NAME_CHANGED = "debt_thing_name_changed";
    public static final String MESSAGE_DEBT_NOTE_CHANGED = "debt_note_changed";
    public static final String MESSAGE_DEBT_CREDITOR_DEBTOR_SWITCHED = "debt_creditor_debtor_switched";
    public static final String MESSAGE_DEBT_PERMISSION_CHANGED = "debt_permission_changed";

    public Action(Integer id, String type, Integer debtId, Integer user1Id, String user1Name, Integer user2Id, String user2Name, String note, Date date, ArrayList messages) {
        this.id = id;
        this.type = type;
        this.debtId = debtId;
        this.user1Id = user1Id;
        this.user1Name = user1Name;
        this.user2Id = user2Id;
        this.user2Name = user2Name;
        this.note = note;
        this.date = date;
        this.messages = messages;
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

        ArrayList<String> messages = new ArrayList<>();
        JSONArray jsonMessages = response.getJSONArray("messages");

        for (int i = 0; i < jsonMessages.length(); i++) {
            messages.add(jsonMessages.getJSONObject(i).getString("note"));
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
                date,
                messages

        );
    }
}
