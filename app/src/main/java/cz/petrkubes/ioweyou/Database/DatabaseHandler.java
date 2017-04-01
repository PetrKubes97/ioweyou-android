package cz.petrkubes.ioweyou.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.petrkubes.ioweyou.Pojos.Action;
import cz.petrkubes.ioweyou.Pojos.Currency;
import cz.petrkubes.ioweyou.Pojos.Debt;
import cz.petrkubes.ioweyou.Pojos.Friend;
import cz.petrkubes.ioweyou.Pojos.User;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * Class for handling all reading and writing to the local database
 *
 * @author Petr Kubes
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 25;
    // Database Name
    private static final String DATABASE_NAME = "payUBack.db";
    // Tables names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DEBTS = "debts";
    private static final String TABLE_FRIENDS = "friends";
    private static final String TABLE_CURRENCIES = "currencies";
    private static final String TABLE_ACTIONS = "actions";
    private static final String TABLE_ACTIONS_MESSAGES = "actions_messages";
    // Column names
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
    private static final String DEBTS_KEY_MANAGER_ID = "manager_id";
    private static final String DEBTS_KEY_VERSION = "version";
    private static final String ACTIONS_KEY_ID = "id";
    private static final String ACTIONS_KEY_TYPE = "type";
    private static final String ACTIONS_KEY_DEBT_ID = "debt_id";
    private static final String ACTIONS_KEY_USER1_ID = "user1_id";
    private static final String ACTIONS_KEY_USER1_NAME = "user1_name";
    private static final String ACTIONS_KEY_USER2_ID = "user2_id";
    private static final String ACTIONS_KEY_USER2_NAME = "user2_name";
    private static final String ACTIONS_KEY_NOTE = "note";
    private static final String ACTIONS_KEY_DATE = "date";
    private static final String ACTIONS_MESSAGES_KEY_ID = "id";
    private static final String ACTIONS_MESSAGES_KEY_ACTION_ID = "action_id";
    private static final String ACTIONS_MESSAGES_KEY_MESSAGE = "message";


    private Context context;
    // Strings including all columns
    private String[] userProjection = new String[]{
            USERS_KEY_ID,
            USERS_KEY_API_KEY,
            USERS_KEY_NAME,
            USERS_KEY_EMAIL,
            USERS_KEY_REGISTERED_AT};

    private String[] friendProjection = new String[]{
            FRIENDS_KEY_ID,
            FRIENDS_KEY_NAME,
            FRIENDS_KEY_EMAIL};

    private String[] currenciesProjection = new String[]{
            CURRENCIES_KEY_ID,
            CURRENCIES_KEY_SYMBOL};

    private String[] debtProjection = new String[]{
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
            DEBTS_KEY_MANAGER_ID,
            DEBTS_KEY_VERSION
    };

    private String[] actionProjection = new String[]{
            ACTIONS_KEY_ID,
            ACTIONS_KEY_TYPE,
            ACTIONS_KEY_DEBT_ID,
            ACTIONS_KEY_USER1_ID,
            ACTIONS_KEY_USER1_NAME,
            ACTIONS_KEY_USER2_ID,
            ACTIONS_KEY_USER2_NAME,
            ACTIONS_KEY_NOTE,
            ACTIONS_KEY_DATE
    };

    private String[] actionsMessageProjection = new String[]{
            ACTIONS_MESSAGES_KEY_ID,
            ACTIONS_MESSAGES_KEY_ACTION_ID,
            ACTIONS_MESSAGES_KEY_MESSAGE
    };

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
                DEBTS_KEY_MANAGER_ID + " INTEGER, " +
                DEBTS_KEY_VERSION + " INTEGER);";

        String CREATE_ACTIONS_TABLE = "CREATE TABLE " + TABLE_ACTIONS + " (" +
                ACTIONS_KEY_ID + " INTEGER PRIMARY KEY, " +
                ACTIONS_KEY_TYPE + " TEXT, " +
                ACTIONS_KEY_DEBT_ID + " INTEGER, " +
                ACTIONS_KEY_USER1_ID + " INTEGER, " +
                ACTIONS_KEY_USER1_NAME + " TEXT, " +
                ACTIONS_KEY_USER2_ID + " INTEGER, " +
                ACTIONS_KEY_USER2_NAME + " TEXT, " +
                ACTIONS_KEY_NOTE + " TEXT, " +
                ACTIONS_KEY_DATE + " NUMERIC);";

        String CREATE_ACTIONS_MESSAGES_TABLE = "CREATE TABLE " + TABLE_ACTIONS_MESSAGES + " (" +
                ACTIONS_MESSAGES_KEY_ID + " INTEGER PRIMARY KEY, " +
                ACTIONS_MESSAGES_KEY_ACTION_ID + " INTEGER, " +
                ACTIONS_MESSAGES_KEY_MESSAGE + " TEXt);";


        db.execSQL(CREATE_DEBTS_TABLE);
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CURRENCIES_TABLE);
        db.execSQL(CREATE_ACTIONS_TABLE);
        db.execSQL(CREATE_ACTIONS_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIONS_MESSAGES);

        // Create tables again
        onCreate(db);
    }

    /**
     * Truncates everything
     */
    public void truncate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_FRIENDS);
        db.execSQL("DELETE FROM " + TABLE_USERS);
        db.execSQL("DELETE FROM " + TABLE_DEBTS);
        db.execSQL("DELETE FROM " + TABLE_CURRENCIES);
        db.execSQL("DELETE FROM " + TABLE_ACTIONS);
        db.execSQL("DELETE FROM " + TABLE_ACTIONS_MESSAGES);
        db.close();
    }

    /**
     * Adds a new user or overrides the previous entry
     * There is always only one row in the user table
     *
     * @param user User object
     */
    public void addOrUpdateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user does already exist
        Cursor cursor = db.query(TABLE_USERS, new String[]{USERS_KEY_ID}, USERS_KEY_ID + "=?",
                new String[]{String.valueOf(user.id)}, null, null, null);

        ContentValues values = new ContentValues();

        // Update only non-null values
        values.put(USERS_KEY_ID, user.id);
        if (user.email != null) {
            values.put(USERS_KEY_EMAIL, user.email);
        }
        if (user.name != null) {
            values.put(USERS_KEY_NAME, user.name);
        }
        if (user.apiKey != null) {
            values.put(USERS_KEY_API_KEY, user.apiKey);
        }
        if (user.registeredAt != null) {
            values.put(USERS_KEY_REGISTERED_AT, String.valueOf(user.registeredAt));
        }

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
     *
     * @return User currently logged in user
     */
    public User getUser() {
        User user = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, userProjection, null, null, null, null, null);

        // It is not necessary to loop through the result because there should always be at most 1 user row
        if (cursor.moveToFirst()) {
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
     * Saves friend to the database
     *
     * @param friend Friend object
     */
    public void addFriend(Friend friend) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_FRIENDS, new String[]{FRIENDS_KEY_ID}, FRIENDS_KEY_ID + "=?",
                new String[]{String.valueOf(friend.id)}, null, null, null);

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
     * Get a list of friends from the database
     *
     * @return ArrayList<Friend>
     */
    public ArrayList<Friend> getFriends() {
        ArrayList<Friend> list = new ArrayList<Friend>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FRIENDS, friendProjection, null, null, null, null, null);

        if (cursor.moveToFirst()) {
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
     * Returns a list of friends who have a common debt with the user
     * Returned friend objects contain additional values - ex. debtsString
     *
     * @param userId User's id
     * @return ArrayList<Friend>
     */
    public ArrayList<Friend> getExtendedFriendsWhoAreCreditorsOrDebtors(Integer userId) {

        ArrayList<Friend> list = new ArrayList<>();
        HashMap<String, Friend> hashMap = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Get all current debts
        Cursor cursor = db.query(TABLE_DEBTS, debtProjection, DEBTS_KEY_PAID_AT + " IS NULL AND " + DEBTS_KEY_DELETED_AT + " IS NULL", null, null, null, DEBTS_KEY_CREATED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {

                Debt debt = Debt.fromCursor(cursor);
                // Get additional variables, so that it can be displayed
                debt = getDebtWithAdditionalVariables(debt, userId);

                // Try to get a friend who is already added in the hashmap
                Friend friend = hashMap.get(getFriendHashFromDebt(debt, userId));

                if (friend == null) { // Friend doesn't exists in the current hash map yet
                    // Add friend
                    if (debt.customFriendName != null) {
                        friend = new Friend(null, debt.customFriendName, "");
                    } else if (userId.equals(debt.creditorId) && debt.debtorId > 0) {
                        friend = getFriend(debt.debtorId);
                    } else {
                        friend = getFriend(debt.creditorId);
                    }

                    friend.debtsString = "";
                    friend.debts = new ArrayList<>();
                    friend.totals = new HashMap<>();
                }

                friend.debts.add(debt);


                // Calculate the total debt
                boolean friendOwes = (debt.creditorId != null && debt.creditorId.equals(userId));

                if (debt.thingName != null) {

                    if (friendOwes) {
                        friend.totals.put("<font color='"+ ResourcesCompat.getColor(context.getResources(), R.color.green, null)+"'>" + debt.thingName + "</font>", 0.0);
                    } else {
                        friend.totals.put("<font color='"+ ResourcesCompat.getColor(context.getResources(), R.color.red, null)+"'>" + debt.thingName + "</font>", 0.0);
                    }

                } else {
                    Double totalAmount = friend.totals.get(debt.currencyString);

                    if (totalAmount == null) {
                        totalAmount = 0.0;
                    }

                    if (friendOwes) {
                        totalAmount -= debt.amount;
                    } else {
                        totalAmount += debt.amount;
                    }



                    friend.totals.put(debt.currencyString, totalAmount);

                    // remove even debts, sum 0 is reserved for things
                    if (totalAmount == 0.0) {
                        friend.totals.remove(debt.currencyString);
                    }
                }

                // Generate the string from the hashmap

                friend.debtsString = "";

                for(Map.Entry<String, Double> entry : friend.totals.entrySet()) {
                    String key = entry.getKey();
                    Double amount = entry.getValue();

                    // Add commas
                    if (friend.debtsString.length() > 0) {
                        friend.debtsString += ", ";
                    }

                    if (amount == 0) { // thing
                        friend.debtsString += key;
                    } else { // money

                        if (amount > 0) {
                            friend.debtsString += "<font color='#ff3737'>" + String.format("%.1f", amount) + " " + key + "</font>";
                        } else {
                            friend.debtsString += "<font color='#009747'>" + String.format("%.1f", -1 * amount) + " " + key + "</font>";
                        }

                    }
                }

                // Update hash map
                hashMap.put(getFriendHashFromDebt(debt, userId), friend);
            } while (cursor.moveToNext());
        }

        list.addAll(hashMap.values());

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Gets a friendId or the customFriend name
     *
     * @param debt   Debt
     * @param userId userID
     * @return String with the id or name
     */
    private String getFriendHashFromDebt(Debt debt, int userId) {

        if (debt == null) {
            return "error";
        }

        if (debt.customFriendName != null) {
            return String.valueOf(debt.customFriendName);
        } else if (userId == debt.creditorId && debt.debtorId != null && debt.debtorId > 0) {
            return String.valueOf(debt.debtorId);
        } else if (userId == debt.debtorId && debt.creditorId != null && debt.creditorId > 0) {
            return String.valueOf(debt.creditorId);
        } else {
            return "error";
        }
    }


    /**
     * Get friend by id
     *
     * @param id id of the friend
     * @return Friend
     */
    public Friend getFriend(Integer id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FRIENDS, friendProjection, FRIENDS_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Friend friend = null;

        if (cursor.moveToFirst()) {
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
     * Inserts currency into the local database
     *
     * @param currency currency received from the web
     */
    public void addCurrency(Currency currency) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_CURRENCIES, new String[]{CURRENCIES_KEY_ID}, CURRENCIES_KEY_ID + "=?",
                new String[]{String.valueOf(currency.id)}, null, null, null);

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
     * Returns list of currencies in the local database
     *
     * @return ArrayList<Currency>
     */
    public ArrayList<Currency> getCurrencies() {
        ArrayList<Currency> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENCIES, currenciesProjection, null, null, null, null, null);

        if (cursor.moveToFirst()) {
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
     *
     * @param id id of the friend
     * @return Currency
     */
    public Currency getCurrency(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENCIES, currenciesProjection, CURRENCIES_KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Currency currency = null;

        if (cursor.moveToFirst()) {
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
     *
     * @return ArrayList<Debt>
     */
    public ArrayList<Debt> getDebts() {
        ArrayList<Debt> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DEBTS, debtProjection, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(Debt.fromCursor(cursor));
            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();
        return list;
    }

    /**
     * Update debt.
     *
     * @param debt Current version of the debt
     */
    public void addOrUpdateDebt(Debt debt) {

        if (debt == null) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        if (debt.id == null) {
            // It's a new debt, we assign it a negative id, so there are not any conflicts with server database
            // Find the currently lowest id in the database
            int lowestId = 0;

            Cursor debtsCursor = db.query(TABLE_DEBTS, new String[]{DEBTS_KEY_ID}, null,
                    null, null, null, DEBTS_KEY_ID + " ASC");

            if (debtsCursor.moveToFirst()) {
                if (debtsCursor.getInt(0) < 0)
                    lowestId = debtsCursor.getInt(0);
            }

            debt.id = lowestId - 1;
            debtsCursor.close();
        }

        // Debt with this id can exist in the local database
        // This happens during debt editing and when the user edits the debt during server synchronization
        Cursor debtCursor = db.query(TABLE_DEBTS, new String[]{DEBTS_KEY_VERSION}, DEBTS_KEY_ID + "=?",
                new String[]{String.valueOf(debt.id)}, null, null, null);

        // Update only the most recent versions of the debt
        // blocks overriding of debts which were updated during server synchronization
        if (debtCursor.moveToFirst()) {
            if (debtCursor.getLong(0) > debt.version) {
                return;
            }
        }

        // Convert date to string
        String paidAt = null;
        String deletedAt = null;

        if (debt.paidAt != null) {
            paidAt = Tools.formatDate(debt.paidAt);
        }

        if (debt.deletedAt != null) {
            deletedAt = Tools.formatDate(debt.deletedAt);
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
        values.put(DEBTS_KEY_MODIFIED_AT, Tools.formatDate(debt.modifiedAt));
        values.put(DEBTS_KEY_CREATED_AT, Tools.formatDate(debt.createdAt));
        values.put(DEBTS_KEY_MANAGER_ID, debt.managerId);
        values.put(DEBTS_KEY_VERSION, debt.version);

        if (debtCursor.getCount() < 1) {
            // Debt doesn't exist
            db.insert(TABLE_DEBTS, null, values);
        } else {
            // Update debt id
            db.update(TABLE_DEBTS, values, DEBTS_KEY_ID + "=?", new String[]{String.valueOf(debt.id)});
        }

        db.close();
        debtCursor.close();

    }

    /**
     * Returns list of debts with added variables for displaying
     *
     * @param my     if true returns only debts owed by the user, otherwise returns debts owed to the user
     * @param userId userId
     * @return list of extended debts
     */
    public ArrayList<Debt> getExtendedDebts(boolean my, Integer userId) {
        ArrayList<Debt> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        if (my) {
            cursor = db.query(TABLE_DEBTS, debtProjection, DEBTS_KEY_DEBTOR_ID + "=? AND " + DEBTS_KEY_PAID_AT + " IS NULL AND " + DEBTS_KEY_DELETED_AT + " IS NULL", new String[]{String.valueOf(userId)}, null, null, DEBTS_KEY_CREATED_AT + " DESC");
        } else {
            cursor = db.query(TABLE_DEBTS, debtProjection, DEBTS_KEY_CREDITOR_ID + "=? AND " + DEBTS_KEY_PAID_AT + " IS NULL AND " + DEBTS_KEY_DELETED_AT + " IS NULL", new String[]{String.valueOf(userId)}, null, null, DEBTS_KEY_CREATED_AT + " DESC");
        }

        if (cursor.moveToFirst()) {
            do {
                Debt debt = Debt.fromCursor(cursor);

                // Set additional variables
                // 1. Name of person
                debt = getDebtWithAdditionalVariables(debt, userId);

                list.add(debt);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Get debt with additional variables for displaying
     *
     * @param debt   Original debt
     * @param userId userId
     * @return Debt with additional variable like debt.what, debt.who etc.
     */
    private Debt getDebtWithAdditionalVariables(Debt debt, Integer userId) {
        if (debt.creditorId != null && debt.creditorId.equals(userId)) {
            if (debt.debtorId == null) {
                debt.who = debt.customFriendName;
            } else {
                Friend friend = getFriend(debt.debtorId);
                if (friend != null) {
                    debt.who = friend.name;
                } else {
                    debt.who = "Error :-(";
                }
            }
        } else {
            if (debt.creditorId == null) {
                debt.who = debt.customFriendName;
            } else {
                Friend friend = getFriend(debt.creditorId);
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
            debt.status = context.getString(R.string.not_synced);;
        } else {
            debt.status = context.getString(R.string.synced);
        }

        return debt;
    }


    /**
     * Inserts an action into the local database
     *
     * @param action Action received from the web
     */
    public void addAction(Action action) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if action doesn't already exist
        Cursor cursor = db.query(TABLE_ACTIONS, new String[]{ACTIONS_KEY_ID}, ACTIONS_KEY_ID + "=?",
                new String[]{String.valueOf(action.id)}, null, null, null);

        if (cursor.getCount() > 0) {
            return;
        }

        // Add action into the database
        ContentValues actionValues = new ContentValues();
        actionValues.put(ACTIONS_KEY_ID, action.id);
        actionValues.put(ACTIONS_KEY_TYPE, action.type);
        actionValues.put(ACTIONS_KEY_DEBT_ID, action.debtId);
        actionValues.put(ACTIONS_KEY_USER1_ID, action.user1Id);
        actionValues.put(ACTIONS_KEY_USER1_NAME, action.user1Name);
        actionValues.put(ACTIONS_KEY_USER2_ID, action.user2Id);
        actionValues.put(ACTIONS_KEY_USER2_NAME, action.user2Name);
        actionValues.put(ACTIONS_KEY_NOTE, action.note);
        actionValues.put(ACTIONS_KEY_DATE, Tools.formatDate(action.date));

        db.insert(TABLE_ACTIONS, null, actionValues);

        // add action messages
        ContentValues messageValues = new ContentValues();
        for (String message : action.messages) {
            messageValues.put(ACTIONS_MESSAGES_KEY_ACTION_ID, action.id);
            messageValues.put(ACTIONS_MESSAGES_KEY_MESSAGE, message);
            db.insert(TABLE_ACTIONS_MESSAGES, null, messageValues);
        }

        db.close();
        cursor.close();
    }

    /**
     * Returns list of actions in the local database
     *
     * @return ArrayList<Action>
     */
    public ArrayList<Action> getActions() {
        ArrayList<Action> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ACTIONS, actionProjection, null, null, null, null, ACTIONS_KEY_DATE + " DESC", "25");

        if (cursor.moveToFirst()) {
            do {

                Cursor messagesCursor = db.query(TABLE_ACTIONS_MESSAGES, actionsMessageProjection,
                        ACTIONS_MESSAGES_KEY_ACTION_ID + "=?", new String[] {String.valueOf(cursor.getInt(0))}, null, null, null);

                ArrayList<String> messages = new ArrayList<>();

                if (messagesCursor.moveToFirst()) {
                    do {
                        messages.add(messagesCursor.getString(2));
                    } while (messagesCursor.moveToNext());
                }

                Action action = new Action(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        Tools.parseDate(cursor.getString(8)),
                        messages
                );

                messagesCursor.close();

                list.add(action);

            } while (cursor.moveToNext());

        }

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Removes all debts, which were updated BEFORE the time of server request
     */
    public void removeOfflineDebts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_DEBTS + " WHERE " + DEBTS_KEY_VERSION + "<" + String.valueOf(System.currentTimeMillis()));
        db.close();
    }
}
