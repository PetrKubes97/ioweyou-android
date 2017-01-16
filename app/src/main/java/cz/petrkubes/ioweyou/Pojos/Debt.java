package cz.petrkubes.ioweyou.Pojos;


import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.Date;

import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * @author Petr Kubes
 */
@Parcel
public class Debt {

    // Variables which are synchronized
    public Integer id;
    public Integer creditorId;
    public Integer debtorId;
    public String customFriendName;
    public Double amount;
    public Integer currencyId;
    public String thingName;
    public String note;
    public Date paidAt;
    public Date deletedAt;
    public Date modifiedAt;
    public Date createdAt;
    public Integer managerId;
    public Long version;

    // Variables used for easier displaying of debts
    public String who;
    public String what;
    public String status;
    public String currencyString;

    // Empty constructor for Parceler
    public Debt() {
    }

    public Debt(Integer id, Integer creditorId, Integer debtorId,
                String customFriendName, Double amount, Integer currencyId,
                String thingName, String note, Date paidAt,
                Date deletedAt, Date modifiedAt, Date createdAt, Integer managerId, Long version) {
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
        this.managerId = managerId;
    }

    /**
     * @param response JSONObject of the debt
     * @return Debt object
     * @throws Exception
     */
    public static Debt fromJson(JSONObject response) throws Exception {

        // It is necessary to convert dates strings to Date classes
        Date paidAt = null;
        Date deletedAt = null;
        Date createdAt = null;
        Date modifiedAt = null;
        Integer creditorId = null;
        Integer debtorId = null;
        Double amount = null;
        Integer currencyId = null;
        String customFriendName = null;
        String thingName = null;
        String note = null;
        Integer managerId = null;

        if (!response.getString("paidAt").isEmpty()) {
            paidAt = Tools.parseDate(response.getString("paidAt"));
        }

        if (!response.getString("deletedAt").isEmpty()) {
            deletedAt = Tools.parseDate(response.getString("deletedAt"));
        }

        if (!response.getString("createdAt").isEmpty()) {
            createdAt = Tools.parseDate(response.getString("createdAt"));
        }

        if (!response.getString("modifiedAt").isEmpty()) {
            modifiedAt = Tools.parseDate(response.getString("modifiedAt"));
        }

        if (!response.getString("creditorId").isEmpty()) {
            creditorId = response.getInt("creditorId");
        }

        if (!response.getString("debtorId").isEmpty()) {
            debtorId = response.getInt("debtorId");
        }

        if (!response.getString("amount").isEmpty()) {
            amount = response.getDouble("amount");
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

        if (!response.getString("managerId").isEmpty()) {
            managerId = response.getInt("managerId");
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
                managerId,
                response.getLong("version")
        );
    }

    /**
     * @param cursor Cursor located at desired debt
     * @return Debt object
     */
    public static Debt fromCursor(Cursor cursor) {
        Date paidAt = null;
        Date deletedAt = null;
        Date modifiedAt = null;
        Date createdAt = null;

        Double amount = null;
        Integer creditorId = null;
        Integer debtorId = null;
        Integer currencyId = null;

        String customFriendName = null;
        String note = null;
        String thing = null;

        Integer managerId = null;


        if (!cursor.isNull(8)) {
            paidAt = Tools.parseDate(cursor.getString(8));
        }

        if (!cursor.isNull(9)) {
            deletedAt = Tools.parseDate(cursor.getString(9));
        }

        if (!cursor.isNull(10)) {
            modifiedAt = Tools.parseDate(cursor.getString(10));
        }

        if (!cursor.isNull(11)) {
            createdAt = Tools.parseDate(cursor.getString(11));
        }

        if (cursor.getInt(4) != 0) {
            amount = cursor.getDouble(4);
        }

        if (!cursor.isNull(1)) {
            creditorId = cursor.getInt(1);
        }

        if (!cursor.isNull(2)) {
            debtorId = cursor.getInt(2);
        }

        if (!cursor.isNull(5)) {
            currencyId = cursor.getInt(5);
        }

        if (!cursor.isNull(3)) {
            customFriendName = cursor.getString(3);
        }

        if (!cursor.isNull(6)) {
            thing = cursor.getString(6);
        }

        if (!cursor.isNull(7)) {
            note = cursor.getString(7);
        }

        if (!cursor.isNull(12)) {
            managerId = cursor.getInt(12);
        }

        return new Debt(
                cursor.getInt(0),
                creditorId,
                debtorId,
                customFriendName,
                amount,
                currencyId,
                thing,
                note,
                paidAt,
                deletedAt,
                modifiedAt,
                createdAt,
                managerId,
                cursor.getLong(13));
    }

    /**
     * @return createdAt date converted to a string
     */
    public String createdAtString() {
        return Tools.formatDate(this.createdAt);
    }

    /**
     * @return deletedAtString date converted to a string
     */
    public String deletedAtString() {
        return Tools.formatDate(this.deletedAt);
    }

    /**
     * @return paidAt date converted to a string
     */
    public String paidAtString() {
        return Tools.formatDate(this.paidAt);
    }

    /**
     * @return Debt converted to a JSONObject
     * @throws JSONException
     */
    public JSONObject toJson() throws JSONException {
        String paidAt = "";
        String deletedAt = "";
        String modifiedAt = "";
        String createdAt = "";
        boolean lock = false;

        if (this.paidAt != null) {
            paidAt = Tools.formatDate(this.paidAt);
        }

        if (this.deletedAt != null) {
            deletedAt = Tools.formatDate(this.deletedAt);
        }

        if (this.modifiedAt != null) {
            modifiedAt = Tools.formatDate(this.modifiedAt);
        }

        if (this.createdAt != null) {
            createdAt = Tools.formatDate(this.createdAt);
        }

        if (this.managerId != null) { // Send only a boolean to prevent hackers from creating uneditable debts
            lock = true;
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
        debtJson.put("lock", lock);
        debtJson.put("version", this.version);

        return debtJson;
    }
}
