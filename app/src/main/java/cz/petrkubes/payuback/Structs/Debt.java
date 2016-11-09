package cz.petrkubes.payuback.Structs;


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

    // Variables used for easier displaying of debts
    public String who;
    public String what;
    public String status;

    public Debt(Integer id, Integer creditorId, Integer debtorId, String customFriendName, Integer amount, Integer currencyId, String thingName, String note, Date paidAt, Date deletedAt, Date modifiedAt, Date createdAt) {
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
    }
}
