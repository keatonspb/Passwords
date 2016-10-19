package ru.discode.passwords;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.security.GeneralSecurityException;

import ru.discode.passwords.adapter.PasswordListAdapter;
import ru.discode.passwords.db.PasswordReaderDbHelper;
import ru.discode.passwords.entry.PasswordEntry;
import ru.discode.passwords.helper.AESCrypt;
import ru.discode.passwords.util.SLog;

public class PasswordListActivity extends AppCompatActivity implements PasswordListAdapter.onClickListener {
    public static String CODE_EXTRA = "CODE";
    private String code;
    private RecyclerView listView;
    private PasswordListAdapter adapter;
    private ProgressBar progressBar;
    private ScrollView scrollingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        code = getIntent().getStringExtra(CODE_EXTRA);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_password);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                showAddDialog();
            }
        });

        listView = (RecyclerView) findViewById(R.id.passwords_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        scrollingView = (ScrollView) findViewById(R.id.list_content);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        getList();
    }

    private void getList() {
        PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(this);
        SQLiteDatabase db = passwordReaderDbHelper.getReadableDatabase();
        String[] projection = {
                PasswordEntry._ID,
                PasswordEntry.COLUMN_NAME_TITLE,
                PasswordEntry.COLUMN_NAME_CONTENT
        };
        Cursor c = db.query(PasswordEntry.TABLE_NAME, projection, null, null, null, null, null);
        adapter = new PasswordListAdapter(this, code);
        adapter.swapCursor(c);
        adapter.setOnClickListener(this);
        listView.setAdapter(adapter);
        showProgress(false);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_password, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.title);
        final EditText contentEditText = (EditText) view.findViewById(R.id.content);
        builder.setView(view);
        builder.setPositiveButton(R.string.promt_add_password, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addPassword(nameEditText.getText().toString(), contentEditText.getText().toString());
            }
        });
        builder.setNeutralButton(R.string.promt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void showEditDialog(PasswordEntry passwordEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_password, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.title);
        nameEditText.setText(passwordEntry.title);
        final EditText contentEditText = (EditText) view.findViewById(R.id.content);
        contentEditText.setText(passwordEntry.content);
        final Long id = passwordEntry.id;
        builder.setView(view);
        builder.setPositiveButton(R.string.promt_add_password, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                savePassword(id, nameEditText.getText().toString(), contentEditText.getText().toString());
            }
        });
        builder.setNeutralButton(R.string.promt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void addPassword(String name, String content) {
        SLog.d("addPassword", "name"+name);
        SLog.d("addPassword", "content"+content);
        showProgress(true);
        try {
            name = AESCrypt.encrypt(code, name);
            content = AESCrypt.encrypt(code, content);
        } catch (GeneralSecurityException e) {
            SLog.d("PS", e.getMessage());
        }
        AddPassword task = new AddPassword();
        task.execute(name, content);
    }

    private void savePassword(Long id, String name, String content) {

    }

    @Override
    public void onClick(Long id) {
        new GetPassword().execute(id);
    }

    private class AddPassword extends AsyncTask<String, Void, Long> {

        @Override
        protected Long doInBackground(String... parameter) {
            PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(PasswordListActivity.this);
            SQLiteDatabase db = passwordReaderDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PasswordEntry.COLUMN_NAME_TITLE, (java.lang.String) parameter[0]);
            values.put(PasswordEntry.COLUMN_NAME_CONTENT, (java.lang.String) parameter[1]);
            long newRowId;
            newRowId = db.insert(PasswordEntry.TABLE_NAME, null, values);
            return newRowId;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            makeMessage("Пароль сохранен");
            getList();
        }
    }

    private class GetPassword extends AsyncTask<Long, Void, PasswordEntry> {

        @Override
        protected PasswordEntry doInBackground(Long... parameter) {
            PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(PasswordListActivity.this);
            SQLiteDatabase db = passwordReaderDbHelper.getReadableDatabase();

            String[] projection = {
                    PasswordEntry._ID,
                    PasswordEntry.COLUMN_NAME_TITLE,
                    PasswordEntry.COLUMN_NAME_CONTENT,
            };

            String selection = PasswordEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(parameter[0]) };

            Cursor c = db.query(PasswordEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
                    );
            PasswordEntry pe = new PasswordEntry();
            if(c.getCount() == 1) {
                c.moveToFirst();
                try {
                    pe.id = c.getLong(c.getColumnIndex(PasswordEntry._ID));
                    pe.title = AESCrypt.decrypt(code, c.getString(c.getColumnIndex(PasswordEntry.COLUMN_NAME_TITLE)));
                    pe.content = AESCrypt.decrypt(code, c.getString(c.getColumnIndex(PasswordEntry.COLUMN_NAME_CONTENT)));
                } catch (GeneralSecurityException e) {

                    return null;
                }

            }
            return pe;
        }

        @Override
        protected void onPostExecute(PasswordEntry entry) {
            super.onPostExecute(entry);
            if(entry == null) {
                makeMessage(getResources().getString(R.string.decrypt_error_string));
            } else {
                showEditDialog(entry);
            }


        }
    }
    protected void makeMessage(String text) {

        Snackbar.make(scrollingView, text, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
    }
    /**
     * Показывае загрузчик
     */
    public void showProgress(boolean show) {
        if(show) {
            progressBar.setVisibility(View.VISIBLE);
            scrollingView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            scrollingView.setVisibility(View.VISIBLE);
        }

    }


}
