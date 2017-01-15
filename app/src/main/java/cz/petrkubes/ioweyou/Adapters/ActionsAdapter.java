package cz.petrkubes.ioweyou.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import cz.petrkubes.ioweyou.Pojos.Action;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * Adapter for displaying info about actions in listview
 *
 * @author Petr Kubes
 */
public class ActionsAdapter extends ArrayAdapter<Action> {

    private ArrayList<Action> actions;

    public ActionsAdapter(Context context, ArrayList<Action> objects) {
        super(context, R.layout.item_action, objects);
        this.actions = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Action action = getItem(position);

        if (action == null) {
            return convertView;
        }

        final ActionsAdapter.ViewHolder viewHolder;

        // If cached view does not exist, create a new one
        if (convertView == null) {

            viewHolder = new ActionsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_action, parent, false);

            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txt_message);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            viewHolder.txtUser1 = (TextView) convertView.findViewById(R.id.txt_user1);
            viewHolder.txtUser2 = (TextView) convertView.findViewById(R.id.txt_user2);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ActionsAdapter.ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.txtUser1.setText(action.user1Name);
        viewHolder.txtUser2.setText(action.user2Name);
        viewHolder.txtMessage.setText(String.format(Locale.getDefault(), "#%d: %s", action.debtId, action.note));
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

    /**
     * View lookup cache
     */
    private static class ViewHolder {
        TextView txtMessage;
        TextView txtDate;
        TextView txtUser1;
        TextView txtUser2;
    }

}
