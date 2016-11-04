package cz.petrkubes.payuback.Structs;


import java.sql.Date;

/**
 * Created by petr on 4.11.16.
 */

public class Debt {

    public Integer creatorId;
    public Integer debtorId;
    public String customFriendName;
    public Integer amount;
    public Integer currencyId;
    public String thingName;
    public String note;
    public Date paidAt;
    public Date deletedAt;
    public Date modifiedAt;

    public Debt(Integer creatorId, Integer debtorId, String customFriendName, Integer amount, Integer currencyId, String thingName, String note, Date paidAt, Date deletedAt, Date modifiedAt) {
        this.creatorId = creatorId;
        this.debtorId = debtorId;
        this.customFriendName = customFriendName;
        this.amount = amount;
        this.currencyId = currencyId;
        this.thingName = thingName;
        this.note = note;
        this.paidAt = paidAt;
        this.deletedAt = deletedAt;
        this.modifiedAt = modifiedAt;
    }
}
