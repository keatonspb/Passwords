package ru.discode.passwords.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.security.GeneralSecurityException;

import ru.discode.passwords.R;
import ru.discode.passwords.entry.PasswordEntry;
import ru.discode.passwords.helper.AESCrypt;
import ru.discode.passwords.util.SLog;

/**
 * Created by broadcaster on 12.10.2016.
 */

public class PasswordListAdapter extends RecyclerViewCursorAdapter<PasswordListAdapter.PasswordViewHolder> {

    private final LayoutInflater layoutInflater;
    private Context ctx;
    private String code;
    private onTouchListener onTouchListener;
    public PasswordListAdapter(final Context context, String code) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.code = code;
        this.ctx = context;
    }
    public void setOnTouchListener(onTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }
    @Override
    public PasswordListAdapter.PasswordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = this.layoutInflater.inflate(R.layout.password_list_item, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PasswordViewHolder holder, Cursor cursor) {
        holder.bindData(cursor);
    }

    public void remove(int position) {
        if(onTouchListener != null) {
            onTouchListener.onSwipe(getItem(position).getLong(getCursor().getColumnIndex(PasswordEntry._ID)));
        }
    }

    public class PasswordViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView title;
        private TextView login;
        private Long id;
        public PasswordViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.title = (TextView) itemView.findViewById(R.id.password_title);
            this.login = (TextView) itemView.findViewById(R.id.password_login);
        }
        public Long getId() {
            return id;
        }
        public void bindData(final Cursor cursor) {
            this.id = cursor.getLong(cursor.getColumnIndex(PasswordEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_NAME_TITLE));
            String login = cursor.getString(cursor.getColumnIndex(PasswordEntry.COLUMN_NAME_LOGIN));
            try {
                name = AESCrypt.decrypt(code, name);
                login = AESCrypt.decrypt(code, login);
            } catch (Exception e) {
                SLog.d("PasswordViewHolder", "decrypt error");

                name = ctx.getResources().getString(R.string.decrypt_error_string);
            }
            this.title.setText(name);
            this.login.setText(login);

            if(onTouchListener != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTouchListener.onClick(PasswordViewHolder.this.id);
                    }
                });
            }

        }

    }


    public interface onTouchListener {
        void onClick(Long id);
        void onSwipe(Long id);
    }

}
