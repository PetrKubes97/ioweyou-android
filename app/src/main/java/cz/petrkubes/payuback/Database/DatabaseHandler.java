package cz.petrkubes.payuback.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cz.petrkubes.payuback.Structs.User;

/**
 * Created by petr on 20.10.16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

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
    private static final String FRIENDS_FACEBOOK_ID = "facebook_id";

    // Strings including all columns
    private String[] userProjection = new String[]{
            USERS_KEY_ID,
            USERS_KEY_NAME,
            USERS_KEY_EMAIL,
            USERS_KEY_FACEBOOK_ID,
            USERS_KEY_FACEBOOK_TOKEN,
            USERS_KEY_REGISTERED_AT};


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
                FRIENDS_FACEBOOK_ID + " TEXT);";

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
        Cursor cursor = db.query(TABLE_USERS, userProjection, USERS_KEY_ID + "=?",
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
}
