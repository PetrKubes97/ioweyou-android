package cz.petrkubes.payuback.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.petrkubes.payuback.Adapters.FriendsSuggestionAdapter;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Friend;

/**
 * Created by petr on 24.10.16.
 */

public class DebtActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private AutoCompleteTextView txtName;
    private EditText txtAmount;
    private RadioButton rdioMyDebt;
    private RadioButton rdioTheirDebt;
    private RadioButton rdioThing;
    private RadioButton rdioMoney;
    private EditText txtNote;
    private FloatingActionButton btnAddDebt;
    private DatabaseHandler db;

    private String tempFacebookFriendId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt);
        // Remove actionbar shadow
        getSupportActionBar().setElevation(0);

        txtName = (AutoCompleteTextView) findViewById(R.id.txt_name);
        txtAmount = (EditText) findViewById(R.id.txt_amount);
        rdioMyDebt = (RadioButton) findViewById(R.id.rdio_my_debt);
        rdioTheirDebt = (RadioButton) findViewById(R.id.rdio_their_debt);
        rdioThing = (RadioButton) findViewById(R.id.rdio_thing);
        rdioMoney = (RadioButton) findViewById(R.id.rdio_money);
        txtNote = (EditText) findViewById(R.id.txt_note);
        btnAddDebt = (FloatingActionButton) findViewById(R.id.btn_add_debt);

        // Set the hint after the animation completes, workaround for Android bug
        txtNote.setHint(getResources().getString(R.string.note));

        db = new DatabaseHandler(getApplicationContext());

        // Array of all friends, who will be suggested
        // 1) facebook friends
        ArrayList<Friend> friends = db.getFriends();
        // 2) contacts
        for (String contact:getContacts()) {
            friends.add(new Friend(-1, contact, "", ""));
        }

        // Create the adapter to convert the array to views
        FriendsSuggestionAdapter adapter = new FriendsSuggestionAdapter(this, friends);

        // Setup autocomplete
        txtName.setAdapter(adapter);
        txtName.setThreshold(1);
        // Save the information, that selected user is facebook friend (his id) or a contact
        txtName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Friend selectedFriend = (Friend) adapterView.getItemAtPosition(i);
                tempFacebookFriendId = selectedFriend.facebookId;

                // Set blue background to the box, so that user knows that facebook friend was selected
                if (selectedFriend.facebookId.length()>1) {
                    txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.facebook_lighter));
                } else {
                    txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            }
        });

        // Delete facebook friend info, when user types anything into the box
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tempFacebookFriendId = "";
                txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAddDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), tempFacebookFriendId,Toast.LENGTH_SHORT).show();
            }
        });


        // Show keyboard - it is necessary to wait for the animation to finish
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtName, InputMethodManager.SHOW_IMPLICIT);
            }

        }, 400);
    }

    private List<String> getContacts() {

        List<String> contacts = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // Permission is granted

            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] {ContactsContract.Data.DISPLAY_NAME_PRIMARY}, null, null, null);

            while (cursor.moveToNext()) {

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));

                if (name != null && !name.contains("@")) {
                    contacts.add(name);
                }
            }

            cursor.close();
        }
        // empty list is returned, when permission isn't granted
        return contacts;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContacts();
            } else {
                Toast.makeText(this, "We won't be able to suggest you people from your contacts. :(", Toast.LENGTH_LONG).show();
            }
        }
    }

}
