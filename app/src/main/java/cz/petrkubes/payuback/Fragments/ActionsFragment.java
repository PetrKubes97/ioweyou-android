package cz.petrkubes.payuback.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cz.petrkubes.payuback.R;

/**
 * Created by petr on 27.10.16.
 */

public class ActionsFragment extends Fragment {

    ListView lstActions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_actions, container, false);
        lstActions = (ListView) rootView.findViewById(R.id.lst_actions);



        return rootView;
    }
}