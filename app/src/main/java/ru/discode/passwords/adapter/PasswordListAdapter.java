package ru.discode.passwords.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.discode.passwords.R;
import ru.discode.passwords.entry.PasswordEntry;

/**
 * Created by broadcaster on 12.10.2016.
 */

public class PasswordListAdapter extends RecyclerViewCursorAdapter<PasswordListAdapter.PasswordViewHolder> {

    private final LayoutInflater layoutInflater;
    public PasswordListAdapter(final Context context) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public PasswordListAdapter.PasswordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = this.layoutInflater.inflate(R.layout.password_list_item, parent, false);
        return null;
    }

    @Override
    public void onBindViewHolder(PasswordViewHolder holder, Cursor cursor) {
        holder.bindData(cursor);
    }

    public class PasswordViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        public PasswordViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.password_title);
        }
        public void bindData(final Cursor cursor) {
            this.title.setText(cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_NAME_TITLE)));
        }

    }
}
