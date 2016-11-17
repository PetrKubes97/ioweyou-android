package cz.petrkubes.payuback.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cz.petrkubes.payuback.Activities.MainActivity;
import cz.petrkubes.payuback.Adapters.DebtsAdapter;
import cz.petrkubes.payuback.Api.ApiRestClient;
import cz.petrkubes.payuback.Api.SimpleCallback;
import cz.petrkubes.payuback.Const;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Debt;
import cz.petrkubes.payuback.Structs.User;


public class DebtsFragment extends Fragment implements UpdateableFragment {

    public static final String ARG_MY = "argMy";
    private DatabaseHandler db;
    private ListView lstDebts;
    private User user;
    private DebtsAdapter adapter;
    private ArrayList<Debt> debts;
    private boolean myDebts = true;
    private View rootView;

    // Dialog elements
    private Button btnDialogPay;
    private Button btnDialogEdit;
    private Button btnDialogCancel;
    private Button btnDialogPlus;
    private Button btnDialogMinus;
    private TextView txtDialogWho;
    private TextView txtDialogWhat;
    private TextView txtDialogNote;
    private TextView txtDialogDate;
    private SeekBar skbrDialogPayment;

    // Dialog variables
    private Integer amount;
    private Handler dialogHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create new database instance
        db = new DatabaseHandler(getContext());
        // Get user
        user = db.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_debts, container, false);
        lstDebts = (ListView) rootView.findViewById(R.id.lst_debts);

        Bundle args = getArguments();

        // Get the correct list of debts - my debts or their debts
        debts = new ArrayList<>();
        myDebts = args.getBoolean(ARG_MY);

        if (user != null) {
             debts = db.getExtendedDebts(myDebts, user.id);
        }

        // Populate the listview
        adapter = new DebtsAdapter(getContext(), debts, myDebts);
        lstDebts.setAdapter(adapter);

        // Set up a click listener for actions with debts
        lstDebts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Debt debt = (Debt) adapterView.getItemAtPosition(i);
                showDialog(debt);

            }
        });

        // This handler is used for updating amount when user holds plus or minus button
        dialogHandler = new Handler();

        return rootView;
    }

    @Override
    public void update() {
        Log.d(Const.TAG, "UPDATING FRAgMENT");

        if (user != null) {
            debts = db.getExtendedDebts(myDebts, user.id);
            Log.d(Const.TAG, debts.toString());
        }
        adapter.clear();
        adapter.addAll(debts);
        adapter.notifyDataSetChanged();
    }

    private void showDialog(final Debt debt) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate layout
        View debtDialogView = inflater.inflate(R.layout.dialog_debt, null);
        builder.setView(debtDialogView);
        final AlertDialog dialog = builder.create();

        // Set up buttons, text views and seekbar
        btnDialogPay = (Button) debtDialogView.findViewById(R.id.btn_pay);
        btnDialogCancel = (Button) debtDialogView.findViewById(R.id.btn_cancel);
        btnDialogEdit = (Button) debtDialogView.findViewById(R.id.btn_edit);
        btnDialogPlus = (Button) debtDialogView.findViewById(R.id.btn_plus);
        btnDialogMinus = (Button) debtDialogView.findViewById(R.id.btn_minus);

        txtDialogWhat = (TextView)  debtDialogView.findViewById(R.id.txt_what);
        txtDialogWho = (TextView)  debtDialogView.findViewById(R.id.txt_who);
        txtDialogNote = (TextView)  debtDialogView.findViewById(R.id.txt_note);
        txtDialogDate = (TextView) debtDialogView.findViewById(R.id.txt_date);

        skbrDialogPayment = (SeekBar) debtDialogView.findViewById(R.id.skbr_payment);

        txtDialogWhat.setText(debt.what);
        txtDialogWho.setText(debt.who);
        txtDialogNote.setText(debt.note);
        txtDialogDate.setText(debt.createdAtString());

        Log.d(Const.TAG, "AMounte " + String.valueOf(debt.amount));

        if (debt.amount == null) {
            skbrDialogPayment.setVisibility(View.GONE);
            btnDialogPlus.setVisibility(View.GONE);
            btnDialogMinus.setVisibility(View.GONE);
        } else {
            skbrDialogPayment.setVisibility(View.VISIBLE);
            btnDialogPlus.setVisibility(View.VISIBLE);
            btnDialogMinus.setVisibility(View.VISIBLE);

            skbrDialogPayment.setMax(debt.amount/5);
            skbrDialogPayment.setProgress(debt.amount);

        }

        if (debt.note.isEmpty()) {
            txtDialogNote.setVisibility(View.GONE);
        }

        amount = debt.amount;

        // Update payment while user hold plus or minus button
        final Runnable runnablePlus = new Runnable() {
            public void run() {
                amount += 1;

                btnDialogPay.setText("PAY " + String.valueOf(amount) + " " + debt.currencyString);
                dialogHandler.postDelayed(this, 150);

                if (amount % 5 == 0) {
                    skbrDialogPayment.setProgress(amount/5);
                }
            }
        };

        final Runnable runnableMinus = new Runnable() {
            public void run() {
                amount -= 1;

                btnDialogPay.setText("PAY " + String.valueOf(amount) + " " + debt.currencyString);
                dialogHandler.postDelayed(this, 150);

                if (amount % 5 == 0) {
                    skbrDialogPayment.setProgress(amount/5);
                }
            }
        };

        // Setup seekbar
        skbrDialogPayment.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (i == seekBar.getMax()) {
                    btnDialogPay.setText("PAY ALL");
                } else {
                    amount = i * (debt.amount/seekBar.getMax());

                    btnDialogPay.setText("PAY " + String.valueOf(amount) + " " + debt.currencyString);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Set up plus and minus buttons
        btnDialogPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == motionEvent.ACTION_UP) {
                    dialogHandler.removeCallbacks(runnablePlus);
                } else if (motionEvent.getAction() == motionEvent.ACTION_DOWN) {
                    runnablePlus.run();
                }

                return false;
            }
        });

        btnDialogMinus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == motionEvent.ACTION_UP) {
                    dialogHandler.removeCallbacks(runnableMinus);
                } else if (motionEvent.getAction() == motionEvent.ACTION_DOWN) {
                    runnableMinus.run();
                }

                return false;
            }
        });

        // Cancel button
        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        // Pay button
        btnDialogPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amount.equals(debt.amount)) { // a) Pay back the whole debt
                    debt.paidAt = new Date();
                } else { // b) Decrease payment amount TODO: separate table for payments
                    debt.amount = debt.amount - amount;
                }

                debt.version += 1;

                db.addOrUpdateDebt(debt.id, debt);

                ((MainActivity) getActivity()).updateDebts();
                dialog.cancel();
            }
        });

        // display dialog
        dialog.show();
    }
}