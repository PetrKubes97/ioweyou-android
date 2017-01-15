package cz.petrkubes.ioweyou.Adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cz.petrkubes.ioweyou.R;
import cz.petrkubes.ioweyou.Pojos.Friend;

/**
 * Adapter for displaying friends suggestions when adding a new debt
 *
 * @author Petr Kubes
 */
public class FriendsSuggestionAdapter extends ArrayAdapter<Friend> implements Filterable {

    private Context context;
    private ArrayList<Friend> originalList; // List containing all friends
    private ArrayList<Friend> suggestions = new ArrayList<>();
    private Filter filter = new CustomFilter();

    public FriendsSuggestionAdapter(Context context, ArrayList<Friend> friends) {
        super(context, R.layout.item_friend_suggestion, friends);
        this.context = context;
        this.originalList = friends;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get friend object for this position
        Friend friend = suggestions.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_friend_suggestion, parent, false);

            viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.item_layout);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.email = (TextView) convertView.findViewById(R.id.txt_email);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.name.setText(friend.name);
        if (friend.email == null) {
            viewHolder.email.setText("");
        } else {
            viewHolder.email.setText(friend.email);
        }


        // Make Facebook friends blue
        if (friend.id != null) {
            viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.facebook_light));
            viewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.email.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            viewHolder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
            viewHolder.email.setTextColor(ContextCompat.getColor(context, R.color.secondary_text));
        }

        // Return the completed view to render on screen
        return convertView;
    }

    @Nullable
    @Override
    public Friend getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public int getCount() {
        return suggestions.size(); // Return the size of the suggestions list.
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    /**
     * Custom filter class
     */
    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            suggestions.clear();

            if (originalList != null && constraint != null) { // Check if the Original List and Constraint aren't null.
                for (int i = 0; i < originalList.size(); i++) {

                    // TODO: Ignore diacritics

                    if (originalList.get(i).name.toLowerCase().matches("(.*)" + constraint.toString().toLowerCase() + "(.*)")) { // Compare item in original list if it contains constraints.
                        suggestions.add(originalList.get(i));
                    }
                }
            }
            FilterResults results = new FilterResults(); // Create new Filter Results and return this to publishResults;
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    /**
     * View lookup cache
     */
    private static class ViewHolder {
        TextView name;
        TextView email;
        LinearLayout layout;
    }
}
