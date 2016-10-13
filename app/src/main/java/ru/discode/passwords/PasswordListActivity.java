package ru.discode.passwords;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import ru.discode.passwords.adapter.PasswordListAdapter;
import ru.discode.passwords.db.PasswordReaderDbHelper;
import ru.discode.passwords.entry.PasswordEntry;
import ru.discode.passwords.helper.Encryptor;

public class PasswordListActivity extends AppCompatActivity {
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
                PasswordEntry.COLUMN_NAME_PASSWORD
        };
        Cursor c = db.query(PasswordEntry.TABLE_NAME, projection, null, null, null, null, null);
        adapter = new PasswordListAdapter(this);
        adapter.swapCursor(c);
        listView.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_password, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.title);
        final EditText contentEditText = (EditText) view.findViewById(R.id.content);
        builder.setView(inflater.inflate(R.layout.edit_password, null));
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

    private void addPassword(String name, String content) {
        showProgress(true);
        AddPassword tasl = new AddPassword();
        tasl.execute(name, content);
    }

    private class AddPassword<String, Integer, Boolean> extends AsyncTask {

        @Override
        protected Boolean doInBackground(Object[] objects) {
            PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(PasswordListActivity.this);
            SQLiteDatabase db = passwordReaderDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            String encryptedData = null;
            try {
                encryptedData = Encryptor.encrypt(code, "asdadasdasdad");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v("EncryptDecrypt", "Encoded String " + encryptedData);
            String decryptedData = Encryptor.decrypt(code, encryptedData);
            Log.v("EncryptDecrypt", "Decoded String " + decryptedData);
            return null;
        }
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
