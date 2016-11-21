package cz.petrkubes.payuback.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import cz.petrkubes.payuback.Adapters.DebtsAdapter;
import cz.petrkubes.payuback.Adapters.FriendsAdapter;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Debt;
import cz.petrkubes.payuback.Structs.Friend;
import cz.petrkubes.payuback.Structs.User;

/**
 * Created by petr on 27.10.16.
 */

public class FriendsFragment extends Fragment implements UpdateableFragment {

    private DatabaseHandler db;
    private ListView lstFriends;
    private User user;
    private FriendsAdapter adapter;
    private ArrayList<Friend> friends;
    private boolean myDebts = true;
    private View rootView;


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




        return rootView;
    }

    @Override
    public void update() {

    }
}
