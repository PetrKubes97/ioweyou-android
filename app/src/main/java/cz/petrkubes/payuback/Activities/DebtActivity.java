package cz.petrkubes.payuback.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import cz.petrkubes.payuback.Adapters.FriendsSuggestionAdapter;
import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Currency;
import cz.petrkubes.payuback.Structs.Debt;
import cz.petrkubes.payuback.Structs.Friend;

/**
 * Created by petr on 24.10.16.
 */

public class DebtActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private AutoCompleteTextView txtName;
    private EditText txtWhat;
    private Spinner spnCurrency;
    private RadioButton rdioMyDebt;
    private RadioButton rdioTheirDebt;
    private RadioButton rdioThing;
    private RadioButton rdioMoney;
    private EditText txtNote;
    private FloatingActionButton btnAddDebt;
    private DatabaseHandler db;
    private TextInputLayout txtIL;

    private Integer tempFacebookFriendId = null;
    private ArrayList<Currency> currencies = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt);
        // Remove actionbar shadow
        getSupportActionBar().setElevation(0);

        txtName = (AutoCompleteTextView) findViewById(R.id.txt_name);
        txtWhat = (EditText) findViewById(R.id.txt_who);
        spnCurrency = (Spinner) findViewById(R.id.spn_currency);
        rdioMyDebt = (RadioButton) findViewById(R.id.rdio_my_debt);
        rdioTheirDebt = (RadioButton) findViewById(R.id.rdio_their_debt);
        rdioThing = (RadioButton) findViewById(R.id.rdio_thing);
        rdioMoney = (RadioButton) findViewById(R.id.rdio_money);
        txtNote = (EditText) findViewById(R.id.txt_note);
        btnAddDebt = (FloatingActionButton) findViewById(R.id.btn_add_debt);
        txtIL = (TextInputLayout) findViewById(R.id.txtIL_what);

        // Set the hint after the animation completes, workaround for Android bug
        txtNote.setHint(getResources().getString(R.string.note));

        db = new DatabaseHandler(getApplicationContext());

        // Array of all friends who will be suggested
        // 1) facebook friends
        ArrayList<Friend> friends = db.getFriends();
        // 2) contacts
        for (String contact : getContacts()) {
            friends.add(new Friend(null, contact, ""));
        }

        // Create the adapter to convert the array to views
        FriendsSuggestionAdapter friendsAdapter = new FriendsSuggestionAdapter(this, friends);

        // Setup autocomplete
        txtName.setAdapter(friendsAdapter);
        txtName.setThreshold(1);
        // Save the information, that selected user is facebook friend (his id) or a contact
        txtName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Friend selectedFriend = (Friend) adapterView.getItemAtPosition(i);
                tempFacebookFriendId = selectedFriend.id;

                // Set blue text, so that user knows that facebook friend was selected
                if (selectedFriend.id != null) {
                    txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.facebook_lighter));
                } else {
                    txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                }
            }
        });

        // Set up currencies spinner
        currencies = db.getCurrencies();
        ArrayAdapter<Currency> currenciesAdapter = new ArrayAdapter<Currency>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, currencies);
        spnCurrency.setPrompt("Currency:");
        spnCurrency.setAdapter(currenciesAdapter);

        // Delete facebook friend info, when user types anything into the box
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tempFacebookFriendId = null;
                txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Add debt button
        btnAddDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), String.valueOf(tempFacebookFriendId), Toast.LENGTH_SHORT).show();
                addDebt();
            }
        });

        // Change input fields if user chooses a thing or money
        rdioThing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // if button is checked
                if (b) {
                    spnCurrency.setVisibility(View.GONE);
                    txtWhat.setInputType(InputType.TYPE_CLASS_TEXT);
                    txtIL.setHint(getResources().getString(R.string.thing));
                } else {
                    spnCurrency.setVisibility(View.VISIBLE);
                    txtWhat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    txtIL.setHint(getResources().getString(R.string.amount));
                }
            }
        });


        // Show keyboard - it is necessary to wait for the animation to finish
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(txtName, InputMethodManager.SHOW_IMPLICIT);
            }

        }, 400);

    }

    /**
     * Returns list of users contacts
     *
     * @return List<String> contacts
     */
    // TODO Refresh list after user accepts contacts permission
    private List<String> getContacts() {

        List<String> contacts = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {

            // Permission is granted
            ContentResolver cr = getContentResolver();
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Data.DISPLAY_NAME_PRIMARY}, null, null, null);

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

    /**
     * Handle user's answer to 'request permission' dialog
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * Adds new debt to local database
     */
    public void addDebt() {

        Integer creditorId = null;
        Integer debtorId = null;
        String customFriendName = null;
        String thingName = null;
        Integer amount = null;
        Integer currencyId = null;

        // Interpret add debt form
        if (rdioMyDebt.isChecked()) {
            debtorId = db.getUser().id;
        } else {
            creditorId = db.getUser().id;
        }

        if (tempFacebookFriendId != null) {
            if (rdioMyDebt.isChecked()) {
                creditorId = tempFacebookFriendId;
            } else {
                debtorId = tempFacebookFriendId;
            }
        } else {
            customFriendName = txtName.getText().toString();
        }

        if (rdioThing.isChecked()) {
            thingName = txtWhat.getText().toString();
        } else {
            amount = Integer.parseInt(txtWhat.getText().toString());
            currencyId = currencies.get(spnCurrency.getSelectedItemPosition()).id;
        }

        Log.d(Const.TAG, "Adding debt to database: " + String.valueOf(tempFacebookFriendId));

        Debt debt = new Debt(
                null,
                creditorId,
                debtorId,
                customFriendName,
                amount,
                currencyId,
                thingName,
                txtNote.getText().toString(),
                null,
                null,
                new Date(),
                new Date(),
                0
        );

        // Add debt into the local database
        db.addOrUpdateDebt(null, debt);

        setResult(RESULT_OK);
        finish();
    }
}
