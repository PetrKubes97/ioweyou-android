package cz.petrkubes.payuback.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cz.petrkubes.payuback.Adapters.ActionsAdapter;
import cz.petrkubes.payuback.Database.DatabaseHandler;
import cz.petrkubes.payuback.R;

/**
 * Created by petr on 27.10.16.
 */

public class ActionsFragment extends Fragment implements UpdateableFragment {

    private ListView lstActions;
    private DatabaseHandler db;
    private ActionsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create new database instance
        db = new DatabaseHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_actions, container, false);
        lstActions = (ListView) rootView.findViewById(R.id.lst_actions);

        adapter = new ActionsAdapter(getContext(), db.getExtendedActions());
        lstActions.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void update() {
        adapter.clear();
        adapter.addAll(db.getExtendedActions());
        adapter.notifyDataSetChanged();
    }
}