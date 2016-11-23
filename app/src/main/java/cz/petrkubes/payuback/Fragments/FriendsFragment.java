package cz.petrkubes.payuback.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cz.petrkubes.payuback.Adapters.FriendsAdapter;
import cz.petrkubes.payuback.Adapters.FriendsDebtsAdapter;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Pojos.Debt;
import cz.petrkubes.payuback.Pojos.Friend;
import cz.petrkubes.payuback.Pojos.User;

/**
 * Created by petr on 27.10.16.
 */

public class FriendsFragment extends Fragment implements UpdateableFragment {

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

        friends = db.getExtendedFriendsWhoAreCreditorsOrDebtors(user.id);
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

    }

    private void showDialog(Friend friend) {

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

        FriendsDebtsAdapter adapter = new FriendsDebtsAdapter(getContext(), friend.debts, user.id);
        lstDialogDebts.setAdapter(adapter);
        lstDialogDebts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lstDialogDebts.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        // display dialog
        dialog.show();
    }

}
