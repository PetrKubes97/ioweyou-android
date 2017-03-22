package cz.petrkubes.ioweyou.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import cz.petrkubes.ioweyou.Pojos.Action;
import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Tools.Const;
import cz.petrkubes.ioweyou.Tools.Tools;

/**
 * Adapter for displaying info about actions in listview
 *
 * @author Petr Kubes
 */
public class ActionsAdapter extends ArrayAdapter<Action> {

    private ArrayList<Action> actions;
    private Context context;
    private LayoutInflater inflater;

    public ActionsAdapter(Context context, ArrayList<Action> objects) {
        super(context, R.layout.item_action, objects);
        this.actions = objects;
        this.context = context;
        this.inflater = LayoutInflater.from(getContext());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Action action = getItem(position);

        if (action == null) {
            return convertView;
        }

        final ActionsAdapter.ViewHolder viewHolder;

        String message = createMessage(action);

        // If cached view does not exist or if the message is long, inflate view
        // the problem is, that list view recycles the heights of items and items with longer text get cut off
        if (convertView == null || message.length() > 45) {
            viewHolder = new ActionsAdapter.ViewHolder();

            convertView = inflater.inflate(R.layout.item_action, parent, false);

            viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txt_message);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            viewHolder.txtUser1 = (TextView) convertView.findViewById(R.id.txt_user1);
            viewHolder.txtUser2 = (TextView) convertView.findViewById(R.id.txt_user2);
            viewHolder.layout = (ConstraintLayout) convertView.findViewById(R.id.layout);

            convertView.setTag(viewHolder);
            Log.d(Const.TAG, "inflating: " + message + "length: " + String.valueOf(message.length()));
        } else {
            viewHolder = (ActionsAdapter.ViewHolder) convertView.getTag();
            Log.d(Const.TAG, "recycling");
        }

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.txtUser1.setText(action.user1Name);
        viewHolder.txtUser2.setText(action.user2Name);
        viewHolder.txtMessage.setText(String.format(Locale.getDefault(), "#%d: %s", action.debtId, message));
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
        ConstraintLayout layout;
    }

    private String createMessage(Action action) {
        String finalMessage = "";
        for (String message : action.messages) {

            try {
                finalMessage += context.getString(context.getResources().getIdentifier(message, "string", context.getPackageName())) + " ";
            } catch (Exception e) {
                Log.d(Const.TAG, "Action Message not found.");
            }
        }

        return finalMessage;
    }

}
