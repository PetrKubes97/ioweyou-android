package cz.petrkubes.ioweyou.Adapters;

/**
 * Created by petr on 1.12.16.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cz.petrkubes.ioweyou.Pojos.Action;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * Created by petr on 23.11.16.
 */

public class ActionsAdapter extends ArrayAdapter<Action> {

    private ArrayList<Action> actions;

    public ActionsAdapter(Context context, ArrayList<Action> objects) {
        super(context, R.layout.item_action,  objects);
        this.actions = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Action action = getItem(position);

        final ActionsAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ActionsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_action, parent, false);

            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txt_message);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ActionsAdapter.ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.txtMessage.setText(action.userName + " " + action.type + " " + action.note);
        viewHolder.txtDate.setText(Tools.formatDate(action.date));

        // Return the completed view to render on screen
        return convertView;
    }

    @Nullable
    @Override
    public Action getItem(int position) {
        return actions.get(position);
    }

    @Override
    public int getCount() {
        return actions.size();
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtMessage;
        TextView txtDate;
    }

}
