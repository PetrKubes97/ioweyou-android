package cz.petrkubes.payuback.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cz.petrkubes.payuback.Adapters.FriendsSuggestionAdapter;
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

        // Construct the data source
        ArrayList<Friend> friends = new ArrayList<Friend>();

        friends.add(new Friend(1000, "Petr Kubeš", "petr1@kubes.cz", "64161616"));
        friends.add(new Friend(2, "asdf", "petr2@kubes.cz", "64161616"));
        friends.add(new Friend(3, "ASD", "petr3@kubes.cz", "64161616"));

        int i = 10;
        for (String contact:getContacts()) {
            friends.add(new Friend(i, contact, "", ""));
            i++;
        }

        // Create the adapter to convert the array to views
        FriendsSuggestionAdapter adapter = new FriendsSuggestionAdapter(this, friends);

        // Setup autocomplete
        txtName.setAdapter(adapter);
        txtName.setThreshold(1);
        txtName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // Show keyboard
        txtName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private List<String> getContacts() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            // Permission is already granted
            List<String> contacts = new ArrayList<>();

            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] {ContactsContract.Data.DISPLAY_NAME_PRIMARY}, null, null, null);

            while (cursor.moveToNext()) {

                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));

                if (name != null && !name.contains("@")) {
                    contacts.add(name);
                }
            }

            cursor.close();

            return contacts;
        }
        return null;
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
