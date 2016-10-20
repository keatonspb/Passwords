package ru.discode.passwords.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import ru.discode.passwords.R;
import ru.discode.passwords.adapter.PasswordListAdapter;

/**
 * Created by broadcaster on 20.10.2016.
 */

public class PasswordTouchHelper extends ItemTouchHelper.SimpleCallback {
    private PasswordListAdapter passwordListAdapter;
    Drawable background;
    Drawable xMark;
    int xMarkMargin;
    boolean initiated;
    public PasswordTouchHelper(PasswordListAdapter passwordListAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
        this.passwordListAdapter = passwordListAdapter;
        background = new ColorDrawable(Color.RED);
        }

    private void init() {

        initiated = true;
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        passwordListAdapter.remove(viewHolder.getAdapterPosition());

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        if (viewHolder.getAdapterPosition() == -1) {
            // not interested in those
            return;
        }

        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        background.draw(c);


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
