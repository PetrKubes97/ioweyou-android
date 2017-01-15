package cz.petrkubes.ioweyou.Fragments;

import android.media.tv.TvContract;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private ProgressBar prgActions;
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
        prgActions = (ProgressBar) rootView.findViewById(R.id.prg_actions);

        prgActions.setVisibility(View.VISIBLE);

        actions = new ArrayList<>();
        new Task().execute();

        toggleNote();
        adapter = new ActionsAdapter(getContext(), actions);
        lstActions.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void update() {
        new Task().execute();
    }

    public void toggleNote() {
        // Show note
        if (actions.size() == 0) {
            txtNote.setVisibility(View.VISIBLE);
        } else {
            txtNote.setVisibility(View.GONE);
        }
    }

    private class Task extends AsyncTask<Void, Void, ArrayList<Action>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgActions.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Action> doInBackground(Void[] params) {
            actions = db.getExtendedActions();
            return actions;
        }

        @Override
        protected void onPostExecute(ArrayList<Action> actions) {
            toggleNote();
            prgActions.setVisibility(GONE);
            adapter.clear();
            adapter.addAll(actions);
            adapter.notifyDataSetChanged();
        }
    }
}