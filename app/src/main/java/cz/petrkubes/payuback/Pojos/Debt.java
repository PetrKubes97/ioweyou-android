package cz.petrkubes.payuback.Pojos;


import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.petrkubes.payuback.Tools.Tools;

/**
 * Created by petr on 4.11.16.
 */

@Parcel
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
    public String currencyString;

    // Empty constructor for Parceler
    public Debt() {};

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

    public String createdAtString() {
        return Tools.formatDate(this.createdAt);
    }

    public String deletedAtString() {
        return Tools.formatDate(this.deletedAt);
    }

    public String paidAtString() {
        return Tools.formatDate(this.paidAt);
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
        String customFriendName = null;
        String thingName = null;
        String note = null;

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

        if (!response.getString("customFriendName").isEmpty()) {
            customFriendName = response.getString("customFriendName");
        }

        if (!response.getString("thingName").isEmpty()) {
            thingName = response.getString("thingName");
        }

        if (!response.getString("note").isEmpty()) {
            note = response.getString("note");
        }

        return new Debt(
                response.getInt("id"),
                creditorId,
                debtorId,
                customFriendName,
                amount,
                currencyId,
                thingName,
                note,
                paidAt,
                deletedAt,
                modifiedAt,
                createdAt,
                response.getInt("version")
        );
    }
}
