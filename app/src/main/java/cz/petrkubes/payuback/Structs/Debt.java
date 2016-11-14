package cz.petrkubes.payuback.Structs;


import android.content.ContentValues;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by petr on 4.11.16.
 */

public class Debt {

    // Variables which are synchronized
    public Integer id;
    public Integer creditorId;
    public Integer debtorId;
    public String customFriendName;
    public Integer amount;
    public Integer currencyId;
    public String thingName;
    public String note;
    public Date paidAt;
    public Date deletedAt;
    public Date modifiedAt;
    public Date createdAt;
    public Integer version;

    // Variables used for easier displaying of debts
    public String who;
    public String what;
    public String status;

    public Debt(Integer id, Integer creditorId, Integer debtorId,
                String customFriendName, Integer amount, Integer currencyId,
                String thingName, String note, Date paidAt,
                Date deletedAt, Date modifiedAt, Date createdAt, Integer version) {
        this.id = id;
        this.creditorId = creditorId;
        this.debtorId = debtorId;
        this.customFriendName = customFriendName;
        this.amount = amount;
        this.currencyId = currencyId;
        this.thingName = thingName;
        this.note = note;
        this.paidAt = paidAt;
        this.deletedAt = deletedAt;
        this.modifiedAt = modifiedAt;
        this.createdAt = createdAt;
        this.version = version;
    }

    public JSONObject toJson() throws JSONException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String paidAt = "";
        String deletedAt = "";
        String modifiedAt = "";
        String createdAt = "";

        if (this.paidAt != null) {
            paidAt = df.format(this.paidAt);
        }

        if (this.deletedAt != null) {
            deletedAt = df.format(this.deletedAt);
        }

        if (this.modifiedAt != null) {
            modifiedAt = df.format(this.modifiedAt);
        }

        if (this.createdAt != null) {
            createdAt = df.format(this.createdAt);
        }

        JSONObject debtJson = new JSONObject();
        debtJson.put("id", this.id);
        debtJson.put("creditorId", this.creditorId);
        debtJson.put("debtorId", this.debtorId);
        debtJson.put("customFriendName", this.customFriendName);
        debtJson.put("amount", this.amount);
        debtJson.put("currencyId", this.currencyId);
        debtJson.put("thingName", this.thingName);
        debtJson.put("note", this.note);
        debtJson.put("paidAt", paidAt);
        debtJson.put("deletedAt", deletedAt);
        debtJson.put("modifiedAt", modifiedAt);
        debtJson.put("createdAt", createdAt);
        debtJson.put("version", this.version);

        return debtJson;
    }

    public static Debt fromJson(JSONObject response) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // It is necessary to convert dates strings to Date classes
        Date paidAt = null;
        Date deletedAt = null;
        Date createdAt = null;
        Date modifiedAt = null;
        Integer creditorId = null;
        Integer debtorId = null;
        Integer amount = null;
        Integer currencyId = null;

        if (!response.getString("paidAt").isEmpty()) {
            paidAt = df.parse(response.getString("paidAt"));
        }

        if (!response.getString("deletedAt").isEmpty()) {
            deletedAt = df.parse(response.getString("deletedAt"));
        }

        if (!response.getString("createdAt").isEmpty()) {
            createdAt = df.parse(response.getString("createdAt"));
        }

        if (!response.getString("modifiedAt").isEmpty()) {
            modifiedAt = df.parse(response.getString("modifiedAt"));
        }

        if (!response.getString("creditorId").isEmpty()) {
            creditorId = response.getInt("creditorId");
        }

        if (!response.getString("debtorId").isEmpty()) {
            debtorId = response.getInt("debtorId");
        }

        if (!response.getString("amount").isEmpty()) {
            amount = response.getInt("amount");
        }

        if (!response.getString("currencyId").isEmpty()) {
            currencyId = response.getInt("currencyId");
        }

        return new Debt(
                response.getInt("id"),
                creditorId,
                debtorId,
                response.getString("customFriendName"),
                amount,
                currencyId,
                response.getString("thingName"),
                response.getString("note"),
                paidAt,
                deletedAt,
                modifiedAt,
                createdAt,
                response.getInt("version")
        );
    }
}