package cz.petrkubes.payuback.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;

import cz.petrkubes.payuback.Structs.Currency;
import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;

/**
 * Created by petr on 20.10.16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 14;

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

        return user;
    }

    /**
     * Save friend object to the database
     * @param friend
     * @throws Exception
     */
    public void addFriend(Friend friend) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_FRIENDS, new String[] {FRIENDS_KEY_ID}, FRIENDS_KEY_ID + "=?",
                new String[] {String.valueOf(friend.id)}, null, null, null);

        if (cursor.getCount() > 0) {
            throw new Exception("Friend already exists.");
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

        return list;
    }

    public void addCurrency(Currency currency) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_CURRENCIES, new String[] {CURRENCIES_KEY_ID}, CURRENCIES_KEY_ID + "=?",
                new String[] {String.valueOf(currency.id)}, null, null, null);

        if (cursor.getCount() > 0) {
            throw new Exception("Currency does already exist.");
        }

        // Add currency into the database
        ContentValues values = new ContentValues();
        values.put(CURRENCIES_KEY_ID, currency.id);
        values.put(CURRENCIES_KEY_SYMBOL, currency.symbol);

        db.insert(TABLE_CURRENCIES, null, values);

        db.close();
        cursor.close();
    }

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

        return list;
    }
}
