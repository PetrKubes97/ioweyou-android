package cz.petrkubes.payuback.Activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cz.petrkubes.payuback.Adapters.FriendsSuggestionAdapter;
import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.Fragments.DebtsFragment;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Currency;
import cz.petrkubes.payuback.Structs.Debt;
import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;
import cz.petrkubes.payuback.Tools.Tools;

/**
 * Created by petr on 24.10.16.
 */

public class DebtActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_created_at";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_created_at";

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
    private TextInputLayout txtILWhat;
    private TextInputLayout txtILWho;
    private Button btnCreatedAt;
    private Button btnPaidUnpaid;
    private Button btnDeleteRestore;
    private TextView txtvPaid;
    private TextView txtvDeleted;
    private TextView txtvPaidLabel;
    private TextView txtvDeletedLabel;

    private Integer tempFacebookFriendId = null;
    private ArrayList<Currency> currencies = null;
    private ArrayList<Friend> friends = null;

    private ArrayAdapter<Friend> friendsAdapter = null;

    private User user;
    private Date createdAt;
    private Debt debtToEdit;
    private Calendar createdAtCal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt);

        // Setup actionbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.debt_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setup all widgets
        txtName = (AutoCompleteTextView) findViewById(R.id.txt_name);
        txtWhat = (EditText) findViewById(R.id.txt_who);
        spnCurrency = (Spinner) findViewById(R.id.spn_currency);
        rdioMyDebt = (RadioButton) findViewById(R.id.rdio_my_debt);
        rdioTheirDebt = (RadioButton) findViewById(R.id.rdio_their_debt);
        rdioThing = (RadioButton) findViewById(R.id.rdio_thing);
        rdioMoney = (RadioButton) findViewById(R.id.rdio_money);
        txtNote = (EditText) findViewById(R.id.txt_note);
        btnAddDebt = (FloatingActionButton) findViewById(R.id.btn_add_debt);
        txtILWhat = (TextInputLayout) findViewById(R.id.txtIL_what);
        txtILWho = (TextInputLayout) findViewById(R.id.txtIL_who);
        btnCreatedAt = (Button) findViewById(R.id.btn_created_at);
        btnPaidUnpaid = (Button) findViewById(R.id.btn_paid_unpaid);
        btnDeleteRestore = (Button) findViewById(R.id.btn_delete_restore);
        txtvPaid = (TextView) findViewById(R.id.txtv_paid);
        txtvDeleted = (TextView) findViewById(R.id.txtv_deleted);
        txtvPaidLabel = (TextView) findViewById(R.id.txtv_paid_label);
        txtvDeletedLabel = (TextView) findViewById(R.id.txtv_deleted_label);

        // Set the hint after the animation completes, workaround for Android bug
        txtNote.setHint(getResources().getString(R.string.note));

        db = new DatabaseHandler(getApplicationContext());

        // Get current user
        user = db.getUser();

        // Array of all friends who will be suggested
        // 1) facebook friends
        friends = db.getFriends();
        // 2) contacts
        for (String contact : getContacts()) {
            friends.add(new Friend(null, contact, ""));
        }

        // Create the adapter to convert the array to views
        friendsAdapter = new FriendsSuggestionAdapter(this, friends);

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
        // Also hide error messages
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tempFacebookFriendId = null;
                txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                txtILWho.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Remove error messages
        txtWhat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txtILWhat.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                    txtILWhat.setHint(getResources().getString(R.string.thing));
                } else {
                    spnCurrency.setVisibility(View.VISIBLE);
                    txtWhat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    txtILWhat.setHint(getResources().getString(R.string.amount));
                }
            }
        });

        // Created at button
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        btnCreatedAt.setText(df.format(new Date()));
        btnCreatedAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        createdAtCal = Calendar.getInstance();

        // Set paid/unpaid button
        btnPaidUnpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debtToEdit.paidAt == null) {
                    debtToEdit.paidAt = new Date();
                } else {
                    debtToEdit.paidAt = null;
                }
                stylePaidUnpaid();
            }
        });

        // Set delete button
        btnDeleteRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debtToEdit.deletedAt == null) {
                    debtToEdit.deletedAt = new Date();
                } else {
                    debtToEdit.deletedAt = null;
                }
                styleDeleteRestore();
            }
        });

        // If there is a debt to be edited, pre-fill text edits
        debtToEdit = Parcels.unwrap(getIntent().getParcelableExtra(DebtsFragment.DEBT_TO_EDIT));
        if (debtToEdit != null) {

            txtName.setText(debtToEdit.who);
            txtNote.setText(debtToEdit.note);
            btnCreatedAt.setText(debtToEdit.createdAtString());

            if (debtToEdit.paidAt != null) {
                txtvPaid.setText(debtToEdit.paidAtString());
            } else {
                txtvPaid.setText(getResources().getString(R.string.no));
            }

            if (debtToEdit.deletedAt != null) {
                txtvDeleted.setText(debtToEdit.deletedAtString());
            } else {
                txtvDeleted.setText(getResources().getString(R.string.no));
            }

            // Mark facebook friend
            if ((debtToEdit.creditorId != null && debtToEdit.debtorId != null)) {
                txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.facebook_lighter));

                if (debtToEdit.creditorId.equals(user.id)) {
                    tempFacebookFriendId = debtToEdit.debtorId;
                } else {
                    tempFacebookFriendId = debtToEdit.creditorId;
                }
            } else {
                tempFacebookFriendId = null;
                txtName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }


            // Check radio buttons
            if (debtToEdit.thingName != null && !debtToEdit.thingName.isEmpty()) {
                rdioThing.setChecked(true);
                txtWhat.setText(debtToEdit.what);
            } else {
                rdioMoney.setChecked(true);
                txtWhat.setText(String.valueOf(debtToEdit.amount));

                // For some reason I can't use functions like indexOf or getPosition ¯\_(ツ)_/¯
                for (Currency currency : currencies) {
                    if (db.getCurrency(debtToEdit.currencyId).id == (currency.id)) {
                        spnCurrency.setSelection(currenciesAdapter.getPosition(currency));
                    }
                }
            }

            if (debtToEdit.creditorId != null && debtToEdit.creditorId.equals(user.id)) {
                rdioMyDebt.setChecked(false);
            } else {
                rdioMyDebt.setChecked(true);
            }

            // Style buttons for deleting and marking debt as paid
            stylePaidUnpaid();
            styleDeleteRestore();

        } else {
            // hide buttons which have no use for a new debt
            btnPaidUnpaid.setVisibility(View.GONE);
            btnDeleteRestore.setVisibility(View.GONE);
            txtvPaid.setVisibility(View.GONE);
            txtvPaidLabel.setVisibility(View.GONE);
            txtvDeleted.setVisibility(View.GONE);
            txtvDeletedLabel.setVisibility(View.GONE);

            // Show keyboard - it is necessary to wait for the animation to finish
            txtName.requestFocus();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtName, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 400);
        }

        // Add debt button
        btnAddDebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), String.valueOf(tempFacebookFriendId), Toast.LENGTH_SHORT).show();
                addDebt();
            }
        });
    }

    /**
     * Returns list of users contacts
     *
     * @return List<String> contacts
     */
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

                // Reset friends array and create it again
                friends.clear();
                // 1) facebook friends
                friends.addAll(db.getFriends());
                // 2) contacts
                for (String contact : getContacts()) {
                    friends.add(new Friend(null, contact, ""));
                }

                friendsAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "We won't be able to suggest you people from your contacts. :(", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Adds new debt to local database
     */
    public void addDebt() {

        Integer id = null;
        Integer creditorId = null;
        Integer debtorId = null;
        String customFriendName = null;
        String thingName = null;
        Integer amount = null;
        Integer currencyId = null;
        Date paidAt = null;
        Date deletedAt = null;
        Integer version = 0;

        // set 'invisible' variables if editing a debt
        if (debtToEdit != null) {
            id = debtToEdit.id;
            paidAt = debtToEdit.paidAt;
            deletedAt = debtToEdit.deletedAt;
            version = debtToEdit.version +1;
        }

        // Set user
        if (rdioMyDebt.isChecked()) {
            debtorId = user.id;
        } else {
            creditorId = user.id;
        }

        // Set the other person taking part in this debt
        if (tempFacebookFriendId != null) {
            if (rdioMyDebt.isChecked()) {
                creditorId = tempFacebookFriendId;
            } else {
                debtorId = tempFacebookFriendId;
            }
        } else {
            customFriendName = txtName.getText().toString();
        }

        // Check if both sides of debt are set
        if ((creditorId == null || debtorId == null) && (customFriendName == null || customFriendName.isEmpty())) {
            txtILWho.setError("This field can't be empty.");
            return;
        }

        if (rdioThing.isChecked()) {
            thingName = txtWhat.getText().toString();

            if (thingName.isEmpty()) {
                txtILWhat.setError("This field can't be empty.");
                return;
            }

        } else {

            try {
                amount = Integer.parseInt(txtWhat.getText().toString());
            } catch (NumberFormatException e) {
                txtILWhat.setError("This field has to be a number.");
                return;
            }

            currencyId = currencies.get(spnCurrency.getSelectedItemPosition()).id;

            if (amount < 1) {
                txtILWhat.setError("Sorry, you can't owe someone 0 money. :-)");
                return;
            }
        }

        // Set createdAt
        if (createdAt == null) {
            createdAt = new Date();
        }

        Log.d(Const.TAG, "Adding debt to database: " + String.valueOf(tempFacebookFriendId));

        Debt debt = new Debt(
                id,
                creditorId,
                debtorId,
                customFriendName,
                amount,
                currencyId,
                thingName,
                txtNote.getText().toString(),
                paidAt,
                deletedAt,
                new Date(),
                createdAt,
                version
        );

        // Add debt into the local database
        db.addOrUpdateDebt(id, debt);

        setResult(RESULT_OK);
        finish();
    }

    private void stylePaidUnpaid() {
        if (debtToEdit != null && debtToEdit.paidAt == null) {
            btnPaidUnpaid.setText(getResources().getString(R.string.mark_as_paid));
            btnPaidUnpaid.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
            txtvPaid.setText(getResources().getString(R.string.no));
        } else {
            btnPaidUnpaid.setText(getResources().getString(R.string.mark_as_unpaid));
            btnPaidUnpaid.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
            txtvPaid.setText(debtToEdit.paidAtString());
        }
    }

    private void styleDeleteRestore() {
        if (debtToEdit != null && debtToEdit.deletedAt == null) {
            btnDeleteRestore.setText(getResources().getString(R.string.delete));
            btnDeleteRestore.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
            txtvDeleted.setText(getResources().getString(R.string.no));
        } else {
            btnDeleteRestore.setText(getResources().getString(R.string.restore));
            btnDeleteRestore.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.green));
            txtvDeleted.setText(debtToEdit.deletedAtString());
        }
    }

    private void showDatePickerDialog() {
        createdAtCal.clear();

        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setOnDateSetListener(DebtActivity.this);
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    private void showTimePickerDialog() {
        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setOnTimeSetListener(DebtActivity.this);
        rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }


    // Dialogs for choosing the date of a debt
    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        createdAtCal.set(year, monthOfYear, dayOfMonth);
        showTimePickerDialog();
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        createdAtCal.set(Calendar.HOUR, hourOfDay);
        createdAtCal.set(Calendar.MINUTE, minute);
        createdAt = createdAtCal.getTime();
        btnCreatedAt.setText(Tools.formatDateTime(createdAt));
    }
}
