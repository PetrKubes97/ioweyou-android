package cz.petrkubes.payuback.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;

/**
 * Created by petr on 20.10.16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 8;

    // Database Name
    private static final String DATABASE_NAME = "payUBack.db";

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DEBTS = "debts";
    private static final String TABLE_FRIENDS = "friends";

    // Table column names
    private static final String USERS_KEY_ID = "id";
    private static final String USERS_KEY_NAME = "name";
    private static final String USERS_KEY_EMAIL = "email";
    private static final String USERS_KEY_FACEBOOK_ID = "facebook_id";
    private static final String USERS_KEY_FACEBOOK_TOKEN = "facebook_token";
    private static final String USERS_KEY_REGISTERED_AT = "registered_at";

    private static final String FRIENDS_KEY_ID = "id";
    private static final String FRIENDS_KEY_NAME = "name";
    private static final String FRIENDS_KEY_EMAIL = "email";
    private static final String FRIENDS_KEY_FACEBOOK_ID = "facebook_id";

    // Strings including all columns
    private String[] userProjection = new String[]{
            USERS_KEY_ID,
            USERS_KEY_NAME,
            USERS_KEY_EMAIL,
            USERS_KEY_FACEBOOK_ID,
            USERS_KEY_FACEBOOK_TOKEN,
            USERS_KEY_REGISTERED_AT};

    private String[] friendProjection = new String[]{
            FRIENDS_KEY_ID,
            FRIENDS_KEY_NAME,
            FRIENDS_KEY_EMAIL,
            FRIENDS_KEY_FACEBOOK_ID};


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                USERS_KEY_ID + " INTEGER PRIMARY KEY, " +
                USERS_KEY_NAME + " TEXT, " +
                USERS_KEY_EMAIL + " TEXT, " +
                USERS_KEY_FACEBOOK_ID + " TEXT, " +
                USERS_KEY_FACEBOOK_TOKEN + " TEXT, " +
                USERS_KEY_REGISTERED_AT + " NUMERIC);";

        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + " (" +
                FRIENDS_KEY_ID + " INTEGER PRIMARY KEY, " +
                FRIENDS_KEY_NAME + " TEXT, " +
                FRIENDS_KEY_EMAIL + " TEXT, " +
                FRIENDS_KEY_FACEBOOK_ID + " TEXT);";

        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEBTS);

        // Create tables again
        onCreate(db);
    }

    public void addUser(User user) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();

        // Checks if user doesn't already exist
        Cursor cursor = db.query(TABLE_USERS, new String[] {USERS_KEY_ID}, USERS_KEY_ID + "=?",
                new String[] {String.valueOf(user.id)}, null, null, null);

        if (cursor.getCount() > 0) {
            throw new Exception("User already exists.");
        }

        // Add user to the database
        ContentValues values = new ContentValues();
        values.put(USERS_KEY_ID, user.id);
        values.put(USERS_KEY_EMAIL, user.email);
        values.put(USERS_KEY_NAME, user.name);
        values.put(USERS_KEY_FACEBOOK_ID, user.facebookId);
        values.put(USERS_KEY_FACEBOOK_TOKEN, user.facebookToken);
        values.put(USERS_KEY_REGISTERED_AT, String.valueOf(user.registredAt));

        db.insert(TABLE_USERS, null, values);

        db.close();
        cursor.close();
    }

    /**
     * Save friend object to database
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

        // Add user to the database
        ContentValues values = new ContentValues();
        values.put(FRIENDS_KEY_ID, friend.id);
        values.put(FRIENDS_KEY_EMAIL, friend.email);
        values.put(FRIENDS_KEY_NAME, friend.name);
        values.put(FRIENDS_KEY_FACEBOOK_ID, friend.facebookId);

        db.insert(TABLE_FRIENDS, null, values);

        db.close();
        cursor.close();
    }

    /**
     * Get list of friends from the database
     * @return
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
                    cursor.getString(2),
                    cursor.getString(3)));
            } while (cursor.moveToNext());

        }

        cursor.close();

        return list;
    }
}
