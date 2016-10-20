package cz.petrkubes.payuback.Datatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by petr on 20.10.16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "payuback.db";

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

    private static  final String FRIENDS_KEY_ID = "id";
    private static  final String FRIENDS_KEY_NAME = "name";
    private static  final String FRIENDS_KEY_EMAIL = "email";
    private static  final String FRIENDS_FACEBOOK_ID = "facebook_id";


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
}
