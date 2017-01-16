package cz.petrkubes.ioweyou.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;

import cz.petrkubes.ioweyou.Activities.DebtActivity;
import cz.petrkubes.ioweyou.Activities.MainActivity;
import cz.petrkubes.ioweyou.Adapters.FriendsAdapter;
import cz.petrkubes.ioweyou.Adapters.FriendsDebtsAdapter;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.Debt;
import cz.petrkubes.ioweyou.Pojos.Friend;
import cz.petrkubes.ioweyou.Pojos.User;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Const;

import static android.view.View.GONE;


/**
 * Fragment representing the friends tab
 *
 * @author Petr Kubes
 */
public class FriendsFragment extends Fragment implements UpdateableFragment {

    public static final String ADD_TO_FRIEND = "addToFriend";

    private DatabaseHandler db;
    private User user;
    private FriendsAdapter adapter;
    private ArrayList<Friend> friends;
    private boolean myDebts = true;
    private View rootView;

    // Widgets
    private ListView lstFriends;

    // Dialog widgets
    private Button btnDialogPay;
    private Button btnDialogCancel;
    private Button btnDialogAdd;
    private ListView lstDialogDebts;
    private TextView txtDialogName;
    private TextView txtNote;

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

        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        lstFriends = (ListView) rootView.findViewById(R.id.lst_friends);
        txtNote = (TextView) rootView.findViewById(R.id.txt_note);

        // Apparently, OnCreateView can be called before OnCreate
        if (user == null) {
            user = db.getUser();
        }

        friends = db.getExtendedFriendsWhoAreCreditorsOrDebtors(user.id);
        toggleNote();
        adapter = new FriendsAdapter(getContext(), friends);
        lstFriends.setAdapter(adapter);

        lstFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDialog((Friend) adapterView.getItemAtPosition(i));
            }
        });

        return rootView;
    }

    @Override
    public void update() {

        if (user != null) {
            friends = db.getExtendedFriendsWhoAreCreditorsOrDebtors(user.id);
        }
        toggleNote();
        adapter.clear();
        adapter.addAll(friends);
        adapter.notifyDataSetChanged();
    }

    /**
     * Toggles the note that is supposed to show only when the list is empty
     */
    public void toggleNote() {
        // Show note
        if (friends.size() == 0) {
            txtNote.setVisibility(View.VISIBLE);
        } else {
            txtNote.setVisibility(GONE);
        }
    }

    private void showDialog(final Friend friend) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate layout
        View friendDialogView = inflater.inflate(R.layout.dialog_friend, null);
        builder.setView(friendDialogView);
        final AlertDialog dialog = builder.create();

        // Set up widgets
        btnDialogPay = (Button) friendDialogView.findViewById(R.id.btn_pay_selected);
        btnDialogCancel = (Button) friendDialogView.findViewById(R.id.btn_cancel);
        btnDialogAdd = (Button) friendDialogView.findViewById(R.id.btn_add_new_debt);
        txtDialogName = (TextView) friendDialogView.findViewById(R.id.txt_name);
        lstDialogDebts = (ListView) friendDialogView.findViewById(R.id.lst_debts);

        // Set name
        txtDialogName.setText(friend.name);

        final FriendsDebtsAdapter friendsDebtsAdapter = new FriendsDebtsAdapter(getContext(), friend.debts, user.id);
        lstDialogDebts.setAdapter(friendsDebtsAdapter);

        btnDialogPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Const.TAG, "-------------------");
                // Mark every selected debt as paid and notify adapter
                for (Debt debt : friendsDebtsAdapter.getSelectedDebts()) {
                    Log.d(Const.TAG, "Marking debt: " + debt.what);
                    friendsDebtsAdapter.remove(debt);
                    debt.paidAt = new Date();
                    debt.version += 1;
                    db.addOrUpdateDebt(debt.id, debt);
                    friendsDebtsAdapter.notifyDataSetChanged();
                }
                // Update friends adapter
                adapter.clear();
                adapter.addAll(db.getExtendedFriendsWhoAreCreditorsOrDebtors(user.id));
                adapter.notifyDataSetChanged();

                ((MainActivity) getActivity()).updateDebtsAndActions();

                dialog.cancel();
            }
        });

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        btnDialogAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DebtActivity.class);
                intent.putExtra(ADD_TO_FRIEND, Parcels.wrap(friend));
                getActivity().startActivityForResult(intent, MainActivity.ADD_DEBT_REQUEST);
                dialog.cancel();
            }
        });

        // display dialog
        dialog.show();
    }

}
