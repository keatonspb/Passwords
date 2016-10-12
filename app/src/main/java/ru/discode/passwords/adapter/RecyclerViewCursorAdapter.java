package ru.discode.passwords.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

/**
 * Created by broadcaster on 12.10.2016.
 */

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Cursor cursor;

    public void swapCursor(final Cursor cursor) {
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.cursor != null ? this.cursor.getCount() : 0;
    }

    public Cursor getItem(final int position) {
        if(this.cursor != null && !this.cursor.isClosed()) {
            this.cursor.moveToPosition(position);
        }
        return cursor;
    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        final Cursor cursor = this.getItem(position);
        this.onBindViewHolder(holder, cursor);
    }

    public abstract void onBindViewHolder(final VH holder, final Cursor cursor);
}
