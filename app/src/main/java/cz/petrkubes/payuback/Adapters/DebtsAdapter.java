package cz.petrkubes.payuback.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.petrkubes.payuback.Api.ApiRestClient;
import cz.petrkubes.payuback.Fragments.UpdateableFragment;
import cz.petrkubes.payuback.R;
import cz.petrkubes.payuback.Structs.Debt;

/**
 * Created by petr on 5.11.16.
 */

public class DebtsAdapter extends ArrayAdapter<Debt> {

    ArrayList<Debt> debts;

    public DebtsAdapter(Context context, List<Debt> debts) {
        super(context, R.layout.item_debt, debts);
        this.debts = (ArrayList<Debt>) debts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Debt debt = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_debt, parent, false);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_name);
            viewHolder.txtWhat = (TextView) convertView.findViewById(R.id.txt_what);
            viewHolder.txtNote = (TextView) convertView.findViewById(R.id.txt_note);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txt_status);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.txtName.setText(debt.who);
        viewHolder.txtWhat.setText(debt.what);
        viewHolder.txtNote.setText(debt.note);
        viewHolder.txtDate.setText(df.format(debt.modifiedAt)); // TODO this should be created at
        viewHolder.txtStatus.setText(debt.status);

        // Return the completed view to render on screen
        return convertView;
    }

    @Nullable
    @Override
    public Debt getItem(int position) {
        return debts.get(position);
    }

    @Override
    public int getCount() {
        return debts.size();
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtWhat;
        TextView txtNote;
        TextView txtDate;
        TextView txtStatus;
    }


}
