package cz.petrkubes.ioweyou.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import cz.petrkubes.ioweyou.Adapters.ActionsAdapter;
import cz.petrkubes.ioweyou.Database.DatabaseHandler;
import cz.petrkubes.ioweyou.Pojos.Action;
import cz.petrkubes.ioweyou.R;

import static android.view.View.GONE;

/**
 * Created by petr on 27.10.16.
 */

public class ActionsFragment extends Fragment implements UpdateableFragment {

    private ListView lstActions;
    private TextView txtNote;
    private ArrayList<Action> actions;
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
        txtNote = (TextView) rootView.findViewById(R.id.txt_note);

        actions = db.getExtendedActions();
        toggleNote();
        adapter = new ActionsAdapter(getContext(), actions);
        lstActions.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void update() {
        actions = db.getExtendedActions();
        toggleNote();
        adapter.clear();
        adapter.addAll(actions);
        adapter.notifyDataSetChanged();
    }

    public void toggleNote() {
        // Show note
        if (actions.size() == 0) {
            txtNote.setVisibility(View.VISIBLE);
        } else {
            txtNote.setVisibility(GONE);
        }
    }
}