package cz.petrkubes.payuback.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Structs.Currency;
import cz.petrkubes.payuback.Structs.Debt;
import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 20;

    // Database Name
    private static final String DATABASE_NAME = "payUBack.db";

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DEBTS = "debts";
    private static final String TABLE_FRIENDS = "friends";
    private static final String TABLE_CURRENCIES = "currencies";

    // Table column names
    private static final String USERS_KEY_ID = "id";
    private static final String USERS_KEY_NAME = "name";
    private static final String USERS_KEY_EMAIL = "email";
    private static final String USERS_KEY_API_KEY = "api_key";
    private static final String USERS_KEY_REGISTERED_AT = "registered_at";

    private static final String FRIENDS_KEY_ID = "id";
    private static final String FRIENDS_KEY_NAME = "name";
    private static final String FRIENDS_KEY_EMAIL = "email";

    private static final String CURRENCIES_KEY_ID = "id";
    private static final String CURRENCIES_KEY_SYMBOL = "symbol";

    private static final String DEBTS_KEY_ID = "id";
    private static final String DEBTS_KEY_CREDITOR_ID = "creditor_id";
    private static final String DEBTS_KEY_DEBTOR_ID = "debtor_id";
    private static final String DEBTS_KEY_CUSTOM_FRIEND_NAME = "custom_friend_name";
    private static final String DEBTS_KEY_AMOUNT = "amount";
    private static final String DEBTS_KEY_CURRENCY_ID = "currency_id";
    private static final String DEBTS_KEY_THING_NAME = "thing_name";
    private static final String DEBTS_KEY_NOTE = "note";
    private static final String DEBTS_KEY_PAID_AT = "paid_at";
    private static final String DEBTS_KEY_DELETED_AT = "deleted_at";
    private static final String DEBTS_KEY_MODIFIED_AT = "modified_at";
    private static final String DEBTS_KEY_CREATED_AT = "created_at";
    private static final String DEBTS_KEY_VERSION = "version";

    // Strings including all columns
    private String[] userProjection = new String[] {
            USERS_KEY_ID,
            USERS_KEY_API_KEY,
            USERS_KEY_NAME,
            USERS_KEY_EMAIL,
            USERS_KEY_REGISTERED_AT};

    private String[] friendProjection = new String[] {
            FRIENDS_KEY_ID,
            FRIENDS_KEY_NAME,
            FRIENDS_KEY_EMAIL};

    private String[] currenciesProjection = new String[] {
            CURRENCIES_KEY_ID,
            CURRENCIES_KEY_SYMBOL};

    private String[] debtProjection = new String[] {
            DEBTS_KEY_ID,
            DEBTS_KEY_CREDITOR_ID,
            DEBTS_KEY_DEBTOR_ID,
            DEBTS_KEY_CUSTOM_FRIEND_NAME,
            DEBTS_KEY_AMOUNT,
            DEBTS_KEY_CURRENCY_ID,
            DEBTS_KEY_THING_NAME,
            DEBTS_KEY_NOTE,
            DEBTS_KEY_PAID_AT,
            DEBTS_KEY_DELETED_AT,
            DEBTS_KEY_MODIFIED_AT,
            DEBTS_KEY_CREATED_AT,
            DEBTS_KEY_VERSION
    };

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                USERS_KEY_ID + " INTEGER PRIMARY KEY, " +
                USERS_KEY_NAME + " TEXT, " +
                USERS_KEY_EMAIL + " TEXT, " +
                USERS_KEY_API_KEY + " TEXT, " +
                USERS_KEY_REGISTERED_AT + " NUMERIC);";

        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + " (" +
                FRIENDS_KEY_ID + " INTEGER PRIMARY KEY, " +
                FRIENDS_KEY_NAME + " TEXT, " +
                FRIENDS_KEY_EMAIL + " TEXT);";

        String CREATE_CURRENCIES_TABLE = "CREATE TABLE " + TABLE_CURRENCIES + " (" +
                CURRENCIES_KEY_ID + " INTEGER PRIMARY KEY, " +
                CURRENCIES_KEY_SYMBOL + " TEXT);";

        String CREATE_DEBTS_TABLE = "CREATE TABLE " + TABLE_DEBTS + " (" +
                DEBTS_KEY_ID + " INTEGER PRIMARY KEY, " +
                DEBTS_KEY_CREDITOR_ID + " INTEGER, " +
                DEBTS_KEY_DEBTOR_ID + " INTEGER, " +
                DEBTS_KEY_CUSTOM_FRIEND_NAME + " TEXT NULL, " +
                DEBTS_KEY_AMOUNT + " NUMERIC, " +
                DEBTS_KEY_CURRENCY_ID + " INTEGER, " +
                DEBTS_KEY_THING_NAME + " TEXT NULL, " +
                DEBTS_KEY_NOTE + " TEXT, " +
                DEBTS_KEY_PAID_AT + " NUMERIC, " +
                DEBTS_KEY_DELETED_AT + " NUMERIC, " +
                DEBTS_KEY_CREATED_AT + " NUMERIC, " +
                DEBTS_KEY_MODIFIED_AT + " NUMERIC, " +
                DEBTS_KEY_VERSION +" INTEGER);";

        db.execSQL(CREATE_DEBTS_TABLE);
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CURRENCIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCIES);

        // Create tables again
        onCreate(db);
    }

    public void addOrUpdateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_USERS, new String[] {USERS_KEY_ID}, USERS_KEY_ID + "=?",
                new String[] {String.valueOf(user.id)}, null, null, null);

        ContentValues values = new ContentValues();

        // Update only non-null values
        values.put(USERS_KEY_ID, user.id);
        if (user.email != null) {values.put(USERS_KEY_EMAIL, user.email);}
        if (user.name != null) {values.put(USERS_KEY_NAME, user.name);}
        if (user.apiKey != null) {values.put(USERS_KEY_API_KEY, user.apiKey);}
        if (user.registredAt != null) {values.put(USERS_KEY_REGISTERED_AT, String.valueOf(user.registredAt));}

        if (cursor.getCount() > 0) {
            // User does exist, let's update his data
            db.update(TABLE_USERS, values, null, null);
        } else {
            // User does not exist, let's add him to the database
            db.insert(TABLE_USERS, null, values);
        }

        db.close();
        cursor.close();
    }

    /**
     * Returns currently logged in user
     * @return User
     */
    public User getUser() {
        User user = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, userProjection, null, null, null, null, null);

        // It is not necessary to loop through the result because there should always be at most 1 user row
        if (cursor.moveToFirst())
        {
             user = new User(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    null
            );
        }

        cursor.close();
        db.close();

        return user;
    }

    /**
     * Save friend object to the database
     * @param friend
     * @throws Exception
     */
    public void addFriend(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_FRIENDS, new String[] {FRIENDS_KEY_ID}, FRIENDS_KEY_ID + "=?",
                new String[] {String.valueOf(friend.id)}, null, null, null);

        if (cursor.getCount() > 0) {
            return;
        }

        // Add friend into the database
        ContentValues values = new ContentValues();
        values.put(FRIENDS_KEY_ID, friend.id);
        values.put(FRIENDS_KEY_EMAIL, friend.email);
        values.put(FRIENDS_KEY_NAME, friend.name);

        db.insert(TABLE_FRIENDS, null, values);

        db.close();
        cursor.close();
    }

    /**
     * Get list of friends from the database
     * @return ArrayList<Friend>
     */
    public ArrayList<Friend> getFriends() {
        ArrayList<Friend> list = new ArrayList<Friend>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FRIENDS, friendProjection, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                list.add(new Friend(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)));
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Get friend by id
     * @return Friend
     */
    public Friend getFriend(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FRIENDS, friendProjection, FRIENDS_KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null);

        Friend friend = null;

        if (cursor.moveToFirst())
        {
            friend = new Friend(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2));
        }

        cursor.close();
        db.close();

        return friend;
    }

    /**
     * Inserts currency into local database
     * @param currency currency received from the web
     * @throws Exception
     */
    public void addCurrency(Currency currency) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_CURRENCIES, new String[] {CURRENCIES_KEY_ID}, CURRENCIES_KEY_ID + "=?",
                new String[] {String.valueOf(currency.id)}, null, null, null);

        if (cursor.getCount() > 0) {
            return;
        }

        // Add currency into the database
        ContentValues values = new ContentValues();
        values.put(CURRENCIES_KEY_ID, currency.id);
        values.put(CURRENCIES_KEY_SYMBOL, currency.symbol);

        db.insert(TABLE_CURRENCIES, null, values);

        db.close();
        cursor.close();
    }

    /**
     * Returns list of currencies in local database
     * @return ArrayList<Currency>
     */
    public ArrayList<Currency> getCurrencies() {
        ArrayList<Currency> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENCIES, currenciesProjection, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                list.add(new Currency(
                        cursor.getInt(0),
                        cursor.getString(1)));
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Get currency by id
     * @return Currency
     */
    public Currency getCurrency(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENCIES, currenciesProjection, CURRENCIES_KEY_ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null);

        Currency currency = null;

        if (cursor.moveToFirst())
        {
            currency = new Currency(
                    cursor.getInt(0),
                    cursor.getString(1));
        }

        cursor.close();
        db.close();

        return currency;
    }

    /**
     * Returns list of debts
     * @return ArrayList<Debt>
     */
    public ArrayList<Debt> getDebts() {
        ArrayList<Debt> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEBTS, debtProjection, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do {
                list.add(debtFromCursor(cursor));
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return list;
    }

    /**
     * Update debt.
     * @param currentId Current id in local database
     * @param debt Current version of the debt
     * @throws Exception
     */
    public void addOrUpdateDebt(Integer currentId, Debt debt) {

        // TODO Exception?
        if (debt == null) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        DateFormat df = new SimpleDateFormat();

        // TODO kontrolovat verze
        // Get current debt
        Cursor cursor = db.query(TABLE_DEBTS, new String[] {DEBTS_KEY_ID}, DEBTS_KEY_ID + "=?",
                new String[] {String.valueOf(currentId)}, null, null, null);

        // Checks if we are updating a debt or adding a new one.
        if (currentId == null || cursor.getCount() < 1) {

            if (debt.id != null) { // It's a debt from the web
                Log.d(Const.TAG, "Adding a downloaded debt");
                // Checks if debt doesn't already exist
                cursor = db.query(TABLE_DEBTS, new String[] {DEBTS_KEY_ID}, DEBTS_KEY_ID + "= ?",
                        new String[] {String.valueOf(debt.id)}, null, null, null);
                if (cursor.getCount() > 0) {
                    return;
                }

            } else { // It's a new debt, we assign it a negative id, so there are not any conflicts with server database
                // Find the currently lowest id in the database
                int lowestId = 0;

                cursor = db.query(TABLE_DEBTS, new String[] {DEBTS_KEY_ID}, null,
                        null, null, null, DEBTS_KEY_ID + " ASC");

                if (cursor.moveToFirst()) {
                    if (cursor.getInt(0) < 0)
                        lowestId = cursor.getInt(0);
                }

                debt.id = lowestId-1;
            }
        }

        // Convert date to string
        String paidAt = null;
        String deletedAt = null;

        if (debt.paidAt != null) {
            paidAt = df.format(debt.paidAt);
        }

        if (debt.deletedAt != null) {
            deletedAt = df.format(debt.deletedAt);
        }

        ContentValues values = new ContentValues();
        values.put(DEBTS_KEY_ID, debt.id);
        values.put(DEBTS_KEY_CREDITOR_ID, debt.creditorId);
        values.put(DEBTS_KEY_DEBTOR_ID, debt.debtorId);
        values.put(DEBTS_KEY_CUSTOM_FRIEND_NAME, debt.customFriendName);
        values.put(DEBTS_KEY_AMOUNT, debt.amount);
        values.put(DEBTS_KEY_CURRENCY_ID, debt.currencyId);
        values.put(DEBTS_KEY_THING_NAME, debt.thingName);
        values.put(DEBTS_KEY_NOTE, debt.note);
        values.put(DEBTS_KEY_PAID_AT, paidAt);
        values.put(DEBTS_KEY_DELETED_AT, deletedAt);
        values.put(DEBTS_KEY_MODIFIED_AT, df.format(debt.modifiedAt));
        values.put(DEBTS_KEY_CREATED_AT, df.format(debt.createdAt));
        values.put(DEBTS_KEY_VERSION, debt.version);

        if (currentId == null || cursor.getCount() < 1) {
            // Debt doesn't exist
            db.insert(TABLE_DEBTS, null, values);
        } else {
            // Update debt id
            db.update(TABLE_DEBTS, values, DEBTS_KEY_ID + "=?", new String[] {String.valueOf(currentId)});
        }

        db.close();
        cursor.close();
    }

    /**
     * Returns list of debts with added variables for displaying
     * @param my If true, returns only debts which user owes, otherwise debts he will collect
     * @return ArrayList<Debt>
     */
    public ArrayList<Debt> getExtendedDebts(boolean my, int userId) {
        ArrayList<Debt> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (my) {
            cursor = db.query(TABLE_DEBTS, debtProjection, DEBTS_KEY_DEBTOR_ID + "=? AND " + DEBTS_KEY_PAID_AT + " IS NULL AND "+DEBTS_KEY_DELETED_AT + " IS NULL", new String[] {String.valueOf(userId)}, null, null, DEBTS_KEY_CREATED_AT + " DESC");
        } else {
            cursor = db.query(TABLE_DEBTS, debtProjection, DEBTS_KEY_CREDITOR_ID + "=? AND " + DEBTS_KEY_PAID_AT + " IS NULL AND "+DEBTS_KEY_DELETED_AT + " IS NULL", new String[] {String.valueOf(userId)}, null, null, DEBTS_KEY_CREATED_AT + " DESC");
        }

        if (cursor.moveToFirst())
        {
            do {
                Debt debt = debtFromCursor(cursor);

                // Set additional variables

                // 1. Name of person
                if (my) {
                    if (debt.creditorId == 0) {
                        debt.who = debt.customFriendName;
                    } else {
                        Friend friend = getFriend(debt.creditorId);
                        if (friend != null) {
                            debt.who = friend.name;
                        } else {
                            debt.who = "Error :-(";
                        }

                    }
                } else {
                    if (debt.debtorId == 0) {
                        debt.who = debt.customFriendName;
                    } else {
                        Friend friend = getFriend(debt.debtorId);
                        if (friend != null) {
                            debt.who = friend.name;
                        } else {
                            debt.who = "Error :-(";
                        }
                    }
                }

                // 2. money or thing that is owed
                if (debt.amount != null) {
                    debt.currencyString = String.valueOf(getCurrency(debt.currencyId));
                    debt.what = String.valueOf(debt.amount) + " " + debt.currencyString;
                } else {
                    debt.what = debt.thingName;
                }

                // 3. status
                if (debt.id < 0) {
                    debt.status = "not synced";
                } else {
                    debt.status = "synced";
                }

                list.add(debt);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return list;
    }


    /**
     * Truncates debts table
     */
    public void removeOfflineDebts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DEBTS);
        db.close();
    }

    // TODO merge this function to addOrUpdate
    /**
     * Inserts a debt into local database
     * @param debt debt object
     * @throws Exception
     */
    public void addDebt(Debt debt) {
        SQLiteDatabase db = this.getWritableDatabase();

        DateFormat df = new SimpleDateFormat();

        String paidAt = null;
        String deletedAt = null;

        if (debt.paidAt != null) {
            paidAt = df.format(debt.paidAt);
        }

        if (debt.deletedAt != null) {
            deletedAt = df.format(debt.deletedAt);
        }

        ContentValues values = new ContentValues();
        values.put(DEBTS_KEY_ID, debt.id);
        values.put(DEBTS_KEY_CREDITOR_ID, debt.creditorId);
        values.put(DEBTS_KEY_DEBTOR_ID, debt.debtorId);
        values.put(DEBTS_KEY_CUSTOM_FRIEND_NAME, debt.customFriendName);
        values.put(DEBTS_KEY_AMOUNT, debt.amount);
        values.put(DEBTS_KEY_CURRENCY_ID, debt.currencyId);
        values.put(DEBTS_KEY_THING_NAME, debt.thingName);
        values.put(DEBTS_KEY_NOTE, debt.note);
        values.put(DEBTS_KEY_PAID_AT, paidAt);
        values.put(DEBTS_KEY_DELETED_AT, deletedAt);
        values.put(DEBTS_KEY_MODIFIED_AT, df.format(debt.modifiedAt));
        values.put(DEBTS_KEY_CREATED_AT, df.format(debt.createdAt));
        values.put(DEBTS_KEY_VERSION, debt.version);

        // Insert debt into the database
        db.insert(TABLE_DEBTS, null, values);
        db.close();
    }


    private Debt debtFromCursor(Cursor cursor) {

        DateFormat df = new SimpleDateFormat();

        Date paidAt = null;
        Date deletedAt = null;
        Date modifiedAt = null;
        Date createdAt = null;

        Integer amount = null;


        try {
            if (cursor.getString(8) != null) {
                    paidAt = df.parse(cursor.getString(8));
            }

            if (cursor.getString(9) != null) {
                deletedAt = df.parse(cursor.getString(9));
            }

            if (cursor.getString(10) != null) {
                modifiedAt = df.parse(cursor.getString(10));
            }

            if (cursor.getString(11) != null) {
                createdAt = df.parse(cursor.getString(11));
            }

            if (cursor.getInt(4) != 0) {
                amount = cursor.getInt(4);
            }

            return new Debt(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    amount,
                    cursor.getInt(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    paidAt,
                    deletedAt,
                    modifiedAt,
                    createdAt,
                    cursor.getInt(12));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
